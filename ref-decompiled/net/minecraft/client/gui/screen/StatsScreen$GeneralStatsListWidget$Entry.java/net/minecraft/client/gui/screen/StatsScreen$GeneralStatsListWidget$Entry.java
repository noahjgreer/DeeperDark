/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.stat.Stat;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
class StatsScreen.GeneralStatsListWidget.Entry
extends AlwaysSelectedEntryListWidget.Entry<StatsScreen.GeneralStatsListWidget.Entry> {
    private final Stat<Identifier> stat;
    private final Text displayName;

    StatsScreen.GeneralStatsListWidget.Entry(Stat<Identifier> stat) {
        this.stat = stat;
        this.displayName = Text.translatable(StatsScreen.getStatTranslationKey(stat));
    }

    private String getFormatted() {
        return this.stat.format(GeneralStatsListWidget.this.field_18750.statHandler.getStat(this.stat));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        int i = this.getContentMiddleY() - ((StatsScreen)GeneralStatsListWidget.this.field_18750).textRenderer.fontHeight / 2;
        int j = GeneralStatsListWidget.this.children().indexOf(this);
        int k = j % 2 == 0 ? -1 : -4539718;
        context.drawTextWithShadow(GeneralStatsListWidget.this.field_18750.textRenderer, this.displayName, this.getContentX() + 2, i, k);
        String string = this.getFormatted();
        context.drawTextWithShadow(GeneralStatsListWidget.this.field_18750.textRenderer, string, this.getContentRightEnd() - GeneralStatsListWidget.this.field_18750.textRenderer.getWidth(string) - 4, i, k);
    }

    @Override
    public Text getNarration() {
        return Text.translatable("narrator.select", Text.empty().append(this.displayName).append(ScreenTexts.SPACE).append(this.getFormatted()));
    }
}
