package org.Rimuru.random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChangeCdCommand implements CommandExecutor {

    private final RTP plugin;

    public ChangeCdCommand(RTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }

        if (!player.hasPermission("rtp.admin")) {
            player.sendMessage("§cYou do not have permission to change cooldowns!");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§eUsage: §b/changecd <seconds>");
            return true;
        }

        try {
            int newCd = Integer.parseInt(args[0]);
            if (newCd < 0) {
                player.sendMessage("§cCooldown must be 0 or higher.");
                return true;
            }

            plugin.getConfig().set("cooldown.time", newCd);
            plugin.saveConfig();

            player.sendMessage("§aRTP cooldown updated to §e" + newCd + " seconds§a.");

        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid number! Example: /changecd 60");
        }

        return true;
    }
}
