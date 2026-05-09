package net.noahsarch.deeperdark.menu;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.noahsarch.deeperdark.component.CollarFuelData;
import net.noahsarch.deeperdark.component.ModComponents;
import net.noahsarch.deeperdark.item.CollarItem;
import net.noahsarch.deeperdark.item.CollarTier;
import net.noahsarch.deeperdark.item.ItemMagnetItem;

import java.util.ArrayList;
import java.util.List;

public class CollarBenchMenu extends AbstractContainerMenu {

    // Slot indices in this menu
    public static final int COLLAR_INPUT_SLOT = 0;
    public static final int FUEL_INPUT_SLOT   = 1;
    public static final int OUTPUT_SLOT       = 2;
    // Trinket slots: 3-7
    // Player inventory: 8-43

    private static final int TRINKET_START  = 3;
    private static final int TRINKET_COUNT  = 5;
    private static final int PLAYER_INV_START = 8;
    private static final int PLAYER_INV_SIZE  = 36;

    private final Container beContainer;
    private final SimpleContainer trinketContainer;
    private final ResultContainer resultContainer = new ResultContainer();
    private final Player player;

    // Track original trinkets so we can detect new vs repositioned
    private List<Item> originalTrinketItems = new ArrayList<>();
    // Per-slot snapshot of trinkets when the collar was inserted (for dirty-check and rollback)
    private NonNullList<ItemStack> originalTrinketSlots = NonNullList.withSize(TRINKET_COUNT, ItemStack.EMPTY);
    private ItemStack lastCollarStack = ItemStack.EMPTY;

    // Synced data slots (server → client)
    private final DataSlot tierSlot     = DataSlot.standalone();
    private final DataSlot curFireSlot  = DataSlot.standalone();
    private final DataSlot curWaterSlot = DataSlot.standalone();
    private final DataSlot addFireSlot  = DataSlot.standalone();
    private final DataSlot addWaterSlot = DataSlot.standalone();
    private final DataSlot maxFireSlot  = DataSlot.standalone();
    private final DataSlot maxWaterSlot = DataSlot.standalone();
    private final DataSlot costSlot     = DataSlot.standalone();

