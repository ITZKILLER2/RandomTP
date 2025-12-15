package org.Rimuru.random;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class RtpCommand implements CommandExecutor {

    private final RTP plugin;

    private static final Set<Material> UNSAFE_BLOCKS = new HashSet<>(Arrays.asList(
            Material.LAVA, Material.WATER, Material.CACTUS, Material.FIRE,
            Material.MAGMA_BLOCK, Material.SOUL_FIRE, Material.CAMPFIRE,
            Material.SOUL_CAMPFIRE, Material.SWEET_BERRY_BUSH,
            Material.WITHER_ROSE, Material.POWDER_SNOW
    ));

    public RtpCommand(RTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.msg("errors.players_only"));
            return true;
        }

        if (!player.hasPermission("rtp.use")) {
            player.sendMessage(plugin.msg("errors.no_permission"));
            return true;
        }

        UUID uuid = player.getUniqueId();

        long left = plugin.getCooldownRemaining(uuid);
        if (left > 0) {
            long sec = left / 1000;
            player.sendMessage(
                    plugin.msg("cooldown.wait").replace("{time}", String.valueOf(sec))
            );
            return true;
        }

        Location target = findSafeRandomLocation(player);
        if (target == null) {
            player.sendMessage(plugin.msg("errors.no_safe_spot"));
            return true;
        }

        player.teleport(target);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 1, false, false));

        String title = plugin.msg("rtp.title");
        String subtitle = plugin.msg("rtp.subtitle")
                .replace("{x}", String.valueOf(target.getBlockX()))
                .replace("{y}", String.valueOf(target.getBlockY()))
                .replace("{z}", String.valueOf(target.getBlockZ()));

        player.sendTitle(title, subtitle, 10, 60, 20);

        plugin.activateCooldown(uuid);
        return true;
    }

    private Location findSafeRandomLocation(Player player) {
        World world = player.getWorld();
        Location base = player.getLocation();

        int min = plugin.getConfig().getInt("rtp.min_radius", 100);
        int max = plugin.getConfig().getInt("rtp.max_radius", 1000);
        int attempts = plugin.getConfig().getInt("rtp.max_attempts", 15);

        for (int i = 0; i < attempts; i++) {
            double angle = Math.random() * Math.PI * 2;
            double dist = min + Math.random() * (max - min);

            double x = base.getX() + Math.cos(angle) * dist;
            double z = base.getZ() + Math.sin(angle) * dist;
            int y = world.getHighestBlockYAt((int) x, (int) z);

            Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);
            if (isSafe(loc)) return loc;
        }
        return null;
    }

    private boolean isSafe(Location loc) {
        Block feet = loc.getBlock();
        Block head = feet.getRelative(BlockFace.UP);
        Block ground = feet.getRelative(BlockFace.DOWN);

        return ground.getType().isSolid()
                && !ground.isLiquid()
                && feet.getType() == Material.AIR
                && head.getType() == Material.AIR
                && !UNSAFE_BLOCKS.contains(ground.getType());
    }
}
