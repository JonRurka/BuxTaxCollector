package net.buxville.nug700.BuxTaxCollector;

import net.buxville.nug700.BuxTaxCollector.BuxTaxCollector.TaxType;
import net.buxville.nug700.BuxTaxCollector.BuxTaxCollector;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class BuxTaxCollectorCommandExecutor implements CommandExecutor {

	BuxTaxCollector plugin;

	public BuxTaxCollectorCommandExecutor(BuxTaxCollector instance) {
		plugin = instance;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("taxCollector"))
		{
			if (args.length == 0)
				return false;
			try {
			if (args[0].equalsIgnoreCase("collect"))
			{
				if (args.length == 1)
				{
					plugin.TaxAllPlayers();
				}
				else if (args.length == 2){
					String player = args[1];
					plugin.TaxPlayer(plugin.getServer().getPlayer(player));
				}
			}
			else if (args[0].equalsIgnoreCase("setAmount"))
				plugin.SetTaxAmount(Integer.parseInt(args[1]));
			else if (args[0].equalsIgnoreCase("setPercent"))
				plugin.SetTaxPercent(Integer.parseInt(args[1]));
			else if (args[0].equalsIgnoreCase("setType"))
				plugin.SetTaxType(TaxType.valueOf(args[1]));
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return false;
	}

}