    public CollarBenchMenu(MenuType<?> type, int containerId, Inventory playerInventory, Container beContainer) {
        super(type, containerId);
        this.player = playerInventory.player;
        this.beContainer = beContainer;

        this.trinketContainer = new SimpleContainer(TRINKET_COUNT) {
            @Override
            public void setChanged() {
                super.setChanged();
                CollarBenchMenu.this.slotsChanged(this);
            }
        };

        initFromCollar(beContainer.getItem(COLLAR_INPUT_SLOT));

        // Slot 0: collar_input (pos 16,56) — locked while trinkets are dirty
        this.addSlot(new Slot(beContainer, COLLAR_INPUT_SLOT, 16, 56) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof CollarItem;
            }
            @Override
            public boolean mayPickup(Player player) {
                return !isTrinketsDirty();
            }
            @Override
            public net.minecraft.resources.Identifier getNoItemIcon() {
                return net.minecraft.resources.Identifier.withDefaultNamespace("container/slot/collar");
            }
            @Override
            public void setChanged() {
                super.setChanged();
                CollarBenchMenu.this.slotsChanged(beContainer);
            }
        });

        // Slot 1: fuel_input (pos 16,8)
        this.addSlot(new Slot(beContainer, FUEL_INPUT_SLOT, 16, 8) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.BLAZE_POWDER) || stack.is(Items.BLAZE_ROD)
                    || stack.is(Items.SPONGE) || stack.is(Items.WET_SPONGE);
            }
            @Override
            public net.minecraft.resources.Identifier getNoItemIcon() {
                return net.minecraft.resources.Identifier.withDefaultNamespace("container/slot/fire_water");
            }
            @Override
            public void setChanged() {
                super.setChanged();
                CollarBenchMenu.this.slotsChanged(beContainer);
            }
        });

        // Slot 2: collar_output (pos 144,33)
        this.addSlot(new Slot(resultContainer, 0, 144, 33) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }

            @Override
            public void onTake(Player taker, ItemStack stack) {
                // Read cost/fuel additions before clearing (slotsChanged resets slots to 0)
                int cost = costSlot.get();
                int actualFire  = addFireSlot.get();
                int actualWater = addWaterSlot.get();
                // Return excess fuel items to player
                ItemStack fuelStack = beContainer.getItem(FUEL_INPUT_SLOT);
                if (!fuelStack.isEmpty()) {
                    int total = fuelStack.getCount();
                    int consumed = 0;
                    if (fuelStack.is(Items.BLAZE_POWDER)) {
                        consumed = (int) Math.ceil((double) actualFire / 30);
                    } else if (fuelStack.is(Items.BLAZE_ROD)) {
                        consumed = (int) Math.ceil((double) actualFire / 60);
                    } else if (fuelStack.is(Items.SPONGE) || fuelStack.is(Items.WET_SPONGE)) {
                        consumed = (int) Math.ceil((double) actualWater / 1000);
                    }
                    consumed = Math.min(consumed, total);
                    int remaining = total - consumed;
                    if (remaining > 0) {
                        ItemStack returnStack = fuelStack.copy();
                        returnStack.setCount(remaining);
                        taker.getInventory().placeItemBackInInventory(returnStack);
                    }
                }
                // Consume inputs
                beContainer.setItem(COLLAR_INPUT_SLOT, ItemStack.EMPTY);
                beContainer.setItem(FUEL_INPUT_SLOT, ItemStack.EMPTY);
                CollarBenchMenu.this.slotsChanged(beContainer);
                // Deduct XP
                if (cost > 0 && !taker.hasInfiniteMaterials()) {
                    taker.giveExperienceLevels(-cost);
                }
                // Play sound via direct holder (same approach as crafting sound)
                if (taker.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    var soundId = net.minecraft.resources.Identifier.fromNamespaceAndPath(
                        net.noahsarch.deeperdark.Deeperdark.MOD_ID, "block.collarbench.use");
                    var soundHolder = net.minecraft.core.Holder.direct(
                        net.minecraft.sounds.SoundEvent.createVariableRangeEvent(soundId));
                    double x = taker.getX(), y = taker.getY(), z = taker.getZ();
                    float vol = 1.0f;
                    long seed = serverLevel.getRandom().nextLong();
                    for (net.minecraft.server.level.ServerPlayer sp : serverLevel.players()) {
                        if (sp.distanceToSqr(x, y, z) < 64 * 64) {
                            sp.connection.send(new net.minecraft.network.protocol.game.ClientboundSoundPacket(
                                soundHolder, net.minecraft.sounds.SoundSource.BLOCKS,
                                x, y, z, vol, 1.0f, seed));
                        }
                    }
                }
                // Reset
                for (int i = 0; i < TRINKET_COUNT; i++) {
                    trinketContainer.setItem(i, ItemStack.EMPTY);
                }
                originalTrinketItems.clear();
                lastCollarStack = ItemStack.EMPTY;
                updateOutput();
            }
        });

        // Slots 3-7: trinket slots (all 5, positions from Netherite layout)
        for (int i = 0; i < TRINKET_COUNT; i++) {
            final int idx = i;
            int x = CollarTier.SLOT_X[i];
            int y = CollarTier.SLOT_Y[i];
            this.addSlot(new Slot(trinketContainer, i, x, y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    if (stack.getItem() instanceof CollarItem) return false;
                    CollarTier tier = getActiveTier();
                    return tier != null && tier.isSlotActive(idx);
                }
                @Override
                public boolean isActive() {
                    int t = tierSlot.get();
                    if (t < 0 || t >= CollarTier.values().length) return false;
                    return CollarTier.values()[t].isSlotActive(idx);
                }
                @Override
                public int getMaxStackSize() { return 1; }
                @Override
                public int getMaxStackSize(ItemStack stack) { return 1; }
            });
        }

        // Player inventory (slots 8-43): 3 rows + hotbar at y=84 and y=142
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        addDataSlot(tierSlot);
        addDataSlot(curFireSlot);
        addDataSlot(curWaterSlot);
        addDataSlot(addFireSlot);
        addDataSlot(addWaterSlot);
        addDataSlot(maxFireSlot);
        addDataSlot(maxWaterSlot);
        addDataSlot(costSlot);

        updateOutput();
    }

    private void initFromCollar(ItemStack collarStack) {
        originalTrinketItems.clear();
        originalTrinketSlots = NonNullList.withSize(TRINKET_COUNT, ItemStack.EMPTY);
        if (!collarStack.isEmpty() && collarStack.getItem() instanceof CollarItem) {
            ItemContainerContents contents = collarStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            NonNullList<ItemStack> list = NonNullList.withSize(TRINKET_COUNT, ItemStack.EMPTY);
            contents.copyInto(list);
            for (int i = 0; i < TRINKET_COUNT; i++) {
                trinketContainer.setItem(i, list.get(i));
                originalTrinketSlots.set(i, list.get(i).copy());
                if (!list.get(i).isEmpty()) {
                    originalTrinketItems.add(list.get(i).getItem());
                }
            }
        } else {
            for (int i = 0; i < TRINKET_COUNT; i++) {
                trinketContainer.setItem(i, ItemStack.EMPTY);
            }
        }
        lastCollarStack = collarStack.copy();
    }

    private boolean isTrinketsDirty() {
        for (int i = 0; i < TRINKET_COUNT; i++) {
            if (!ItemStack.isSameItemSameComponents(originalTrinketSlots.get(i), trinketContainer.getItem(i))) {
                return true;
            }
        }
        return false;
    }

    private CollarTier getActiveTier() {
        ItemStack collarIn = beContainer.getItem(COLLAR_INPUT_SLOT);
        if (collarIn.isEmpty() || !(collarIn.getItem() instanceof CollarItem ci)) return null;
        return ci.getTier();
    }

    private void updateOutput() {
        ItemStack collarIn = beContainer.getItem(COLLAR_INPUT_SLOT);
        ItemStack fuelIn   = beContainer.getItem(FUEL_INPUT_SLOT);

        if (collarIn.isEmpty() || !(collarIn.getItem() instanceof CollarItem ci)) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            tierSlot.set(-1);
            curFireSlot.set(0); curWaterSlot.set(0);
            addFireSlot.set(0); addWaterSlot.set(0);
            maxFireSlot.set(0); maxWaterSlot.set(0);
            costSlot.set(0);
            broadcastChanges();
            return;
        }

        CollarTier tier = ci.getTier();
        CollarFuelData fuel = collarIn.getOrDefault(ModComponents.COLLAR_FUEL, CollarFuelData.EMPTY);
        int curFire  = Math.min(fuel.fireTicks(),  tier.fireMax);
        int curWater = Math.min(fuel.waterTicks(), tier.waterMax);

        // Calculate fuel additions from fuel_input
        int addFire = 0, addWater = 0;
        if (!fuelIn.isEmpty()) {
            int count = fuelIn.getCount();
            if (fuelIn.is(Items.BLAZE_POWDER)) {
                addFire = count * 30;
            } else if (fuelIn.is(Items.BLAZE_ROD)) {
                addFire = count * 60;
            } else if (fuelIn.is(Items.SPONGE) || fuelIn.is(Items.WET_SPONGE)) {
                addWater = count * 1000;
            }
        }

        // Cap additions
        int newFire  = Math.min(curFire  + addFire,  tier.fireMax);
        int newWater = Math.min(curWater + addWater, tier.waterMax);
        int actualAddFire  = newFire  - curFire;
        int actualAddWater = newWater - curWater;

        // Calculate costs
        int trinketCost = calcTrinketCost(tier);
        int fuelCost    = calcFuelCost(actualAddFire, actualAddWater, tier);
        int totalCost   = trinketCost + fuelCost;

        tierSlot.set(tier.ordinal());
        curFireSlot.set(curFire);
        curWaterSlot.set(curWater);
        addFireSlot.set(actualAddFire);
        addWaterSlot.set(actualAddWater);
        maxFireSlot.set(tier.fireMax);
        maxWaterSlot.set(tier.waterMax);
        costSlot.set(totalCost);

        // Build result collar only if player can afford it (or is in creative)
        boolean canAfford = player.hasInfiniteMaterials() || totalCost == 0 || player.experienceLevel >= totalCost;
        if (!canAfford) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            broadcastChanges();
            return;
        }

        ItemStack result = collarIn.copy();

        // Write trinkets into result
        NonNullList<ItemStack> trinkets = NonNullList.withSize(TRINKET_COUNT, ItemStack.EMPTY);
        for (int i = 0; i < TRINKET_COUNT; i++) {
            trinkets.set(i, trinketContainer.getItem(i).copy());
        }
        result.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(trinkets));

        // Apply fuel
        result.set(ModComponents.COLLAR_FUEL, new CollarFuelData(newFire, newWater));

        // Update CustomModelData flags to match new trinket state
        updateModelFlags(result, trinkets);

        resultContainer.setItem(0, result);
        broadcastChanges();
    }

    private int calcTrinketCost(CollarTier tier) {
        List<Item> remaining = new ArrayList<>(originalTrinketItems);
        int cost = 0;
        for (int idx : tier.activeSlotIndices) {
            ItemStack s = trinketContainer.getItem(idx);
            if (s.isEmpty()) continue;
            if (!remaining.remove(s.getItem())) {
                cost += 5;
            }
        }
        return cost;
    }

    private int calcFuelCost(int addFire, int addWater, CollarTier tier) {
        if (addFire == 0 && addWater == 0) return 0;
        double totalMax    = tier.fireMax + tier.waterMax;
        double totalAdded  = addFire + addWater;
        double pct = totalAdded / totalMax * 100.0;
        if (pct < 20) return 1;
        if (pct <= 50) return 2;
        return 3;
    }

    private void updateModelFlags(ItemStack result, NonNullList<ItemStack> trinkets) {
        boolean hasSponge = false, hasGold = false, hasBell = false;
        boolean hasMagnet = false, hasBlazeRod = false, hasGlowBerries = false;
        for (ItemStack s : trinkets) {
            if (s.isEmpty()) continue;
            if (s.is(Items.SPONGE) || s.is(Items.WET_SPONGE)) hasSponge = true;
            if (s.is(Items.GOLD_INGOT)) hasGold = true;
            if (s.is(Items.BELL)) hasBell = true;
            if (s.getItem() instanceof ItemMagnetItem) hasMagnet = true;
            if (s.is(Items.BLAZE_ROD)) hasBlazeRod = true;
            if (s.is(Items.GLOW_BERRIES)) hasGlowBerries = true;
        }
        List<Boolean> flags = List.of(hasSponge, hasGold, hasBell, hasMagnet, hasBlazeRod, hasGlowBerries);
        CustomModelData existing = result.getOrDefault(DataComponents.CUSTOM_MODEL_DATA, CustomModelData.EMPTY);
        result.set(DataComponents.CUSTOM_MODEL_DATA,
            new CustomModelData(existing.floats(), flags, existing.strings(), existing.colors()));
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (container == beContainer) {
            ItemStack collarNow = beContainer.getItem(COLLAR_INPUT_SLOT);
            if (!ItemStack.isSameItemSameComponents(collarNow, lastCollarStack)) {
                initFromCollar(collarNow);
            }
        }
        updateOutput();
    }

    @Override
    public boolean stillValid(Player player) { return true; }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        result = stack.copy();

        if (slotIndex == OUTPUT_SLOT) {
            // Output → player inventory
            if (!this.moveItemStackTo(stack, PLAYER_INV_START, PLAYER_INV_START + PLAYER_INV_SIZE, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, result);
            slot.onTake(player, result);
        } else if (slotIndex >= PLAYER_INV_START) {
            // Player inventory → try collar_input, then fuel_input, then trinket slots
            if (stack.getItem() instanceof CollarItem) {
                if (!this.moveItemStackTo(stack, COLLAR_INPUT_SLOT, COLLAR_INPUT_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.is(Items.BLAZE_POWDER) || stack.is(Items.BLAZE_ROD)
                    || stack.is(Items.SPONGE) || stack.is(Items.WET_SPONGE)) {
                if (!this.moveItemStackTo(stack, FUEL_INPUT_SLOT, FUEL_INPUT_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!(stack.getItem() instanceof CollarItem)) {
                if (!this.moveItemStackTo(stack, TRINKET_START, TRINKET_START + TRINKET_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
        } else {
            // collar_input, fuel_input, or trinket → player inventory
            if (!this.moveItemStackTo(stack, PLAYER_INV_START, PLAYER_INV_START + PLAYER_INV_SIZE, true)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return result;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        resultContainer.setItem(0, ItemStack.EMPTY);

        if (isTrinketsDirty()) {
            // True rollback: return the original collar unchanged.
            // Items the player removed are already in their inventory.
            // Items the player added are still in trinketContainer — return those too.
            if (!lastCollarStack.isEmpty()) {
                player.getInventory().placeItemBackInInventory(lastCollarStack.copy());
                beContainer.setItem(COLLAR_INPUT_SLOT, ItemStack.EMPTY);
            }
            // Build pool of original trinket items to detect newly-added ones
            List<ItemStack> originalPool = new ArrayList<>();
            for (int i = 0; i < TRINKET_COUNT; i++) {
                if (!originalTrinketSlots.get(i).isEmpty()) {
                    originalPool.add(originalTrinketSlots.get(i).copy());
                }
            }
            for (int i = 0; i < TRINKET_COUNT; i++) {
                ItemStack current = trinketContainer.getItem(i);
                if (!current.isEmpty()) {
                    boolean fromOriginal = false;
                    for (int j = 0; j < originalPool.size(); j++) {
                        if (ItemStack.isSameItemSameComponents(originalPool.get(j), current)) {
                            originalPool.remove(j);
                            fromOriginal = true;
                            break;
                        }
                    }
                    if (!fromOriginal) {
                        player.getInventory().placeItemBackInInventory(current.copy());
                    }
                }
                trinketContainer.setItem(i, ItemStack.EMPTY);
            }
            // Return fuel (never consumed on cancel)
            ItemStack fuel = beContainer.getItem(FUEL_INPUT_SLOT);
            if (!fuel.isEmpty()) {
                player.getInventory().placeItemBackInInventory(fuel);
                beContainer.setItem(FUEL_INPUT_SLOT, ItemStack.EMPTY);
            }
        } else {
            // Normal close: return collar input and fuel input
            for (int i = 0; i < beContainer.getContainerSize(); i++) {
                ItemStack s = beContainer.getItem(i);
                if (!s.isEmpty()) {
                    player.getInventory().placeItemBackInInventory(s);
                    beContainer.setItem(i, ItemStack.EMPTY);
                }
            }
            // Trinket container is a mirror of the collar's embedded data — just clear it,
            // don't return items separately (they'd dupe what's already inside the returned collar)
            for (int i = 0; i < TRINKET_COUNT; i++) {
                trinketContainer.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    // --- Accessors for screen ---

    public int getTier()     { return tierSlot.get(); }
    public int getCurFire()  { return curFireSlot.get(); }
    public int getCurWater() { return curWaterSlot.get(); }
    public int getAddFire()  { return addFireSlot.get(); }
    public int getAddWater() { return addWaterSlot.get(); }
    public int getMaxFire()  { return maxFireSlot.get(); }
    public int getMaxWater() { return maxWaterSlot.get(); }
    public int getCost()     { return costSlot.get(); }
}
