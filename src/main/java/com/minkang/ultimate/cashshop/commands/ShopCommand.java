package com.minkang.ultimate.cashshop.commands;

import com.minkang.ultimate.cashshop.Main;
import com.minkang.ultimate.cashshop.gui.CashShopGUI;
import com.minkang.ultimate.cashshop.store.ShopStore;
import com.minkang.ultimate.cashshop.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ShopCommand implements CommandExecutor {
    private final Main plugin;

    public ShopCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.msg("only_player"));
                return true;
            }
            CashShopGUI.open((Player) sender, plugin);
            return true;
        }

        if (!(sender instanceof Player p)) {
            sender.sendMessage(plugin.msg("only_player"));
            return true;
        }

        // /캐시상점 등록 <슬롯> <가격> <수량>
        if (args.length == 4 && args[0].equalsIgnoreCase("등록")) {
            Integer slot = parseInt(args[1]);
            Long price = parsePositiveLong(args[2]);
            Integer amount = parseInt(args[3]);
            if (slot == null || price == null || amount == null || amount <= 0) {
                sender.sendMessage(plugin.msg("invalid_args", Map.of("usage", command.getUsage())));
                return true;
            }
            int max = CashShopGUI.size(plugin) - 1;
            if (slot < 0 || slot > max) {
                sender.sendMessage(plugin.msg("invalid_slot", Map.of("max", String.valueOf(max))));
                return true;
            }
            ItemStack inHand = p.getInventory().getItemInMainHand();
            if (inHand == null || inHand.getType() == Material.AIR) {
                sender.sendMessage(plugin.msg("hold_item"));
                return true;
            }
            if (plugin.getShopStore().getListing(slot).isPresent()) {
                sender.sendMessage(plugin.msg("shop_occupied", Map.of("slot", String.valueOf(slot))));
                return true;
            }
            ItemStack base = inHand.clone();
            base.setAmount(1); // display as 1
            plugin.getShopStore().setListing(slot, new ShopStore.Listing(base, price, amount));
            sender.sendMessage(plugin.msg("shop_registered", Map.of(
                    "slot", String.valueOf(slot),
                    "price", ItemUtil.fmt(price),
                    "amount", String.valueOf(amount)
            )));
            return true;
        }

        // /캐시상점 취소 <슬롯>
        if (args.length == 2 && args[0].equalsIgnoreCase("취소")) {
            Integer slot = parseInt(args[1]);
            if (slot == null) {
                sender.sendMessage(plugin.msg("invalid_args", Map.of("usage", command.getUsage())));
                return true;
            }
            plugin.getShopStore().removeListing(slot);
            sender.sendMessage(plugin.msg("shop_unregistered", Map.of("slot", String.valueOf(slot))));
            return true;
        }

        // /캐시상점 링크 <NPC_ID>
        if (args.length == 2 && args[0].equalsIgnoreCase("링크")) {
            Integer id = parseInt(args[1]);
            if (id == null) {
                sender.sendMessage(plugin.msg("link_need_npc"));
                return true;
            }
            try {
                if (plugin.getLinkStore().isLinked(id)) {
                    sender.sendMessage(plugin.msg("link_already", Map.of("npcId", String.valueOf(id))));
                } else {
                    plugin.getLinkStore().link(id);
                    sender.sendMessage(plugin.msg("link_success", Map.of("npcId", String.valueOf(id))));
                }
            } catch (Throwable t) {
                sender.sendMessage(plugin.msg("link_error"));
            }
            return true;
        }

        // /캐시상점 링크해제 <NPC_ID>
        if (args.length == 2 && args[0].equalsIgnoreCase("링크해제")) {
            Integer id = parseInt(args[1]);
            if (id == null) {
                sender.sendMessage(plugin.msg("link_need_npc"));
                return true;
            }
            if (plugin.getLinkStore().unlink(id)) {
                sender.sendMessage(plugin.msg("link_unlinked", Map.of("npcId", String.valueOf(id))));
            } else {
                sender.sendMessage(plugin.msg("link_not_linked", Map.of("npcId", String.valueOf(id))));
            }
            return true;
        }

        sender.sendMessage(plugin.msg("invalid_args", Map.of("usage", command.getUsage())));
        return true;
    }

    private Integer parseInt(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return null; }
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
