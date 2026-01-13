/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.ChestLidAnimator
 *  net.minecraft.block.entity.EnderChestBlockEntity
 *  net.minecraft.block.entity.LidOpenable
 *  net.minecraft.block.entity.ViewerCountManager
 *  net.minecraft.entity.ContainerUser
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestLidAnimator;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.ContainerUser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnderChestBlockEntity
extends BlockEntity
implements LidOpenable {
    private final ChestLidAnimator lidAnimator = new ChestLidAnimator();
    private final ViewerCountManager stateManager = new /* Unavailable Anonymous Inner Class!! */;

    public EnderChestBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.ENDER_CHEST, pos, state);
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, EnderChestBlockEntity blockEntity) {
        blockEntity.lidAnimator.step();
    }

    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1) {
            this.lidAnimator.setOpen(data > 0);
            return true;
        }
        return super.onSyncedBlockEvent(type, data);
    }

    public void onOpen(ContainerUser user) {
        if (!this.removed && !user.asLivingEntity().isSpectator()) {
            this.stateManager.openContainer(user.asLivingEntity(), this.getWorld(), this.getPos(), this.getCachedState(), user.getContainerInteractionRange());
        }
    }

    public void onClose(ContainerUser user) {
        if (!this.removed && !user.asLivingEntity().isSpectator()) {
            this.stateManager.closeContainer(user.asLivingEntity(), this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse((BlockEntity)this, (PlayerEntity)player);
    }

    public void onScheduledTick() {
        if (!this.removed) {
            this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    public float getAnimationProgress(float tickProgress) {
        return this.lidAnimator.getProgress(tickProgress);
    }
}

