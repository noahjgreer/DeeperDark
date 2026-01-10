package net.noahsarch.deeperdark.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Property;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
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
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlacementData data) {
        BlockState currentState = currentBlockInfo.state();
        Block newBlock = REPLACEMENTS.get(currentState.getBlock());
        if (newBlock != null) {
            BlockState newState = newBlock.getDefaultState();
            // Copy properties if they exist
            for (Property property : currentState.getProperties()) {
                if (newState.contains(property)) {
                    newState = copyProperty(currentState, newState, property);
                }
            }
            return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), newState, currentBlockInfo.nbt());
        }
        return currentBlockInfo;
    }

    private <T extends Comparable<T>> BlockState copyProperty(BlockState source, BlockState target, Property<T> property) {
        return target.with(property, source.get(property));
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return net.noahsarch.deeperdark.Deeperdark.PALE_MANSION_PROCESSOR;
    }
}

