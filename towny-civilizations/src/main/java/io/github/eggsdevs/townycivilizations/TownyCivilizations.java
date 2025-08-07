package io.github.eggsdevs.townycivilizations;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for TownyCivilizations.
 */
public class TownyCivilizations extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("TownyCivilizations enabled");
        registerMayoralDeskRecipe();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("townycivilizations")) {
            sender.sendMessage(ChatColor.GOLD + "Towny Civilizations" + ChatColor.GRAY + " adds civilization management features to Towny.");
            sender.sendMessage(ChatColor.YELLOW + "Develop your town into a thriving civilization!");
            return true;
        }
        return false;
    }

    /**
     * Registers the crafting recipe for a special lectern named "Mayoral Desk".
     */
    private void registerMayoralDeskRecipe() {
        ItemStack mayoralDesk = new ItemStack(Material.LECTERN);
        ItemMeta meta = mayoralDesk.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Mayoral Desk");
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