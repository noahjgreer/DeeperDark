/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.model;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.ResolvableModel;

@Environment(value=EnvType.CLIENT)
public record BundleSelectedItemModel.Unbaked() implements ItemModel.Unbaked
{
    public static final MapCodec<BundleSelectedItemModel.Unbaked> CODEC = MapCodec.unit((Object)new BundleSelectedItemModel.Unbaked());

    public MapCodec<BundleSelectedItemModel.Unbaked> getCodec() {
        return CODEC;
    }

    @Override
    public ItemModel bake(ItemModel.BakeContext context) {
        return INSTANCE;
    }

    @Override
    public void resolve(ResolvableModel.Resolver resolver) {
    }
}
