package org.lampserv.wallpaperbitcoinclicker;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

public class PhysText {
    public static ArrayList<PhysText> physTexts = new ArrayList<>();

    private Vec2D pos, grav, vel;
    private String text;
    private float friction;
    private int color;

    public PhysText(String text, Vec2D pos, Vec2D grav, Vec2D vel, float friction, int color) {
        this.text = text;
        this.pos = pos;
        this.grav = grav;
        this.vel = vel;
        this.friction = friction;
        this.color = color;

        physTexts.add(this);
    }

    public void update() {
        vel.add(grav);
        vel.multiply(1f - friction);
        pos.add(vel);

        if (pos.x > WallpaperBitcoin.ScrW || pos.x < 0 || pos.y > WallpaperBitcoin.ScrH || pos.y < 0) {
            physTexts.remove(this);
        }
    }

    public void draw(Canvas canvas, Paint p) {
        p.setColor(color);
        canvas.drawText(text, pos.x, pos.y, p);
    }
}
