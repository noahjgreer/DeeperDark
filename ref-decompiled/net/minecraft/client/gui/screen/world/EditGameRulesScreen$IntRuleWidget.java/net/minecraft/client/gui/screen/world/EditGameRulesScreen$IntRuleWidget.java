/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import com.mojang.serialization.DataResult;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.world.rule.GameRule;

@Environment(value=EnvType.CLIENT)
public class EditGameRulesScreen.IntRuleWidget
extends EditGameRulesScreen.NamedRuleWidget {
    private final TextFieldWidget valueWidget;

    public EditGameRulesScreen.IntRuleWidget(Text name, List<OrderedText> description, String ruleName, GameRule<Integer> rule) {
        super(EditGameRulesScreen.this, description, name);
        this.valueWidget = new TextFieldWidget(((EditGameRulesScreen)EditGameRulesScreen.this).client.textRenderer, 10, 5, 44, 20, name.copy().append("\n").append(ruleName).append("\n"));
        this.valueWidget.setText(EditGameRulesScreen.this.gameRules.getRuleValueName(rule));
        this.valueWidget.setChangedListener(value -> {
            DataResult dataResult = rule.deserialize((String)value);
            if (dataResult.isSuccess()) {
                this.valueWidget.setEditableColor(-2039584);
                EditGameRulesScreen.this.markValid(this);
                EditGameRulesScreen.this.gameRules.setValue(rule, (Integer)dataResult.getOrThrow(), null);
            } else {
                this.valueWidget.setEditableColor(-65536);
                EditGameRulesScreen.this.markInvalid(this);
            }
        });
        this.children.add(this.valueWidget);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        this.drawName(context, this.getContentY(), this.getContentX());
        this.valueWidget.setX(this.getContentRightEnd() - 45);
        this.valueWidget.setY(this.getContentY());
        this.valueWidget.render(context, mouseX, mouseY, deltaTicks);
    }
}
