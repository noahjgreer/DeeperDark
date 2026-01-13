/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.ModelIds;
import net.minecraft.client.data.Models;
import net.minecraft.client.data.TextureMap;
import net.minecraft.client.data.TexturedModel;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockStateModelGenerator.BlockTexturePool {
    private final TextureMap textures;
    private final Map<Model, Identifier> knownModels = new HashMap<Model, Identifier>();
    private @Nullable BlockFamily family;
    private @Nullable ModelVariant baseModelId;
    private final Set<Block> children = new HashSet<Block>();

    public BlockStateModelGenerator.BlockTexturePool(TextureMap textures) {
        this.textures = textures;
    }

    public BlockStateModelGenerator.BlockTexturePool base(Block block, Model model) {
        this.baseModelId = BlockStateModelGenerator.createModelVariant(model.upload(block, this.textures, BlockStateModelGenerator.this.modelCollector));
        if (BASE_WITH_CUSTOM_GENERATOR.containsKey(block)) {
            BlockStateModelGenerator.this.blockStateCollector.accept(BASE_WITH_CUSTOM_GENERATOR.get(block).create(block, this.baseModelId, this.textures, BlockStateModelGenerator.this.modelCollector));
        } else {
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, BlockStateModelGenerator.createWeightedVariant(this.baseModelId)));
        }
        return this;
    }

    public BlockStateModelGenerator.BlockTexturePool parented(Block parent, Block child) {
        Identifier identifier = ModelIds.getBlockModelId(parent);
        BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(child, BlockStateModelGenerator.createWeightedVariant(identifier)));
        BlockStateModelGenerator.this.itemModelOutput.acceptAlias(parent.asItem(), child.asItem());
        this.children.add(child);
        return this;
    }

    public BlockStateModelGenerator.BlockTexturePool button(Block buttonBlock) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.BUTTON.upload(buttonBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.BUTTON_PRESSED.upload(buttonBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
        BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createButtonBlockState(buttonBlock, weightedVariant, weightedVariant2));
        Identifier identifier = Models.BUTTON_INVENTORY.upload(buttonBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
        BlockStateModelGenerator.this.registerParentedItemModel(buttonBlock, identifier);
        return this;
    }

    public BlockStateModelGenerator.BlockTexturePool wall(Block wallBlock) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_WALL_POST.upload(wallBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_WALL_SIDE.upload(wallBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_WALL_SIDE_TALL.upload(wallBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
        BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createWallBlockState(wallBlock, weightedVariant, weightedVariant2, weightedVariant3));
        Identifier identifier = Models.WALL_INVENTORY.upload(wallBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
        BlockStateModelGenerator.this.registerParentedItemModel(wallBlock, identifier);
        return this;
    }

    public BlockStateModelGenerator.BlockTexturePool customFence(Block customFenceBlock) {
        TextureMap textureMap = TextureMap.textureParticle(customFenceBlock);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.CUSTOM_FENCE_POST.upload(customFenceBlock, textureMap, BlockStateModelGenerator.this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.CUSTOM_FENCE_SIDE_NORTH.upload(customFenceBlock, textureMap, BlockStateModelGenerator.this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.CUSTOM_FENCE_SIDE_EAST.upload(customFenceBlock, textureMap, BlockStateModelGenerator.this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(Models.CUSTOM_FENCE_SIDE_SOUTH.upload(customFenceBlock, textureMap, BlockStateModelGenerator.this.modelCollector));
        WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant(Models.CUSTOM_FENCE_SIDE_WEST.upload(customFenceBlock, textureMap, BlockStateModelGenerator.this.modelCollector));
        BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createCustomFenceBlockState(customFenceBlock, weightedVariant, weightedVariant2, weightedVariant3, weightedVariant4, weightedVariant5));
        Identifier identifier = Models.CUSTOM_FENCE_INVENTORY.upload(customFenceBlock, textureMap, BlockStateModelGenerator.this.modelCollector);
        BlockStateModelGenerator.this.registerParentedItemModel(customFenceBlock, identifier);
        return this;
    }

    public BlockStateModelGenerator.BlockTexturePool fence(Block fenceBlock) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.FENCE_POST.upload(fenceBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.FENCE_SIDE.upload(fenceBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
        BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createFenceBlockState(fenceBlock, weightedVariant, weightedVariant2));
        Identifier identifier = Models.FENCE_INVENTORY.upload(fenceBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
        BlockStateModelGenerator.this.registerParentedItemModel(fenceBlock, identifier);
        return this;
    }

    public BlockStateModelGenerator.BlockTexturePool customFenceGate(Block customFenceGateBlock) {
        TextureMap textureMap = TextureMap.textureParticle(customFenceGateBlock);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CUSTOM_FENCE_GATE_OPEN.upload(customFenceGateBlock, textureMap, BlockStateModelGenerator.this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CUSTOM_FENCE_GATE.upload(customFenceGateBlock, textureMap, BlockStateModelGenerator.this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CUSTOM_FENCE_GATE_WALL_OPEN.upload(customFenceGateBlock, textureMap, BlockStateModelGenerator.this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_CUSTOM_FENCE_GATE_WALL.upload(customFenceGateBlock, textureMap, BlockStateModelGenerator.this.modelCollector));
        BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createFenceGateBlockState(customFenceGateBlock, weightedVariant, weightedVariant2, weightedVariant3, weightedVariant4, false));
        return this;
    }

    public BlockStateModelGenerator.BlockTexturePool fenceGate(Block fenceGateBlock) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_FENCE_GATE_OPEN.upload(fenceGateBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_FENCE_GATE.upload(fenceGateBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_FENCE_GATE_WALL_OPEN.upload(fenceGateBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant(Models.TEMPLATE_FENCE_GATE_WALL.upload(fenceGateBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
        BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createFenceGateBlockState(fenceGateBlock, weightedVariant, weightedVariant2, weightedVariant3, weightedVariant4, true));
        return this;
    }

    public BlockStateModelGenerator.BlockTexturePool pressurePlate(Block pressurePlateBlock) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.PRESSURE_PLATE_UP.upload(pressurePlateBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(Models.PRESSURE_PLATE_DOWN.upload(pressurePlateBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
        BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createPressurePlateBlockState(pressurePlateBlock, weightedVariant, weightedVariant2));
        return this;
    }

    public BlockStateModelGenerator.BlockTexturePool sign(Block signBlock) {
        if (this.family == null) {
            throw new IllegalStateException("Family not defined");
        }
        Block block = this.family.getVariants().get((Object)BlockFamily.Variant.WALL_SIGN);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.PARTICLE.upload(signBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
        BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(signBlock, weightedVariant));
        BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, weightedVariant));
        BlockStateModelGenerator.this.registerItemModel(signBlock.asItem());
        return this;
    }

    public BlockStateModelGenerator.BlockTexturePool slab(Block block) {
        if (this.baseModelId == null) {
            throw new IllegalStateException("Full block not generated yet");
        }
        Identifier identifier = this.ensureModel(Models.SLAB, block);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(this.ensureModel(Models.SLAB_TOP, block));
        BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSlabBlockState(block, BlockStateModelGenerator.createWeightedVariant(identifier), weightedVariant, BlockStateModelGenerator.createWeightedVariant(this.baseModelId)));
        BlockStateModelGenerator.this.registerParentedItemModel(block, identifier);
        return this;
    }

    public BlockStateModelGenerator.BlockTexturePool stairs(Block block) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(this.ensureModel(Models.INNER_STAIRS, block));
        Identifier identifier = this.ensureModel(Models.STAIRS, block);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant(this.ensureModel(Models.OUTER_STAIRS, block));
        BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createStairsBlockState(block, weightedVariant, BlockStateModelGenerator.createWeightedVariant(identifier), weightedVariant2));
        BlockStateModelGenerator.this.registerParentedItemModel(block, identifier);
        return this;
    }

    private BlockStateModelGenerator.BlockTexturePool block(Block block) {
        TexturedModel texturedModel = TEXTURED_MODELS.getOrDefault(block, TexturedModel.CUBE_ALL.get(block));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(texturedModel.upload(block, BlockStateModelGenerator.this.modelCollector));
        BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, weightedVariant));
        return this;
    }

    private BlockStateModelGenerator.BlockTexturePool door(Block block) {
        BlockStateModelGenerator.this.registerDoor(block);
        return this;
    }

    private void registerTrapdoor(Block block) {
        if (UNORIENTABLE_TRAPDOORS.contains(block)) {
            BlockStateModelGenerator.this.registerTrapdoor(block);
        } else {
            BlockStateModelGenerator.this.registerOrientableTrapdoor(block);
        }
    }

    private Identifier ensureModel(Model model, Block block) {
        return this.knownModels.computeIfAbsent(model, newModel -> newModel.upload(block, this.textures, BlockStateModelGenerator.this.modelCollector));
    }

    public BlockStateModelGenerator.BlockTexturePool family(BlockFamily family) {
        this.family = family;
        family.getVariants().forEach((variant, block) -> {
            if (this.children.contains(block)) {
                return;
            }
            BiConsumer<BlockStateModelGenerator.BlockTexturePool, Block> biConsumer = VARIANT_POOL_FUNCTIONS.get(variant);
            if (biConsumer != null) {
                biConsumer.accept(this, (Block)block);
            }
        });
        return this;
    }
}
