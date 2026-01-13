/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.FishingBobberEntityRenderer
 *  net.minecraft.client.render.item.property.bool.BooleanProperty
 *  net.minecraft.client.render.item.property.bool.FishingRodCastProperty
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Arm
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.property.bool;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record FishingRodCastProperty() implements BooleanProperty
{
    public static final MapCodec<FishingRodCastProperty> CODEC = MapCodec.unit((Object)new FishingRodCastProperty());

    public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)entity;
            if (playerEntity.fishHook != null) {
                Arm arm = FishingBobberEntityRenderer.getArmHoldingRod((PlayerEntity)playerEntity);
                return entity.getStackInArm(arm) == stack;
            }
        }
        return false;
    }

    public MapCodec<FishingRodCastProperty> getCodec() {
        return CODEC;
    }
}

