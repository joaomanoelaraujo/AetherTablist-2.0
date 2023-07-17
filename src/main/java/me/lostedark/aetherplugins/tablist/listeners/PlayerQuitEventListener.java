package me.lostedark.aetherplugins.tablist.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PlayerQuitEventListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        resetTablist(player);
    }

    private void resetTablist(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null) {
            return;
        }

        Team team = scoreboard.getTeam("tablist");
        if (team != null) {
            team.unregister();
        }

        player.setPlayerListName(player.getPlayerListName());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }
}
