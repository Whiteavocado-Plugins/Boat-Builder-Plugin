package com.wwwqr.plugin.command;

public class CardinalDirectionVector {
    private int x;
    private int y;
    private int z;

    public CardinalDirectionVector() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public CardinalDirectionVector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() { return this.x; }
    public int getY() { return this.y; }
    public int getZ() { return this.z; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setZ(int z) { this.z = z; }
}
