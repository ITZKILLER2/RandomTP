package org.Rimuru.random;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ChangeCdCommand implements CommandExecutor {

    private final RTP plugin;

    public ChangeCdCommand(RTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.msg("errors.players_only"));
            return true;
        }

        if (!player.hasPermission("rtp.admin")) {
            player.sendMessage(plugin.msg("errors.no_permission"));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(plugin.msg("admin.usage_changecd"));
            return true;
        }

        try {
            int cd = Integer.parseInt(args[0]);
            if (cd < 0) return true;

            plugin.getConfig().set("cooldown.time", cd);
            plugin.saveConfig();

            player.sendMessage(
                    plugin.msg("admin.cooldown_updated")
                            .replace("{time}", String.valueOf(cd))
            );

        } catch (NumberFormatException e) {
            player.sendMessage(plugin.msg("admin.invalid_number"));
        }
        return true;
    }
}
