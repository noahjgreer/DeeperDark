/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.CyclingButtonWidget
 *  net.minecraft.client.gui.widget.CyclingButtonWidget$UpdateCallback
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.realms.RealmsLabel
 *  net.minecraft.client.realms.dto.RealmsServer$WorldType
 *  net.minecraft.client.realms.dto.RealmsSlot
 *  net.minecraft.client.realms.dto.RealmsWorldOptions
 *  net.minecraft.client.realms.gui.RealmsPopups
 *  net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsScreen
 *  net.minecraft.client.realms.gui.screen.RealmsSlotOptionsScreen
 *  net.minecraft.client.realms.gui.screen.RealmsSlotOptionsScreen$SettingsSlider
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.GameMode
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsSlot;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.gui.RealmsPopups;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.gui.screen.RealmsSlotOptionsScreen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RealmsSlotOptionsScreen
extends RealmsScreen {
    private static final int field_32125 = 2;
    public static final List<Difficulty> DIFFICULTIES = ImmutableList.of((Object)Difficulty.PEACEFUL, (Object)Difficulty.EASY, (Object)Difficulty.NORMAL, (Object)Difficulty.HARD);
    private static final int field_32126 = 0;
    public static final List<GameMode> GAME_MODES = ImmutableList.of((Object)GameMode.SURVIVAL, (Object)GameMode.CREATIVE, (Object)GameMode.ADVENTURE);
    private static final Text EDIT_SLOT_NAME = Text.translatable((String)"mco.configure.world.edit.slot.name");
    static final Text SPAWN_PROTECTION = Text.translatable((String)"mco.configure.world.spawnProtection");
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
        super((Text)Text.translatable((String)"mco.configure.world.buttons.options"));
        this.parent = parent;
        this.slot = slot;
        this.worldType = worldType;
        this.difficulty = (Difficulty)RealmsSlotOptionsScreen.get((List)DIFFICULTIES, (int)slot.options.difficulty, (int)2);
        this.gameMode = (GameMode)RealmsSlotOptionsScreen.get((List)GAME_MODES, (int)slot.options.gameMode, (int)0);
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

    public void close() {
        this.client.setScreen((Screen)this.parent);
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

    public void init() {
        this.column2_x = 170;
        this.column1_x = this.width / 2 - this.column2_x;
        int i = this.width / 2 + 10;
        if (this.worldType != RealmsServer.WorldType.NORMAL) {
            MutableText text = this.worldType == RealmsServer.WorldType.ADVENTUREMAP ? Text.translatable((String)"mco.configure.world.edit.subscreen.adventuremap") : (this.worldType == RealmsServer.WorldType.INSPIRATION ? Text.translatable((String)"mco.configure.world.edit.subscreen.inspiration") : Text.translatable((String)"mco.configure.world.edit.subscreen.experience"));
            this.addLabel(new RealmsLabel((Text)text, this.width / 2, 26, -65536));
        }
        this.nameEdit = (TextFieldWidget)this.addSelectableChild((Element)new TextFieldWidget(this.client.textRenderer, this.column1_x, RealmsSlotOptionsScreen.row((int)1), this.column2_x, 20, null, (Text)Text.translatable((String)"mco.configure.world.edit.slot.name")));
        this.nameEdit.setText(this.slotName);
        this.nameEdit.setChangedListener(arg_0 -> this.setSlotName(arg_0));
        CyclingButtonWidget cyclingButtonWidget2 = (CyclingButtonWidget)this.addDrawableChild((Element)CyclingButtonWidget.builder(Difficulty::getTranslatableName, (Object)this.difficulty).values((Collection)DIFFICULTIES).build(i, RealmsSlotOptionsScreen.row((int)1), this.column2_x, 20, (Text)Text.translatable((String)"options.difficulty"), (cyclingButtonWidget, difficulty) -> {
            this.difficulty = difficulty;
        }));
        CyclingButtonWidget cyclingButtonWidget22 = (CyclingButtonWidget)this.addDrawableChild((Element)CyclingButtonWidget.builder(GameMode::getSimpleTranslatableName, (Object)this.gameMode).values((Collection)GAME_MODES).build(this.column1_x, RealmsSlotOptionsScreen.row((int)3), this.column2_x, 20, (Text)Text.translatable((String)"selectWorld.gameMode"), (button, gameModeIndex) -> {
            this.gameMode = gameModeIndex;
        }));
        CyclingButtonWidget cyclingButtonWidget3 = (CyclingButtonWidget)this.addDrawableChild((Element)CyclingButtonWidget.onOffBuilder((boolean)this.forceGameMode).build(i, RealmsSlotOptionsScreen.row((int)3), this.column2_x, 20, (Text)Text.translatable((String)"mco.configure.world.forceGameMode"), (button, forceGameMode) -> {
            this.forceGameMode = forceGameMode;
        }));
        this.spawnProtectionButton = (SettingsSlider)this.addDrawableChild((Element)new SettingsSlider(this, this.column1_x, RealmsSlotOptionsScreen.row((int)5), this.column2_x, this.spawnProtection, 0.0f, 16.0f));
        if (this.worldType != RealmsServer.WorldType.NORMAL) {
            this.spawnProtectionButton.active = false;
            cyclingButtonWidget3.active = false;
        }
        if (this.slot.isHardcore()) {
            cyclingButtonWidget2.active = false;
            cyclingButtonWidget22.active = false;
            cyclingButtonWidget3.active = false;
        }
        this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"mco.configure.world.buttons.done"), button -> this.saveSettings()).dimensions(this.column1_x, RealmsSlotOptionsScreen.row((int)13), this.column2_x, 20).build());
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.close()).dimensions(i, RealmsSlotOptionsScreen.row((int)13), this.column2_x, 20).build());
    }

    private CyclingButtonWidget.UpdateCallback<Boolean> getSpawnToggleButtonCallback(Text text, Consumer<Boolean> valueSetter) {
        return (button, value) -> {
            if (value.booleanValue()) {
                valueSetter.accept(true);
            } else {
                this.client.setScreen((Screen)RealmsPopups.createContinuableWarningPopup((Screen)this, (Text)text, popup -> {
                    valueSetter.accept(false);
                    popup.close();
                }));
            }
        };
    }

    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences((Text[])new Text[]{this.getTitle(), this.narrateLabels()});
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 17, -1);
        context.drawTextWithShadow(this.textRenderer, EDIT_SLOT_NAME, this.column1_x + this.column2_x / 2 - this.textRenderer.getWidth((StringVisitable)EDIT_SLOT_NAME) / 2, RealmsSlotOptionsScreen.row((int)0) - 5, -1);
        this.nameEdit.render(context, mouseX, mouseY, deltaTicks);
    }

    private void setSlotName(String slotName) {
        this.slotName = slotName.equals(this.defaultSlotName) ? "" : slotName;
    }

    private void saveSettings() {
        int i = RealmsSlotOptionsScreen.indexOf((List)DIFFICULTIES, (Object)this.difficulty, (int)2);
        int j = RealmsSlotOptionsScreen.indexOf((List)GAME_MODES, (Object)this.gameMode, (int)0);
        if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP || this.worldType == RealmsServer.WorldType.EXPERIENCE || this.worldType == RealmsServer.WorldType.INSPIRATION) {
            this.parent.saveSlotSettings(new RealmsSlot(this.slot.slotId, new RealmsWorldOptions(this.slot.options.spawnProtection, i, j, this.slot.options.forceGameMode, this.slotName, this.slot.options.version, this.slot.options.compatibility), this.slot.settings));
        } else {
            this.parent.saveSlotSettings(new RealmsSlot(this.slot.slotId, new RealmsWorldOptions(this.spawnProtection, i, j, this.forceGameMode, this.slotName, this.slot.options.version, this.slot.options.compatibility), this.slot.settings));
        }
    }
}

