/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
class StatsScreen.GeneralStatsListWidget
extends AlwaysSelectedEntryListWidget<Entry> {
    public StatsScreen.GeneralStatsListWidget(MinecraftClient client) {
        super(client, StatsScreen.this.width, StatsScreen.this.layout.getContentHeight(), 33, 14);
        ObjectArrayList objectArrayList = new ObjectArrayList(Stats.CUSTOM.iterator());
        objectArrayList.sort(Comparator.comparing(stat -> I18n.translate(StatsScreen.getStatTranslationKey(stat), new Object[0])));
        for (Stat stat2 : objectArrayList) {
            this.addEntry(new Entry(stat2));
        }
    }

    @Override
    public int getRowWidth() {
        return 280;
    }

    @Override
    protected void drawMenuListBackground(DrawContext context) {
    }

    @Override
    protected void drawHeaderAndFooterSeparators(DrawContext context) {
    }

    @Environment(value=EnvType.CLIENT)
    class Entry
    extends AlwaysSelectedEntryListWidget.Entry<Entry> {
        private final Stat<Identifier> stat;
        private final Text displayName;

        Entry(Stat<Identifier> stat) {
            this.stat = stat;
            this.displayName = Text.translatable(StatsScreen.getStatTranslationKey(stat));
        }

        private String getFormatted() {
            return this.stat.format(StatsScreen.this.statHandler.getStat(this.stat));
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            int i = this.getContentMiddleY() - ((StatsScreen)StatsScreen.this).textRenderer.fontHeight / 2;
            int j = GeneralStatsListWidget.this.children().indexOf(this);
            int k = j % 2 == 0 ? -1 : -4539718;
            context.drawTextWithShadow(StatsScreen.this.textRenderer, this.displayName, this.getContentX() + 2, i, k);
            String string = this.getFormatted();
            context.drawTextWithShadow(StatsScreen.this.textRenderer, string, this.getContentRightEnd() - StatsScreen.this.textRenderer.getWidth(string) - 4, i, k);
        }

        @Override
        public Text getNarration() {
            return Text.translatable("narrator.select", Text.empty().append(this.displayName).append(ScreenTexts.SPACE).append(this.getFormatted()));
        }
    }
}
