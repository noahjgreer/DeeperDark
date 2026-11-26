package net.noahsarch.deeperdark.block;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Hopper and pipe access. Vanilla hoppers expect {@code Container.getItem} to return a live stack,
 * which a crate entry isn't, so the crate exposes Fabric's transactional storage instead. Fabric
 * routes hoppers and hopper minecarts to it when a block has no {@code Container}.
 */
final class CrateStorage extends SnapshotParticipant<CrateContents> implements Storage<ItemVariant> {

    private final CrateBlockEntity crate;

    CrateStorage(CrateBlockEntity crate) {
        this.crate = crate;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        ItemStack stack = resource.toStack((int) Math.min(maxAmount, Integer.MAX_VALUE));
        if (!crate.canAccept(stack)) return 0;
        updateSnapshots(transaction);
        return stack.getCount() - crate.insert(stack).getCount();
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        int index = crate.find(resource.toStack());
        if (index < 0) return 0;
        updateSnapshots(transaction);
        return crate.withdraw(index, (int) Math.min(maxAmount, Integer.MAX_VALUE)).getCount();
    }

    /** Worst first, so hoppers drain the junk and leave the best items. */
    @Override
    public Iterator<StorageView<ItemVariant>> iterator() {
        List<StorageView<ItemVariant>> views = new ArrayList<>(crate.entries.size());
        for (int i = crate.entries.size() - 1; i >= 0; i--) {
            views.add(new View(ItemVariant.of(crate.entries.get(i).representative)));
        }
        return views.iterator();
    }

    @Override
    protected CrateContents createSnapshot() {
        return crate.contents();
    }

    @Override
    protected void readSnapshot(CrateContents snapshot) {
        crate.setContents(snapshot);
    }

    @Override
    protected void onFinalCommit() {
        crate.setChanged();
    }

    /** Looks its entry up by variant each time, since transactions replace and reorder entries. */
    private final class View implements StorageView<ItemVariant> {
        private final ItemVariant resource;

        View(ItemVariant resource) {
            this.resource = resource;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            return resource.equals(this.resource) ? CrateStorage.this.extract(resource, maxAmount, transaction) : 0;
        }

        @Override public boolean isResourceBlank() { return false; }
        @Override public ItemVariant getResource() { return resource; }

        @Override
        public long getAmount() {
            int index = crate.find(resource.toStack());
            return index < 0 ? 0 : crate.entries.get(index).count;
        }

        @Override public long getCapacity() { return Integer.MAX_VALUE; }
    }
}
