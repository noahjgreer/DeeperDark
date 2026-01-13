/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRuleCategory;
import net.minecraft.world.rule.GameRuleVisitor;
import net.minecraft.world.rule.GameRules;

@Environment(value=EnvType.CLIENT)
public class EditGameRulesScreen.RuleListWidget
extends ElementListWidget<EditGameRulesScreen.AbstractRuleWidget> {
    private static final int field_49561 = 24;

    public EditGameRulesScreen.RuleListWidget(GameRules gameRules) {
        super(MinecraftClient.getInstance(), EditGameRulesScreen.this.width, EditGameRulesScreen.this.layout.getContentHeight(), EditGameRulesScreen.this.layout.getHeaderHeight(), 24);
        final HashMap map = Maps.newHashMap();
        gameRules.accept(new GameRuleVisitor(){

            @Override
            public void visitBoolean(GameRule<Boolean> rule2) {
                this.createRuleWidget(rule2, (name, description, ruleName, rule) -> new EditGameRulesScreen.BooleanRuleWidget(EditGameRulesScreen.this, name, description, ruleName, rule));
            }

            @Override
            public void visitInt(GameRule<Integer> rule2) {
                this.createRuleWidget(rule2, (name, description, ruleName, rule) -> new EditGameRulesScreen.IntRuleWidget(EditGameRulesScreen.this, name, description, ruleName, rule));
            }

            private <T> void createRuleWidget(GameRule<T> key, EditGameRulesScreen.RuleWidgetFactory<T> widgetFactory) {
                Object string2;
                ImmutableList list;
                MutableText text = Text.translatable(key.getTranslationKey());
                MutableText text2 = Text.literal(key.toShortString()).formatted(Formatting.YELLOW);
                MutableText text3 = Text.translatable("editGamerule.default", Text.literal(key.getValueName(key.getDefaultValue()))).formatted(Formatting.GRAY);
                String string = key.getTranslationKey() + ".description";
                if (I18n.hasTranslation(string)) {
                    ImmutableList.Builder builder = ImmutableList.builder().add((Object)text2.asOrderedText());
                    MutableText text4 = Text.translatable(string);
                    EditGameRulesScreen.this.textRenderer.wrapLines(text4, 150).forEach(arg_0 -> ((ImmutableList.Builder)builder).add(arg_0));
                    list = builder.add((Object)text3.asOrderedText()).build();
                    string2 = text4.getString() + "\n" + text3.getString();
                } else {
                    list = ImmutableList.of((Object)text2.asOrderedText(), (Object)text3.asOrderedText());
                    string2 = text3.getString();
                }
                map.computeIfAbsent(key.getCategory(), category -> Maps.newHashMap()).put(key, widgetFactory.create(text, (List<OrderedText>)list, (String)string2, key));
            }
        });
        map.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.comparing(GameRuleCategory::getCategory))).forEach(entry -> {
            this.addEntry(new EditGameRulesScreen.RuleCategoryWidget(EditGameRulesScreen.this, ((GameRuleCategory)entry.getKey()).getText().formatted(Formatting.BOLD, Formatting.YELLOW)));
            ((Map)entry.getValue()).entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.comparing(GameRule::getTranslationKey))).forEach(e -> this.addEntry((EditGameRulesScreen.AbstractRuleWidget)e.getValue()));
        });
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderWidget(context, mouseX, mouseY, deltaTicks);
        EditGameRulesScreen.AbstractRuleWidget abstractRuleWidget = (EditGameRulesScreen.AbstractRuleWidget)this.getHoveredEntry();
        if (abstractRuleWidget != null && abstractRuleWidget.description != null) {
            context.drawTooltip(abstractRuleWidget.description, mouseX, mouseY);
        }
    }
}
