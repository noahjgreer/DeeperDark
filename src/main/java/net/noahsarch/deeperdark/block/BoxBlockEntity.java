package net.noahsarch.deeperdark.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.noahsarch.deeperdark.menu.BoxMenu;
import net.noahsarch.deeperdark.menu.ModMenus;
import net.noahsarch.deeperdark.sound.ModSounds;

public class BoxBlockEntity extends RandomizableContainerBlockEntity {
    private final BoxTier tier;
    private NonNullList<ItemStack> items;
    private int openCount;

    public BoxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BOX, pos, state);
        this.tier = BoxTier.fromState(state);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(input)) {
            ContainerHelper.loadAllItems(input, this.items);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!this.trySaveLootTable(output)) {
            ContainerHelper.saveAllItems(output, this.items);
        }
    }

    @Override
    public int getContainerSize() {
        return this.tier.getSlotCount();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected Component getDefaultName() {
        return this.tier.getTitle();
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return this.tier.createMenu(containerId, inventory, this);
    }

    @Override
    public void startOpen(ContainerUser containerUser) {
        if (!this.remove && !containerUser.getLivingEntity().isSpectator()) {
            if (this.openCount < 0) {
                this.openCount = 0;
            }

            this.openCount++;
            if (this.openCount == 1) {
                this.level.gameEvent(containerUser.getLivingEntity(), GameEvent.CONTAINER_OPEN, this.worldPosition);
                this.playSound(ModSounds.BOX_OPEN);
            }
        }
    }

    @Override
    public void stopOpen(ContainerUser containerUser) {
        if (!this.remove && !containerUser.getLivingEntity().isSpectator()) {
            this.openCount--;
            if (this.openCount <= 0) {
                this.level.gameEvent(containerUser.getLivingEntity(), GameEvent.CONTAINER_CLOSE, this.worldPosition);
                this.playSound(ModSounds.BOX_CLOSE);
            }
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.items));
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter getter) {
        super.applyImplicitComponents(getter);
        ItemContainerContents contents = getter.get(DataComponents.CONTAINER);
        if (contents != null) {
            this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
            contents.copyInto(this.items);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        // Shulker-style behavior: do not drop contents on block removal.
    }

    private void playSound(SoundEvent event) {
        if (this.level == null) {
            return;
        }

        this.level.playSound(
            null,
            this.worldPosition,
            event,
            SoundSource.BLOCKS,
            0.5F,
            this.level.getRandom().nextFloat() * 0.1F + 0.9F
        );
    }

    private enum BoxTier {
        FLIMSY(3, 1, "container.deeperdark.flimsy_box") {
            @Override
            protected AbstractContainerMenu createMenu(int containerId, Inventory inventory, BoxBlockEntity entity) {
                return new BoxMenu(ModMenus.FLIMSY_BOX, containerId, inventory, entity, getRows());
            }
        },
        STURDY(6, 2, "container.deeperdark.sturdy_box") {
            @Override
            protected AbstractContainerMenu createMenu(int containerId, Inventory inventory, BoxBlockEntity entity) {
                return new BoxMenu(ModMenus.STURDY_BOX, containerId, inventory, entity, getRows());
            }
        },
        REINFORCED(9, 3, "container.deeperdark.reinforced_box") {
            @Override
            protected AbstractContainerMenu createMenu(int containerId, Inventory inventory, BoxBlockEntity entity) {
                return new DispenserMenu(containerId, inventory, entity);
            }
        };

        private final int slotCount;
        private final int rows;
        private final Component title;

        BoxTier(int slotCount, int rows, String titleKey) {
            this.slotCount = slotCount;
            this.rows = rows;
            this.title = Component.translatable(titleKey);
        }

        public int getSlotCount() {
            return this.slotCount;
        }

        public Component getTitle() {
            return this.title;
        }

        public int getRows() {
            return this.rows;
        }

        protected abstract AbstractContainerMenu createMenu(int containerId, Inventory inventory, BoxBlockEntity entity);

        private static BoxTier fromState(BlockState state) {
            if (state.is(ModBlocks.FLIMSY_BOX)) {
                return FLIMSY;
            }
            if (state.is(ModBlocks.STURDY_BOX)) {
                return STURDY;
            }

            return REINFORCED;
        }
    }
}
