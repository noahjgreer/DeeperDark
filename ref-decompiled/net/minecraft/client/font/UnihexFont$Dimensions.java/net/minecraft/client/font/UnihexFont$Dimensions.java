/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class UnihexFont.Dimensions
extends Record {
    final int left;
    final int right;
    public static final MapCodec<UnihexFont.Dimensions> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.INT.fieldOf("left").forGetter(UnihexFont.Dimensions::left), (App)Codec.INT.fieldOf("right").forGetter(UnihexFont.Dimensions::right)).apply((Applicative)instance, UnihexFont.Dimensions::new));
    public static final Codec<UnihexFont.Dimensions> CODEC = MAP_CODEC.codec();

    public UnihexFont.Dimensions(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public int packedValue() {
        return UnihexFont.Dimensions.pack(this.left, this.right);
    }

    public static int pack(int left, int right) {
        return (left & 0xFF) << 8 | right & 0xFF;
    }

    public static int getLeft(int packed) {
        return (byte)(packed >> 8);
    }

    public static int getRight(int packed) {
        return (byte)packed;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{UnihexFont.Dimensions.class, "left;right", "left", "right"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{UnihexFont.Dimensions.class, "left;right", "left", "right"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{UnihexFont.Dimensions.class, "left;right", "left", "right"}, this, object);
    }

    public int left() {
        return this.left;
    }

    public int right() {
        return this.right;
    }
}
