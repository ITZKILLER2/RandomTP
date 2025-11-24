package org.Rimuru.random;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RTP extends JavaPlugin {

    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("rtp").setExecutor(new RtpCommand(this));
        getCommand("rtpcd").setExecutor(new RtpCooldownCommand(this));
        getCommand("changecd").setExecutor(new ChangeCdCommand(this));

        getLogger().info("RTP plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        saveConfig();
        getLogger().info("RTP plugin has been disabled!");
    }

    public String gold(String text) {
        return ChatColor.GOLD + "" + ChatColor.BOLD + text;
    }

    //COOLDOWN SYSTEM

    public long getCooldownRemaining(UUID uuid) {
        if (!getConfig().getBoolean("cooldown.enabled", true)) return 0;

        long last = cooldowns.getOrDefault(uuid, 0L);
        int cd = getConfig().getInt("cooldown.time", 60); // seconds

        long end = last + cd * 1000L;
        long now = System.currentTimeMillis();

        return Math.max(0, end - now);
    }

    public void activateCooldown(UUID uuid) {
        cooldowns.put(uuid, System.currentTimeMillis());
    }
}
