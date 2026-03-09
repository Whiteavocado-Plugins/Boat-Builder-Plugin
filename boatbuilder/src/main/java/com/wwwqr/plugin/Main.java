package com.wwwqr.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import com.wwwqr.plugin.command.BoatCommand;
import com.wwwqr.plugin.database.DatabaseManager;
import com.wwwqr.plugin.observe.BlockListener;
import com.wwwqr.plugin.observe.BlockListener;

public class Main extends JavaPlugin {
    private DatabaseManager dbManager;

    @Override
    public void onEnable() {
        //Database
        dbManager = new DatabaseManager(this);
        DatabaseManager.connect();
        //

        //Commands
        getCommand("boat").setExecutor(new BoatCommand(this, getLogger()));
        //

        //Listeners
        getServer().getPluginManager().registerEvents(new BlockListener(getLogger(), this), this);
        //
        
        getLogger().info("Plugin activated!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }
}
