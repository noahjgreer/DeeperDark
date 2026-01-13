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
 *  net.minecraft.client.render.item.tint.MapColorTintSource
 *  net.minecraft.client.render.item.tint.TintSource
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.MapColorComponent
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapColorComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record MapColorTintSource(int defaultColor) implements TintSource
{
    private final int defaultColor;
    public static final MapCodec<MapColorTintSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.RGB.fieldOf("default").forGetter(MapColorTintSource::defaultColor)).apply((Applicative)instance, MapColorTintSource::new));

    public MapColorTintSource() {
        this(MapColorComponent.DEFAULT.rgb());
    }

    public MapColorTintSource(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
        MapColorComponent mapColorComponent = (MapColorComponent)stack.get(DataComponentTypes.MAP_COLOR);
        if (mapColorComponent != null) {
            return ColorHelper.fullAlpha((int)mapColorComponent.rgb());
        }
        return ColorHelper.fullAlpha((int)this.defaultColor);
    }

    public MapCodec<MapColorTintSource> getCodec() {
        return CODEC;
    }

    public int defaultColor() {
        return this.defaultColor;
    }
}

