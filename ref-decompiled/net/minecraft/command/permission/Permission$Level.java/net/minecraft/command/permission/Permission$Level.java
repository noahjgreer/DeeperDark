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
import net.minecraft.command.permission.PermissionLevel;

public record Permission.Level(PermissionLevel level) implements Permission
{
    public static final MapCodec<Permission.Level> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)PermissionLevel.CODEC.fieldOf("level").forGetter(Permission.Level::level)).apply((Applicative)instance, Permission.Level::new));

    public MapCodec<Permission.Level> getCodec() {
        return CODEC;
    }
}
