package net.biryeongtrain.jsonDataAPI.impl.manager;


import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.biryeongtrain.jsonDataAPI.impl.storage.DataStorage;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unchecked")
public class DataManagerImpl implements DataManager {
    private final Map<UUID, Map<DataStorage<Object>, Object>> dataMap = new Object2ObjectOpenHashMap<>();
    @Override
    public Map<DataStorage<Object>, Object> getStorageMap(UUID uuid) {
        return this.dataMap.get(uuid);
    }

    @Override
    public <T> T getStorageValue(UUID uuid, DataStorage<T> storage) {
        var map = this.dataMap.get(uuid);
        return map != null ? (T) map.get(storage) : null;
    }

    @Override
    public <T> void setStorageValue(UUID uuid, DataStorage<T> storage, T value) {
        var map = this.dataMap.get(uuid);
        if (map != null) {
            map.put((DataStorage<Object>) storage, value);
        }
    }

    @Override
    public boolean isStored(UUID uuid) {
        return this.dataMap.containsKey(uuid);
    }

    @Override
    public void addHoldingPlayer(UUID uuid, Map<DataStorage<Object>, Object> data) {
        this.dataMap.put(uuid, data);
    }

    @Override
    public void removeHoldingPlayer(UUID uuid) {
        this.dataMap.remove(uuid);
    }
}
