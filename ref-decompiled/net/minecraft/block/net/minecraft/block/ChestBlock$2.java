/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import java.util.Optional;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

class ChestBlock.2
implements DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<NamedScreenHandlerFactory>> {
    ChestBlock.2() {
    }

    @Override
    public Optional<NamedScreenHandlerFactory> getFromBoth(final ChestBlockEntity chestBlockEntity, final ChestBlockEntity chestBlockEntity2) {
        final DoubleInventory inventory = new DoubleInventory(chestBlockEntity, chestBlockEntity2);
        return Optional.of(new NamedScreenHandlerFactory(){

            @Override
            public @Nullable ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                if (chestBlockEntity.checkUnlocked(playerEntity) && chestBlockEntity2.checkUnlocked(playerEntity)) {
                    chestBlockEntity.generateLoot(playerInventory.player);
                    chestBlockEntity2.generateLoot(playerInventory.player);
                    return GenericContainerScreenHandler.createGeneric9x6(i, playerInventory, inventory);
                }
                Direction direction = ChestBlock.getFacing(chestBlockEntity.getCachedState());
                Vec3d vec3d = chestBlockEntity.getPos().toCenterPos();
                Vec3d vec3d2 = vec3d.add((double)direction.getOffsetX() / 2.0, 0.0, (double)direction.getOffsetZ() / 2.0);
                LockableContainerBlockEntity.handleLocked(vec3d2, playerEntity, this.getDisplayName());
                return null;
            }

            @Override
            public Text getDisplayName() {
                if (chestBlockEntity.hasCustomName()) {
                    return chestBlockEntity.getDisplayName();
                }
                if (chestBlockEntity2.hasCustomName()) {
                    return chestBlockEntity2.getDisplayName();
                }
                return Text.translatable("container.chestDouble");
            }
        });
    }

    @Override
    public Optional<NamedScreenHandlerFactory> getFrom(ChestBlockEntity chestBlockEntity) {
        return Optional.of(chestBlockEntity);
    }

    @Override
    public Optional<NamedScreenHandlerFactory> getFallback() {
        return Optional.empty();
    }

    @Override
    public /* synthetic */ Object getFallback() {
        return this.getFallback();
    }
}
