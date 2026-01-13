/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.item.property.numeric.CooldownProperty
 *  net.minecraft.client.render.item.property.numeric.NumericProperty
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.HeldItemContext
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.property.numeric;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record CooldownProperty() implements NumericProperty
{
    public static final MapCodec<CooldownProperty> CODEC = MapCodec.unit((Object)new CooldownProperty());

    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable HeldItemContext context, int seed) {
        float f;
        LivingEntity livingEntity;
        if (context != null && (livingEntity = context.getEntity()) instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)livingEntity;
            f = playerEntity.getItemCooldownManager().getCooldownProgress(stack, 0.0f);
        } else {
            f = 0.0f;
        }
        return f;
    }

    public MapCodec<CooldownProperty> getCodec() {
        return CODEC;
    }
}

