package me.Arcator.RegionRestore.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import me.Arcator.RegionRestore.Main;
import me.Arcator.RegionRestore.Files.FileManager;

public class ExplodeEntity implements Listener {

	@SuppressWarnings("unused")
	private Main plugin;

	public ExplodeEntity(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockBreak(EntityExplodeEvent e) {

		if (plugin.getConfig().getBoolean("RegionOptions.Protection") == false) {
			return;
		}
		if (plugin.getConfig().getBoolean("RegionOptions.TNTExplosions") == true) {
			return;
		}

		if (e.getEntityType() != EntityType.PRIMED_TNT) {
			return;
		}

		FileManager filemanager = new FileManager(plugin);
		String[] fileList = plugin.getDataFolder().list();
		for (String s : fileList) {
			if (s.equalsIgnoreCase("config.yml")) {
				continue;
			}
			String name = s.replace(".yml", "");
			if (plugin.getConfig().getList("RegionOptions.RegionsDisabled").contains(name)) {
				continue;
			}
			FileConfiguration blockDataConfig = filemanager.getDataConfig(s.replace(".yml", ""));
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

			Location loc = e.getLocation();
			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();
			// If TNT is out of area
			// Corners YAML to avoid so much math beign done every event
			if (x < xLow || x > xHigh || y < yLow || y > yHigh || z < zLow || z > zHigh) {
				return;
			}
			// TNT is in area
			if (x >= xLow && x <= xHigh && y >= yLow && y <= yHigh && z >= zLow && z <= zHigh) {
				e.setCancelled(true);
				return;
			}

		}
	}
}
