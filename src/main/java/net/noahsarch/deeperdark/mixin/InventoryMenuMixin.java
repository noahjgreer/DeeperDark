package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.noahsarch.deeperdark.duck.CollarHolder;
import net.noahsarch.deeperdark.duck.CraftingPanelHolder;
import net.noahsarch.deeperdark.item.CollarItem;
import net.noahsarch.deeperdark.menu.CollarSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin extends AbstractContainerMenu implements CraftingPanelHolder {

    // ── Collar slot ───────────────────────────────────────────────────────────

    protected InventoryMenuMixin() { super(null, 0); }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void deeperdark$addCollarSlot(Inventory inventory, boolean onServer, Player player, CallbackInfo ci) {
        if (player instanceof CollarHolder holder) {
            this.addSlot(new CollarSlot(holder, 77, 8));
        }
    }

    // ── Embedded 3×3 crafting panel ──────────────────────────────────────────

    // Panel slot positions within the panel texture (user-reported, +1 corrected):
    //   Grid  row/col: (8,8),(26,8),(44,8),(8,26),(26,26),(44,26),(8,44),(26,44),(44,44)
    //   Output: (26, 102)
    // Menu slot x = PANEL_X + panelX, slot y = panelY.
    private static final int PANEL_X = 176;
    private static final int[][] GRID_POS = {
        {8,8},{26,8},{44,8},{8,26},{26,26},{44,26},{8,44},{26,44},{44,44}
    };
    private static final int OUT_X = 26, OUT_Y = 102;

    @Unique private CraftingContainer deeperdark$craftGrid;
    @Unique private ResultContainer   deeperdark$craftResult;
    @Unique private Player            deeperdark$craftPlayer;
    @Unique private boolean           deeperdark$panelOpen       = false;
    @Unique private boolean           deeperdark$forceVanillaSlots = false;
    @Unique private int               deeperdark$resultIdx       = -1;
    @Unique private int               deeperdark$gridStart       = -1;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void deeperdark$addCraftingPanelSlots(Inventory inventory, boolean onServer, Player player, CallbackInfo ci) {
        this.deeperdark$craftPlayer = player;
        this.deeperdark$craftGrid   = new TransientCraftingContainer((AbstractContainerMenu)(Object)this, 3, 3);
        this.deeperdark$craftResult = new ResultContainer();

        this.deeperdark$resultIdx = this.slots.size();
        this.addSlot(new ResultSlot(player, this.deeperdark$craftGrid, this.deeperdark$craftResult,
                                    0, PANEL_X + OUT_X, OUT_Y) {
            @Override public boolean isActive() { return deeperdark$panelOpen; }
        });

        this.deeperdark$gridStart = this.slots.size();
        for (int i = 0; i < 9; i++) {
            final int idx = i;
            this.addSlot(new Slot(this.deeperdark$craftGrid, idx,
                                  PANEL_X + GRID_POS[i][0], GRID_POS[i][1]) {
                @Override public boolean isActive() { return deeperdark$panelOpen; }
            });
        }
    }

    // ── Crafting result computation ──────────────────────────────────────────

    @Inject(method = "slotsChanged", at = @At("TAIL"))
    private void deeperdark$handleGridChange(Container container, CallbackInfo ci) {
        if (container != this.deeperdark$craftGrid) return;
        if (!(this.deeperdark$craftPlayer.level() instanceof ServerLevel level)) return;
        computeCraftingResult(level);
    }

    @Unique
    private void computeCraftingResult(ServerLevel level) {
        CraftingInput input = this.deeperdark$craftGrid.asCraftInput();
        ServerPlayer serverPlayer = (ServerPlayer) this.deeperdark$craftPlayer;
        ItemStack result = ItemStack.EMPTY;

        Optional<RecipeHolder<CraftingRecipe>> maybeRecipe =
            level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level,
                (RecipeHolder<CraftingRecipe>) null);

        if (maybeRecipe.isPresent()) {
            RecipeHolder<CraftingRecipe> holder = maybeRecipe.get();
            if (this.deeperdark$craftResult.setRecipeUsed(serverPlayer, holder)) {
                ItemStack assembled = holder.value().assemble(input);
                if (assembled.isItemEnabled(level.enabledFeatures())) {
                    result = assembled;
                }
            }
        }

        this.deeperdark$craftResult.setItem(0, result);
        this.setRemoteSlot(this.deeperdark$resultIdx, result);
        serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(
            this.containerId, this.incrementStateId(), this.deeperdark$resultIdx, result));
    }

    // ── CraftingPanelHolder duck interface ───────────────────────────────────

    @Override public boolean deeperdark$isPanelOpen()           { return this.deeperdark$panelOpen; }
    @Override public void    deeperdark$setPanelOpen(boolean v) { this.deeperdark$panelOpen = v; }

    @Override public boolean deeperdark$isUseVanillaSlots()          { return this.deeperdark$forceVanillaSlots; }
    @Override public void    deeperdark$setUseVanillaSlots(boolean v) { this.deeperdark$forceVanillaSlots = v; }

    @Override public CraftingContainer deeperdark$getExtraCraftSlots()  { return this.deeperdark$craftGrid; }
    @Override public ResultContainer   deeperdark$getExtraResultSlots() { return this.deeperdark$craftResult; }

    // ── Recipe-book slot routing ──────────────────────────────────────────────
    // When panel is open AND deeperdark$forceVanillaSlots is false, redirect to
    // the 3×3 panel slots. When forceVanillaSlots is true (2×2 recipe selected),
    // fall through to vanilla which returns the 2×2 inventory slots.

    @Inject(method = "getInputGridSlots", at = @At("HEAD"), cancellable = true)
    private void deeperdark$overrideInputSlots(CallbackInfoReturnable<List<Slot>> cir) {
        if (this.deeperdark$panelOpen && !this.deeperdark$forceVanillaSlots && this.deeperdark$gridStart >= 0) {
            cir.setReturnValue(this.slots.subList(this.deeperdark$gridStart, this.deeperdark$gridStart + 9));
        }
    }

    @Inject(method = "getResultSlot", at = @At("HEAD"), cancellable = true)
    private void deeperdark$overrideResultSlot(CallbackInfoReturnable<Slot> cir) {
        if (this.deeperdark$panelOpen && !this.deeperdark$forceVanillaSlots && this.deeperdark$resultIdx >= 0) {
            cir.setReturnValue(this.slots.get(this.deeperdark$resultIdx));
        }
    }

    // ── Cleanup ──────────────────────────────────────────────────────────────

    @Inject(method = "removed", at = @At("TAIL"))
    private void deeperdark$clearCraftingGrid(Player player, CallbackInfo ci) {
        if (this.deeperdark$craftGrid != null) {
            // clearContainer uses removeItemNoUpdate which skips slotsChanged,
            // so computeCraftingResult is never triggered automatically.
            this.clearContainer(player, this.deeperdark$craftGrid);
        }
        // Explicitly clear the result so re-opening the inventory doesn't show
        // a stale craftable result with empty input slots (infinite-craft exploit).
        if (this.deeperdark$craftResult != null) {
            this.deeperdark$craftResult.setItem(0, ItemStack.EMPTY);
        }
    }

    // ── Quick-move (shift-click) ──────────────────────────────────────────────

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void deeperdark$collarQuickMove(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir) {
        // ── Collar slot ──────────────────────────────────────────────────────
        CollarSlot collarSlot = null;
        int collarSlotIndex = -1;
        for (int i = 0; i < this.slots.size(); i++) {
            if (this.slots.get(i) instanceof CollarSlot cs) {
                collarSlot = cs;
                collarSlotIndex = i;
                break;
            }
        }

        if (collarSlot != null) {
            Slot slot = this.slots.get(slotIndex);
            if (!slot.hasItem()) return;

            if (slotIndex == collarSlotIndex) {
                ItemStack stack = slot.getItem();
                ItemStack result = stack.copy();
                if (!this.moveItemStackTo(stack, 9, collarSlotIndex, false)) {
                    cir.setReturnValue(ItemStack.EMPTY);
                    return;
                }
                if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
                cir.setReturnValue(result);
                return;
            }

            if (slotIndex >= 9 && slotIndex <= 44 && slot.getItem().is(Items.SADDLE)) {
                Slot headSlot = this.slots.get(5);
                if (!headSlot.hasItem()) {
                    ItemStack stack = slot.getItem();
                    ItemStack result = stack.copy();
                    headSlot.setByPlayer(stack.copy());
                    slot.setByPlayer(ItemStack.EMPTY);
                    cir.setReturnValue(result);
                    return;
                }
            }

            if (slot.getItem().getItem() instanceof CollarItem && !collarSlot.hasItem()) {
                ItemStack stack = slot.getItem();
                ItemStack result = stack.copy();
                collarSlot.setByPlayer(stack.copy());
                slot.setByPlayer(ItemStack.EMPTY);
                // ResultSlot.onTake() consumes crafting ingredients; skipping it
                // leaves the grid full while the collar equips → duplication.
                if (slot instanceof ResultSlot) {
                    slot.onTake(player, result);
                }
                cir.setReturnValue(result);
                return;
            }
        }

        // ── Panel result slot ─────────────────────────────────────────────────
        if (this.deeperdark$resultIdx >= 0 && slotIndex == this.deeperdark$resultIdx) {
            Slot slot = this.slots.get(slotIndex);
            if (!slot.hasItem()) { cir.setReturnValue(ItemStack.EMPTY); return; }
            ItemStack stack  = slot.getItem();
            ItemStack result = stack.copy();
            stack.getItem().onCraftedBy(stack, player);
            if (!this.moveItemStackTo(stack, 9, 45, true)) { cir.setReturnValue(ItemStack.EMPTY); return; }
            slot.onQuickCraft(stack, result);
            if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
            // Only proceed if items were actually moved (matches vanilla CraftingMenu pattern)
            if (stack.getCount() == result.getCount()) { cir.setReturnValue(ItemStack.EMPTY); return; }
            slot.onTake(player, stack);
            player.drop(stack, false);
            cir.setReturnValue(result);
            return;
        }

        // ── Panel grid slots ──────────────────────────────────────────────────
        if (this.deeperdark$gridStart >= 0
                && slotIndex >= this.deeperdark$gridStart
                && slotIndex < this.deeperdark$gridStart + 9) {
            Slot slot = this.slots.get(slotIndex);
            if (!slot.hasItem()) { cir.setReturnValue(ItemStack.EMPTY); return; }
            ItemStack stack  = slot.getItem();
            ItemStack result = stack.copy();
            if (!this.moveItemStackTo(stack, 9, 45, false)) { cir.setReturnValue(ItemStack.EMPTY); return; }
            if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
            if (stack.getCount() != result.getCount()) slot.onTake(player, stack);
            cir.setReturnValue(result);
        }
    }
}
