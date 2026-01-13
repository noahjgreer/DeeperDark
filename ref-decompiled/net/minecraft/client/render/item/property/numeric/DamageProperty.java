/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.item.property.numeric.DamageProperty
 *  net.minecraft.client.render.item.property.numeric.NumericProperty
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.HeldItemContext
 *  net.minecraft.util.math.MathHelper
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.property.numeric;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record DamageProperty(boolean normalize) implements NumericProperty
{
    private final boolean normalize;
    public static final MapCodec<DamageProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.BOOL.optionalFieldOf("normalize", (Object)true).forGetter(DamageProperty::normalize)).apply((Applicative)instance, DamageProperty::new));

    public DamageProperty(boolean normalize) {
        this.normalize = normalize;
    }

    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable HeldItemContext context, int seed) {
        float f = stack.getDamage();
        float g = stack.getMaxDamage();
        if (this.normalize) {
            return MathHelper.clamp((float)(f / g), (float)0.0f, (float)1.0f);
        }
        return MathHelper.clamp((float)f, (float)0.0f, (float)g);
    }

    public MapCodec<DamageProperty> getCodec() {
        return CODEC;
    }

    public boolean normalize() {
        return this.normalize;
    }
}

