/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.passive;

import java.util.List;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.attribute.EnvironmentAttributes;
import org.jspecify.annotations.Nullable;

static class CatEntity.SleepWithOwnerGoal
extends Goal {
    private final CatEntity cat;
    private @Nullable PlayerEntity owner;
    private @Nullable BlockPos bedPos;
    private int ticksOnBed;

    public CatEntity.SleepWithOwnerGoal(CatEntity cat) {
        this.cat = cat;
    }

    @Override
    public boolean canStart() {
        if (!this.cat.isTamed()) {
            return false;
        }
        if (this.cat.isSitting()) {
            return false;
        }
        LivingEntity livingEntity = this.cat.getOwner();
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity;
            this.owner = playerEntity = (PlayerEntity)livingEntity;
            if (!livingEntity.isSleeping()) {
                return false;
            }
            if (this.cat.squaredDistanceTo(this.owner) > 100.0) {
                return false;
            }
            BlockPos blockPos = this.owner.getBlockPos();
            BlockState blockState = this.cat.getEntityWorld().getBlockState(blockPos);
            if (blockState.isIn(BlockTags.BEDS)) {
                this.bedPos = blockState.getOrEmpty(BedBlock.FACING).map(direction -> blockPos.offset(direction.getOpposite())).orElseGet(() -> new BlockPos(blockPos));
                return !this.cannotSleep();
            }
        }
        return false;
    }

    private boolean cannotSleep() {
        List<CatEntity> list = this.cat.getEntityWorld().getNonSpectatingEntities(CatEntity.class, new Box(this.bedPos).expand(2.0));
        for (CatEntity catEntity : list) {
            if (catEntity == this.cat || !catEntity.isInSleepingPose() && !catEntity.isHeadDown()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        return this.cat.isTamed() && !this.cat.isSitting() && this.owner != null && this.owner.isSleeping() && this.bedPos != null && !this.cannotSleep();
    }

    @Override
    public void start() {
        if (this.bedPos != null) {
            this.cat.setInSittingPose(false);
            this.cat.getNavigation().startMovingTo(this.bedPos.getX(), this.bedPos.getY(), this.bedPos.getZ(), 1.1f);
        }
    }

    @Override
    public void stop() {
        this.cat.setInSleepingPose(false);
        if (this.owner.getSleepTimer() >= 100 && this.cat.getEntityWorld().getRandom().nextFloat() < this.cat.getEntityWorld().getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.CAT_WAKING_UP_GIFT_CHANCE_GAMEPLAY, this.cat.getEntityPos()).floatValue()) {
            this.dropMorningGifts();
        }
        this.ticksOnBed = 0;
        this.cat.setHeadDown(false);
        this.cat.getNavigation().stop();
    }

    private void dropMorningGifts() {
        Random random = this.cat.getRandom();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        mutable.set(this.cat.isLeashed() ? this.cat.getLeashHolder().getBlockPos() : this.cat.getBlockPos());
        this.cat.teleport(mutable.getX() + random.nextInt(11) - 5, mutable.getY() + random.nextInt(5) - 2, mutable.getZ() + random.nextInt(11) - 5, false);
        mutable.set(this.cat.getBlockPos());
        this.cat.forEachGiftedItem(CatEntity.SleepWithOwnerGoal.getServerWorld(this.cat), LootTables.CAT_MORNING_GIFT_GAMEPLAY, (world, stack) -> world.spawnEntity(new ItemEntity((World)world, (double)mutable.getX() - (double)MathHelper.sin(this.cat.bodyYaw * ((float)Math.PI / 180)), mutable.getY(), (double)mutable.getZ() + (double)MathHelper.cos(this.cat.bodyYaw * ((float)Math.PI / 180)), (ItemStack)stack)));
    }

    @Override
    public void tick() {
        if (this.owner != null && this.bedPos != null) {
            this.cat.setInSittingPose(false);
            this.cat.getNavigation().startMovingTo(this.bedPos.getX(), this.bedPos.getY(), this.bedPos.getZ(), 1.1f);
            if (this.cat.squaredDistanceTo(this.owner) < 2.5) {
                ++this.ticksOnBed;
                if (this.ticksOnBed > this.getTickCount(16)) {
                    this.cat.setInSleepingPose(true);
                    this.cat.setHeadDown(false);
                } else {
                    this.cat.lookAtEntity(this.owner, 45.0f, 45.0f);
                    this.cat.setHeadDown(true);
                }
            } else {
                this.cat.setInSleepingPose(false);
            }
        }
    }
}
