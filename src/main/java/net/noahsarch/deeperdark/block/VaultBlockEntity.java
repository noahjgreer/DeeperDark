package net.noahsarch.deeperdark.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.noahsarch.deeperdark.menu.ModMenus;
import net.noahsarch.deeperdark.menu.VaultMenu;

import java.util.ArrayList;
import java.util.List;

public class VaultBlockEntity extends BlockEntity implements MenuProvider, Container {

    public static class VaultEntry {
        public ItemStack representative;
        public int count;

        public VaultEntry(ItemStack representative, int count) {
            this.representative = representative.copyWithCount(1);
            this.count = count;
        }
    }

    private static final Codec<VaultEntry> ENTRY_CODEC = RecordCodecBuilder.create(inst -> inst.group(
        ItemStack.CODEC.fieldOf("item").forGetter(e -> e.representative),
        Codec.INT.fieldOf("count").forGetter(e -> e.count)
    ).apply(inst, VaultEntry::new));

    private final VaultTier tier;
    private final List<VaultEntry> entries = new ArrayList<>();
    private int openCount;

    public VaultBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VAULT, pos, state);
        this.tier = VaultTier.fromState(state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("vault_entries", ENTRY_CODEC.listOf(), entries);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        entries.clear();
        input.read("vault_entries", ENTRY_CODEC.listOf()).ifPresent(list -> {
            int max = tier.maxTypes;
            for (int i = 0; i < list.size() && i < max; i++) {
                entries.add(list.get(i));
            }
        });
    }

    public int getMaxTypes() { return tier.maxTypes; }
    public int getEntryCount() { return entries.size(); }

    public VaultEntry getEntry(int index) {
        return (index >= 0 && index < entries.size()) ? entries.get(index) : null;
    }

    public int getCount(int index) {
        VaultEntry e = getEntry(index);
        return e != null ? e.count : 0;
    }

    public ItemStack getDisplayStack(int index) {
        VaultEntry e = getEntry(index);
        if (e == null || e.count <= 0) return ItemStack.EMPTY;
        return e.representative.copyWithCount(1); // count shown in stats panel; suppress stack overlay
    }

    public boolean canAccept(ItemStack stack) {
        if (stack.isEmpty()) return false;
        // Prevent vault items and shulker boxes from being stored inside vaults.
        if (stack.getItem() instanceof net.minecraft.world.item.BlockItem bi
                && bi.getBlock() instanceof VaultBlock) return false;
        if (stack.is(net.minecraft.tags.ItemTags.SHULKER_BOXES)) return false;
        for (VaultEntry entry : entries) {
            if (ItemStack.isSameItemSameComponents(entry.representative, stack)) return true;
        }
        return entries.size() < tier.maxTypes;
    }

    // Absorbs as many items from the stack as possible. Returns the leftover.
    public ItemStack addItems(ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        for (VaultEntry entry : entries) {
            if (ItemStack.isSameItemSameComponents(entry.representative, stack)) {
                long space = (long) Integer.MAX_VALUE - entry.count;
                if (space <= 0) return stack;
                int add = (int) Math.min(stack.getCount(), space);
                entry.count += add;
                playSound(SoundEvents.ITEM_PICKUP, 1.2F);
                if (add >= stack.getCount()) return ItemStack.EMPTY;
                return stack.copyWithCount(stack.getCount() - add);
            }
        }
        if (entries.size() < tier.maxTypes) {
            entries.add(new VaultEntry(stack.copyWithCount(1), stack.getCount()));
            playSound(SoundEvents.ITEM_PICKUP, 1.2F);
            return ItemStack.EMPTY;
        }
        return stack;
    }

    public ItemStack withdraw(int index, int amount) {
        if (index < 0 || index >= entries.size()) return ItemStack.EMPTY;
        VaultEntry entry = entries.get(index);
        int taken = Math.min(amount, entry.count);
        if (taken <= 0) return ItemStack.EMPTY;
        ItemStack result = entry.representative.copyWithCount(taken);
        entry.count -= taken;
        if (entry.count <= 0) {
            entries.remove(index);
        }
        playSound(SoundEvents.ITEM_PICKUP, 0.8F);
        setChanged();
        return result;
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        List<ItemStack> stacks = new ArrayList<>();
        for (VaultEntry entry : entries) {
            // Split into maxStackSize batches so ItemStack.validateStrict passes
            int batchSize = entry.representative.getMaxStackSize();
            int remaining = entry.count;
            while (remaining > 0) {
                int batch = Math.min(remaining, batchSize);
                stacks.add(entry.representative.copyWithCount(batch));
                remaining -= batch;
            }
        }
        if (!stacks.isEmpty()) {
            builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(stacks));
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter getter) {
        super.applyImplicitComponents(getter);
        ItemContainerContents contents = getter.get(DataComponents.CONTAINER);
        if (contents == null) return;
        entries.clear();
        // Merge consecutive same-item stacks back into single entries
        for (net.minecraft.world.item.ItemStackTemplate template : contents.nonEmptyItems()) {
            ItemStack stack = template.create();
            if (stack.isEmpty()) continue;
            if (!entries.isEmpty()) {
                VaultEntry last = entries.get(entries.size() - 1);
                if (ItemStack.isSameItemSameComponents(last.representative, stack)) {
                    last.count += stack.getCount();
                    continue;
                }
            }
            if (entries.size() < tier.maxTypes) {
                entries.add(new VaultEntry(stack.copyWithCount(1), stack.getCount()));
            }
        }
    }

    public boolean stillValid(Player player) {
        if (level == null) return false;
        if (level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(
            worldPosition.getX() + 0.5,
            worldPosition.getY() + 0.5,
            worldPosition.getZ() + 0.5
        ) <= 64.0;
    }

    public void startOpen(Player player) {
        if (!remove && !player.isSpectator()) {
            openCount++;
            if (openCount == 1) {
                level.gameEvent(player, GameEvent.CONTAINER_OPEN, worldPosition);
                playSound(SoundEvents.ENDER_CHEST_OPEN, 0.0F);
            }
        }
    }

    public void stopOpen(Player player) {
        if (!remove && !player.isSpectator()) {
            openCount = Math.max(0, openCount - 1);
            if (openCount == 0) {
                level.gameEvent(player, GameEvent.CONTAINER_CLOSE, worldPosition);
                playSound(SoundEvents.ENDER_CHEST_CLOSE, 0.0F);
            }
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private void playSound(SoundEvent event, float pitch) {
        if (level == null) return;
        level.playSound(null, worldPosition, event, SoundSource.BLOCKS, 0.5F, pitch);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(tier.titleKey);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new VaultMenu(tier.getMenuType(), containerId, inventory, this);
    }

    // ---- Container (hopper support: deposit-only) ----

    @Override public int getContainerSize() { return 1; }
    @Override public boolean isEmpty() { return entries.isEmpty(); }

    /** Always empty so hoppers see a free slot and will try to insert. */
    @Override public ItemStack getItem(int slot) { return ItemStack.EMPTY; }

    /** Hoppers don't extract from vaults. */
    @Override public ItemStack removeItem(int slot, int amount) { return ItemStack.EMPTY; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ItemStack.EMPTY; }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (!stack.isEmpty()) {
            addItems(stack);
            setChanged();
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) { return canAccept(stack); }

    @Override public void clearContent() { entries.clear(); setChanged(); }

    // ---- End Container ----

    enum VaultTier {
        SMALL(1, "container.deeperdark.small_item_vault"),
        MEDIUM(3, "container.deeperdark.medium_item_vault"),
        LARGE(9, "container.deeperdark.large_item_vault");

        final int maxTypes;
        final String titleKey;

        VaultTier(int maxTypes, String titleKey) {
            this.maxTypes = maxTypes;
            this.titleKey = titleKey;
        }

        MenuType<?> getMenuType() {
            return switch (this) {
                case SMALL -> ModMenus.SMALL_VAULT;
                case MEDIUM -> ModMenus.MEDIUM_VAULT;
                case LARGE -> ModMenus.LARGE_VAULT;
            };
        }

        static VaultTier fromState(BlockState state) {
            if (state.is(ModBlocks.SMALL_ITEM_VAULT)) return SMALL;
            if (state.is(ModBlocks.MEDIUM_ITEM_VAULT)) return MEDIUM;
            return LARGE;
        }
    }
}
