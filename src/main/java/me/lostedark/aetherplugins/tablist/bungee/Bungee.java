package me.lostedark.aetherplugins.tablist.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class Bungee extends Plugin {
   private static Bungee instance;

   public static Bungee getInstance() {
      return instance;
   }

   public void onLoad() {
      instance = this;
      this.loadConfiguration("tab");
   }

   public void onEnable() {
      this.getProxy().registerChannel("AetherTablist");
      this.sendMessage("O plugin iniciou com sucesso!");
   }

   public void loadConfiguration(String... filesName) {
      int var3 = filesName.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String fileName = filesName[var4];
         File file = new File("plugins/" + this.getDescription().getName() + "/" + fileName + ".yml");
         if (!file.exists()) {
            File folder = file.getParentFile();
            if (!folder.exists()) {
               folder.mkdirs();
            }

            try {
               Files.copy(Objects.requireNonNull(getInstance().getClass().getResourceAsStream("/" + fileName + ".yml")), file.toPath());
            } catch (IOException var9) {
               var9.printStackTrace();
            }
         }
      }

   }

   public void onDisable() {
      this.sendMessage("O plugin desligou com sucesso!");
   }

   public void sendMessage(String message) {
      this.getLogger().info("§a" + message);
   }

}
