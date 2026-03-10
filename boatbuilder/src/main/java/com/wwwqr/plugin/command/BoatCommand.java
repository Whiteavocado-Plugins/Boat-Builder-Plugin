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
import java.util.List;
import java.util.ArrayList;
import org.bukkit.World;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

import com.wwwqr.plugin.command.BlockData;

import com.wwwqr.plugin.command.CardinalDirectionVector;

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
                toggleEditMode(player, false);
                return true;

            case "save":
                toggleEditMode(player, true);
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

    private void toggleEditMode(Player player, boolean save) {
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

                final boolean edits = editsABoat(player);
                char editingNum = '1';
                String execMsg = "Entered boat-edit mode successfully";

                if (edits && !save) {
                    Bukkit.getScheduler().runTask(plugin, () ->
                        Global.showMsg(player, "You're already in boat-edit mode! Use [/boat save] to exit edit mode")
                    );
                    return;
                }
                
                if (save) {
                    editingNum = '0';
                    execMsg = "Saved boat successfully";
                    if (!edits) {
                        Bukkit.getScheduler().runTask(plugin, () ->
                            Global.showMsg(player, "You already left boat-edit mode! Use [/boat edit] to edit your boat")
                        );
                        return;
                    }
                }

                final String execMsgRes = execMsg;

                String updateQuery = "UPDATE boat SET inEditMode = " + editingNum + " WHERE ownerUUID = ?";

                try {
                    PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(updateQuery);
                    stmt.setString(1, playerUUID.toString());

                    stmt.executeUpdate();

                    Bukkit.getScheduler().runTask(plugin, () ->
                        Global.showMsg(player, execMsgRes)
                    );
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        );
    }

    private CardinalDirectionVector getCardinalDirectionVectorMutation(Player player, int distance) {
        float yaw = player.getLocation().getYaw();
        yaw = (yaw % 360 + 360) % 360;//Normalize

        CardinalDirectionVector cdv = new CardinalDirectionVector();

        if (yaw >= 45 && yaw < 135) {
            cdv.setX(-distance);//West
            return cdv;
        }
        if (yaw >= 135 && yaw < 225) {
            cdv.setZ(-distance);//North
            return cdv;
        }
        if (yaw >= 225 && yaw < 315) {
            cdv.setX(distance);//East
            return cdv;
        }

        cdv.setZ(distance);//South
        return cdv;
    }

    private void removeBoatBlocks(Player player, boolean withDrop, boolean dbDel) {
        UUID playerUUID = player.getUniqueId();

        if (!ownsABoat(player)) {
            Bukkit.getScheduler().runTask(plugin, () ->
                Global.showMsg(player, "You don't own a boat yet! Use [/boat create]")
            );
            return;
        }

        String pullQuery = "SELECT x, y, z FROM boat_block WHERE boatID = (SELECT ID FROM boat WHERE ownerUUID = ? LIMIT 1)";
        String deleteQuery = "DELETE FROM boat_block WHERE boatID = (SELECT ID FROM boat WHERE ownerUUID = ? LIMIT 1)";
        
        try {
            PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(pullQuery);
            stmt.setString(1, playerUUID.toString());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");

                Bukkit.getScheduler().runTask(
                    plugin,
                    () -> {
                        try {
                            Block block = player.getWorld().getBlockAt(x, y, z);
                            if (withDrop) block.breakNaturally();
                            else block.setType(Material.AIR);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                );
            }

            if (dbDel) {
                stmt = DatabaseManager.getConn().prepareStatement(deleteQuery);
                stmt.setString(1, playerUUID.toString());
                stmt.executeUpdate();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveBoat(Player player) {
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

                removeBoatBlocks(player, false, false);

                String pullQuery = "SELECT ID, x, y, z, material FROM boat_block WHERE boatID = (SELECT ID FROM boat WHERE ownerUUID = ? LIMIT 1)";
                String updateQuery = "UPDATE boat_block SET x = ?, y = ?, z = ? WHERE ID = ?";
                CardinalDirectionVector cdv = getCardinalDirectionVectorMutation(player, 1);

                try {
                    PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(pullQuery);
                    PreparedStatement stmt2 = DatabaseManager.getConn().prepareStatement(updateQuery);

                    stmt.setString(1, playerUUID.toString());

                    ResultSet rs = stmt.executeQuery();

                    List<BlockData> blockList = new ArrayList<>();

                    while (rs.next()) {
                        String material = rs.getString("material");
                        int ID = rs.getInt("ID");

                        int x = rs.getInt("x") + cdv.getX();
                        int y = rs.getInt("y") + cdv.getY();
                        int z = rs.getInt("z") + cdv.getZ();

                        stmt2.setInt(1, x);
                        stmt2.setInt(2, y);
                        stmt2.setInt(3, z);
                        stmt2.setInt(4, ID);
                        stmt2.addBatch();

                        blockList.add(new BlockData(x, y, z, Material.valueOf(material)));
                    }

                    Bukkit.getScheduler().runTask(
                        plugin,
                        () -> {
                            World world = player.getWorld();
                            for (BlockData bd : blockList) {
                                world.getBlockAt(bd.x, bd.y, bd.z).setType(bd.material);
                            }
                        }
                    );

                    stmt2.executeBatch();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        );
    }

    private void mount(Player player) {
        moveBoat(player);//Testing
    }

    private void unmount(Player player) {

    }

    private void remove(Player player) {
        UUID playerUUID = player.getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(
            plugin,
            () -> {
                removeBoatBlocks(player, true, true);
            }
        );
    }
}
