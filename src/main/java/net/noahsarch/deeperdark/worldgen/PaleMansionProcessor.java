package net.noahsarch.deeperdark.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PaleMansionProcessor extends StructureProcessor {
    public static final PaleMansionProcessor INSTANCE = new PaleMansionProcessor();
    public static final MapCodec<PaleMansionProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

    private static final Map<Block, Block> REPLACEMENTS = new HashMap<>();

    static {
        REPLACEMENTS.put(Blocks.DARK_OAK_LOG, Blocks.PALE_OAK_LOG);
        REPLACEMENTS.put(Blocks.DARK_OAK_PLANKS, Blocks.PALE_OAK_PLANKS);
        REPLACEMENTS.put(Blocks.DARK_OAK_STAIRS, Blocks.PALE_OAK_STAIRS);
        REPLACEMENTS.put(Blocks.DARK_OAK_SLAB, Blocks.PALE_OAK_SLAB);
        REPLACEMENTS.put(Blocks.DARK_OAK_FENCE, Blocks.PALE_OAK_FENCE);
        REPLACEMENTS.put(Blocks.DARK_OAK_PRESSURE_PLATE, Blocks.PALE_OAK_PRESSURE_PLATE);
        REPLACEMENTS.put(Blocks.DARK_OAK_FENCE_GATE, Blocks.PALE_OAK_FENCE_GATE);
        REPLACEMENTS.put(Blocks.DARK_OAK_TRAPDOOR, Blocks.PALE_OAK_TRAPDOOR);
        REPLACEMENTS.put(Blocks.DARK_OAK_BUTTON, Blocks.PALE_OAK_BUTTON);
        REPLACEMENTS.put(Blocks.DARK_OAK_DOOR, Blocks.PALE_OAK_DOOR);
        REPLACEMENTS.put(Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_PALE_OAK_LOG);
        REPLACEMENTS.put(Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.STRIPPED_PALE_OAK_WOOD);
        REPLACEMENTS.put(Blocks.DARK_OAK_WOOD, Blocks.PALE_OAK_WOOD);
        REPLACEMENTS.put(Blocks.COBBLESTONE, Blocks.COBBLED_DEEPSLATE);
        REPLACEMENTS.put(Blocks.COBBLESTONE_SLAB, Blocks.COBBLED_DEEPSLATE_SLAB);
        REPLACEMENTS.put(Blocks.COBBLESTONE_STAIRS, Blocks.COBBLED_DEEPSLATE_STAIRS);
        REPLACEMENTS.put(Blocks.COBBLESTONE_WALL, Blocks.COBBLED_DEEPSLATE_WALL);
        REPLACEMENTS.put(Blocks.MOSSY_COBBLESTONE, Blocks.COBBLED_DEEPSLATE); // Simplification, or use another variant if preferred
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlaceSettings data) {
        BlockState currentState = currentBlockInfo.state();

        if (currentState.getBlock() == Blocks.BIRCH_PLANKS) {
             return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), Blocks.STRIPPED_PALE_OAK_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.X), currentBlockInfo.nbt());
        }

        Block newBlock = REPLACEMENTS.get(currentState.getBlock());
        if (newBlock != null) {
            BlockState newState = newBlock.defaultBlockState();
            // Copy properties if they exist
            for (Property property : currentState.getProperties()) {
                if (newState.hasProperty(property)) {
                    newState = copyProperty(currentState, newState, property);
                }
            }
            return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), newState, currentBlockInfo.nbt());
        }
        return currentBlockInfo;
    }

    private <T extends Comparable<T>> BlockState copyProperty(BlockState source, BlockState target, Property<T> property) {
        return target.setValue(property, source.getValue(property));
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return net.noahsarch.deeperdark.Deeperdark.PALE_MANSION_PROCESSOR;
    }
}

