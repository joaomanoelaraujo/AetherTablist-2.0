package me.lostedark.aetherplugins.tablist;

import me.lostedark.aetherplugins.tablist.listeners.PlayerJoinEventListener;
import me.lostedark.aetherplugins.tablist.listeners.PlayerQuitEventListener;
import me.lostedark.aetherplugins.tablist.listeners.TablistUpdater;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {
   private static Main instance;
   private FileConfiguration tabConfig;
   private int tick;

   public static Main getInstance() {
      return instance;
   }

   public void onEnable() {
      instance = this;

      loadTabConfig();

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


      registerEvents(new PlayerJoinEventListener(tabConfig));
      registerEvents(new PlayerQuitEventListener());
      TablistUpdater tablistUpdater = new TablistUpdater(tabConfig);
      tablistUpdater.runTaskTimer(this, 0L, tick);
   }

   private void loadTabConfig() {
      File tabFile = new File(getDataFolder(), "tab.yml");
      if (!tabFile.exists()) {
         saveResource("tab.yml", false);
      }

      tabConfig = YamlConfiguration.loadConfiguration(tabFile);

      tick = tabConfig.getInt("tick");
      if (tick <= 0) {
         tick = 20; // Valor padrão caso o valor do tick seja inválido
      }
   }

   public void onDisable() {
      this.getLogger().info("Plugin desabilitado!");
   }

   private void registerEvents(Listener listener) {
      getServer().getPluginManager().registerEvents(listener, this);
   }
}
