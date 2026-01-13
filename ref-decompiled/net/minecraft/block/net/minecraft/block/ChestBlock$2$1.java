/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

class ChestBlock.1
implements NamedScreenHandlerFactory {
    final /* synthetic */ ChestBlockEntity field_17358;
    final /* synthetic */ ChestBlockEntity field_17359;
    final /* synthetic */ Inventory field_17360;

    ChestBlock.1() {
        this.field_17358 = chestBlockEntity;
        this.field_17359 = chestBlockEntity2;
        this.field_17360 = inventory;
    }

    @Override
    public @Nullable ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        if (this.field_17358.checkUnlocked(playerEntity) && this.field_17359.checkUnlocked(playerEntity)) {
            this.field_17358.generateLoot(playerInventory.player);
            this.field_17359.generateLoot(playerInventory.player);
            return GenericContainerScreenHandler.createGeneric9x6(i, playerInventory, this.field_17360);
        }
        Direction direction = ChestBlock.getFacing(this.field_17358.getCachedState());
        Vec3d vec3d = this.field_17358.getPos().toCenterPos();
        Vec3d vec3d2 = vec3d.add((double)direction.getOffsetX() / 2.0, 0.0, (double)direction.getOffsetZ() / 2.0);
        LockableContainerBlockEntity.handleLocked(vec3d2, playerEntity, this.getDisplayName());
        return null;
    }

    @Override
    public Text getDisplayName() {
        if (this.field_17358.hasCustomName()) {
            return this.field_17358.getDisplayName();
        }
        if (this.field_17359.hasCustomName()) {
            return this.field_17359.getDisplayName();
        }
        return Text.translatable("container.chestDouble");
    }
}
