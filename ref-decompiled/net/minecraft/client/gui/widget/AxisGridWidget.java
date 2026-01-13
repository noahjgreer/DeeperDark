/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.widget.AxisGridWidget
 *  net.minecraft.client.gui.widget.AxisGridWidget$DisplayAxis
 *  net.minecraft.client.gui.widget.AxisGridWidget$Element
 *  net.minecraft.client.gui.widget.Positioner
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.gui.widget.WrapperWidget
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.Divider
 */
package net.minecraft.client.gui.widget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AxisGridWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.WrapperWidget;
import net.minecraft.util.Util;
import net.minecraft.util.math.Divider;

@Environment(value=EnvType.CLIENT)
public class AxisGridWidget
extends WrapperWidget {
    private final DisplayAxis axis;
    private final List<Element> elements = new ArrayList();
    private final Positioner mainPositioner = Positioner.create();

    public AxisGridWidget(int width, int height, DisplayAxis axis) {
        this(0, 0, width, height, axis);
    }

    public AxisGridWidget(int x, int y, int width, int height, DisplayAxis axis) {
        super(x, y, width, height);
        this.axis = axis;
    }

    public void refreshPositions() {
        super.refreshPositions();
        if (this.elements.isEmpty()) {
            return;
        }
        int i = 0;
        int j = this.axis.getOtherAxisLength((Widget)this);
        for (Element element : this.elements) {
            i += this.axis.getSameAxisLength(element);
            j = Math.max(j, this.axis.getOtherAxisLength(element));
        }
        int k = this.axis.getSameAxisLength((Widget)this) - i;
        int l = this.axis.getSameAxisCoordinate((Widget)this);
        Iterator iterator = this.elements.iterator();
        Element element2 = (Element)iterator.next();
        this.axis.setSameAxisCoordinate(element2, l);
        l += this.axis.getSameAxisLength(element2);
        if (this.elements.size() >= 2) {
            Divider divider = new Divider(k, this.elements.size() - 1);
            while (divider.hasNext()) {
                Element element3 = (Element)iterator.next();
                this.axis.setSameAxisCoordinate(element3, l += divider.nextInt());
                l += this.axis.getSameAxisLength(element3);
            }
        }
        int m = this.axis.getOtherAxisCoordinate((Widget)this);
        for (Element element4 : this.elements) {
            this.axis.setOtherAxisCoordinate(element4, m, j);
        }
        switch (this.axis.ordinal()) {
            case 0: {
                this.height = j;
                break;
            }
            case 1: {
                this.width = j;
            }
        }
    }

    public void forEachElement(Consumer<Widget> consumer) {
        this.elements.forEach(element -> consumer.accept(element.widget));
    }

    public Positioner copyPositioner() {
        return this.mainPositioner.copy();
    }

    public Positioner getMainPositioner() {
        return this.mainPositioner;
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
}

