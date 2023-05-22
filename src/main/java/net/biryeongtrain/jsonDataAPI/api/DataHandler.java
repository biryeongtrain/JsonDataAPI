package net.biryeongtrain.jsonDataAPI.api;

import com.google.common.collect.ImmutableSet;
import net.biryeongtrain.jsonDataAPI.impl.manager.DataManager;
import net.biryeongtrain.jsonDataAPI.impl.manager.DataManagerImpl;
import net.biryeongtrain.jsonDataAPI.impl.storage.DataStorage;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DataHandler {
    private static final DataManager MANAGER;
    private static final Set<DataStorage<?>> STORAGE = new HashSet<>();

    /**
     * DataStorage를 생성하면 무조건 register 해주셔야합니다.
     */
    public static <T extends DataStorage<?>> boolean register(T dataStorage) {
        return STORAGE.add(dataStorage);
    }

    /**
     * 플레이어의 데이터를 Json에서 받아옵니다.
     * @param player
     * @param storage {@link DataStorage}
     * @return 데이터가 없을 경우 {@link null} 을 반환함.
     */
    public static <T> T getDataFor(Player player, DataStorage<T> storage) {
        return getDataFor(player.getServer(), player.getUniqueId(), storage);
    }

    public static <T> T getDataFor(Server server, UUID uuid, DataStorage<T> dataStorage)  {
        if (MANAGER.isStored(uuid)){
            return MANAGER.getStorageValue(uuid, dataStorage);
        } else {
            return dataStorage.load(server, uuid);
        }
    }

    /**
     * 플레이어의 데이터를 Json 파일에 저장합니다.
     * @param player
     * @param storage
     * @param value Class 값
     */
    public static <T> void setDataFor(Player player, DataStorage<T> storage, T value) {
        setDataFor(player.getServer(), player.getUniqueId(), storage, value);
    }

    public static <T> void setDataFor(Server server, UUID uuid, DataStorage<T> storage, T value) {
        if (MANAGER.isStored(uuid)) {
            MANAGER.setStorageValue(uuid, storage, value);
        } else {
            storage.save(server, uuid, value);
        }
    }

    public static ImmutableSet<DataStorage<?>> getDataStorageSet() {
        return ImmutableSet.copyOf(STORAGE);
    }

    static {
        MANAGER = new DataManagerImpl();
    }
}
