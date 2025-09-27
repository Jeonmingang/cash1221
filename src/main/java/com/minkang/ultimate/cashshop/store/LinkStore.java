package com.minkang.ultimate.cashshop.store;

import com.minkang.ultimate.cashshop.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class LinkStore {
    private final Main plugin;
    private final File file;
    private FileConfiguration conf;
    private final Set<Integer> linked = new HashSet<>();

    public LinkStore(Main plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "links.yml");
        load();
    }

    public synchronized boolean isLinked(int npcId) {
        return linked.contains(npcId);
    }

    public synchronized boolean link(int npcId) {
        boolean added = linked.add(npcId);
        save();
        return added;
    }

    public synchronized boolean unlink(int npcId) {
        boolean removed = linked.remove(npcId);
        save();
        return removed;
    }

    private void load() {
        if (!file.exists()) {
            try { file.getParentFile().mkdirs(); file.createNewFile(); } catch (IOException ignored) {}
        }
        conf = YamlConfiguration.loadConfiguration(file);
        linked.clear();
        for (String k : conf.getStringList("linked")) {
            try { linked.add(Integer.parseInt(k)); } catch (NumberFormatException ignored) {}
        }
    }

    public synchronized void save() {
        YamlConfiguration out = new YamlConfiguration();
        out.set("linked", linked.stream().map(String::valueOf).toList());
        try { out.save(file); } catch (IOException ignored) {}
    }
}
