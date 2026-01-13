/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.BlockDataObject;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

static class BlockDataObject.1
implements DataCommand.ObjectType {
    final /* synthetic */ String argumentName;

    BlockDataObject.1(String string) {
        this.argumentName = string;
    }

    @Override
    public DataCommandObject getObject(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        BlockPos blockPos = BlockPosArgumentType.getLoadedBlockPos(context, this.argumentName + "Pos");
        BlockEntity blockEntity = ((ServerCommandSource)context.getSource()).getWorld().getBlockEntity(blockPos);
        if (blockEntity == null) {
            throw INVALID_BLOCK_EXCEPTION.create();
        }
        return new BlockDataObject(blockEntity, blockPos);
    }

    @Override
    public ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(ArgumentBuilder<ServerCommandSource, ?> argument, Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> argumentAdder) {
        return argument.then(CommandManager.literal("block").then(argumentAdder.apply((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument(this.argumentName + "Pos", BlockPosArgumentType.blockPos()))));
    }
}
