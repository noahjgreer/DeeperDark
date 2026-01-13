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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class StatsScreen.EntityStatsListWidget
extends AlwaysSelectedEntryListWidget<Entry> {
    public StatsScreen.EntityStatsListWidget(MinecraftClient client) {
        super(client, StatsScreen.this.width, StatsScreen.this.layout.getContentHeight(), 33, ((StatsScreen)StatsScreen.this).textRenderer.fontHeight * 4);
        for (EntityType entityType : Registries.ENTITY_TYPE) {
            if (StatsScreen.this.statHandler.getStat(Stats.KILLED.getOrCreateStat(entityType)) <= 0 && StatsScreen.this.statHandler.getStat(Stats.KILLED_BY.getOrCreateStat(entityType)) <= 0) continue;
            this.addEntry(new Entry(entityType));
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
        private final Text entityTypeName;
        private final Text killedText;
        private final Text killedByText;
        private final boolean killedAny;
        private final boolean killedByAny;

        public Entry(EntityType<?> entityType) {
            this.entityTypeName = entityType.getName();
            int i = StatsScreen.this.statHandler.getStat(Stats.KILLED.getOrCreateStat(entityType));
            if (i == 0) {
                this.killedText = Text.translatable("stat_type.minecraft.killed.none", this.entityTypeName);
                this.killedAny = false;
            } else {
                this.killedText = Text.translatable("stat_type.minecraft.killed", i, this.entityTypeName);
                this.killedAny = true;
            }
            int j = StatsScreen.this.statHandler.getStat(Stats.KILLED_BY.getOrCreateStat(entityType));
            if (j == 0) {
                this.killedByText = Text.translatable("stat_type.minecraft.killed_by.none", this.entityTypeName);
                this.killedByAny = false;
            } else {
                this.killedByText = Text.translatable("stat_type.minecraft.killed_by", this.entityTypeName, j);
                this.killedByAny = true;
            }
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            context.drawTextWithShadow(StatsScreen.this.textRenderer, this.entityTypeName, this.getContentX() + 2, this.getContentY() + 1, -1);
            context.drawTextWithShadow(StatsScreen.this.textRenderer, this.killedText, this.getContentX() + 2 + 10, this.getContentY() + 1 + ((StatsScreen)StatsScreen.this).textRenderer.fontHeight, this.killedAny ? -4539718 : -8355712);
            context.drawTextWithShadow(StatsScreen.this.textRenderer, this.killedByText, this.getContentX() + 2 + 10, this.getContentY() + 1 + ((StatsScreen)StatsScreen.this).textRenderer.fontHeight * 2, this.killedByAny ? -4539718 : -8355712);
        }

        @Override
        public Text getNarration() {
            return Text.translatable("narrator.select", ScreenTexts.joinSentences(this.killedText, this.killedByText));
        }
    }
}
