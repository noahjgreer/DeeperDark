/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command.permission;

import net.minecraft.command.permission.PermissionLevel;

static class LeveledPermissionPredicate.2 {
    static final /* synthetic */ int[] field_63187;

    static {
        field_63187 = new int[PermissionLevel.values().length];
        try {
            LeveledPermissionPredicate.2.field_63187[PermissionLevel.ALL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LeveledPermissionPredicate.2.field_63187[PermissionLevel.MODERATORS.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LeveledPermissionPredicate.2.field_63187[PermissionLevel.GAMEMASTERS.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LeveledPermissionPredicate.2.field_63187[PermissionLevel.ADMINS.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LeveledPermissionPredicate.2.field_63187[PermissionLevel.OWNERS.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
