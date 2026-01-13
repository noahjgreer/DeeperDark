/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.BlockStateVariantMap;
import net.minecraft.client.data.PropertiesMap;
import net.minecraft.state.property.Property;

@Environment(value=EnvType.CLIENT)
public static class BlockStateVariantMap.SingleProperty<V, T1 extends Comparable<T1>>
extends BlockStateVariantMap<V> {
    private final Property<T1> property;

    BlockStateVariantMap.SingleProperty(Property<T1> property) {
        this.property = property;
    }

    @Override
    public List<Property<?>> getProperties() {
        return List.of(this.property);
    }

    public BlockStateVariantMap.SingleProperty<V, T1> register(T1 property, V variant) {
        PropertiesMap propertiesMap = PropertiesMap.withValues(this.property.createValue(property));
        this.register(propertiesMap, variant);
        return this;
    }

    public BlockStateVariantMap<V> generate(Function<T1, V> variantFactory) {
        this.property.getValues().forEach(value -> this.register(value, variantFactory.apply(value)));
        return this;
    }
}
