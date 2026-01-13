/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

class FunctionCommand.5
implements FunctionCommand.ResultConsumer<ServerCommandSource> {
    FunctionCommand.5() {
    }

    @Override
    public void accept(ServerCommandSource serverCommandSource, Identifier identifier, int i) {
        serverCommandSource.sendFeedback(() -> Text.translatable("commands.function.result", Text.of(identifier), i), true);
    }
}
