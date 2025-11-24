package org.Rimuru.random;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

public class RTP extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("rtp").setExecutor(new RtpCommand(this));
        getLogger().info("RTP plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        // Save config
        saveConfig();
        getLogger().info("RTP plugin has been disabled!");
    }

    public String gold(String text) {
        return ChatColor.GOLD + "" + ChatColor.BOLD + text;
    }
}