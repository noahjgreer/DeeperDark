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
 *  net.minecraft.client.render.item.tint.TeamTintSource
 *  net.minecraft.client.render.item.tint.TintSource
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.scoreboard.Team
 *  net.minecraft.util.Formatting
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record TeamTintSource(int defaultColor) implements TintSource
{
    private final int defaultColor;
    public static final MapCodec<TeamTintSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.RGB.fieldOf("default").forGetter(TeamTintSource::defaultColor)).apply((Applicative)instance, TeamTintSource::new));

    public TeamTintSource(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
        Formatting formatting;
        Team abstractTeam;
        if (user != null && (abstractTeam = user.getScoreboardTeam()) != null && (formatting = abstractTeam.getColor()).getColorValue() != null) {
            return ColorHelper.fullAlpha((int)formatting.getColorValue());
        }
        return ColorHelper.fullAlpha((int)this.defaultColor);
    }

    public MapCodec<TeamTintSource> getCodec() {
        return CODEC;
    }

    public int defaultColor() {
        return this.defaultColor;
    }
}

