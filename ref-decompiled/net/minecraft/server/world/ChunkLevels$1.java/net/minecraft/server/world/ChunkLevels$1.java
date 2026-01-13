/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.world;

import net.minecraft.server.world.ChunkLevelType;

static class ChunkLevels.1 {
    static final /* synthetic */ int[] field_44853;

    static {
        field_44853 = new int[ChunkLevelType.values().length];
        try {
            ChunkLevels.1.field_44853[ChunkLevelType.INACCESSIBLE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ChunkLevels.1.field_44853[ChunkLevelType.FULL.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ChunkLevels.1.field_44853[ChunkLevelType.BLOCK_TICKING.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ChunkLevels.1.field_44853[ChunkLevelType.ENTITY_TICKING.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
