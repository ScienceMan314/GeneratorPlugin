package me.ScienceMan.generatorplugin;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;

public class Generator {
	
	private Main plugin;
	private int level, lastRunTime;
	private Material material;
	private Inventory inventory;
	private Location dropLoc;
	
	public Generator(Main plugin, Location l, int level, int lastRunTime) {
		this.plugin = plugin;
		this.level = level;
		dropLoc = l;
		this.lastRunTime = lastRunTime;
		material = Material.valueOf(Main.getConfig("pricing").getString("level" + this.level + ".item").toUpperCase());
		updateInventory();
	}
	
	public static ItemMeta getMeta(int l) {
		ItemMeta meta = (new ItemStack(Material.FURNACE,1)).getItemMeta();
		meta.setDisplayName(ChatColor.DARK_AQUA + "Generator");
		String materialString = "";
		for(String s : Main.getConfig("pricing").getString("level" + l + ".item").toUpperCase().split("_")) {
			materialString += s.charAt(0);
			materialString += s.substring(1).toLowerCase();
			materialString += " ";
		}
		meta.setLore(Arrays.asList(
				ChatColor.WHITE + "Level " + ChatColor.GREEN + l + ChatColor.WHITE + " Generator",
				ChatColor.WHITE + "Produces " + ChatColor.GOLD + "" + materialString.subSequence(0, materialString.length() - 1) + ChatColor.WHITE
					+ " every " + ChatColor.GREEN + "" + Main.getConfig("pricing").getInt("level" + l + ".time") + ChatColor.WHITE + " seconds"));
		return meta;
	}
	
	public void updateInventory() {				
		inventory = plugin.getServer().createInventory(null, 9, ChatColor.BLUE + "Generator Upgrade Menu");
		
		ItemStack empty = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
		ItemMeta emptyMeta = empty.getItemMeta();
		emptyMeta.setDisplayName("*");
		empty.setItemMeta(emptyMeta);

		ItemStack currentIs = new ItemStack(Material.FURNACE, 1);
		currentIs.setItemMeta(getMeta(level));
		
		if(canLevelUp()) {
			ItemStack upgradeIs = new ItemStack(Material.valueOf(Main.getConfig("pricing").getString("level"+ (level + 1) + ".item").toUpperCase()), 1);
			ItemMeta upgradeMeta = upgradeIs.getItemMeta();
			upgradeMeta.setDisplayName(ChatColor.DARK_AQUA + "Upgrade Generator");
			String materialString = "";
			for(String s : Main.getConfig("pricing").getString("level" + (level+1) + ".item").toUpperCase().split("_")) {
				materialString += s.charAt(0);
				materialString += s.substring(1).toLowerCase();
				materialString += " ";
			}
			upgradeMeta.setLore(Arrays.asList(
					ChatColor.WHITE + "Upgrade to level " + ChatColor.GREEN + (level + 1),
					ChatColor.WHITE + "Will produce " + ChatColor.GOLD + materialString.substring(0, materialString.length()-1) + ChatColor.WHITE
						+ " every " + ChatColor.GREEN + Main.getConfig("pricing").getInt("level" + (level+1) + ".time") + ChatColor.WHITE + " seconds",
					ChatColor.WHITE + "Costs " + ChatColor.GREEN + "$" + Main.getConfig("pricing").getInt("level"+ (level + 1) + ".price")));
			upgradeIs.setItemMeta(upgradeMeta);
			
			ItemStack[] items = {empty, empty, empty, currentIs, empty, upgradeIs, empty, empty, empty};
			inventory.setContents(items);
		}
		else {
			ItemStack[] items = {empty, empty, empty, empty, currentIs, empty, empty, empty, empty};
			inventory.setContents(items);
		}
		
	}
	
	public void run() {
		lastRunTime++;
		if(lastRunTime >= Main.getConfig("pricing").getInt("level" + level + ".time")) {
			dropLoc.getWorld().dropItemNaturally(dropLoc, new ItemStack(material, 1)).setVelocity(new Vector());
			lastRunTime = 0;
		}
	}
	
	public int getLastRunTime() {
		return lastRunTime;
	}
	
	public ItemMeta getMeta() {
		return getMeta(level);
	}
	
	public int getLevel() {
		return level;
	}
	
	public static int getMaxLevel() {
		int i = 1;
		while(Main.getConfig("pricing").contains("level"+i))
			i++;
		i--;
		return i;
	}
	
	public boolean canLevelUp() {
		return level < getMaxLevel();
	}
	
	public double getLevelUpCost() {
		return Main.getConfig("pricing").getDouble("level" + (level+1) + ".price");
	}
	
	public void openUpgradeMenu(Player player) {
		player.openInventory(inventory);
	}
	
	public void levelUp() {
		level++;
		material = Material.valueOf(Main.getConfig("pricing").getString("level" + level + ".item").toUpperCase());
		updateInventory();
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	public Location getDropLocation() {
		return dropLoc;
	}
}
