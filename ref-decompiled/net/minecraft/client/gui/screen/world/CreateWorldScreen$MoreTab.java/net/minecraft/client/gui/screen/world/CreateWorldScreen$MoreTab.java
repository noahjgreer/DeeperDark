/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class CreateWorldScreen.MoreTab
extends GridScreenTab {
    private static final Text MORE_TAB_TITLE_TEXT = Text.translatable("createWorld.tab.more.title");
    private static final Text GAME_RULES_TEXT = Text.translatable("selectWorld.gameRules");
    private static final Text DATA_PACKS_TEXT = Text.translatable("selectWorld.dataPacks");

    CreateWorldScreen.MoreTab() {
        super(MORE_TAB_TITLE_TEXT);
        GridWidget.Adder adder = this.grid.setRowSpacing(8).createAdder(1);
        adder.add(ButtonWidget.builder(GAME_RULES_TEXT, button -> this.openGameRulesScreen()).width(210).build());
        adder.add(ButtonWidget.builder(EXPERIMENTS_TEXT, button -> CreateWorldScreen.this.openExperimentsScreen(CreateWorldScreen.this.worldCreator.getGeneratorOptionsHolder().dataConfiguration())).width(210).build());
        adder.add(ButtonWidget.builder(DATA_PACKS_TEXT, button -> CreateWorldScreen.this.openPackScreen(CreateWorldScreen.this.worldCreator.getGeneratorOptionsHolder().dataConfiguration())).width(210).build());
    }

    private void openGameRulesScreen() {
        CreateWorldScreen.this.client.setScreen(new EditGameRulesScreen(CreateWorldScreen.this.worldCreator.getGameRules().withEnabledFeatures(CreateWorldScreen.this.worldCreator.getGeneratorOptionsHolder().dataConfiguration().enabledFeatures()), gameRules -> {
            CreateWorldScreen.this.client.setScreen(CreateWorldScreen.this);
            gameRules.ifPresent(CreateWorldScreen.this.worldCreator::setGameRules);
        }));
    }
}
