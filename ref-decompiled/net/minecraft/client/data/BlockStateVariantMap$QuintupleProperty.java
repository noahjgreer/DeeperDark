/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Function5
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import com.mojang.datafixers.util.Function5;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.BlockStateVariantMap;
import net.minecraft.client.data.PropertiesMap;
import net.minecraft.state.property.Property;

@Environment(value=EnvType.CLIENT)
public static class BlockStateVariantMap.QuintupleProperty<V, T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>>
extends BlockStateVariantMap<V> {
    private final Property<T1> first;
    private final Property<T2> second;
    private final Property<T3> third;
    private final Property<T4> fourth;
    private final Property<T5> fifth;

    BlockStateVariantMap.QuintupleProperty(Property<T1> first, Property<T2> second, Property<T3> third, Property<T4> fourth, Property<T5> fifth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.fifth = fifth;
    }

    @Override
    public List<Property<?>> getProperties() {
        return List.of(this.first, this.second, this.third, this.fourth, this.fifth);
    }

    public BlockStateVariantMap.QuintupleProperty<V, T1, T2, T3, T4, T5> register(T1 firstProperty, T2 secondProperty, T3 thirdProperty, T4 fourthProperty, T5 fifthProperty, V variant) {
        PropertiesMap propertiesMap = PropertiesMap.withValues(this.first.createValue(firstProperty), this.second.createValue(secondProperty), this.third.createValue(thirdProperty), this.fourth.createValue(fourthProperty), this.fifth.createValue(fifthProperty));
        this.register(propertiesMap, variant);
        return this;
    }

    public BlockStateVariantMap<V> generate(Function5<T1, T2, T3, T4, T5, V> variantFactory) {
        this.first.getValues().forEach(firstValue -> this.second.getValues().forEach(secondValue -> this.third.getValues().forEach(thirdValue -> this.fourth.getValues().forEach(fourthValue -> this.fifth.getValues().forEach(fifthValue -> this.register(firstValue, secondValue, thirdValue, fourthValue, fifthValue, variantFactory.apply(firstValue, secondValue, thirdValue, fourthValue, fifthValue)))))));
        return this;
    }
}
