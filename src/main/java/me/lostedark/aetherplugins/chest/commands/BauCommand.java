package me.lostedark.aetherplugins.chest.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BauCommand implements CommandExecutor {

    private static final int MAX_BAUS = 4; // Defina o número máximo de baus
    private static final String BAUS_FILE_NAME = "baus.yml"; // Nome do arquivo de configuração

    private Map<UUID, Inventory[]> baus = new HashMap<>(); // HashMap para mapear jogadores aos seus baus
    private Plugin plugin;
    private File bausFile;
    private YamlConfiguration bausConfig;

    public BauCommand(Plugin plugin) {
        this.plugin = plugin;
        bausFile = new File(plugin.getDataFolder(), BAUS_FILE_NAME);
        if (!bausFile.exists()) {
            try {
                bausFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bausConfig = YamlConfiguration.loadConfiguration(bausFile);
        loadBaus();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando só pode ser executado por jogadores!");
            return true;
        }

        Player player = (Player) sender;

        // Verificar o número de argumentos passados
        if (args.length == 1) {
            int inventarioNumero = 0;
            try {
                inventarioNumero = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage("Número de inventário inválido!");
                return true;
            }

            if (inventarioNumero > 0 && inventarioNumero <= MAX_BAUS) {
                if (hasPermissionForInventario(player, inventarioNumero)) {
                    openInventario(player, inventarioNumero); // Abre o inventário correspondente
                } else {
                    player.sendMessage("Você não tem permissão para acessar este inventário!");
                }
            } else {
                player.sendMessage("Número de inventário inválido!");
            }
        } else {
            player.sendMessage("Uso correto: /bau <número do inventário>");
        }

        return true;
    }

    private boolean hasPermissionForInventario(Player player, int inventarioNumero) {
        String permission = "role.bau." + inventarioNumero;

        // Verificar as permissões do jogador
        if (player.hasPermission("role.mvpplus")) {
            return true; // Acesso a todos os baus
        } else if (player.hasPermission("role.mvp") && inventarioNumero <= 3) {
            return true; // Acesso aos baus 1, 2 e 3
        } else if (player.hasPermission("role.vip") && inventarioNumero <= 2) {
            return true; // Acesso aos baus 1 e 2
        } else if (inventarioNumero == 1) {
            return true; // Acesso ao bau 1 para jogadores sem permissão
        }

        return false; // Sem permissão para acessar o inventário especificado
    }

    private void openInventario(Player player, int inventarioNumero) {
        UUID playerId = player.getUniqueId();
        Inventory[] inventarios = baus.get(playerId); // Obtém os baus do jogador a partir do HashMap

        if (inventarios == null) {
            inventarios = new Inventory[MAX_BAUS]; // Cria um novo array de inventários
            baus.put(playerId, inventarios); // Mapeia o jogador aos baus
        }

        int inventarioIndex = inventarioNumero - 1;
        Inventory inventory = inventarios[inventarioIndex]; // Obtém o inventário correspondente

        if (inventory == null) {
            inventory = copyEnderChest(player.getEnderChest()); // Cria uma cópia do inventário do Ender Chest
            inventarios[inventarioIndex] = inventory; // Armazena o inventário no array
        }

        player.openInventory(inventory); // Abre o inventário para o jogador
    }

    private Inventory copyEnderChest(Inventory enderChest) {
        Inventory inventory = Bukkit.createInventory(null, enderChest.getSize());
        inventory.setContents(enderChest.getContents());
        return inventory;
    }

    private void loadBaus() {
        ConfigurationSection bausSection = bausConfig.getConfigurationSection("baus");
        if (bausSection == null) {
            return;
        }

        for (String playerIdString : bausSection.getKeys(false)) {
            UUID playerId = UUID.fromString(playerIdString);
            Inventory[] inventarios = new Inventory[MAX_BAUS];

            ConfigurationSection jogadorSection = bausSection.getConfigurationSection(playerIdString);
            for (String inventarioIndexString : jogadorSection.getKeys(false)) {
                int inventarioIndex = Integer.parseInt(inventarioIndexString);
                ConfigurationSection inventarioSection = jogadorSection.getConfigurationSection(inventarioIndexString);
                Inventory inventory = deserializeInventory(inventarioSection);
                inventarios[inventarioIndex] = inventory;
            }

            baus.put(playerId, inventarios);
        }
    }

    private void saveBaus() {
        ConfigurationSection bausSection = bausConfig.createSection("baus");

        for (Map.Entry<UUID, Inventory[]> entry : baus.entrySet()) {
            UUID playerId = entry.getKey();
            Inventory[] inventarios = entry.getValue();

            ConfigurationSection jogadorSection = bausSection.createSection(playerId.toString());
            for (int inventarioIndex = 0; inventarioIndex < inventarios.length; inventarioIndex++) {
                Inventory inventory = inventarios[inventarioIndex];
                if (inventory != null) {
                    ConfigurationSection inventarioSection = jogadorSection.createSection(Integer.toString(inventarioIndex));
                    serializeInventory(inventory, inventarioSection);
                }
            }
        }

        try {
            bausConfig.save(bausFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Chame esse método quando o plugin estiver sendo desativado para salvar os baus dos jogadores
    public void saveBausOnDisable() {
        saveBaus();
    }

    private void serializeInventory(Inventory inventory, ConfigurationSection inventarioSection) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                inventarioSection.set(Integer.toString(i), item);
            }
        }
    }

    private Inventory deserializeInventory(ConfigurationSection inventarioSection) {
        Inventory inventory = Bukkit.createInventory(null, 27);

        for (String slotString : inventarioSection.getKeys(false)) {
            int slot = Integer.parseInt(slotString);
            ItemStack item = inventarioSection.getItemStack(slotString);
            inventory.setItem(slot, item);
        }

        return inventory;
    }
}
