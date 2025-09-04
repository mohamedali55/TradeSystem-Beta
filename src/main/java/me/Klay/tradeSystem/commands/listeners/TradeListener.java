package me.Klay.tradeSystem.commands.listeners;

import me.Klay.tradeSystem.TradeSystem;
import me.Klay.tradeSystem.gui.TradeGUI;
import me.Klay.tradeSystem.mangers.TradeManger;
import me.Klay.tradeSystem.models.TradeSession;
import me.Klay.tradeSystem.utils.TradeConstants;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.http.WebSocket;
import java.util.HashMap;
import java.util.stream.IntStream;

public class TradeListener implements Listener {

    private final TradeSystem plugin;
    public TradeListener(TradeSystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        Player player = (Player) e.getWhoClicked();
        var session = plugin.getTradeManager().getActiveTrade(player);
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
                handleCancel(session);
                return;
            }
            if(slot == TradeConstants.CONFIRM_BUTTON_1 || slot == TradeConstants.CONFIRM_BUTTON_2){
                handleConfirm(player, session, slot);

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
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        Player player = (Player) e.getPlayer();
        TradeManger tradeManger = plugin.getTradeManager();
        TradeSession session = tradeManger.getActiveTrade(player);
        if(session != null && e.getInventory().equals(session.getTradeInventory())){
            handleCancel(session);
        }
        Player otherPlayer = session.isPlayer1(player) ? session.getPlayer2() : session.getPlayer1();
        otherPlayer.sendMessage(ChatColor.RED + player.getName() + " has closed the trade!");
    }
    //Notify other player
    @EventHandler
    public void onPlayerQuit(InventoryCloseEvent e){
        Player player = (Player) e.getPlayer();
        TradeManger tradeManger = plugin.getTradeManager();
        TradeSession session = tradeManger.getActiveTrade(player);
        if(session != null ){
            handleCancel(session);
        }
        Player otherPlayer = session.isPlayer1(player) ? session.getPlayer2() : session.getPlayer1();
        otherPlayer.sendMessage(ChatColor.RED + player.getName() + " has quit the trade!");
    }


