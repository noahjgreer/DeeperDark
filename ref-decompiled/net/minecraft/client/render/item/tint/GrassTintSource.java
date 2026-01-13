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
 *  net.minecraft.client.render.item.tint.GrassTintSource
 *  net.minecraft.client.render.item.tint.TintSource
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.dynamic.Codecs
 *  net.minecraft.world.biome.GrassColors
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.tint;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.biome.GrassColors;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record GrassTintSource(float temperature, float downfall) implements TintSource
{
    private final float temperature;
    private final float downfall;
    public static final MapCodec<GrassTintSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.rangedInclusiveFloat((float)0.0f, (float)1.0f).fieldOf("temperature").forGetter(GrassTintSource::temperature), (App)Codecs.rangedInclusiveFloat((float)0.0f, (float)1.0f).fieldOf("downfall").forGetter(GrassTintSource::downfall)).apply((Applicative)instance, GrassTintSource::new));

    public GrassTintSource() {
        this(0.5f, 1.0f);
    }

    public GrassTintSource(float temperature, float downfall) {
        this.temperature = temperature;
        this.downfall = downfall;
    }

    public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
        return GrassColors.getColor((double)this.temperature, (double)this.downfall);
    }

    public MapCodec<GrassTintSource> getCodec() {
        return CODEC;
    }

    public float temperature() {
        return this.temperature;
    }

    public float downfall() {
        return this.downfall;
    }
}

