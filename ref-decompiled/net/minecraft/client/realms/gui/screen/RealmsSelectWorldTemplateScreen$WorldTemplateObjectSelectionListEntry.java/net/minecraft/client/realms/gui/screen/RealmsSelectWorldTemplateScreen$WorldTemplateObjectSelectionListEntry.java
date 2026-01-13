/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.gui.screen.RealmsSelectWorldTemplateScreen;
import net.minecraft.client.realms.util.RealmsTextureManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry
extends AlwaysSelectedEntryListWidget.Entry<RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry> {
    private static final ButtonTextures LINK_TEXTURES = new ButtonTextures(Identifier.ofVanilla("icon/link"), Identifier.ofVanilla("icon/link_highlighted"));
    private static final ButtonTextures VIDEO_LINK_TEXTURES = new ButtonTextures(Identifier.ofVanilla("icon/video_link"), Identifier.ofVanilla("icon/video_link_highlighted"));
    private static final Text INFO_TOOLTIP_TEXT = Text.translatable("mco.template.info.tooltip");
    private static final Text TRAILER_TOOLTIP_TEXT = Text.translatable("mco.template.trailer.tooltip");
    public final WorldTemplate mTemplate;
    private @Nullable TexturedButtonWidget infoButton;
    private @Nullable TexturedButtonWidget trailerButton;

    public RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry(WorldTemplate template) {
        this.mTemplate = template;
        if (!template.link().isBlank()) {
            this.infoButton = new TexturedButtonWidget(15, 15, LINK_TEXTURES, ConfirmLinkScreen.opening((Screen)RealmsSelectWorldTemplateScreen.this, template.link()), INFO_TOOLTIP_TEXT);
            this.infoButton.setTooltip(Tooltip.of(INFO_TOOLTIP_TEXT));
        }
        if (!template.trailer().isBlank()) {
            this.trailerButton = new TexturedButtonWidget(15, 15, VIDEO_LINK_TEXTURES, ConfirmLinkScreen.opening((Screen)RealmsSelectWorldTemplateScreen.this, template.trailer()), TRAILER_TOOLTIP_TEXT);
            this.trailerButton.setTooltip(Tooltip.of(TRAILER_TOOLTIP_TEXT));
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        RealmsSelectWorldTemplateScreen.this.selectedTemplate = this.mTemplate;
        RealmsSelectWorldTemplateScreen.this.updateButtonStates();
        if (doubled && this.isFocused()) {
            RealmsSelectWorldTemplateScreen.this.callback.accept(this.mTemplate);
        }
        if (this.infoButton != null) {
            this.infoButton.mouseClicked(click, doubled);
        }
        if (this.trailerButton != null) {
            this.trailerButton.mouseClicked(click, doubled);
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, RealmsTextureManager.getTextureId(this.mTemplate.id(), this.mTemplate.image()), this.getContentX() + 1, this.getContentY() + 1 + 1, 0.0f, 0.0f, 38, 38, 38, 38);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_FRAME_TEXTURE, this.getContentX(), this.getContentY() + 1, 40, 40);
        int i = 5;
        int j = RealmsSelectWorldTemplateScreen.this.textRenderer.getWidth(this.mTemplate.version());
        if (this.infoButton != null) {
            this.infoButton.setPosition(this.getContentRightEnd() - j - this.infoButton.getWidth() - 10, this.getContentY());
            this.infoButton.render(context, mouseX, mouseY, deltaTicks);
        }
        if (this.trailerButton != null) {
            this.trailerButton.setPosition(this.getContentRightEnd() - j - this.trailerButton.getWidth() * 2 - 15, this.getContentY());
            this.trailerButton.render(context, mouseX, mouseY, deltaTicks);
        }
        int k = this.getContentX() + 45 + 20;
        int l = this.getContentY() + 5;
        context.drawTextWithShadow(RealmsSelectWorldTemplateScreen.this.textRenderer, this.mTemplate.name(), k, l, -1);
        context.drawTextWithShadow(RealmsSelectWorldTemplateScreen.this.textRenderer, this.mTemplate.version(), this.getContentRightEnd() - j - 5, l, -6250336);
        context.drawTextWithShadow(RealmsSelectWorldTemplateScreen.this.textRenderer, this.mTemplate.author(), k, l + ((RealmsSelectWorldTemplateScreen)RealmsSelectWorldTemplateScreen.this).textRenderer.fontHeight + 5, -6250336);
        if (!this.mTemplate.recommendedPlayers().isBlank()) {
            context.drawTextWithShadow(RealmsSelectWorldTemplateScreen.this.textRenderer, this.mTemplate.recommendedPlayers(), k, this.getContentBottomEnd() - ((RealmsSelectWorldTemplateScreen)RealmsSelectWorldTemplateScreen.this).textRenderer.fontHeight / 2 - 5, -8355712);
        }
    }

    @Override
    public Text getNarration() {
        Text text = ScreenTexts.joinLines(Text.literal(this.mTemplate.name()), Text.translatable("mco.template.select.narrate.authors", this.mTemplate.author()), Text.literal(this.mTemplate.recommendedPlayers()), Text.translatable("mco.template.select.narrate.version", this.mTemplate.version()));
        return Text.translatable("narrator.select", text);
    }
}
