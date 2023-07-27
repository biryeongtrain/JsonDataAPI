package net.biryeongtrain.jsonDataAPI.impl.gson;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.UnaryOperator;


public class BaseGson {
    private static final @NotNull UnaryOperator<GsonBuilder> KiyoriComponentsGsonBuilder = GsonComponentSerializer.gson().populator();

    public static final Gson GSON = KiyoriComponentsGsonBuilder.apply(new GsonBuilder())
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .registerTypeHierarchyAdapter(Material.class, new RegistrySerializer<>(Registry.MATERIAL))
            .registerTypeHierarchyAdapter(Enchantment.class, new RegistrySerializer<>(Registry.ENCHANTMENT))
            .registerTypeHierarchyAdapter(Sound.class, new RegistrySerializer<>(Registry.SOUNDS))
            .registerTypeHierarchyAdapter(EntityType.class, new RegistrySerializer<>(Registry.ENTITY_TYPE))
//            .registerTypeHierarchyAdapter(TrimMaterial.class, new RegistrySerializer<>(Registry.TRIM_MATERIAL))
//            .registerTypeHierarchyAdapter(TrimPattern.class, new RegistrySerializer<>(Registry.TRIM_MATERIAL))
            .registerTypeHierarchyAdapter(Villager.Profession.class, new RegistrySerializer<>(Registry.VILLAGER_PROFESSION))
            .registerTypeHierarchyAdapter(Villager.Type.class, new RegistrySerializer<>(Registry.VILLAGER_TYPE))
            .registerTypeHierarchyAdapter(PotionEffectType.class, new RegistrySerializer<>(Registry.POTION_EFFECT_TYPE))

            .registerTypeHierarchyAdapter(ItemStack.class, new CodecSerializer<>(ItemStack.CODEC))
            .registerTypeHierarchyAdapter(BlockPos.class, new CodecSerializer<>(BlockPos.CODEC))
            .registerTypeHierarchyAdapter(Vec3.class, new CodecSerializer<>(Vec3.CODEC))

            .registerTypeHierarchyAdapter(Location.class, new LocationSerializer())
            .create();

    private record RegistrySerializer<T extends Keyed>(Registry<T> registry) implements JsonSerializer<T>, JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return this.registry.get(NamespacedKey.fromString(json.getAsString()));
            }
            return null;
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(String.valueOf(this.registry.get(src.getKey())));
        }
    }

    private static class LocationSerializer implements JsonSerializer<Location>, JsonDeserializer<Location> {

        @Override
        public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json instanceof JsonObject jsonObject) {
                Map<String, Object> map = new LinkedHashMap<>();
                jsonObject.asMap().forEach((key, value) -> {
                    switch (key) {
                        case "world" -> map.put("world", value.toString());
                        case "x", "y", "z" -> map.put(key, value.getAsDouble());
                        case "yaw", "pitch" -> map.put(key, value.getAsFloat());
                    }
                });
                return Location.deserialize(map);
            }
            return null;
        }

        @Override
        public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
            var map = src.serialize();
            JsonObject jsonObject = new JsonObject();
            map.forEach((key, value) -> {
                switch (key) {
                    case "world" -> jsonObject.addProperty("world", value.toString());
                    case "x", "z", "y" -> jsonObject.addProperty(key, NumberConversions.toDouble(value));
                    case "yaw", "pitch" -> jsonObject.addProperty(key, NumberConversions.toFloat(value));
                }
            });
            return jsonObject;
        }
    }

    private record CodecSerializer<T>(Codec<T> codec) implements JsonSerializer<T>,JsonDeserializer<T> {

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return this.codec.decode(JsonOps.INSTANCE, json).getOrThrow(false, (x) -> {
                }).getFirst();
            } catch (Throwable e) {
                return null;
            }
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            try {
                return src != null ? this.codec.encodeStart(JsonOps.INSTANCE, src).getOrThrow(false, (x) -> {
                }) :JsonNull.INSTANCE;
            } catch (Throwable e) {
                return JsonNull.INSTANCE;
            }
        }
    }
}



