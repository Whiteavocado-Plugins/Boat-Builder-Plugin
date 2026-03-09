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

import com.wwwqr.plugin.Global;

import java.util.Random;

import com.wwwqr.plugin.database.DatabaseManager;

import com.wwwqr.plugin.command.BoatCommand;

public class BlockListener implements Listener {
    private final Logger logger;
    private final JavaPlugin plugin;
    private final Random random;
    private final BoatCommand bcmd;
    private final String triggerMsg;

    public BlockListener(Logger logger, JavaPlugin plugin) {
        this.logger = logger;
        this.plugin = plugin;
        this.random = new Random();
        this.bcmd = new BoatCommand(plugin, logger);
        this.triggerMsg = "[Boat edit mode]";
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        Bukkit.getScheduler().runTaskAsynchronously(
            plugin,
            () -> {
                if (!bcmd.ownsABoat(player) || !bcmd.editsABoat(player)) {
                    return;
                }

                if (!removeBoatBlock(player, block)) {
                    Bukkit.getScheduler().runTask(plugin, () ->
                        Global.showMsg(player, "Couldn't remove block from your boat!")
                    );
                    return;
                }

                Bukkit.getScheduler().runTask(plugin, () ->
                    Global.showMsg(player, triggerMsg)
                );
            }
        );
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        Bukkit.getScheduler().runTaskAsynchronously(
            plugin,
            () -> {
                if (!bcmd.ownsABoat(player) || !bcmd.editsABoat(player)) {
                    return;
                }

                if (!addBoatBlock(player, block)) {
                    Bukkit.getScheduler().runTask(plugin, () ->
                        Global.showMsg(player, "Couldn't add block to your boat!")
                    );
                    return;
                }

                Bukkit.getScheduler().runTask(plugin, () ->
                    Global.showMsg(player, triggerMsg)
                );
            }
        );
    }

    private boolean executeBoatStmt(Player player, Block block, String query, boolean withMaterial) {//Only use inside a async task!
        try {
            PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(query);

            stmt.setInt(1, (int)block.getLocation().getX());
            stmt.setInt(2, (int)block.getLocation().getY());
            stmt.setInt(3, (int)block.getLocation().getZ());
            stmt.setString(4, player.getUniqueId().toString());
            if (withMaterial) stmt.setString(5, block.getType().name());

            stmt.executeUpdate();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean addBoatBlock(Player player, Block block) {//Only use inside a async task!
        String query = "INSERT INTO boat_block(isCenter, x, y, z, boatID, material) VALUES (0, ?, ?, ?, (SELECT ID FROM boat WHERE ownerUUID = ? LIMIT 1), ?)";

        return executeBoatStmt(player, block, query, true);
    }

    private boolean removeBoatBlock(Player player, Block block) {//Only use inside a async task!
        String query = "DELETE FROM boat_block WHERE x = ? AND y = ? AND z = ? AND boatID = (SELECT ID FROM boat WHERE ownerUUID = ? LIMIT 1)";

        return executeBoatStmt(player, block, query, false);
    }
}