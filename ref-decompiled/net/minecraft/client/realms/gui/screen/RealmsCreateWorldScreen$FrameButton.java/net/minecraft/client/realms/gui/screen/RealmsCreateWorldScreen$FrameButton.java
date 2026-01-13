/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Environment(value=EnvType.CLIENT)
class RealmsCreateWorldScreen.FrameButton
extends ButtonWidget {
    private static final Identifier TEXTURE = Identifier.ofVanilla("widget/slot_frame");
    private static final int SIZE = 60;
    private static final int TEXTURE_MARGIN = 2;
    private static final int TEXTURE_SIZE = 56;
    private final Identifier image;

    RealmsCreateWorldScreen.FrameButton(TextRenderer textRenderer, Text message, Identifier image, ButtonWidget.PressAction onPress) {
        super(0, 0, 60, 60 + textRenderer.fontHeight, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.image = image;
    }

    @Override
    public void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        boolean bl = this.isSelected();
        int i = -1;
        if (bl) {
            i = ColorHelper.fromFloats(1.0f, 0.56f, 0.56f, 0.56f);
        }
        int j = this.getX();
        int k = this.getY();
        context.drawTexture(RenderPipelines.GUI_TEXTURED, this.image, j + 2, k + 2, 0.0f, 0.0f, 56, 56, 56, 56, 56, 56, i);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, j, k, 60, 60, i);
        int l = bl ? -6250336 : -1;
        context.drawCenteredTextWithShadow(RealmsCreateWorldScreen.this.textRenderer, this.getMessage(), j + 28, k - 14, l);
    }
}
