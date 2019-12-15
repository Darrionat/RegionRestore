package me.Arcator.RegionRestore.Listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.Arcator.RegionRestore.Main;
import me.Arcator.RegionRestore.Utils.Maps;
import me.Arcator.RegionRestore.Utils.Selection;
import me.Arcator.RegionRestore.Utils.Utils;

public class ToolClick implements Listener {

	@SuppressWarnings("unused")
	private Main plugin;

	public ToolClick(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (p.getInventory().getItemInHand() == null) {
			return;
		}
		ItemStack hand = p.getInventory().getItemInHand();
		if (hand.getType() != Material.GOLD_SPADE) {
			return;
		}
		if (hand.getItemMeta().getDisplayName() == null) {
			return;
		}
		if (!hand.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&eRegion Selector"))) {
			return;
		}
		if (e.getClickedBlock() == null || e.getClickedBlock().getType().equals(Material.AIR)) {
			return;
		}
		if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_AIR) {
			return;
		}
		Block b = e.getClickedBlock();
		Location loc = b.getLocation();
		Action action = e.getAction();
		Maps maps = new Maps();
		HashMap<String, Location> map = maps.getSelectionMap();
		if (action == Action.LEFT_CLICK_BLOCK) {
			map.put("L " + p.getName(), loc);
			String msg = Utils.chat("&cPosition 1 Selected &7(" + loc.getBlockX() + ", " + loc.getBlockY() + ", "
					+ loc.getBlockZ() + ") (" + blockCounter(p) + " blocks)");
			p.sendMessage(msg);
			return;
		}
		if (action == Action.RIGHT_CLICK_BLOCK) {
			map.put("R " + p.getName(), loc);
			String msg = Utils.chat("&cPosition 2 Selected &7(" + loc.getBlockX() + ", " + loc.getBlockY() + ", "
					+ loc.getBlockZ() + ") (" + blockCounter(p) + " blocks)");
			p.sendMessage(msg);
			return;
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (p.getInventory().getItemInHand() == null) {
			return;
		}
		ItemStack hand = p.getInventory().getItemInHand();
		if (hand.getType() != Material.GOLD_SPADE) {
			return;
		}
		if (hand.getItemMeta().getDisplayName() == null) {
			return;
		}
		if (hand.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&eRegion Selector"))) {
			e.setCancelled(true);
			return;
		}
	}

	public int blockCounter(Player p) {
		Maps maps = new Maps();
		HashMap<String, Location> blocksSelectedMap = maps.getSelectionMap();
		if (blocksSelectedMap.get("L " + p.getName()) == null || blocksSelectedMap.get("R " + p.getName()) == null) {
			return 0;
		}
		Location locOne = blocksSelectedMap.get("L " + p.getName());
		Location locTwo = blocksSelectedMap.get("R " + p.getName());
		int size = Selection.blocksFromTwoPoints(locOne, locTwo).size();

		return size;
	}
}
