/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.Widget;

@Environment(value=EnvType.CLIENT)
protected static abstract class EntryListWidget.Entry<E extends EntryListWidget.Entry<E>>
implements Element,
Widget {
    public static final int PADDING = 2;
    private int x = 0;
    private int y = 0;
    private int width = 0;
    private int height;
    @Deprecated
    EntryListWidget<E> parentList;

    protected EntryListWidget.Entry() {
    }

    @Override
    public void setFocused(boolean focused) {
    }

    @Override
    public boolean isFocused() {
        return this.parentList.getFocused() == this;
    }

    public abstract void render(DrawContext var1, int var2, int var3, boolean var4, float var5);

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.getNavigationFocus().contains((int)mouseX, (int)mouseY);
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getContentX() {
        return this.getX() + 2;
    }

    public int getContentY() {
        return this.getY() + 2;
    }

    public int getContentHeight() {
        return this.getHeight() - 4;
    }

    public int getContentMiddleY() {
        return this.getContentY() + this.getContentHeight() / 2;
    }

    public int getContentBottomEnd() {
        return this.getContentY() + this.getContentHeight();
    }

    public int getContentWidth() {
        return this.getWidth() - 4;
    }

    public int getContentMiddleX() {
        return this.getContentX() + this.getContentWidth() / 2;
    }

    public int getContentRightEnd() {
        return this.getContentX() + this.getContentWidth();
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
    }

    @Override
    public ScreenRect getNavigationFocus() {
        return Widget.super.getNavigationFocus();
    }
}
