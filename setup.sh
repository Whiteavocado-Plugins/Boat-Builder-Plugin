#!/bin/bash

#
# By wwwqr-000, v1.2
# Source of this script: https://hacker.onthewifi.com/api/gist/mc-plugin
# Random Hash: 9f2051ecd227b143ee91b74227fffccd984e4c09f503c890caab0feeb1ee338e
#

sudo apt-get update
sudo apt-get install maven
read -p "Enter project name (All lower-case!) > " project
read -p "Enter author name (All lower-case!) > " author

mvn archetype:generate -DgroupId=com.$author.plugin -DartifactId=$project -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
sudo apt install openjdk-21-jdk
echo "---> SELECT JAVA 21 OPTION FOR THIS SETUP! <---"
update-alternatives --config java
echo sudo mvn clean package > $project/build.sh
chmod +x $project/build.sh

#Setting up the pom file for papermc
rm $project/pom.xml

cat > $project/pom.xml << EOF
<project xmlns="http://maven.apache.org/POM/4.0.0" 
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.$author.plugin</groupId>
    <artifactId>$project</artifactId>
    <version>1.0</version>
    <packaging>jar</packaging>

    <name>$project</name>
    <url>http://maven.apache.org</url>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>io.papermc.paper</groupId>
            <artifactId>paper-api</artifactId>
            <version>1.21.4-R0.1-SNAPSHOT</version> <!-- https://artifactory.papermc.io/ui/native/universe/io/papermc/paper/paper-api/ -->
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.2</version>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>8.0.33</version>
        </dependency>

    </dependencies>

    <repositories>
        <repository>
            <id>papermc</id>
            <url>https://repo.papermc.io/repository/maven-public/</url>
        </repository>
    </repositories>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>com.$author.plugin.Main</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
EOF
#

#Setting up the Main.java file
rm $project/src/main/java/com/$author/plugin/App.java

cat > $project/src/main/java/com/$author/plugin/Main.java << EOF
package com.$author.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import com.$author.plugin.database.DatabaseManager;
import com.$author.plugin.observe.BlockBreakListener;

public class Main extends JavaPlugin {
    private DatabaseManager dbManager;

    @Override
    public void onEnable() {

        dbManager = new DatabaseManager(getLogger());
        dbManager.connect();

        getLogger().info("Your Plugin has been enabled!");

        // The line below is a database example
        //getServer().getPluginManager().registerEvents(new BlockBreakListener(getLogger(), dbManager, this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Your Plugin has been disabled!");
    }
}
EOF
#

#Setting up the AppTest.java file
rm $project/src/test/java/com/$author/plugin/AppTest.java

cat > $project/src/test/java/com/$author/plugin/AppTest.java << EOF
package com.$author.plugin;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

//Unit tests
public class AppTest {

    @Test
    public void testApp() {
        assertTrue(true);
    }
}
EOF
#

#Setting up the .gitignore if you were to store this project on git/ github
echo target/* > $project/.gitignore
#

#Setting up the plugin.yml file
mkdir $project/src/main/resources

cat > $project/src/main/resources/plugin.yml << EOF
name: $project
version: 1.0
main: com.$author.plugin.Main
api-version: 1.21
description: Example description
authors:
    -   $author
EOF
#

#Setting up the DatabaseManager.java file
mkdir $project/src/main/java/com/$author/plugin/database

cat > $project/src/main/java/com/$author/plugin/database/DatabaseManager.java << EOF
package com.$author.plugin.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseManager {
    private final Logger logger;
    private final String url;
    private final String user;
    private final String password;

    public DatabaseManager(Logger logger) {
        this.logger = logger;
        this.url = "jdbc:mysql://localhost:3306/DATABASE_NAME_HERE";
        this.user = "DATABASE_USER_NAME_HERE";
        this.password = "DATABASE_USER_PW_HERE";
    }

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.info("Database driver found");
        }
        catch (ClassNotFoundException e) {
            logger.severe("Mysql driver not found!");
            e.printStackTrace();
        }
    }

    public void logBlockBreak(String playerName, String blockName) {
        String sql = "INSERT INTO log(nickname, blockname) VALUES (?, ?)";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, playerName);
            stmt.setString(2, blockName);

            stmt.executeUpdate();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
EOF
#

#Setting up the BlockBreakListener.java file
mkdir $project/src/main/java/com/$author/plugin/observe

cat > $project/src/main/java/com/$author/plugin/observe/BlockBreakListener.java << EOF
package com.$author.plugin.observe;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

import com.$author.plugin.database.DatabaseManager;

import com.$author.plugin.Main;

public class BlockBreakListener implements Listener {
    private final Logger logger;
    private final DatabaseManager dbManager;
    private final Main plugin;

    public BlockBreakListener(Logger logger, DatabaseManager dbManager, Main plugin) {
        this.logger = logger;
        this.dbManager = dbManager;
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        String playerName = event.getPlayer().getName();
        String blockName = event.getBlock().getType().name();

        Bukkit.getScheduler().runTaskAsynchronously(
            plugin,
            () -> dbManager.logBlockBreak(playerName, blockName)
        );

        logger.info(playerName + " broke " + blockName);
    }
}
EOF
#