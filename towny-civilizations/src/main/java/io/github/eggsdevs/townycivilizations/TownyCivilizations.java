package io.github.eggsdevs.townycivilizations;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class TownyCivilizations extends JavaPlugin {

    private MayoralDeskListener mayoralDeskListener;
    private File desksFile;

    @Override
    public void onEnable() {
        getLogger().info("TownyCivilizations enabled");
        registerMayoralDeskRecipe();

        File serverFolder = getDataFolder().getParentFile().getParentFile();
        File dataFolder = new File(serverFolder, "Towny Civilizations");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        desksFile = new File(dataFolder, "mayoral-desks.yml");

        mayoralDeskListener = new MayoralDeskListener(this, desksFile);
        mayoralDeskListener.loadDesks();
        getServer().getPluginManager().registerEvents(mayoralDeskListener, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("townycivilizations")) {
            sender.sendMessage(ChatColor.GOLD + "Towny Civilizations" + ChatColor.GRAY + " adds civilization management features to Towny.");
            sender.sendMessage(ChatColor.YELLOW + "Develop your town into a thriving civilization!");
            return true;
        } else if (command.getName().equalsIgnoreCase("mayoraldesks")) {
            if (!sender.isOp() && !sender.hasPermission("townycivilizations.mayoraldesks")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }

            int page = 1;
            if (args.length > 0) {
                try {
                    page = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid page number.");
                    return true;
                }
            }

            List<Map.Entry<String, Location>> entries = new ArrayList<>(mayoralDeskListener.getDesks().entrySet());
            int perPage = 5;
            int totalPages = (int) Math.ceil(entries.size() / (double) perPage);
            if (totalPages == 0) {
                totalPages = 1;
            }
            if (page < 1 || page > totalPages) {
                sender.sendMessage(ChatColor.RED + "Page out of range. (1-" + totalPages + ")");
                return true;
            }

            sender.sendMessage(ChatColor.GOLD + "Active Mayoral Desks - Page " + page + "/" + totalPages);
            int start = (page - 1) * perPage;
            int end = Math.min(start + perPage, entries.size());
            for (int i = start; i < end; i++) {
                var entry = entries.get(i);
                Location loc = entry.getValue();
                sender.sendMessage(ChatColor.YELLOW + entry.getKey() + ChatColor.GRAY + " at "
                        + ChatColor.AQUA + loc.getWorld().getName() + " " + loc.getBlockX() + ", "
                        + loc.getBlockY() + ", " + loc.getBlockZ());
            }
            return true;
        }
        return false;
    }

    @Override
    public void onDisable() {
        if (mayoralDeskListener != null) {
            mayoralDeskListener.saveDesks();
        }
    }

    private void registerMayoralDeskRecipe() {
        ItemStack mayoralDesk = new ItemStack(Material.LECTERN);
        ItemMeta meta = mayoralDesk.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Mayoral Desk");
            meta.setLore(Arrays.asList(ChatColor.GRAY + "Allows mayors to configure their town."));
            mayoralDesk.setItemMeta(meta);
        }

        NamespacedKey key = new NamespacedKey(this, "mayoral_desk");
        ShapedRecipe recipe = new ShapedRecipe(key, mayoralDesk);
        recipe.shape("XXX", "XLX", "XXX");
        recipe.setIngredient('X', Material.DIAMOND);
        recipe.setIngredient('L', Material.LECTERN);

        Bukkit.addRecipe(recipe);
    }
}