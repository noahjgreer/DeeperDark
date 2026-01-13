/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.network;

import net.minecraft.server.command.CommandOutput;
import net.minecraft.text.Text;
import net.minecraft.world.rule.GameRules;

class ServerPlayerEntity.3
implements CommandOutput {
    ServerPlayerEntity.3() {
    }

    @Override
    public boolean shouldReceiveFeedback() {
        return ServerPlayerEntity.this.getEntityWorld().getGameRules().getValue(GameRules.SEND_COMMAND_FEEDBACK);
    }

    @Override
    public boolean shouldTrackOutput() {
        return true;
    }

    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return true;
    }

    @Override
    public void sendMessage(Text message) {
        ServerPlayerEntity.this.sendMessage(message);
    }
}
