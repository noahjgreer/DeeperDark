/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.context.ContextChain
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.util.Pair
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import net.minecraft.command.ControlFlowAware;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.ExecutionFlags;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

static abstract class FunctionCommand.Command
extends ControlFlowAware.Helper<ServerCommandSource>
implements ControlFlowAware.Command<ServerCommandSource> {
    FunctionCommand.Command() {
    }

    protected abstract @Nullable NbtCompound getArguments(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;

    @Override
    public void executeInner(ServerCommandSource serverCommandSource, ContextChain<ServerCommandSource> contextChain, ExecutionFlags executionFlags, ExecutionControl<ServerCommandSource> executionControl) throws CommandSyntaxException {
        CommandContext commandContext = contextChain.getTopContext().copyFor((Object)serverCommandSource);
        Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> pair = CommandFunctionArgumentType.getIdentifiedFunctions((CommandContext<ServerCommandSource>)commandContext, "name");
        Collection collection = (Collection)pair.getSecond();
        if (collection.isEmpty()) {
            throw NO_FUNCTIONS_EXCEPTION.create((Object)Text.of((Identifier)pair.getFirst()));
        }
        NbtCompound nbtCompound = this.getArguments((CommandContext<ServerCommandSource>)commandContext);
        ServerCommandSource serverCommandSource2 = FunctionCommand.createFunctionCommandSource(serverCommandSource);
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(() -> Text.translatable("commands.function.scheduled.single", Text.of(((CommandFunction)collection.iterator().next()).id())), true);
        } else {
            serverCommandSource.sendFeedback(() -> Text.translatable("commands.function.scheduled.multiple", Texts.join(collection.stream().map(CommandFunction::id).toList(), Text::of)), true);
        }
        FunctionCommand.enqueueAction(collection, nbtCompound, serverCommandSource, serverCommandSource2, executionControl, RESULT_REPORTER, executionFlags);
    }

    @Override
    public /* synthetic */ void executeInner(AbstractServerCommandSource source, ContextChain contextChain, ExecutionFlags flags, ExecutionControl control) throws CommandSyntaxException {
        this.executeInner((ServerCommandSource)source, (ContextChain<ServerCommandSource>)contextChain, flags, (ExecutionControl<ServerCommandSource>)control);
    }
}
