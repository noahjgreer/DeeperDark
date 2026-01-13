/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ButtonWidget$Builder
 *  net.minecraft.client.gui.widget.ButtonWidget$NarrationSupplier
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 *  net.minecraft.client.gui.widget.PressableWidget
 *  net.minecraft.client.input.AbstractInput
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public abstract class ButtonWidget
extends PressableWidget {
    public static final int DEFAULT_WIDTH_SMALL = 120;
    public static final int DEFAULT_WIDTH = 150;
    public static final int field_49479 = 200;
    public static final int DEFAULT_HEIGHT = 20;
    public static final int field_46856 = 8;
    protected static final NarrationSupplier DEFAULT_NARRATION_SUPPLIER = textSupplier -> (MutableText)textSupplier.get();
    protected final PressAction onPress;
    protected final NarrationSupplier narrationSupplier;

    public static Builder builder(Text message, PressAction onPress) {
        return new Builder(message, onPress);
    }

    protected ButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
        super(x, y, width, height, message);
        this.onPress = onPress;
        this.narrationSupplier = narrationSupplier;
    }

    public void onPress(AbstractInput input) {
        this.onPress.onPress(this);
    }

    protected MutableText getNarrationMessage() {
        return this.narrationSupplier.createNarrationMessage(() -> super.getNarrationMessage());
    }

    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }
}

