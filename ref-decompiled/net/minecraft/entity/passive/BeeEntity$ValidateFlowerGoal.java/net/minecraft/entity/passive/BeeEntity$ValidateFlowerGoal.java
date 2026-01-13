/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

class BeeEntity.ValidateFlowerGoal
extends BeeEntity.NotAngryGoal {
    private final int ticksUntilNextValidate;
    private long lastValidateTime;

    BeeEntity.ValidateFlowerGoal() {
        super(BeeEntity.this);
        this.ticksUntilNextValidate = MathHelper.nextInt(BeeEntity.this.random, 20, 40);
        this.lastValidateTime = -1L;
    }

    @Override
    public void start() {
        if (BeeEntity.this.flowerPos != null && BeeEntity.this.getEntityWorld().isPosLoaded(BeeEntity.this.flowerPos) && !this.isFlower(BeeEntity.this.flowerPos)) {
            BeeEntity.this.clearFlowerPos();
        }
        this.lastValidateTime = BeeEntity.this.getEntityWorld().getTime();
    }

    @Override
    public boolean canBeeStart() {
        return BeeEntity.this.getEntityWorld().getTime() > this.lastValidateTime + (long)this.ticksUntilNextValidate;
    }

    @Override
    public boolean canBeeContinue() {
        return false;
    }

    private boolean isFlower(BlockPos pos) {
        return BeeEntity.isAttractive(BeeEntity.this.getEntityWorld().getBlockState(pos));
    }
}
