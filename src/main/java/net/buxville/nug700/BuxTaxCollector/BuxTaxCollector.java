package net.buxville.nug700.BuxTaxCollector;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

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
	
	public void onEnable()
	{
		Instance = this;
		saveDefaultConfig();
		
		if (!setupEconomy()) {
			getLogger().severe("Could not connect to Economy. Shutting down.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		reloadValues();
		TaxAllPlayers();
	}
	
	public void onDisable()
	{
		
	}
	
	public void reloadValues()
	{
		GetTaxPercent();
		GetTaxAmount();
		GetTaxType();
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
		enableAutoTax = enabled;
		getConfig().set("enableautotax", enabled);
		SaveConfigValues();
		if (!enableAutoTax)
			CancelAutoTax();
		else
			ResetAutoTax();
	}
	
	public void SetAutoTaxDelay(int newDelay)
	{
		timeDelay = newDelay;
		getConfig().set("timedelay", newDelay);
		SaveConfigValues();
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
		double balance = econ.getBalance(player);
		if (taxType == TaxType.Amount)
			econ.withdrawPlayer(player, taxPercent);
		else
			econ.withdrawPlayer(player, (balance * taxPercent));
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
