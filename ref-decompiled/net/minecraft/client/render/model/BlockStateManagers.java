package net.minecraft.client.render.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BlockStateManagers {
   private static final StateManager ITEM_FRAME = createItemFrameStateManager();
   private static final StateManager GLOW_ITEM_FRAME = createItemFrameStateManager();
   private static final Identifier GLOW_ITEM_FRAME_ID = Identifier.ofVanilla("glow_item_frame");
   private static final Identifier ITEM_FRAME_ID = Identifier.ofVanilla("item_frame");
   private static final Map STATIC_MANAGERS;

   private static StateManager createItemFrameStateManager() {
      return (new StateManager.Builder(Blocks.AIR)).add(Properties.MAP).build(Block::getDefaultState, BlockState::new);
   }

   public static BlockState getStateForItemFrame(boolean hasGlow, boolean hasMap) {
      return (BlockState)((BlockState)(hasGlow ? GLOW_ITEM_FRAME : ITEM_FRAME).getDefaultState()).with(Properties.MAP, hasMap);
   }

   static Function createIdToManagerMapper() {
      Map map = new HashMap(STATIC_MANAGERS);
      Iterator var1 = Registries.BLOCK.iterator();

      while(var1.hasNext()) {
         Block block = (Block)var1.next();
         map.put(block.getRegistryEntry().registryKey().getValue(), block.getStateManager());
      }

      Objects.requireNonNull(map);
      return map::get;
   }

   static {
      STATIC_MANAGERS = Map.of(ITEM_FRAME_ID, ITEM_FRAME, GLOW_ITEM_FRAME_ID, GLOW_ITEM_FRAME);
   }
}
