/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.data.BlockStateVariantMap
 *  net.minecraft.client.data.BlockStateVariantMap$DoubleProperty
 *  net.minecraft.client.data.BlockStateVariantMap$QuadrupleProperty
 *  net.minecraft.client.data.BlockStateVariantMap$QuintupleProperty
 *  net.minecraft.client.data.BlockStateVariantMap$SingleProperty
 *  net.minecraft.client.data.BlockStateVariantMap$TripleProperty
 *  net.minecraft.client.data.PropertiesMap
 *  net.minecraft.client.render.model.json.ModelVariantOperator
 *  net.minecraft.client.render.model.json.WeightedVariant
 *  net.minecraft.state.property.Property
 */
package net.minecraft.client.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.BlockStateVariantMap;
import net.minecraft.client.data.PropertiesMap;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.state.property.Property;

@Environment(value=EnvType.CLIENT)
public abstract class BlockStateVariantMap<V> {
    private final Map<PropertiesMap, V> variants = new HashMap();

    protected void register(PropertiesMap properties, V variant) {
        V object = this.variants.put(properties, variant);
        if (object != null) {
            throw new IllegalStateException("Value " + String.valueOf(properties) + " is already defined");
        }
    }

    Map<PropertiesMap, V> getVariants() {
        this.validate();
        return Map.copyOf(this.variants);
    }

    private void validate() {
        List list = this.getProperties();
        Stream<Object> stream = Stream.of(PropertiesMap.EMPTY);
        for (Property property : list) {
            stream = stream.flatMap(propertiesMap -> property.stream().map(arg_0 -> ((PropertiesMap)propertiesMap).withValue(arg_0)));
        }
        List<PropertiesMap> list2 = stream.filter(propertiesMap -> !this.variants.containsKey(propertiesMap)).toList();
        if (!list2.isEmpty()) {
            throw new IllegalStateException("Missing definition for properties: " + String.valueOf(list2));
        }
    }

    abstract List<Property<?>> getProperties();

    public static <T1 extends Comparable<T1>> SingleProperty<WeightedVariant, T1> models(Property<T1> property) {
        return new SingleProperty(property);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>> DoubleProperty<WeightedVariant, T1, T2> models(Property<T1> property1, Property<T2> property2) {
        return new DoubleProperty(property1, property2);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> TripleProperty<WeightedVariant, T1, T2, T3> models(Property<T1> property1, Property<T2> property2, Property<T3> property3) {
        return new TripleProperty(property1, property2, property3);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>> QuadrupleProperty<WeightedVariant, T1, T2, T3, T4> models(Property<T1> property1, Property<T2> property2, Property<T3> property3, Property<T4> property4) {
        return new QuadrupleProperty(property1, property2, property3, property4);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>> QuintupleProperty<WeightedVariant, T1, T2, T3, T4, T5> models(Property<T1> property1, Property<T2> property2, Property<T3> property3, Property<T4> property4, Property<T5> property5) {
        return new QuintupleProperty(property1, property2, property3, property4, property5);
    }

    public static <T1 extends Comparable<T1>> SingleProperty<ModelVariantOperator, T1> operations(Property<T1> property) {
        return new SingleProperty(property);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>> DoubleProperty<ModelVariantOperator, T1, T2> operations(Property<T1> property1, Property<T2> property2) {
        return new DoubleProperty(property1, property2);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> TripleProperty<ModelVariantOperator, T1, T2, T3> operations(Property<T1> property1, Property<T2> property2, Property<T3> property3) {
        return new TripleProperty(property1, property2, property3);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>> QuadrupleProperty<ModelVariantOperator, T1, T2, T3, T4> operations(Property<T1> property1, Property<T2> property2, Property<T3> property3, Property<T4> property4) {
        return new QuadrupleProperty(property1, property2, property3, property4);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>> QuintupleProperty<ModelVariantOperator, T1, T2, T3, T4, T5> operations(Property<T1> property1, Property<T2> property2, Property<T3> property3, Property<T4> property4, Property<T5> property5) {
        return new QuintupleProperty(property1, property2, property3, property4, property5);
    }
}

