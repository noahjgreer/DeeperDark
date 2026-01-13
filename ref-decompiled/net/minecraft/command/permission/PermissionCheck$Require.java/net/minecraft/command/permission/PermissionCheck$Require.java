/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.command.permission;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.command.permission.Permission;
import net.minecraft.command.permission.PermissionCheck;
import net.minecraft.command.permission.PermissionPredicate;

public record PermissionCheck.Require(Permission permission) implements PermissionCheck
{
    public static final MapCodec<PermissionCheck.Require> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Permission.CODEC.fieldOf("permission").forGetter(PermissionCheck.Require::permission)).apply((Applicative)instance, PermissionCheck.Require::new));

    public MapCodec<PermissionCheck.Require> getCodec() {
        return CODEC;
    }

    @Override
    public boolean allows(PermissionPredicate permissions) {
        return permissions.hasPermission(this.permission);
    }
}
