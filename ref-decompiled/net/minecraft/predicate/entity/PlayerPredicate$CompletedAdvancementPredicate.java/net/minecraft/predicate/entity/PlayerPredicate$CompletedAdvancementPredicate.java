/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.predicate.entity.PlayerPredicate;

record PlayerPredicate.CompletedAdvancementPredicate(boolean done) implements PlayerPredicate.AdvancementPredicate
{
    public static final Codec<PlayerPredicate.CompletedAdvancementPredicate> CODEC = Codec.BOOL.xmap(PlayerPredicate.CompletedAdvancementPredicate::new, PlayerPredicate.CompletedAdvancementPredicate::done);

    @Override
    public boolean test(AdvancementProgress advancementProgress) {
        return advancementProgress.isDone() == this.done;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PlayerPredicate.CompletedAdvancementPredicate.class, "state", "done"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PlayerPredicate.CompletedAdvancementPredicate.class, "state", "done"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PlayerPredicate.CompletedAdvancementPredicate.class, "state", "done"}, this, object);
    }

    @Override
    public /* synthetic */ boolean test(Object progress) {
        return this.test((AdvancementProgress)progress);
    }
}
