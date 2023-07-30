package net.biryeongtrain.jsonDataAPI.impl.storage;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.UUID;

public interface DataStorage<T> {
    default boolean save(Player player, T value) {
        return this.save(player.getServer(), player.getUniqueId(), value);
    }

    boolean save(Server server, UUID player, T setting);

    @Nullable
    default T load(Player player) {return this.load(player.getServer(), player.getUniqueId());}

    @Nullable
    T load(Server server, UUID player);

    default Path getPath(Player player) {
        return getPath(player.getUniqueId());
    }

    Path getPath(UUID uuid);
}
