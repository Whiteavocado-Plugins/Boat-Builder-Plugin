package com.wwwqr.plugin.command;

import org.bukkit.Material;

public class BlockData {
    int x, y, z;
    Material material;

    BlockData(int x, int y, int z, Material material) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.material = material;
    }
}
