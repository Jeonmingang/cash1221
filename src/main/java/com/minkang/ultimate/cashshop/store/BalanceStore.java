package com.minkang.ultimate.cashshop.store;

import com.minkang.ultimate.cashshop.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BalanceStore {
    private final Main plugin;
    private final File file;
    private FileConfiguration conf;
    private final Map<UUID, Long> balances = new HashMap<>();

    public BalanceStore(Main plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "balances.yml");
        load();
    }

    public synchronized long get(UUID id) {
        return balances.getOrDefault(id, 0L);
    }

    public synchronized void set(UUID id, long value) {
        if (value < 0) value = 0;
        balances.put(id, value);
        save();
    }

    public synchronized void add(UUID id, long delta) {
        if (delta <= 0) return;
        long cur = balances.getOrDefault(id, 0L);
        long next = cur + delta;
        if (next < 0) next = Long.MAX_VALUE; // overflow guard
        balances.put(id, next);
        save();
    }

    public synchronized boolean subtractIfEnough(UUID id, long delta) {
        long cur = balances.getOrDefault(id, 0L);
        if (delta <= 0 || cur < delta) return false;
        balances.put(id, cur - delta);
        save();
        return true;
    }

    public synchronized List<Map.Entry<UUID, Long>> topN(int n) {
        return balances.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(n)
                .collect(Collectors.toList());
    }

    private void load() {
        if (!file.exists()) {
            try { file.getParentFile().mkdirs(); file.createNewFile(); } catch (IOException ignored) {}
        }
        conf = YamlConfiguration.loadConfiguration(file);
        for (String k : conf.getKeys(false)) {
            try {
                UUID id = UUID.fromString(k);
                long v = conf.getLong(k, 0L);
                balances.put(id, v);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public synchronized void save() {
        YamlConfiguration out = new YamlConfiguration();
        for (Map.Entry<UUID, Long> e : balances.entrySet()) {
            out.set(e.getKey().toString(), e.getValue());
        }
        try { out.save(file); } catch (IOException ignored) {}
    }
}
