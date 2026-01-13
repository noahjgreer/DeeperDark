/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
static final class MultilineTextWidget.CacheKey
extends Record {
    final Text message;
    final int maxWidth;
    final OptionalInt maxRows;

    MultilineTextWidget.CacheKey(Text message, int maxWidth, OptionalInt maxRows) {
        this.message = message;
        this.maxWidth = maxWidth;
        this.maxRows = maxRows;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MultilineTextWidget.CacheKey.class, "message;maxWidth;maxRows", "message", "maxWidth", "maxRows"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MultilineTextWidget.CacheKey.class, "message;maxWidth;maxRows", "message", "maxWidth", "maxRows"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MultilineTextWidget.CacheKey.class, "message;maxWidth;maxRows", "message", "maxWidth", "maxRows"}, this, object);
    }

    public Text message() {
        return this.message;
    }

    public int maxWidth() {
        return this.maxWidth;
    }

    public OptionalInt maxRows() {
        return this.maxRows;
    }
}
