/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.IconWidget
 *  net.minecraft.client.gui.widget.Positioner
 *  net.minecraft.client.gui.widget.SimplePositioningWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.realms.ServiceQuality
 *  net.minecraft.client.realms.dto.RealmsServerAddress
 *  net.minecraft.client.realms.gui.screen.RealmsConnectingScreen
 *  net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen
 *  net.minecraft.client.realms.task.LongRunningTask
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.IconWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.realms.ServiceQuality;
import net.minecraft.client.realms.dto.RealmsServerAddress;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class RealmsConnectingScreen
extends RealmsLongRunningMcoTaskScreen {
    private final LongRunningTask connectTask;
    private final RealmsServerAddress serverAddress;
    private final DirectionalLayoutWidget footerLayout = DirectionalLayoutWidget.vertical();

    public RealmsConnectingScreen(Screen parent, RealmsServerAddress serverAddress, LongRunningTask connectTask) {
        super(parent, new LongRunningTask[]{connectTask});
        this.connectTask = connectTask;
        this.serverAddress = serverAddress;
    }

    public void init() {
        super.init();
        if (this.serverAddress.regionData() == null || this.serverAddress.regionData().region() == null) {
            return;
        }
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(10);
        TextWidget textWidget = new TextWidget((Text)Text.translatable((String)"mco.connect.region", (Object[])new Object[]{Text.translatable((String)this.serverAddress.regionData().region().translationKey)}), this.textRenderer);
        directionalLayoutWidget.add((Widget)textWidget);
        Identifier identifier = this.serverAddress.regionData().serviceQuality() != null ? this.serverAddress.regionData().serviceQuality().getIcon() : ServiceQuality.UNKNOWN.getIcon();
        directionalLayoutWidget.add((Widget)IconWidget.create((int)10, (int)8, (Identifier)identifier), Positioner::alignTop);
        this.footerLayout.add((Widget)directionalLayoutWidget, positioner -> positioner.marginTop(40));
        this.footerLayout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        super.refreshWidgetPositions();
        int i = this.layout.getY() + this.layout.getHeight();
        ScreenRect screenRect = new ScreenRect(0, i, this.width, this.height - i);
        this.footerLayout.refreshPositions();
        SimplePositioningWidget.setPos((Widget)this.footerLayout, (ScreenRect)screenRect, (float)0.5f, (float)0.0f);
    }

    public void tick() {
        super.tick();
        this.connectTask.tick();
    }

    protected void onCancel() {
        this.connectTask.abortTask();
        super.onCancel();
    }
}

