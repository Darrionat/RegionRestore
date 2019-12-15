package me.Arcator.RegionRestore.Files;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.Arcator.RegionRestore.Main;
import me.Arcator.RegionRestore.Utils.Utils;

public class FileManager {

	private Main plugin;

	public FileManager(Main plugin) {
		this.plugin = plugin;
	}

	// Files and File configurations here
	public static FileConfiguration blockDataConfig;
	public File blockDataFile;
	// -------------------------------------

	public void setup(String fileName, Player p) {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		blockDataFile = new File(plugin.getDataFolder(), fileName + ".yml");

		if (!blockDataFile.exists()) {
			try {
				blockDataFile.createNewFile();
				blockDataConfig = YamlConfiguration.loadConfiguration(blockDataFile);
				String successMessage = "&e[" + plugin.getName() + "] &aCreated the " + fileName + ".yml file";
				Bukkit.getServer().getConsoleSender().sendMessage(Utils.chat(successMessage));
				p.sendMessage(Utils.chat(successMessage));
				saveMessages();
			} catch (IOException exe) {
				String failMessage = "&e[" + plugin.getName() + "] &cFailed to create the " + fileName + ".yml file";
				Bukkit.getServer().getConsoleSender().sendMessage(Utils.chat(failMessage));
				p.sendMessage(Utils.chat(failMessage));
				exe.printStackTrace();
			}
		}

	}

	public boolean fileExists(String fileName) {
		blockDataFile = new File(plugin.getDataFolder(), fileName + ".yml");
		if (blockDataFile.exists()) {
			return true;
		}
		return false;
	}

	public void deleteFile(String fileName) {
		blockDataFile = new File(plugin.getDataFolder(), fileName + ".yml");
		blockDataFile.delete();
		return;
	}

	public FileConfiguration getDataConfig(String fileName) {
		blockDataFile = new File(plugin.getDataFolder(), fileName + ".yml");
		blockDataConfig = YamlConfiguration.loadConfiguration(blockDataFile);
		return blockDataConfig;
	}

	public File getBlockDataFile(String fileName) {
		blockDataFile = new File(plugin.getDataFolder(), fileName + ".yml");
		return blockDataFile;
	}

	public void saveMessages() {
		/*
		 * try { messagesConfig.set("updatePt2",
		 * "&bAn update for &7GUIShopSpawners &f(%UpdatedVersion%) &bis available at");
		 * messagesConfig.save(messagesFile); String successMessage = "&e[" +
		 * plugin.getName() + "] &aSaved the messages.yml file";
		 * Bukkit.getServer().getConsoleSender().sendMessage(Utils.chat(successMessage))
		 * ; } catch (IOException exe) {
		 * 
		 * }
		 */

	}

	public void reloadBlockDataFile(String fileName) {
		blockDataFile = new File(plugin.getDataFolder(), fileName + ".yml");
		blockDataConfig = YamlConfiguration.loadConfiguration(blockDataFile);
		String reloadMessage = "&e[" + plugin.getName() + "] &aReloaded the " + fileName + ".yml file";
		Bukkit.getServer().getConsoleSender().sendMessage(Utils.chat(reloadMessage));
	}

	public void setupBlockData(String fileName) {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		blockDataFile = new File(plugin.getDataFolder(), fileName + ".yml");

		if (!blockDataFile.exists()) {
			try {
				blockDataFile.createNewFile();
				blockDataConfig = YamlConfiguration.loadConfiguration(blockDataFile);
				String successMessage = "&e[" + plugin.getName() + "] &aCreated the " + fileName + ".yml file";
				Bukkit.getServer().getConsoleSender().sendMessage(Utils.chat(successMessage));
				saveMessages();
			} catch (IOException exe) {
				String failMessage = "&e[" + plugin.getName() + "] &cFailed to create the " + fileName + ".yml file";
				Bukkit.getServer().getConsoleSender().sendMessage(Utils.chat(failMessage));
				exe.printStackTrace();
			}
		}
	}
}
