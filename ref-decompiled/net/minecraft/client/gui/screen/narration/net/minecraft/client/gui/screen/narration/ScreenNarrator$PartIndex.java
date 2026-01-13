/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.narration;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.NarrationPart;

@Environment(value=EnvType.CLIENT)
static final class ScreenNarrator.PartIndex
extends Record {
    final NarrationPart part;
    final int depth;

    ScreenNarrator.PartIndex(NarrationPart part, int depth) {
        this.part = part;
        this.depth = depth;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ScreenNarrator.PartIndex.class, "type;depth", "part", "depth"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ScreenNarrator.PartIndex.class, "type;depth", "part", "depth"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ScreenNarrator.PartIndex.class, "type;depth", "part", "depth"}, this, object);
    }

    public NarrationPart part() {
        return this.part;
    }

    public int depth() {
        return this.depth;
    }
}
