package me.Klay.tradeSystem;

import org.bukkit.plugin.java.JavaPlugin;

public final class TradeSystem extends JavaPlugin {

    private TradeManger tradeManger;



    @Override
    public void onEnable() {
        // Plugin startup logic
        this.tradeManger = new TradeManger();
        getCommand("Trade").setExecutor(new TradeCommand(tradeManger));


    }
    public TradeManger getTradeManger() {
        return tradeManger;
    }


}
