/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

record CloneCommand.DimensionalPos(ServerWorld dimension, BlockPos position) {
}
