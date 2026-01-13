/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlock;
import net.minecraft.command.permission.LeveledPermissionPredicate;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CommandBlockExecutor;

class CommandBlockBlockEntity.1
extends CommandBlockExecutor {
    CommandBlockBlockEntity.1() {
    }

    @Override
    public void setCommand(String command) {
        super.setCommand(command);
        CommandBlockBlockEntity.this.markDirty();
    }

    @Override
    public void markDirty(ServerWorld world) {
        BlockState blockState = world.getBlockState(CommandBlockBlockEntity.this.pos);
        world.updateListeners(CommandBlockBlockEntity.this.pos, blockState, blockState, 3);
    }

    @Override
    public ServerCommandSource getSource(ServerWorld world, CommandOutput output) {
        Direction direction = CommandBlockBlockEntity.this.getCachedState().get(CommandBlock.FACING);
        return new ServerCommandSource(output, Vec3d.ofCenter(CommandBlockBlockEntity.this.pos), new Vec2f(0.0f, direction.getPositiveHorizontalDegrees()), world, LeveledPermissionPredicate.GAMEMASTERS, this.getName().getString(), this.getName(), world.getServer(), null);
    }

    @Override
    public boolean isEditable() {
        return !CommandBlockBlockEntity.this.isRemoved();
    }
}
