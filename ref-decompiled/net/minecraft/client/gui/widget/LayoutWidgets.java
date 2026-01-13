/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.LayoutWidget
 *  net.minecraft.client.gui.widget.LayoutWidgets
 *  net.minecraft.client.gui.widget.Positioner
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.widget;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class LayoutWidgets {
    private static final int SPACING = 4;

    private LayoutWidgets() {
    }

    public static LayoutWidget createLabeledWidget(TextRenderer textRenderer, Widget widget, Text label) {
        return LayoutWidgets.createLabeledWidget((TextRenderer)textRenderer, (Widget)widget, (Text)label, (T positioner) -> {});
    }

    public static LayoutWidget createLabeledWidget(TextRenderer textRenderer, Widget widget, Text label, Consumer<Positioner> callback) {
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.vertical().spacing(4);
        directionalLayoutWidget.add((Widget)new TextWidget(label, textRenderer));
        directionalLayoutWidget.add(widget, callback);
        return directionalLayoutWidget;
    }
}

