/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsSlot;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.gui.RealmsPopups;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class RealmsSlotOptionsScreen
extends RealmsScreen {
    private static final int field_32125 = 2;
    public static final List<Difficulty> DIFFICULTIES = ImmutableList.of((Object)Difficulty.PEACEFUL, (Object)Difficulty.EASY, (Object)Difficulty.NORMAL, (Object)Difficulty.HARD);
    private static final int field_32126 = 0;
    public static final List<GameMode> GAME_MODES = ImmutableList.of((Object)GameMode.SURVIVAL, (Object)GameMode.CREATIVE, (Object)GameMode.ADVENTURE);
    private static final Text EDIT_SLOT_NAME = Text.translatable("mco.configure.world.edit.slot.name");
    static final Text SPAWN_PROTECTION = Text.translatable("mco.configure.world.spawnProtection");
    private TextFieldWidget nameEdit;
    protected final RealmsConfigureWorldScreen parent;
    private int column1_x;
    private int column2_x;
    private final RealmsSlot slot;
    private final RealmsServer.WorldType worldType;
    private Difficulty difficulty;
    private GameMode gameMode;
    private final String defaultSlotName;
    private String slotName;
    int spawnProtection;
    private boolean forceGameMode;
    SettingsSlider spawnProtectionButton;

    public RealmsSlotOptionsScreen(RealmsConfigureWorldScreen parent, RealmsSlot slot, RealmsServer.WorldType worldType, int activeSlot) {
        super(Text.translatable("mco.configure.world.buttons.options"));
        this.parent = parent;
        this.slot = slot;
        this.worldType = worldType;
        this.difficulty = RealmsSlotOptionsScreen.get(DIFFICULTIES, slot.options.difficulty, 2);
        this.gameMode = RealmsSlotOptionsScreen.get(GAME_MODES, slot.options.gameMode, 0);
        this.defaultSlotName = slot.options.getDefaultSlotName(activeSlot);
        this.setSlotName(slot.options.getSlotName(activeSlot));
        if (worldType == RealmsServer.WorldType.NORMAL) {
            this.spawnProtection = slot.options.spawnProtection;
            this.forceGameMode = slot.options.forceGameMode;
        } else {
            this.spawnProtection = 0;
            this.forceGameMode = false;
        }
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    private static <T> T get(List<T> list, int index, int fallbackIndex) {
        try {
            return list.get(index);
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            return list.get(fallbackIndex);
        }
    }

    private static <T> int indexOf(List<T> list, T value, int fallbackIndex) {
        int i = list.indexOf(value);
        return i == -1 ? fallbackIndex : i;
    }

    @Override
    public void init() {
        this.column2_x = 170;
        this.column1_x = this.width / 2 - this.column2_x;
        int i = this.width / 2 + 10;
        if (this.worldType != RealmsServer.WorldType.NORMAL) {
            MutableText text = this.worldType == RealmsServer.WorldType.ADVENTUREMAP ? Text.translatable("mco.configure.world.edit.subscreen.adventuremap") : (this.worldType == RealmsServer.WorldType.INSPIRATION ? Text.translatable("mco.configure.world.edit.subscreen.inspiration") : Text.translatable("mco.configure.world.edit.subscreen.experience"));
            this.addLabel(new RealmsLabel(text, this.width / 2, 26, -65536));
        }
        this.nameEdit = this.addSelectableChild(new TextFieldWidget(this.client.textRenderer, this.column1_x, RealmsSlotOptionsScreen.row(1), this.column2_x, 20, null, Text.translatable("mco.configure.world.edit.slot.name")));
        this.nameEdit.setText(this.slotName);
        this.nameEdit.setChangedListener(this::setSlotName);
        CyclingButtonWidget<Difficulty> cyclingButtonWidget2 = this.addDrawableChild(CyclingButtonWidget.builder(Difficulty::getTranslatableName, this.difficulty).values((Collection<Difficulty>)DIFFICULTIES).build(i, RealmsSlotOptionsScreen.row(1), this.column2_x, 20, Text.translatable("options.difficulty"), (cyclingButtonWidget, difficulty) -> {
            this.difficulty = difficulty;
        }));
        CyclingButtonWidget<GameMode> cyclingButtonWidget22 = this.addDrawableChild(CyclingButtonWidget.builder(GameMode::getSimpleTranslatableName, this.gameMode).values((Collection<GameMode>)GAME_MODES).build(this.column1_x, RealmsSlotOptionsScreen.row(3), this.column2_x, 20, Text.translatable("selectWorld.gameMode"), (button, gameModeIndex) -> {
            this.gameMode = gameModeIndex;
        }));
        CyclingButtonWidget<Boolean> cyclingButtonWidget3 = this.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.forceGameMode).build(i, RealmsSlotOptionsScreen.row(3), this.column2_x, 20, Text.translatable("mco.configure.world.forceGameMode"), (button, forceGameMode) -> {
            this.forceGameMode = forceGameMode;
        }));
        this.spawnProtectionButton = this.addDrawableChild(new SettingsSlider(this.column1_x, RealmsSlotOptionsScreen.row(5), this.column2_x, this.spawnProtection, 0.0f, 16.0f));
        if (this.worldType != RealmsServer.WorldType.NORMAL) {
            this.spawnProtectionButton.active = false;
            cyclingButtonWidget3.active = false;
        }
        if (this.slot.isHardcore()) {
            cyclingButtonWidget2.active = false;
            cyclingButtonWidget22.active = false;
            cyclingButtonWidget3.active = false;
        }
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.configure.world.buttons.done"), button -> this.saveSettings()).dimensions(this.column1_x, RealmsSlotOptionsScreen.row(13), this.column2_x, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close()).dimensions(i, RealmsSlotOptionsScreen.row(13), this.column2_x, 20).build());
    }

    private CyclingButtonWidget.UpdateCallback<Boolean> getSpawnToggleButtonCallback(Text text, Consumer<Boolean> valueSetter) {
        return (button, value) -> {
            if (value.booleanValue()) {
                valueSetter.accept(true);
            } else {
                this.client.setScreen(RealmsPopups.createContinuableWarningPopup(this, text, popup -> {
                    valueSetter.accept(false);
                    popup.close();
                }));
            }
        };
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(this.getTitle(), this.narrateLabels());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 17, -1);
        context.drawTextWithShadow(this.textRenderer, EDIT_SLOT_NAME, this.column1_x + this.column2_x / 2 - this.textRenderer.getWidth(EDIT_SLOT_NAME) / 2, RealmsSlotOptionsScreen.row(0) - 5, -1);
        this.nameEdit.render(context, mouseX, mouseY, deltaTicks);
    }

    private void setSlotName(String slotName) {
        this.slotName = slotName.equals(this.defaultSlotName) ? "" : slotName;
    }

    private void saveSettings() {
        int i = RealmsSlotOptionsScreen.indexOf(DIFFICULTIES, this.difficulty, 2);
        int j = RealmsSlotOptionsScreen.indexOf(GAME_MODES, this.gameMode, 0);
        if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP || this.worldType == RealmsServer.WorldType.EXPERIENCE || this.worldType == RealmsServer.WorldType.INSPIRATION) {
            this.parent.saveSlotSettings(new RealmsSlot(this.slot.slotId, new RealmsWorldOptions(this.slot.options.spawnProtection, i, j, this.slot.options.forceGameMode, this.slotName, this.slot.options.version, this.slot.options.compatibility), this.slot.settings));
        } else {
            this.parent.saveSlotSettings(new RealmsSlot(this.slot.slotId, new RealmsWorldOptions(this.spawnProtection, i, j, this.forceGameMode, this.slotName, this.slot.options.version, this.slot.options.compatibility), this.slot.settings));
        }
    }

    @Environment(value=EnvType.CLIENT)
    class SettingsSlider
    extends SliderWidget {
        private final double min;
        private final double max;

        public SettingsSlider(int x, int y, int width, int value, float min, float max) {
            super(x, y, width, 20, ScreenTexts.EMPTY, 0.0);
            this.min = min;
            this.max = max;
            this.value = (MathHelper.clamp((float)value, min, max) - min) / (max - min);
            this.updateMessage();
        }

        @Override
        public void applyValue() {
            if (!RealmsSlotOptionsScreen.this.spawnProtectionButton.active) {
                return;
            }
            RealmsSlotOptionsScreen.this.spawnProtection = (int)MathHelper.lerp(MathHelper.clamp(this.value, 0.0, 1.0), this.min, this.max);
        }

        @Override
        protected void updateMessage() {
            this.setMessage(ScreenTexts.composeGenericOptionText(SPAWN_PROTECTION, RealmsSlotOptionsScreen.this.spawnProtection == 0 ? ScreenTexts.OFF : Text.literal(String.valueOf(RealmsSlotOptionsScreen.this.spawnProtection))));
        }
    }
}
