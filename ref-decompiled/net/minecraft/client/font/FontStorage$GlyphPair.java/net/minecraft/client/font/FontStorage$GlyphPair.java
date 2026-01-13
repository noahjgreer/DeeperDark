/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;

@Environment(value=EnvType.CLIENT)
record FontStorage.GlyphPair(Supplier<BakedGlyph> any, Supplier<BakedGlyph> advanceValidating) {
    Supplier<BakedGlyph> get(boolean advanceValidating) {
        return advanceValidating ? this.advanceValidating : this.any;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{FontStorage.GlyphPair.class, "any;nonFishy", "any", "advanceValidating"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{FontStorage.GlyphPair.class, "any;nonFishy", "any", "advanceValidating"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{FontStorage.GlyphPair.class, "any;nonFishy", "any", "advanceValidating"}, this, object);
    }
}
