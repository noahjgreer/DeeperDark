/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemAsset
 *  net.minecraft.client.item.ItemAsset$Properties
 *  net.minecraft.client.render.item.model.ItemModel$Unbaked
 *  net.minecraft.client.render.item.model.ItemModelTypes
 *  net.minecraft.registry.ContextSwapper
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.item;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.registry.ContextSwapper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ItemAsset(ItemModel.Unbaked model, Properties properties, @Nullable ContextSwapper registrySwapper) {
    private final ItemModel.Unbaked model;
    private final Properties properties;
    private final @Nullable ContextSwapper registrySwapper;
    public static final Codec<ItemAsset> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ItemModelTypes.CODEC.fieldOf("model").forGetter(ItemAsset::model), (App)Properties.CODEC.forGetter(ItemAsset::properties)).apply((Applicative)instance, ItemAsset::new));

    public ItemAsset(ItemModel.Unbaked model, Properties properties) {
        this(model, properties, null);
    }

    public ItemAsset(ItemModel.Unbaked model, Properties properties, @Nullable ContextSwapper registrySwapper) {
        this.model = model;
        this.properties = properties;
        this.registrySwapper = registrySwapper;
    }

    public ItemAsset withContextSwapper(ContextSwapper contextSwapper) {
        return new ItemAsset(this.model, this.properties, contextSwapper);
    }

    public ItemModel.Unbaked model() {
        return this.model;
    }

    public Properties properties() {
        return this.properties;
    }

    public @Nullable ContextSwapper registrySwapper() {
        return this.registrySwapper;
    }
}

