package org.lampserv.wallpaperbitcoinclicker;

// My own Point class. With some useful additions.
public class Vec2D {
    public float x, y;

    public Vec2D() {
        x = 0;
        y = 0;
    }

    public Vec2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void add(Vec2D v) {
        x += v.x;
        y += v.y;
    }

    public void sub(Vec2D v) {
        x -= v.x;
        y -= v.y;
    }

    public void multiply(Vec2D v) {
        x *= v.x;
        y *= v.y;
    }

    public void multiply(float m) {
        x *= m;
        y *= m;
    }

    public Vec2D plus(Vec2D v) {
        return new Vec2D(x + v.x, y + v.y);
    }

    public Vec2D minus(Vec2D v) {
        return new Vec2D(x - v.x, y - v.y);
    }

    public Vec2D mul(Vec2D v) {
        return new Vec2D(x * v.x, y * v.y);
    }

    public Vec2D mul(float n) {
        return new Vec2D(x * n, y * n);
    }

    public Vec2D copy() {
        return new Vec2D(x, y);
    }

    public double Distance(Vec2D v) {
        return Math.abs(this.x - v.x) + Math.abs(this.y - v.y);
    }

    public double Length() {
        return Math.sqrt(x * x + y * y);
    }

    @Override
    public String toString() {
        return x + " " + y;
    }
}
