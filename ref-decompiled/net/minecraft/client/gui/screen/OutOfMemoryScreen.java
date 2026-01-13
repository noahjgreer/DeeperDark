/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.screen.OutOfMemoryScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.TitleScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.NarratedMultilineTextWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class OutOfMemoryScreen
extends Screen {
    private static final Text TITLE = Text.translatable((String)"outOfMemory.title");
    private static final Text MESSAGE = Text.translatable((String)"outOfMemory.message");
    private static final int MAX_TEXT_WIDTH = 300;
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);

    public OutOfMemoryScreen() {
        super(TITLE);
    }

    protected void init() {
        this.layout.addHeader(TITLE, this.textRenderer);
        this.layout.addBody((Widget)NarratedMultilineTextWidget.builder((Text)MESSAGE, (TextRenderer)this.textRenderer).width(300).build());
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addFooter((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)ScreenTexts.TO_TITLE, button -> this.client.setScreen((Screen)new TitleScreen())).build());
        directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"menu.quit"), button -> this.client.scheduleStop()).build());
        this.layout.forEachChild(arg_0 -> ((OutOfMemoryScreen)this).addDrawableChild(arg_0));
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }
}

