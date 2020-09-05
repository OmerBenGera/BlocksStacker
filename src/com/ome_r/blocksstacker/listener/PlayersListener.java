package com.ome_r.blocksstacker.listener;

import com.ome_r.blocksstacker.BlocksStacker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayersListener implements Listener {

    private BlocksStacker plugin;

    public PlayersListener(BlocksStacker plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e){
        if(!plugin.getSettings().toggleCommandEnabled)
            return;

        String command = e.getMessage().substring(1);

        if(!plugin.getSettings().toggleCommand.equalsIgnoreCase(command))
            return;

        e.setCancelled(true);

        if(plugin.getData().hasStackModeEnabled(e.getPlayer())){
            sendMessage(e.getPlayer(), "&7[&c&l!&7] Toggling blocks stacker &cOFF&7.");
        }else{
            sendMessage(e.getPlayer(), "&7[&a&l!&7] Toggling blocks stacker &aON&7.");
        }

        plugin.getData().toggleStackMode(e.getPlayer());
    }

    private void sendMessage(Player player, String message){
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

}
