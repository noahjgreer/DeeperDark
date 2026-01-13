/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen.tab;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.realms.gui.screen.tab.RealmsPlayerTab;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
class RealmsPlayerTab.HeaderEntry
extends RealmsPlayerTab.PlayerTabEntry {
    private String invitedPlayerCount = "";
    private final NarratedMultilineTextWidget textWidget;

    public RealmsPlayerTab.HeaderEntry() {
        MutableText text = Text.translatable("mco.configure.world.invited.number", "").formatted(Formatting.UNDERLINE);
        this.textWidget = NarratedMultilineTextWidget.builder(text, RealmsPlayerTab.this.textRenderer).alwaysShowBorders(false).backgroundRendering(NarratedMultilineTextWidget.BackgroundRendering.ON_FOCUS).build();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        String string;
        String string2 = string = RealmsPlayerTab.this.serverData.players != null ? Integer.toString(RealmsPlayerTab.this.serverData.players.size()) : "0";
        if (!string.equals(this.invitedPlayerCount)) {
            this.invitedPlayerCount = string;
            MutableText text = Text.translatable("mco.configure.world.invited.number", string).formatted(Formatting.UNDERLINE);
            this.textWidget.setMessage(text);
        }
        this.textWidget.setPosition(RealmsPlayerTab.this.playerList.getRowLeft() + RealmsPlayerTab.this.playerList.getRowWidth() / 2 - this.textWidget.getWidth() / 2, this.getY() + this.getHeight() / 2 - this.textWidget.getHeight() / 2);
        this.textWidget.render(context, mouseX, mouseY, deltaTicks);
    }

    int getHeight(int innerHeight) {
        return innerHeight + this.textWidget.getMargin() * 2;
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return List.of(this.textWidget);
    }

    @Override
    public List<? extends Element> children() {
        return List.of(this.textWidget);
    }
}
