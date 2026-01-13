/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.ConfirmScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.multiplayer.AddServerScreen
 *  net.minecraft.client.gui.screen.multiplayer.ConnectScreen
 *  net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget$Entry
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget$ScanningEntry
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget$ServerEntry
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.network.LanServerQueryManager$LanServerDetector
 *  net.minecraft.client.network.LanServerQueryManager$LanServerEntryList
 *  net.minecraft.client.network.MultiplayerServerListPinger
 *  net.minecraft.client.network.ServerAddress
 *  net.minecraft.client.network.ServerInfo
 *  net.minecraft.client.network.ServerInfo$ServerType
 *  net.minecraft.client.option.ServerList
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.multiplayer;

import com.mojang.logging.LogUtils;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class MultiplayerScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int FIRST_ROW_BUTTON_WIDTH = 100;
    private static final int SECOND_ROW_BUTTON_WIDTH = 74;
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this, 33, 60);
    private final MultiplayerServerListPinger serverListPinger = new MultiplayerServerListPinger();
    private final Screen parent;
    protected MultiplayerServerListWidget serverListWidget;
    private ServerList serverList;
    private ButtonWidget buttonEdit;
    private ButtonWidget buttonJoin;
    private ButtonWidget buttonDelete;
    private ServerInfo selectedEntry;
    private LanServerQueryManager.LanServerEntryList lanServers;
    private // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable LanServerQueryManager.LanServerDetector lanServerDetector;

    public MultiplayerScreen(Screen parent) {
        super((Text)Text.translatable((String)"multiplayer.title"));
        this.parent = parent;
    }

    protected void init() {
        this.layout.addHeader(this.title, this.textRenderer);
        this.serverList = new ServerList(this.client);
        this.serverList.loadFile();
        this.lanServers = new LanServerQueryManager.LanServerEntryList();
        try {
            this.lanServerDetector = new LanServerQueryManager.LanServerDetector(this.lanServers);
            this.lanServerDetector.start();
        }
        catch (Exception exception) {
            LOGGER.warn("Unable to start LAN server detection: {}", (Object)exception.getMessage());
        }
        this.serverListWidget = (MultiplayerServerListWidget)this.layout.addBody((Widget)new MultiplayerServerListWidget(this, this.client, this.width, this.layout.getContentHeight(), this.layout.getHeaderHeight(), 36));
        this.serverListWidget.setServers(this.serverList);
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addFooter((Widget)DirectionalLayoutWidget.vertical().spacing(4));
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        DirectionalLayoutWidget directionalLayoutWidget2 = (DirectionalLayoutWidget)directionalLayoutWidget.add((Widget)DirectionalLayoutWidget.horizontal().spacing(4));
        DirectionalLayoutWidget directionalLayoutWidget3 = (DirectionalLayoutWidget)directionalLayoutWidget.add((Widget)DirectionalLayoutWidget.horizontal().spacing(4));
        this.buttonJoin = (ButtonWidget)directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"selectServer.select"), button -> {
            MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelectedOrNull();
            if (entry != null) {
                entry.connect();
            }
        }).width(100).build());
        directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"selectServer.direct"), button -> {
            this.selectedEntry = new ServerInfo(I18n.translate((String)"selectServer.defaultName", (Object[])new Object[0]), "", ServerInfo.ServerType.OTHER);
            this.client.setScreen((Screen)new DirectConnectScreen((Screen)this, arg_0 -> this.directConnect(arg_0), this.selectedEntry));
        }).width(100).build());
        directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"selectServer.add"), button -> {
            this.selectedEntry = new ServerInfo("", "", ServerInfo.ServerType.OTHER);
            this.client.setScreen((Screen)new AddServerScreen((Screen)this, (Text)Text.translatable((String)"manageServer.add.title"), arg_0 -> this.addEntry(arg_0), this.selectedEntry));
        }).width(100).build());
        this.buttonEdit = (ButtonWidget)directionalLayoutWidget3.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"selectServer.edit"), button -> {
            MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelectedOrNull();
            if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
                ServerInfo serverInfo = ((MultiplayerServerListWidget.ServerEntry)entry).getServer();
                this.selectedEntry = new ServerInfo(serverInfo.name, serverInfo.address, ServerInfo.ServerType.OTHER);
                this.selectedEntry.copyWithSettingsFrom(serverInfo);
                this.client.setScreen((Screen)new AddServerScreen((Screen)this, (Text)Text.translatable((String)"manageServer.edit.title"), arg_0 -> this.editEntry(arg_0), this.selectedEntry));
            }
        }).width(74).build());
        this.buttonDelete = (ButtonWidget)directionalLayoutWidget3.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"selectServer.delete"), button -> {
            String string;
            MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelectedOrNull();
            if (entry instanceof MultiplayerServerListWidget.ServerEntry && (string = ((MultiplayerServerListWidget.ServerEntry)entry).getServer().name) != null) {
                MutableText text = Text.translatable((String)"selectServer.deleteQuestion");
                MutableText text2 = Text.translatable((String)"selectServer.deleteWarning", (Object[])new Object[]{string});
                MutableText text3 = Text.translatable((String)"selectServer.deleteButton");
                Text text4 = ScreenTexts.CANCEL;
                this.client.setScreen((Screen)new ConfirmScreen(arg_0 -> this.removeEntry(arg_0), (Text)text, (Text)text2, (Text)text3, text4));
            }
        }).width(74).build());
        directionalLayoutWidget3.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"selectServer.refresh"), button -> this.refresh()).width(74).build());
        directionalLayoutWidget3.add((Widget)ButtonWidget.builder((Text)ScreenTexts.BACK, button -> this.close()).width(74).build());
        this.layout.forEachChild(element -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(element);
        });
        this.refreshWidgetPositions();
        this.updateButtonActivationStates();
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        if (this.serverListWidget != null) {
            this.serverListWidget.position(this.width, this.layout);
        }
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    public void tick() {
        super.tick();
        List list = this.lanServers.getEntriesIfUpdated();
        if (list != null) {
            this.serverListWidget.setLanServers(list);
        }
        this.serverListPinger.tick();
    }

    public void removed() {
        if (this.lanServerDetector != null) {
            this.lanServerDetector.interrupt();
            this.lanServerDetector = null;
        }
        this.serverListPinger.cancel();
        this.serverListWidget.onRemoved();
    }

    private void refresh() {
        this.client.setScreen((Screen)new MultiplayerScreen(this.parent));
    }

    private void removeEntry(boolean confirmedAction) {
        MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelectedOrNull();
        if (confirmedAction && entry instanceof MultiplayerServerListWidget.ServerEntry) {
            this.serverList.remove(((MultiplayerServerListWidget.ServerEntry)entry).getServer());
            this.serverList.saveFile();
            this.serverListWidget.setSelected(null);
            this.serverListWidget.setServers(this.serverList);
        }
        this.client.setScreen((Screen)this);
    }

    private void editEntry(boolean confirmedAction) {
        MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelectedOrNull();
        if (confirmedAction && entry instanceof MultiplayerServerListWidget.ServerEntry) {
            ServerInfo serverInfo = ((MultiplayerServerListWidget.ServerEntry)entry).getServer();
            serverInfo.name = this.selectedEntry.name;
            serverInfo.address = this.selectedEntry.address;
            serverInfo.copyWithSettingsFrom(this.selectedEntry);
            this.serverList.saveFile();
            this.serverListWidget.setServers(this.serverList);
        }
        this.client.setScreen((Screen)this);
    }

    private void addEntry(boolean confirmedAction) {
        if (confirmedAction) {
            ServerInfo serverInfo = this.serverList.tryUnhide(this.selectedEntry.address);
            if (serverInfo != null) {
                serverInfo.copyFrom(this.selectedEntry);
                this.serverList.saveFile();
            } else {
                this.serverList.add(this.selectedEntry, false);
                this.serverList.saveFile();
            }
            this.serverListWidget.setSelected(null);
            this.serverListWidget.setServers(this.serverList);
        }
        this.client.setScreen((Screen)this);
    }

    private void directConnect(boolean confirmedAction) {
        if (confirmedAction) {
            ServerInfo serverInfo = this.serverList.get(this.selectedEntry.address);
            if (serverInfo == null) {
                this.serverList.add(this.selectedEntry, true);
                this.serverList.saveFile();
                this.connect(this.selectedEntry);
            } else {
                this.connect(serverInfo);
            }
        } else {
            this.client.setScreen((Screen)this);
        }
    }

    public boolean keyPressed(KeyInput input) {
        if (super.keyPressed(input)) {
            return true;
        }
        if (input.key() == 294) {
            this.refresh();
            return true;
        }
        return false;
    }

    public void connect(ServerInfo entry) {
        ConnectScreen.connect((Screen)this, (MinecraftClient)this.client, (ServerAddress)ServerAddress.parse((String)entry.address), (ServerInfo)entry, (boolean)false, null);
    }

    protected void updateButtonActivationStates() {
        this.buttonJoin.active = false;
        this.buttonEdit.active = false;
        this.buttonDelete.active = false;
        MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelectedOrNull();
        if (entry != null && !(entry instanceof MultiplayerServerListWidget.ScanningEntry)) {
            this.buttonJoin.active = true;
            if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
                this.buttonEdit.active = true;
                this.buttonDelete.active = true;
            }
        }
    }

    public MultiplayerServerListPinger getServerListPinger() {
        return this.serverListPinger;
    }

    public ServerList getServerList() {
        return this.serverList;
    }
}

