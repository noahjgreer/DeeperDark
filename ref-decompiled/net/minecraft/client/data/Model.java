/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Streams
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.Block
 *  net.minecraft.client.data.Model
 *  net.minecraft.client.data.ModelIds
 *  net.minecraft.client.data.ModelSupplier
 *  net.minecraft.client.data.TextureKey
 *  net.minecraft.client.data.TextureMap
 *  net.minecraft.item.Item
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.data.ModelIds;
import net.minecraft.client.data.ModelSupplier;
import net.minecraft.client.data.TextureKey;
import net.minecraft.client.data.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class Model {
    private final Optional<Identifier> parent;
    private final Set<TextureKey> requiredTextures;
    private final Optional<String> variant;

    public Model(Optional<Identifier> parent, Optional<String> variant, TextureKey ... requiredTextureKeys) {
        this.parent = parent;
        this.variant = variant;
        this.requiredTextures = ImmutableSet.copyOf((Object[])requiredTextureKeys);
    }

    public Identifier getBlockSubModelId(Block block) {
        return ModelIds.getBlockSubModelId((Block)block, (String)this.variant.orElse(""));
    }

    public Identifier upload(Block block, TextureMap textures, BiConsumer<Identifier, ModelSupplier> modelCollector) {
        return this.upload(ModelIds.getBlockSubModelId((Block)block, (String)this.variant.orElse("")), textures, modelCollector);
    }

    public Identifier upload(Block block, String suffix, TextureMap textures, BiConsumer<Identifier, ModelSupplier> modelCollector) {
        return this.upload(ModelIds.getBlockSubModelId((Block)block, (String)(suffix + this.variant.orElse(""))), textures, modelCollector);
    }

    public Identifier uploadWithoutVariant(Block block, String suffix, TextureMap textures, BiConsumer<Identifier, ModelSupplier> modelCollector) {
        return this.upload(ModelIds.getBlockSubModelId((Block)block, (String)suffix), textures, modelCollector);
    }

    public Identifier upload(Item item, TextureMap textures, BiConsumer<Identifier, ModelSupplier> modelCollector) {
        return this.upload(ModelIds.getItemSubModelId((Item)item, (String)this.variant.orElse("")), textures, modelCollector);
    }

    public Identifier upload(Identifier id, TextureMap textures, BiConsumer<Identifier, ModelSupplier> modelCollector) {
        Map map = this.createTextureMap(textures);
        modelCollector.accept(id, () -> {
            JsonObject jsonObject = new JsonObject();
            this.parent.ifPresent(identifier -> jsonObject.addProperty("parent", identifier.toString()));
            if (!map.isEmpty()) {
                JsonObject jsonObject2 = new JsonObject();
                map.forEach((textureKey, identifier) -> jsonObject2.addProperty(textureKey.getName(), identifier.toString()));
                jsonObject.add("textures", (JsonElement)jsonObject2);
            }
            return jsonObject;
        });
        return id;
    }

    private Map<TextureKey, Identifier> createTextureMap(TextureMap textures) {
        return (Map)Streams.concat((Stream[])new Stream[]{this.requiredTextures.stream(), textures.getInherited()}).collect(ImmutableMap.toImmutableMap(Function.identity(), arg_0 -> ((TextureMap)textures).getTexture(arg_0)));
    }
}

