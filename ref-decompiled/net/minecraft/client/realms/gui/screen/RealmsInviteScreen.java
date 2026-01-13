/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.LayoutWidgets
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.realms.dto.RealmsServer
 *  net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsInviteScreen
 *  net.minecraft.client.realms.gui.screen.RealmsScreen
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.StringHelper
 *  net.minecraft.util.Util
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.gui.screen;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsInviteScreen
extends RealmsScreen {
    private static final Text INVITE_TEXT = Text.translatable((String)"mco.configure.world.buttons.invite");
    private static final Text INVITE_PROFILE_NAME_TEXT = Text.translatable((String)"mco.configure.world.invite.profile.name").withColor(-6250336);
    private static final Text INVITING_TEXT = Text.translatable((String)"mco.configure.world.players.inviting").withColor(-6250336);
    private static final Text PLAYER_ERROR_TEXT = Text.translatable((String)"mco.configure.world.players.error").withColor(-65536);
    private static final Text field_61501 = Text.translatable((String)"mco.configure.world.players.invite.duplicate").withColor(-65536);
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);
    private @Nullable TextFieldWidget nameWidget;
    private @Nullable ButtonWidget inviteButton;
    private final RealmsServer serverData;
    private final RealmsConfigureWorldScreen configureScreen;
    private @Nullable Text errorMessage;

    public RealmsInviteScreen(RealmsConfigureWorldScreen configureScreen, RealmsServer serverData) {
        super(INVITE_TEXT);
        this.configureScreen = configureScreen;
        this.serverData = serverData;
    }

    public void init() {
        this.layout.addHeader(INVITE_TEXT, this.textRenderer);
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addBody((Widget)DirectionalLayoutWidget.vertical().spacing(8));
        this.nameWidget = new TextFieldWidget(this.client.textRenderer, 200, 20, (Text)Text.translatable((String)"mco.configure.world.invite.profile.name"));
        directionalLayoutWidget.add((Widget)LayoutWidgets.createLabeledWidget((TextRenderer)this.textRenderer, (Widget)this.nameWidget, (Text)INVITE_PROFILE_NAME_TEXT));
        this.inviteButton = (ButtonWidget)directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)INVITE_TEXT, button -> this.onInvite()).width(200).build());
        this.layout.addFooter((Widget)ButtonWidget.builder((Text)ScreenTexts.BACK, button -> this.close()).width(200).build());
        this.layout.forEachChild(element -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(element);
        });
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
    }

    protected void setInitialFocus() {
        if (this.nameWidget != null) {
            this.setInitialFocus((Element)this.nameWidget);
        }
    }

    private void onInvite() {
        if (this.inviteButton == null || this.nameWidget == null) {
            return;
        }
        if (StringHelper.isBlank((String)this.nameWidget.getText())) {
            this.showError(PLAYER_ERROR_TEXT);
            return;
        }
        if (this.serverData.players.stream().anyMatch(playerInfo -> playerInfo.name.equalsIgnoreCase(this.nameWidget.getText()))) {
            this.showError(field_61501);
            return;
        }
        long l = this.serverData.id;
        String string = this.nameWidget.getText().trim();
        this.inviteButton.active = false;
        this.nameWidget.setEditable(false);
        this.showError(INVITING_TEXT);
        CompletableFuture.supplyAsync(() -> this.configureScreen.invite(l, string), (Executor)Util.getIoWorkerExecutor()).thenAcceptAsync(success -> {
            if (success.booleanValue()) {
                this.client.setScreen((Screen)this.configureScreen);
            } else {
                this.showError(PLAYER_ERROR_TEXT);
            }
            this.nameWidget.setEditable(true);
            this.inviteButton.active = true;
        }, this.executor);
    }

    private void showError(Text errorMessage) {
        this.errorMessage = errorMessage;
        this.client.getNarratorManager().narrateSystemImmediately(errorMessage);
    }

    public void close() {
        this.client.setScreen((Screen)this.configureScreen);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        if (this.errorMessage != null && this.inviteButton != null) {
            context.drawCenteredTextWithShadow(this.textRenderer, this.errorMessage, this.width / 2, this.inviteButton.getY() + this.inviteButton.getHeight() + 8, -1);
        }
    }
}

