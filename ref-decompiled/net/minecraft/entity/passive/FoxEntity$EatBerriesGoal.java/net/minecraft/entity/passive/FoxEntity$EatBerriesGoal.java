/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CaveVines;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.rule.GameRules;

public class FoxEntity.EatBerriesGoal
extends MoveToTargetPosGoal {
    private static final int EATING_TIME = 40;
    protected int timer;

    public FoxEntity.EatBerriesGoal(double speed, int range, int maxYDifference) {
        super(FoxEntity.this, speed, range, maxYDifference);
    }

    @Override
    public double getDesiredDistanceToTarget() {
        return 2.0;
    }

    @Override
    public boolean shouldResetPath() {
        return this.tryingTime % 100 == 0;
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isOf(Blocks.SWEET_BERRY_BUSH) && blockState.get(SweetBerryBushBlock.AGE) >= 2 || CaveVines.hasBerries(blockState);
    }

    @Override
    public void tick() {
        if (this.hasReached()) {
            if (this.timer >= 40) {
                this.eatBerries();
            } else {
                ++this.timer;
            }
        } else if (!this.hasReached() && FoxEntity.this.random.nextFloat() < 0.05f) {
            FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_SNIFF, 1.0f, 1.0f);
        }
        super.tick();
    }

    protected void eatBerries() {
        if (!FoxEntity.EatBerriesGoal.castToServerWorld(FoxEntity.this.getEntityWorld()).getGameRules().getValue(GameRules.DO_MOB_GRIEFING).booleanValue()) {
            return;
        }
        BlockState blockState = FoxEntity.this.getEntityWorld().getBlockState(this.targetPos);
        if (blockState.isOf(Blocks.SWEET_BERRY_BUSH)) {
            this.pickSweetBerries(blockState);
        } else if (CaveVines.hasBerries(blockState)) {
            this.pickGlowBerries(blockState);
        }
    }

    private void pickGlowBerries(BlockState state) {
        CaveVines.pickBerries(FoxEntity.this, state, FoxEntity.this.getEntityWorld(), this.targetPos);
    }

    private void pickSweetBerries(BlockState state) {
        int i = state.get(SweetBerryBushBlock.AGE);
        state.with(SweetBerryBushBlock.AGE, 1);
        int j = 1 + FoxEntity.this.getEntityWorld().random.nextInt(2) + (i == 3 ? 1 : 0);
        ItemStack itemStack = FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
        if (itemStack.isEmpty()) {
            FoxEntity.this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.SWEET_BERRIES));
            --j;
        }
        if (j > 0) {
            Block.dropStack(FoxEntity.this.getEntityWorld(), this.targetPos, new ItemStack(Items.SWEET_BERRIES, j));
        }
        FoxEntity.this.playSound(SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, 1.0f, 1.0f);
        FoxEntity.this.getEntityWorld().setBlockState(this.targetPos, (BlockState)state.with(SweetBerryBushBlock.AGE, 1), 2);
        FoxEntity.this.getEntityWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, this.targetPos, GameEvent.Emitter.of(FoxEntity.this));
    }

    @Override
    public boolean canStart() {
        return !FoxEntity.this.isSleeping() && super.canStart();
    }

    @Override
    public void start() {
        this.timer = 0;
        FoxEntity.this.setSitting(false);
        super.start();
    }
}
