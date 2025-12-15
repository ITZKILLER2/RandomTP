package org.Rimuru.random;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class RtpCooldownCommand implements CommandExecutor {

    private final RTP plugin;

    public RtpCooldownCommand(RTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.msg("errors.players_only"));
            return true;
        }

        long left = plugin.getCooldownRemaining(player.getUniqueId());

        if (left > 0) {
            long sec = left / 1000;
            player.sendMessage(
                    plugin.msg("cooldown.wait").replace("{time}", String.valueOf(sec))
            );
        } else {
            player.sendMessage(plugin.msg("cooldown.ready"));
        }
        return true;
    }
}
