/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.Alignment
 *  net.minecraft.client.font.DrawnTextConsumer
 *  net.minecraft.client.font.DrawnTextConsumer$ClickHandler
 *  net.minecraft.client.font.DrawnTextConsumer$Transformation
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.DrawContext$HoverType
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.DeathScreen
 *  net.minecraft.client.gui.screen.DeathScreen$TitleScreenConfirmScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.TitleScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.ClickEvent
 *  net.minecraft.text.ClickEvent$OpenUrl
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.net.URI;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class DeathScreen
extends Screen {
    private static final int field_63892 = 2;
    private static final Identifier DRAFT_REPORT_ICON_TEXTURE = Identifier.ofVanilla((String)"icon/draft_report");
    private int ticksSinceDeath;
    private final @Nullable Text message;
    private final boolean isHardcore;
    private final ClientPlayerEntity decedent;
    private final Text scoreText;
    private final List<ButtonWidget> buttons = Lists.newArrayList();
    private @Nullable ButtonWidget titleScreenButton;

    public DeathScreen(@Nullable Text message, boolean isHardcore, ClientPlayerEntity decedent) {
        super((Text)Text.translatable((String)(isHardcore ? "deathScreen.title.hardcore" : "deathScreen.title")));
        this.message = message;
        this.isHardcore = isHardcore;
        this.decedent = decedent;
        MutableText text = Text.literal((String)Integer.toString(decedent.getScore())).formatted(Formatting.YELLOW);
        this.scoreText = Text.translatable((String)"deathScreen.score.value", (Object[])new Object[]{text});
    }

    protected void init() {
        this.ticksSinceDeath = 0;
        this.buttons.clear();
        MutableText text = this.isHardcore ? Text.translatable((String)"deathScreen.spectate") : Text.translatable((String)"deathScreen.respawn");
        this.buttons.add((ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)text, button -> {
            this.decedent.requestRespawn();
            button.active = false;
        }).dimensions(this.width / 2 - 100, this.height / 4 + 72, 200, 20).build()));
        this.titleScreenButton = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"deathScreen.titleScreen"), button -> this.client.getAbuseReportContext().tryShowDraftScreen(this.client, (Screen)this, () -> this.onTitleScreenButtonClicked(), true)).dimensions(this.width / 2 - 100, this.height / 4 + 96, 200, 20).build());
        this.buttons.add(this.titleScreenButton);
        this.setButtonsActive(false);
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    private void onTitleScreenButtonClicked() {
        if (this.isHardcore) {
            this.quitLevel();
            return;
        }
        TitleScreenConfirmScreen confirmScreen = new TitleScreenConfirmScreen(confirmed -> {
            if (confirmed) {
                this.quitLevel();
            } else {
                this.decedent.requestRespawn();
                this.client.setScreen(null);
            }
        }, (Text)Text.translatable((String)"deathScreen.quit.confirm"), ScreenTexts.EMPTY, (Text)Text.translatable((String)"deathScreen.titleScreen"), (Text)Text.translatable((String)"deathScreen.respawn"));
        this.client.setScreen((Screen)confirmScreen);
        confirmScreen.disableButtons(20);
    }

    private void quitLevel() {
        if (this.client.world != null) {
            this.client.world.disconnect(ClientWorld.QUITTING_MULTIPLAYER_TEXT);
        }
        this.client.disconnectWithSavingScreen();
        this.client.setScreen((Screen)new TitleScreen());
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawTitles(context.getTextConsumer(DrawContext.HoverType.TOOLTIP_AND_CURSOR));
        if (this.titleScreenButton != null && this.client.getAbuseReportContext().hasDraft()) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, DRAFT_REPORT_ICON_TEXTURE, this.titleScreenButton.getX() + this.titleScreenButton.getWidth() - 17, this.titleScreenButton.getY() + 3, 15, 15);
        }
    }

    private void drawTitles(DrawnTextConsumer drawer) {
        DrawnTextConsumer.Transformation transformation = drawer.getTransformation();
        int i = this.width / 2;
        drawer.setTransformation(transformation.scaled(2.0f));
        drawer.text(Alignment.CENTER, i / 2, 30, this.title);
        drawer.setTransformation(transformation);
        if (this.message != null) {
            drawer.text(Alignment.CENTER, i, 85, this.message);
        }
        drawer.text(Alignment.CENTER, i, 100, this.scoreText);
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        DeathScreen.fillBackgroundGradient((DrawContext)context, (int)this.width, (int)this.height);
    }

    static void fillBackgroundGradient(DrawContext context, int width, int height) {
        context.fillGradient(0, 0, width, height, 0x60500000, -1602211792);
    }

    public boolean mouseClicked(Click click, boolean doubled) {
        ClickEvent clickEvent;
        DrawnTextConsumer.ClickHandler clickHandler = new DrawnTextConsumer.ClickHandler(this.getTextRenderer(), (int)click.x(), (int)click.y());
        this.drawTitles((DrawnTextConsumer)clickHandler);
        Style style = clickHandler.getStyle();
        if (style != null && (clickEvent = style.getClickEvent()) instanceof ClickEvent.OpenUrl) {
            ClickEvent.OpenUrl openUrl = (ClickEvent.OpenUrl)clickEvent;
            return DeathScreen.handleOpenUri((MinecraftClient)this.client, (Screen)this, (URI)openUrl.uri());
        }
        return super.mouseClicked(click, doubled);
    }

    public boolean shouldPause() {
        return false;
    }

    public boolean keepOpenThroughPortal() {
        return true;
    }

    public void tick() {
        super.tick();
        ++this.ticksSinceDeath;
        if (this.ticksSinceDeath == 20) {
            this.setButtonsActive(true);
        }
    }

    private void setButtonsActive(boolean active) {
        for (ButtonWidget buttonWidget : this.buttons) {
            buttonWidget.active = active;
        }
    }
}

