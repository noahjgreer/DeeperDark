/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.entity.player;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;

public static final class PlayerAbilities.Packed
extends Record {
    final boolean invulnerable;
    final boolean flying;
    final boolean mayFly;
    final boolean instabuild;
    final boolean mayBuild;
    final float flyingSpeed;
    final float walkingSpeed;
    public static final Codec<PlayerAbilities.Packed> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.BOOL.fieldOf("invulnerable").orElse((Object)false).forGetter(PlayerAbilities.Packed::invulnerable), (App)Codec.BOOL.fieldOf("flying").orElse((Object)false).forGetter(PlayerAbilities.Packed::flying), (App)Codec.BOOL.fieldOf("mayfly").orElse((Object)false).forGetter(PlayerAbilities.Packed::mayFly), (App)Codec.BOOL.fieldOf("instabuild").orElse((Object)false).forGetter(PlayerAbilities.Packed::instabuild), (App)Codec.BOOL.fieldOf("mayBuild").orElse((Object)true).forGetter(PlayerAbilities.Packed::mayBuild), (App)Codec.FLOAT.fieldOf("flySpeed").orElse((Object)Float.valueOf(0.05f)).forGetter(PlayerAbilities.Packed::flyingSpeed), (App)Codec.FLOAT.fieldOf("walkSpeed").orElse((Object)Float.valueOf(0.1f)).forGetter(PlayerAbilities.Packed::walkingSpeed)).apply((Applicative)instance, PlayerAbilities.Packed::new));

    public PlayerAbilities.Packed(boolean invulnerable, boolean flying, boolean mayFly, boolean instabuild, boolean mayBuild, float flyingSpeed, float walkingSpeed) {
        this.invulnerable = invulnerable;
        this.flying = flying;
        this.mayFly = mayFly;
        this.instabuild = instabuild;
        this.mayBuild = mayBuild;
        this.flyingSpeed = flyingSpeed;
        this.walkingSpeed = walkingSpeed;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PlayerAbilities.Packed.class, "invulnerable;flying;mayFly;instabuild;mayBuild;flyingSpeed;walkingSpeed", "invulnerable", "flying", "mayFly", "instabuild", "mayBuild", "flyingSpeed", "walkingSpeed"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PlayerAbilities.Packed.class, "invulnerable;flying;mayFly;instabuild;mayBuild;flyingSpeed;walkingSpeed", "invulnerable", "flying", "mayFly", "instabuild", "mayBuild", "flyingSpeed", "walkingSpeed"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PlayerAbilities.Packed.class, "invulnerable;flying;mayFly;instabuild;mayBuild;flyingSpeed;walkingSpeed", "invulnerable", "flying", "mayFly", "instabuild", "mayBuild", "flyingSpeed", "walkingSpeed"}, this, object);
    }

    public boolean invulnerable() {
        return this.invulnerable;
    }

    public boolean flying() {
        return this.flying;
    }

    public boolean mayFly() {
        return this.mayFly;
    }

    public boolean instabuild() {
        return this.instabuild;
    }

    public boolean mayBuild() {
        return this.mayBuild;
    }

    public float flyingSpeed() {
        return this.flyingSpeed;
    }

    public float walkingSpeed() {
        return this.walkingSpeed;
    }
}
