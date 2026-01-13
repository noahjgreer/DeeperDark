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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.OrderedText;

@Environment(value=EnvType.CLIENT)
public static final class MultilineText.Line
extends Record {
    final OrderedText text;
    final int width;

    public MultilineText.Line(OrderedText text, int width) {
        this.text = text;
        this.width = width;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MultilineText.Line.class, "text;width", "text", "width"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MultilineText.Line.class, "text;width", "text", "width"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MultilineText.Line.class, "text;width", "text", "width"}, this, object);
    }

    public OrderedText text() {
        return this.text;
    }

    public int width() {
        return this.width;
    }
}
