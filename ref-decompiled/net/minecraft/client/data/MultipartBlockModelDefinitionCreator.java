/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.Block
 *  net.minecraft.client.data.BlockModelDefinitionCreator
 *  net.minecraft.client.data.MultipartBlockModelDefinitionCreator
 *  net.minecraft.client.data.MultipartBlockModelDefinitionCreator$Part
 *  net.minecraft.client.render.model.json.BlockModelDefinition
 *  net.minecraft.client.render.model.json.BlockModelDefinition$Multipart
 *  net.minecraft.client.render.model.json.MultipartModelCondition
 *  net.minecraft.client.render.model.json.MultipartModelConditionBuilder
 *  net.minecraft.client.render.model.json.WeightedVariant
 */
package net.minecraft.client.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockModelDefinitionCreator;
import net.minecraft.client.data.MultipartBlockModelDefinitionCreator;
import net.minecraft.client.render.model.json.BlockModelDefinition;
import net.minecraft.client.render.model.json.MultipartModelCondition;
import net.minecraft.client.render.model.json.MultipartModelConditionBuilder;
import net.minecraft.client.render.model.json.WeightedVariant;

@Environment(value=EnvType.CLIENT)
public class MultipartBlockModelDefinitionCreator
implements BlockModelDefinitionCreator {
    private final Block block;
    private final List<Part> multiparts = new ArrayList();

    private MultipartBlockModelDefinitionCreator(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return this.block;
    }

    public static MultipartBlockModelDefinitionCreator create(Block block) {
        return new MultipartBlockModelDefinitionCreator(block);
    }

    public MultipartBlockModelDefinitionCreator with(WeightedVariant part) {
        this.multiparts.add(new Part(Optional.empty(), part));
        return this;
    }

    private void validate(MultipartModelCondition selector) {
        selector.instantiate(this.block.getStateManager());
    }

    public MultipartBlockModelDefinitionCreator with(MultipartModelCondition condition, WeightedVariant part) {
        this.validate(condition);
        this.multiparts.add(new Part(Optional.of(condition), part));
        return this;
    }

    public MultipartBlockModelDefinitionCreator with(MultipartModelConditionBuilder conditionBuilder, WeightedVariant part) {
        return this.with(conditionBuilder.build(), part);
    }

    public BlockModelDefinition createBlockModelDefinition() {
        return new BlockModelDefinition(Optional.empty(), Optional.of(new BlockModelDefinition.Multipart(this.multiparts.stream().map(Part::toComponent).toList())));
    }
}

