package me.Klay.tradeSystem;

import me.Klay.tradeSystem.commands.TradeCommand;
import me.Klay.tradeSystem.mangers.TradeManger;
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
