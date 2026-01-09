package net.minecraft.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.fabricmc.fabric.api.block.v1.FabricBlockState;
import net.minecraft.registry.Registries;

public class BlockState extends AbstractBlock.AbstractBlockState implements FabricBlockState {
   public static final Codec CODEC;

   public BlockState(Block block, Reference2ObjectArrayMap reference2ObjectArrayMap, MapCodec mapCodec) {
      super(block, reference2ObjectArrayMap, mapCodec);
   }

   protected BlockState asBlockState() {
      return this;
   }

   static {
      CODEC = createCodec(Registries.BLOCK.getCodec(), Block::getDefaultState).stable();
   }
}
