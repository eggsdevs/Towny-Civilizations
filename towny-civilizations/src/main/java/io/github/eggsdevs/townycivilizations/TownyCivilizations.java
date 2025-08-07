package io.github.eggsdevs.townycivilizations;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for TownyCivilizations.
 */
public class TownyCivilizations extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("TownyCivilizations enabled");
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
}