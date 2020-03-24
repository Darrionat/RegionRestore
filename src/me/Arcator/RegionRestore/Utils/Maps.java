package me.Arcator.RegionRestore.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;

public class Maps {

	public Maps() {
		// TODO Auto-generated constructor stub
	}

	private static HashMap<String, Location> blocksSelectedMap = new HashMap<String, Location>();

	public HashMap<String, Location> getSelectionMap() {

		return blocksSelectedMap;
	}

	private static List<String> toggleBypassMap = new ArrayList<String>();

	public List<String> getToggledBypassMap() {

		return toggleBypassMap;
	}

}
