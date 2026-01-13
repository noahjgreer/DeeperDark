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

public record EnterBlockCriterion.Conditions(Optional<LootContextPredicate> player, Optional<RegistryEntry<Block>> block, Optional<StatePredicate> state) implements AbstractCriterion.Conditions
{
    public static final Codec<EnterBlockCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(EnterBlockCriterion.Conditions::player), (App)Registries.BLOCK.getEntryCodec().optionalFieldOf("block").forGetter(EnterBlockCriterion.Conditions::block), (App)StatePredicate.CODEC.optionalFieldOf("state").forGetter(EnterBlockCriterion.Conditions::state)).apply((Applicative)instance, EnterBlockCriterion.Conditions::new)).validate(EnterBlockCriterion.Conditions::validate);

    private static DataResult<EnterBlockCriterion.Conditions> validate(EnterBlockCriterion.Conditions conditions) {
        return conditions.block.flatMap(block -> conditions.state.flatMap(state -> state.findMissing(((Block)block.value()).getStateManager())).map(property -> DataResult.error(() -> "Block" + String.valueOf(block) + " has no property " + property))).orElseGet(() -> DataResult.success((Object)conditions));
    }

    public static AdvancementCriterion<EnterBlockCriterion.Conditions> block(Block block) {
        return Criteria.ENTER_BLOCK.create(new EnterBlockCriterion.Conditions(Optional.empty(), Optional.of(block.getRegistryEntry()), Optional.empty()));
    }

    public boolean matches(BlockState state) {
        if (this.block.isPresent() && !state.isOf(this.block.get())) {
            return false;
        }
        return !this.state.isPresent() || this.state.get().test(state);
    }
}
