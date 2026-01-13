/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.decoration;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.Text;

public static final class DisplayEntity.TextDisplayEntity.Data
extends Record {
    private final Text text;
    private final int lineWidth;
    final DisplayEntity.IntLerper textOpacity;
    final DisplayEntity.IntLerper backgroundColor;
    private final byte flags;

    public DisplayEntity.TextDisplayEntity.Data(Text text, int lineWidth, DisplayEntity.IntLerper textOpacity, DisplayEntity.IntLerper backgroundColor, byte flags) {
        this.text = text;
        this.lineWidth = lineWidth;
        this.textOpacity = textOpacity;
        this.backgroundColor = backgroundColor;
        this.flags = flags;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{DisplayEntity.TextDisplayEntity.Data.class, "text;lineWidth;textOpacity;backgroundColor;flags", "text", "lineWidth", "textOpacity", "backgroundColor", "flags"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{DisplayEntity.TextDisplayEntity.Data.class, "text;lineWidth;textOpacity;backgroundColor;flags", "text", "lineWidth", "textOpacity", "backgroundColor", "flags"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{DisplayEntity.TextDisplayEntity.Data.class, "text;lineWidth;textOpacity;backgroundColor;flags", "text", "lineWidth", "textOpacity", "backgroundColor", "flags"}, this, object);
    }

    public Text text() {
        return this.text;
    }

    public int lineWidth() {
        return this.lineWidth;
    }

    public DisplayEntity.IntLerper textOpacity() {
        return this.textOpacity;
    }

    public DisplayEntity.IntLerper backgroundColor() {
        return this.backgroundColor;
    }

    public byte flags() {
        return this.flags;
    }
}
