/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server;

import net.minecraft.command.permission.PermissionLevel;

static class PlayerManager.2 {
    static final /* synthetic */ int[] field_63214;

    static {
        field_63214 = new int[PermissionLevel.values().length];
        try {
            PlayerManager.2.field_63214[PermissionLevel.ALL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PlayerManager.2.field_63214[PermissionLevel.MODERATORS.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PlayerManager.2.field_63214[PermissionLevel.GAMEMASTERS.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PlayerManager.2.field_63214[PermissionLevel.ADMINS.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PlayerManager.2.field_63214[PermissionLevel.OWNERS.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
