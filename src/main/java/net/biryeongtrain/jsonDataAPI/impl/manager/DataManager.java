package net.biryeongtrain.jsonDataAPI.impl.manager;

import net.biryeongtrain.jsonDataAPI.impl.storage.DataStorage;

import java.util.Map;
import java.util.UUID;

public interface DataManager {
    Map<DataStorage<Object>, Object> getStorageMap(UUID uuid);
    <T> T getStorageValue(UUID uuid, DataStorage<T> storage);
    <T> void setStorageValue(UUID uuid, DataStorage<T> storage, T value);
    boolean isStored(UUID uuid);
    void addHoldingPlayer(UUID uuid, Map<DataStorage<Object>, Object> data);
    void removeHoldingPlayer(UUID uuid);
}

