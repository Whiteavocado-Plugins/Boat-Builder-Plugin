package com.wwwqr.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.entity.Player;

public class Global {
    public static void showMsg(Player player, String msg) {
        Component pTxt = Component.text(msg).color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD);
        player.sendActionBar(pTxt);
    }
}
