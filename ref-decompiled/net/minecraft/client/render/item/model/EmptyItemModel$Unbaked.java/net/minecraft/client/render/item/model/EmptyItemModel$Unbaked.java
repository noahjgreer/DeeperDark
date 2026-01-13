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
public record EmptyItemModel.Unbaked() implements ItemModel.Unbaked
{
    public static final MapCodec<EmptyItemModel.Unbaked> CODEC = MapCodec.unit(EmptyItemModel.Unbaked::new);

    @Override
    public void resolve(ResolvableModel.Resolver resolver) {
    }

    @Override
    public ItemModel bake(ItemModel.BakeContext context) {
        return INSTANCE;
    }

    public MapCodec<EmptyItemModel.Unbaked> getCodec() {
        return CODEC;
    }
}
