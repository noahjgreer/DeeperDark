/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.gui.screen;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
abstract class RealmsMainScreen.Entry
extends AlwaysSelectedEntryListWidget.Entry<RealmsMainScreen.Entry> {
    protected static final int field_46680 = 10;
    private static final int field_46681 = 28;
    protected static final int field_52117 = 7;
    protected static final int field_52118 = 2;

    RealmsMainScreen.Entry() {
    }

    protected void renderStatusIcon(RealmsServer server, DrawContext context, int x, int y, int mouseX, int mouseY) {
        int i = x - 10 - 7;
        int j = y + 2;
        if (server.expired) {
            this.drawTextureWithTooltip(context, i, j, mouseX, mouseY, EXPIRED_STATUS_TEXTURE, () -> EXPIRED_TEXT);
        } else if (server.state == RealmsServer.State.CLOSED) {
            this.drawTextureWithTooltip(context, i, j, mouseX, mouseY, CLOSED_STATUS_TEXTURE, () -> CLOSED_TEXT);
        } else if (RealmsMainScreen.isSelfOwnedServer(server) && server.daysLeft < 7) {
            this.drawTextureWithTooltip(context, i, j, mouseX, mouseY, EXPIRES_SOON_STATUS_TEXTURE, () -> {
                if (realmsServer.daysLeft <= 0) {
                    return EXPIRES_SOON_TEXT;
                }
                if (realmsServer.daysLeft == 1) {
                    return EXPIRES_IN_A_DAY_TEXT;
                }
                return Text.translatable("mco.selectServer.expires.days", realmsServer.daysLeft);
            });
        } else if (server.state == RealmsServer.State.OPEN) {
            this.drawTextureWithTooltip(context, i, j, mouseX, mouseY, OPEN_STATUS_TEXTURE, () -> OPEN_TEXT);
        }
    }

    private void drawTextureWithTooltip(DrawContext context, int x, int y, int mouseX, int mouseY, Identifier texture, Supplier<Text> tooltip) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, 10, 28);
        if (RealmsMainScreen.this.realmSelectionList.isMouseOver(mouseX, mouseY) && mouseX >= x && mouseX <= x + 10 && mouseY >= y && mouseY <= y + 28) {
            context.drawTooltip(tooltip.get(), mouseX, mouseY);
        }
    }

    protected void drawServerNameAndVersion(DrawContext context, int y, int x, int width, int color, RealmsServer server) {
        int i = this.getNameX(x);
        int j = this.getNameY(y);
        Text text = RealmsMainScreen.getVersionText(server.activeVersion, server.isCompatible());
        int k = this.getVersionRight(x, width, text);
        this.drawTrimmedText(context, server.getName(), i, j, k, color);
        if (text != ScreenTexts.EMPTY && !server.isMinigame()) {
            context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, text, k, j, -8355712);
        }
    }

    protected void drawDescription(DrawContext context, int y, int x, int width, RealmsServer server) {
        int i = this.getNameX(x);
        int j = this.getNameY(y);
        int k = this.getDescriptionY(j);
        String string = server.getMinigameName();
        boolean bl = server.isMinigame();
        if (bl && string != null) {
            MutableText text = Text.literal(string).formatted(Formatting.GRAY);
            context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, Text.translatable("mco.selectServer.minigameName", text).withColor(-171), i, k, -1);
        } else {
            int l = this.drawGameMode(server, context, x, width, j);
            this.drawTrimmedText(context, server.getDescription(), i, this.getDescriptionY(j), l, -8355712);
        }
    }

    protected void drawOwnerOrExpiredText(DrawContext context, int y, int x, RealmsServer server) {
        int i = this.getNameX(x);
        int j = this.getNameY(y);
        int k = this.getStatusY(j);
        if (!RealmsMainScreen.isSelfOwnedServer(server)) {
            context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, server.owner, i, this.getStatusY(j), -8355712);
        } else if (server.expired) {
            Text text = server.expiredTrial ? EXPIRED_TRIAL_TEXT : EXPIRED_LIST_TEXT;
            context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, text, i, k, -2142128);
        }
    }

    protected void drawTrimmedText(DrawContext context, @Nullable String string, int left, int y, int right, int color) {
        if (string == null) {
            return;
        }
        int i = right - left;
        if (RealmsMainScreen.this.textRenderer.getWidth(string) > i) {
            String string2 = RealmsMainScreen.this.textRenderer.trimToWidth(string, i - RealmsMainScreen.this.textRenderer.getWidth("... "));
            context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, string2 + "...", left, y, color);
        } else {
            context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, string, left, y, color);
        }
    }

    protected int getVersionRight(int x, int width, Text version) {
        return x + width - RealmsMainScreen.this.textRenderer.getWidth(version) - 20;
    }

    protected int getGameModeRight(int x, int width, Text gameMode) {
        return x + width - RealmsMainScreen.this.textRenderer.getWidth(gameMode) - 20;
    }

    protected int drawGameMode(RealmsServer server, DrawContext context, int x, int entryWidth, int y) {
        boolean bl = server.hardcore;
        int i = server.gameMode;
        int j = x;
        if (GameMode.isValid(i)) {
            Text text = RealmsMainScreen.getGameModeText(i, bl);
            j = this.getGameModeRight(x, entryWidth, text);
            context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, text, j, this.getDescriptionY(y), -8355712);
        }
        if (bl) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HARDCORE_ICON_TEXTURE, j -= 10, this.getDescriptionY(y), 8, 8);
        }
        return j;
    }

    protected int getNameY(int y) {
        return y + 1;
    }

    protected int getTextHeight() {
        return 2 + ((RealmsMainScreen)RealmsMainScreen.this).textRenderer.fontHeight;
    }

    protected int getNameX(int x) {
        return x + 36 + 2;
    }

    protected int getDescriptionY(int y) {
        return y + this.getTextHeight();
    }

    protected int getStatusY(int y) {
        return y + this.getTextHeight() * 2;
    }
}
