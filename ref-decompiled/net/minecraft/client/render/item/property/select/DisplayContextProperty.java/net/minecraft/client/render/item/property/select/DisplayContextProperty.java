/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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
    public static final SelectProperty.Type<DisplayContextProperty, ItemDisplayContext> TYPE = SelectProperty.Type.create(MapCodec.unit((Object)new DisplayContextProperty()), VALUE_CODEC);

    @Override
    public ItemDisplayContext getValue(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        return itemDisplayContext;
    }

    @Override
    public SelectProperty.Type<DisplayContextProperty, ItemDisplayContext> getType() {
        return TYPE;
    }

    @Override
    public Codec<ItemDisplayContext> valueCodec() {
        return VALUE_CODEC;
    }

    @Override
    public /* synthetic */ Object getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ItemDisplayContext displayContext) {
        return this.getValue(stack, world, user, seed, displayContext);
    }
}
