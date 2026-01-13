/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.Block
 *  net.minecraft.client.data.BlockModelDefinitionCreator
 *  net.minecraft.client.data.BlockStateVariantMap
 *  net.minecraft.client.data.PropertiesMap
 *  net.minecraft.client.data.VariantsBlockModelDefinitionCreator
 *  net.minecraft.client.data.VariantsBlockModelDefinitionCreator$Empty
 *  net.minecraft.client.data.VariantsBlockModelDefinitionCreator$Entry
 *  net.minecraft.client.render.model.BlockStateModel$Unbaked
 *  net.minecraft.client.render.model.json.BlockModelDefinition
 *  net.minecraft.client.render.model.json.BlockModelDefinition$Variants
 *  net.minecraft.client.render.model.json.ModelVariantOperator
 *  net.minecraft.client.render.model.json.WeightedVariant
 *  net.minecraft.state.property.Property
 */
package net.minecraft.client.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockModelDefinitionCreator;
import net.minecraft.client.data.BlockStateVariantMap;
import net.minecraft.client.data.PropertiesMap;
import net.minecraft.client.data.VariantsBlockModelDefinitionCreator;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.json.BlockModelDefinition;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.state.property.Property;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class VariantsBlockModelDefinitionCreator
implements BlockModelDefinitionCreator {
    private final Block block;
    private final List<Entry> variants;
    private final Set<Property<?>> definedProperties;

    VariantsBlockModelDefinitionCreator(Block block, List<Entry> variants, Set<Property<?>> definedProperties) {
        this.block = block;
        this.variants = variants;
        this.definedProperties = definedProperties;
    }

    static Set<Property<?>> validateAndAddProperties(Set<Property<?>> definedProperties, Block block, BlockStateVariantMap<?> variantMap) {
        List list = variantMap.getProperties();
        list.forEach(property -> {
            if (block.getStateManager().getProperty(property.getName()) != property) {
                throw new IllegalStateException("Property " + String.valueOf(property) + " is not defined for block " + String.valueOf(block));
            }
            if (definedProperties.contains(property)) {
                throw new IllegalStateException("Values of property " + String.valueOf(property) + " already defined for block " + String.valueOf(block));
            }
        });
        HashSet set = new HashSet(definedProperties);
        set.addAll(list);
        return set;
    }

    public VariantsBlockModelDefinitionCreator apply(BlockStateVariantMap<ModelVariantOperator> operators) {
        Set set = VariantsBlockModelDefinitionCreator.validateAndAddProperties((Set)this.definedProperties, (Block)this.block, operators);
        List list = this.variants.stream().flatMap(variant -> variant.apply(operators)).toList();
        return new VariantsBlockModelDefinitionCreator(this.block, list, set);
    }

    public VariantsBlockModelDefinitionCreator apply(ModelVariantOperator operator) {
        List list = this.variants.stream().flatMap(variant -> variant.apply(operator)).toList();
        return new VariantsBlockModelDefinitionCreator(this.block, list, this.definedProperties);
    }

    public BlockModelDefinition createBlockModelDefinition() {
        HashMap<String, BlockStateModel.Unbaked> map = new HashMap<String, BlockStateModel.Unbaked>();
        for (Entry entry : this.variants) {
            map.put(entry.properties.asString(), entry.variant.toModel());
        }
        return new BlockModelDefinition(Optional.of(new BlockModelDefinition.Variants(map)), Optional.empty());
    }

    public Block getBlock() {
        return this.block;
    }

    public static Empty of(Block block) {
        return new Empty(block);
    }

    public static VariantsBlockModelDefinitionCreator of(Block block, WeightedVariant model) {
        return new VariantsBlockModelDefinitionCreator(block, List.of(new Entry(PropertiesMap.EMPTY, model)), Set.of());
    }
}

