/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Portal
 *  net.minecraft.block.Portal$Effect
 *  net.minecraft.entity.Entity
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.TeleportTarget
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import net.minecraft.block.Portal;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;
import org.jspecify.annotations.Nullable;

public interface Portal {
    default public int getPortalDelay(ServerWorld world, Entity entity) {
        return 0;
    }

    public @Nullable TeleportTarget createTeleportTarget(ServerWorld var1, Entity var2, BlockPos var3);

    default public Effect getPortalEffect() {
        return Effect.NONE;
    }
}

