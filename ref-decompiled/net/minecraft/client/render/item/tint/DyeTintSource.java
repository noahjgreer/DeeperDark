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
 *  net.minecraft.client.render.item.tint.DyeTintSource
 *  net.minecraft.client.render.item.tint.TintSource
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.component.type.DyedColorComponent
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.dynamic.Codecs
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
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record DyeTintSource(int defaultColor) implements TintSource
{
    private final int defaultColor;
    public static final MapCodec<DyeTintSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.RGB.fieldOf("default").forGetter(DyeTintSource::defaultColor)).apply((Applicative)instance, DyeTintSource::new));

    public DyeTintSource(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
        return DyedColorComponent.getColor((ItemStack)stack, (int)this.defaultColor);
    }

    public MapCodec<DyeTintSource> getCodec() {
        return CODEC;
    }

    public int defaultColor() {
        return this.defaultColor;
    }
}

