package net.biryeongtrain.jsonDataAPI;

import lombok.Getter;
import net.biryeongtrain.jsonDataAPI.impl.listener.PlayerDataListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class JsonDataAPI extends JavaPlugin {
    @Getter
    private static Logger logger;

    @Override
    public void onEnable() {
        logger = getLogger();

        getServer().getPluginManager().registerEvents(new PlayerDataListener(), this);
    }

    @Override
    public void onDisable() {
    }
}
