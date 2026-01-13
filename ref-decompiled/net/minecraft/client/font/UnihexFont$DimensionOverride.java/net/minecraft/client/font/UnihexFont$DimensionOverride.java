/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.UnihexFont;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
static final class UnihexFont.DimensionOverride
extends Record {
    final int from;
    final int to;
    final UnihexFont.Dimensions dimensions;
    private static final Codec<UnihexFont.DimensionOverride> NON_VALIDATED_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.CODEPOINT.fieldOf("from").forGetter(UnihexFont.DimensionOverride::from), (App)Codecs.CODEPOINT.fieldOf("to").forGetter(UnihexFont.DimensionOverride::to), (App)UnihexFont.Dimensions.MAP_CODEC.forGetter(UnihexFont.DimensionOverride::dimensions)).apply((Applicative)instance, UnihexFont.DimensionOverride::new));
    public static final Codec<UnihexFont.DimensionOverride> CODEC = NON_VALIDATED_CODEC.validate(override -> {
        if (override.from >= override.to) {
            return DataResult.error(() -> "Invalid range: [" + dimensionOverride.from + ";" + dimensionOverride.to + "]");
        }
        return DataResult.success((Object)override);
    });

    private UnihexFont.DimensionOverride(int from, int to, UnihexFont.Dimensions dimensions) {
        this.from = from;
        this.to = to;
        this.dimensions = dimensions;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{UnihexFont.DimensionOverride.class, "from;to;dimensions", "from", "to", "dimensions"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{UnihexFont.DimensionOverride.class, "from;to;dimensions", "from", "to", "dimensions"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{UnihexFont.DimensionOverride.class, "from;to;dimensions", "from", "to", "dimensions"}, this, object);
    }

    public int from() {
        return this.from;
    }

    public int to() {
        return this.to;
    }

    public UnihexFont.Dimensions dimensions() {
        return this.dimensions;
    }
}
