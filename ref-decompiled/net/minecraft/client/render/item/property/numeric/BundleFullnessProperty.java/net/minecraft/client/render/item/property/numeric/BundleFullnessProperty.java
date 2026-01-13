/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.property.numeric;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record BundleFullnessProperty() implements NumericProperty
{
    public static final MapCodec<BundleFullnessProperty> CODEC = MapCodec.unit((Object)new BundleFullnessProperty());

    @Override
    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable HeldItemContext context, int seed) {
        return BundleItem.getAmountFilled(stack);
    }

    public MapCodec<BundleFullnessProperty> getCodec() {
        return CODEC;
    }
}
