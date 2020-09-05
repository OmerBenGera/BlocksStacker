package com.ome_r.blocksstacker.handlers;

import com.ome_r.blocksstacker.BlocksStacker;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class SettingsHandler {

    public final List<String> stackedBlocks;
    public final boolean toggleCommandEnabled;
    public final String toggleCommand;
    public final String customName;

    public SettingsHandler(BlocksStacker plugin){
        File file = new File(plugin.getDataFolder(), "config.yml");

        if(!file.exists())
            plugin.saveResource("config.yml", false);

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        stackedBlocks = cfg.getStringList("stacked-blocks");
        toggleCommandEnabled = cfg.getBoolean("toggle-command.enabled");
        toggleCommand = cfg.getString("toggle-command.command");
        customName = ChatColor.translateAlternateColorCodes('&', cfg.getString("custom-name"));
    }

}
