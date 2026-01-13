/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class RealmsMainScreen.NotificationButtonWidget
extends TextIconButtonWidget.IconOnly {
    private static final Identifier[] TEXTURES = new Identifier[]{Identifier.ofVanilla("notification/1"), Identifier.ofVanilla("notification/2"), Identifier.ofVanilla("notification/3"), Identifier.ofVanilla("notification/4"), Identifier.ofVanilla("notification/5"), Identifier.ofVanilla("notification/more")};
    private static final int field_45228 = Integer.MAX_VALUE;
    private static final int SIZE = 20;
    private static final int TEXTURE_SIZE = 14;
    private int notificationCount;

    public RealmsMainScreen.NotificationButtonWidget(Text message, Identifier texture, ButtonWidget.PressAction onPress, @Nullable Text tooltip) {
        super(20, 20, message, 14, 14, new ButtonTextures(texture), onPress, tooltip, null);
    }

    int getNotificationCount() {
        return this.notificationCount;
    }

    public void setNotificationCount(int notificationCount) {
        this.notificationCount = notificationCount;
    }

    @Override
    public void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.drawIcon(context, mouseX, mouseY, deltaTicks);
        if (this.active && this.notificationCount != 0) {
            this.render(context);
        }
    }

    private void render(DrawContext context) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURES[Math.min(this.notificationCount, 6) - 1], this.getX() + this.getWidth() - 5, this.getY() - 3, 8, 8);
    }
}
