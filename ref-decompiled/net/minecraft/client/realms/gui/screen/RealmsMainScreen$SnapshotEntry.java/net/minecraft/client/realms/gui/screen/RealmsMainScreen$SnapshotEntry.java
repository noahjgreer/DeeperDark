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
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.PopupScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipState;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.RealmsCreateRealmScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class RealmsMainScreen.SnapshotEntry
extends RealmsMainScreen.Entry {
    private static final Text START_TEXT = Text.translatable("mco.snapshot.start");
    private static final int field_46677 = 5;
    private final TooltipState tooltip;
    private final RealmsServer server;

    public RealmsMainScreen.SnapshotEntry(RealmsServer server) {
        super(RealmsMainScreen.this);
        this.tooltip = new TooltipState();
        this.server = server;
        this.tooltip.setTooltip(Tooltip.of(Text.translatable("mco.snapshot.tooltip")));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, NEW_REALM_ICON_TEXTURE, this.getContentX() - 5, this.getContentMiddleY() - 10, 40, 20);
        int i = this.getContentMiddleY() - ((RealmsMainScreen)RealmsMainScreen.this).textRenderer.fontHeight / 2;
        context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, START_TEXT, this.getContentX() + 40 - 2, i - 5, -8388737);
        context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, Text.translatable("mco.snapshot.description", Objects.requireNonNullElse(this.server.name, "unknown server")), this.getContentX() + 40 - 2, i + 5, -8355712);
        this.tooltip.render(context, mouseX, mouseY, hovered, this.isFocused(), new ScreenRect(this.getContentX(), this.getContentY(), this.getContentWidth(), this.getContentHeight()));
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        this.showPopup();
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.isEnterOrSpace()) {
            this.showPopup();
            return false;
        }
        return super.keyPressed(input);
    }

    private void showPopup() {
        RealmsMainScreen.this.client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        RealmsMainScreen.this.client.setScreen(new PopupScreen.Builder(RealmsMainScreen.this, Text.translatable("mco.snapshot.createSnapshotPopup.title")).message(Text.translatable("mco.snapshot.createSnapshotPopup.text")).button(Text.translatable("mco.selectServer.create"), screen -> RealmsMainScreen.this.client.setScreen(new RealmsCreateRealmScreen(RealmsMainScreen.this, this.server, true))).button(ScreenTexts.CANCEL, PopupScreen::close).build());
    }

    @Override
    public Text getNarration() {
        return Text.translatable("gui.narrate.button", ScreenTexts.joinSentences(START_TEXT, Text.translatable("mco.snapshot.description", Objects.requireNonNullElse(this.server.name, "unknown server"))));
    }
}
