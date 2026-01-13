/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector2i
 */
package net.minecraft.client.gui.screen.ingame;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import org.joml.Vector2i;

@Environment(value=EnvType.CLIENT)
static final class HandledScreen.LetGoTouchStack
extends Record {
    final ItemStack item;
    final Vector2i start;
    final Vector2i end;
    final long time;

    HandledScreen.LetGoTouchStack(ItemStack item, Vector2i start, Vector2i end, long time) {
        this.item = item;
        this.start = start;
        this.end = end;
        this.time = time;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{HandledScreen.LetGoTouchStack.class, "item;start;end;time", "item", "start", "end", "time"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{HandledScreen.LetGoTouchStack.class, "item;start;end;time", "item", "start", "end", "time"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{HandledScreen.LetGoTouchStack.class, "item;start;end;time", "item", "start", "end", "time"}, this, object);
    }

    public ItemStack item() {
        return this.item;
    }

    public Vector2i start() {
        return this.start;
    }

    public Vector2i end() {
        return this.end;
    }

    public long time() {
        return this.time;
    }
}
