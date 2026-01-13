/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.item.tint.FireworkTintSource
 *  net.minecraft.client.render.item.tint.TintSource
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.FireworkExplosionComponent
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.dynamic.Codecs
 *  net.minecraft.util.math.ColorHelper
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.tint;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record FireworkTintSource(int defaultColor) implements TintSource
{
    private final int defaultColor;
    public static final MapCodec<FireworkTintSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.RGB.fieldOf("default").forGetter(FireworkTintSource::defaultColor)).apply((Applicative)instance, FireworkTintSource::new));

    public FireworkTintSource() {
        this(-7697782);
    }

    public FireworkTintSource(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
        FireworkExplosionComponent fireworkExplosionComponent = (FireworkExplosionComponent)stack.get(DataComponentTypes.FIREWORK_EXPLOSION);
        IntList intList = fireworkExplosionComponent != null ? fireworkExplosionComponent.colors() : IntList.of();
        int i = intList.size();
        if (i == 0) {
            return this.defaultColor;
        }
        if (i == 1) {
            return ColorHelper.fullAlpha((int)intList.getInt(0));
        }
        int j = 0;
        int k = 0;
        int l = 0;
        for (int m = 0; m < i; ++m) {
            int n = intList.getInt(m);
            j += ColorHelper.getRed((int)n);
            k += ColorHelper.getGreen((int)n);
            l += ColorHelper.getBlue((int)n);
        }
        return ColorHelper.getArgb((int)(j / i), (int)(k / i), (int)(l / i));
    }

    public MapCodec<FireworkTintSource> getCodec() {
        return CODEC;
    }

    public int defaultColor() {
        return this.defaultColor;
    }
}

