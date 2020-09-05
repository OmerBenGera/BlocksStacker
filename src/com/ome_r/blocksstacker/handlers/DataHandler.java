package com.ome_r.blocksstacker.handlers;

import com.ome_r.blocksstacker.BlocksStacker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DataHandler {

    private BlocksStacker plugin;

    private Set<UUID> enabledStackMode = new HashSet<>();
    private Map<BlockPosition, Integer> stackedBlocks = new HashMap<>();

    public DataHandler(BlocksStacker plugin){
        this.plugin = plugin;
        File file = new File(plugin.getDataFolder(), "data/blocks.yml");

        if(!file.exists())
            return;

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        BlockPosition blockPosition;
        int amount;

        for(String _blockPosition : cfg.getConfigurationSection("").getKeys(false)){
            blockPosition = new BlockPosition(_blockPosition);
            amount = cfg.getInt(_blockPosition);
            setStackAmount(blockPosition, amount);
        }

    }

    public void setStackAmount(BlockPosition blockPosition, int amount){
        stackedBlocks.put(blockPosition, amount);
        ArmorStand armorStand = getHologram(blockPosition);
        if(amount <= 1){
            stackedBlocks.remove(blockPosition);
            armorStand.remove();
        }else{
            armorStand.setCustomName(plugin.getSettings().customName
                    .replace("{0}", String.valueOf(amount))
                    .replace("{1}", getFormattedType(blockPosition.getBlock().getType().name()))
            );
        }
    }

    public int getStackAmount(Location location){
        return getStackAmount(new BlockPosition(location));
    }

    public int getStackAmount(BlockPosition blockPosition){
        return stackedBlocks.getOrDefault(blockPosition, 1);
    }

    public boolean hasStackModeEnabled(Player player){
        return enabledStackMode.contains(player.getUniqueId());
    }

    public void toggleStackMode(Player player){
        if(hasStackModeEnabled(player)){
            enabledStackMode.remove(player.getUniqueId());
        }else{
            enabledStackMode.add(player.getUniqueId());
        }
    }

    public void saveDatabase(){
        File file = new File(plugin.getDataFolder(), "data/blocks.yml");

        if(file.exists())
            file.delete();

        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }catch(Exception ex){
            ex.printStackTrace();
            return;
        }

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        for(BlockPosition blockPosition : stackedBlocks.keySet()){
            int amount = stackedBlocks.get(blockPosition);
            if(amount > 1)
                cfg.set(blockPosition.toString(), amount);
        }

        try{
            cfg.save(file);
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    private ArmorStand getHologram(BlockPosition blockPosition){
        Location hologramLocation = blockPosition.getBlock().getLocation().add(0.5, 1, 0.5);

        // Looking for an armorstand
        for(Entity entity : blockPosition.getBlock().getChunk().getEntities()){
            if(entity instanceof ArmorStand && entity.getLocation().equals(hologramLocation)){
                return (ArmorStand) entity;
            }
        }

        // Couldn't find one, creating one...

        ArmorStand armorStand = blockPosition.getWorld().spawn(hologramLocation, ArmorStand.class);

        armorStand.setGravity(false);
        armorStand.setSmall(true);
        armorStand.setVisible(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setMarker(true);

        return armorStand;
    }

    private String getFormattedType(String type){
        StringBuilder stringBuilder = new StringBuilder();

        for(String section : type.split("_")){
            stringBuilder.append(" ").append(section.substring(0, 1).toUpperCase()).append(section.substring(1).toLowerCase());
        }

        return stringBuilder.toString().substring(1);
    }

    public static class BlockPosition{

        private int x, y, z;
        private String world;

        public BlockPosition(Location location){
            this(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        }

        public BlockPosition(Block block){
            this(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
        }

        private BlockPosition(String world, int x, int y, int z){
            this.x = x;
            this.y = y;
            this.z = z;
            this.world = world;
        }

        private BlockPosition(String string){
            String[] sections = string.split(", ");
            this.world = sections[0];
            this.x = Integer.valueOf(sections[1]);
            this.y = Integer.valueOf(sections[2]);
            this.z = Integer.valueOf(sections[3]);
        }

        public World getWorld() {
            return Bukkit.getWorld(world);
        }

        public Block getBlock(){
            return getWorld().getBlockAt(x, y, z);
        }

        @Override
        public String toString() {
            return world + ", " + x + ", " + y + ", " + z;
        }

        @Override
        public int hashCode() {
            int hash = 19 * 3 + (this.world != null ? this.world.hashCode() : 0);
            hash = 19 * hash + (int)(Double.doubleToLongBits(this.x) ^ Double.doubleToLongBits(this.x) >>> 32);
            hash = 19 * hash + (int)(Double.doubleToLongBits(this.y) ^ Double.doubleToLongBits(this.y) >>> 32);
            hash = 19 * hash + (int)(Double.doubleToLongBits(this.z) ^ Double.doubleToLongBits(this.z) >>> 32);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof BlockPosition){
                BlockPosition other = (BlockPosition) obj;
                return world.equals(other.world) && x == other.x && y == other.y && z == other.z;
            }
            return super.equals(obj);
        }
    }

}
