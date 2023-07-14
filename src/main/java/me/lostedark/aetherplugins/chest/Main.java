package me.lostedark.aetherplugins.chest;

import me.lostedark.aetherplugins.chest.commands.BauCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
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

      getCommand("bau").setExecutor(new BauCommand());
   }

   public void onDisable() {
      this.getLogger().info("Plugin desabilitado!");
   }
}
