/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.vehicle;

import net.minecraft.command.permission.LeveledPermissionPredicate;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.CommandBlockExecutor;

class CommandBlockMinecartEntity.CommandExecutor
extends CommandBlockExecutor {
    CommandBlockMinecartEntity.CommandExecutor() {
    }

    @Override
    public void markDirty(ServerWorld world) {
        CommandBlockMinecartEntity.this.getDataTracker().set(COMMAND, this.getCommand());
        CommandBlockMinecartEntity.this.getDataTracker().set(LAST_OUTPUT, this.getLastOutput());
    }

    @Override
    public ServerCommandSource getSource(ServerWorld world, CommandOutput output) {
        return new ServerCommandSource(output, CommandBlockMinecartEntity.this.getEntityPos(), CommandBlockMinecartEntity.this.getRotationClient(), world, LeveledPermissionPredicate.GAMEMASTERS, this.getName().getString(), CommandBlockMinecartEntity.this.getDisplayName(), world.getServer(), CommandBlockMinecartEntity.this);
    }

    @Override
    public boolean isEditable() {
        return !CommandBlockMinecartEntity.this.isRemoved();
    }
}
