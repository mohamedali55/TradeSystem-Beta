package me.Klay.tradeSystem.models;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class TradeSession {
    private final Player player1;
    private final Player player2;
    private boolean player1Confirmed;
    private boolean player2Confirmed;
    private final Inventory tradeInventory;

    public TradeSession(Player player1, Player player2, Inventory tradeInventory) {
        this.player1 = player1;
        this.player2 = player2;
        this.tradeInventory = tradeInventory;
    }
    public boolean isPlayer1(Player player){
        return player.equals(this.player1);
    }
    public boolean isConfirmed(Player player){
        return isPlayer1(player)? player1Confirmed : player2Confirmed;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public boolean isPlayer1Confirmed() {
        return player1Confirmed;
    }

    public boolean isPlayer2Confirmed() {
        return player2Confirmed;
    }

    public Inventory getTradeInventory() {
        return tradeInventory;
    }


}
