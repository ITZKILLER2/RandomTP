package org.Rimuru.random;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RTP extends JavaPlugin {

    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    private File messagesFile;
    private FileConfiguration messages;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadMessages();

        getCommand("rtp").setExecutor(new RtpCommand(this));
        getCommand("rtpcd").setExecutor(new RtpCooldownCommand(this));
        getCommand("changecd").setExecutor(new ChangeCdCommand(this));
        getCommand("rtpreload").setExecutor(new ReloadCommand(this));

        getLogger().info("RTP plugin enabled!");
    }

    // ===== MESSAGES SYSTEM =====

    public void loadMessages() {
        messagesFile = new File(getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reloadMessages() {
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String msg(String path) {
        String text = messages.getString(path, "&cMissing message: " + path);
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    // ===== COOLDOWN SYSTEM =====

    public long getCooldownRemaining(UUID uuid) {
        if (!getConfig().getBoolean("cooldown.enabled", true)) return 0;

        long last = cooldowns.getOrDefault(uuid, 0L);
        int cd = getConfig().getInt("cooldown.time", 60);

        long end = last + (cd * 1000L);
        return Math.max(0, end - System.currentTimeMillis());
    }

    public void activateCooldown(UUID uuid) {
        cooldowns.put(uuid, System.currentTimeMillis());
    }
}
