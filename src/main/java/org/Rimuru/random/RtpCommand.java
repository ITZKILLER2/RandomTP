package org.Rimuru.random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RtpCommand implements CommandExecutor {

    private final RTP plugin;
    private static final Set<Material> UNSAFE_BLOCKS = new HashSet<>(Arrays.asList(
            Material.LAVA, Material.WATER, Material.CACTUS, Material.FIRE, Material.MAGMA_BLOCK,
            Material.SOUL_FIRE, Material.CAMPFIRE, Material.SOUL_CAMPFIRE, Material.SWEET_BERRY_BUSH,
            Material.WITHER_ROSE, Material.POWDER_SNOW
    ));

    public RtpCommand(RTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("rtp.use")) {
            player.sendMessage(plugin.getConfig().getString("messages.no_permission", "No permission!"));
            return true;
        }

        UUID uuid = player.getUniqueId();

        // ===== COOLDOWN CHECK =====
        long cdRemaining = plugin.getCooldownRemaining(uuid);
        if (cdRemaining > 0) {
            long sec = cdRemaining / 1000;
            player.sendMessage("§cYou must wait §e" + sec + "s §cbefore using /rtp again!");
            return true;
        }

        Location target = player.getBedSpawnLocation();

        if (target == null) {
            target = findSafeRandomLocation(player);
            if (target == null) {
                player.sendMessage(plugin.getConfig().getString("messages.no_safe_spot", "No safe spot found!"));
                return true;
            }
        }

        // TELEPORT + EFFECTS
        player.teleport(target);

        // Apply blindness
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 1, false, false));

        // Title
        String coords = String.format("x=%d, y=%d, z=%d", target.getBlockX(), target.getBlockY(), target.getBlockZ());
        player.sendTitle(plugin.gold("Teleported at"), plugin.gold(coords), 10, 60, 20);

        // ACTIVATE COOLDOWN
        plugin.activateCooldown(uuid);

        return true;
    }

    private Location findSafeRandomLocation(Player player) {
        World world = player.getWorld();
        Location current = player.getLocation();
        int min = plugin.getConfig().getInt("rtp.min_radius", 100);
        int max = plugin.getConfig().getInt("rtp.max_radius", 1000);
        int attempts = plugin.getConfig().getInt("rtp.max_attempts", 15);

        for (int i = 0; i < attempts; i++) {
            double theta = Math.random() * 2 * Math.PI;
            double dist = min + Math.random() * (max - min);
            double x = current.getX() + dist * Math.cos(theta);
            double z = current.getZ() + dist * Math.sin(theta);

            int y = world.getHighestBlockYAt((int) x, (int) z);
            Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);

            if (isSafeLocation(loc)) return loc;
        }
        return null;
    }

    private boolean isSafeLocation(Location loc) {
        Block feet = loc.getBlock();
        Block head = feet.getRelative(BlockFace.UP);
        Block ground = feet.getRelative(BlockFace.DOWN);

        return ground.getType().isSolid() && !ground.isLiquid() &&
                feet.getType() == Material.AIR &&
                head.getType() == Material.AIR &&
                !UNSAFE_BLOCKS.contains(ground.getType());
    }
}
