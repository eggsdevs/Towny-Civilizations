package io.github.eggsdevs.townycivilizations;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lectern;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;

/**
 * Handles interactions and placement of the Mayoral Desk.
 */
public class MayoralDeskListener implements Listener {

    private static final String DESK_NAME = ChatColor.GOLD + "Mayoral Desk";

    private final Map<String, Location> desks = new HashMap<>();

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        String name = item.getItemMeta().getDisplayName();
        if (!DESK_NAME.equals(name)) {
            return;
        }

        Player player = event.getPlayer();
        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null) {
            player.sendMessage(ChatColor.RED + "You must belong to a town to place a Mayoral Desk.");
            event.setCancelled(true);
            return;
        }

        String townName = town.getName();
        if (desks.containsKey(townName)) {
            player.sendMessage(ChatColor.RED + "Your town already has a Mayoral Desk.");
            event.setCancelled(true);
            return;
        }

        Location loc = event.getBlockPlaced().getLocation();
        desks.put(townName, loc);

        BlockState state = loc.getBlock().getState();
        if (state instanceof Lectern lectern) {
            lectern.getInventory().setItem(0, new ItemStack(Material.WRITABLE_BOOK));
            lectern.update();
        }

                loc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc.clone().add(0.5, 1, 0.5), 20, 0.25, 0.5, 0.25, 0.1);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.LECTERN) {
            return;
        }

        Location loc = block.getLocation();
        desks.entrySet().removeIf(entry -> entry.getValue().equals(loc));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.LECTERN) {
            return;
        }

        Location loc = block.getLocation();
        if (!desks.containsValue(loc)) {
            return;
        }

        event.setCancelled(true);
        Inventory gui = Bukkit.createInventory(null, 9, DESK_NAME);
        event.getPlayer().openInventory(gui);
    }
}