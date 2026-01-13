/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.BlockStateVariantMap;
import net.minecraft.client.data.PropertiesMap;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.client.render.model.json.WeightedVariant;

@Environment(value=EnvType.CLIENT)
static final class VariantsBlockModelDefinitionCreator.Entry
extends Record {
    final PropertiesMap properties;
    final WeightedVariant variant;

    VariantsBlockModelDefinitionCreator.Entry(PropertiesMap properties, WeightedVariant variant) {
        this.properties = properties;
        this.variant = variant;
    }

    public Stream<VariantsBlockModelDefinitionCreator.Entry> apply(BlockStateVariantMap<ModelVariantOperator> operatorMap) {
        return operatorMap.getVariants().entrySet().stream().map(variant -> {
            PropertiesMap propertiesMap = this.properties.copyOf((PropertiesMap)variant.getKey());
            WeightedVariant weightedVariant = this.variant.apply((ModelVariantOperator)variant.getValue());
            return new VariantsBlockModelDefinitionCreator.Entry(propertiesMap, weightedVariant);
        });
    }

    public Stream<VariantsBlockModelDefinitionCreator.Entry> apply(ModelVariantOperator operator) {
        return Stream.of(new VariantsBlockModelDefinitionCreator.Entry(this.properties, this.variant.apply(operator)));
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{VariantsBlockModelDefinitionCreator.Entry.class, "properties;variant", "properties", "variant"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{VariantsBlockModelDefinitionCreator.Entry.class, "properties;variant", "properties", "variant"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{VariantsBlockModelDefinitionCreator.Entry.class, "properties;variant", "properties", "variant"}, this, object);
    }

    public PropertiesMap properties() {
        return this.properties;
    }

    public WeightedVariant variant() {
        return this.variant;
    }
}
