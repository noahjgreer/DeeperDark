/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

import net.minecraft.server.command.CommandOutput;
import net.minecraft.text.Text;

class CommandOutput.1
implements CommandOutput {
    CommandOutput.1() {
    }

    @Override
    public void sendMessage(Text message) {
    }

    @Override
    public boolean shouldReceiveFeedback() {
        return false;
    }

    @Override
    public boolean shouldTrackOutput() {
        return false;
    }

    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return false;
    }
}
