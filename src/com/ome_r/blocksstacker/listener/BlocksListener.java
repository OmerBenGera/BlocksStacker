package com.ome_r.blocksstacker.listener;

import com.ome_r.blocksstacker.BlocksStacker;
import com.ome_r.blocksstacker.event.StackBlockEvent;
import com.ome_r.blocksstacker.event.UnstackBlockByPlayerEvent;
import com.ome_r.blocksstacker.event.UnstackBlockByEntityEvent;
import com.ome_r.blocksstacker.handlers.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BlocksListener implements Listener {

    private BlocksStacker plugin;

    public BlocksListener(BlocksStacker plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e){
        // Check if block is stackable
        if(!plugin.getSettings().stackedBlocks.contains(e.getBlock().getType().name()) &&
                !plugin.getSettings().stackedBlocks.contains(e.getBlock().getType().name() + ":" + e.getBlock().getData()))
            return;

        // Check if stack mode enabled in config
        if(plugin.getSettings().toggleCommandEnabled){
            //Check if player has stack mode enabled
            if(!plugin.getData().hasStackModeEnabled(e.getPlayer()))
                return;
        }

        // Check if the block player place against to is the same as the one he placed
        if(e.getBlockAgainst().getType() != e.getBlock().getType() || e.getBlockAgainst().getData() != e.getBlock().getData())
            return;

        // All checks are done. We can cancel event and stack the blocks

        e.setCancelled(true);

        // When sneaking, you'll stack all the items in your hand. Otherwise, you'll stack only 1 block
        int amount = !e.getPlayer().isSneaking() ? 1 : e.getItemInHand().getAmount();

        StackBlockEvent stackBlockEvent = new StackBlockEvent(e.getPlayer(), e.getBlockPlaced(), e.getBlockAgainst(), amount);
        Bukkit.getPluginManager().callEvent(stackBlockEvent);

        if(stackBlockEvent.isCancelled()){
            return;
        }

        DataHandler.BlockPosition blockPosition = new DataHandler.BlockPosition(e.getBlockAgainst());

        plugin.getData().setStackAmount(blockPosition, plugin.getData().getStackAmount(blockPosition) + amount);

        ItemStack inHand = e.getItemInHand().clone();
        inHand.setAmount(amount);
        e.getPlayer().getInventory().removeItem(inHand);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e){
        // Check if block is stackable
        if(!plugin.getSettings().stackedBlocks.contains(e.getBlock().getType().name()) &&
                !plugin.getSettings().stackedBlocks.contains(e.getBlock().getType().name() + ":" + e.getBlock().getData()))
            return;

        DataHandler.BlockPosition blockPosition = new DataHandler.BlockPosition(e.getBlock());

        // Check if block is stacked
        if(plugin.getData().getStackAmount(blockPosition) <= 1)
            return;

        // All checks are done. We can cancel event and add 1 to the stacked block

        e.setCancelled(true);

        // When sneaking, you'll break 64 from the stack. Otherwise, 1.
        int amount = !e.getPlayer().isSneaking() ? 1 : 64, leftAmount;

        // Fix amount so it won't be more than the stack's amount
        amount = Math.min(amount, plugin.getData().getStackAmount(blockPosition));

        UnstackBlockByPlayerEvent unstackBlockByPlayerEvent = new UnstackBlockByPlayerEvent(e.getPlayer(), e.getBlock(), amount);
        Bukkit.getPluginManager().callEvent(unstackBlockByPlayerEvent);

        if(unstackBlockByPlayerEvent.isCancelled()){
            return;
        }

        plugin.getData().setStackAmount(blockPosition, (leftAmount = plugin.getData().getStackAmount(blockPosition) - amount));

        ItemStack blockItem = e.getBlock().getState().getData().toItemStack(amount);

        // If the amount of the stack is less than 0, it should be air.
        if(leftAmount <= 0){
            e.getBlock().setType(Material.AIR);
        }

        // Dropping the item
        e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), blockItem);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent e){
        List<Block> blockList = new ArrayList<>(e.blockList());
        DataHandler.BlockPosition blockPosition;
        ItemStack blockItem;

        for(Block block : blockList){
            // Check if block is stackable
            if(!plugin.getSettings().stackedBlocks.contains(block.getType().name()) &&
                    !plugin.getSettings().stackedBlocks.contains(block.getType().name() + ":" + block.getData()))
                continue;

            blockPosition = new DataHandler.BlockPosition(block);
            int amount = plugin.getData().getStackAmount(blockPosition);

            UnstackBlockByEntityEvent unstackBlockByEntityEvent = new UnstackBlockByEntityEvent(e.getEntity(), block, amount);
            Bukkit.getPluginManager().callEvent(unstackBlockByEntityEvent);

            if(unstackBlockByEntityEvent.isCancelled()){
                continue;
            }

            if(amount <= 1)
                continue;

            // All checks are done. We can remove the block from the list.
            e.blockList().remove(block);

            blockItem = block.getState().getData().toItemStack(amount);

            plugin.getData().setStackAmount(blockPosition, 0);
            block.setType(Material.AIR);

            // Dropping the item
            block.getWorld().dropItemNaturally(block.getLocation(), blockItem);
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent e){
        DataHandler.BlockPosition blockPosition;
        for(Block block : e.getBlocks()){
            blockPosition = new DataHandler.BlockPosition(block);
            if(plugin.getData().getStackAmount(blockPosition) > 1) {
                e.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent e){
        DataHandler.BlockPosition blockPosition;
        for(Block block : e.getBlocks()){
            blockPosition = new DataHandler.BlockPosition(block);
            if(plugin.getData().getStackAmount(blockPosition) > 1) {
                e.setCancelled(true);
                break;
            }
        }
    }


}
