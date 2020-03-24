package me.Arcator.RegionRestore;

import org.bukkit.plugin.java.JavaPlugin;

import me.Arcator.RegionRestore.Commands.RegionRestore;
import me.Arcator.RegionRestore.Files.FileManager;
import me.Arcator.RegionRestore.Listeners.BlockBreak;
import me.Arcator.RegionRestore.Listeners.BlockPlace;
import me.Arcator.RegionRestore.Listeners.ExplodeEntity;
import me.Arcator.RegionRestore.Listeners.ToolClick;

public class Main extends JavaPlugin {

	public void onEnable() {
		// Starting up commands
		new RegionRestore(this);
		// Setting up listeners
		new ToolClick(this);
		new BlockBreak(this);
		new BlockPlace(this);
		new ExplodeEntity(this);
		saveDefaultConfig();
		FileManager filemanager = new FileManager(this);
		if (!filemanager.fileExists("blockdata")) {
			filemanager.setupBlockData("blockdata");
		}
	}

	public void onDisable() {

	}

}
