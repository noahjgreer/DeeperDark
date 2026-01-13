/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap$Entry
 */
package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.predicate.entity.PlayerPredicate;
import net.minecraft.util.dynamic.Codecs;

record PlayerPredicate.AdvancementCriteriaPredicate(Object2BooleanMap<String> criteria) implements PlayerPredicate.AdvancementPredicate
{
    public static final Codec<PlayerPredicate.AdvancementCriteriaPredicate> CODEC = Codecs.object2BooleanMap(Codec.STRING).xmap(PlayerPredicate.AdvancementCriteriaPredicate::new, PlayerPredicate.AdvancementCriteriaPredicate::criteria);

    @Override
    public boolean test(AdvancementProgress advancementProgress) {
        for (Object2BooleanMap.Entry entry : this.criteria.object2BooleanEntrySet()) {
            CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
            if (criterionProgress != null && criterionProgress.isObtained() == entry.getBooleanValue()) continue;
            return false;
        }
        return true;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PlayerPredicate.AdvancementCriteriaPredicate.class, "criterions", "criteria"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PlayerPredicate.AdvancementCriteriaPredicate.class, "criterions", "criteria"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PlayerPredicate.AdvancementCriteriaPredicate.class, "criterions", "criteria"}, this, object);
    }

    @Override
    public /* synthetic */ boolean test(Object progress) {
        return this.test((AdvancementProgress)progress);
    }
}
