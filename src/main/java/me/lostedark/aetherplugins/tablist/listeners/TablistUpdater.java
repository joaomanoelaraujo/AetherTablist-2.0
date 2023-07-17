package me.lostedark.aetherplugins.tablist.listeners;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TablistUpdater extends BukkitRunnable {

    private final FileConfiguration config;

    public TablistUpdater(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerJoinEventListener.updateTablist(player, config);
        }
    }
}
