/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.render.model.BlockStateManagers
 *  net.minecraft.registry.Registries
 *  net.minecraft.state.StateManager
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.model;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BlockStateManagers {
    private static final StateManager<Block, BlockState> ITEM_FRAME = BlockStateManagers.createItemFrameStateManager();
    private static final StateManager<Block, BlockState> GLOW_ITEM_FRAME = BlockStateManagers.createItemFrameStateManager();
    private static final Identifier GLOW_ITEM_FRAME_ID = Identifier.ofVanilla((String)"glow_item_frame");
    private static final Identifier ITEM_FRAME_ID = Identifier.ofVanilla((String)"item_frame");
    private static final Map<Identifier, StateManager<Block, BlockState>> STATIC_MANAGERS = Map.of(ITEM_FRAME_ID, ITEM_FRAME, GLOW_ITEM_FRAME_ID, GLOW_ITEM_FRAME);

    private static StateManager<Block, BlockState> createItemFrameStateManager() {
        return new StateManager.Builder((Object)Blocks.AIR).add(new Property[]{Properties.MAP}).build(Block::getDefaultState, BlockState::new);
    }

    public static BlockState getStateForItemFrame(boolean hasGlow, boolean hasMap) {
        return (BlockState)((BlockState)(hasGlow ? GLOW_ITEM_FRAME : ITEM_FRAME).getDefaultState()).with((Property)Properties.MAP, (Comparable)Boolean.valueOf(hasMap));
    }

    static Function<Identifier, StateManager<Block, BlockState>> createIdToManagerMapper() {
        HashMap<Identifier, StateManager> map = new HashMap<Identifier, StateManager>(STATIC_MANAGERS);
        for (Block block : Registries.BLOCK) {
            map.put(block.getRegistryEntry().registryKey().getValue(), block.getStateManager());
        }
        return map::get;
    }
}

