/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.tab.GridScreenTab
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.GridWidget$Adder
 *  net.minecraft.client.gui.widget.Positioner
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.realms.dto.RealmsServer
 *  net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsInviteScreen
 *  net.minecraft.client.realms.gui.screen.tab.RealmsPlayerTab
 *  net.minecraft.client.realms.gui.screen.tab.RealmsPlayerTab$InvitedObjectSelectionList
 *  net.minecraft.client.realms.gui.screen.tab.RealmsUpdatableTab
 *  net.minecraft.text.Text
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen.tab;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsInviteScreen;
import net.minecraft.client.realms.gui.screen.tab.RealmsPlayerTab;
import net.minecraft.client.realms.gui.screen.tab.RealmsUpdatableTab;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
class RealmsPlayerTab
extends GridScreenTab
implements RealmsUpdatableTab {
    static final Logger LOGGER = LogUtils.getLogger();
    static final Text TITLE = Text.translatable((String)"mco.configure.world.players.title");
    static final Text QUESTION_TEXT = Text.translatable((String)"mco.question");
    private static final int field_49462 = 8;
    final RealmsConfigureWorldScreen screen;
    final MinecraftClient client;
    final TextRenderer textRenderer;
    RealmsServer serverData;
    final InvitedObjectSelectionList playerList;

    RealmsPlayerTab(RealmsConfigureWorldScreen screen, MinecraftClient client, RealmsServer serverData) {
        super(TITLE);
        this.screen = screen;
        this.client = client;
        this.textRenderer = screen.getTextRenderer();
        this.serverData = serverData;
        GridWidget.Adder adder = this.grid.setSpacing(8).createAdder(1);
        this.playerList = (InvitedObjectSelectionList)adder.add((Widget)new InvitedObjectSelectionList(this, screen.width, this.getPlayerListHeight()), Positioner.create().alignTop().alignHorizontalCenter());
        adder.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"mco.configure.world.buttons.invite"), button -> client.setScreen((Screen)new RealmsInviteScreen(screen, serverData))).build(), Positioner.create().alignBottom().alignHorizontalCenter());
        this.update(serverData);
    }

    public int getPlayerListHeight() {
        return this.screen.getContentHeight() - 20 - 16;
    }

    public void refreshGrid(ScreenRect tabArea) {
        this.playerList.position(this.screen.width, this.getPlayerListHeight(), this.screen.layout.getHeaderHeight());
        super.refreshGrid(tabArea);
    }

    public void update(RealmsServer server) {
        this.serverData = server;
        this.playerList.refreshEntries(server);
    }
}

