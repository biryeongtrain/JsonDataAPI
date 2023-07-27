package net.biryeongtrain.jsonDataAPI.impl.storage;

import com.google.gson.Gson;
import net.biryeongtrain.jsonDataAPI.JsonDataAPI;
import net.biryeongtrain.jsonDataAPI.impl.gson.BaseGson;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.codehaus.plexus.util.IOUtil;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Logger;

public record JsonDataStorage<T>(String path, Class<T> clazz, Gson gson, Path saveDir) implements DataStorage<T> {
    static Logger logger = JsonDataAPI.getLogger();

    public JsonDataStorage(String path, Class<T> clazz) {
        this(path, clazz, BaseGson.GSON, Bukkit.getServer().getPluginsFolder().toPath());
    }

    public JsonDataStorage (String path, Class<T> clazz, Path saveDir) {
        this(path, clazz, BaseGson.GSON, saveDir);
    }

    @Override
    public boolean save(Server server, UUID player, T setting) {
        if (setting == null) {
            return false;
        }

        try {
            Path path = getPath(server, player);
            path.toFile().mkdirs();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.resolve(this.path + ".json").toFile())));
            writer.write(this.gson().toJson(setting));
            writer.close();

            return true;
        } catch (Exception e) {
            logger.warning("Couldn't save player data of player " + player + " for path " + this.path);
            e.printStackTrace();
            return false;
        }

    }

    @Nullable
    @Override
    public T load(Server server, UUID player) {
        try {
            Path path = getPath(server, player).resolve(this.path + ".json");
            if (!path.toFile().exists()) return null;

            String json = IOUtil.toString(new InputStreamReader(new FileInputStream(path.toFile()), StandardCharsets.UTF_8));
            return this.gson.fromJson(json, this.clazz);
        } catch (Exception e) {
            logger.warning("Couldn't load player data of player " + player + " for path " + this.path);
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Path getPath(Server server, UUID uuid) {
        return this.saveDir.resolve("recipe_holder").resolve(uuid.toString());
    }
}