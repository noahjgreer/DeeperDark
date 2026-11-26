package net.noahsarch.deeperdark.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.noahsarch.deeperdark.block.VaultBlockEntity.VaultEntry;
import net.noahsarch.deeperdark.component.ModComponents;
import net.noahsarch.deeperdark.inventory.ContainerItemUtil;
import net.noahsarch.deeperdark.menu.CrateMenu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Holds any number of variants of one item. The first item in locks the crate to that item until
 * it is empty again. Identical variants share an entry with a count.
 */
public class CrateBlockEntity extends BlockEntity implements MenuProvider {

    // Distinct variants each carry their own NBT, so this bounds the chunk and the crate item.
    public static final int MAX_ENTRIES = 128;

    // Best first: more enchantment levels, then more durability left.
    private static final Comparator<ItemStack> ORDER = Comparator
        .<ItemStack>comparingInt(s -> -enchantmentLevels(s))
        .thenComparingInt(s -> s.getDamageValue() - s.getMaxDamage());

    // Kept in ORDER, so storage views walking it backwards hand hoppers the worst first.
    final List<VaultEntry> entries = new ArrayList<>();
    final CrateStorage storage = new CrateStorage(this);
    private boolean autoMerge;
    private int openCount;
    // The best entry, as last sent to clients for the labels on the block's faces.
    private ItemStack shown = ItemStack.EMPTY;

