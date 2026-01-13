/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.option;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
record TelemetryEventWidget.Contents(LayoutWidget grid, Text narration) {
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{TelemetryEventWidget.Contents.class, "container;narration", "grid", "narration"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TelemetryEventWidget.Contents.class, "container;narration", "grid", "narration"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TelemetryEventWidget.Contents.class, "container;narration", "grid", "narration"}, this, object);
    }
}
