/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static final class SpriteContents.AnimationFrame
extends Record {
    final int index;
    final int time;

    SpriteContents.AnimationFrame(int index, int time) {
        this.index = index;
        this.time = time;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SpriteContents.AnimationFrame.class, "index;time", "index", "time"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SpriteContents.AnimationFrame.class, "index;time", "index", "time"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SpriteContents.AnimationFrame.class, "index;time", "index", "time"}, this, object);
    }

    public int index() {
        return this.index;
    }

    public int time() {
        return this.time;
    }
}
