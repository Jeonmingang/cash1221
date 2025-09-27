package com.minkang.ultimate.cashshop.listeners;

import com.minkang.ultimate.cashshop.Main;
import com.minkang.ultimate.cashshop.gui.CashShopGUI;
import com.minkang.ultimate.cashshop.store.ShopStore;
import com.minkang.ultimate.cashshop.util.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class GuiListener implements Listener {

    private final Main plugin;

    public GuiListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        InventoryView view = e.getView();
        String title = view.getTitle();
        if (!title.equals(CashShopGUI.title(plugin))) return;

        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();

        // Only top inventory
        if (e.getRawSlot() < 0 || e.getRawSlot() >= view.getTopInventory().getSize()) return;

        int slot = e.getRawSlot();
        ShopStore.Listing listing = plugin.getShopStore().getListing(slot).orElse(null);
        if (listing == null) return;

        long bal = plugin.getBalanceStore().get(p.getUniqueId());
        long price = listing.price();
        if (bal < price) {
            p.sendMessage(plugin.msg("buy_not_enough", Map.of(
                    "price", ItemUtil.fmt(price),
                    "balance", ItemUtil.fmt(bal)
            )));
            return;
        }

        // subtract first
        plugin.getBalanceStore().subtractIfEnough(p.getUniqueId(), price);

        // give item
        ItemStack toGive = listing.item().clone();
        toGive.setAmount(listing.amount());
        Map<Integer, ItemStack> leftover = p.getInventory().addItem(toGive);
        if (!leftover.isEmpty()) {
            for (ItemStack rest : leftover.values()) {
                if (rest == null) continue;
                p.getWorld().dropItemNaturally(p.getLocation(), rest);
            }
        }
        long nb = plugin.getBalanceStore().get(p.getUniqueId());
        p.sendMessage(plugin.msg("bought", Map.of(
                "amount", String.valueOf(listing.amount()),
                "price", ItemUtil.fmt(price),
                "balance", ItemUtil.fmt(nb)
        )));
    }
}
