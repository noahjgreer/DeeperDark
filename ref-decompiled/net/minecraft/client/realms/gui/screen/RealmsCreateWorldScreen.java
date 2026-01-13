/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.EmptyWidget
 *  net.minecraft.client.gui.widget.GridWidget
 *  net.minecraft.client.gui.widget.GridWidget$Adder
 *  net.minecraft.client.gui.widget.Positioner
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.realms.dto.RealmsServer
 *  net.minecraft.client.realms.dto.RealmsServer$WorldType
 *  net.minecraft.client.realms.dto.WorldTemplate
 *  net.minecraft.client.realms.dto.WorldTemplatePaginatedList
 *  net.minecraft.client.realms.gui.screen.RealmsCreateWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsCreateWorldScreen$FrameButton
 *  net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen
 *  net.minecraft.client.realms.gui.screen.RealmsMainScreen
 *  net.minecraft.client.realms.gui.screen.RealmsScreen
 *  net.minecraft.client.realms.gui.screen.RealmsSelectFileToUploadScreen
 *  net.minecraft.client.realms.gui.screen.RealmsSelectWorldTemplateScreen
 *  net.minecraft.client.realms.gui.screen.RealmsWorldCreating
 *  net.minecraft.client.realms.task.LongRunningTask
 *  net.minecraft.client.realms.task.ResettingWorldTemplateTask
 *  net.minecraft.client.realms.task.SwitchSlotTask
 *  net.minecraft.client.realms.task.WorldCreationTask
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.dto.WorldTemplatePaginatedList;
import net.minecraft.client.realms.gui.screen.RealmsCreateWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.gui.screen.RealmsSelectFileToUploadScreen;
import net.minecraft.client.realms.gui.screen.RealmsSelectWorldTemplateScreen;
import net.minecraft.client.realms.gui.screen.RealmsWorldCreating;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.client.realms.task.ResettingWorldTemplateTask;
import net.minecraft.client.realms.task.SwitchSlotTask;
import net.minecraft.client.realms.task.WorldCreationTask;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsCreateWorldScreen
extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Text CREATE_REALM_TITLE = Text.translatable((String)"mco.selectServer.create");
    private static final Text CREATE_REALM_SUBTITLE = Text.translatable((String)"mco.selectServer.create.subtitle").withColor(-6250336);
    private static final Text CREATE_WORLD_TITLE = Text.translatable((String)"mco.configure.world.switch.slot");
    private static final Text CREATE_WORLD_SUBTITLE = Text.translatable((String)"mco.configure.world.switch.slot.subtitle").withColor(-6250336);
    private static final Text NEW_WORLD_BUTTON_TEXT = Text.translatable((String)"mco.reset.world.generate");
    private static final Text RESET_WORLD_TITLE = Text.translatable((String)"mco.reset.world.title");
    private static final Text RESET_WORLD_SUBTITLE = Text.translatable((String)"mco.reset.world.warning").withColor(-65536);
    public static final Text CREATING_TEXT = Text.translatable((String)"mco.create.world.reset.title");
    private static final Text RESETTING_TEXT = Text.translatable((String)"mco.reset.world.resetting.screen.title");
    private static final Text TEMPLATE_TEXT = Text.translatable((String)"mco.reset.world.template");
    private static final Text ADVENTURE_TEXT = Text.translatable((String)"mco.reset.world.adventure");
    private static final Text EXPERIENCE_TEXT = Text.translatable((String)"mco.reset.world.experience");
    private static final Text INSPIRATION_TEXT = Text.translatable((String)"mco.reset.world.inspiration");
    private final Screen parent;
    private final RealmsServer serverData;
    private final Text subtitle;
    private final Text taskTitle;
    private static final Identifier UPLOAD_TEXTURE = Identifier.ofVanilla((String)"textures/gui/realms/upload.png");
    private static final Identifier ADVENTURE_TEXTURE = Identifier.ofVanilla((String)"textures/gui/realms/adventure.png");
    private static final Identifier SURVIVAL_SPAWN_TEXTURE = Identifier.ofVanilla((String)"textures/gui/realms/survival_spawn.png");
    private static final Identifier NEW_WORLD_TEXTURE = Identifier.ofVanilla((String)"textures/gui/realms/new_world.png");
    private static final Identifier EXPERIENCE_TEXTURE = Identifier.ofVanilla((String)"textures/gui/realms/experience.png");
    private static final Identifier INSPIRATION_TEXTURE = Identifier.ofVanilla((String)"textures/gui/realms/inspiration.png");
    WorldTemplatePaginatedList normalWorldTemplates;
    WorldTemplatePaginatedList adventureWorldTemplates;
    WorldTemplatePaginatedList experienceWorldTemplates;
    WorldTemplatePaginatedList inspirationWorldTemplates;
    public final int slot;
    private final @Nullable WorldCreationTask creationTask;
    private final Runnable callback;
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);

    private RealmsCreateWorldScreen(Screen parent, RealmsServer serverData, int slot, Text title, Text subtitle, Text text, Runnable runnable) {
        this(parent, serverData, slot, title, subtitle, text, null, runnable);
    }

    public RealmsCreateWorldScreen(Screen parent, RealmsServer serverData, int slot, Text title, Text subtitle, Text text, @Nullable WorldCreationTask worldCreationTask, Runnable runnable) {
        super(title);
        this.parent = parent;
        this.serverData = serverData;
        this.slot = slot;
        this.subtitle = subtitle;
        this.taskTitle = text;
        this.creationTask = worldCreationTask;
        this.callback = runnable;
    }

    public static RealmsCreateWorldScreen newRealm(Screen parent, RealmsServer serverData, WorldCreationTask creationTask, Runnable callback) {
        return new RealmsCreateWorldScreen(parent, serverData, serverData.activeSlot, CREATE_REALM_TITLE, CREATE_REALM_SUBTITLE, CREATING_TEXT, creationTask, callback);
    }

    public static RealmsCreateWorldScreen newWorld(Screen parent, int slot, RealmsServer serverData, Runnable callback) {
        return new RealmsCreateWorldScreen(parent, serverData, slot, CREATE_WORLD_TITLE, CREATE_WORLD_SUBTITLE, CREATING_TEXT, callback);
    }

    public static RealmsCreateWorldScreen resetWorld(Screen parent, RealmsServer serverData, Runnable callback) {
        return new RealmsCreateWorldScreen(parent, serverData, serverData.activeSlot, RESET_WORLD_TITLE, RESET_WORLD_SUBTITLE, RESETTING_TEXT, callback);
    }

    public void init() {
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addHeader((Widget)DirectionalLayoutWidget.vertical());
        Positioner positioner = directionalLayoutWidget.getMainPositioner();
        Objects.requireNonNull(this.textRenderer);
        positioner.margin(9 / 3);
        directionalLayoutWidget.add((Widget)new TextWidget(this.title, this.textRenderer), Positioner::alignHorizontalCenter);
        directionalLayoutWidget.add((Widget)new TextWidget(this.subtitle, this.textRenderer), Positioner::alignHorizontalCenter);
        new /* Unavailable Anonymous Inner Class!! */.start();
        GridWidget gridWidget = (GridWidget)this.layout.addBody((Widget)new GridWidget());
        GridWidget.Adder adder = gridWidget.createAdder(3);
        adder.getMainPositioner().marginX(16);
        adder.add((Widget)new FrameButton(this, this.client.textRenderer, NEW_WORLD_BUTTON_TEXT, NEW_WORLD_TEXTURE, button -> RealmsWorldCreating.showCreateWorldScreen((MinecraftClient)this.client, (Screen)this.parent, (Screen)this, (int)this.slot, (RealmsServer)this.serverData, (WorldCreationTask)this.creationTask)));
        adder.add((Widget)new FrameButton(this, this.client.textRenderer, RealmsSelectFileToUploadScreen.TITLE, UPLOAD_TEXTURE, button -> this.client.setScreen((Screen)new RealmsSelectFileToUploadScreen(this.creationTask, this.serverData.id, this.slot, this))));
        adder.add((Widget)new FrameButton(this, this.client.textRenderer, TEMPLATE_TEXT, SURVIVAL_SPAWN_TEXTURE, button -> this.client.setScreen((Screen)new RealmsSelectWorldTemplateScreen(TEMPLATE_TEXT, arg_0 -> this.onSelectWorldTemplate(arg_0), RealmsServer.WorldType.NORMAL, this.normalWorldTemplates))));
        adder.add((Widget)EmptyWidget.ofHeight((int)16), 3);
        adder.add((Widget)new FrameButton(this, this.client.textRenderer, ADVENTURE_TEXT, ADVENTURE_TEXTURE, button -> this.client.setScreen((Screen)new RealmsSelectWorldTemplateScreen(ADVENTURE_TEXT, arg_0 -> this.onSelectWorldTemplate(arg_0), RealmsServer.WorldType.ADVENTUREMAP, this.adventureWorldTemplates))));
        adder.add((Widget)new FrameButton(this, this.client.textRenderer, EXPERIENCE_TEXT, EXPERIENCE_TEXTURE, button -> this.client.setScreen((Screen)new RealmsSelectWorldTemplateScreen(EXPERIENCE_TEXT, arg_0 -> this.onSelectWorldTemplate(arg_0), RealmsServer.WorldType.EXPERIENCE, this.experienceWorldTemplates))));
        adder.add((Widget)new FrameButton(this, this.client.textRenderer, INSPIRATION_TEXT, INSPIRATION_TEXTURE, button -> this.client.setScreen((Screen)new RealmsSelectWorldTemplateScreen(INSPIRATION_TEXT, arg_0 -> this.onSelectWorldTemplate(arg_0), RealmsServer.WorldType.INSPIRATION, this.inspirationWorldTemplates))));
        this.layout.addFooter((Widget)ButtonWidget.builder((Text)ScreenTexts.BACK, button -> this.close()).build());
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
    }

    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences((Text[])new Text[]{this.getTitle(), this.subtitle});
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    private void onSelectWorldTemplate(@Nullable WorldTemplate template) {
        this.client.setScreen((Screen)this);
        if (template != null) {
            this.runTasks((LongRunningTask)new ResettingWorldTemplateTask(template, this.serverData.id, this.taskTitle, this.callback));
        }
        RealmsMainScreen.resetServerList();
    }

    private void runTasks(LongRunningTask task) {
        ArrayList<Object> list = new ArrayList<Object>();
        if (this.creationTask != null) {
            list.add(this.creationTask);
        }
        if (this.slot != this.serverData.activeSlot) {
            list.add(new SwitchSlotTask(this.serverData.id, this.slot, () -> {}));
        }
        list.add(task);
        this.client.setScreen((Screen)new RealmsLongRunningMcoTaskScreen(this.parent, list.toArray(new LongRunningTask[0])));
    }

    static /* synthetic */ MinecraftClient method_25205(RealmsCreateWorldScreen realmsCreateWorldScreen) {
        return realmsCreateWorldScreen.client;
    }

    static /* synthetic */ TextRenderer method_53803(RealmsCreateWorldScreen realmsCreateWorldScreen) {
        return realmsCreateWorldScreen.textRenderer;
    }
}

