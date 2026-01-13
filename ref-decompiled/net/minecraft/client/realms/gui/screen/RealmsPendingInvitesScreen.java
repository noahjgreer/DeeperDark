/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.realms.RealmsClient
 *  net.minecraft.client.realms.dto.PendingInvite
 *  net.minecraft.client.realms.exception.RealmsServiceException
 *  net.minecraft.client.realms.gui.screen.RealmsMainScreen
 *  net.minecraft.client.realms.gui.screen.RealmsPendingInvitesScreen
 *  net.minecraft.client.realms.gui.screen.RealmsPendingInvitesScreen$PendingInvitationSelectionList
 *  net.minecraft.client.realms.gui.screen.RealmsPendingInvitesScreen$PendingInvitationSelectionListEntry
 *  net.minecraft.client.realms.gui.screen.RealmsScreen
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.Util
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.PendingInvite;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsPendingInvitesScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsPendingInvitesScreen
extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Text NO_PENDING_TEXT = Text.translatable((String)"mco.invites.nopending");
    private final Screen parent;
    private final CompletableFuture<List<PendingInvite>> pendingInvites = CompletableFuture.supplyAsync(() -> {
        try {
            return RealmsClient.create().pendingInvites().pendingInvites();
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't list invites", (Throwable)realmsServiceException);
            return List.of();
        }
    }, (Executor)Util.getIoWorkerExecutor());
    final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);
    // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable PendingInvitationSelectionList pendingInvitationSelectionList;

    public RealmsPendingInvitesScreen(Screen parent, Text title) {
        super(title);
        this.parent = parent;
    }

    public void init() {
        RealmsMainScreen.resetPendingInvitesCount();
        this.layout.addHeader(this.title, this.textRenderer);
        this.pendingInvitationSelectionList = (PendingInvitationSelectionList)this.layout.addBody((Widget)new PendingInvitationSelectionList(this, this.client));
        this.pendingInvites.thenAcceptAsync(pendingInvites -> {
            List<PendingInvitationSelectionListEntry> list = pendingInvites.stream().map(invite -> new PendingInvitationSelectionListEntry(this, invite)).toList();
            this.pendingInvitationSelectionList.replaceEntries(list);
            if (list.isEmpty()) {
                this.client.getNarratorManager().narrateSystemMessage(NO_PENDING_TEXT);
            }
        }, this.executor);
        this.layout.addFooter((Widget)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> this.close()).width(200).build());
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        if (this.pendingInvitationSelectionList != null) {
            this.pendingInvitationSelectionList.position(this.width, this.layout);
        }
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        if (this.pendingInvites.isDone() && this.pendingInvitationSelectionList.isEmpty()) {
            context.drawCenteredTextWithShadow(this.textRenderer, NO_PENDING_TEXT, this.width / 2, this.height / 2 - 20, -1);
        }
    }

    static /* synthetic */ TextRenderer method_52675(RealmsPendingInvitesScreen realmsPendingInvitesScreen) {
        return realmsPendingInvitesScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_52677(RealmsPendingInvitesScreen realmsPendingInvitesScreen) {
        return realmsPendingInvitesScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_52678(RealmsPendingInvitesScreen realmsPendingInvitesScreen) {
        return realmsPendingInvitesScreen.textRenderer;
    }

    static /* synthetic */ Executor method_73342(RealmsPendingInvitesScreen realmsPendingInvitesScreen) {
        return realmsPendingInvitesScreen.executor;
    }

    static /* synthetic */ MinecraftClient method_73343(RealmsPendingInvitesScreen realmsPendingInvitesScreen) {
        return realmsPendingInvitesScreen.client;
    }
}

