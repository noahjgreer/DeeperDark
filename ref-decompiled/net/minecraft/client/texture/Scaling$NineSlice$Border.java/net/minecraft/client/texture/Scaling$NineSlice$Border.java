/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record Scaling.NineSlice.Border(int left, int top, int right, int bottom) {
    private static final Codec<Scaling.NineSlice.Border> UNIFORM_SIDE_SIZES_CODEC = Codecs.POSITIVE_INT.flatComapMap(size -> new Scaling.NineSlice.Border((int)size, (int)size, (int)size, (int)size), border -> {
        OptionalInt optionalInt = border.getUniformSideSize();
        if (optionalInt.isPresent()) {
            return DataResult.success((Object)optionalInt.getAsInt());
        }
        return DataResult.error(() -> "Border has different side sizes");
    });
    private static final Codec<Scaling.NineSlice.Border> DIFFERENT_SIDE_SIZES_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.NON_NEGATIVE_INT.fieldOf("left").forGetter(Scaling.NineSlice.Border::left), (App)Codecs.NON_NEGATIVE_INT.fieldOf("top").forGetter(Scaling.NineSlice.Border::top), (App)Codecs.NON_NEGATIVE_INT.fieldOf("right").forGetter(Scaling.NineSlice.Border::right), (App)Codecs.NON_NEGATIVE_INT.fieldOf("bottom").forGetter(Scaling.NineSlice.Border::bottom)).apply((Applicative)instance, Scaling.NineSlice.Border::new));
    static final Codec<Scaling.NineSlice.Border> CODEC = Codec.either(UNIFORM_SIDE_SIZES_CODEC, DIFFERENT_SIDE_SIZES_CODEC).xmap(Either::unwrap, border -> {
        if (border.getUniformSideSize().isPresent()) {
            return Either.left((Object)border);
        }
        return Either.right((Object)border);
    });

    private OptionalInt getUniformSideSize() {
        if (this.left() == this.top() && this.top() == this.right() && this.right() == this.bottom()) {
            return OptionalInt.of(this.left());
        }
        return OptionalInt.empty();
    }
}
