package me.lostedark.aetherplugins.combatlog.api;

import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class ProtectionAPI implements Listener {
   public static ArrayList<String> protegido = new ArrayList();

   public static void setProtection(Player p, boolean bol) {
      if (bol) {
         if (!protegido.contains(p.getName())) {
            protegido.add(p.getName());
         }
      } else {
         protegido.remove(p.getName());
      }

   }

   public static boolean inProtection(Player p) {
      return protegido.contains(p.getName());
   }

   @EventHandler
   public void onAttack(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
         Player player = (Player)event.getEntity();
         Player damager = (Player)event.getDamager();
         if (inProtection(player) && inProtection(damager)) {
            event.setCancelled(true);
            return;
         }

         if (!inProtection(player) && inProtection(damager)) {
            event.setCancelled(true);
            return;
         }

         if (inProtection(player) && !inProtection(damager)) {
            event.setCancelled(true);
            return;
         }

         if (!inProtection(player) && !inProtection(damager)) {
            event.setCancelled(false);
            return;
         }
      }

   }

   @EventHandler
   public void onDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (inProtection(player)) {
            event.setCancelled(true);
            return;
         }

         if (!inProtection(player)) {
            event.setCancelled(false);
            return;
         }
      }

   }
}
