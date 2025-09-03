package me.Klay.tradeSystem;

import org.bukkit.ChatColor;
import org.bukkit.block.data.type.Switch;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class TradeCommand implements CommandExecutor {

    private final TradeManger tradeManger;
    public TradeCommand(TradeManger tradeManger) {
        this.tradeManger = tradeManger;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args){

        if(!(sender instanceof Player requester)){
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;

        }
        if(args.length != 1){
            sender.sendMessage(ChatColor.RED + "Usage: /trade <player> or /trade accept/deny");
            return true;
        }
        String action = args[0].toLowerCase();
        switch(action){
            case "accept":
                tradeManger.handleTradeAccept(requester);
                break;
        case "deny":
            tradeManger.handleTradeDeny(requester);

            break;
            default:
                Player receiver = sender.getServer().getPlayer(args[0]);
                if(receiver == null){
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                }
                tradeManger.initiateTradeRequest(requester , receiver);

        }

        return true;
    }


}
