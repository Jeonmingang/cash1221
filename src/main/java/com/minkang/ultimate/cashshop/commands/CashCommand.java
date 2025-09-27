package com.minkang.ultimate.cashshop.commands;

import com.minkang.ultimate.cashshop.Main;
import com.minkang.ultimate.cashshop.store.BalanceStore;
import com.minkang.ultimate.cashshop.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CashCommand implements CommandExecutor {
    private final Main plugin;

    public CashCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        BalanceStore store = plugin.getBalanceStore();

        if (args.length == 0) {
            if (sender instanceof Player p) {
                long bal = store.get(p.getUniqueId());
                sender.sendMessage(plugin.msg("balance", Map.of("balance", ItemUtil.fmt(bal))));
            } else {
                sender.sendMessage(plugin.msg("only_player"));
            }
            return true;
        }

        // /캐시 랭킹
        if (args.length == 1 && args[0].equalsIgnoreCase("랭킹")) {
            int n = 10;
            List<Map.Entry<UUID, Long>> top = new ArrayList<>(store.topN(n));
            sender.sendMessage(plugin.msg("ranking_header", Map.of("n", String.valueOf(n))));
            for (int i = 0; i < top.size(); i++) {
                UUID id = top.get(i).getKey();
                String name = nameOf(id);
                long bal = top.get(i).getValue();
                sender.sendMessage(plugin.msg("ranking_row", Map.of(
                        "rank", String.valueOf(i + 1),
                        "player", name,
                        "balance", ItemUtil.fmt(bal)
                )));
            }
            return true;
        }

        // /캐시 <플레이어>
        if (args.length == 1) {
            String targetName = args[0];
            OfflinePlayer op = Bukkit.getPlayerExact(targetName);
            if (op == null) op = Bukkit.getOfflinePlayerIfCached(targetName);
            if (op == null) {
                sender.sendMessage(plugin.msg("player_not_found"));
                return true;
            }
            long bal = store.get(op.getUniqueId());
            sender.sendMessage(plugin.msg("balance_other", Map.of(
                    "player", op.getName() == null ? targetName : op.getName(),
                    "balance", ItemUtil.fmt(bal)
            )));
            return true;
        }

        // /캐시 보내기 <플레이어> <수량>
        if (args.length == 3 && args[0].equalsIgnoreCase("보내기")) {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(plugin.msg("only_player"));
                return true;
            }
            String targetName = args[1];
            OfflinePlayer target = Bukkit.getPlayerExact(targetName);
            if (target == null) target = Bukkit.getOfflinePlayerIfCached(targetName);
            if (target == null || target.getUniqueId() == null) {
                sender.sendMessage(plugin.msg("player_not_found"));
                return true;
            }
            Long amount = parsePositiveLong(args[2]);
            if (amount == null) {
                sender.sendMessage(plugin.msg("invalid_number"));
                return true;
            }
            if (!store.subtractIfEnough(p.getUniqueId(), amount)) {
                long bal = store.get(p.getUniqueId());
                p.sendMessage(plugin.msg("not_enough", Map.of("balance", ItemUtil.fmt(bal))));
                return true;
            }
            store.add(target.getUniqueId(), amount);
            long sb = store.get(p.getUniqueId());
            p.sendMessage(plugin.msg("sent", Map.of(
                    "to", target.getName() == null ? targetName : target.getName(),
                    "amount", ItemUtil.fmt(amount),
                    "sender_balance", ItemUtil.fmt(sb)
            )));
            Player targetOnline = Bukkit.getPlayer(target.getUniqueId());
            if (targetOnline != null) {
                long tb = store.get(target.getUniqueId());
                targetOnline.sendMessage(plugin.msg("incoming", Map.of(
                        "from", p.getName(),
                        "amount", ItemUtil.fmt(amount),
                        "balance", ItemUtil.fmt(tb)
                )));
            }
            return true;
        }

        // Admin: /캐시 지급 <플레이어> <수량>
        if (args.length == 3 && args[0].equalsIgnoreCase("지급")) {
            if (!sender.hasPermission("ultimate.cashshop.admin")) {
                sender.sendMessage(plugin.msg("player_not_found")); // mask
                return true;
            }
            String targetName = args[1];
            OfflinePlayer target = Bukkit.getPlayerExact(targetName);
            if (target == null) target = Bukkit.getOfflinePlayerIfCached(targetName);
            if (target == null || target.getUniqueId() == null) {
                sender.sendMessage(plugin.msg("player_not_found"));
                return true;
            }
            Long amount = parsePositiveLong(args[2]);
            if (amount == null) {
                sender.sendMessage(plugin.msg("invalid_number"));
                return true;
            }
            store.add(target.getUniqueId(), amount);
            long nb = store.get(target.getUniqueId());
            sender.sendMessage(plugin.msg("given", Map.of(
                    "player", target.getName() == null ? targetName : target.getName(),
                    "amount", ItemUtil.fmt(amount),
                    "balance", ItemUtil.fmt(nb)
            )));
            Player to = Bukkit.getPlayer(target.getUniqueId());
            if (to != null) {
                to.sendMessage(plugin.msg("received", Map.of(
                        "amount", ItemUtil.fmt(amount),
                        "balance", ItemUtil.fmt(nb)
                )));
            }
            return true;
        }

        // Admin: /캐시 차감 <플레이어> <수량>
        if (args.length == 3 && args[0].equalsIgnoreCase("차감")) {
            if (!sender.hasPermission("ultimate.cashshop.admin")) {
                sender.sendMessage(plugin.msg("player_not_found")); // mask
                return true;
            }
            String targetName = args[1];
            OfflinePlayer target = Bukkit.getPlayerExact(targetName);
            if (target == null) target = Bukkit.getOfflinePlayerIfCached(targetName);
            if (target == null || target.getUniqueId() == null) {
                sender.sendMessage(plugin.msg("player_not_found"));
                return true;
            }
            Long amount = parsePositiveLong(args[2]);
            if (amount == null) {
                sender.sendMessage(plugin.msg("invalid_number"));
                return true;
            }
            long cur = store.get(target.getUniqueId());
            long next = Math.max(0, cur - amount);
            store.set(target.getUniqueId(), next);
            sender.sendMessage(plugin.msg("taken", Map.of(
                    "player", target.getName() == null ? targetName : target.getName(),
                    "amount", ItemUtil.fmt(amount),
                    "balance", ItemUtil.fmt(next)
            )));
            Player to = Bukkit.getPlayer(target.getUniqueId());
            if (to != null) {
                to.sendMessage(plugin.msg("deducted", Map.of(
                        "amount", ItemUtil.fmt(amount),
                        "balance", ItemUtil.fmt(next)
                )));
            }
            return true;
        }

        sender.sendMessage(plugin.msg("invalid_args", Map.of("usage", command.getUsage())));
        return true;
    }

    private String nameOf(UUID id) {
        Player p = Bukkit.getPlayer(id);
        if (p != null) return p.getName();
        OfflinePlayer op = Bukkit.getOfflinePlayer(id);
        return op.getName() == null ? id.toString() : op.getName();
    }

    private Long parsePositiveLong(String s) {
        try {
            long v = Long.parseLong(s);
            if (v <= 0) return null;
            return v;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
