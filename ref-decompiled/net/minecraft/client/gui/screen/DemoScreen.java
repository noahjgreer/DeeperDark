/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.Alignment
 *  net.minecraft.client.font.DrawnTextConsumer
 *  net.minecraft.client.font.MultilineText
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.DemoScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Urls
 *  net.minecraft.util.Util
 */
package net.minecraft.client.gui.screen;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Urls;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class DemoScreen
extends Screen {
    private static final Identifier DEMO_BG = Identifier.ofVanilla((String)"textures/gui/demo_background.png");
    private static final int DEMO_BG_WIDTH = 256;
    private static final int DEMO_BG_HEIGHT = 256;
    private static final int field_63895 = -14737633;
    private MultilineText movementText = MultilineText.EMPTY;
    private MultilineText fullWrappedText = MultilineText.EMPTY;

    public DemoScreen() {
        super((Text)Text.translatable((String)"demo.help.title"));
    }

    protected void init() {
        int i = -16;
        this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"demo.help.buy"), button -> {
            button.active = false;
            Util.getOperatingSystem().open(Urls.BUY_JAVA);
        }).dimensions(this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20).build());
        this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"demo.help.later"), button -> {
            this.client.setScreen(null);
            this.client.mouse.lockCursor();
        }).dimensions(this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20).build());
        GameOptions gameOptions = this.client.options;
        this.movementText = MultilineText.create((TextRenderer)this.textRenderer, (Text[])new Text[]{this.styleHelp(Text.translatable((String)"demo.help.movementShort", (Object[])new Object[]{gameOptions.forwardKey.getBoundKeyLocalizedText(), gameOptions.leftKey.getBoundKeyLocalizedText(), gameOptions.backKey.getBoundKeyLocalizedText(), gameOptions.rightKey.getBoundKeyLocalizedText()})), this.styleHelp(Text.translatable((String)"demo.help.movementMouse")), this.styleHelp(Text.translatable((String)"demo.help.jump", (Object[])new Object[]{gameOptions.jumpKey.getBoundKeyLocalizedText()})), this.styleHelp(Text.translatable((String)"demo.help.inventory", (Object[])new Object[]{gameOptions.inventoryKey.getBoundKeyLocalizedText()}))});
        this.fullWrappedText = MultilineText.create((TextRenderer)this.textRenderer, (Text)Text.translatable((String)"demo.help.fullWrapped").withoutShadow().withColor(-14737633), (int)218);
    }

    private Text styleHelp(MutableText text) {
        return text.withoutShadow().withColor(-11579569);
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderBackground(context, mouseX, mouseY, deltaTicks);
        int i = (this.width - 248) / 2;
        int j = (this.height - 166) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, DEMO_BG, i, j, 0.0f, 0.0f, 248, 166, 256, 256);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        int i = (this.width - 248) / 2 + 10;
        int j = (this.height - 166) / 2 + 8;
        DrawnTextConsumer drawnTextConsumer = context.getTextConsumer();
        context.drawText(this.textRenderer, this.title, i, j, -14737633, false);
        j = this.movementText.draw(Alignment.LEFT, i, j + 12, 12, drawnTextConsumer);
        Objects.requireNonNull(this.textRenderer);
        this.fullWrappedText.draw(Alignment.LEFT, i, j + 20, 9, drawnTextConsumer);
    }
}

