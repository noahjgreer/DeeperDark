/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.report;

import com.mojang.authlib.GameProfile;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.screen.report.ChatSelectionScreen;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class ChatSelectionScreen.SelectionListWidget.SenderEntry
extends ChatSelectionScreen.SelectionListWidget.Entry {
    private static final int PLAYER_SKIN_SIZE = 12;
    private static final int field_49545 = 4;
    private final Text headingText;
    private final Supplier<SkinTextures> skinTexturesSupplier;
    private final boolean fromReportedPlayer;

    public ChatSelectionScreen.SelectionListWidget.SenderEntry(GameProfile gameProfile, Text headingText, boolean fromReportedPlayer) {
        this.headingText = headingText;
        this.fromReportedPlayer = fromReportedPlayer;
        this.skinTexturesSupplier = SelectionListWidget.this.client.getSkinProvider().supplySkinTextures(gameProfile, true);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        int i = this.getContentX() - 12 + 4;
        int j = this.getContentY() + (this.getContentHeight() - 12) / 2;
        PlayerSkinDrawer.draw(context, this.skinTexturesSupplier.get(), i, j, 12);
        int k = this.getContentY() + 1 + (this.getContentHeight() - ((ChatSelectionScreen)SelectionListWidget.this.field_39592).textRenderer.fontHeight) / 2;
        context.drawTextWithShadow(SelectionListWidget.this.field_39592.textRenderer, this.headingText, i + 12 + 4, k, this.fromReportedPlayer ? -1 : -1593835521);
    }
}
