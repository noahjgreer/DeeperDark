/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.predicate.entity;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.predicate.NumberRange;

record LocationPredicate.PositionRange(NumberRange.DoubleRange x, NumberRange.DoubleRange y, NumberRange.DoubleRange z) {
    public static final Codec<LocationPredicate.PositionRange> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)NumberRange.DoubleRange.CODEC.optionalFieldOf("x", (Object)NumberRange.DoubleRange.ANY).forGetter(LocationPredicate.PositionRange::x), (App)NumberRange.DoubleRange.CODEC.optionalFieldOf("y", (Object)NumberRange.DoubleRange.ANY).forGetter(LocationPredicate.PositionRange::y), (App)NumberRange.DoubleRange.CODEC.optionalFieldOf("z", (Object)NumberRange.DoubleRange.ANY).forGetter(LocationPredicate.PositionRange::z)).apply((Applicative)instance, LocationPredicate.PositionRange::new));

    static Optional<LocationPredicate.PositionRange> create(NumberRange.DoubleRange x, NumberRange.DoubleRange y, NumberRange.DoubleRange z) {
        if (x.isDummy() && y.isDummy() && z.isDummy()) {
            return Optional.empty();
        }
        return Optional.of(new LocationPredicate.PositionRange(x, y, z));
    }

    public boolean test(double x, double y, double z) {
        return this.x.test(x) && this.y.test(y) && this.z.test(z);
    }
}
