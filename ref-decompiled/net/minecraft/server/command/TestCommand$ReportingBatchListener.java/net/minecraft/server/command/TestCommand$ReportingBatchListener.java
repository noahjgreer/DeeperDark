/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.test.BatchListener;
import net.minecraft.test.GameTestBatch;
import net.minecraft.text.Text;

record TestCommand.ReportingBatchListener(ServerCommandSource source) implements BatchListener
{
    @Override
    public void onStarted(GameTestBatch batch) {
        this.source.sendFeedback(() -> Text.translatable("commands.test.batch.starting", batch.environment().getIdAsString(), batch.index()), true);
    }

    @Override
    public void onFinished(GameTestBatch batch) {
    }
}