    public CrateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRATE, pos, state);
    }

    public int getEntryCount() { return entries.size(); }

    public ItemStack getShown() { return shown; }

    private ItemStack best() {
        return entries.isEmpty() ? ItemStack.EMPTY : entries.getFirst().representative;
    }

    public boolean isAutoMerge() { return autoMerge; }

    /** Turning it on also merges what is already inside. */
    public void setAutoMerge(boolean on) {
        autoMerge = on;
        if (!on) return;
        List<ItemStack> damaged = new ArrayList<>();
        entries.removeIf(e -> {
            if (!isRepairable(e.representative)) return false;
            damaged.add(e.representative.copyWithCount(e.count));
            return true;
        });
        for (ItemStack stack : damaged) insert(stack, true);
    }

    /** The entry as a stack of its full count, or empty. */
    public ItemStack getDisplayStack(int index) {
        if (index < 0 || index >= entries.size()) return ItemStack.EMPTY;
        VaultEntry e = entries.get(index);
        return e.representative.copyWithCount(e.count);
    }

    public boolean canAccept(ItemStack stack) {
        if (stack.isEmpty() || !ContainerItemUtil.canStore(ContainerItemUtil.VAULT_TIER, stack)) return false;
        if (entries.isEmpty()) return true;
        if (!stack.is(entries.getFirst().representative.getItem())) return false;
        return entries.size() < MAX_ENTRIES || find(stack) >= 0
            || (autoMerge && isRepairable(stack) && findRepairPartner() >= 0);
    }

    /** Adds what fits, merging damaged items when auto-merge is on, and returns the rest. Callers call setChanged. */
    public ItemStack insert(ItemStack stack) {
        return insert(stack, autoMerge);
    }

    /** Puts back items that were just taken out, without merging them. */
    public ItemStack putBack(ItemStack stack) {
        return insert(stack, false);
    }

    private ItemStack insert(ItemStack stack, boolean merge) {
        if (!canAccept(stack)) return stack;
        ItemStack rest = stack.copy();
        while (merge && !rest.isEmpty() && isRepairable(rest)) {
            int partner = findRepairPartner();
            ItemStack one = rest.split(1);
            ItemStack partnerItem = partner >= 0 ? withdraw(partner, 1) : ItemStack.EMPTY;
            if (add(partnerItem.isEmpty() ? one : repair(one, partnerItem)) == 0) {
                // The partner's entry held just that item, so its freed room takes it back.
                if (!partnerItem.isEmpty()) add(partnerItem);
                rest.grow(1);
                break;
            }
        }
        if (!rest.isEmpty()) rest.shrink(add(rest));
        return rest;
    }

    /** Callers call setChanged. */
    public ItemStack withdraw(int index, int amount) {
        if (index < 0 || index >= entries.size() || amount <= 0) return ItemStack.EMPTY;
        VaultEntry e = entries.get(index);
        int taken = Math.min(amount, e.count);
        e.count -= taken;
        if (e.count == 0) entries.remove(index);
        return e.representative.copyWithCount(taken);
    }

    int find(ItemStack stack) {
        for (int i = 0; i < entries.size(); i++) {
            if (ItemStack.isSameItemSameComponents(entries.get(i).representative, stack)) return i;
        }
        return -1;
    }

    private int add(ItemStack stack) {
        int i = find(stack);
        if (i >= 0) {
            VaultEntry e = entries.get(i);
            int added = (int) Math.min(stack.getCount(), (long) Integer.MAX_VALUE - e.count);
            e.count += added;
            return added;
        }
        if (entries.size() >= MAX_ENTRIES) return 0;
        int at = 0;
        while (at < entries.size() && ORDER.compare(entries.get(at).representative, stack) <= 0) at++;
        entries.add(at, new VaultEntry(stack, stack.getCount()));
        return stack.getCount();
    }

    /** Auto-merge keeps at most one repairable entry, of count 1: any second one merges into it. */
    private int findRepairPartner() {
        for (int i = 0; i < entries.size(); i++) {
            VaultEntry e = entries.get(i);
            if (e.count == 1 && isRepairable(e.representative)) return i;
        }
        return -1;
    }

    /** Damaged, and otherwise unchanged from the default item: grid repair would lose nothing. */
    static boolean isRepairable(ItemStack stack) {
        if (!stack.isDamaged()) return false;
        ItemStack undamaged = stack.copyWithCount(1);
        undamaged.setDamageValue(0);
        return undamaged.getComponentsPatch().isEmpty();
    }

    /** Vanilla's crafting-grid repair: both durabilities plus 5%. */
    private static ItemStack repair(ItemStack a, ItemStack b) {
        int max = a.getMaxDamage();
        int remaining = (max - a.getDamageValue()) + (max - b.getDamageValue()) + max * 5 / 100;
        ItemStack result = a.copyWithCount(1);
        result.setDamageValue(Math.max(max - remaining, 0));
        return result;
    }

    private static int enchantmentLevels(ItemStack stack) {
        int levels = 0;
        for (var e : EnchantmentHelper.getEnchantmentsForCrafting(stack).entrySet()) levels += e.getIntValue();
        return levels;
    }

    CrateContents contents() {
        List<VaultEntry> copy = new ArrayList<>(entries.size());
        for (VaultEntry e : entries) copy.add(new VaultEntry(e.representative, e.count));
        return new CrateContents(copy, autoMerge);
    }

    void setContents(CrateContents contents) {
        entries.clear();
        for (VaultEntry e : contents.entries()) {
            if (entries.size() < MAX_ENTRIES) entries.add(new VaultEntry(e.representative, e.count));
        }
        entries.sort((a, b) -> ORDER.compare(a.representative, b.representative));
        autoMerge = contents.autoMerge();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("contents", CrateContents.CODEC, contents());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        setContents(input.read("contents", CrateContents.CODEC).orElse(CrateContents.EMPTY));
        // Only the client's update tag carries it; a save from disk recomputes it.
        shown = input.read("shown", ItemStack.OPTIONAL_CODEC).orElseGet(() -> best().copy());
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level == null || level.isClientSide() || ItemStack.matches(best(), shown)) return;
        shown = best().copy();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // Just the label, never the contents. Written even when empty so an emptied crate clears its label.
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.store("shown", ItemStack.OPTIONAL_CODEC, registries.createSerializationContext(NbtOps.INSTANCE), shown);
        return tag;
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        if (!entries.isEmpty() || autoMerge) builder.set(ModComponents.CRATE_CONTENTS, contents());
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter getter) {
        super.applyImplicitComponents(getter);
        CrateContents contents = getter.get(ModComponents.CRATE_CONTENTS);
        setContents(contents != null ? contents : CrateContents.EMPTY);
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        output.discard("contents");
    }

    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    public void startOpen(Player player) {
        if (remove || player.isSpectator() || openCount++ > 0) return;
        level.gameEvent(player, GameEvent.CONTAINER_OPEN, worldPosition);
        playSound(SoundEvents.BARREL_OPEN);
    }

    public void stopOpen(Player player) {
        if (remove || player.isSpectator()) return;
        openCount = Math.max(0, openCount - 1);
        if (openCount > 0) return;
        level.gameEvent(player, GameEvent.CONTAINER_CLOSE, worldPosition);
        playSound(SoundEvents.BARREL_CLOSE);
    }

    private void playSound(SoundEvent sound) {
        level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 0.5F, 1.0F);
    }

    @Override
    public Component getDisplayName() {
        Component customName = components().get(DataComponents.CUSTOM_NAME);
        return customName != null ? customName : Component.translatable("container.deeperdark.crate");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new CrateMenu(containerId, inventory, this);
    }
}
