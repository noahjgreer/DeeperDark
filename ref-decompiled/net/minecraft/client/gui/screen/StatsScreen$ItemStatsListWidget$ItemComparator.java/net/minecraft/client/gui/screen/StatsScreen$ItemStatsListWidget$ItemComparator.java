/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.stat.StatType;

@Environment(value=EnvType.CLIENT)
class StatsScreen.ItemStatsListWidget.ItemComparator
implements Comparator<StatsScreen.ItemStatsListWidget.StatEntry> {
    StatsScreen.ItemStatsListWidget.ItemComparator() {
    }

    @Override
    public int compare(StatsScreen.ItemStatsListWidget.StatEntry statEntry, StatsScreen.ItemStatsListWidget.StatEntry statEntry2) {
        int j;
        int i;
        Item item = statEntry.getItem();
        Item item2 = statEntry2.getItem();
        if (ItemStatsListWidget.this.selectedStatType == null) {
            i = 0;
            j = 0;
        } else if (ItemStatsListWidget.this.blockStatTypes.contains(ItemStatsListWidget.this.selectedStatType)) {
            StatType<?> statType = ItemStatsListWidget.this.selectedStatType;
            i = item instanceof BlockItem ? ItemStatsListWidget.this.field_18752.statHandler.getStat(statType, ((BlockItem)item).getBlock()) : -1;
            j = item2 instanceof BlockItem ? ItemStatsListWidget.this.field_18752.statHandler.getStat(statType, ((BlockItem)item2).getBlock()) : -1;
        } else {
            StatType<?> statType = ItemStatsListWidget.this.selectedStatType;
            i = ItemStatsListWidget.this.field_18752.statHandler.getStat(statType, item);
            j = ItemStatsListWidget.this.field_18752.statHandler.getStat(statType, item2);
        }
        if (i == j) {
            return ItemStatsListWidget.this.listOrder * Integer.compare(Item.getRawId(item), Item.getRawId(item2));
        }
        return ItemStatsListWidget.this.listOrder * Integer.compare(i, j);
    }

    @Override
    public /* synthetic */ int compare(Object a, Object b) {
        return this.compare((StatsScreen.ItemStatsListWidget.StatEntry)a, (StatsScreen.ItemStatsListWidget.StatEntry)b);
    }
}
