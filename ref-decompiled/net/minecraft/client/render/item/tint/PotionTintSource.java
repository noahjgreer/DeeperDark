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
 *  net.minecraft.client.render.item.tint.PotionTintSource
 *  net.minecraft.client.render.item.tint.TintSource
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.PotionContentsComponent
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
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record PotionTintSource(int defaultColor) implements TintSource
{
    private final int defaultColor;
    public static final MapCodec<PotionTintSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.RGB.fieldOf("default").forGetter(PotionTintSource::defaultColor)).apply((Applicative)instance, PotionTintSource::new));

    public PotionTintSource() {
        this(-13083194);
    }

    public PotionTintSource(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
        PotionContentsComponent potionContentsComponent = (PotionContentsComponent)stack.get(DataComponentTypes.POTION_CONTENTS);
        if (potionContentsComponent != null) {
            return ColorHelper.fullAlpha((int)potionContentsComponent.getColor(this.defaultColor));
        }
        return ColorHelper.fullAlpha((int)this.defaultColor);
    }

    public MapCodec<PotionTintSource> getCodec() {
        return CODEC;
    }

    public int defaultColor() {
        return this.defaultColor;
    }
}

