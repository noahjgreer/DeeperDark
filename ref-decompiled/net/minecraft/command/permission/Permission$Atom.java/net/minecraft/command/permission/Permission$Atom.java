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
import net.minecraft.util.Identifier;

public record Permission.Atom(Identifier id) implements Permission
{
    public static final MapCodec<Permission.Atom> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("id").forGetter(Permission.Atom::id)).apply((Applicative)instance, Permission.Atom::new));

    public MapCodec<Permission.Atom> getCodec() {
        return CODEC;
    }

    public static Permission.Atom ofVanilla(String path) {
        return Permission.Atom.of(Identifier.ofVanilla(path));
    }

    public static Permission.Atom of(Identifier id) {
        return new Permission.Atom(id);
    }
}
