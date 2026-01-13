/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateVariantMap;
import net.minecraft.client.data.PropertiesMap;
import net.minecraft.client.data.VariantsBlockModelDefinitionCreator;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.state.property.Property;

@Environment(value=EnvType.CLIENT)
public static class VariantsBlockModelDefinitionCreator.Empty {
    private final Block block;

    public VariantsBlockModelDefinitionCreator.Empty(Block block) {
        this.block = block;
    }

    public VariantsBlockModelDefinitionCreator with(BlockStateVariantMap<WeightedVariant> variantMap) {
        Set<Property<?>> set = VariantsBlockModelDefinitionCreator.validateAndAddProperties(Set.of(), this.block, variantMap);
        List<VariantsBlockModelDefinitionCreator.Entry> list = variantMap.getVariants().entrySet().stream().map(entry -> new VariantsBlockModelDefinitionCreator.Entry((PropertiesMap)entry.getKey(), (WeightedVariant)entry.getValue())).toList();
        return new VariantsBlockModelDefinitionCreator(this.block, list, set);
    }
}
