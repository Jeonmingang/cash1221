package com.minkang.ultimate.cashshop.gui;

import com.minkang.ultimate.cashshop.Main;
import com.minkang.ultimate.cashshop.store.ShopStore;
import com.minkang.ultimate.cashshop.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CashShopGUI {

    public static String title(Main plugin) {
        return ItemUtil.color(plugin.getConfig().getString("gui.title", "&8후원 상점"));
    }

    public static int size(Main plugin) {
        int s = plugin.getConfig().getInt("gui.size", 54);
        if (s < 9) s = 9;
        if (s % 9 != 0) s = 54;
        if (s > 54) s = 54;
        return s;
    }

    public static List<String> overlay(Main plugin) {
        List<String> raw = plugin.getConfig().getStringList("lore.overlay");
        if (raw == null || raw.isEmpty()) {
            raw = new ArrayList<>();
            raw.add("&7-------------------------");
            raw.add("&a가격: &f{price}&7 캐시");
            raw.add("&a수량: &f{amount}&7 개");
            raw.add("&e클릭하여 구매");
            raw.add("&7-------------------------");
        }
        return raw;
    }

    public static void open(Player p, Main plugin) {
        Inventory inv = Bukkit.createInventory(null, size(plugin), title(plugin));
        for (Map.Entry<Integer, ShopStore.Listing> e : plugin.getShopStore().getListings().entrySet()) {
            int slot = e.getKey();
            ShopStore.Listing listing = e.getValue();
            Map<String, String> ph = new HashMap<>();
            ph.put("price", ItemUtil.fmt(listing.price()));
            ph.put("amount", String.valueOf(listing.amount()));
            List<String> lore = ItemUtil.formatLore(overlay(plugin), ph);
            ItemStack show = ItemUtil.withOverlay(listing.item(), lore);
            if (slot >= 0 && slot < inv.getSize()) {
                inv.setItem(slot, show);
            }
        }
        p.openInventory(inv);
        p.sendMessage(plugin.msg("shop_opened"));
    }
}
