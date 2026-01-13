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
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.world.rule.GameRule;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
static interface EditGameRulesScreen.RuleWidgetFactory<T> {
    public EditGameRulesScreen.AbstractRuleWidget create(Text var1, List<OrderedText> var2, String var3, GameRule<T> var4);
}
