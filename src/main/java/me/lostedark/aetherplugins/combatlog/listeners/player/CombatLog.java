package me.lostedark.aetherplugins.combatlog.listeners.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import me.lostedark.aetherplugins.combatlog.Main;
import me.lostedark.aetherplugins.combatlog.api.ProtectionAPI;
import me.lostedark.aetherplugins.combatlog.utils.ActionBarUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatLog implements Listener {
   private final boolean actionBarEnabled;
   private final List<String> blockedCommands;
   private final List<String> blockedWorlds;
   public static HashMap<Player, String> previousWorlds = new HashMap();
   public static ArrayList<Player> combat = new ArrayList();
   private final String combatPrefix;

   public CombatLog(boolean actionBarEnabled, List<String> blockedCommands, List<String> blockedWorlds, String combatPrefix) {
      this.actionBarEnabled = actionBarEnabled;
      this.blockedCommands = blockedCommands;
      this.blockedWorlds = blockedWorlds;
      this.combatPrefix = combatPrefix;
   }

   @EventHandler
   public void aoPlayerhit(EntityDamageByEntityEvent e) {
      if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
         Player p = (Player) e.getEntity();
         Player k = (Player) e.getDamager();
         if (!ProtectionAPI.inProtection(p) && !ProtectionAPI.inProtection(k)) {
            if (!combat.contains(p)) {
               if (this.actionBarEnabled) {
                  ActionBarUtil.sendActionBar(p, ChatColor.RED + "Você está em " + ChatColor.RED + ChatColor.BOLD + "COMBATE");
               }

               p.sendMessage(ChatColor.RED + combatPrefix + ChatColor.RESET + "Você entrou em combate com " + ChatColor.RED + k.getName());
               combat.add(p);
               Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                  combat.remove(p);
                  p.sendMessage(ChatColor.RED + combatPrefix + ChatColor.RESET + "Você saiu de combate.");
               }, 200L);
            }

            if (!combat.contains(k)) {
               combat.add(k);
               k.sendMessage(ChatColor.RED + combatPrefix + ChatColor.RESET + "Você entrou em combate com " + ChatColor.RED + p.getName());
               if (this.actionBarEnabled) {
                  ActionBarUtil.sendActionBar(k, ChatColor.RED + "Você está em " + ChatColor.RED + ChatColor.BOLD + "COMBATE");
               }

               Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                  combat.remove(k);
                  k.sendMessage(ChatColor.GREEN + combatPrefix + ChatColor.RESET + "Você saiu de combate.");
               }, 200L);
            }
         }
      }

   }

   @EventHandler
   public void aoPlayerExit(PlayerQuitEvent e) {
      Player p = e.getPlayer();
      if (combat.contains(p)) {
         combat.remove(p);
      }

   }

   @EventHandler
   public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
      Player player = e.getPlayer();
      if (combat.contains(player)) {
         String command = e.getMessage().split(" ")[0].substring(1).toLowerCase();
         if (this.isBlockedCommand(command)) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Você não pode usar esse comando durante o combate!");
         }
      }

   }

   @EventHandler
   public void aoPlayerChangedWorld(PlayerChangedWorldEvent e) {
      Player player = e.getPlayer();
      if (combat.contains(player)) {
         String previousWorld = (String)previousWorlds.get(player);
         if (previousWorld != null) {
            player.teleport(Bukkit.getWorld(previousWorld).getSpawnLocation());
            player.sendMessage(ChatColor.RED + "Você não pode mudar de mundo durante o combate!");
         }
      }

   }

   private boolean isBlockedCommand(String command) {
      Iterator var2 = this.blockedCommands.iterator();

      String blockedCommand;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         blockedCommand = (String)var2.next();
      } while(!command.startsWith(blockedCommand));

      return true;
   }

   private boolean isBlockedWorld(World world) {
      return this.blockedWorlds.contains(world.getName());
   }
}
