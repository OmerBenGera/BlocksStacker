package com.ome_r.blocksstacker;

import com.ome_r.blocksstacker.handlers.DataHandler;
import com.ome_r.blocksstacker.handlers.SettingsHandler;
import com.ome_r.blocksstacker.listener.BlocksListener;
import com.ome_r.blocksstacker.listener.PlayersListener;
import org.bukkit.plugin.java.JavaPlugin;

public class BlocksStacker extends JavaPlugin {

    private static BlocksStacker plugin;

    private SettingsHandler settingsHandler;
    private DataHandler dataHandler;

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        plugin = this;

        /* L O A D  C O N F I G */
        settingsHandler = new SettingsHandler(this);

        /* L O A D  D A T A */
        dataHandler = new DataHandler(this);

        /* L O A D  L I S T E N E R S */
        getServer().getPluginManager().registerEvents(new BlocksListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayersListener(this), this);

        /* S T A R T  A S Y N C  S A V E */
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> dataHandler.saveDatabase(), 12000, 12000);

        getLogger().info("Successfully loaded plugin (Took " + (System.currentTimeMillis() - startTime) + "ms)");
    }

    @Override
    public void onDisable() {
        dataHandler.saveDatabase();
    }

    public SettingsHandler getSettings() {
        return settingsHandler;
    }

    public DataHandler getData() {
        return dataHandler;
    }

    public static BlocksStacker getPlugin(){
        return plugin;
    }

}
