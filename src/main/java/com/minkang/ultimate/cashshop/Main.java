package com.minkang.ultimate.cashshop;

import com.minkang.ultimate.cashshop.commands.CashCommand;
import com.minkang.ultimate.cashshop.commands.ShopCommand;
import com.minkang.ultimate.cashshop.listeners.GuiListener;
import com.minkang.ultimate.cashshop.store.BalanceStore;
import com.minkang.ultimate.cashshop.store.LinkStore;
import com.minkang.ultimate.cashshop.store.ShopStore;
import com.minkang.ultimate.cashshop.util.CitizenHook;
import com.minkang.ultimate.cashshop.util.ItemUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class Main extends JavaPlugin {

    private static Main instance;
    private BalanceStore balanceStore;
    private ShopStore shopStore;
    private LinkStore linkStore;

    public static Main getInstance() {
        return instance;
    }

    public BalanceStore getBalanceStore() {
        return balanceStore;
    }

    public ShopStore getShopStore() {
        return shopStore;
    }

    public LinkStore getLinkStore() {
        return linkStore;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.balanceStore = new BalanceStore(this);
        this.shopStore = new ShopStore(this);
        this.linkStore = new LinkStore(this);

        // Commands
        if (getCommand("캐시") != null) {
            getCommand("캐시").setExecutor(new CashCommand(this));
        }
        if (getCommand("캐시상점") != null) {
            getCommand("캐시상점").setExecutor(new ShopCommand(this));
        }

        // Listeners
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);

        // Optional Citizens hook
        CitizenHook.tryRegister(this);

        getLogger().info(getDescription().getName() + " v" + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        if (balanceStore != null) balanceStore.save();
        if (shopStore != null) shopStore.save();
        if (linkStore != null) linkStore.save();
        getLogger().info(getDescription().getName() + " disabled.");
    }

    public String msg(String key) {
        return ItemUtil.color(getConfig().getString("messages." + key, key));
    }

    public String msg(String key, Map<String, String> placeholders) {
        String s = getConfig().getString("messages." + key, key);
        if (placeholders != null) {
            for (Map.Entry<String, String> e : placeholders.entrySet()) {
                s = s.replace("{" + e.getKey() + "}", e.getValue());
            }
        }
        String prefix = getConfig().getString("messages.prefix", "");
        return ItemUtil.color(prefix + s);
    }
}
