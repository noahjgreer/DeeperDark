/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Function3
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import com.mojang.datafixers.util.Function3;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.BlockStateVariantMap;
import net.minecraft.client.data.PropertiesMap;
import net.minecraft.state.property.Property;

@Environment(value=EnvType.CLIENT)
public static class BlockStateVariantMap.TripleProperty<V, T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>>
extends BlockStateVariantMap<V> {
    private final Property<T1> first;
    private final Property<T2> second;
    private final Property<T3> third;

    BlockStateVariantMap.TripleProperty(Property<T1> first, Property<T2> second, Property<T3> third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public List<Property<?>> getProperties() {
        return List.of(this.first, this.second, this.third);
    }

    public BlockStateVariantMap.TripleProperty<V, T1, T2, T3> register(T1 firstProperty, T2 secondProperty, T3 thirdProperty, V variant) {
        PropertiesMap propertiesMap = PropertiesMap.withValues(this.first.createValue(firstProperty), this.second.createValue(secondProperty), this.third.createValue(thirdProperty));
        this.register(propertiesMap, variant);
        return this;
    }

    public BlockStateVariantMap<V> generate(Function3<T1, T2, T3, V> variantFactory) {
        this.first.getValues().forEach(firstValue -> this.second.getValues().forEach(secondValue -> this.third.getValues().forEach(thirdValue -> this.register(firstValue, secondValue, thirdValue, variantFactory.apply(firstValue, secondValue, thirdValue)))));
        return this;
    }
}
