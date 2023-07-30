package net.biryeongtrain.jsonDataAPI.impl.listener;


import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.biryeongtrain.jsonDataAPI.api.DataHandler;
import net.biryeongtrain.jsonDataAPI.impl.manager.DataManager;
import net.biryeongtrain.jsonDataAPI.impl.manager.DataManagerImpl;
import net.biryeongtrain.jsonDataAPI.impl.storage.DataStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.biryeongtrain.jsonDataAPI.api.DataHandler.MANAGER;

@SuppressWarnings("unchecked")
public class PlayerDataListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoined(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        var map = new Object2ObjectOpenHashMap<DataStorage<Object>, Object>();
        for (DataStorage<?> storage : DataHandler.getDataStorageSet()) {
            try {
                map.put((DataStorage<Object>) storage, storage.load(player));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        MANAGER.addHoldingPlayer(player.getUniqueId(), map);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        var data = MANAGER.getStorageMap(player.getUniqueId());
        data.forEach((storage, value) -> storage.save(player, value));
        MANAGER.removeHoldingPlayer(player.getUniqueId());
    }
}
