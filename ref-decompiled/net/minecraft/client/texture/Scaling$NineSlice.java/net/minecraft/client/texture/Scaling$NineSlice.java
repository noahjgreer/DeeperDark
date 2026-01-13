/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
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
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Scaling;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record Scaling.NineSlice(int width, int height, Border border, boolean stretchInner) implements Scaling
{
    public static final MapCodec<Scaling.NineSlice> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.POSITIVE_INT.fieldOf("width").forGetter(Scaling.NineSlice::width), (App)Codecs.POSITIVE_INT.fieldOf("height").forGetter(Scaling.NineSlice::height), (App)Border.CODEC.fieldOf("border").forGetter(Scaling.NineSlice::border), (App)Codec.BOOL.optionalFieldOf("stretch_inner", (Object)false).forGetter(Scaling.NineSlice::stretchInner)).apply((Applicative)instance, Scaling.NineSlice::new)).validate(Scaling.NineSlice::validate);

    private static DataResult<Scaling.NineSlice> validate(Scaling.NineSlice nineSlice) {
        Border border = nineSlice.border();
        if (border.left() + border.right() >= nineSlice.width()) {
            return DataResult.error(() -> "Nine-sliced texture has no horizontal center slice: " + border.left() + " + " + border.right() + " >= " + nineSlice.width());
        }
        if (border.top() + border.bottom() >= nineSlice.height()) {
            return DataResult.error(() -> "Nine-sliced texture has no vertical center slice: " + border.top() + " + " + border.bottom() + " >= " + nineSlice.height());
        }
        return DataResult.success((Object)nineSlice);
    }

    @Override
    public Scaling.Type getType() {
        return Scaling.Type.NINE_SLICE;
    }

    @Environment(value=EnvType.CLIENT)
    public record Border(int left, int top, int right, int bottom) {
        private static final Codec<Border> UNIFORM_SIDE_SIZES_CODEC = Codecs.POSITIVE_INT.flatComapMap(size -> new Border((int)size, (int)size, (int)size, (int)size), border -> {
            OptionalInt optionalInt = border.getUniformSideSize();
            if (optionalInt.isPresent()) {
                return DataResult.success((Object)optionalInt.getAsInt());
            }
            return DataResult.error(() -> "Border has different side sizes");
        });
        private static final Codec<Border> DIFFERENT_SIDE_SIZES_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.NON_NEGATIVE_INT.fieldOf("left").forGetter(Border::left), (App)Codecs.NON_NEGATIVE_INT.fieldOf("top").forGetter(Border::top), (App)Codecs.NON_NEGATIVE_INT.fieldOf("right").forGetter(Border::right), (App)Codecs.NON_NEGATIVE_INT.fieldOf("bottom").forGetter(Border::bottom)).apply((Applicative)instance, Border::new));
        static final Codec<Border> CODEC = Codec.either(UNIFORM_SIDE_SIZES_CODEC, DIFFERENT_SIDE_SIZES_CODEC).xmap(Either::unwrap, border -> {
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
}
