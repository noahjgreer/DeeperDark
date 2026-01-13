/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public abstract class EditGameRulesScreen.NamedRuleWidget
extends EditGameRulesScreen.AbstractRuleWidget {
    private final List<OrderedText> name;
    protected final List<ClickableWidget> children;

    public EditGameRulesScreen.NamedRuleWidget(List<OrderedText> description, Text name) {
        super(description);
        this.children = Lists.newArrayList();
        this.name = ((EditGameRulesScreen)EditGameRulesScreen.this).client.textRenderer.wrapLines(name, 175);
    }

    @Override
    public List<? extends Element> children() {
        return this.children;
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return this.children;
    }

    protected void drawName(DrawContext context, int x, int y) {
        if (this.name.size() == 1) {
            context.drawTextWithShadow(((EditGameRulesScreen)EditGameRulesScreen.this).client.textRenderer, this.name.get(0), y, x + 5, -1);
        } else if (this.name.size() >= 2) {
            context.drawTextWithShadow(((EditGameRulesScreen)EditGameRulesScreen.this).client.textRenderer, this.name.get(0), y, x, -1);
            context.drawTextWithShadow(((EditGameRulesScreen)EditGameRulesScreen.this).client.textRenderer, this.name.get(1), y, x + 10, -1);
        }
    }
}
