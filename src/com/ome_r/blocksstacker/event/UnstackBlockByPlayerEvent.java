package com.ome_r.blocksstacker.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UnstackBlockByPlayerEvent extends Event implements Cancellable {

    private static HandlerList handlerList = new HandlerList();

    private final Player player;
    private final Block block;
    private final int amount;

    private boolean cancelled;

    public UnstackBlockByPlayerEvent(Player player, Block block, int amount){
        this.player = player;
        this.block = block;
        this.amount = amount;
        this.cancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public Block getBlock() {
        return block;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

}
