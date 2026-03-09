package com.wwwqr.plugin.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.Location;

import com.wwwqr.plugin.database.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

import org.bukkit.plugin.java.JavaPlugin;

import com.wwwqr.plugin.Global;

public class BoatCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final Logger logger;

    public BoatCommand(JavaPlugin plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length != 1) {
            return false;
        }

        String arg = args[0];

        switch (arg) {
            case "create":
                return create(player);

            case "edit":
                return edit(player);

            case "save":
                return save(player);

            case "mount":
                return mount(player);

            case "unmount":
                return unmount(player);
                
            case "remove":
                return remove(player);
        
            default:
                return false;
        }
    }

    private boolean ownsABoat(Player player) {
        UUID playerUUID = player.getUniqueId();
        boolean status = false;

        Bukkit.getScheduler().runTaskAsynchronously(
            plugin,
            () -> {
                String checkQuery = "SELECT COUNT(ID) FROM boat WHERE ownerUUID = ?";

                try {
                    PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(checkQuery);
                    stmt.setString(1, playerUUID.toString());

                    ResultSet rs = stmt.executeQuery();
                    rs.next();
                    int count = rs.getInt(1);

                    if (count != 0) {
                        status = true;
                        return;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        );

        return status;
    }

    private boolean create(Player player) {
        UUID playerUUID = player.getUniqueId();
        Location playerLoc = player.getLocation();

        if (ownsABoat(player)) {
            Global.showMsg(player, "You already own a boat!");
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(
            plugin,
            () -> {
                String insertQuery = "INSERT INTO boat(ownerUUID, x, y, z) VALUES (?, ?, ?, ?)";
                try {
                    PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(insertQuery);

                    stmt.setString(1, playerUUID.toString());
                    stmt.setInt(2, (int)playerLoc.getX());
                    stmt.setInt(3, (int)playerLoc.getY());
                    stmt.setInt(4, (int)playerLoc.getZ());

                    stmt.executeUpdate();

                    Bukkit.getScheduler().runTask(
                        plugin,
                        () -> {
                            Global.showMsg(player, "You've created a boat! Use /boat edit to build it");
                        }
                    );

                    return;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        );

        return true;
    }

    private boolean edit(Player player) {
        UUID playerUUID = player.getUniqueId();

        if (!ownsABoat(player)) {
            Global.showMsg(player, "You don't have a boat yet! (/boat create)");
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(
            plugin,
            () -> {
                String checkQuery = "SELECT inEditMode FROM boat WHERE ownerUUID = ?";
                String updateQuery = "UPDATE boat SET inEditMode = 1 WHERE ownerUUID = ?";

                try {
                    PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(checkQuery);

                    stmt.setString(1, playerUUID.toString());

                    ResultSet rs = stmt.executeQuery();
                    rs.next();
                    int count = rs.getInt(1);

                    if (count != 0) {
                        Bukkit.getScheduler().runTask(
                            plugin,
                            () -> {
                                Global.showMsg(player, "You're already in edit mode!");
                                return;
                            }
                        );

                        return;
                    }

                    stmt = DatabaseManager.getConn().prepareStatement(updateQuery);
                    stmt.setString(1, playerUUID.toString());

                    
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        );

        return true;
    }

    private boolean save(Player player) {
        return true;
    }

    private boolean mount(Player player) {
        return true;
    }

    private boolean unmount(Player player) {
        return true;
    }

    private boolean remove(Player player) {
        return true;
    }
}
