/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.AbstractParentElement
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Drawable
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.ParentElement
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.Selectable
 *  net.minecraft.client.gui.Selectable$SelectionType
 *  net.minecraft.client.gui.navigation.GuiNavigation
 *  net.minecraft.client.gui.navigation.GuiNavigation$Arrow
 *  net.minecraft.client.gui.navigation.GuiNavigation$Down
 *  net.minecraft.client.gui.navigation.GuiNavigation$Tab
 *  net.minecraft.client.gui.navigation.GuiNavigationPath
 *  net.minecraft.client.gui.navigation.Navigable
 *  net.minecraft.client.gui.navigation.NavigationDirection
 *  net.minecraft.client.gui.screen.ConfirmLinkScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.Screen$SelectedElementNarrationData
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.screen.narration.NarrationPart
 *  net.minecraft.client.gui.screen.narration.ScreenNarrator
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.CyclingButtonWidget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.option.NarratorMode
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item$TooltipContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.tooltip.TooltipType
 *  net.minecraft.item.tooltip.TooltipType$Default
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.common.CustomClickActionC2SPacket
 *  net.minecraft.server.command.CommandManager
 *  net.minecraft.sound.MusicSound
 *  net.minecraft.text.ClickEvent
 *  net.minecraft.text.ClickEvent$CopyToClipboard
 *  net.minecraft.text.ClickEvent$Custom
 *  net.minecraft.text.ClickEvent$OpenFile
 *  net.minecraft.text.ClickEvent$OpenUrl
 *  net.minecraft.text.ClickEvent$RunCommand
 *  net.minecraft.text.ClickEvent$ShowDialog
 *  net.minecraft.text.ClickEvent$SuggestCommand
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 *  net.minecraft.util.crash.CrashReport
 *  net.minecraft.util.crash.CrashReportSection
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.lang.runtime.SwitchBootstraps;
import java.net.URI;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.Navigable;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.screen.narration.ScreenNarrator;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CustomClickActionC2SPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.sound.MusicSound;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public abstract class Screen
extends AbstractParentElement
implements Drawable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Text SCREEN_USAGE_TEXT = Text.translatable((String)"narrator.screen.usage");
    public static final Identifier MENU_BACKGROUND_TEXTURE = Identifier.ofVanilla((String)"textures/gui/menu_background.png");
    public static final Identifier HEADER_SEPARATOR_TEXTURE = Identifier.ofVanilla((String)"textures/gui/header_separator.png");
    public static final Identifier FOOTER_SEPARATOR_TEXTURE = Identifier.ofVanilla((String)"textures/gui/footer_separator.png");
    private static final Identifier INWORLD_MENU_BACKGROUND_TEXTURE = Identifier.ofVanilla((String)"textures/gui/inworld_menu_background.png");
    public static final Identifier INWORLD_HEADER_SEPARATOR_TEXTURE = Identifier.ofVanilla((String)"textures/gui/inworld_header_separator.png");
    public static final Identifier INWORLD_FOOTER_SEPARATOR_TEXTURE = Identifier.ofVanilla((String)"textures/gui/inworld_footer_separator.png");
    protected static final float field_60460 = 2000.0f;
    protected final Text title;
    private final List<Element> children = Lists.newArrayList();
    private final List<Selectable> selectables = Lists.newArrayList();
    protected final MinecraftClient client;
    private boolean screenInitialized;
    public int width;
    public int height;
    private final List<Drawable> drawables = Lists.newArrayList();
    protected final TextRenderer textRenderer;
    private static final long SCREEN_INIT_NARRATION_DELAY;
    private static final long NARRATOR_MODE_CHANGE_DELAY;
    private static final long MOUSE_MOVE_NARRATION_DELAY = 750L;
    private static final long MOUSE_PRESS_SCROLL_NARRATION_DELAY = 200L;
    private static final long KEY_PRESS_NARRATION_DELAY = 200L;
    private final ScreenNarrator narrator = new ScreenNarrator();
    private long elementNarrationStartTime = Long.MIN_VALUE;
    private long screenNarrationStartTime = Long.MAX_VALUE;
    protected @Nullable CyclingButtonWidget<NarratorMode> narratorToggleButton;
    private @Nullable Selectable selected;
    protected final Executor executor;

    protected Screen(Text title) {
        this(MinecraftClient.getInstance(), MinecraftClient.getInstance().textRenderer, title);
    }

    protected Screen(MinecraftClient minecraftClient, TextRenderer textRenderer, Text text) {
        this.client = minecraftClient;
        this.textRenderer = textRenderer;
        this.title = text;
        this.executor = runnable -> minecraftClient.execute(() -> {
            if (minecraftClient.currentScreen == this) {
                runnable.run();
            }
        });
    }

    public Text getTitle() {
        return this.title;
    }

    public Text getNarratedTitle() {
        return this.getTitle();
    }

    public final void renderWithTooltip(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.createNewRootLayer();
        this.renderBackground(context, mouseX, mouseY, deltaTicks);
        context.createNewRootLayer();
        this.render(context, mouseX, mouseY, deltaTicks);
        context.drawDeferredElements();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        for (Drawable drawable : this.drawables) {
            drawable.render(context, mouseX, mouseY, deltaTicks);
        }
    }

    public boolean keyPressed(KeyInput input) {
        GuiNavigation.Arrow guiNavigation;
        if (input.isEscape() && this.shouldCloseOnEsc()) {
            this.close();
            return true;
        }
        if (super.keyPressed(input)) {
            return true;
        }
        switch (input.key()) {
            case 263: {
                GuiNavigation.Arrow arrow = this.getArrowNavigation(NavigationDirection.LEFT);
                break;
            }
            case 262: {
                GuiNavigation.Arrow arrow = this.getArrowNavigation(NavigationDirection.RIGHT);
                break;
            }
            case 265: {
                GuiNavigation.Arrow arrow = this.getArrowNavigation(NavigationDirection.UP);
                break;
            }
            case 264: {
                GuiNavigation.Arrow arrow = this.getArrowNavigation(NavigationDirection.DOWN);
                break;
            }
            case 258: {
                GuiNavigation.Arrow arrow = this.getTabNavigation(!input.hasShift());
                break;
            }
            default: {
                GuiNavigation.Arrow arrow = guiNavigation = null;
            }
        }
        if (guiNavigation != null) {
            GuiNavigationPath guiNavigationPath = super.getNavigationPath((GuiNavigation)guiNavigation);
            if (guiNavigationPath == null && guiNavigation instanceof GuiNavigation.Tab) {
                this.blur();
                guiNavigationPath = super.getNavigationPath((GuiNavigation)guiNavigation);
            }
            if (guiNavigationPath != null) {
                this.switchFocus(guiNavigationPath);
            }
        }
        return false;
    }

    private GuiNavigation.Tab getTabNavigation(boolean bl) {
        return new GuiNavigation.Tab(bl);
    }

    private GuiNavigation.Arrow getArrowNavigation(NavigationDirection direction) {
        return new GuiNavigation.Arrow(direction);
    }

    protected void setInitialFocus() {
        GuiNavigation.Tab tab;
        GuiNavigationPath guiNavigationPath;
        if (this.client.getNavigationType().isKeyboard() && (guiNavigationPath = super.getNavigationPath((GuiNavigation)(tab = new GuiNavigation.Tab(true)))) != null) {
            this.switchFocus(guiNavigationPath);
        }
    }

    protected void setInitialFocus(Element element) {
        GuiNavigationPath guiNavigationPath = GuiNavigationPath.of((ParentElement)this, (GuiNavigationPath)element.getNavigationPath((GuiNavigation)new GuiNavigation.Down()));
        if (guiNavigationPath != null) {
            this.switchFocus(guiNavigationPath);
        }
    }

    public void blur() {
        GuiNavigationPath guiNavigationPath = this.getFocusedPath();
        if (guiNavigationPath != null) {
            guiNavigationPath.setFocused(false);
        }
    }

    @VisibleForTesting
    protected void switchFocus(GuiNavigationPath path) {
        this.blur();
        path.setFocused(true);
    }

    public boolean shouldCloseOnEsc() {
        return true;
    }

    public void close() {
        this.client.setScreen(null);
    }

    protected <T extends Element & Drawable> T addDrawableChild(T drawableElement) {
        this.drawables.add((Drawable)drawableElement);
        return (T)this.addSelectableChild(drawableElement);
    }

    protected <T extends Drawable> T addDrawable(T drawable) {
        this.drawables.add(drawable);
        return drawable;
    }

    protected <T extends Element & Selectable> T addSelectableChild(T child) {
        this.children.add(child);
        this.selectables.add((Selectable)child);
        return child;
    }

    protected void remove(Element child) {
        if (child instanceof Drawable) {
            this.drawables.remove((Drawable)child);
        }
        if (child instanceof Selectable) {
            this.selectables.remove((Selectable)child);
        }
        if (this.getFocused() == child) {
            this.blur();
        }
        this.children.remove(child);
    }

    protected void clearChildren() {
        this.drawables.clear();
        this.children.clear();
        this.selectables.clear();
    }

    public static List<Text> getTooltipFromItem(MinecraftClient client, ItemStack stack) {
        return stack.getTooltip(Item.TooltipContext.create((World)client.world), (PlayerEntity)client.player, (TooltipType)(client.options.advancedItemTooltips ? TooltipType.Default.ADVANCED : TooltipType.Default.BASIC));
    }

    protected void insertText(String text, boolean override) {
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected static void handleClickEvent(ClickEvent clickEvent, MinecraftClient client, @Nullable Screen screenAfterRun) {
        ClientPlayerEntity clientPlayerEntity = Objects.requireNonNull(client.player, "Player not available");
        ClickEvent clickEvent2 = clickEvent;
        Objects.requireNonNull(clickEvent2);
        ClickEvent clickEvent3 = clickEvent2;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ClickEvent.RunCommand.class, ClickEvent.ShowDialog.class, ClickEvent.Custom.class}, (Object)clickEvent3, n)) {
            case 0: {
                String string2;
                ClickEvent.RunCommand runCommand = (ClickEvent.RunCommand)clickEvent3;
                try {
                    String string;
                    string2 = string = runCommand.command();
                }
                catch (Throwable throwable) {
                    throw new MatchException(throwable.toString(), throwable);
                }
                Screen.handleRunCommand((ClientPlayerEntity)clientPlayerEntity, (String)string2, (Screen)screenAfterRun);
                return;
            }
            case 1: {
                ClickEvent.ShowDialog showDialog = (ClickEvent.ShowDialog)clickEvent3;
                clientPlayerEntity.networkHandler.showDialog(showDialog.dialog(), screenAfterRun);
                return;
            }
            case 2: {
                ClickEvent.Custom custom = (ClickEvent.Custom)clickEvent3;
                clientPlayerEntity.networkHandler.sendPacket((Packet)new CustomClickActionC2SPacket(custom.id(), custom.payload()));
                if (client.currentScreen == screenAfterRun) return;
                client.setScreen(screenAfterRun);
                return;
            }
        }
        Screen.handleBasicClickEvent((ClickEvent)clickEvent, (MinecraftClient)client, (Screen)screenAfterRun);
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    protected static void handleBasicClickEvent(ClickEvent clickEvent, MinecraftClient client, @Nullable Screen screenAfterRun) {
        block11: {
            v0 = clickEvent;
            Objects.requireNonNull(v0);
            var4_3 = v0;
            var5_5 = 0;
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ClickEvent.OpenUrl.class, ClickEvent.OpenFile.class, ClickEvent.SuggestCommand.class, ClickEvent.CopyToClipboard.class}, (Object)var4_3, var5_5)) {
                case 0: {
                    var6_6 = (ClickEvent.OpenUrl)var4_3;
                    uRI = var8_7 = var6_6.uri();
                    Screen.handleOpenUri((MinecraftClient)client, (Screen)screenAfterRun, (URI)uRI);
                    v1 = false;
                    break;
                }
                case 1: {
                    openFile = (ClickEvent.OpenFile)var4_3;
                    Util.getOperatingSystem().open(openFile.file());
                    v1 = true;
                    break;
                }
                case 2: {
                    var9_9 = (ClickEvent.SuggestCommand)var4_3;
                    string /* !! */  = var11_10 /* !! */  = var9_9.command();
                    if (screenAfterRun == null) ** GOTO lbl26
                    screenAfterRun.insertText((String)string /* !! */ , true);
lbl26:
                    // 2 sources

                    v1 = true;
                    break;
                }
                case 3: {
                    var11_10 /* !! */  = (ClickEvent.CopyToClipboard)var4_3;
                    string2 = var13_12 = var11_10 /* !! */ .value();
                    client.keyboard.setClipboard(string2);
                    v1 = true;
                    break;
                }
                default: {
                    Screen.LOGGER.error("Don't know how to handle {}", (Object)clickEvent);
                    v1 = bl = true;
                }
            }
            if (bl && client.currentScreen != screenAfterRun) {
                client.setScreen(screenAfterRun);
            }
            break block11;
            catch (Throwable var4_4) {
                throw new MatchException(var4_4.toString(), var4_4);
            }
        }
    }

    protected static boolean handleOpenUri(MinecraftClient client, @Nullable Screen screen, URI uri) {
        if (!((Boolean)client.options.getChatLinks().getValue()).booleanValue()) {
            return false;
        }
        if (((Boolean)client.options.getChatLinksPrompt().getValue()).booleanValue()) {
            client.setScreen((Screen)new ConfirmLinkScreen(confirmed -> {
                if (confirmed) {
                    Util.getOperatingSystem().open(uri);
                }
                client.setScreen(screen);
            }, uri.toString(), false));
        } else {
            Util.getOperatingSystem().open(uri);
        }
        return true;
    }

    protected static void handleRunCommand(ClientPlayerEntity player, String command, @Nullable Screen screenAfterRun) {
        player.networkHandler.runClickEventCommand(CommandManager.stripLeadingSlash((String)command), screenAfterRun);
    }

    public final void init(int width, int height) {
        this.width = width;
        this.height = height;
        if (!this.screenInitialized) {
            this.init();
            this.setInitialFocus();
        } else {
            this.refreshWidgetPositions();
        }
        this.screenInitialized = true;
        this.narrateScreenIfNarrationEnabled(false);
        if (this.client.getNavigationType().isKeyboard()) {
            this.setElementNarrationStartTime(Long.MAX_VALUE);
        } else {
            this.setElementNarrationDelay(SCREEN_INIT_NARRATION_DELAY);
        }
    }

    protected void clearAndInit() {
        this.clearChildren();
        this.blur();
        this.init();
        this.setInitialFocus();
    }

    protected void setWidgetAlpha(float alpha) {
        for (Element element : this.children()) {
            if (!(element instanceof ClickableWidget)) continue;
            ClickableWidget clickableWidget = (ClickableWidget)element;
            clickableWidget.setAlpha(alpha);
        }
    }

    public List<? extends Element> children() {
        return this.children;
    }

    protected void init() {
    }

    public void tick() {
    }

    public void removed() {
    }

    public void onDisplayed() {
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (this.deferSubtitles()) {
            this.renderInGameBackground(context);
        } else {
            if (this.client.world == null) {
                this.renderPanoramaBackground(context, deltaTicks);
            }
            this.applyBlur(context);
            this.renderDarkening(context);
        }
        this.client.inGameHud.renderDeferredSubtitles();
    }

    protected void applyBlur(DrawContext context) {
        float f = this.client.options.getMenuBackgroundBlurrinessValue();
        if (f >= 1.0f) {
            context.applyBlur();
        }
    }

    protected void renderPanoramaBackground(DrawContext context, float deltaTicks) {
        this.client.gameRenderer.getRotatingPanoramaRenderer().render(context, this.width, this.height, this.allowRotatingPanorama());
    }

    protected void renderDarkening(DrawContext context) {
        this.renderDarkening(context, 0, 0, this.width, this.height);
    }

    protected void renderDarkening(DrawContext context, int x, int y, int width, int height) {
        Screen.renderBackgroundTexture((DrawContext)context, (Identifier)(this.client.world == null ? MENU_BACKGROUND_TEXTURE : INWORLD_MENU_BACKGROUND_TEXTURE), (int)x, (int)y, (float)0.0f, (float)0.0f, (int)width, (int)height);
    }

    public static void renderBackgroundTexture(DrawContext context, Identifier texture, int x, int y, float u, float v, int width, int height) {
        int i = 32;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, 32, 32);
    }

    public void renderInGameBackground(DrawContext context) {
        context.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
    }

    public boolean shouldPause() {
        return true;
    }

    public boolean deferSubtitles() {
        return false;
    }

    protected boolean allowRotatingPanorama() {
        return true;
    }

    public boolean keepOpenThroughPortal() {
        return this.shouldPause();
    }

    protected void refreshWidgetPositions() {
        this.clearAndInit();
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        this.refreshWidgetPositions();
    }

    public void addCrashReportSection(CrashReport report) {
        CrashReportSection crashReportSection = report.addElement("Affected screen", 1);
        crashReportSection.add("Screen name", () -> this.getClass().getCanonicalName());
    }

    protected boolean isValidCharacterForName(String name, int codepoint, int cursorPos) {
        int i = name.indexOf(58);
        int j = name.indexOf(47);
        if (codepoint == 58) {
            return (j == -1 || cursorPos <= j) && i == -1;
        }
        if (codepoint == 47) {
            return cursorPos > i;
        }
        return codepoint == 95 || codepoint == 45 || codepoint >= 97 && codepoint <= 122 || codepoint >= 48 && codepoint <= 57 || codepoint == 46;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return true;
    }

    public void onFilesDropped(List<Path> paths) {
    }

    private void setScreenNarrationDelay(long delayMs, boolean restartElementNarration) {
        this.screenNarrationStartTime = Util.getMeasuringTimeMs() + delayMs;
        if (restartElementNarration) {
            this.elementNarrationStartTime = Long.MIN_VALUE;
        }
    }

    private void setElementNarrationDelay(long delayMs) {
        this.setElementNarrationStartTime(Util.getMeasuringTimeMs() + delayMs);
    }

    private void setElementNarrationStartTime(long startTimeMs) {
        this.elementNarrationStartTime = startTimeMs;
    }

    public void applyMouseMoveNarratorDelay() {
        this.setScreenNarrationDelay(750L, false);
    }

    public void applyMousePressScrollNarratorDelay() {
        this.setScreenNarrationDelay(200L, true);
    }

    public void applyKeyPressNarratorDelay() {
        this.setScreenNarrationDelay(200L, true);
    }

    private boolean isNarratorActive() {
        return SharedConstants.UI_NARRATION || this.client.getNarratorManager().isActive();
    }

    public void updateNarrator() {
        long l;
        if (this.isNarratorActive() && (l = Util.getMeasuringTimeMs()) > this.screenNarrationStartTime && l > this.elementNarrationStartTime) {
            this.narrateScreen(true);
            this.screenNarrationStartTime = Long.MAX_VALUE;
        }
    }

    public void narrateScreenIfNarrationEnabled(boolean onlyChangedNarrations) {
        if (this.isNarratorActive()) {
            this.narrateScreen(onlyChangedNarrations);
        }
    }

    private void narrateScreen(boolean onlyChangedNarrations) {
        this.narrator.buildNarrations(arg_0 -> this.addScreenNarrations(arg_0));
        String string = this.narrator.buildNarratorText(!onlyChangedNarrations);
        if (!string.isEmpty()) {
            this.client.getNarratorManager().narrateSystemImmediately(string);
        }
    }

    protected boolean hasUsageText() {
        return true;
    }

    protected void addScreenNarrations(NarrationMessageBuilder messageBuilder) {
        messageBuilder.put(NarrationPart.TITLE, this.getNarratedTitle());
        if (this.hasUsageText()) {
            messageBuilder.put(NarrationPart.USAGE, SCREEN_USAGE_TEXT);
        }
        this.addElementNarrations(messageBuilder);
    }

    protected void addElementNarrations(NarrationMessageBuilder builder) {
        List<Selectable> list = this.selectables.stream().flatMap(selectable -> selectable.getNarratedParts().stream()).filter(Selectable::isInteractable).sorted(Comparator.comparingInt(Navigable::getNavigationOrder)).toList();
        SelectedElementNarrationData selectedElementNarrationData = Screen.findSelectedElementData(list, (Selectable)this.selected);
        if (selectedElementNarrationData != null) {
            if (selectedElementNarrationData.selectType.isFocused()) {
                this.selected = selectedElementNarrationData.selectable;
            }
            if (list.size() > 1) {
                builder.put(NarrationPart.POSITION, (Text)Text.translatable((String)"narrator.position.screen", (Object[])new Object[]{selectedElementNarrationData.index + 1, list.size()}));
                if (selectedElementNarrationData.selectType == Selectable.SelectionType.FOCUSED) {
                    builder.put(NarrationPart.USAGE, this.getUsageNarrationText());
                }
            }
            selectedElementNarrationData.selectable.appendNarrations(builder.nextMessage());
        }
    }

    protected Text getUsageNarrationText() {
        return Text.translatable((String)"narration.component_list.usage");
    }

    public static // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable Screen.SelectedElementNarrationData findSelectedElementData(List<? extends Selectable> selectables, @Nullable Selectable selectable) {
        SelectedElementNarrationData selectedElementNarrationData = null;
        SelectedElementNarrationData selectedElementNarrationData2 = null;
        int j = selectables.size();
        for (int i = 0; i < j; ++i) {
            Selectable selectable2 = selectables.get(i);
            Selectable.SelectionType selectionType = selectable2.getType();
            if (selectionType.isFocused()) {
                if (selectable2 == selectable) {
                    selectedElementNarrationData2 = new SelectedElementNarrationData(selectable2, i, selectionType);
                    continue;
                }
                return new SelectedElementNarrationData(selectable2, i, selectionType);
            }
            if (selectionType.compareTo((Enum)(selectedElementNarrationData != null ? selectedElementNarrationData.selectType : Selectable.SelectionType.NONE)) <= 0) continue;
            selectedElementNarrationData = new SelectedElementNarrationData(selectable2, i, selectionType);
        }
        return selectedElementNarrationData != null ? selectedElementNarrationData : selectedElementNarrationData2;
    }

    public void refreshNarrator(boolean previouslyDisabled) {
        if (previouslyDisabled) {
            this.setScreenNarrationDelay(NARRATOR_MODE_CHANGE_DELAY, false);
        }
        if (this.narratorToggleButton != null) {
            this.narratorToggleButton.setValue((Object)((NarratorMode)this.client.options.getNarrator().getValue()));
        }
    }

    public TextRenderer getTextRenderer() {
        return this.textRenderer;
    }

    public boolean showsStatusEffects() {
        return false;
    }

    public boolean canInterruptOtherScreen() {
        return this.shouldCloseOnEsc();
    }

    public ScreenRect getNavigationFocus() {
        return new ScreenRect(0, 0, this.width, this.height);
    }

    public @Nullable MusicSound getMusic() {
        return null;
    }

    static {
        NARRATOR_MODE_CHANGE_DELAY = SCREEN_INIT_NARRATION_DELAY = TimeUnit.SECONDS.toMillis(2L);
    }
}

