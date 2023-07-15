package me.lostedark.aetherplugins.chest;

import me.lostedark.aetherplugins.chest.commands.BauCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.PluginDisableEvent;

public class Main extends JavaPlugin implements Listener {
   private static Main instance;

   public static Main getInstance() {
      return instance;
   }

   public void onEnable() {
      instance = this;

      String pluginName = this.getDescription().getName();
      String pluginVersion = this.getDescription().getVersion();
      this.getLogger().info("--------------------------------------");
      this.getLogger().info("|                                     |");
      this.getLogger().info("|   AETHER PLUGINS - BY D4RKK         |");
      this.getLogger().info("|   Plugin: " + pluginName + " v" + pluginVersion + "          |");
      this.getLogger().info("| Loja: https://plugins.aethershop.store |");
      this.getLogger().info("|                                     |");
      this.getLogger().info("| Obrigado por usar nossos plugins <3 |");
      this.getLogger().info("|                                     |");
      this.getLogger().info("--------------------------------------");
      this.getLogger().info("Plugin iniciado com sucesso!");

      getCommand("bau").setExecutor(new BauCommand(this));

      // Registrar o listener de eventos
      getServer().getPluginManager().registerEvents(this, this);
   }

   public void onDisable() {
      this.getLogger().info("Plugin desabilitado!");
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onPluginDisable(PluginDisableEvent event) {
      if (event.getPlugin() == this) {
         BauCommand bauCommand = (BauCommand) getCommand("bau").getExecutor();
         bauCommand.saveBausOnDisable();
      }
   }
}
