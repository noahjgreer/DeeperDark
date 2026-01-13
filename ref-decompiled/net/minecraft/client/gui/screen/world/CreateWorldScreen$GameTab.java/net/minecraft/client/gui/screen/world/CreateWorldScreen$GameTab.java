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
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.Difficulty;

@Environment(value=EnvType.CLIENT)
class CreateWorldScreen.GameTab
extends GridScreenTab {
    private static final Text GAME_TAB_TITLE_TEXT = Text.translatable("createWorld.tab.game.title");
    private static final Text ALLOW_COMMANDS_TEXT = Text.translatable("selectWorld.allowCommands");
    private final TextFieldWidget worldNameField;

    CreateWorldScreen.GameTab() {
        super(GAME_TAB_TITLE_TEXT);
        GridWidget.Adder adder = this.grid.setRowSpacing(8).createAdder(1);
        Positioner positioner = adder.copyPositioner();
        this.worldNameField = new TextFieldWidget(CreateWorldScreen.this.textRenderer, 208, 20, Text.translatable("selectWorld.enterName"));
        this.worldNameField.setText(CreateWorldScreen.this.worldCreator.getWorldName());
        this.worldNameField.setChangedListener(CreateWorldScreen.this.worldCreator::setWorldName);
        CreateWorldScreen.this.worldCreator.addListener(creator -> this.worldNameField.setTooltip(Tooltip.of(Text.translatable("selectWorld.targetFolder", Text.literal(creator.getWorldDirectoryName()).formatted(Formatting.ITALIC)))));
        CreateWorldScreen.this.setInitialFocus(this.worldNameField);
        adder.add(LayoutWidgets.createLabeledWidget(CreateWorldScreen.this.textRenderer, this.worldNameField, ENTER_NAME_TEXT), adder.copyPositioner().alignHorizontalCenter());
        CyclingButtonWidget<WorldCreator.Mode> cyclingButtonWidget = adder.add(CyclingButtonWidget.builder(value -> value.name, CreateWorldScreen.this.worldCreator.getGameMode()).values((WorldCreator.Mode[])new WorldCreator.Mode[]{WorldCreator.Mode.SURVIVAL, WorldCreator.Mode.HARDCORE, WorldCreator.Mode.CREATIVE}).build(0, 0, 210, 20, GAME_MODE_TEXT, (button, value) -> CreateWorldScreen.this.worldCreator.setGameMode((WorldCreator.Mode)((Object)value))), positioner);
        CreateWorldScreen.this.worldCreator.addListener(creator -> {
            cyclingButtonWidget.setValue(creator.getGameMode());
            cyclingButtonWidget.active = !creator.isDebug();
            cyclingButtonWidget.setTooltip(Tooltip.of(creator.getGameMode().getInfo()));
        });
        CyclingButtonWidget<Difficulty> cyclingButtonWidget2 = adder.add(CyclingButtonWidget.builder(Difficulty::getTranslatableName, CreateWorldScreen.this.worldCreator.getDifficulty()).values((Difficulty[])Difficulty.values()).build(0, 0, 210, 20, Text.translatable("options.difficulty"), (button, value) -> CreateWorldScreen.this.worldCreator.setDifficulty((Difficulty)value)), positioner);
        CreateWorldScreen.this.worldCreator.addListener(creator -> {
            cyclingButtonWidget2.setValue(CreateWorldScreen.this.worldCreator.getDifficulty());
            cyclingButtonWidget.active = !CreateWorldScreen.this.worldCreator.isHardcore();
            cyclingButtonWidget2.setTooltip(Tooltip.of(CreateWorldScreen.this.worldCreator.getDifficulty().getInfo()));
        });
        CyclingButtonWidget<Boolean> cyclingButtonWidget3 = adder.add(CyclingButtonWidget.onOffBuilder(CreateWorldScreen.this.worldCreator.areCheatsEnabled()).tooltip(value -> Tooltip.of(ALLOW_COMMANDS_INFO_TEXT)).build(0, 0, 210, 20, ALLOW_COMMANDS_TEXT, (button, value) -> CreateWorldScreen.this.worldCreator.setCheatsEnabled((boolean)value)));
        CreateWorldScreen.this.worldCreator.addListener(creator -> {
            cyclingButtonWidget3.setValue(CreateWorldScreen.this.worldCreator.areCheatsEnabled());
            cyclingButtonWidget.active = !CreateWorldScreen.this.worldCreator.isDebug() && !CreateWorldScreen.this.worldCreator.isHardcore();
        });
        if (!SharedConstants.getGameVersion().stable()) {
            adder.add(ButtonWidget.builder(EXPERIMENTS_TEXT, button -> CreateWorldScreen.this.openExperimentsScreen(CreateWorldScreen.this.worldCreator.getGeneratorOptionsHolder().dataConfiguration())).width(210).build());
        }
    }
}
