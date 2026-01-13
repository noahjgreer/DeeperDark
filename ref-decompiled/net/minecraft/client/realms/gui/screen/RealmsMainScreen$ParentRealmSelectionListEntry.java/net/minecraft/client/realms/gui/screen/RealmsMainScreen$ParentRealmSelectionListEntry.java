/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipState;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class RealmsMainScreen.ParentRealmSelectionListEntry
extends RealmsMainScreen.Entry {
    private final RealmsServer server;
    private final TooltipState tooltip = new TooltipState();

    public RealmsMainScreen.ParentRealmSelectionListEntry(RealmsMainScreen realmsMainScreen, RealmsServer server) {
        super(realmsMainScreen);
        this.server = server;
        if (!server.expired) {
            this.tooltip.setTooltip(Tooltip.of(Text.translatable("mco.snapshot.parent.tooltip")));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        this.renderStatusIcon(this.server, context, this.getContentRightEnd(), this.getContentY(), mouseX, mouseY);
        RealmsUtil.drawPlayerHead(context, this.getContentX(), this.getContentY(), 32, this.server.ownerUUID);
        this.drawServerNameAndVersion(context, this.getContentY(), this.getContentX(), this.getContentWidth(), -8355712, this.server);
        this.drawDescription(context, this.getContentY(), this.getContentX(), this.getContentWidth(), this.server);
        this.drawOwnerOrExpiredText(context, this.getContentY(), this.getContentX(), this.server);
        this.tooltip.render(context, mouseX, mouseY, hovered, this.isFocused(), new ScreenRect(this.getContentX(), this.getContentY(), this.getContentWidth(), this.getContentHeight()));
    }

    @Override
    public Text getNarration() {
        return Text.literal(Objects.requireNonNullElse(this.server.name, "unknown server"));
    }
}
