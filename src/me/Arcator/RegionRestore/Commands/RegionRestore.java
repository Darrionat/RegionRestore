package me.Arcator.RegionRestore.Commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import me.Arcator.RegionRestore.Main;
import me.Arcator.RegionRestore.Files.FileManager;
import me.Arcator.RegionRestore.Utils.Maps;
import me.Arcator.RegionRestore.Utils.Selection;
import me.Arcator.RegionRestore.Utils.Utils;

public class RegionRestore implements CommandExecutor {

	private Main plugin;

	public RegionRestore(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("regionrestore").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		ConfigurationSection config = plugin.getConfig();
		String noPermMsg = Utils.chat(config.getString("messages.NoPermission"));
		String onlyPlayersMsg = Utils.chat(config.getString("messages.OnlyPlayers"));
		Maps maps = new Maps();
		if (sender instanceof Player) {
			Player p = (Player) sender;
			String commandPermission = "regionrestore.command";
			if (!p.hasPermission(commandPermission)) {
				p.sendMessage(noPermMsg.replace("%permission%", commandPermission));
				return true;
			}
		}
		if (args.length == 0) {
			for (String s : descriptionMessage(sender)) {
				sender.sendMessage(Utils.chat(s));
				continue;
			}
			return true;
		}
		// Give a player with permission a golden shovel. Custom name '&eRegion
		// Selector'
		if (args[0].equalsIgnoreCase("tool")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(onlyPlayersMsg);
				return true;
			}
			Player p = (Player) sender;
			String toolPermission = "regionrestore.tool";
			if (!p.hasPermission(toolPermission)) {
				p.sendMessage(noPermMsg.replace("%permission%", toolPermission));
				return true;
			}
			// If player has permission to use tool
			ItemStack goldShovel = new ItemStack(Material.GOLD_SPADE, 1);
			ItemMeta meta = goldShovel.getItemMeta();
			meta.setDisplayName(Utils.chat("&eRegion Selector"));
			goldShovel.setItemMeta(meta);
			p.getInventory().addItem(goldShovel);
			return true;
		}
		// /regionrestore save NAME
		if (args[0].equalsIgnoreCase("save")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(onlyPlayersMsg);
				return true;
			}
			Player p = (Player) sender;
			String savePermission = "regionrestore.save";
			if (!p.hasPermission(savePermission)) {
				p.sendMessage(noPermMsg.replace("%permission%", savePermission));
				return true;
			}
			HashMap<String, Location> blocksSelectedMap = maps.getSelectionMap();
			if (blocksSelectedMap.get("L " + p.getName()) == null
					|| blocksSelectedMap.get("R " + p.getName()) == null) {
				p.sendMessage(Utils.chat("&cYou have not made a selection."));
				return true;
			}
			if (args.length != 2) {
				p.sendMessage(Utils.chat("&4Error: &cUse this format"));
				p.sendMessage(Utils.chat("&c/regionrestore save name_of_selection"));
				return true;
			}
			// Make custom file for the certain name such as spawn.yml
			FileManager filemanager = new FileManager(plugin);
			String fileName = args[1];
			if (filemanager.fileExists(fileName) == true) {
				p.sendMessage(Utils.chat("&4Error: &cA file with the name " + fileName + " already exists!"));
				return true;
			}
			FileConfiguration blockDataConfig = filemanager.getDataConfig(fileName);
			File blockDataFile = filemanager.getBlockDataFile(fileName);
			Location locOne = blocksSelectedMap.get("L " + p.getName());
			Location locTwo = blocksSelectedMap.get("R " + p.getName());
			Selection.blocksFromTwoPoints(locOne, locTwo);
			saveMethod(locOne, locTwo, blockDataConfig, blockDataFile, plugin, p, fileName);
			return true;

		}
		if (args[0].equalsIgnoreCase("delete")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				String deletePermission = "regionrestore.delete";
				if (!p.hasPermission(deletePermission)) {
					p.sendMessage(noPermMsg.replace("%permission%", deletePermission));
					return true;
				}
			}

			// regionrestore delete NAME confirm
			if (args.length != 3 || !args[2].equalsIgnoreCase("confirm")) {
				sender.sendMessage(Utils.chat("&4Error: &cUse this format"));
				sender.sendMessage(Utils.chat("&c/regionrestore delete {NAME} confirm"));
				return true;
			}
			FileManager filemanager = new FileManager(plugin);
			String fileName = args[1];
			if (fileName.equalsIgnoreCase("config")) {
				sender.sendMessage(Utils.chat(config.getString("messages.NoDeleteConfig")));
				return true;
			}
			if (filemanager.fileExists(fileName) == false) {
				sender.sendMessage(
						Utils.chat(config.getString("messages.FileDoesNotExist")).replace("%name%", fileName));
				return true;
			}
			filemanager.deleteFile(fileName);
			sender.sendMessage(Utils.chat(config.getString("messages.SuccessfulDeletion").replace("%name%", fileName)));
			return true;
		}
		if (args[0].equalsIgnoreCase("restore")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				String restorePermission = "regionrestore.restore";
				if (!p.hasPermission(restorePermission)) {
					p.sendMessage(noPermMsg.replace("%permission%", restorePermission));
					return true;
				}
			}
			// /regionrestore restore NAME
			if (args.length != 2) {
				sender.sendMessage(Utils.chat("&4Error: &cUse this format"));
				sender.sendMessage(Utils.chat("&c/regionrestore restore {NAME}"));
				return true;
			}
			FileManager filemanager = new FileManager(plugin);
			String fileName = args[1];
			if (filemanager.fileExists(fileName) == false) {
				sender.sendMessage(
						Utils.chat(config.getString("messages.FileDoesNotExist")).replace("%name%", fileName));
				return true;
			}
			restoreMethod(fileName, sender);
			return true;

		}
		if (args[0].equalsIgnoreCase("list")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				String listPermission = "regionrestore.list";
				if (!p.hasPermission(listPermission)) {
					p.sendMessage(noPermMsg.replace("%permission%", listPermission));
					return true;
				}
			}
			sender.sendMessage(Utils.chat("&eAvailable Regions to Restore:"));
			String[] fileList = plugin.getDataFolder().list();
			for (String s : fileList) {
				sender.sendMessage(Utils.chat("&e" + s).replace(".yml", ""));
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("bypass")) {

			if (!(sender instanceof Player)) {
				sender.sendMessage(onlyPlayersMsg);
				return true;
			}
			Player p = (Player) sender;
			if (config.getBoolean("RegionOptions.Protection") == false) {
				p.sendMessage(Utils.chat(config.getString("messages.BypassProtectionDisabled")));
				return true;
			}

			String bypassPermission = "regionrestore.bypass";
			if (!p.hasPermission(bypassPermission)) {
				p.sendMessage(noPermMsg.replace("%permission%", bypassPermission));
				return true;
			}
			List<String> bypassList = maps.getToggledBypassMap();
			if (!bypassList.contains(p.getName())) {
				bypassList.add(p.getName());
				p.sendMessage(Utils.chat(config.getString("messages.EnablingBypassProtection")));
				return true;
			}
			bypassList.remove(p.getName());
			p.sendMessage(Utils.chat(config.getString("messages.DisablingBypassProtection")));
			return true;
		}
		return true;
	}

	public ArrayList<String> descriptionMessage(CommandSender sender) {
		ArrayList<String> messages = new ArrayList<String>();
		messages.add("&cRegion Restore by Darrionat");
		messages.add("&e/rr list - Lists all regions");
		messages.add("&e/rr tool - Gives tool for selection");
		messages.add("&e/rr bypass - Bypass region protection");
		messages.add("&e/rr save {NAME} - Save a region");
		messages.add("&e/rr restore {NAME} - Restore a region from save");
		messages.add("&e/rr delete {NAME} confirm - Delete a region forever");
		return messages;
	}

	public void saveMethod(Location locOne, Location locTwo, FileConfiguration blockDataConfig, File blockDataFile,
			JavaPlugin plugin, Player p, String fileName) {
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				long start = System.currentTimeMillis();
				blockDataConfig.set("PostCommand", "");
				blockDataConfig.createSection("corners");
				ConfigurationSection cornersSection = blockDataConfig.getConfigurationSection("corners");
				cornersSection.set("1",
						String.valueOf(locOne.getBlockX() + "," + locOne.getBlockY() + "," + locOne.getBlockZ()));
				cornersSection.set("2",
						String.valueOf(locTwo.getBlockX() + "," + locTwo.getBlockY() + "," + locTwo.getBlockZ()));
				cornersSection.set("World", locOne.getWorld().getName());
				FileConfiguration config = plugin.getConfig();
				int i = 0;

				for (Block block : Selection.blocksFromTwoPoints(locOne, locTwo)) {
					Location loc = block.getLocation();
					if (block.getType() == Material.AIR) {
						if (!loc.equals(locOne) && !loc.equals(locTwo)) {
							continue;
						}
					}

					int x = loc.getBlockX();
					int y = loc.getBlockY();
					int z = loc.getBlockZ();
					String sectionName = String.valueOf(x + "," + y + "," + z);

					blockDataConfig.createSection(sectionName);
					ConfigurationSection section = blockDataConfig.getConfigurationSection(sectionName);
					section.set("TypeID", block.getTypeId());
					section.set("ByteID", block.getData());
					section.set("World", block.getWorld().getName());
					i++;
					if (i == 5000) {
						try {
							blockDataConfig.save(blockDataFile);

							i = 0;
							continue;
						} catch (IOException e) {
							e.printStackTrace();
							return;
						}
					}
					// plugin.saveConfig();
					/*
					 * try { Thread.sleep(1); } catch (InterruptedException e) {
					 * e.printStackTrace(); break; }
					 */
					continue;
				}

				// Saves
				try {
					long end = System.currentTimeMillis();
					long timeTaken = end / 1000 - start / 1000;
					blockDataConfig.save(blockDataFile);
					String successMsg = Utils.chat(config.getString("messages.SuccessfulSave"))
							.replace("%name%", fileName).replace("%time%", String.valueOf(timeTaken));
					p.sendMessage(successMsg);
					System.out.println(successMsg);
					return;
				} catch (IOException e) {
					String unsuccessfulMsg = Utils.chat(config.getString("messages.UnsuccessfulSave")).replace("%name%",
							fileName);
					p.sendMessage(unsuccessfulMsg);
					System.out.println(unsuccessfulMsg);
					e.printStackTrace();
					return;
				}
			}
		});// 60 L == 3 sec, 20 ticks == 1 sec

	}

	@SuppressWarnings("deprecation")
	public void restoreMethod(String fileName, CommandSender sender) {
		FileManager filemanager = new FileManager(plugin);
		FileConfiguration blockDataConfig = filemanager.getDataConfig(fileName);
		// Location loc = new Location(world, x,y,z);
		long start = System.currentTimeMillis();

		ConfigurationSection corners = blockDataConfig.getConfigurationSection("corners");
		String coordinatesOne = corners.getString("1");
		String[] arg1 = coordinatesOne.split(",");
		int[] parsed1 = new int[3];
		for (int a = 0; a < 3; a++) {
			parsed1[a] = Integer.parseInt(arg1[a]);
		}
		Location loc1 = new Location(
				Bukkit.getWorld(blockDataConfig.getConfigurationSection(coordinatesOne).getString("World")), parsed1[0],
				parsed1[1], parsed1[2]);

		String coordinatesTwo = corners.getString("2");
		String[] arg2 = coordinatesTwo.split(",");
		int[] parsed2 = new int[3];
		for (int a = 0; a < 3; a++) {
			parsed2[a] = Integer.parseInt(arg2[a]);
		}
		Location loc2 = new Location(
				Bukkit.getWorld(blockDataConfig.getConfigurationSection(coordinatesTwo).getString("World")), parsed2[0],
				parsed2[1], parsed2[2]);
		for (Block b : Selection.blocksFromTwoPoints(loc1, loc2)) {

			Location loc = b.getLocation();
			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();
			String key = String.valueOf(x + "," + y + "," + z);
			if (blockDataConfig.getConfigurationSection(key) == null) {
				b.setType(Material.AIR);
				continue;
			}
			ConfigurationSection section = blockDataConfig.getConfigurationSection(key);
			b.setTypeId(section.getInt("TypeID"));
			b.setData((byte) section.getInt("ByteID"));
		}
		if (!blockDataConfig.getString("PostCommand").equalsIgnoreCase("")) {
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), blockDataConfig.getString("PostCommand"));
		}
		long end = System.currentTimeMillis();
		long timeTaken = end / 1000 - start / 1000;
		sender.sendMessage(Utils.chat(plugin.getConfig().getString("messages.RestoreSuccessful"))
				.replace("%name%", fileName).replace("%time%", String.valueOf(timeTaken)));
		return;

	}

}
