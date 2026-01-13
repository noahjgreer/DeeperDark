/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command.permission;

import net.minecraft.command.permission.LeveledPermissionPredicate;
import net.minecraft.command.permission.PermissionLevel;

static class LeveledPermissionPredicate.1
implements LeveledPermissionPredicate {
    final /* synthetic */ PermissionLevel field_63186;

    LeveledPermissionPredicate.1(PermissionLevel permissionLevel) {
        this.field_63186 = permissionLevel;
    }

    @Override
    public PermissionLevel getLevel() {
        return this.field_63186;
    }

    public String toString() {
        return "permission level: " + this.field_63186.name();
    }
}
