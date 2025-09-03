package me.Klay.tradeSystem.mangers;

import me.Klay.tradeSystem.gui.TradeGUI;
import me.Klay.tradeSystem.models.TradeRequest;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TradeManger {
    private final Map<UUID, TradeRequest> tradeRequests;

    public TradeManger() {
        this.tradeRequests = new HashMap<>();
    }
    public void initiateTradeRequest(Player requester , Player receiver) {
        if (!isPlayerInRange(requester, receiver)) {
            requester.sendMessage(ChatColor.RED + "You are not in range of the target!");
            sendTradeRequestMessage(requester, receiver);
            return;

        }
        TradeRequest tradeRequest = new TradeRequest(requester, receiver);
        if(!addPlayerToTradeRequest(tradeRequest)){
            requester.sendMessage(ChatColor.RED + "You are already in a trade request!");
            return;


        }
        receiver.sendMessage(ChatColor.GREEN + "You have received a trade request from " + requester.getName() + "!");
        return;


    }
    public void handleTradeAccept(Player player) {
        var tradeRequest = getTradeRequest(player);
        if (tradeRequest == null) {
            return;
        }
        Player initiator = tradeRequest.getRequester();
        removemTradeRequest(player);

        if(!initiator .isOnline()){
            player.sendMessage(ChatColor.RED + "The trade request is no longer valid!");

            return;

        }
        //start trade session
        initiator.sendMessage(ChatColor.GREEN + player.getName() + " has accepted your trade request!");
        player.sendMessage(ChatColor.GREEN + "You have accepted " + initiator.getName() + "'s trade request!");
        startTradeSession(player, initiator);
        removemTradeRequest(player);

    }
    private void startTradeSession(Player player1 , Player player2) {
        var tradeInventory = TradeGUI.createTradeInventory(player1, player2);
        player1.openInventory(tradeInventory);
        player2.openInventory(tradeInventory);
    }


    public void handleTradeDeny(Player player) {
        var tradeRequest = getTradeRequest(player);
        if (tradeRequest == null) {
            player.sendMessage(ChatColor.RED + "You are not in a trade request!");
            return;
        }
        Player initiator = tradeRequest.getRequester();
        removemTradeRequest(player);

        if(!initiator .isOnline()){
            player.sendMessage(ChatColor.RED + "The player denied the trade request");

            return;

        }
        player.sendMessage(ChatColor.RED + player.getName() + " has denied your trade request!");
        removemTradeRequest(player);


    }
    private void removemTradeRequest(Player player) {
        tradeRequests.get(player.getUniqueId());
    }


    private  TradeRequest getTradeRequest(Player player) {
        return tradeRequests.get(player.getUniqueId());
    }

    private boolean addPlayerToTradeRequest(TradeRequest request) {
        if (tradeRequests.containsKey(request.getRequester().getUniqueId())) {
            return false;
        }
        tradeRequests.put(request.getRequester().getUniqueId(), request);
        return true;
    }

    private boolean isPlayerInRange(Player requester, Player receiver) {
        return requester.getWorld().equals(receiver.getWorld()) && requester.getLocation().distance(receiver.getLocation()) <= 10;
    }
    private void sendTradeRequestMessage(Player requester , Player receiver){
        TextComponent message = new  TextComponent(ChatColor.GREEN + "You have received a trade request from " + requester.getName() + "!");

        TextComponent acceptButton = new TextComponent(ChatColor.GREEN + "[Accept]");
        acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , "/trade accept " + requester.getName()));
        acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GREEN + "Click to accept the trade request!")));

        TextComponent denyButton = new TextComponent(ChatColor.RED + "[Deny]");
        denyButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , "/trade deny " + requester.getName()));
        denyButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.RED + "Click to deny the trade request!")));

        message.addExtra(acceptButton);
        message.addExtra(denyButton);
        receiver.spigot().sendMessage(message);

    }
}
