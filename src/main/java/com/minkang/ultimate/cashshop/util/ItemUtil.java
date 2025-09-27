package com.minkang.ultimate.cashshop.util;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ItemUtil {
    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String fmt(long n) {
        return NumberFormat.getInstance(Locale.KOREA).format(n);
    }

    public static List<String> formatLore(List<String> raw, Map<String, String> ph) {
        List<String> out = new ArrayList<>();
        for (String line : raw) {
            String r = line;
            if (ph != null) {
                for (Map.Entry<String, String> e : ph.entrySet()) {
                    r = r.replace("{" + e.getKey() + "}", e.getValue());
                }
            }
            out.add(color(r));
        }
        return out;
    }

    public static ItemStack withOverlay(ItemStack base, List<String> lore) {
        ItemStack item = base.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
