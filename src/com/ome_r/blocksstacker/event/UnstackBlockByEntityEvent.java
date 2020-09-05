package com.ome_r.blocksstacker.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UnstackBlockByEntityEvent extends Event implements Cancellable {

    private static HandlerList handlerList = new HandlerList();

    private final Entity entity;
    private final Block block;
    private final int amount;

    private boolean cancelled;

    public UnstackBlockByEntityEvent(Entity entity, Block block, int amount){
        this.entity = entity;
        this.block = block;
        this.amount = amount;
        this.cancelled = false;
    }

    public Entity getEntity() {
        return entity;
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
