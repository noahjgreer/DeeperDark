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
 *  net.minecraft.client.render.item.tint.CustomModelDataTintSource
 *  net.minecraft.client.render.item.tint.TintSource
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.CustomModelDataComponent
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
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record CustomModelDataTintSource(int index, int defaultColor) implements TintSource
{
    private final int index;
    private final int defaultColor;
    public static final MapCodec<CustomModelDataTintSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.NON_NEGATIVE_INT.optionalFieldOf("index", (Object)0).forGetter(CustomModelDataTintSource::index), (App)Codecs.RGB.fieldOf("default").forGetter(CustomModelDataTintSource::defaultColor)).apply((Applicative)instance, CustomModelDataTintSource::new));

    public CustomModelDataTintSource(int index, int defaultColor) {
        this.index = index;
        this.defaultColor = defaultColor;
    }

    public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
        Integer integer;
        CustomModelDataComponent customModelDataComponent = (CustomModelDataComponent)stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        if (customModelDataComponent != null && (integer = customModelDataComponent.getColor(this.index)) != null) {
            return ColorHelper.fullAlpha((int)integer);
        }
        return ColorHelper.fullAlpha((int)this.defaultColor);
    }

    public MapCodec<CustomModelDataTintSource> getCodec() {
        return CODEC;
    }

    public int index() {
        return this.index;
    }

    public int defaultColor() {
        return this.defaultColor;
    }
}

