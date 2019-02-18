package org.lampserv.wallpaperbitcoinclicker;

import android.content.SharedPreferences;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Vars {
    public static class Upgrade {
        public String name;
        public int type;
        public BigDecimal price;
        public BigDecimal a;

        public BigDecimal getPrice() {
            BigDecimal price = this.price;
            if (type == 0) {
                price = price.multiply(Vars.c);
            }

            return price;
        }

        public boolean canBuy() {
            BigDecimal price = getPrice();
            return Vars.a.compareTo(price) >= 0;
        }

        public Upgrade(String s, BigDecimal d, BigDecimal bd, int t) {
            name = s;
            price = d;
            a = bd;
            type = t;
        }
    }

    // BTC Wallet
    public static BigDecimal a = new BigDecimal("0.00000000");

    // Amount to add on click
    public static BigDecimal c = new BigDecimal("0.00000001");

    // Amount to generate every second
    public static BigDecimal g = new BigDecimal("0.00000000");

    public static void save(SharedPreferences sharedPref, SharedPreferences.Editor editor) {
        a = a.stripTrailingZeros();
        c = c.stripTrailingZeros();
        g = g.stripTrailingZeros();
        editor.putString("wallpaperbitcoin_a", a.toPlainString());
        editor.putString("wallpaperbitcoin_c", c.toPlainString());
        editor.putString("wallpaperbitcoin_g", g.toPlainString());

        editor.commit();
    }

    public static void load(SharedPreferences sharedPref) {
        a = new BigDecimal(sharedPref.getString("wallpaperbitcoin_a","0.00000000"));
        c = new BigDecimal(sharedPref.getString("wallpaperbitcoin_c","0.00000001"));
        g = new BigDecimal(sharedPref.getString("wallpaperbitcoin_g","0.00000000"));
    }

    public static Upgrade[] upgrades = new Upgrade[] {
            new Upgrade("Cloud Shares (Increases Click Revenue)", new BigDecimal("1000"), new BigDecimal(2), 0),
            new Upgrade("CPU (1Ghz, 1 Core)", new BigDecimal("0.00000500"), new BigDecimal("0.00000001"), 1),
            new Upgrade("CPU (2Ghz, 1 Core)", new BigDecimal("0.00002000"), new BigDecimal("0.00000005"), 1),
            new Upgrade("CPU (3Ghz, 1 Core)", new BigDecimal("0.00005000"), new BigDecimal("0.00000015"), 1),
            new Upgrade("CPU (3Ghz, 2 Core)", new BigDecimal("0.00010000"), new BigDecimal("0.00000035"), 1),
            new Upgrade("CPU (4Ghz, 2 Core)", new BigDecimal("0.00015000"), new BigDecimal("0.00000055"), 1),
            new Upgrade("CPU (4Ghz, 4 Core)", new BigDecimal("0.00025000"), new BigDecimal("0.00000110"), 1),
            new Upgrade("CPU (4.5Ghz, 4 Core)", new BigDecimal("0.00030000"), new BigDecimal("0.00000140"), 1),
            new Upgrade("CPU (4.5Ghz, 8 Core)", new BigDecimal("0.00040000"), new BigDecimal("0.00000200"), 1),
            new Upgrade("GTX 1050", new BigDecimal("0.00060000"), new BigDecimal("0.00000400"), 1),
            new Upgrade("GTX 1060", new BigDecimal("0.00080000"), new BigDecimal("0.00000600"), 1),
            new Upgrade("GTX 1070", new BigDecimal("0.00100000"), new BigDecimal("0.00000800"), 1),
            new Upgrade("GTX 1080", new BigDecimal("0.00120000"), new BigDecimal("0.00001000"), 1),
            new Upgrade("ASIC Miner", new BigDecimal("0.00150000"), new BigDecimal("0.00001500"), 1),
    };

    public static String[] texts = new String[] {
            "wow",
            "much bitcoin",
            "so many",
            "click",
            "pls more",
            "rich",
            "crypto currency",
            "more",
            "+++",
            "mining",
            "bitcoin +999%",
            "cloud mining",
            "asic",
            "gpu",
            "99999MHs",
            "cpu",
            "farm",
            "satoshi",
            "buy car",
            "buy house",
            "yachts",
            "charts",
            "bits",
            "bots",
            "hashes",
            "keys",
            "blocks"
    };

    public static SharedPreferences sharedPref;
    public static SharedPreferences.Editor sharedPrefEditor;
}
