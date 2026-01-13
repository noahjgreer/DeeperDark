/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.advancement.criterion;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public record SlideDownBlockCriterion.Conditions(Optional<LootContextPredicate> player, Optional<RegistryEntry<Block>> block, Optional<StatePredicate> state) implements AbstractCriterion.Conditions
{
    public static final Codec<SlideDownBlockCriterion.Conditions> CODEC = RecordCodecBuilder.create((T instance) -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(SlideDownBlockCriterion.Conditions::player), (App)Registries.BLOCK.getEntryCodec().optionalFieldOf("block").forGetter(SlideDownBlockCriterion.Conditions::block), (App)StatePredicate.CODEC.optionalFieldOf("state").forGetter(SlideDownBlockCriterion.Conditions::state)).apply((Applicative)instance, SlideDownBlockCriterion.Conditions::new)).validate(SlideDownBlockCriterion.Conditions::validate);

    private static DataResult<SlideDownBlockCriterion.Conditions> validate(SlideDownBlockCriterion.Conditions conditions) {
        return conditions.block.flatMap(block -> conditions.state.flatMap(state -> state.findMissing(((Block)block.value()).getStateManager())).map(property -> DataResult.error(() -> "Block" + String.valueOf(block) + " has no property " + property))).orElseGet(() -> DataResult.success((Object)conditions));
    }

    public static AdvancementCriterion<SlideDownBlockCriterion.Conditions> create(Block block) {
        return Criteria.SLIDE_DOWN_BLOCK.create(new SlideDownBlockCriterion.Conditions(Optional.empty(), Optional.of(block.getRegistryEntry()), Optional.empty()));
    }

    public boolean test(BlockState state) {
        if (this.block.isPresent() && !state.isOf(this.block.get())) {
            return false;
        }
        return !this.state.isPresent() || this.state.get().test(state);
    }
}
