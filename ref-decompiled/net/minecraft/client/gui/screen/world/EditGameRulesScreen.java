/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.world.EditGameRulesScreen
 *  net.minecraft.client.gui.screen.world.EditGameRulesScreen$AbstractRuleWidget
 *  net.minecraft.client.gui.screen.world.EditGameRulesScreen$RuleListWidget
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.world.rule.GameRules
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.rule.GameRules;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class EditGameRulesScreen
extends Screen {
    private static final Text TITLE = Text.translatable((String)"editGamerule.title");
    private static final int field_49559 = 8;
    final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);
    private final Consumer<Optional<GameRules>> ruleSaver;
    private final Set<AbstractRuleWidget> invalidRuleWidgets = Sets.newHashSet();
    final GameRules gameRules;
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable RuleListWidget ruleListWidget;
    private @Nullable ButtonWidget doneButton;

    public EditGameRulesScreen(GameRules gameRules, Consumer<Optional<GameRules>> ruleSaveConsumer) {
        super(TITLE);
        this.gameRules = gameRules;
        this.ruleSaver = ruleSaveConsumer;
    }

    protected void init() {
        this.layout.addHeader(TITLE, this.textRenderer);
        this.ruleListWidget = (RuleListWidget)this.layout.addBody((Widget)new RuleListWidget(this, this.gameRules));
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addFooter((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        this.doneButton = (ButtonWidget)directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> this.ruleSaver.accept(Optional.of(this.gameRules))).build());
        directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.close()).build());
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        if (this.ruleListWidget != null) {
            this.ruleListWidget.position(this.width, this.layout);
        }
    }

    public void close() {
        this.ruleSaver.accept(Optional.empty());
    }

    private void updateDoneButton() {
        if (this.doneButton != null) {
            this.doneButton.active = this.invalidRuleWidgets.isEmpty();
        }
    }

    void markInvalid(AbstractRuleWidget ruleWidget) {
        this.invalidRuleWidgets.add(ruleWidget);
        this.updateDoneButton();
    }

    void markValid(AbstractRuleWidget ruleWidget) {
        this.invalidRuleWidgets.remove(ruleWidget);
        this.updateDoneButton();
    }

    static /* synthetic */ MinecraftClient method_27621(EditGameRulesScreen editGameRulesScreen) {
        return editGameRulesScreen.client;
    }

    static /* synthetic */ MinecraftClient method_29984(EditGameRulesScreen editGameRulesScreen) {
        return editGameRulesScreen.client;
    }

    static /* synthetic */ MinecraftClient method_27629(EditGameRulesScreen editGameRulesScreen) {
        return editGameRulesScreen.client;
    }

    static /* synthetic */ MinecraftClient method_29985(EditGameRulesScreen editGameRulesScreen) {
        return editGameRulesScreen.client;
    }

    static /* synthetic */ MinecraftClient method_29986(EditGameRulesScreen editGameRulesScreen) {
        return editGameRulesScreen.client;
    }

    static /* synthetic */ MinecraftClient method_27627(EditGameRulesScreen editGameRulesScreen) {
        return editGameRulesScreen.client;
    }

    static /* synthetic */ TextRenderer method_57771(EditGameRulesScreen editGameRulesScreen) {
        return editGameRulesScreen.textRenderer;
    }
}

