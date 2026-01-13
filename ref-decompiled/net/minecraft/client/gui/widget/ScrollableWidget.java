/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.cursor.StandardCursors
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.ScrollableWidget
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class ScrollableWidget
extends ClickableWidget {
    public static final int SCROLLBAR_WIDTH = 6;
    private double scrollY;
    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla((String)"widget/scroller");
    private static final Identifier SCROLLER_BACKGROUND_TEXTURE = Identifier.ofVanilla((String)"widget/scroller_background");
    private boolean scrollbarDragged;

    public ScrollableWidget(int i, int j, int k, int l, Text text) {
        super(i, j, k, l, text);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!this.visible) {
            return false;
        }
        this.setScrollY(this.getScrollY() - verticalAmount * this.getDeltaYPerScroll());
        return true;
    }

    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (this.scrollbarDragged) {
            if (click.y() < (double)this.getY()) {
                this.setScrollY(0.0);
            } else if (click.y() > (double)this.getBottom()) {
                this.setScrollY((double)this.getMaxScrollY());
            } else {
                double d = Math.max(1, this.getMaxScrollY());
                int i = this.getScrollbarThumbHeight();
                double e = Math.max(1.0, d / (double)(this.height - i));
                this.setScrollY(this.getScrollY() + offsetY * e);
            }
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    public void onRelease(Click click) {
        this.scrollbarDragged = false;
    }

    public double getScrollY() {
        return this.scrollY;
    }

    public void setScrollY(double scrollY) {
        this.scrollY = MathHelper.clamp((double)scrollY, (double)0.0, (double)this.getMaxScrollY());
    }

    public boolean checkScrollbarDragged(Click click) {
        this.scrollbarDragged = this.overflows() && this.isValidClickButton(click.buttonInfo()) && this.isInScrollbar(click.x(), click.y());
        return this.scrollbarDragged;
    }

    protected boolean isInScrollbar(double mouseX, double mouseY) {
        return mouseX >= (double)this.getScrollbarX() && mouseX <= (double)(this.getScrollbarX() + 6) && mouseY >= (double)this.getY() && mouseY < (double)this.getBottom();
    }

    public void refreshScroll() {
        this.setScrollY(this.scrollY);
    }

    public int getMaxScrollY() {
        return Math.max(0, this.getContentsHeightWithPadding() - this.height);
    }

    protected boolean overflows() {
        return this.getMaxScrollY() > 0;
    }

    protected int getScrollbarThumbHeight() {
        return MathHelper.clamp((int)((int)((float)(this.height * this.height) / (float)this.getContentsHeightWithPadding())), (int)32, (int)(this.height - 8));
    }

    protected int getScrollbarX() {
        return this.getRight() - 6;
    }

    protected int getScrollbarThumbY() {
        return Math.max(this.getY(), (int)this.scrollY * (this.height - this.getScrollbarThumbHeight()) / this.getMaxScrollY() + this.getY());
    }

    protected void drawScrollbar(DrawContext context, int mouseX, int mouseY) {
        if (this.overflows()) {
            int i = this.getScrollbarX();
            int j = this.getScrollbarThumbHeight();
            int k = this.getScrollbarThumbY();
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCROLLER_BACKGROUND_TEXTURE, i, this.getY(), 6, this.getHeight());
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCROLLER_TEXTURE, i, k, 6, j);
            if (this.isInScrollbar((double)mouseX, (double)mouseY)) {
                context.setCursor(this.scrollbarDragged ? StandardCursors.RESIZE_NS : StandardCursors.POINTING_HAND);
            }
        }
    }

    protected abstract int getContentsHeightWithPadding();

    protected abstract double getDeltaYPerScroll();
}

