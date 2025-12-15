package org.Rimuru.random;

import org.bukkit.command.*;

public class ReloadCommand implements CommandExecutor {

    private final RTP plugin;

    public ReloadCommand(RTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.hasPermission("rtp.admin")) {
            sender.sendMessage(plugin.msg("errors.no_permission"));
            return true;
        }

        plugin.reloadConfig();
        plugin.reloadMessages();
        sender.sendMessage("§aRTP config & messages reloaded!");
        return true;
    }
}
