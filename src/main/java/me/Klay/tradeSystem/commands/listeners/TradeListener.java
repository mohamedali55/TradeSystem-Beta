package me.Klay.tradeSystem.commands.listeners;

import me.Klay.tradeSystem.TradeSystem;
import me.Klay.tradeSystem.models.TradeSession;
import me.Klay.tradeSystem.utils.TradeConstants;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.net.http.WebSocket;
import java.util.stream.IntStream;

public class TradeListener implements Listener {

    private final TradeSystem plugin;
    public TradeListener(TradeSystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        Player player = (Player) e.getWhoClicked();
        var session = plugin.getTradeManger().getActiveTrade(player);
        if(session == null){
            return;
        }
        if (!e.getInventory().equals(session.getTradeInventory())) {
            return;
        }
        int slot = e.getSlot();
        if(isGuiControlSlot(slot)){
            e.setCancelled(true);
            if (slot == TradeConstants.CANCEL_BUTTON){
                return;
            }
            if(slot == TradeConstants.CONFIRM_BUTTON_1 || slot == TradeConstants.CONFIRM_BUTTON_2){
                return;
            }
            return;
        }
        boolean isPlayer1 = session.isPlayer1(player);
        if (session.isConfirmed(player)) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can't modify the trade anymore!");
            return;
        }
        if (slot <= 54){
            return;
        }
        if (isPlayer1) {
            if (IntStream.of(TradeConstants.PLAYER1_WINDOW).anyMatch( x -> x ==slot)) {
                e.setCancelled(true);
            }

        }else {
            if (IntStream.of(TradeConstants.PLAYER2_WINDOW).anyMatch( x -> x ==slot)) {
                e.setCancelled(true);
            }
        }




    }



    private boolean isGuiControlSlot(int slot){
        return IntStream.of(TradeConstants.PLAYER1_WINDOW).noneMatch( x -> x ==slot)&&
                IntStream.of(TradeConstants.PLAYER2_WINDOW).noneMatch( x -> x ==slot);
    }




}
