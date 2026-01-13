/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.ConfirmScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen
 *  net.minecraft.client.gui.screen.option.ChatOptionsScreen
 *  net.minecraft.client.gui.screen.option.ControlsOptionsScreen
 *  net.minecraft.client.gui.screen.option.CreditsAndAttributionScreen
 *  net.minecraft.client.gui.screen.option.LanguageOptionsScreen
 *  net.minecraft.client.gui.screen.option.OnlineOptionsScreen
 *  net.minecraft.client.gui.screen.option.OptionsScreen
 *  net.minecraft.client.gui.screen.option.SkinOptionsScreen
 *  net.minecraft.client.gui.screen.option.SoundOptionsScreen
 *  net.minecraft.client.gui.screen.option.TelemetryInfoScreen
 *  net.minecraft.client.gui.screen.option.VideoOptionsScreen
 *  net.minecraft.client.gui.screen.pack.PackScreen
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.widget.AxisGridWidget
 *  net.minecraft.client.gui.widget.AxisGridWidget$DisplayAxis
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.CyclingButtonWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.GridWidget
 *  net.minecraft.client.gui.widget.GridWidget$Adder
 *  net.minecraft.client.gui.widget.LockButtonWidget
 *  net.minecraft.client.gui.widget.Positioner
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket
 *  net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket
 *  net.minecraft.resource.ResourcePackManager
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.world.Difficulty
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.option;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.ChatOptionsScreen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OnlineOptionsScreen;
import net.minecraft.client.gui.screen.option.SkinOptionsScreen;
import net.minecraft.client.gui.screen.option.SoundOptionsScreen;
import net.minecraft.client.gui.screen.option.TelemetryInfoScreen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AxisGridWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class OptionsScreen
extends Screen {
    private static final Text TITLE_TEXT = Text.translatable((String)"options.title");
    private static final Text SKIN_CUSTOMIZATION_TEXT = Text.translatable((String)"options.skinCustomisation");
    private static final Text SOUNDS_TEXT = Text.translatable((String)"options.sounds");
    private static final Text VIDEO_TEXT = Text.translatable((String)"options.video");
    public static final Text CONTROL_TEXT = Text.translatable((String)"options.controls");
    private static final Text LANGUAGE_TEXT = Text.translatable((String)"options.language");
    private static final Text CHAT_TEXT = Text.translatable((String)"options.chat");
    private static final Text RESOURCE_PACK_TEXT = Text.translatable((String)"options.resourcepack");
    private static final Text ACCESSIBILITY_TEXT = Text.translatable((String)"options.accessibility");
    private static final Text TELEMETRY_TEXT = Text.translatable((String)"options.telemetry");
    private static final Tooltip TELEMETRY_DISABLED_TOOLTIP = Tooltip.of((Text)Text.translatable((String)"options.telemetry.disabled"));
    private static final Text CREDITS_AND_ATTRIBUTION_TEXT = Text.translatable((String)"options.credits_and_attribution");
    private static final int COLUMNS = 2;
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this, 61, 33);
    private final Screen parent;
    private final GameOptions settings;
    private @Nullable CyclingButtonWidget<Difficulty> difficultyButton;
    private @Nullable LockButtonWidget lockDifficultyButton;

    public OptionsScreen(Screen parent, GameOptions gameOptions) {
        super(TITLE_TEXT);
        this.parent = parent;
        this.settings = gameOptions;
    }

    protected void init() {
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addHeader((Widget)DirectionalLayoutWidget.vertical().spacing(8));
        directionalLayoutWidget.add((Widget)new TextWidget(TITLE_TEXT, this.textRenderer), Positioner::alignHorizontalCenter);
        DirectionalLayoutWidget directionalLayoutWidget2 = ((DirectionalLayoutWidget)directionalLayoutWidget.add((Widget)DirectionalLayoutWidget.horizontal())).spacing(8);
        directionalLayoutWidget2.add((Widget)this.settings.getFov().createWidget(this.client.options));
        directionalLayoutWidget2.add(this.createTopRightButton());
        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().marginX(4).marginBottom(4).alignHorizontalCenter();
        GridWidget.Adder adder = gridWidget.createAdder(2);
        adder.add((Widget)this.createButton(SKIN_CUSTOMIZATION_TEXT, () -> new SkinOptionsScreen((Screen)this, this.settings)));
        adder.add((Widget)this.createButton(SOUNDS_TEXT, () -> new SoundOptionsScreen((Screen)this, this.settings)));
        adder.add((Widget)this.createButton(VIDEO_TEXT, () -> new VideoOptionsScreen((Screen)this, this.client, this.settings)));
        adder.add((Widget)this.createButton(CONTROL_TEXT, () -> new ControlsOptionsScreen((Screen)this, this.settings)));
        adder.add((Widget)this.createButton(LANGUAGE_TEXT, () -> new LanguageOptionsScreen((Screen)this, this.settings, this.client.getLanguageManager())));
        adder.add((Widget)this.createButton(CHAT_TEXT, () -> new ChatOptionsScreen((Screen)this, this.settings)));
        adder.add((Widget)this.createButton(RESOURCE_PACK_TEXT, () -> new PackScreen(this.client.getResourcePackManager(), arg_0 -> this.refreshResourcePacks(arg_0), this.client.getResourcePackDir(), (Text)Text.translatable((String)"resourcePack.title"))));
        adder.add((Widget)this.createButton(ACCESSIBILITY_TEXT, () -> new AccessibilityOptionsScreen((Screen)this, this.settings)));
        ButtonWidget buttonWidget = (ButtonWidget)adder.add((Widget)this.createButton(TELEMETRY_TEXT, () -> new TelemetryInfoScreen((Screen)this, this.settings)));
        if (!this.client.isTelemetryEnabledByApi()) {
            buttonWidget.active = false;
            buttonWidget.setTooltip(TELEMETRY_DISABLED_TOOLTIP);
        }
        adder.add((Widget)this.createButton(CREDITS_AND_ATTRIBUTION_TEXT, () -> new CreditsAndAttributionScreen((Screen)this)));
        this.layout.addBody((Widget)gridWidget);
        this.layout.addFooter((Widget)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> this.close()).width(200).build());
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    private void refreshResourcePacks(ResourcePackManager resourcePackManager) {
        this.settings.refreshResourcePacks(resourcePackManager);
        this.client.setScreen((Screen)this);
    }

    private Widget createTopRightButton() {
        if (this.client.world != null && this.client.isIntegratedServerRunning()) {
            this.difficultyButton = OptionsScreen.createDifficultyButtonWidget((int)0, (int)0, (String)"options.difficulty", (MinecraftClient)this.client);
            if (!this.client.world.getLevelProperties().isHardcore()) {
                this.lockDifficultyButton = new LockButtonWidget(0, 0, button -> this.client.setScreen((Screen)new ConfirmScreen(arg_0 -> this.lockDifficulty(arg_0), (Text)Text.translatable((String)"difficulty.lock.title"), (Text)Text.translatable((String)"difficulty.lock.question", (Object[])new Object[]{this.client.world.getLevelProperties().getDifficulty().getTranslatableName()}))));
                this.difficultyButton.setWidth(this.difficultyButton.getWidth() - this.lockDifficultyButton.getWidth());
                this.lockDifficultyButton.setLocked(this.client.world.getLevelProperties().isDifficultyLocked());
                this.lockDifficultyButton.active = !this.lockDifficultyButton.isLocked();
                this.difficultyButton.active = !this.lockDifficultyButton.isLocked();
                AxisGridWidget axisGridWidget = new AxisGridWidget(150, 0, AxisGridWidget.DisplayAxis.HORIZONTAL);
                axisGridWidget.add((Widget)this.difficultyButton);
                axisGridWidget.add((Widget)this.lockDifficultyButton);
                return axisGridWidget;
            }
            this.difficultyButton.active = false;
            return this.difficultyButton;
        }
        return ButtonWidget.builder((Text)Text.translatable((String)"options.online"), button -> this.client.setScreen((Screen)new OnlineOptionsScreen((Screen)this, this.settings))).dimensions(this.width / 2 + 5, this.height / 6 - 12 + 24, 150, 20).build();
    }

    public static CyclingButtonWidget<Difficulty> createDifficultyButtonWidget(int x, int y, String translationKey, MinecraftClient client) {
        return CyclingButtonWidget.builder(Difficulty::getTranslatableName, (Object)client.world.getDifficulty()).values((Object[])Difficulty.values()).build(x, y, 150, 20, (Text)Text.translatable((String)translationKey), (button, difficulty) -> client.getNetworkHandler().sendPacket((Packet)new UpdateDifficultyC2SPacket(difficulty)));
    }

    private void lockDifficulty(boolean difficultyLocked) {
        this.client.setScreen((Screen)this);
        if (difficultyLocked && this.client.world != null && this.lockDifficultyButton != null && this.difficultyButton != null) {
            this.client.getNetworkHandler().sendPacket((Packet)new UpdateDifficultyLockC2SPacket(true));
            this.lockDifficultyButton.setLocked(true);
            this.lockDifficultyButton.active = false;
            this.difficultyButton.active = false;
        }
    }

    public void removed() {
        this.settings.write();
    }

    private ButtonWidget createButton(Text message, Supplier<Screen> screenSupplier) {
        return ButtonWidget.builder((Text)message, button -> this.client.setScreen((Screen)screenSupplier.get())).build();
    }
}

