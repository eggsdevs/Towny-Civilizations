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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;

/**
 * Handles interactions and placement of the Mayoral Desk.
 */
public class MayoralDeskListener implements Listener {

    private static final String DESK_NAME = ChatColor.GOLD + "Mayoral Desk";

    private final Map<String, Location> desks = new HashMap<>();
    private final JavaPlugin plugin;

    public MayoralDeskListener(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Location loc : desks.values()) {
                loc.getWorld().spawnParticle(Particle.ENCHANT, loc.clone().add(0.5, 0.75, 0.5), 20, 0.5, 0.5, 0.5, 0.0);
            }
        }, 20L, 20L);
    }

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

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null || !resident.equals(town.getMayor())) {
            player.sendMessage(ChatColor.RED + "Only the Mayor can place a Mayoral Desk.");
            event.setCancelled(true);
            return;
        }

        Location loc = event.getBlockPlaced().getLocation();
        TownBlock townBlock = TownyAPI.getInstance().getTownBlock(loc);
        if (townBlock == null || town.getHomeBlockOrNull() == null || !townBlock.equals(town.getHomeBlockOrNull())) {
            player.sendMessage(ChatColor.RED + "The Mayoral Desk must be placed in your town's homeblock.");
            event.setCancelled(true);
            return;
        }

        String townName = town.getName();
        Location old = desks.get(townName);
        if (old != null) {
            old.getBlock().setType(Material.AIR);
            old.getWorld().spawnParticle(Particle.LARGE_SMOKE, old.clone().add(0.5, 0.5, 0.5), 20, 0.2, 0.2, 0.2, 0.0);
        }
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
        if (desks.entrySet().removeIf(entry -> entry.getValue().equals(loc))) {
            loc.getWorld().spawnParticle(Particle.LARGE_SMOKE, loc.clone().add(0.5, 0.5, 0.5), 30, 0.3, 0.3, 0.3, 0.0);
        }
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (DESK_NAME.equals(event.getView().getTitle())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (DESK_NAME.equals(event.getView().getTitle())) {
            event.setCancelled(true);
        }
    }
}