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
                create(player);
                return true;

            case "edit":
                edit(player);
                return true;

            case "save":
                save(player);
                return true;

            case "mount":
                mount(player);
                return true;

            case "unmount":
                unmount(player);
                return true;
                
            case "remove":
                remove(player);
                return true;
        
            default:
                return false;
        }
    }

    public boolean ownsABoat(Player player) {//Only use inside a async task!
        UUID playerUUID = player.getUniqueId();
        boolean status = false;
        String checkQuery = "SELECT COUNT(ID) FROM boat WHERE ownerUUID = ?";

        try {
            PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(checkQuery);
            stmt.setString(1, playerUUID.toString());

            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            status = count != 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }

    public boolean editsABoat(Player player) {//Only use inside a async task!
        UUID playerUUID = player.getUniqueId();
        boolean status = false;
        String query = "SELECT inEditMode FROM boat WHERE ownerUUID = ?";

        try {
            PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(query);
            stmt.setString(1, playerUUID.toString());

            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return status;
            }

            return rs.getInt("inEditMode") == 1;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }

    private void create(Player player) {
        UUID playerUUID = player.getUniqueId();
        Location playerLoc = player.getLocation();

        Bukkit.getScheduler().runTaskAsynchronously(
            plugin,
            () -> {
                if (ownsABoat(player)) {
                    Bukkit.getScheduler().runTask(plugin, () ->
                        Global.showMsg(player, "You already own a boat!")
                    );
                    return;
                }

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
                            Global.showMsg(player, "You've created a boat! Use [/boat edit] to build it");
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
    }

    private void edit(Player player) {
        UUID playerUUID = player.getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(
            plugin,
            () -> {
                if (!ownsABoat(player)) {
                    Bukkit.getScheduler().runTask(plugin, () ->
                        Global.showMsg(player, "You don't own a boat yet! Use [/boat create]")
                    );
                    return;
                }

                if (editsABoat(player)) {
                    Bukkit.getScheduler().runTask(plugin, () ->
                        Global.showMsg(player, "You're already in boat-edit mode! Use [/boat save] to exit edit mode")
                    );
                    return;
                }

                String updateQuery = "UPDATE boat SET inEditMode = 1 WHERE ownerUUID = ?";

                try {
                    PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(updateQuery);
                    stmt.setString(1, playerUUID.toString());

                    stmt.executeUpdate();

                    Bukkit.getScheduler().runTask(plugin, () ->
                        Global.showMsg(player, "Entered boat-edit mode successfully")
                    );
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        );
    }

    private void save(Player player) {

    }

    private void mount(Player player) {

    }

    private void unmount(Player player) {

    }

    private void remove(Player player) {

    }
}
