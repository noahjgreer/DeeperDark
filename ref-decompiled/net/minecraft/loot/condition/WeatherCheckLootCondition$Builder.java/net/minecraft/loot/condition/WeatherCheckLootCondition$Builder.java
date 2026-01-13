/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.condition;

import java.util.Optional;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.WeatherCheckLootCondition;

public static class WeatherCheckLootCondition.Builder
implements LootCondition.Builder {
    private Optional<Boolean> raining = Optional.empty();
    private Optional<Boolean> thundering = Optional.empty();

    public WeatherCheckLootCondition.Builder raining(boolean raining) {
        this.raining = Optional.of(raining);
        return this;
    }

    public WeatherCheckLootCondition.Builder thundering(boolean thundering) {
        this.thundering = Optional.of(thundering);
        return this;
    }

    @Override
    public WeatherCheckLootCondition build() {
        return new WeatherCheckLootCondition(this.raining, this.thundering);
    }

    @Override
    public /* synthetic */ LootCondition build() {
        return this.build();
    }
}
