/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.event.GameEvent;

class EvokerEntity.ConjureFangsGoal
extends SpellcastingIllagerEntity.CastSpellGoal {
    EvokerEntity.ConjureFangsGoal() {
        super(EvokerEntity.this);
    }

    @Override
    protected int getSpellTicks() {
        return 40;
    }

    @Override
    protected int startTimeDelay() {
        return 100;
    }

    @Override
    protected void castSpell() {
        LivingEntity livingEntity = EvokerEntity.this.getTarget();
        double d = Math.min(livingEntity.getY(), EvokerEntity.this.getY());
        double e = Math.max(livingEntity.getY(), EvokerEntity.this.getY()) + 1.0;
        float f = (float)MathHelper.atan2(livingEntity.getZ() - EvokerEntity.this.getZ(), livingEntity.getX() - EvokerEntity.this.getX());
        if (EvokerEntity.this.squaredDistanceTo(livingEntity) < 9.0) {
            float g;
            int i;
            for (i = 0; i < 5; ++i) {
                g = f + (float)i * (float)Math.PI * 0.4f;
                this.conjureFangs(EvokerEntity.this.getX() + (double)MathHelper.cos(g) * 1.5, EvokerEntity.this.getZ() + (double)MathHelper.sin(g) * 1.5, d, e, g, 0);
            }
            for (i = 0; i < 8; ++i) {
                g = f + (float)i * (float)Math.PI * 2.0f / 8.0f + 1.2566371f;
                this.conjureFangs(EvokerEntity.this.getX() + (double)MathHelper.cos(g) * 2.5, EvokerEntity.this.getZ() + (double)MathHelper.sin(g) * 2.5, d, e, g, 3);
            }
        } else {
            for (int i = 0; i < 16; ++i) {
                double h = 1.25 * (double)(i + 1);
                int j = 1 * i;
                this.conjureFangs(EvokerEntity.this.getX() + (double)MathHelper.cos(f) * h, EvokerEntity.this.getZ() + (double)MathHelper.sin(f) * h, d, e, f, j);
            }
        }
    }

    private void conjureFangs(double x, double z, double maxY, double y, float yaw, int warmup) {
        BlockPos blockPos = BlockPos.ofFloored(x, y, z);
        boolean bl = false;
        double d = 0.0;
        do {
            BlockState blockState2;
            VoxelShape voxelShape;
            BlockPos blockPos2 = blockPos.down();
            BlockState blockState = EvokerEntity.this.getEntityWorld().getBlockState(blockPos2);
            if (!blockState.isSideSolidFullSquare(EvokerEntity.this.getEntityWorld(), blockPos2, Direction.UP)) continue;
            if (!EvokerEntity.this.getEntityWorld().isAir(blockPos) && !(voxelShape = (blockState2 = EvokerEntity.this.getEntityWorld().getBlockState(blockPos)).getCollisionShape(EvokerEntity.this.getEntityWorld(), blockPos)).isEmpty()) {
                d = voxelShape.getMax(Direction.Axis.Y);
            }
            bl = true;
            break;
        } while ((blockPos = blockPos.down()).getY() >= MathHelper.floor(maxY) - 1);
        if (bl) {
            EvokerEntity.this.getEntityWorld().spawnEntity(new EvokerFangsEntity(EvokerEntity.this.getEntityWorld(), x, (double)blockPos.getY() + d, z, yaw, warmup, EvokerEntity.this));
            EvokerEntity.this.getEntityWorld().emitGameEvent(GameEvent.ENTITY_PLACE, new Vec3d(x, (double)blockPos.getY() + d, z), GameEvent.Emitter.of(EvokerEntity.this));
        }
    }

    @Override
    protected SoundEvent getSoundPrepare() {
        return SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK;
    }

    @Override
    protected SpellcastingIllagerEntity.Spell getSpell() {
        return SpellcastingIllagerEntity.Spell.FANGS;
    }
}
