/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.command.permission;

import com.mojang.serialization.MapCodec;
import net.minecraft.command.permission.PermissionCheck;
import net.minecraft.command.permission.PermissionPredicate;

public static class PermissionCheck.AlwaysPass
implements PermissionCheck {
    public static final PermissionCheck.AlwaysPass INSTANCE = new PermissionCheck.AlwaysPass();
    public static final MapCodec<PermissionCheck.AlwaysPass> CODEC = MapCodec.unit((Object)INSTANCE);

    private PermissionCheck.AlwaysPass() {
    }

    @Override
    public boolean allows(PermissionPredicate permissions) {
        return true;
    }

    public MapCodec<PermissionCheck.AlwaysPass> getCodec() {
        return CODEC;
    }
}
