package org.lampserv.wallpaperbitcoinclicker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class WallpaperBitcoin extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new MyWallpaperEngine();
    }

    private static int cpSize = Math.min(Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
    // Converted Pixels
    public static float cp(float pixels) {
        return pixels * (cpSize / 600f) / 2;
    }

    public static float cp(int pixels) {
        return pixels * (cpSize / 600f) / 2;
    }

    public static double cp(double pixels) {
        return pixels * (cpSize / 600f) / 2;
    }

    public static int ScrW = 0, ScrH = 0;

    private class MyWallpaperEngine extends WallpaperService.Engine {
        private final Handler handler;
        private SurfaceHolder holder;
        private boolean visible = true;
        private Bitmap bitcoinBitmap_original;
        private Bitmap bitcoinBitmap_normal;
        private Bitmap bitcoinBitmap_clicked;
        private Bitmap bitcoinBitmap;
        private Random random = new Random();
        private long secondCoolDown = 0;

        private Vec2D btcMove = new Vec2D();

        public MyWallpaperEngine() {
            handler = new Handler();

            bitcoinBitmap_original = BitmapFactory.decodeResource(getResources(), R.drawable.icon2);
            bitcoinBitmap_normal = Bitmap.createScaledBitmap(bitcoinBitmap_original, (int) cp(650), (int) cp(650), false);
            bitcoinBitmap_clicked = Bitmap.createScaledBitmap(bitcoinBitmap_original, (int) cp(750), (int) cp(750), false);
            bitcoinBitmap = bitcoinBitmap_normal;
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);

            if (event.getX() > ScrW/2 - cp(325) && event.getX() < ScrW/2 + cp(325) && event.getY() > ScrH/2 - cp(325) && event.getY() < ScrH/2 + cp(325)) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bitcoinBitmap = bitcoinBitmap_clicked;
                        break;
                    case MotionEvent.ACTION_UP:
                        bitcoinBitmap = bitcoinBitmap_normal;

                        // Increasing BTC on click
                        Vars.a = Vars.a.add(Vars.c);

                        btcMove.x = random.nextInt(40) - 20;
                        btcMove.y = random.nextInt(40) - 20;

                        new PhysText(
                                Vars.texts[random.nextInt(Vars.texts.length)],
                                new Vec2D(ScrW/2 + cp(btcMove.x), ScrH * 0.175f + cp(btcMove.y)),
                                new Vec2D(0f, cp(1)),
                                new Vec2D(cp(random.nextInt(20) - 10), -20f),
                                0.01f,
                                Color.HSVToColor(new float[] {360f * random.nextFloat(), 1f, 1f})
                        );

                        break;
                }
            }
        }

        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
            }
        };

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.holder = surfaceHolder;

            if (Vars.sharedPref == null) {
                Vars.sharedPref = getSharedPreferences("bitcoinWallpaperPrefs", Context.MODE_PRIVATE);
                Vars.sharedPrefEditor = Vars.sharedPref.edit();
            }

            Vars.load(Vars.sharedPref);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            ScrW = width;
            ScrH = height;

            cpSize = Math.min(Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                handler.post(drawRunner);
            } else {
                handler.removeCallbacks(drawRunner);
            }
        }

        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    /* -- DRAW -- */
                    Paint p = new Paint();
                    p.setColor(Color.rgb(0, 0, 0));
                    canvas.drawPaint(p);

                    // BTC Circle
                    p.setColor(Color.WHITE);
                    canvas.drawBitmap(bitcoinBitmap, ScrW/2 - bitcoinBitmap.getWidth()/2, ScrH/2 - bitcoinBitmap.getHeight()/2, p);

                    // Texts
                    p.setTextSize(cp(40));
                    p.setTextAlign(Paint.Align.CENTER);
                    for (PhysText t : (ArrayList<PhysText>) PhysText.physTexts.clone()) {
                        t.update();
                        t.draw(canvas, p);
                    }

                    // BTC Amount
                    p.setTextSize(cp(80));
                    canvas.drawText(Vars.a.toPlainString() + " BTC", ScrW/2 + cp(btcMove.x), ScrH * 0.175f + cp(btcMove.y), p);

                    p.setTextSize(cp(50));
                    canvas.drawText("(+" + Vars.g.toPlainString() + ")", ScrW/2 + cp(btcMove.x), ScrH * 0.2f + cp(btcMove.y), p);
                    /* ---------- */

                    long time = System.currentTimeMillis();
                    if (time > secondCoolDown) {
                        secondCoolDown = time + 1000;

                        Vars.a = Vars.a.add(Vars.g);
                        Vars.save(Vars.sharedPref, Vars.sharedPrefEditor);
                    }
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
            handler.removeCallbacks(drawRunner);
            if (visible) {
                handler.postDelayed(drawRunner, 20);
            }
        }
    }
}
