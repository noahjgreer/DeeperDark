/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.entity;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.VariantSelectorProvider;

public record VariantSelectorProvider.Selector<Context, Condition extends VariantSelectorProvider.SelectorCondition<Context>>(Optional<Condition> condition, int priority) {
    public VariantSelectorProvider.Selector(Condition condition, int priority) {
        this(Optional.of(condition), priority);
    }

    public VariantSelectorProvider.Selector(int priority) {
        this(Optional.empty(), priority);
    }

    public static <Context, Condition extends VariantSelectorProvider.SelectorCondition<Context>> Codec<VariantSelectorProvider.Selector<Context, Condition>> createCodec(Codec<Condition> conditionCodec) {
        return RecordCodecBuilder.create(instance -> instance.group((App)conditionCodec.optionalFieldOf("condition").forGetter(VariantSelectorProvider.Selector::condition), (App)Codec.INT.fieldOf("priority").forGetter(VariantSelectorProvider.Selector::priority)).apply((Applicative)instance, VariantSelectorProvider.Selector::new));
    }
}
