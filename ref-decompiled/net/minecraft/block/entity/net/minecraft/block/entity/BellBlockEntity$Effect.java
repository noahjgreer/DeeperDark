/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
static interface BellBlockEntity.Effect {
    public void run(World var1, BlockPos var2, List<LivingEntity> var3);
}
