package me.lostedark.aetherplugins.chest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BauCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando só pode ser executado por jogadores!");
            return true;
        }

        Player player = (Player) sender;
        Inventory inventory = player.getEnderChest(); // Use getEnderChest() para obter o bau do jogador

        player.openInventory(inventory); // Abre o bau do jogador

        return true;
    }
}
