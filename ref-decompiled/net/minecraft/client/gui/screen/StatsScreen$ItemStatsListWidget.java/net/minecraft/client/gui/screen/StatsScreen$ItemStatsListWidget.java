/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.ItemStackWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class StatsScreen.ItemStatsListWidget
extends ElementListWidget<Entry> {
    private static final int field_49524 = 18;
    private static final int field_49525 = 22;
    private static final int field_49526 = 1;
    private static final int field_49527 = 0;
    private static final int field_49528 = -1;
    private static final int field_49529 = 1;
    protected final List<StatType<Block>> blockStatTypes;
    protected final List<StatType<Item>> itemStatTypes;
    protected final Comparator<StatEntry> comparator;
    protected @Nullable StatType<?> selectedStatType;
    protected int listOrder;

    public StatsScreen.ItemStatsListWidget(MinecraftClient client) {
        boolean bl;
        super(client, StatsScreen.this.width, StatsScreen.this.layout.getContentHeight(), 33, 22);
        this.comparator = new ItemComparator();
        this.blockStatTypes = Lists.newArrayList();
        this.blockStatTypes.add(Stats.MINED);
        this.itemStatTypes = Lists.newArrayList((Object[])new StatType[]{Stats.BROKEN, Stats.CRAFTED, Stats.USED, Stats.PICKED_UP, Stats.DROPPED});
        Set set = Sets.newIdentityHashSet();
        for (Item item : Registries.ITEM) {
            bl = false;
            for (StatType<Item> statType : this.itemStatTypes) {
                if (!statType.hasStat(item) || StatsScreen.this.statHandler.getStat(statType.getOrCreateStat(item)) <= 0) continue;
                bl = true;
            }
            if (!bl) continue;
            set.add(item);
        }
        for (Block block : Registries.BLOCK) {
            bl = false;
            for (StatType<ItemConvertible> statType : this.blockStatTypes) {
                if (!statType.hasStat(block) || StatsScreen.this.statHandler.getStat(statType.getOrCreateStat(block)) <= 0) continue;
                bl = true;
            }
            if (!bl) continue;
            set.add(block.asItem());
        }
        set.remove(Items.AIR);
        if (!set.isEmpty()) {
            this.addEntry(new Header());
            for (Item item : set) {
                this.addEntry(new StatEntry(item));
            }
        }
    }

    @Override
    protected void drawMenuListBackground(DrawContext context) {
    }

    int getIconX(int index) {
        return 75 + 40 * index;
    }

    @Override
    public int getRowWidth() {
        return 280;
    }

    StatType<?> getStatType(int headerColumn) {
        return headerColumn < this.blockStatTypes.size() ? this.blockStatTypes.get(headerColumn) : this.itemStatTypes.get(headerColumn - this.blockStatTypes.size());
    }

    int getHeaderIndex(StatType<?> statType) {
        int i = this.blockStatTypes.indexOf(statType);
        if (i >= 0) {
            return i;
        }
        int j = this.itemStatTypes.indexOf(statType);
        if (j >= 0) {
            return j + this.blockStatTypes.size();
        }
        return -1;
    }

    protected void selectStatType(StatType<?> statType) {
        if (statType != this.selectedStatType) {
            this.selectedStatType = statType;
            this.listOrder = -1;
        } else if (this.listOrder == -1) {
            this.listOrder = 1;
        } else {
            this.selectedStatType = null;
            this.listOrder = 0;
        }
        this.sortStats(this.comparator);
    }

    protected void sortStats(Comparator<StatEntry> comparator) {
        List<StatEntry> list = this.getStatEntries();
        list.sort(comparator);
        this.clearEntriesExcept((Entry)this.children().getFirst());
        for (StatEntry statEntry : list) {
            this.addEntry(statEntry);
        }
    }

    private List<StatEntry> getStatEntries() {
        ArrayList<StatEntry> list = new ArrayList<StatEntry>();
        this.children().forEach(child -> {
            if (child instanceof StatEntry) {
                StatEntry statEntry = (StatEntry)child;
                list.add(statEntry);
            }
        });
        return list;
    }

    @Override
    protected void drawHeaderAndFooterSeparators(DrawContext context) {
    }

    @Environment(value=EnvType.CLIENT)
    class ItemComparator
    implements Comparator<StatEntry> {
        ItemComparator() {
        }

        @Override
        public int compare(StatEntry statEntry, StatEntry statEntry2) {
            int j;
            int i;
            Item item = statEntry.getItem();
            Item item2 = statEntry2.getItem();
            if (ItemStatsListWidget.this.selectedStatType == null) {
                i = 0;
                j = 0;
            } else if (ItemStatsListWidget.this.blockStatTypes.contains(ItemStatsListWidget.this.selectedStatType)) {
                StatType<?> statType = ItemStatsListWidget.this.selectedStatType;
                i = item instanceof BlockItem ? StatsScreen.this.statHandler.getStat(statType, ((BlockItem)item).getBlock()) : -1;
                j = item2 instanceof BlockItem ? StatsScreen.this.statHandler.getStat(statType, ((BlockItem)item2).getBlock()) : -1;
            } else {
                StatType<?> statType = ItemStatsListWidget.this.selectedStatType;
                i = StatsScreen.this.statHandler.getStat(statType, item);
                j = StatsScreen.this.statHandler.getStat(statType, item2);
            }
            if (i == j) {
                return ItemStatsListWidget.this.listOrder * Integer.compare(Item.getRawId(item), Item.getRawId(item2));
            }
            return ItemStatsListWidget.this.listOrder * Integer.compare(i, j);
        }

        @Override
        public /* synthetic */ int compare(Object a, Object b) {
            return this.compare((StatEntry)a, (StatEntry)b);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class Header
    extends Entry {
        private static final Identifier BLOCK_MINED_TEXTURE = Identifier.ofVanilla("statistics/block_mined");
        private static final Identifier ITEM_BROKEN_TEXTURE = Identifier.ofVanilla("statistics/item_broken");
        private static final Identifier ITEM_CRAFTED_TEXTURE = Identifier.ofVanilla("statistics/item_crafted");
        private static final Identifier ITEM_USED_TEXTURE = Identifier.ofVanilla("statistics/item_used");
        private static final Identifier ITEM_PICKED_UP_TEXTURE = Identifier.ofVanilla("statistics/item_picked_up");
        private static final Identifier ITEM_DROPPED_TEXTURE = Identifier.ofVanilla("statistics/item_dropped");
        private final HeaderButton blockMinedButton;
        private final HeaderButton itemBrokenButton;
        private final HeaderButton itemCraftedButton;
        private final HeaderButton itemUsedButton;
        private final HeaderButton itemPickedUpButton;
        private final HeaderButton itemDroppedButton;
        private final List<ClickableWidget> buttons = new ArrayList<ClickableWidget>();

        Header() {
            this.blockMinedButton = new HeaderButton(this, 0, BLOCK_MINED_TEXTURE);
            this.itemBrokenButton = new HeaderButton(this, 1, ITEM_BROKEN_TEXTURE);
            this.itemCraftedButton = new HeaderButton(this, 2, ITEM_CRAFTED_TEXTURE);
            this.itemUsedButton = new HeaderButton(this, 3, ITEM_USED_TEXTURE);
            this.itemPickedUpButton = new HeaderButton(this, 4, ITEM_PICKED_UP_TEXTURE);
            this.itemDroppedButton = new HeaderButton(this, 5, ITEM_DROPPED_TEXTURE);
            this.buttons.addAll(List.of(this.blockMinedButton, this.itemBrokenButton, this.itemCraftedButton, this.itemUsedButton, this.itemPickedUpButton, this.itemDroppedButton));
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            this.blockMinedButton.setPosition(this.getContentX() + ItemStatsListWidget.this.getIconX(0) - 18, this.getContentY() + 1);
            this.blockMinedButton.render(context, mouseX, mouseY, deltaTicks);
            this.itemBrokenButton.setPosition(this.getContentX() + ItemStatsListWidget.this.getIconX(1) - 18, this.getContentY() + 1);
            this.itemBrokenButton.render(context, mouseX, mouseY, deltaTicks);
            this.itemCraftedButton.setPosition(this.getContentX() + ItemStatsListWidget.this.getIconX(2) - 18, this.getContentY() + 1);
            this.itemCraftedButton.render(context, mouseX, mouseY, deltaTicks);
            this.itemUsedButton.setPosition(this.getContentX() + ItemStatsListWidget.this.getIconX(3) - 18, this.getContentY() + 1);
            this.itemUsedButton.render(context, mouseX, mouseY, deltaTicks);
            this.itemPickedUpButton.setPosition(this.getContentX() + ItemStatsListWidget.this.getIconX(4) - 18, this.getContentY() + 1);
            this.itemPickedUpButton.render(context, mouseX, mouseY, deltaTicks);
            this.itemDroppedButton.setPosition(this.getContentX() + ItemStatsListWidget.this.getIconX(5) - 18, this.getContentY() + 1);
            this.itemDroppedButton.render(context, mouseX, mouseY, deltaTicks);
            if (ItemStatsListWidget.this.selectedStatType != null) {
                int i = ItemStatsListWidget.this.getIconX(ItemStatsListWidget.this.getHeaderIndex(ItemStatsListWidget.this.selectedStatType)) - 36;
                Identifier identifier = ItemStatsListWidget.this.listOrder == 1 ? SORT_UP_TEXTURE : SORT_DOWN_TEXTURE;
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, this.getContentX() + i, this.getContentY() + 1, 18, 18);
            }
        }

        @Override
        public List<? extends Element> children() {
            return this.buttons;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return this.buttons;
        }

        @Environment(value=EnvType.CLIENT)
        class HeaderButton
        extends TexturedButtonWidget {
            private final Identifier texture;

            HeaderButton(Header header, int index, Identifier texture) {
                super(18, 18, new ButtonTextures(HEADER_TEXTURE, SLOT_TEXTURE), button -> header.ItemStatsListWidget.this.selectStatType(header.ItemStatsListWidget.this.getStatType(index)), header.ItemStatsListWidget.this.getStatType(index).getName());
                this.texture = texture;
                this.setTooltip(Tooltip.of(this.getMessage()));
            }

            @Override
            public void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
                Identifier identifier = this.textures.get(this.isInteractable(), this.isSelected());
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, this.getX(), this.getY(), this.width, this.height);
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.texture, this.getX(), this.getY(), this.width, this.height);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class StatEntry
    extends Entry {
        private final Item item;
        private final ItemStackInSlotWidget button;

        StatEntry(Item item) {
            this.item = item;
            this.button = new ItemStackInSlotWidget(item.getDefaultStack());
        }

        protected Item getItem() {
            return this.item;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            int j;
            this.button.setPosition(this.getContentX(), this.getContentY());
            this.button.render(context, mouseX, mouseY, deltaTicks);
            StatsScreen.ItemStatsListWidget itemStatsListWidget = ItemStatsListWidget.this;
            int i = itemStatsListWidget.children().indexOf(this);
            for (j = 0; j < itemStatsListWidget.blockStatTypes.size(); ++j) {
                Stat<Block> stat;
                Item item = this.item;
                if (item instanceof BlockItem) {
                    BlockItem blockItem = (BlockItem)item;
                    stat = itemStatsListWidget.blockStatTypes.get(j).getOrCreateStat(blockItem.getBlock());
                } else {
                    stat = null;
                }
                this.render(context, stat, this.getContentX() + ItemStatsListWidget.this.getIconX(j), this.getContentMiddleY() - ((StatsScreen)StatsScreen.this).textRenderer.fontHeight / 2, i % 2 == 0);
            }
            for (j = 0; j < itemStatsListWidget.itemStatTypes.size(); ++j) {
                this.render(context, itemStatsListWidget.itemStatTypes.get(j).getOrCreateStat(this.item), this.getContentX() + ItemStatsListWidget.this.getIconX(j + itemStatsListWidget.blockStatTypes.size()), this.getContentMiddleY() - ((StatsScreen)StatsScreen.this).textRenderer.fontHeight / 2, i % 2 == 0);
            }
        }

        protected void render(DrawContext context, @Nullable Stat<?> stat, int x, int y, boolean white) {
            Text text = stat == null ? NONE_TEXT : Text.literal(stat.format(StatsScreen.this.statHandler.getStat(stat)));
            context.drawTextWithShadow(StatsScreen.this.textRenderer, text, x - StatsScreen.this.textRenderer.getWidth(text), y, white ? -1 : -4539718);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(this.button);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(this.button);
        }

        @Environment(value=EnvType.CLIENT)
        class ItemStackInSlotWidget
        extends ItemStackWidget {
            ItemStackInSlotWidget(ItemStack stack) {
                super(ItemStatsListWidget.this.client, 1, 1, 18, 18, stack.getName(), stack, false, true);
            }

            @Override
            protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_TEXTURE, StatEntry.this.getContentX(), StatEntry.this.getContentY(), 18, 18);
                super.renderWidget(context, mouseX, mouseY, deltaTicks);
            }

            @Override
            protected void renderTooltip(DrawContext context, int mouseX, int mouseY) {
                super.renderTooltip(context, StatEntry.this.getContentX() + 18, StatEntry.this.getContentY() + 18);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static abstract class Entry
    extends ElementListWidget.Entry<Entry> {
        Entry() {
        }
    }
}
