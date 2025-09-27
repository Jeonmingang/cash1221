package com.minkang.ultimate.cashshop.util;

import com.minkang.ultimate.cashshop.Main;
import com.minkang.ultimate.cashshop.gui.CashShopGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Method;

public class CitizenHook {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void tryRegister(Main plugin) {
        try {
            // net.citizensnpcs.api.event.NPCRightClickEvent
            Class<?> eventClass = Class.forName("net.citizensnpcs.api.event.NPCRightClickEvent");
            Method getNPC = eventClass.getMethod("getNPC");
            Class<?> npcClass = Class.forName("net.citizensnpcs.api.npc.NPC");
            Method getId = npcClass.getMethod("getId");
            Method getClicker = eventClass.getMethod("getClicker");

            PluginManager pm = Bukkit.getPluginManager();
            pm.registerEvent((Class<? extends Event>) eventClass, new Listener() {}, EventPriority.NORMAL, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event event) {
                    try {
                        Object npc = getNPC.invoke(event);
                        int id = (Integer) getId.invoke(npc);
                        Object clickerObj = getClicker.invoke(event);
                        if (clickerObj instanceof Player p) {
                            if (plugin.getLinkStore().isLinked(id)) {
                                CashShopGUI.open(p, plugin);
                            }
                        }
                    } catch (Throwable ignore) {}
                }
            }, plugin);
            plugin.getLogger().info("Citizens hook registered.");
        } catch (Throwable t) {
            plugin.getLogger().info("Citizens not present/compatible; NPC hook disabled.");
        }
    }
}
