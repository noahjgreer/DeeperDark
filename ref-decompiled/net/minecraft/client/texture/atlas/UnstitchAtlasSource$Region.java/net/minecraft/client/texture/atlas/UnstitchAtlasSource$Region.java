/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture.atlas;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public static final class UnstitchAtlasSource.Region
extends Record {
    final Identifier sprite;
    final double x;
    final double y;
    final double width;
    final double height;
    public static final Codec<UnstitchAtlasSource.Region> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Identifier.CODEC.fieldOf("sprite").forGetter(UnstitchAtlasSource.Region::sprite), (App)Codec.DOUBLE.fieldOf("x").forGetter(UnstitchAtlasSource.Region::x), (App)Codec.DOUBLE.fieldOf("y").forGetter(UnstitchAtlasSource.Region::y), (App)Codec.DOUBLE.fieldOf("width").forGetter(UnstitchAtlasSource.Region::width), (App)Codec.DOUBLE.fieldOf("height").forGetter(UnstitchAtlasSource.Region::height)).apply((Applicative)instance, UnstitchAtlasSource.Region::new));

    public UnstitchAtlasSource.Region(Identifier sprite, double x, double y, double width, double height) {
        this.sprite = sprite;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{UnstitchAtlasSource.Region.class, "sprite;x;y;width;height", "sprite", "x", "y", "width", "height"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{UnstitchAtlasSource.Region.class, "sprite;x;y;width;height", "sprite", "x", "y", "width", "height"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{UnstitchAtlasSource.Region.class, "sprite;x;y;width;height", "sprite", "x", "y", "width", "height"}, this, object);
    }

    public Identifier sprite() {
        return this.sprite;
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public double width() {
        return this.width;
    }

    public double height() {
        return this.height;
    }
}
