package me.ScienceMan.generatorplugin;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class Commands implements Listener, CommandExecutor{
	public String[] commands = {"buygenerator"};
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase(commands[0])) {
			if(args.length == 0) {
				if(sender instanceof Player) {
					Player player = (Player)sender;
					Double price = Main.getConfigs().getConfig("generator-pricing").getDouble("level1.price");
					if(Main.getEconomy().has(player, price)) {
						Main.getEconomy().withdrawPlayer(player, price);
						ItemStack generator = new ItemStack(Material.FURNACE, 1);
						generator.setItemMeta(Generator.getMeta(1));
						player.getInventory().addItem(generator);
						player.sendMessage(ChatColor.GREEN + "Congratulation on purchasing a new Generator!");
					}
					else {
						player.sendMessage(ChatColor.RED + "Sorry, but you do not have sufficient funds to purchase a generator. It costs: " + price);
					}
				}
				else{
					sender.sendMessage(ChatColor.RED + "Sender must be a Player");
					return false;
				}
			}
			else{
				sender.sendMessage(ChatColor.RED + "The command " + commands[0] + " does not take parameters");
				return false;
			}
		}
		return true;
	}
}