    private void handleConfirm(Player player, TradeSession session, int slot){
        boolean isPlayer1 = session.isPlayer1(player);

        if(isPlayer1 && slot == TradeConstants.CONFIRM_BUTTON_1 || !isPlayer1 && slot == TradeConstants.CONFIRM_BUTTON_2){
            player.sendMessage(ChatColor.RED + "That is not your Confirm button!");
            return;
        }
        boolean wasConfirmed = session.isConfirmed(player);
        session.setConfirmed(player, !wasConfirmed);
        var newButton = TradeGUI.createConfirmButton(player, !wasConfirmed);
        session.getTradeInventory().setItem(slot, newButton);
        if(!wasConfirmed){
            //confirmed Sound
            player.playSound(player.getLocation() , Sound.BLOCK_NOTE_BLOCK_BELL , 1.0f , 1.5f);
            Player otherPlayer = isPlayer1 ? session.getPlayer2() : session.getPlayer1();
            otherPlayer.playSound(otherPlayer.getLocation() , Sound.BLOCK_NOTE_BLOCK_BELL , 1.0f , 1.5f);


            if (session.bothConfirmed()) {
                handleTradeComplete(session);
            }

        }else {
            player.sendMessage(ChatColor.YELLOW + "Confirmation Cancelled");

            //UnconfirmSound
            player.playSound(player.getLocation() , Sound.BLOCK_NOTE_BLOCK_BELL , 1.0f , 0.8f);
            Player otherPlayer = isPlayer1 ? session.getPlayer2() : session.getPlayer1();
            otherPlayer.playSound(otherPlayer.getLocation() , Sound.BLOCK_NOTE_BLOCK_BELL , 1.0f , 0.8f);

        }

    }
    public void playTradeAnimation(TradeSession session , Runnable onComplete){
        Player player1 = session.getPlayer1();
        Player player2 = session.getPlayer2();
        new BukkitRunnable(){
            int tick = 0;
            int boarderIndex = 0;

            @Override
            public void run() {

                if (tick >= 0){
                    cancel();
                    onComplete.run();
                    return;
                }
                // Play countdown
                if (tick % 20 == 0 ){
                    int secondsLeft = 2 - (tick / 20);
                    Sound sound = secondsLeft > 0 ? Sound.BLOCK_NOTE_BLOCK_PLING : Sound.ENTITY_PLAYER_LEVELUP;
                    float pitch = secondsLeft > 0 ? 1.0f * ((2 - secondsLeft) *0.3f) : 0.0f;
                    player1.playSound(player1.getLocation() , sound , 1.0f , pitch);
                    player2.playSound(player1.getLocation() , sound , 1.0f , pitch);
                }
                if (boarderIndex > 0 ){
                    int prevSlot = TradeConstants.BORDER_SLOTS[boarderIndex -1 % TradeConstants.BORDER_SLOTS.length];
                    session.getTradeInventory().setItem(prevSlot, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
                }

                int currentSlot = TradeConstants.BORDER_SLOTS[boarderIndex % TradeConstants.BORDER_SLOTS.length];
                session.getTradeInventory().setItem(currentSlot, new ItemStack(Material.GREEN_STAINED_GLASS_PANE));
                boarderIndex++;
                tick++;


            }

        }.runTaskTimer(plugin, 0L, 1L);

    }





    public void handleTradeComplete(TradeSession session){
        Player player1 = session.getPlayer1();
        Player player2 = session.getPlayer2();
        playTradeAnimation(session , () -> {
            plugin.getTradeManager().endTradeSession(session);
            var tradeInv = session.getTradeInventory();
            transferItems(session.getPlayer1(), tradeInv, TradeConstants.PLAYER2_WINDOW);
            transferItems(session.getPlayer2(), tradeInv, TradeConstants.PLAYER1_WINDOW);

            player1.closeInventory();
            player2.closeInventory();

            player1.sendTitle(
                    ChatColor.GREEN + "Trade Complete!",
                    ChatColor.GOLD + "Items have been exchanged!",
                    10,
                    40,
                    10
            );
            player2.sendTitle(
                    ChatColor.GREEN + "Trade Complete!",
                    ChatColor.GOLD + "Items have been exchanged!",
                    10,
                    40,
                    10
            );
            player1.sendMessage(ChatColor.GREEN + "the Trade with " + player2.getName() + " has been completed!");
            player2.sendMessage(ChatColor.GREEN + "the Trade with " + player1.getName() + " has been completed!");
        });



    }




    private void handleCancel(TradeSession session){
        this.plugin.getTradeManager().endTradeSession(session);
        var tradeInv = session.getTradeInventory();
        transferItems(session.getPlayer1(), tradeInv, TradeConstants.PLAYER1_WINDOW);
        transferItems(session.getPlayer2(), tradeInv, TradeConstants.PLAYER2_WINDOW);

        session.getPlayer1().sendMessage(ChatColor.GREEN + "The trade has been cancelled!");
        session.getPlayer2().sendMessage(ChatColor.GREEN + "The trade has been cancelled!");

        session.getPlayer1().closeInventory();
        session.getPlayer2().closeInventory();
    }
    private void transferItems(Player player, Inventory tradeInv, int[] slots){
        boolean hasDroppedItems = false;
        for (int slot : slots) {
            ItemStack item = tradeInv.getItem(slot);
            if(item != null &&item.getType().equals(Material.AIR)){
                HashMap<Integer, ItemStack> couldntfit = player.getInventory().addItem(item);
                if(!couldntfit.isEmpty()){
                    for (ItemStack leftover : couldntfit.values()) {
                        player.getWorld().dropItemNaturally(player.getLocation(), leftover);
                    }
                    hasDroppedItems = true;
                }
            }
        }
    }



    private boolean isGuiControlSlot(int slot){
        return IntStream.of(TradeConstants.PLAYER1_WINDOW).noneMatch( x -> x ==slot)&&
                IntStream.of(TradeConstants.PLAYER2_WINDOW).noneMatch( x -> x ==slot);
    }




}
