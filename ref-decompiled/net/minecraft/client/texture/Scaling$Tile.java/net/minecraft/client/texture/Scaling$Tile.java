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
 */
package net.minecraft.client.texture;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Scaling;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record Scaling.Tile(int width, int height) implements Scaling
{
    public static final MapCodec<Scaling.Tile> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.POSITIVE_INT.fieldOf("width").forGetter(Scaling.Tile::width), (App)Codecs.POSITIVE_INT.fieldOf("height").forGetter(Scaling.Tile::height)).apply((Applicative)instance, Scaling.Tile::new));

    @Override
    public Scaling.Type getType() {
        return Scaling.Type.TILE;
    }
}
