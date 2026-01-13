/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.item.property.numeric.NumericProperty
 *  net.minecraft.client.render.item.property.numeric.UseCycleProperty
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.HeldItemContext
 *  net.minecraft.util.dynamic.Codecs
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.property.numeric;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.dynamic.Codecs;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record UseCycleProperty(float period) implements NumericProperty
{
    private final float period;
    public static final MapCodec<UseCycleProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.POSITIVE_FLOAT.optionalFieldOf("period", (Object)Float.valueOf(1.0f)).forGetter(UseCycleProperty::period)).apply((Applicative)instance, UseCycleProperty::new));

    public UseCycleProperty(float period) {
        this.period = period;
    }

    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable HeldItemContext context, int seed) {
        LivingEntity livingEntity;
        LivingEntity livingEntity2 = livingEntity = context == null ? null : context.getEntity();
        if (livingEntity == null || livingEntity.getActiveItem() != stack) {
            return 0.0f;
        }
        return (float)livingEntity.getItemUseTimeLeft() % this.period;
    }

    public MapCodec<UseCycleProperty> getCodec() {
        return CODEC;
    }

    public float period() {
        return this.period;
    }
}

