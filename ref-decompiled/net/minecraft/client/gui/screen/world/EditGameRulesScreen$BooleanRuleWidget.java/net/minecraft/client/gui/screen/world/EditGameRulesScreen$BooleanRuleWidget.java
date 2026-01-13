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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.world.rule.GameRule;

@Environment(value=EnvType.CLIENT)
public class EditGameRulesScreen.BooleanRuleWidget
extends EditGameRulesScreen.NamedRuleWidget {
    private final CyclingButtonWidget<Boolean> toggleButton;

    public EditGameRulesScreen.BooleanRuleWidget(Text name, List<OrderedText> description, String ruleName, GameRule<Boolean> rule) {
        super(EditGameRulesScreen.this, description, name);
        this.toggleButton = CyclingButtonWidget.onOffBuilder(EditGameRulesScreen.this.gameRules.getValue(rule)).omitKeyText().narration(button -> button.getGenericNarrationMessage().append("\n").append(ruleName)).build(10, 5, 44, 20, name, (button, value) -> EditGameRulesScreen.this.gameRules.setValue(rule, value, null));
        this.children.add(this.toggleButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        this.drawName(context, this.getContentY(), this.getContentX());
        this.toggleButton.setX(this.getContentRightEnd() - 45);
        this.toggleButton.setY(this.getContentY());
        this.toggleButton.render(context, mouseX, mouseY, deltaTicks);
    }
}
