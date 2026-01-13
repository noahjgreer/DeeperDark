/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.item.property.numeric.CrossbowPullProperty
 *  net.minecraft.client.render.item.property.numeric.NumericProperty
 *  net.minecraft.client.render.item.property.numeric.UseDurationProperty
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.CrossbowItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.HeldItemContext
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.property.numeric;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.render.item.property.numeric.UseDurationProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CrossbowPullProperty
implements NumericProperty {
    public static final MapCodec<CrossbowPullProperty> CODEC = MapCodec.unit((Object)new CrossbowPullProperty());

    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable HeldItemContext context, int seed) {
        LivingEntity livingEntity;
        LivingEntity livingEntity2 = livingEntity = context == null ? null : context.getEntity();
        if (livingEntity == null) {
            return 0.0f;
        }
        if (CrossbowItem.isCharged((ItemStack)stack)) {
            return 0.0f;
        }
        int i = CrossbowItem.getPullTime((ItemStack)stack, (LivingEntity)livingEntity);
        return (float)UseDurationProperty.getTicksUsedSoFar((ItemStack)stack, (LivingEntity)livingEntity) / (float)i;
    }

    public MapCodec<CrossbowPullProperty> getCodec() {
        return CODEC;
    }
}

