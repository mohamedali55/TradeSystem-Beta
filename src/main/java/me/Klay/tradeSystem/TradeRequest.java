package me.Klay.tradeSystem;

import org.bukkit.entity.Player;

public class TradeRequest {
    private final Player requester;
    private final Player receiver;
    private final long tiimestamp;

    public TradeRequest(Player requester, Player receiver) {
        this.requester = requester;
        this.receiver = receiver;
        this.tiimestamp = System.currentTimeMillis();
    }

    public Player getRequester() {
        return requester;
    }

    public Player getReceiver() {
        return receiver;
    }

    public long getTiimestamp() {
        return tiimestamp;
    }
}
