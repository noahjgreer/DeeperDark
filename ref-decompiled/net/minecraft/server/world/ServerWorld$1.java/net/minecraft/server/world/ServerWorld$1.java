/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.world;

import net.minecraft.world.World;

static class ServerWorld.1 {
    static final /* synthetic */ int[] field_52357;

    static {
        field_52357 = new int[World.ExplosionSourceType.values().length];
        try {
            ServerWorld.1.field_52357[World.ExplosionSourceType.NONE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerWorld.1.field_52357[World.ExplosionSourceType.BLOCK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerWorld.1.field_52357[World.ExplosionSourceType.MOB.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerWorld.1.field_52357[World.ExplosionSourceType.TNT.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerWorld.1.field_52357[World.ExplosionSourceType.TRIGGER.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
