package org.Rimuru.random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RtpCooldownCommand implements CommandExecutor {

    private final RTP plugin;

    public RtpCooldownCommand(RTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }

        long left = plugin.getCooldownRemaining(player.getUniqueId());

        if (left > 0) {
            long sec = left / 1000;
            player.sendMessage("§eRTP Cooldown: §c" + sec + "s remaining.");
        } else {
            player.sendMessage("§aYou can use /rtp right now!");
        }

        return true;
    }
}
