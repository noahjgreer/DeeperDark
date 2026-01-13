/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.widget.Positioner
 *  net.minecraft.client.gui.widget.SimplePositioningWidget
 *  net.minecraft.client.gui.widget.SimplePositioningWidget$Element
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.gui.widget.WrapperWidget
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.WrapperWidget;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class SimplePositioningWidget
extends WrapperWidget {
    private final List<Element> elements = new ArrayList();
    private int minWidth;
    private int minHeight;
    private final Positioner mainPositioner = Positioner.create().relative(0.5f, 0.5f);

    public SimplePositioningWidget() {
        this(0, 0, 0, 0);
    }

    public SimplePositioningWidget(int width, int height) {
        this(0, 0, width, height);
    }

    public SimplePositioningWidget(int i, int j, int k, int l) {
        super(i, j, k, l);
        this.setDimensions(k, l);
    }

    public SimplePositioningWidget setDimensions(int minWidth, int minHeight) {
        return this.setMinWidth(minWidth).setMinHeight(minHeight);
    }

    public SimplePositioningWidget setMinHeight(int minHeight) {
        this.minHeight = minHeight;
        return this;
    }

    public SimplePositioningWidget setMinWidth(int minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    public Positioner copyPositioner() {
        return this.mainPositioner.copy();
    }

    public Positioner getMainPositioner() {
        return this.mainPositioner;
    }

    public void refreshPositions() {
        super.refreshPositions();
        int i = this.minWidth;
        int j = this.minHeight;
        for (Element element : this.elements) {
            i = Math.max(i, element.getWidth());
            j = Math.max(j, element.getHeight());
        }
        for (Element element : this.elements) {
            element.setX(this.getX(), i);
            element.setY(this.getY(), j);
        }
        this.width = i;
        this.height = j;
    }

    public <T extends Widget> T add(T widget) {
        return (T)this.add(widget, this.copyPositioner());
    }

    public <T extends Widget> T add(T widget, Positioner positioner) {
        this.elements.add(new Element(widget, positioner));
        return widget;
    }

    public <T extends Widget> T add(T widget, Consumer<Positioner> callback) {
        return (T)this.add(widget, (Positioner)Util.make((Object)this.copyPositioner(), callback));
    }

    public void forEachElement(Consumer<Widget> consumer) {
        this.elements.forEach(element -> consumer.accept(element.widget));
    }

    public static void setPos(Widget widget, int left, int top, int right, int bottom) {
        SimplePositioningWidget.setPos((Widget)widget, (int)left, (int)top, (int)right, (int)bottom, (float)0.5f, (float)0.5f);
    }

    public static void setPos(Widget widget, ScreenRect rect) {
        SimplePositioningWidget.setPos((Widget)widget, (int)rect.position().x(), (int)rect.position().y(), (int)rect.width(), (int)rect.height());
    }

    public static void setPos(Widget widget, ScreenRect rect, float relativeX, float relativeY) {
        SimplePositioningWidget.setPos((Widget)widget, (int)rect.getLeft(), (int)rect.getTop(), (int)rect.width(), (int)rect.height(), (float)relativeX, (float)relativeY);
    }

    public static void setPos(Widget widget, int left, int top, int right, int bottom, float relativeX, float relativeY) {
        SimplePositioningWidget.setPos((int)left, (int)right, (int)widget.getWidth(), arg_0 -> ((Widget)widget).setX(arg_0), (float)relativeX);
        SimplePositioningWidget.setPos((int)top, (int)bottom, (int)widget.getHeight(), arg_0 -> ((Widget)widget).setY(arg_0), (float)relativeY);
    }

    public static void setPos(int low, int high, int length, Consumer<Integer> setter, float relative) {
        int i = (int)MathHelper.lerp((float)relative, (float)0.0f, (float)(high - length));
        setter.accept(low + i);
    }
}

