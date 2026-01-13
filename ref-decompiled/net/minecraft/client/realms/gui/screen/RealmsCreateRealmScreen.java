/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.NoticeScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.LayoutWidgets
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.realms.RealmsClient
 *  net.minecraft.client.realms.dto.RealmsServer
 *  net.minecraft.client.realms.exception.RealmsServiceException
 *  net.minecraft.client.realms.gui.screen.RealmsCreateRealmScreen
 *  net.minecraft.client.realms.gui.screen.RealmsCreateWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen
 *  net.minecraft.client.realms.gui.screen.RealmsMainScreen
 *  net.minecraft.client.realms.gui.screen.RealmsScreen
 *  net.minecraft.client.realms.task.WorldCreationTask
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.StringHelper
 *  net.minecraft.util.Util
 */
package net.minecraft.client.realms.gui.screen;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsCreateWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.task.WorldCreationTask;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RealmsCreateRealmScreen
extends RealmsScreen {
    private static final Text TITLE_TEXT = Text.translatable((String)"mco.selectServer.create");
    private static final Text WORLD_NAME_TEXT = Text.translatable((String)"mco.configure.world.name");
    private static final Text WORLD_DESCRIPTION_TEXT = Text.translatable((String)"mco.configure.world.description");
    private static final int field_45243 = 10;
    private static final int field_45244 = 210;
    private final RealmsMainScreen parent;
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);
    private TextFieldWidget nameBox;
    private TextFieldWidget descriptionBox;
    private final Runnable worldCreator;

    public RealmsCreateRealmScreen(RealmsMainScreen parent, RealmsServer server, boolean prerelease) {
        super(TITLE_TEXT);
        this.parent = parent;
        this.worldCreator = () -> this.createWorld(server, prerelease);
    }

    public void init() {
        this.layout.addHeader(this.title, this.textRenderer);
        DirectionalLayoutWidget directionalLayoutWidget = ((DirectionalLayoutWidget)this.layout.addBody((Widget)DirectionalLayoutWidget.vertical())).spacing(10);
        ButtonWidget buttonWidget = ButtonWidget.builder((Text)ScreenTexts.CONTINUE, button -> this.worldCreator.run()).build();
        buttonWidget.active = false;
        this.nameBox = new TextFieldWidget(this.textRenderer, 210, 20, WORLD_NAME_TEXT);
        this.nameBox.setChangedListener(name -> {
            buttonWidget.active = !StringHelper.isBlank((String)name);
        });
        this.descriptionBox = new TextFieldWidget(this.textRenderer, 210, 20, WORLD_DESCRIPTION_TEXT);
        directionalLayoutWidget.add((Widget)LayoutWidgets.createLabeledWidget((TextRenderer)this.textRenderer, (Widget)this.nameBox, (Text)WORLD_NAME_TEXT));
        directionalLayoutWidget.add((Widget)LayoutWidgets.createLabeledWidget((TextRenderer)this.textRenderer, (Widget)this.descriptionBox, (Text)WORLD_DESCRIPTION_TEXT));
        DirectionalLayoutWidget directionalLayoutWidget2 = (DirectionalLayoutWidget)this.layout.addFooter((Widget)DirectionalLayoutWidget.horizontal().spacing(10));
        directionalLayoutWidget2.add((Widget)buttonWidget);
        directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)ScreenTexts.BACK, button -> this.close()).build());
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    protected void setInitialFocus() {
        this.setInitialFocus((Element)this.nameBox);
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
    }

    private void createWorld(RealmsServer realmsServer, boolean prerelease) {
        if (!realmsServer.isPrerelease() && prerelease) {
            AtomicBoolean atomicBoolean = new AtomicBoolean();
            this.client.setScreen((Screen)new NoticeScreen(() -> {
                atomicBoolean.set(true);
                this.parent.removeSelection();
                this.client.setScreen((Screen)this.parent);
            }, (Text)Text.translatable((String)"mco.upload.preparing"), (Text)Text.empty()));
            ((CompletableFuture)CompletableFuture.supplyAsync(() -> RealmsCreateRealmScreen.createPrereleaseServer((RealmsServer)realmsServer), (Executor)Util.getMainWorkerExecutor()).thenAcceptAsync(prereleaseServer -> {
                if (!atomicBoolean.get()) {
                    this.createWorld(prereleaseServer);
                }
            }, (Executor)this.client)).exceptionallyAsync(throwable -> {
                MutableText text;
                this.parent.removeSelection();
                Throwable throwable2 = throwable.getCause();
                if (throwable2 instanceof RealmsServiceException) {
                    RealmsServiceException realmsServiceException = (RealmsServiceException)throwable2;
                    text = realmsServiceException.error.getText();
                } else {
                    text = Text.translatable((String)"mco.errorMessage.initialize.failed");
                }
                this.client.setScreen((Screen)new RealmsGenericErrorScreen((Text)text, (Screen)this.parent));
                return null;
            }, (Executor)this.client);
        } else {
            this.createWorld(realmsServer);
        }
    }

    private static RealmsServer createPrereleaseServer(RealmsServer parent) {
        RealmsClient realmsClient = RealmsClient.create();
        try {
            return realmsClient.createPrereleaseServer(Long.valueOf(parent.id));
        }
        catch (RealmsServiceException realmsServiceException) {
            throw new RuntimeException(realmsServiceException);
        }
    }

    private void createWorld(RealmsServer server) {
        WorldCreationTask worldCreationTask = new WorldCreationTask(server.id, this.nameBox.getText(), this.descriptionBox.getText());
        RealmsCreateWorldScreen realmsCreateWorldScreen = RealmsCreateWorldScreen.newRealm((Screen)this, (RealmsServer)server, (WorldCreationTask)worldCreationTask, () -> this.client.execute(() -> {
            RealmsMainScreen.resetServerList();
            this.client.setScreen((Screen)this.parent);
        }));
        this.client.setScreen((Screen)realmsCreateWorldScreen);
    }

    public void close() {
        this.client.setScreen((Screen)this.parent);
    }
}

