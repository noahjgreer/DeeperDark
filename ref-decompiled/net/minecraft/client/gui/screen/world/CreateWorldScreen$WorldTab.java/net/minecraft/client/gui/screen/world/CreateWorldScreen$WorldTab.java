/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.LevelScreenProvider;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.screen.world.WorldScreenOptionGrid;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class CreateWorldScreen.WorldTab
extends GridScreenTab {
    private static final Text WORLD_TAB_TITLE_TEXT = Text.translatable("createWorld.tab.world.title");
    private static final Text AMPLIFIED_GENERATOR_INFO_TEXT = Text.translatable("generator.minecraft.amplified.info");
    private static final Text MAP_FEATURES_TEXT = Text.translatable("selectWorld.mapFeatures");
    private static final Text MAP_FEATURES_INFO_TEXT = Text.translatable("selectWorld.mapFeatures.info");
    private static final Text BONUS_ITEMS_TEXT = Text.translatable("selectWorld.bonusItems");
    private static final Text ENTER_SEED_TEXT = Text.translatable("selectWorld.enterSeed");
    static final Text SEED_INFO_TEXT = Text.translatable("selectWorld.seedInfo");
    private static final int field_42190 = 310;
    private final TextFieldWidget seedField;
    private final ButtonWidget customizeButton;

    CreateWorldScreen.WorldTab() {
        super(WORLD_TAB_TITLE_TEXT);
        GridWidget.Adder adder = this.grid.setColumnSpacing(10).setRowSpacing(8).createAdder(2);
        CyclingButtonWidget<WorldCreator.WorldType> cyclingButtonWidget = adder.add(CyclingButtonWidget.builder(WorldCreator.WorldType::getName, CreateWorldScreen.this.worldCreator.getWorldType()).values(this.getWorldTypes()).narration(CreateWorldScreen.WorldTab::getWorldTypeNarrationMessage).build(0, 0, 150, 20, Text.translatable("selectWorld.mapType"), (button, worldType) -> CreateWorldScreen.this.worldCreator.setWorldType((WorldCreator.WorldType)worldType)));
        cyclingButtonWidget.setValue(CreateWorldScreen.this.worldCreator.getWorldType());
        CreateWorldScreen.this.worldCreator.addListener(creator -> {
            WorldCreator.WorldType worldType = creator.getWorldType();
            cyclingButtonWidget.setValue(worldType);
            if (worldType.isAmplified()) {
                cyclingButtonWidget.setTooltip(Tooltip.of(AMPLIFIED_GENERATOR_INFO_TEXT));
            } else {
                cyclingButtonWidget.setTooltip(null);
            }
            cyclingButtonWidget.active = CreateWorldScreen.this.worldCreator.getWorldType().preset() != null;
        });
        this.customizeButton = adder.add(ButtonWidget.builder(Text.translatable("selectWorld.customizeType"), button -> this.openCustomizeScreen()).build());
        CreateWorldScreen.this.worldCreator.addListener(creator -> {
            this.customizeButton.active = !creator.isDebug() && creator.getLevelScreenProvider() != null;
        });
        this.seedField = new TextFieldWidget(this, CreateWorldScreen.this.textRenderer, 308, 20, Text.translatable("selectWorld.enterSeed")){

            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(ScreenTexts.SENTENCE_SEPARATOR).append(SEED_INFO_TEXT);
            }
        };
        this.seedField.setPlaceholder(SEED_INFO_TEXT);
        this.seedField.setText(CreateWorldScreen.this.worldCreator.getSeed());
        this.seedField.setChangedListener(seed -> CreateWorldScreen.this.worldCreator.setSeed(this.seedField.getText()));
        adder.add(LayoutWidgets.createLabeledWidget(CreateWorldScreen.this.textRenderer, this.seedField, ENTER_SEED_TEXT), 2);
        WorldScreenOptionGrid.Builder builder = WorldScreenOptionGrid.builder(310);
        builder.add(MAP_FEATURES_TEXT, CreateWorldScreen.this.worldCreator::shouldGenerateStructures, CreateWorldScreen.this.worldCreator::setGenerateStructures).toggleable(() -> !CreateWorldScreen.this.worldCreator.isDebug()).tooltip(MAP_FEATURES_INFO_TEXT);
        builder.add(BONUS_ITEMS_TEXT, CreateWorldScreen.this.worldCreator::isBonusChestEnabled, CreateWorldScreen.this.worldCreator::setBonusChestEnabled).toggleable(() -> !CreateWorldScreen.this.worldCreator.isHardcore() && !CreateWorldScreen.this.worldCreator.isDebug());
        WorldScreenOptionGrid worldScreenOptionGrid = builder.build();
        adder.add(worldScreenOptionGrid.getLayout(), 2);
        CreateWorldScreen.this.worldCreator.addListener(creator -> worldScreenOptionGrid.refresh());
    }

    private void openCustomizeScreen() {
        LevelScreenProvider levelScreenProvider = CreateWorldScreen.this.worldCreator.getLevelScreenProvider();
        if (levelScreenProvider != null) {
            CreateWorldScreen.this.client.setScreen(levelScreenProvider.createEditScreen(CreateWorldScreen.this, CreateWorldScreen.this.worldCreator.getGeneratorOptionsHolder()));
        }
    }

    private CyclingButtonWidget.Values<WorldCreator.WorldType> getWorldTypes() {
        return new CyclingButtonWidget.Values<WorldCreator.WorldType>(){

            @Override
            public List<WorldCreator.WorldType> getCurrent() {
                return CyclingButtonWidget.HAS_ALT_DOWN.getAsBoolean() ? CreateWorldScreen.this.worldCreator.getExtendedWorldTypes() : CreateWorldScreen.this.worldCreator.getNormalWorldTypes();
            }

            @Override
            public List<WorldCreator.WorldType> getDefaults() {
                return CreateWorldScreen.this.worldCreator.getNormalWorldTypes();
            }
        };
    }

    private static MutableText getWorldTypeNarrationMessage(CyclingButtonWidget<WorldCreator.WorldType> worldTypeButton) {
        if (worldTypeButton.getValue().isAmplified()) {
            return ScreenTexts.joinSentences(worldTypeButton.getGenericNarrationMessage(), AMPLIFIED_GENERATOR_INFO_TEXT);
        }
        return worldTypeButton.getGenericNarrationMessage();
    }
}
