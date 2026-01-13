/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.itemgroup.v1.FabricCreativeInventoryScreen
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.cursor.StandardCursors
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.CreativeInventoryListener
 *  net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
 *  net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen$CreativeScreenHandler
 *  net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen$CreativeSlot
 *  net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen$LockableSlot
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 *  net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.input.CharInput
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.option.HotbarStorage
 *  net.minecraft.client.option.HotbarStorageEntry
 *  net.minecraft.client.search.SearchManager
 *  net.minecraft.client.search.SearchProvider
 *  net.minecraft.client.util.InputUtil
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.inventory.SimpleInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$TooltipContext
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemGroup
 *  net.minecraft.item.ItemGroup$Row
 *  net.minecraft.item.ItemGroup$Type
 *  net.minecraft.item.ItemGroups
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.item.tooltip.TooltipType
 *  net.minecraft.item.tooltip.TooltipType$Default
 *  net.minecraft.registry.DynamicRegistryManager
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.registry.entry.RegistryEntryList$Named
 *  net.minecraft.registry.tag.TagKey
 *  net.minecraft.resource.featuretoggle.FeatureSet
 *  net.minecraft.screen.PlayerScreenHandler
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.ScreenHandlerListener
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Unit
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.itemgroup.v1.FabricCreativeInventoryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryListener;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.HotbarStorage;
import net.minecraft.client.option.HotbarStorageEntry;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchProvider;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CreativeInventoryScreen
extends HandledScreen<CreativeScreenHandler>
implements FabricCreativeInventoryScreen {
    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla((String)"container/creative_inventory/scroller");
    private static final Identifier SCROLLER_DISABLED_TEXTURE = Identifier.ofVanilla((String)"container/creative_inventory/scroller_disabled");
    private static final Identifier[] TAB_TOP_UNSELECTED_TEXTURES = new Identifier[]{Identifier.ofVanilla((String)"container/creative_inventory/tab_top_unselected_1"), Identifier.ofVanilla((String)"container/creative_inventory/tab_top_unselected_2"), Identifier.ofVanilla((String)"container/creative_inventory/tab_top_unselected_3"), Identifier.ofVanilla((String)"container/creative_inventory/tab_top_unselected_4"), Identifier.ofVanilla((String)"container/creative_inventory/tab_top_unselected_5"), Identifier.ofVanilla((String)"container/creative_inventory/tab_top_unselected_6"), Identifier.ofVanilla((String)"container/creative_inventory/tab_top_unselected_7")};
    private static final Identifier[] TAB_TOP_SELECTED_TEXTURES = new Identifier[]{Identifier.ofVanilla((String)"container/creative_inventory/tab_top_selected_1"), Identifier.ofVanilla((String)"container/creative_inventory/tab_top_selected_2"), Identifier.ofVanilla((String)"container/creative_inventory/tab_top_selected_3"), Identifier.ofVanilla((String)"container/creative_inventory/tab_top_selected_4"), Identifier.ofVanilla((String)"container/creative_inventory/tab_top_selected_5"), Identifier.ofVanilla((String)"container/creative_inventory/tab_top_selected_6"), Identifier.ofVanilla((String)"container/creative_inventory/tab_top_selected_7")};
    private static final Identifier[] TAB_BOTTOM_UNSELECTED_TEXTURES = new Identifier[]{Identifier.ofVanilla((String)"container/creative_inventory/tab_bottom_unselected_1"), Identifier.ofVanilla((String)"container/creative_inventory/tab_bottom_unselected_2"), Identifier.ofVanilla((String)"container/creative_inventory/tab_bottom_unselected_3"), Identifier.ofVanilla((String)"container/creative_inventory/tab_bottom_unselected_4"), Identifier.ofVanilla((String)"container/creative_inventory/tab_bottom_unselected_5"), Identifier.ofVanilla((String)"container/creative_inventory/tab_bottom_unselected_6"), Identifier.ofVanilla((String)"container/creative_inventory/tab_bottom_unselected_7")};
    private static final Identifier[] TAB_BOTTOM_SELECTED_TEXTURES = new Identifier[]{Identifier.ofVanilla((String)"container/creative_inventory/tab_bottom_selected_1"), Identifier.ofVanilla((String)"container/creative_inventory/tab_bottom_selected_2"), Identifier.ofVanilla((String)"container/creative_inventory/tab_bottom_selected_3"), Identifier.ofVanilla((String)"container/creative_inventory/tab_bottom_selected_4"), Identifier.ofVanilla((String)"container/creative_inventory/tab_bottom_selected_5"), Identifier.ofVanilla((String)"container/creative_inventory/tab_bottom_selected_6"), Identifier.ofVanilla((String)"container/creative_inventory/tab_bottom_selected_7")};
    private static final int ROWS_COUNT = 5;
    private static final int COLUMNS_COUNT = 9;
    private static final int TAB_WIDTH = 26;
    private static final int TAB_HEIGHT = 32;
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_HEIGHT = 15;
    static final SimpleInventory INVENTORY = new SimpleInventory(45);
    private static final Text DELETE_ITEM_SLOT_TEXT = Text.translatable((String)"inventory.binSlot");
    private static ItemGroup selectedTab = ItemGroups.getDefaultTab();
    private float scrollPosition;
    private boolean scrolling;
    private TextFieldWidget searchBox;
    private @Nullable List<Slot> slots;
    private @Nullable Slot deleteItemSlot;
    private CreativeInventoryListener listener;
    private boolean ignoreTypedCharacter;
    private boolean lastClickOutsideBounds;
    private final Set<TagKey<Item>> searchResultTags = new HashSet();
    private final boolean operatorTabEnabled;
    private final StatusEffectsDisplay statusEffectsDisplay;

    public CreativeInventoryScreen(ClientPlayerEntity player, FeatureSet enabledFeatures, boolean operatorTabEnabled) {
        super((ScreenHandler)new CreativeScreenHandler((PlayerEntity)player), player.getInventory(), ScreenTexts.EMPTY);
        player.currentScreenHandler = this.handler;
        this.backgroundHeight = 136;
        this.backgroundWidth = 195;
        this.operatorTabEnabled = operatorTabEnabled;
        this.populateDisplay(player.networkHandler.getSearchManager(), enabledFeatures, this.shouldShowOperatorTab((PlayerEntity)player), (RegistryWrapper.WrapperLookup)player.getEntityWorld().getRegistryManager());
        this.statusEffectsDisplay = new StatusEffectsDisplay((HandledScreen)this);
    }

    private boolean shouldShowOperatorTab(PlayerEntity player) {
        return player.isCreativeLevelTwoOp() && this.operatorTabEnabled;
    }

    private void updateDisplayParameters(FeatureSet enabledFeatures, boolean showOperatorTab, RegistryWrapper.WrapperLookup registries) {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
        if (this.populateDisplay(clientPlayNetworkHandler != null ? clientPlayNetworkHandler.getSearchManager() : null, enabledFeatures, showOperatorTab, registries)) {
            for (ItemGroup itemGroup : ItemGroups.getGroups()) {
                Collection collection = itemGroup.getDisplayStacks();
                if (itemGroup != selectedTab) continue;
                if (itemGroup.getType() == ItemGroup.Type.CATEGORY && collection.isEmpty()) {
                    this.setSelectedTab(ItemGroups.getDefaultTab());
                    continue;
                }
                this.refreshSelectedTab(collection);
            }
        }
    }

    private boolean populateDisplay(@Nullable SearchManager searchManager, FeatureSet enabledFeatures, boolean showOperatorTab, RegistryWrapper.WrapperLookup registries) {
        if (!ItemGroups.updateDisplayContext((FeatureSet)enabledFeatures, (boolean)showOperatorTab, (RegistryWrapper.WrapperLookup)registries)) {
            return false;
        }
        if (searchManager != null) {
            List list = List.copyOf(ItemGroups.getSearchGroup().getDisplayStacks());
            searchManager.addItemTooltipReloader(registries, list);
            searchManager.addItemTagReloader(list);
        }
        return true;
    }

    private void refreshSelectedTab(Collection<ItemStack> displayStacks) {
        int i = ((CreativeScreenHandler)this.handler).getRow(this.scrollPosition);
        ((CreativeScreenHandler)this.handler).itemList.clear();
        if (selectedTab.getType() == ItemGroup.Type.SEARCH) {
            this.search();
        } else {
            ((CreativeScreenHandler)this.handler).itemList.addAll(displayStacks);
        }
        this.scrollPosition = ((CreativeScreenHandler)this.handler).getScrollPosition(i);
        ((CreativeScreenHandler)this.handler).scrollItems(this.scrollPosition);
    }

    public void handledScreenTick() {
        super.handledScreenTick();
        ClientPlayerEntity clientPlayerEntity = this.client.player;
        if (clientPlayerEntity != null) {
            this.updateDisplayParameters(clientPlayerEntity.networkHandler.getEnabledFeatures(), this.shouldShowOperatorTab((PlayerEntity)clientPlayerEntity), (RegistryWrapper.WrapperLookup)clientPlayerEntity.getEntityWorld().getRegistryManager());
            if (!clientPlayerEntity.isInCreativeMode()) {
                this.client.setScreen((Screen)new InventoryScreen((PlayerEntity)clientPlayerEntity));
            }
        }
    }

    protected void onMouseClick(@Nullable Slot slot, int slotId, int button, SlotActionType actionType) {
        if (this.isCreativeInventorySlot(slot)) {
            this.searchBox.setCursorToEnd(false);
            this.searchBox.setSelectionEnd(0);
        }
        boolean bl = actionType == SlotActionType.QUICK_MOVE;
        SlotActionType slotActionType = actionType = slotId == -999 && actionType == SlotActionType.PICKUP ? SlotActionType.THROW : actionType;
        if (actionType == SlotActionType.THROW && !this.client.player.canDropItems()) {
            return;
        }
        this.onMouseClick(slot, actionType);
        if (slot != null || selectedTab.getType() == ItemGroup.Type.INVENTORY || actionType == SlotActionType.QUICK_CRAFT) {
            if (slot != null && !slot.canTakeItems((PlayerEntity)this.client.player)) {
                return;
            }
            if (slot == this.deleteItemSlot && bl) {
                for (int i = 0; i < this.client.player.playerScreenHandler.getStacks().size(); ++i) {
                    this.client.player.playerScreenHandler.getSlot(i).setStackNoCallbacks(ItemStack.EMPTY);
                    this.client.interactionManager.clickCreativeStack(ItemStack.EMPTY, i);
                }
            } else if (selectedTab.getType() == ItemGroup.Type.INVENTORY) {
                if (slot == this.deleteItemSlot) {
                    ((CreativeScreenHandler)this.handler).setCursorStack(ItemStack.EMPTY);
                } else if (actionType == SlotActionType.THROW && slot != null && slot.hasStack()) {
                    ItemStack itemStack = slot.takeStack(button == 0 ? 1 : slot.getStack().getMaxCount());
                    ItemStack itemStack2 = slot.getStack();
                    this.client.player.dropItem(itemStack, true);
                    this.client.interactionManager.dropCreativeStack(itemStack);
                    this.client.interactionManager.clickCreativeStack(itemStack2, ((CreativeSlot)slot).slot.id);
                } else if (actionType == SlotActionType.THROW && slotId == -999 && !((CreativeScreenHandler)this.handler).getCursorStack().isEmpty()) {
                    this.client.player.dropItem(((CreativeScreenHandler)this.handler).getCursorStack(), true);
                    this.client.interactionManager.dropCreativeStack(((CreativeScreenHandler)this.handler).getCursorStack());
                    ((CreativeScreenHandler)this.handler).setCursorStack(ItemStack.EMPTY);
                } else {
                    this.client.player.playerScreenHandler.onSlotClick(slot == null ? slotId : ((CreativeSlot)slot).slot.id, button, actionType, (PlayerEntity)this.client.player);
                    this.client.player.playerScreenHandler.sendContentUpdates();
                }
            } else if (actionType != SlotActionType.QUICK_CRAFT && slot.inventory == INVENTORY) {
                ItemStack itemStack = ((CreativeScreenHandler)this.handler).getCursorStack();
                ItemStack itemStack2 = slot.getStack();
                if (actionType == SlotActionType.SWAP) {
                    if (!itemStack2.isEmpty()) {
                        this.client.player.getInventory().setStack(button, itemStack2.copyWithCount(itemStack2.getMaxCount()));
                        this.client.player.playerScreenHandler.sendContentUpdates();
                    }
                    return;
                }
                if (actionType == SlotActionType.CLONE) {
                    if (((CreativeScreenHandler)this.handler).getCursorStack().isEmpty() && slot.hasStack()) {
                        ItemStack itemStack3 = slot.getStack();
                        ((CreativeScreenHandler)this.handler).setCursorStack(itemStack3.copyWithCount(itemStack3.getMaxCount()));
                    }
                    return;
                }
                if (actionType == SlotActionType.THROW) {
                    if (!itemStack2.isEmpty()) {
                        ItemStack itemStack3 = itemStack2.copyWithCount(button == 0 ? 1 : itemStack2.getMaxCount());
                        this.client.player.dropItem(itemStack3, true);
                        this.client.interactionManager.dropCreativeStack(itemStack3);
                    }
                    return;
                }
                if (!itemStack.isEmpty() && !itemStack2.isEmpty() && ItemStack.areItemsAndComponentsEqual((ItemStack)itemStack, (ItemStack)itemStack2)) {
                    if (button == 0) {
                        if (bl) {
                            itemStack.setCount(itemStack.getMaxCount());
                        } else if (itemStack.getCount() < itemStack.getMaxCount()) {
                            itemStack.increment(1);
                        }
                    } else {
                        itemStack.decrement(1);
                    }
                } else if (itemStack2.isEmpty() || !itemStack.isEmpty()) {
                    if (button == 0) {
                        ((CreativeScreenHandler)this.handler).setCursorStack(ItemStack.EMPTY);
                    } else if (!((CreativeScreenHandler)this.handler).getCursorStack().isEmpty()) {
                        ((CreativeScreenHandler)this.handler).getCursorStack().decrement(1);
                    }
                } else {
                    int j = bl ? itemStack2.getMaxCount() : itemStack2.getCount();
                    ((CreativeScreenHandler)this.handler).setCursorStack(itemStack2.copyWithCount(j));
                }
            } else if (this.handler != null) {
                ItemStack itemStack = slot == null ? ItemStack.EMPTY : ((CreativeScreenHandler)this.handler).getSlot(slot.id).getStack();
                ((CreativeScreenHandler)this.handler).onSlotClick(slot == null ? slotId : slot.id, button, actionType, (PlayerEntity)this.client.player);
                if (ScreenHandler.unpackQuickCraftStage((int)button) == 2) {
                    for (int k = 0; k < 9; ++k) {
                        this.client.interactionManager.clickCreativeStack(((CreativeScreenHandler)this.handler).getSlot(45 + k).getStack(), 36 + k);
                    }
                } else if (slot != null && PlayerInventory.isValidHotbarIndex((int)slot.getIndex()) && selectedTab.getType() != ItemGroup.Type.INVENTORY) {
                    if (actionType == SlotActionType.THROW && !itemStack.isEmpty() && !((CreativeScreenHandler)this.handler).getCursorStack().isEmpty()) {
                        int k = button == 0 ? 1 : itemStack.getCount();
                        ItemStack itemStack3 = itemStack.copyWithCount(k);
                        itemStack.decrement(k);
                        this.client.player.dropItem(itemStack3, true);
                        this.client.interactionManager.dropCreativeStack(itemStack3);
                    }
                    this.client.player.playerScreenHandler.sendContentUpdates();
                }
            }
        } else if (!((CreativeScreenHandler)this.handler).getCursorStack().isEmpty() && this.lastClickOutsideBounds) {
            if (!this.client.player.canDropItems()) {
                return;
            }
            if (button == 0) {
                this.client.player.dropItem(((CreativeScreenHandler)this.handler).getCursorStack(), true);
                this.client.interactionManager.dropCreativeStack(((CreativeScreenHandler)this.handler).getCursorStack());
                ((CreativeScreenHandler)this.handler).setCursorStack(ItemStack.EMPTY);
            }
            if (button == 1) {
                ItemStack itemStack = ((CreativeScreenHandler)this.handler).getCursorStack().split(1);
                this.client.player.dropItem(itemStack, true);
                this.client.interactionManager.dropCreativeStack(itemStack);
            }
        }
    }

    private boolean isCreativeInventorySlot(@Nullable Slot slot) {
        return slot != null && slot.inventory == INVENTORY;
    }

    protected void init() {
        if (this.client.player.isInCreativeMode()) {
            super.init();
            Objects.requireNonNull(this.textRenderer);
            this.searchBox = new TextFieldWidget(this.textRenderer, this.x + 82, this.y + 6, 80, 9, (Text)Text.translatable((String)"itemGroup.search"));
            this.searchBox.setMaxLength(50);
            this.searchBox.setDrawsBackground(false);
            this.searchBox.setVisible(false);
            this.searchBox.setEditableColor(-1);
            this.searchBox.setInvertSelectionBackground(false);
            this.addSelectableChild((Element)this.searchBox);
            ItemGroup itemGroup = selectedTab;
            selectedTab = ItemGroups.getDefaultTab();
            this.setSelectedTab(itemGroup);
            this.client.player.playerScreenHandler.removeListener((ScreenHandlerListener)this.listener);
            this.listener = new CreativeInventoryListener(this.client);
            this.client.player.playerScreenHandler.addListener((ScreenHandlerListener)this.listener);
            if (!selectedTab.shouldDisplay()) {
                this.setSelectedTab(ItemGroups.getDefaultTab());
            }
        } else {
            this.client.setScreen((Screen)new InventoryScreen((PlayerEntity)this.client.player));
        }
    }

    public void resize(int width, int height) {
        int i = ((CreativeScreenHandler)this.handler).getRow(this.scrollPosition);
        String string = this.searchBox.getText();
        this.init(width, height);
        this.searchBox.setText(string);
        if (!this.searchBox.getText().isEmpty()) {
            this.search();
        }
        this.scrollPosition = ((CreativeScreenHandler)this.handler).getScrollPosition(i);
        ((CreativeScreenHandler)this.handler).scrollItems(this.scrollPosition);
    }

    public void removed() {
        super.removed();
        if (this.client.player != null && this.client.player.getInventory() != null) {
            this.client.player.playerScreenHandler.removeListener((ScreenHandlerListener)this.listener);
        }
    }

    public boolean charTyped(CharInput input) {
        if (this.ignoreTypedCharacter) {
            return false;
        }
        if (selectedTab.getType() != ItemGroup.Type.SEARCH) {
            return false;
        }
        String string = this.searchBox.getText();
        if (this.searchBox.charTyped(input)) {
            if (!Objects.equals(string, this.searchBox.getText())) {
                this.search();
            }
            return true;
        }
        return false;
    }

    public boolean keyPressed(KeyInput input) {
        this.ignoreTypedCharacter = false;
        if (selectedTab.getType() != ItemGroup.Type.SEARCH) {
            if (this.client.options.chatKey.matchesKey(input)) {
                this.ignoreTypedCharacter = true;
                this.setSelectedTab(ItemGroups.getSearchGroup());
                return true;
            }
            return super.keyPressed(input);
        }
        boolean bl = !this.isCreativeInventorySlot(this.focusedSlot) || this.focusedSlot.hasStack();
        boolean bl2 = InputUtil.fromKeyCode((KeyInput)input).toInt().isPresent();
        if (bl && bl2 && this.handleHotbarKeyPressed(input)) {
            this.ignoreTypedCharacter = true;
            return true;
        }
        String string = this.searchBox.getText();
        if (this.searchBox.keyPressed(input)) {
            if (!Objects.equals(string, this.searchBox.getText())) {
                this.search();
            }
            return true;
        }
        if (this.searchBox.isFocused() && this.searchBox.isVisible() && !input.isEscape()) {
            return true;
        }
        return super.keyPressed(input);
    }

    public boolean keyReleased(KeyInput input) {
        this.ignoreTypedCharacter = false;
        return super.keyReleased(input);
    }

    private void search() {
        ((CreativeScreenHandler)this.handler).itemList.clear();
        this.searchResultTags.clear();
        String string = this.searchBox.getText();
        if (string.isEmpty()) {
            ((CreativeScreenHandler)this.handler).itemList.addAll(selectedTab.getDisplayStacks());
        } else {
            ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
            if (clientPlayNetworkHandler != null) {
                SearchProvider searchProvider;
                SearchManager searchManager = clientPlayNetworkHandler.getSearchManager();
                if (string.startsWith("#")) {
                    string = string.substring(1);
                    searchProvider = searchManager.getItemTagReloadFuture();
                    this.searchForTags(string);
                } else {
                    searchProvider = searchManager.getItemTooltipReloadFuture();
                }
                ((CreativeScreenHandler)this.handler).itemList.addAll((Collection)searchProvider.findAll(string.toLowerCase(Locale.ROOT)));
            }
        }
        this.scrollPosition = 0.0f;
        ((CreativeScreenHandler)this.handler).scrollItems(0.0f);
    }

    private void searchForTags(String id2) {
        Predicate<Identifier> predicate;
        int i = id2.indexOf(58);
        if (i == -1) {
            predicate = id -> id.getPath().contains(id2);
        } else {
            String string = id2.substring(0, i).trim();
            String string2 = id2.substring(i + 1).trim();
            predicate = id -> id.getNamespace().contains(string) && id.getPath().contains(string2);
        }
        Registries.ITEM.streamTags().map(RegistryEntryList.Named::getTag).filter(tag -> predicate.test(tag.id())).forEach(this.searchResultTags::add);
    }

    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        if (selectedTab.shouldRenderName()) {
            context.drawText(this.textRenderer, selectedTab.getDisplayName(), 8, 6, -12566464, false);
        }
    }

    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() == 0) {
            double d = click.x() - (double)this.x;
            double e = click.y() - (double)this.y;
            for (ItemGroup itemGroup : ItemGroups.getGroupsToDisplay()) {
                if (!this.isClickInTab(itemGroup, d, e)) continue;
                return true;
            }
            if (selectedTab.getType() != ItemGroup.Type.INVENTORY && this.isClickInScrollbar(click.x(), click.y())) {
                this.scrolling = this.hasScrollbar();
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    public boolean mouseReleased(Click click) {
        if (click.button() == 0) {
            double d = click.x() - (double)this.x;
            double e = click.y() - (double)this.y;
            this.scrolling = false;
            for (ItemGroup itemGroup : ItemGroups.getGroupsToDisplay()) {
                if (!this.isClickInTab(itemGroup, d, e)) continue;
                this.setSelectedTab(itemGroup);
                return true;
            }
        }
        return super.mouseReleased(click);
    }

    private boolean hasScrollbar() {
        return selectedTab.hasScrollbar() && ((CreativeScreenHandler)this.handler).shouldShowScrollbar();
    }

    private void setSelectedTab(ItemGroup group) {
        int j;
        int i;
        ItemGroup itemGroup = selectedTab;
        selectedTab = group;
        this.cursorDragSlots.clear();
        ((CreativeScreenHandler)this.handler).itemList.clear();
        this.endTouchDrag();
        if (selectedTab.getType() == ItemGroup.Type.HOTBAR) {
            HotbarStorage hotbarStorage = this.client.getCreativeHotbarStorage();
            for (i = 0; i < 9; ++i) {
                HotbarStorageEntry hotbarStorageEntry = hotbarStorage.getSavedHotbar(i);
                if (hotbarStorageEntry.isEmpty()) {
                    for (j = 0; j < 9; ++j) {
                        if (j == i) {
                            ItemStack itemStack = new ItemStack((ItemConvertible)Items.PAPER);
                            itemStack.set(DataComponentTypes.CREATIVE_SLOT_LOCK, (Object)Unit.INSTANCE);
                            Text text = this.client.options.hotbarKeys[i].getBoundKeyLocalizedText();
                            Text text2 = this.client.options.saveToolbarActivatorKey.getBoundKeyLocalizedText();
                            itemStack.set(DataComponentTypes.ITEM_NAME, (Object)Text.translatable((String)"inventory.hotbarInfo", (Object[])new Object[]{text2, text}));
                            ((CreativeScreenHandler)this.handler).itemList.add((Object)itemStack);
                            continue;
                        }
                        ((CreativeScreenHandler)this.handler).itemList.add((Object)ItemStack.EMPTY);
                    }
                    continue;
                }
                ((CreativeScreenHandler)this.handler).itemList.addAll((Collection)hotbarStorageEntry.deserialize((RegistryWrapper.WrapperLookup)this.client.world.getRegistryManager()));
            }
        } else if (selectedTab.getType() == ItemGroup.Type.CATEGORY) {
            ((CreativeScreenHandler)this.handler).itemList.addAll(selectedTab.getDisplayStacks());
        }
        if (selectedTab.getType() == ItemGroup.Type.INVENTORY) {
            PlayerScreenHandler screenHandler = this.client.player.playerScreenHandler;
            if (this.slots == null) {
                this.slots = ImmutableList.copyOf((Collection)((CreativeScreenHandler)this.handler).slots);
            }
            ((CreativeScreenHandler)this.handler).slots.clear();
            for (i = 0; i < screenHandler.slots.size(); ++i) {
                int n;
                if (i >= 5 && i < 9) {
                    int k = i - 5;
                    l = k / 2;
                    m = k % 2;
                    n = 54 + l * 54;
                    j = 6 + m * 27;
                } else if (i >= 0 && i < 5) {
                    n = -2000;
                    j = -2000;
                } else if (i == 45) {
                    n = 35;
                    j = 20;
                } else {
                    int k = i - 9;
                    l = k % 9;
                    m = k / 9;
                    n = 9 + l * 18;
                    j = i >= 36 ? 112 : 54 + m * 18;
                }
                CreativeSlot slot = new CreativeSlot((Slot)screenHandler.slots.get(i), i, n, j);
                ((CreativeScreenHandler)this.handler).slots.add((Object)slot);
            }
            this.deleteItemSlot = new Slot((Inventory)INVENTORY, 0, 173, 112);
            ((CreativeScreenHandler)this.handler).slots.add((Object)this.deleteItemSlot);
        } else if (itemGroup.getType() == ItemGroup.Type.INVENTORY) {
            ((CreativeScreenHandler)this.handler).slots.clear();
            ((CreativeScreenHandler)this.handler).slots.addAll((Collection)this.slots);
            this.slots = null;
        }
        if (selectedTab.getType() == ItemGroup.Type.SEARCH) {
            this.searchBox.setVisible(true);
            this.searchBox.setFocusUnlocked(false);
            this.searchBox.setFocused(true);
            if (itemGroup != group) {
                this.searchBox.setText("");
            }
            this.search();
        } else {
            this.searchBox.setVisible(false);
            this.searchBox.setFocusUnlocked(true);
            this.searchBox.setFocused(false);
            this.searchBox.setText("");
        }
        this.scrollPosition = 0.0f;
        ((CreativeScreenHandler)this.handler).scrollItems(0.0f);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        }
        if (!this.hasScrollbar()) {
            return false;
        }
        this.scrollPosition = ((CreativeScreenHandler)this.handler).getScrollPosition(this.scrollPosition, verticalAmount);
        ((CreativeScreenHandler)this.handler).scrollItems(this.scrollPosition);
        return true;
    }

    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top) {
        boolean bl = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
        this.lastClickOutsideBounds = bl && !this.isClickInTab(selectedTab, mouseX, mouseY);
        return this.lastClickOutsideBounds;
    }

    protected boolean isClickInScrollbar(double mouseX, double mouseY) {
        int i = this.x;
        int j = this.y;
        int k = i + 175;
        int l = j + 18;
        int m = k + 14;
        int n = l + 112;
        return mouseX >= (double)k && mouseY >= (double)l && mouseX < (double)m && mouseY < (double)n;
    }

    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (this.scrolling) {
            int i = this.y + 18;
            int j = i + 112;
            this.scrollPosition = ((float)click.y() - (float)i - 7.5f) / ((float)(j - i) - 15.0f);
            this.scrollPosition = MathHelper.clamp((float)this.scrollPosition, (float)0.0f, (float)1.0f);
            ((CreativeScreenHandler)this.handler).scrollItems(this.scrollPosition);
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        ItemGroup itemGroup;
        this.statusEffectsDisplay.render(context, mouseX, mouseY);
        super.render(context, mouseX, mouseY, deltaTicks);
        Iterator iterator = ItemGroups.getGroupsToDisplay().iterator();
        while (iterator.hasNext() && !this.renderTabTooltipIfHovered(context, itemGroup = (ItemGroup)iterator.next(), mouseX, mouseY)) {
        }
        if (this.deleteItemSlot != null && selectedTab.getType() == ItemGroup.Type.INVENTORY && this.isPointWithinBounds(this.deleteItemSlot.x, this.deleteItemSlot.y, 16, 16, (double)mouseX, (double)mouseY)) {
            context.drawTooltip(this.textRenderer, DELETE_ITEM_SLOT_TEXT, mouseX, mouseY);
        }
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    public boolean showsStatusEffects() {
        return this.statusEffectsDisplay.shouldHideStatusEffectHud();
    }

    public List<Text> getTooltipFromItem(ItemStack stack) {
        boolean bl = this.focusedSlot != null && this.focusedSlot instanceof LockableSlot;
        boolean bl2 = selectedTab.getType() == ItemGroup.Type.CATEGORY;
        boolean bl3 = selectedTab.getType() == ItemGroup.Type.SEARCH;
        TooltipType.Default default_ = this.client.options.advancedItemTooltips ? TooltipType.Default.ADVANCED : TooltipType.Default.BASIC;
        TooltipType.Default tooltipType = bl ? default_.withCreative() : default_;
        List list = stack.getTooltip(Item.TooltipContext.create((World)this.client.world), (PlayerEntity)this.client.player, (TooltipType)tooltipType);
        if (list.isEmpty()) {
            return list;
        }
        if (!bl2 || !bl) {
            ArrayList list2 = Lists.newArrayList((Iterable)list);
            if (bl3 && bl) {
                this.searchResultTags.forEach(tagKey -> {
                    if (stack.isIn(tagKey)) {
                        list2.add(1, Text.literal((String)("#" + String.valueOf(tagKey.id()))).formatted(Formatting.DARK_PURPLE));
                    }
                });
            }
            int i = 1;
            for (ItemGroup itemGroup : ItemGroups.getGroupsToDisplay()) {
                if (itemGroup.getType() == ItemGroup.Type.SEARCH || !itemGroup.contains(stack)) continue;
                list2.add(i++, itemGroup.getDisplayName().copy().formatted(Formatting.BLUE));
            }
            return list2;
        }
        return list;
    }

    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        for (ItemGroup itemGroup : ItemGroups.getGroupsToDisplay()) {
            if (itemGroup == selectedTab) continue;
            this.renderTabIcon(context, mouseX, mouseY, itemGroup);
        }
        context.drawTexture(RenderPipelines.GUI_TEXTURED, selectedTab.getTexture(), this.x, this.y, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 256);
        if (this.isClickInScrollbar((double)mouseX, (double)mouseY) && this.hasScrollbar()) {
            context.setCursor(this.scrolling ? StandardCursors.RESIZE_NS : StandardCursors.POINTING_HAND);
        }
        this.searchBox.render(context, mouseX, mouseY, deltaTicks);
        int i = this.x + 175;
        int j = this.y + 18;
        int k = j + 112;
        if (selectedTab.hasScrollbar()) {
            Identifier identifier = this.hasScrollbar() ? SCROLLER_TEXTURE : SCROLLER_DISABLED_TEXTURE;
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, i, j + (int)((float)(k - j - 17) * this.scrollPosition), 12, 15);
        }
        this.renderTabIcon(context, mouseX, mouseY, selectedTab);
        if (selectedTab.getType() == ItemGroup.Type.INVENTORY) {
            InventoryScreen.drawEntity((DrawContext)context, (int)(this.x + 73), (int)(this.y + 6), (int)(this.x + 105), (int)(this.y + 49), (int)20, (float)0.0625f, (float)mouseX, (float)mouseY, (LivingEntity)this.client.player);
        }
    }

    private int getTabX(ItemGroup group) {
        int i = group.getColumn();
        int j = 27;
        int k = 27 * i;
        if (group.isSpecial()) {
            k = this.backgroundWidth - 27 * (7 - i) + 1;
        }
        return k;
    }

    private int getTabY(ItemGroup group) {
        int i = 0;
        i = group.getRow() == ItemGroup.Row.TOP ? (i -= 32) : (i += this.backgroundHeight);
        return i;
    }

    protected boolean isClickInTab(ItemGroup group, double mouseX, double mouseY) {
        int i = this.getTabX(group);
        int j = this.getTabY(group);
        return mouseX >= (double)i && mouseX <= (double)(i + 26) && mouseY >= (double)j && mouseY <= (double)(j + 32);
    }

    protected boolean renderTabTooltipIfHovered(DrawContext context, ItemGroup group, int mouseX, int mouseY) {
        int j;
        int i = this.getTabX(group);
        if (this.isPointWithinBounds(i + 3, (j = this.getTabY(group)) + 3, 21, 27, (double)mouseX, (double)mouseY)) {
            context.drawTooltip(this.textRenderer, group.getDisplayName(), mouseX, mouseY);
            return true;
        }
        return false;
    }

    protected void renderTabIcon(DrawContext context, int mouseX, int mouseY, ItemGroup tab) {
        Identifier[] identifiers;
        boolean bl = tab == selectedTab;
        boolean bl2 = tab.getRow() == ItemGroup.Row.TOP;
        int i = tab.getColumn();
        int j = this.x + this.getTabX(tab);
        int k = this.y - (bl2 ? 28 : -(this.backgroundHeight - 4));
        if (bl2) {
            identifiers = bl ? TAB_TOP_SELECTED_TEXTURES : TAB_TOP_UNSELECTED_TEXTURES;
        } else {
            Identifier[] identifierArray = identifiers = bl ? TAB_BOTTOM_SELECTED_TEXTURES : TAB_BOTTOM_UNSELECTED_TEXTURES;
        }
        if (!bl && mouseX > j && mouseY > k && mouseX < j + 26 && mouseY < k + 32) {
            context.setCursor(StandardCursors.POINTING_HAND);
        }
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifiers[MathHelper.clamp((int)i, (int)0, (int)identifiers.length)], j, k, 26, 32);
        int l = j + 13 - 8;
        int m = k + 16 - 8 + (bl2 ? 1 : -1);
        context.drawItem(tab.getIcon(), l, m);
    }

    public boolean isInventoryTabSelected() {
        return selectedTab.getType() == ItemGroup.Type.INVENTORY;
    }

    public static void onHotbarKeyPress(MinecraftClient client, int index, boolean restore, boolean save) {
        ClientPlayerEntity clientPlayerEntity = client.player;
        DynamicRegistryManager dynamicRegistryManager = clientPlayerEntity.getEntityWorld().getRegistryManager();
        HotbarStorage hotbarStorage = client.getCreativeHotbarStorage();
        HotbarStorageEntry hotbarStorageEntry = hotbarStorage.getSavedHotbar(index);
        if (restore) {
            List list = hotbarStorageEntry.deserialize((RegistryWrapper.WrapperLookup)dynamicRegistryManager);
            for (int i = 0; i < PlayerInventory.getHotbarSize(); ++i) {
                ItemStack itemStack = (ItemStack)list.get(i);
                clientPlayerEntity.getInventory().setStack(i, itemStack);
                client.interactionManager.clickCreativeStack(itemStack, 36 + i);
            }
            clientPlayerEntity.playerScreenHandler.sendContentUpdates();
        } else if (save) {
            hotbarStorageEntry.serialize(clientPlayerEntity.getInventory(), dynamicRegistryManager);
            Text text = client.options.hotbarKeys[index].getBoundKeyLocalizedText();
            Text text2 = client.options.loadToolbarActivatorKey.getBoundKeyLocalizedText();
            MutableText text3 = Text.translatable((String)"inventory.hotbarSaved", (Object[])new Object[]{text2, text});
            client.inGameHud.setOverlayMessage((Text)text3, false);
            client.getNarratorManager().narrateSystemImmediately((Text)text3);
            hotbarStorage.save();
        }
    }
}

