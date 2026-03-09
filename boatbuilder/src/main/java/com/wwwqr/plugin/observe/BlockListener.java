package com.wwwqr.plugin.observe;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import java.util.Random;

import com.wwwqr.plugin.database.DatabaseManager;

public class BlockListener implements Listener {
    private final Logger logger;
    private final JavaPlugin plugin;
    private Random random;

    public BlockListener(Logger logger, JavaPlugin plugin) {
        this.logger = logger;
        this.plugin = plugin;
        this.random = new Random();
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

    }

    public void blockUpdate(Player player, Block block, String eventType) {

    }
}