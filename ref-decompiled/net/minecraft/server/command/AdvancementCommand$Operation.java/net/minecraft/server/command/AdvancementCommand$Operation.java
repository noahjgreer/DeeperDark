/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.server.network.ServerPlayerEntity;

static abstract sealed class AdvancementCommand.Operation
extends Enum<AdvancementCommand.Operation> {
    public static final /* enum */ AdvancementCommand.Operation GRANT = new AdvancementCommand.Operation("grant"){

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
    };
    public static final /* enum */ AdvancementCommand.Operation REVOKE = new AdvancementCommand.Operation("revoke"){

        @Override
        protected boolean processEach(ServerPlayerEntity player, AdvancementEntry advancement) {
            AdvancementProgress advancementProgress = player.getAdvancementTracker().getProgress(advancement);
            if (!advancementProgress.isAnyObtained()) {
                return false;
            }
            for (String string : advancementProgress.getObtainedCriteria()) {
                player.getAdvancementTracker().revokeCriterion(advancement, string);
            }
            return true;
        }

        @Override
        protected boolean processEachCriterion(ServerPlayerEntity player, AdvancementEntry advancement, String criterion) {
            return player.getAdvancementTracker().revokeCriterion(advancement, criterion);
        }
    };
    private final String commandPrefix;
    private static final /* synthetic */ AdvancementCommand.Operation[] field_13455;

    public static AdvancementCommand.Operation[] values() {
        return (AdvancementCommand.Operation[])field_13455.clone();
    }

    public static AdvancementCommand.Operation valueOf(String string) {
        return Enum.valueOf(AdvancementCommand.Operation.class, string);
    }

    AdvancementCommand.Operation(String name) {
        this.commandPrefix = "commands.advancement." + name;
    }

    public int processAll(ServerPlayerEntity player, Iterable<AdvancementEntry> advancements, boolean skipSync) {
        int i = 0;
        if (!skipSync) {
            player.getAdvancementTracker().sendUpdate(player, true);
        }
        for (AdvancementEntry advancementEntry : advancements) {
            if (!this.processEach(player, advancementEntry)) continue;
            ++i;
        }
        if (!skipSync) {
            player.getAdvancementTracker().sendUpdate(player, false);
        }
        return i;
    }

    protected abstract boolean processEach(ServerPlayerEntity var1, AdvancementEntry var2);

    protected abstract boolean processEachCriterion(ServerPlayerEntity var1, AdvancementEntry var2, String var3);

    protected String getCommandPrefix() {
        return this.commandPrefix;
    }

    private static /* synthetic */ AdvancementCommand.Operation[] method_36964() {
        return new AdvancementCommand.Operation[]{GRANT, REVOKE};
    }

    static {
        field_13455 = AdvancementCommand.Operation.method_36964();
    }
}
