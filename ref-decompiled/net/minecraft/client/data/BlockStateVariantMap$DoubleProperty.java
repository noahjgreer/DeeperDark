/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import java.util.List;
import java.util.function.BiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.BlockStateVariantMap;
import net.minecraft.client.data.PropertiesMap;
import net.minecraft.state.property.Property;

@Environment(value=EnvType.CLIENT)
public static class BlockStateVariantMap.DoubleProperty<V, T1 extends Comparable<T1>, T2 extends Comparable<T2>>
extends BlockStateVariantMap<V> {
    private final Property<T1> first;
    private final Property<T2> second;

    BlockStateVariantMap.DoubleProperty(Property<T1> first, Property<T2> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public List<Property<?>> getProperties() {
        return List.of(this.first, this.second);
    }

    public BlockStateVariantMap.DoubleProperty<V, T1, T2> register(T1 firstProperty, T2 secondProperty, V variant) {
        PropertiesMap propertiesMap = PropertiesMap.withValues(this.first.createValue(firstProperty), this.second.createValue(secondProperty));
        this.register(propertiesMap, variant);
        return this;
    }

    public BlockStateVariantMap<V> generate(BiFunction<T1, T2, V> variantFactory) {
        this.first.getValues().forEach(firstValue -> this.second.getValues().forEach(secondValue -> this.register(firstValue, secondValue, variantFactory.apply(firstValue, secondValue))));
        return this;
    }
}
