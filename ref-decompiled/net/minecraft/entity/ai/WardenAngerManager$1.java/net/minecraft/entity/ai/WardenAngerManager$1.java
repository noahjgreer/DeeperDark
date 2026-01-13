/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;

static class WardenAngerManager.1 {
    static final /* synthetic */ int[] field_39116;

    static {
        field_39116 = new int[Entity.RemovalReason.values().length];
        try {
            WardenAngerManager.1.field_39116[Entity.RemovalReason.CHANGED_DIMENSION.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            WardenAngerManager.1.field_39116[Entity.RemovalReason.UNLOADED_TO_CHUNK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            WardenAngerManager.1.field_39116[Entity.RemovalReason.UNLOADED_WITH_PLAYER.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
