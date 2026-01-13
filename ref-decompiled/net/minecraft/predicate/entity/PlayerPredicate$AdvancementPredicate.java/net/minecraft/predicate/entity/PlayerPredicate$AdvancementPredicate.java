/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 */
package net.minecraft.predicate.entity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Predicate;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.predicate.entity.PlayerPredicate;

static interface PlayerPredicate.AdvancementPredicate
extends Predicate<AdvancementProgress> {
    public static final Codec<PlayerPredicate.AdvancementPredicate> CODEC = Codec.either(PlayerPredicate.CompletedAdvancementPredicate.CODEC, PlayerPredicate.AdvancementCriteriaPredicate.CODEC).xmap(Either::unwrap, predicate -> {
        if (predicate instanceof PlayerPredicate.CompletedAdvancementPredicate) {
            PlayerPredicate.CompletedAdvancementPredicate completedAdvancementPredicate = (PlayerPredicate.CompletedAdvancementPredicate)predicate;
            return Either.left((Object)completedAdvancementPredicate);
        }
        if (predicate instanceof PlayerPredicate.AdvancementCriteriaPredicate) {
            PlayerPredicate.AdvancementCriteriaPredicate advancementCriteriaPredicate = (PlayerPredicate.AdvancementCriteriaPredicate)predicate;
            return Either.right((Object)advancementCriteriaPredicate);
        }
        throw new UnsupportedOperationException();
    });
}
