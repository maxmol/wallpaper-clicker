package org.lampserv.wallpaperbitcoinclicker;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    private ArrayList<TextView> panels_costs = new ArrayList<>();
    private ArrayList<Button> panels_btns = new ArrayList<>();

    public static MainActivity self;
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    private static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    private void btnStyle(Button b, Vars.Upgrade upgrade) {
        if (upgrade.canBuy()) {
            b.setText("BUY");
            b.setBackgroundColor(Color.rgb(64, 196, 96));
        }
        else {
            b.setText("Not Enough");
            b.setBackgroundColor(Color.rgb(196, 64, 96));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-8876273919420617~7490462110");
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        self = this;
        Button wallpaper_button = (Button) findViewById(R.id.wallpaper_button);
        wallpaper_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(self, WallpaperBitcoin.class));
                startActivity(intent);
            }
        });

        LinearLayout sv = (LinearLayout) findViewById(R.id.scrollview);

        if (Vars.sharedPref == null) {
            Vars.sharedPref = getSharedPreferences("bitcoinWallpaperPrefs", Context.MODE_PRIVATE);
            Vars.sharedPrefEditor = Vars.sharedPref.edit();
        }

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams p_block = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        p_block.setMargins(30, 30, 30, 30);


        for (final Vars.Upgrade upgrade : Vars.upgrades) {
            LinearLayout block = new LinearLayout(this);
            block.setOrientation(LinearLayout.VERTICAL);
            block.setId(generateViewId());
            block.setPadding(0, 8, 0, 0);
            block.setBackgroundColor(Color.rgb(64, 96, 200));

            sv.addView(block, p_block);

            TextView title = new TextView(this);
            title.setText(upgrade.name);
            title.setTextSize(24);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.WHITE);
            block.addView(title, p);

            final TextView cost = new TextView(this);
            cost.setText(upgrade.getPrice() + "BTC");
            cost.setTextSize(16);
            cost.setGravity(Gravity.CENTER);
            cost.setTextColor(Color.WHITE);
            block.addView(cost, p);

            panels_costs.add(cost);

            final Button b = new Button(this);
            b.setTextSize(20);
            b.setTextColor(Color.WHITE);
            btnStyle(b, upgrade);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!upgrade.canBuy()) {
                        Toast.makeText(getBaseContext(), "Not enough bitcoin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Vars.a = Vars.a.add(upgrade.getPrice().negate());

                    switch (upgrade.type) {
                        case 0:
                            Vars.c = Vars.c.multiply(upgrade.a);
                            break;

                        case 1:
                            Vars.g = Vars.g.add(upgrade.a);
                            break;
                    }

                    Toast.makeText(getBaseContext(), "Purchased!", Toast.LENGTH_SHORT).show();
                    updateAllPanels();

                    Vars.save(Vars.sharedPref, Vars.sharedPrefEditor);
                }
            });

            panels_btns.add(b);
            block.addView(b, p);
        }

        Button b = new Button(this);
        b.setText("Remove all progress");
        b.setBackgroundColor(Color.rgb(255, 0, 0));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(self)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Remove all progress")
                        .setMessage("Are you sure you want to reset all progress you made?")
                        .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Vars.a = new BigDecimal("0.00000000");
                                Vars.c = new BigDecimal("0.00000001");
                                Vars.g = new BigDecimal("0.00000000");

                                Toast.makeText(getBaseContext(), "All progress removed!", Toast.LENGTH_SHORT).show();

                                Vars.save(Vars.sharedPref, Vars.sharedPrefEditor);
                                updateAllPanels();
                            }

                        })
                        .setNegativeButton("Close", null)
                        .show();
            }
        });
        sv.addView(b, p);
    }

    private void updateAllPanels() {
        for (int i = 0; i < Vars.upgrades.length; i++) {
            panels_costs.get(i).setText(Vars.upgrades[i].getPrice() + "BTC");
            btnStyle(panels_btns.get(i), Vars.upgrades[i]);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateAllPanels();
    }
}