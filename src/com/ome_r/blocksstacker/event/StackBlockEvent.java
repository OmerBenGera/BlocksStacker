package com.ome_r.blocksstacker.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StackBlockEvent extends Event implements Cancellable {

    private static HandlerList handlerList = new HandlerList();

    private final Block source, target;
    private final Player player;
    private final int amount;

    private boolean cancelled;

    public StackBlockEvent(Player player, Block source, Block target, int amount){
        this.player = player;
        this.source = source;
        this.target = target;
        this.amount = amount;
        this.cancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public Block getSource() {
        return source;
    }

    public Block getTarget() {
        return target;
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
