/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.item.property.select.DisplayContextProperty
 *  net.minecraft.client.render.item.property.select.SelectProperty
 *  net.minecraft.client.render.item.property.select.SelectProperty$Type
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.property.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record DisplayContextProperty() implements SelectProperty<ItemDisplayContext>
{
    public static final Codec<ItemDisplayContext> VALUE_CODEC = ItemDisplayContext.CODEC;
    public static final SelectProperty.Type<DisplayContextProperty, ItemDisplayContext> TYPE = SelectProperty.Type.create((MapCodec)MapCodec.unit((Object)new DisplayContextProperty()), (Codec)VALUE_CODEC);

    public ItemDisplayContext getValue(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        return itemDisplayContext;
    }

    public SelectProperty.Type<DisplayContextProperty, ItemDisplayContext> getType() {
        return TYPE;
    }

    public Codec<ItemDisplayContext> valueCodec() {
        return VALUE_CODEC;
    }

    public /* synthetic */ Object getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ItemDisplayContext displayContext) {
        return this.getValue(stack, world, user, seed, displayContext);
    }
}

