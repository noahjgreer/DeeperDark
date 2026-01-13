/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class EditGameRulesScreen.RuleCategoryWidget
extends EditGameRulesScreen.AbstractRuleWidget {
    final Text name;

    public EditGameRulesScreen.RuleCategoryWidget(Text text) {
        super(null);
        this.name = text;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        context.drawCenteredTextWithShadow(((EditGameRulesScreen)EditGameRulesScreen.this).client.textRenderer, this.name, this.getContentMiddleX(), this.getContentY() + 5, -1);
    }

    @Override
    public List<? extends Element> children() {
        return ImmutableList.of();
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return ImmutableList.of((Object)new Selectable(){

            @Override
            public Selectable.SelectionType getType() {
                return Selectable.SelectionType.HOVERED;
            }

            @Override
            public void appendNarrations(NarrationMessageBuilder builder) {
                builder.put(NarrationPart.TITLE, RuleCategoryWidget.this.name);
            }
        });
    }
}
