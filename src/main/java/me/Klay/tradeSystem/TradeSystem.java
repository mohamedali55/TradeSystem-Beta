package me.Klay.tradeSystem;

import me.Klay.tradeSystem.commands.TradeCommand;
import me.Klay.tradeSystem.listeners.TradeListener;
import me.Klay.tradeSystem.mangers.TradeManger;
import org.bukkit.plugin.java.JavaPlugin;

public final class TradeSystem extends JavaPlugin {

    private TradeManger tradeManager;

    @Override
    public void onEnable() {
        // Initialize TradeManager
        tradeManager = new TradeManger(this);

        // Register command and event listener
        getCommand("trade").setExecutor(new TradeCommand(this));
        getServer().getPluginManager().registerEvents(new TradeListener(this), this);
    }

    @Override
    public void onDisable() {
        tradeManager = null;
    }

    public TradeManger getTradeManager() {
        return tradeManager;
    }

}
