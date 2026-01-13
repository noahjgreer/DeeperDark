/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

import java.io.PrintWriter;
import net.minecraft.command.CommandExecutionContext;
import net.minecraft.command.CommandFunctionAction;
import net.minecraft.command.Frame;
import net.minecraft.command.ReturnValueConsumer;
import net.minecraft.server.command.DebugCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.Procedure;

class DebugCommand.Command.1
extends CommandFunctionAction<ServerCommandSource> {
    final /* synthetic */ PrintWriter field_46639;
    final /* synthetic */ CommandFunction field_46640;

    DebugCommand.Command.1(DebugCommand.Command command, Procedure procedure, ReturnValueConsumer returnValueConsumer, boolean bl, PrintWriter printWriter, CommandFunction commandFunction) {
        this.field_46639 = printWriter;
        this.field_46640 = commandFunction;
        super(procedure, returnValueConsumer, bl);
    }

    @Override
    public void execute(ServerCommandSource serverCommandSource, CommandExecutionContext<ServerCommandSource> commandExecutionContext, Frame frame) {
        this.field_46639.println(this.field_46640.id());
        super.execute(serverCommandSource, commandExecutionContext, frame);
    }

    @Override
    public /* synthetic */ void execute(Object object, CommandExecutionContext commandExecutionContext, Frame frame) {
        this.execute((ServerCommandSource)object, (CommandExecutionContext<ServerCommandSource>)commandExecutionContext, frame);
    }
}
