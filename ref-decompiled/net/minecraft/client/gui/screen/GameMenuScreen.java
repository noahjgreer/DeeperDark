/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.ConfirmLinkScreen
 *  net.minecraft.client.gui.screen.GameMenuScreen
 *  net.minecraft.client.gui.screen.GameMenuScreen$FeedbackScreen
 *  net.minecraft.client.gui.screen.OpenToLanScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.StatsScreen
 *  net.minecraft.client.gui.screen.advancement.AdvancementsScreen
 *  net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen
 *  net.minecraft.client.gui.screen.option.OptionsScreen
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 *  net.minecraft.client.gui.widget.GridWidget
 *  net.minecraft.client.gui.widget.GridWidget$Adder
 *  net.minecraft.client.gui.widget.SimplePositioningWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.client.option.MusicToastMode
 *  net.minecraft.client.toast.NowPlayingToast
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.dialog.Dialogs
 *  net.minecraft.dialog.type.Dialog
 *  net.minecraft.registry.Registry
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.entry.RegistryEntryList
 *  net.minecraft.registry.tag.DialogTags
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.server.ServerLinks
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Urls
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.MusicToastMode;
import net.minecraft.client.toast.NowPlayingToast;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.dialog.Dialogs;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.DialogTags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.ServerLinks;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Urls;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class GameMenuScreen
extends Screen {
    private static final Identifier DRAFT_REPORT_ICON_TEXTURE = Identifier.ofVanilla((String)"icon/draft_report");
    private static final int GRID_COLUMNS = 2;
    private static final int BUTTONS_TOP_MARGIN = 50;
    private static final int GRID_MARGIN = 4;
    private static final int WIDE_BUTTON_WIDTH = 204;
    private static final int NORMAL_BUTTON_WIDTH = 98;
    private static final Text RETURN_TO_GAME_TEXT = Text.translatable((String)"menu.returnToGame");
    private static final Text ADVANCEMENTS_TEXT = Text.translatable((String)"gui.advancements");
    private static final Text STATS_TEXT = Text.translatable((String)"gui.stats");
    private static final Text SEND_FEEDBACK_TEXT = Text.translatable((String)"menu.sendFeedback");
    private static final Text REPORT_BUGS_TEXT = Text.translatable((String)"menu.reportBugs");
    private static final Text FEEDBACK_TEXT = Text.translatable((String)"menu.feedback");
    private static final Text OPTIONS_TEXT = Text.translatable((String)"menu.options");
    private static final Text SHARE_TO_LAN_TEXT = Text.translatable((String)"menu.shareToLan");
    private static final Text PLAYER_REPORTING_TEXT = Text.translatable((String)"menu.playerReporting");
    private static final Text GAME_TEXT = Text.translatable((String)"menu.game");
    private static final Text PAUSED_TEXT = Text.translatable((String)"menu.paused");
    private static final Tooltip CUSTOM_OPTIONS_TOOLTIP = Tooltip.of((Text)Text.translatable((String)"menu.custom_options.tooltip"));
    private final boolean showMenu;
    private @Nullable ButtonWidget exitButton;

    public GameMenuScreen(boolean showMenu) {
        super(showMenu ? GAME_TEXT : PAUSED_TEXT);
        this.showMenu = showMenu;
    }

    public boolean shouldShowMenu() {
        return this.showMenu;
    }

    protected void init() {
        if (this.showMenu) {
            this.initWidgets();
        }
        int i = this.textRenderer.getWidth((StringVisitable)this.title);
        int n = this.width / 2 - i / 2;
        int n2 = this.showMenu ? 40 : 10;
        Objects.requireNonNull(this.textRenderer);
        this.addDrawableChild((Element)new TextWidget(n, n2, i, 9, this.title, this.textRenderer));
    }

    private void initWidgets() {
        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().margin(4, 4, 4, 0);
        GridWidget.Adder adder = gridWidget.createAdder(2);
        adder.add((Widget)ButtonWidget.builder((Text)RETURN_TO_GAME_TEXT, button -> {
            this.client.setScreen(null);
            this.client.mouse.lockCursor();
        }).width(204).build(), 2, gridWidget.copyPositioner().marginTop(50));
        adder.add((Widget)this.createButton(ADVANCEMENTS_TEXT, () -> new AdvancementsScreen(this.client.player.networkHandler.getAdvancementHandler(), (Screen)this)));
        adder.add((Widget)this.createButton(STATS_TEXT, () -> new StatsScreen((Screen)this, this.client.player.getStatHandler())));
        Optional optional = this.getCustomOptionsDialog();
        if (optional.isEmpty()) {
            GameMenuScreen.addFeedbackAndBugsButtons((Screen)this, (GridWidget.Adder)adder);
        } else {
            this.addFeedbackAndCustomOptionsButtons(this.client, (RegistryEntry)optional.get(), adder);
        }
        adder.add((Widget)this.createButton(OPTIONS_TEXT, () -> new OptionsScreen((Screen)this, this.client.options)));
        if (this.client.isIntegratedServerRunning() && !this.client.getServer().isRemote()) {
            adder.add((Widget)this.createButton(SHARE_TO_LAN_TEXT, () -> new OpenToLanScreen((Screen)this)));
        } else {
            adder.add((Widget)this.createButton(PLAYER_REPORTING_TEXT, () -> new SocialInteractionsScreen((Screen)this)));
        }
        this.exitButton = (ButtonWidget)adder.add((Widget)ButtonWidget.builder((Text)ScreenTexts.returnToMenuOrDisconnect((boolean)this.client.isInSingleplayer()), button -> {
            button.active = false;
            this.client.getAbuseReportContext().tryShowDraftScreen(this.client, (Screen)this, () -> this.client.disconnect(ClientWorld.QUITTING_MULTIPLAYER_TEXT), true);
        }).width(204).build(), 2);
        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos((Widget)gridWidget, (int)0, (int)0, (int)this.width, (int)this.height, (float)0.5f, (float)0.25f);
        gridWidget.forEachChild(arg_0 -> ((GameMenuScreen)this).addDrawableChild(arg_0));
    }

    private Optional<? extends RegistryEntry<Dialog>> getCustomOptionsDialog() {
        RegistryEntryList registryEntryList;
        Registry registry = this.client.player.networkHandler.getRegistryManager().getOrThrow(RegistryKeys.DIALOG);
        Optional optional = registry.getOptional(DialogTags.PAUSE_SCREEN_ADDITIONS);
        if (optional.isPresent() && (registryEntryList = (RegistryEntryList)optional.get()).size() > 0) {
            if (registryEntryList.size() == 1) {
                return Optional.of(registryEntryList.get(0));
            }
            return registry.getOptional(Dialogs.CUSTOM_OPTIONS);
        }
        ServerLinks serverLinks = this.client.player.networkHandler.getServerLinks();
        if (!serverLinks.isEmpty()) {
            return registry.getOptional(Dialogs.SERVER_LINKS);
        }
        return Optional.empty();
    }

    static void addFeedbackAndBugsButtons(Screen parentScreen, GridWidget.Adder gridAdder) {
        gridAdder.add((Widget)GameMenuScreen.createUrlButton((Screen)parentScreen, (Text)SEND_FEEDBACK_TEXT, (URI)(SharedConstants.getGameVersion().stable() ? Urls.JAVA_FEEDBACK : Urls.SNAPSHOT_FEEDBACK)));
        ((ButtonWidget)gridAdder.add((Widget)GameMenuScreen.createUrlButton((Screen)parentScreen, (Text)GameMenuScreen.REPORT_BUGS_TEXT, (URI)Urls.SNAPSHOT_BUGS))).active = !SharedConstants.getGameVersion().dataVersion().isNotMainSeries();
    }

    private void addFeedbackAndCustomOptionsButtons(MinecraftClient client, RegistryEntry<Dialog> dialog, GridWidget.Adder gridAdder) {
        gridAdder.add((Widget)this.createButton(FEEDBACK_TEXT, () -> new FeedbackScreen((Screen)this)));
        gridAdder.add((Widget)ButtonWidget.builder((Text)((Dialog)dialog.value()).common().getExternalTitle(), button -> minecraftClient.player.networkHandler.showDialog(dialog, (Screen)this)).width(98).tooltip(CUSTOM_OPTIONS_TOOLTIP).build());
    }

    public void tick() {
        if (this.shouldShowNowPlayingToast()) {
            NowPlayingToast.tick();
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        if (this.shouldShowNowPlayingToast()) {
            NowPlayingToast.draw((DrawContext)context, (TextRenderer)this.textRenderer);
        }
        if (this.showMenu && this.client.getAbuseReportContext().hasDraft() && this.exitButton != null) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, DRAFT_REPORT_ICON_TEXTURE, this.exitButton.getX() + this.exitButton.getWidth() - 17, this.exitButton.getY() + 3, 15, 15);
        }
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (this.showMenu) {
            super.renderBackground(context, mouseX, mouseY, deltaTicks);
        }
    }

    public boolean shouldShowNowPlayingToast() {
        GameOptions gameOptions = this.client.options;
        return ((MusicToastMode)gameOptions.getMusicToast().getValue()).canShow() && gameOptions.getSoundVolume(SoundCategory.MUSIC) > 0.0f && this.showMenu;
    }

    private ButtonWidget createButton(Text text, Supplier<Screen> screenSupplier) {
        return ButtonWidget.builder((Text)text, button -> this.client.setScreen((Screen)screenSupplier.get())).width(98).build();
    }

    private static ButtonWidget createUrlButton(Screen parent, Text text, URI uri) {
        return ButtonWidget.builder((Text)text, (ButtonWidget.PressAction)ConfirmLinkScreen.opening((Screen)parent, (URI)uri)).width(98).build();
    }
}

