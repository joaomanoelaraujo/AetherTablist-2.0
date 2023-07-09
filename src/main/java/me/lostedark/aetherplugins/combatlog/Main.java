package me.lostedark.aetherplugins.combatlog;

import java.io.File;
import java.io.IOException;
import java.util.List;
import me.lostedark.aetherplugins.combatlog.listeners.player.CombatLog;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
   private static Main instance;
   private FileConfiguration languageConfig;

   public static Main getInstance() {
      return instance;
   }

   public void onEnable() {
      instance = this;
      saveDefaultConfig();
      loadLanguageConfig();

      String pluginName = getDescription().getName();
      String pluginVersion = getDescription().getVersion();
      String combatPrefix = languageConfig.getString("combat-prefix", "COMBATE");

      getLogger().info("--------------------------------------");
      getLogger().info("|                                     |");
      getLogger().info("|   AETHER PLUGINS - BY D4RKK         |");
      getLogger().info("|   Plugin: " + pluginName + " v" + pluginVersion + "          |");
      getLogger().info("| Loja: https://plugins.aethershop.store |");
      getLogger().info("|                                     |");
      getLogger().info("| Obrigado por usar nossos plugins <3 |");
      getLogger().info("|                                     |");
      getLogger().info("--------------------------------------");
      getLogger().info("Plugin iniciado com sucesso!");

      ConfigurationSection combatLogSection = getConfig().getConfigurationSection("combat-log");
      boolean actionBarEnabled = combatLogSection.getBoolean("actionbar-enabled");
      List<String> blockedCommands = combatLogSection.getStringList("blocked-commands");
      List<String> blockedWorlds = combatLogSection.getStringList("blocked-worlds");

      getServer().getPluginManager().registerEvents(new CombatLog(actionBarEnabled, blockedCommands, blockedWorlds, combatPrefix), this);
   }

   public void onDisable() {
      getLogger().info("Plugin desabilitado!");
   }

   private void loadLanguageConfig() {
      File languageFile = new File(getDataFolder(), "language.yml");
      if (!languageFile.exists()) {
         saveResource("language.yml", false);
      }

      languageConfig = YamlConfiguration.loadConfiguration(languageFile);

      // Configurar o prefixo de combate corretamente
      String prefix = languageConfig.getString("combat-prefix");
      if (prefix != null) {
         prefix = ChatColor.translateAlternateColorCodes('&', prefix);
         prefix = prefix.replace("Â", "§");
         languageConfig.set("combat-prefix", prefix);
      }
   }

   public FileConfiguration getLanguageConfig() {
      return languageConfig;
   }
}
