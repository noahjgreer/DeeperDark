/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.tab.GridScreenTab
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.GridWidget
 *  net.minecraft.client.gui.widget.GridWidget$Adder
 *  net.minecraft.client.gui.widget.Positioner
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.realms.dto.RealmsServer
 *  net.minecraft.client.realms.dto.RealmsServer$WorldType
 *  net.minecraft.client.realms.dto.RealmsSlot
 *  net.minecraft.client.realms.dto.WorldTemplate
 *  net.minecraft.client.realms.dto.WorldTemplate$WorldTemplateType
 *  net.minecraft.client.realms.gui.RealmsPopups
 *  net.minecraft.client.realms.gui.RealmsWorldSlotButton
 *  net.minecraft.client.realms.gui.RealmsWorldSlotButton$State
 *  net.minecraft.client.realms.gui.screen.RealmsBackupScreen
 *  net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsCreateWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen
 *  net.minecraft.client.realms.gui.screen.RealmsSelectWorldTemplateScreen
 *  net.minecraft.client.realms.gui.screen.RealmsSlotOptionsScreen
 *  net.minecraft.client.realms.gui.screen.tab.RealmsUpdatableTab
 *  net.minecraft.client.realms.gui.screen.tab.RealmsWorldsTab
 *  net.minecraft.client.realms.gui.screen.tab.RealmsWorldsTab$1
 *  net.minecraft.client.realms.task.LongRunningTask
 *  net.minecraft.client.realms.task.SwitchMinigameTask
 *  net.minecraft.client.realms.task.SwitchSlotTask
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.gui.screen.tab;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsSlot;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.gui.RealmsPopups;
import net.minecraft.client.realms.gui.RealmsWorldSlotButton;
import net.minecraft.client.realms.gui.screen.RealmsBackupScreen;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsCreateWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsSelectWorldTemplateScreen;
import net.minecraft.client.realms.gui.screen.RealmsSlotOptionsScreen;
import net.minecraft.client.realms.gui.screen.tab.RealmsUpdatableTab;
import net.minecraft.client.realms.gui.screen.tab.RealmsWorldsTab;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.client.realms.task.SwitchMinigameTask;
import net.minecraft.client.realms.task.SwitchSlotTask;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class RealmsWorldsTab
extends GridScreenTab
implements RealmsUpdatableTab {
    static final Text TITLE_TEXT = Text.translatable((String)"mco.configure.worlds.title");
    private final RealmsConfigureWorldScreen screen;
    private final MinecraftClient client;
    private RealmsServer server;
    private final ButtonWidget optionsButton;
    private final ButtonWidget backupButton;
    private final ButtonWidget resetButton;
    private final List<RealmsWorldSlotButton> slotButtons = Lists.newArrayList();

    RealmsWorldsTab(RealmsConfigureWorldScreen screen, MinecraftClient client, RealmsServer server) {
        super(TITLE_TEXT);
        this.screen = screen;
        this.client = client;
        this.server = server;
        GridWidget.Adder adder = this.grid.setSpacing(20).createAdder(1);
        GridWidget.Adder adder2 = new GridWidget().setSpacing(16).createAdder(4);
        this.slotButtons.clear();
        for (int i = 1; i < 5; ++i) {
            this.slotButtons.add((RealmsWorldSlotButton)adder2.add((Widget)this.createSlotButton(i), Positioner.create().alignBottom()));
        }
        adder.add((Widget)adder2.getGridWidget());
        GridWidget.Adder adder3 = new GridWidget().setSpacing(8).createAdder(1);
        this.optionsButton = (ButtonWidget)adder3.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"mco.configure.world.buttons.options"), button -> client.setScreen((Screen)new RealmsSlotOptionsScreen(screen, ((RealmsSlot)realmsServer.slots.get(realmsServer.activeSlot)).copy(), realmsServer.worldType, realmsServer.activeSlot))).dimensions(0, 0, 150, 20).build());
        this.backupButton = (ButtonWidget)adder3.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"mco.configure.world.backup"), button -> client.setScreen((Screen)new RealmsBackupScreen(screen, server.copy(), realmsServer.activeSlot))).dimensions(0, 0, 150, 20).build());
        this.resetButton = (ButtonWidget)adder3.add((Widget)ButtonWidget.builder((Text)Text.empty(), button -> this.reset()).dimensions(0, 0, 150, 20).build());
        adder.add((Widget)adder3.getGridWidget(), Positioner.create().alignHorizontalCenter());
        this.backupButton.active = true;
        this.update(server);
    }

    private void reset() {
        if (this.isMinigame()) {
            this.client.setScreen((Screen)new RealmsSelectWorldTemplateScreen((Text)Text.translatable((String)"mco.template.title.minigame"), arg_0 -> this.switchMinigame(arg_0), RealmsServer.WorldType.MINIGAME, null));
        } else {
            this.client.setScreen((Screen)RealmsCreateWorldScreen.resetWorld((Screen)this.screen, (RealmsServer)this.server.copy(), () -> this.client.execute(() -> this.client.setScreen((Screen)this.screen.getNewScreen()))));
        }
    }

    private void switchMinigame(@Nullable WorldTemplate template) {
        if (template != null && WorldTemplate.WorldTemplateType.MINIGAME == template.type()) {
            this.screen.stateChanged();
            RealmsConfigureWorldScreen realmsConfigureWorldScreen = this.screen.getNewScreen();
            this.client.setScreen((Screen)new RealmsLongRunningMcoTaskScreen((Screen)realmsConfigureWorldScreen, new LongRunningTask[]{new SwitchMinigameTask(this.server.id, template, realmsConfigureWorldScreen)}));
        } else {
            this.client.setScreen((Screen)this.screen);
        }
    }

    private boolean isMinigame() {
        return this.server.isMinigame();
    }

    public void onLoaded(RealmsServer server) {
        this.update(server);
    }

    public void update(RealmsServer server) {
        this.server = server;
        this.optionsButton.active = !server.expired && !this.isMinigame();
        boolean bl = this.resetButton.active = !server.expired;
        if (this.isMinigame()) {
            this.resetButton.setMessage((Text)Text.translatable((String)"mco.configure.world.buttons.switchminigame"));
        } else {
            boolean bl2;
            boolean bl3 = bl2 = server.slots.containsKey(server.activeSlot) && ((RealmsSlot)server.slots.get((Object)Integer.valueOf((int)server.activeSlot))).options.empty;
            if (bl2) {
                this.resetButton.setMessage((Text)Text.translatable((String)"mco.configure.world.buttons.newworld"));
            } else {
                this.resetButton.setMessage((Text)Text.translatable((String)"mco.configure.world.buttons.resetworld"));
            }
        }
        this.backupButton.active = !this.isMinigame();
        for (RealmsWorldSlotButton realmsWorldSlotButton : this.slotButtons) {
            RealmsWorldSlotButton.State state = realmsWorldSlotButton.setServer(server);
            if (state.active) {
                realmsWorldSlotButton.setDimensions(80, 80);
                continue;
            }
            realmsWorldSlotButton.setDimensions(50, 50);
        }
    }

    private RealmsWorldSlotButton createSlotButton(int slotIndex) {
        return new RealmsWorldSlotButton(0, 0, 80, 80, slotIndex, this.server, button -> {
            RealmsWorldSlotButton.State state = ((RealmsWorldSlotButton)button).getState();
            switch (1.field_19812[state.action.ordinal()]) {
                case 1: {
                    break;
                }
                case 2: {
                    if (state.minigame) {
                        this.showSwitchMinigameScreen();
                        break;
                    }
                    if (state.empty) {
                        this.createWorld(slotIndex, this.server);
                        break;
                    }
                    this.switchWorld(slotIndex, this.server);
                    break;
                }
                default: {
                    throw new IllegalStateException("Unknown action " + String.valueOf(state.action));
                }
            }
        });
    }

    private void showSwitchMinigameScreen() {
        RealmsSelectWorldTemplateScreen realmsSelectWorldTemplateScreen = new RealmsSelectWorldTemplateScreen((Text)Text.translatable((String)"mco.template.title.minigame"), arg_0 -> this.switchMinigame(arg_0), RealmsServer.WorldType.MINIGAME, null, List.of(Text.translatable((String)"mco.minigame.world.info.line1").withColor(-4539718), Text.translatable((String)"mco.minigame.world.info.line2").withColor(-4539718)));
        this.client.setScreen((Screen)realmsSelectWorldTemplateScreen);
    }

    private void switchWorld(int slotId, RealmsServer server) {
        this.client.setScreen((Screen)RealmsPopups.createInfoPopup((Screen)this.screen, (Text)Text.translatable((String)"mco.configure.world.slot.switch.question.line1"), popup -> {
            RealmsConfigureWorldScreen realmsConfigureWorldScreen = this.screen.getNewScreen();
            this.screen.stateChanged();
            this.client.setScreen((Screen)new RealmsLongRunningMcoTaskScreen((Screen)realmsConfigureWorldScreen, new LongRunningTask[]{new SwitchSlotTask(realmsServer.id, slotId, () -> this.client.execute(() -> this.client.setScreen((Screen)realmsConfigureWorldScreen)))}));
        }));
    }

    private void createWorld(int slotId, RealmsServer server) {
        this.client.setScreen((Screen)RealmsPopups.createInfoPopup((Screen)this.screen, (Text)Text.translatable((String)"mco.configure.world.slot.switch.question.line1"), popup -> {
            this.screen.stateChanged();
            RealmsCreateWorldScreen realmsCreateWorldScreen = RealmsCreateWorldScreen.newWorld((Screen)this.screen, (int)slotId, (RealmsServer)server, () -> this.client.execute(() -> this.client.setScreen((Screen)this.screen.getNewScreen())));
            this.client.setScreen((Screen)realmsCreateWorldScreen);
        }));
    }
}

