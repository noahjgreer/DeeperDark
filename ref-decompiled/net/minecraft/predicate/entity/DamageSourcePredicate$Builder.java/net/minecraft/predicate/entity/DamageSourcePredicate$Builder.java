/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package net.minecraft.predicate.entity;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.predicate.TagPredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;

public static class DamageSourcePredicate.Builder {
    private final ImmutableList.Builder<TagPredicate<DamageType>> tagPredicates = ImmutableList.builder();
    private Optional<EntityPredicate> directEntity = Optional.empty();
    private Optional<EntityPredicate> sourceEntity = Optional.empty();
    private Optional<Boolean> isDirect = Optional.empty();

    public static DamageSourcePredicate.Builder create() {
        return new DamageSourcePredicate.Builder();
    }

    public DamageSourcePredicate.Builder tag(TagPredicate<DamageType> tagPredicate) {
        this.tagPredicates.add(tagPredicate);
        return this;
    }

    public DamageSourcePredicate.Builder directEntity(EntityPredicate.Builder entity) {
        this.directEntity = Optional.of(entity.build());
        return this;
    }

    public DamageSourcePredicate.Builder sourceEntity(EntityPredicate.Builder entity) {
        this.sourceEntity = Optional.of(entity.build());
        return this;
    }

    public DamageSourcePredicate.Builder isDirect(boolean direct) {
        this.isDirect = Optional.of(direct);
        return this;
    }

    public DamageSourcePredicate build() {
        return new DamageSourcePredicate((List<TagPredicate<DamageType>>)this.tagPredicates.build(), this.directEntity, this.sourceEntity, this.isDirect);
    }
}
