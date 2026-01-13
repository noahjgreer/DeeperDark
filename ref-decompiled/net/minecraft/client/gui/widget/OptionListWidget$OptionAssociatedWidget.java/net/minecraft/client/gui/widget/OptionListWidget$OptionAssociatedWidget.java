/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.SimpleOption;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static final class OptionListWidget.OptionAssociatedWidget
extends Record {
    private final ClickableWidget widget;
    final @Nullable SimpleOption<?> optionInstance;

    public OptionListWidget.OptionAssociatedWidget(ClickableWidget widget) {
        this(widget, null);
    }

    public OptionListWidget.OptionAssociatedWidget(ClickableWidget widget, @Nullable SimpleOption<?> optionInstance) {
        this.widget = widget;
        this.optionInstance = optionInstance;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{OptionListWidget.OptionAssociatedWidget.class, "widget;optionInstance", "widget", "optionInstance"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{OptionListWidget.OptionAssociatedWidget.class, "widget;optionInstance", "widget", "optionInstance"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{OptionListWidget.OptionAssociatedWidget.class, "widget;optionInstance", "widget", "optionInstance"}, this, object);
    }

    public ClickableWidget widget() {
        return this.widget;
    }

    public @Nullable SimpleOption<?> optionInstance() {
        return this.optionInstance;
    }
}
