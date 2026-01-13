/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.server.command.AdvancementCommand;
import net.minecraft.server.network.ServerPlayerEntity;

final class AdvancementCommand.Operation.1
extends AdvancementCommand.Operation {
    AdvancementCommand.Operation.1(String string2) {
    }

    @Override
    protected boolean processEach(ServerPlayerEntity player, AdvancementEntry advancement) {
        AdvancementProgress advancementProgress = player.getAdvancementTracker().getProgress(advancement);
        if (advancementProgress.isDone()) {
            return false;
        }
        for (String string : advancementProgress.getUnobtainedCriteria()) {
            player.getAdvancementTracker().grantCriterion(advancement, string);
        }
        return true;
    }

    @Override
    protected boolean processEachCriterion(ServerPlayerEntity player, AdvancementEntry advancement, String criterion) {
        return player.getAdvancementTracker().grantCriterion(advancement, criterion);
    }
}
