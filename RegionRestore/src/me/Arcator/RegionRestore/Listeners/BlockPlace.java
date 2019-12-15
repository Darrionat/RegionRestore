package me.Arcator.RegionRestore.Listeners;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.Arcator.RegionRestore.Main;
import me.Arcator.RegionRestore.Files.FileManager;

public class BlockPlace implements Listener {

	@SuppressWarnings("unused")
	private Main plugin;

	public BlockPlace(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockBreak(BlockPlaceEvent e) {
		if (plugin.getConfig().getBoolean("RegionOptions.Protection") == false) {
			return;
		}

		String blockName = e.getBlock().getType().name();
		if (plugin.getConfig().getList("RegionOptions.BlocksThatCanBeBroken").contains(blockName)) {
			return;
		}

		FileManager filemanager = new FileManager(plugin);
		String[] fileList = plugin.getDataFolder().list();
		FileConfiguration blockdata = filemanager.getDataConfig("blockdata");
		File blockDataFile = filemanager.getBlockDataFile("blockdata");
		for (String s : fileList) {
			if (s.equalsIgnoreCase("config.yml") || s.equalsIgnoreCase("blockdata.yml")) {
				continue;
			}
			String name = s.replace(".yml", "");
			if (plugin.getConfig().getList("RegionOptions.RegionsDisabled").contains(name)) {
				continue;
			}
			FileConfiguration blockDataConfig = filemanager.getDataConfig(name);

			String cornerOne = blockDataConfig.getString("corners.1");
			String[] arg1 = cornerOne.split(",");

			int[] parsed1 = new int[3];
			for (int a = 0; a < 3; a++) {
				parsed1[a] = Integer.parseInt(arg1[a]);
			}
			int x1 = parsed1[0];
			int y1 = parsed1[1];
			int z1 = parsed1[2];

			String cornerTwo = blockDataConfig.getString("corners.2");
			String[] arg2 = cornerTwo.split(",");
			int[] parsed2 = new int[3];
			for (int a = 0; a < 3; a++) {
				parsed2[a] = Integer.parseInt(arg2[a]);
			}
			int x2 = parsed2[0];
			int y2 = parsed2[1];
			int z2 = parsed2[2];

			int xLow = Math.min(x1, x2);
			int yLow = Math.min(y1, y2);
			int zLow = Math.min(z1, z2);
			int xHigh = Math.max(x1, x2);
			int yHigh = Math.max(y1, y2);
			int zHigh = Math.max(z1, z2);

			Location loc = e.getBlock().getLocation();
			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();
			// If player is out of area
			if (x < xLow || x > xHigh || y < yLow || y > yHigh || z < zLow || z > zHigh) {
				continue;
			}
			// Player is in area
			if (x >= xLow && x <= xHigh && y >= yLow && y <= yHigh && z >= zLow && z <= zHigh) {

				String sectionName = String.valueOf(x + "," + y + "," + z);
				blockdata.createSection(sectionName);
				ConfigurationSection section = blockdata.getConfigurationSection(sectionName);
				Block block = e.getBlock();
				section.set("TypeID", block.getTypeId());
				section.set("ByteID", block.getData());
				section.set("World", block.getWorld().getName());
				try {
					blockdata.save(blockDataFile);
					continue;
				} catch (IOException exe) {
					exe.printStackTrace();
					return;
				}
			}

		}
	}

}