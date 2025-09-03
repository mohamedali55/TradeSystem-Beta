package me.Klay.tradeSystem.gui;

import me.Klay.tradeSystem.utils.TradeConstants;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TradeGUI {
    public static ItemStack createConfirmButton(Player player, boolean Confirmed){
        ItemStack button = new ItemStack(Confirmed ? Material.GREEN_TERRACOTTA : Material.ORANGE_TERRACOTTA);
        var meta = button.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + player.getName() + " Confirm Trade");
        meta.setLore(java.util.Arrays.asList(
                ChatColor.GRAY + "Click to " + (Confirmed ? ChatColor.GREEN + "Confirm" : ChatColor.RED + "Deny") + ChatColor.GRAY + " the trade!",
                ChatColor.GRAY + "Status: " +(Confirmed ? ChatColor.GREEN + "Confirmed": ChatColor.RED + "Not Confirmed"),
                "",
                ChatColor.YELLOW +". Both players must confirm",
                ChatColor.YELLOW + ". Click again to unconfirm"

        ));
        button.setItemMeta(meta);
        return button;

    }


    public static Inventory createTradeInventory(Player player1, Player player2) {
        Inventory inventory =  Bukkit.createInventory(null, 45,
        ChatColor.DARK_PURPLE + "* " + ChatColor.GREEN + player1.getName() + " Trading with " + player2.getName() +ChatColor.DARK_PURPLE);
        for (int i = 0; i < 45; i++) {
            inventory.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));

        }
        for (int i : TradeConstants.PLAYER1_WINDOW) {
            inventory.setItem(i, null);

        }
        for (int i : TradeConstants.PLAYER2_WINDOW) {
            inventory.setItem(i, null);

        }
        ItemStack player1confirmButton = createConfirmButton(player1, false);
        ItemStack player2confirmButton = createConfirmButton(player2, false);

        ItemStack cancelButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        var meta = cancelButton.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Cancel Trade");
        meta.setLore(java.util.Arrays.asList(
                ChatColor.GRAY + "Click to cancel the trade!"
        ));
        cancelButton.setItemMeta(meta);
        inventory.setItem(38, player1confirmButton);
        inventory.setItem(42, player2confirmButton);
        inventory.setItem(49, cancelButton);
        return inventory;


    }
}
