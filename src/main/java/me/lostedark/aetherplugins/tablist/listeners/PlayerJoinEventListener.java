package me.lostedark.aetherplugins.tablist.listeners;

import me.lostedark.aetherplugins.tablist.utils.PingUtil;
import me.lostedark.aetherplugins.tablist.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;



public class PlayerJoinEventListener implements Listener {

    private final FileConfiguration config;

    public PlayerJoinEventListener(FileConfiguration config) {
        this.config = config;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updateTablist(player, config);
    }

    public static void updateTablist(Player player, FileConfiguration config) {
        if (!config.getBoolean("enabled")) {
            return;
        }

        String headerString = config.getString("header");
        String footerString = config.getString("footer");

        String translatedHeader = StringUtils.color(headerString);
        String translatedFooter = StringUtils.color(footerString);

        int ping = PingUtil.getPing(player);
        translatedHeader = translatedHeader.replace("%ping%", String.valueOf(ping));
        translatedFooter = translatedFooter.replace("%ping%", String.valueOf(ping));

        int totalPlayers = Bukkit.getOnlinePlayers().size();

        translatedHeader = translatedHeader.replace("%online%", String.valueOf(totalPlayers));
        translatedFooter = translatedFooter.replace("%online%", String.valueOf(totalPlayers));


        String playerName = player.getName();
        translatedHeader = translatedHeader.replace("%player%", playerName);
        translatedFooter = translatedFooter.replace("%player%", playerName);


        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String coordinates = decimalFormat.format(player.getLocation().getX()) + ", " +
                decimalFormat.format(player.getLocation().getY()) + ", " +
                decimalFormat.format(player.getLocation().getZ());

        translatedHeader = translatedHeader.replace("%coordinates%", coordinates);
        translatedFooter = translatedFooter.replace("%coordinates%", coordinates);

        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);

            Class<?> packetPlayOutPlayerListHeaderFooterClass = Class.forName("net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter");
            Constructor<?> packetConstructor = packetPlayOutPlayerListHeaderFooterClass.getDeclaredConstructor();
            Object packet = packetConstructor.newInstance();

            Field headerField = packetPlayOutPlayerListHeaderFooterClass.getDeclaredField("a");
            headerField.setAccessible(true);
            headerField.set(packet, createChatComponent(translatedHeader));

            Field footerField = packetPlayOutPlayerListHeaderFooterClass.getDeclaredField("b");
            footerField.setAccessible(true);
            footerField.set(packet, createChatComponent(translatedFooter));

            Object craftPlayerHandle = craftPlayerClass.getMethod("getHandle").invoke(craftPlayer);
            Object playerConnection = craftPlayerHandle.getClass().getField("playerConnection").get(craftPlayerHandle);

            Method sendPacketMethod = playerConnection.getClass().getMethod("sendPacket", getNmsClass("Packet"));
            sendPacketMethod.invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static Class<?> getNmsClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server.v1_8_R3." + name);
    }

    private static Object createChatComponent(String text) throws Exception {
        Class<?> chatComponentTextClass = getNmsClass("ChatComponentText");
        Constructor<?> chatComponentTextConstructor = chatComponentTextClass.getDeclaredConstructor(String.class);

        return chatComponentTextConstructor.newInstance(text);
    }
}
