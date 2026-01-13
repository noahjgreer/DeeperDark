/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.FontType;
import net.minecraft.client.font.SpaceFont;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record SpaceFont.Loader(Map<Integer, Float> advances) implements FontLoader
{
    public static final MapCodec<SpaceFont.Loader> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.unboundedMap(Codecs.CODEPOINT, (Codec)Codec.FLOAT).fieldOf("advances").forGetter(SpaceFont.Loader::advances)).apply((Applicative)instance, SpaceFont.Loader::new));

    @Override
    public FontType getType() {
        return FontType.SPACE;
    }

    @Override
    public Either<FontLoader.Loadable, FontLoader.Reference> build() {
        FontLoader.Loadable loadable = resourceManager -> new SpaceFont(this.advances);
        return Either.left((Object)loadable);
    }
}
