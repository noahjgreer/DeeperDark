package net.minecraft.world.chunk.light;

import java.util.function.BiConsumer;
import net.minecraft.world.BlockView;

public interface LightSourceView extends BlockView {
   void forEachLightSource(BiConsumer callback);

   ChunkSkyLight getChunkSkyLight();
}
