package net.buxville.nug700.BuxTaxCollector;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class BuxTaxCollector extends JavaPlugin {
	public enum TaxType{
		Amount ("Amount"),
		Percent ("Percent");
		
		private final String typeStr;
		TaxType(String type)
		{
			this.typeStr = type;
		}
	}
	public static BuxTaxCollector Instance;
	Connection con;
	Economy econ;
	int taxPercent;
	int taxAmount;
	TaxType taxType;
	boolean enableAutoTax;
	int timeDelay;
	int scheduleID;
	String username;
	String password;
	String url;
	
	public void onEnable()
	{
		Instance = this;
		saveDefaultConfig();
		
		if (!setupEconomy()) {
			getLogger().severe("Could not connect to Economy. Shutting down.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		username = getConfig().getString("username");
		password = getConfig().getString("password");
		url = getConfig().getString("url");
		
		reloadValues();
		
		if (enableAutoTax)
			InitAutoTax(timeDelay);
		
		this.getCommand("taxCollector").setExecutor(new BuxTaxCollectorCommandExecutor(this));
	}
	
	public void onDisable()
	{
		
	}
	
	public void reloadValues()
	{
		GetTaxPercent();
		GetTaxAmount();
		GetTaxType();
		GetAutoTaxDelay();
		GetAutoTaxEnabled();
	}
	
	public void SetTaxPercent(int newPercent)
	{
		taxPercent = newPercent;
		getConfig().set("taxpercent", newPercent);
		SaveConfigValues();
	}
	
	public void SetTaxAmount(int newAmount)
	{
		taxAmount = newAmount;
		getConfig().set("taxeamount", newAmount);
		SaveConfigValues();
	}
	
	public void SetAutoTaxEnabled(boolean enabled)
	{
		SetAutoTaxEnabled(enabled, true);
	}
	
	public void SetAutoTaxEnabled(boolean enabled, boolean run)
	{
		enableAutoTax = enabled;
		getConfig().set("enableautotax", enabled);
		SaveConfigValues();
		if (enableAutoTax)
			if (run) ResetAutoTax();
		else
			CancelAutoTax();
	}
	
	public void SetAutoTaxDelay(int newDelay)
	{
		SetAutoTaxDelay(newDelay, true);
	}
	
	public void SetAutoTaxDelay(int newDelay, boolean run){
		timeDelay = newDelay;
		getConfig().set("timedelay", newDelay);
		SaveConfigValues();
		if (run)
			ResetAutoTax();
	}
	
	public void SetTaxType(TaxType newType)
	{
		taxType = newType;
		getConfig().set("taxetype", newType);
		SaveConfigValues();
	}
	
 	public int GetTaxPercent()
	{
		taxPercent = getConfig().getInt("taxpercent");
		return taxPercent;
	}
 	
	public TaxType GetTaxType()
	{
		taxType = TaxType.valueOf(getConfig().getString("taxetype"));
		return taxType;
	}

	public int GetTaxAmount()
	{
		taxAmount = getConfig().getInt("taxeamount");
		return taxAmount;
	}
	
	public int GetAutoTaxDelay()
	{
		timeDelay = getConfig().getInt("timedelay");
		return timeDelay;
	}
	
	public boolean GetAutoTaxEnabled()
	{
		enableAutoTax = getConfig().getBoolean("enableautotax");
		return enableAutoTax;
	}
	
 	public void SaveConfigValues()
 	{
		try {
			getConfig().save(getDataFolder() + File.separator + "config.yml");
		} catch (IOException e) {
			e.printStackTrace();
		}
 	}
	
	public void TaxAllPlayers()
	{
		// TODO: get players from database.
	}
	
	public void TaxPlayer(Player player)
	{
		if (player.hasPermission("BuxTaxCollector.taxee"))
		{
			double balance = econ.getBalance(player);
			if (taxType == TaxType.Amount)
				econ.withdrawPlayer(player, taxPercent);
			else
				econ.withdrawPlayer(player, (balance * taxPercent));
		}
	}

	public void ResetAutoTax()
	{
		CancelAutoTax();
		InitAutoTax(timeDelay);
	}
	
	public void CancelAutoTax()
	{
		getServer().getScheduler().cancelTask(scheduleID);
	}
	
	public void InitAutoTax(int seconds)
	{
		int delay = 20;
		int period = seconds * 20;
		scheduleID = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				TaxAllPlayers();
			}} , delay, period);
	}
	
	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer()
				.getServicesManager().getRegistration(
						net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			econ = economyProvider.getProvider();
		}

		return (econ != null);
	}
}
