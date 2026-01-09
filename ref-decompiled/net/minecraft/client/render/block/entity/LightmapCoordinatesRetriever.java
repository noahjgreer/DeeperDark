package net.minecraft.client.render.block.entity;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;

@Environment(EnvType.CLIENT)
public class LightmapCoordinatesRetriever implements DoubleBlockProperties.PropertyRetriever {
   public Int2IntFunction getFromBoth(BlockEntity blockEntity, BlockEntity blockEntity2) {
      return (i) -> {
         int j = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos());
         int k = WorldRenderer.getLightmapCoordinates(blockEntity2.getWorld(), blockEntity2.getPos());
         int l = LightmapTextureManager.getBlockLightCoordinates(j);
         int m = LightmapTextureManager.getBlockLightCoordinates(k);
         int n = LightmapTextureManager.getSkyLightCoordinates(j);
         int o = LightmapTextureManager.getSkyLightCoordinates(k);
         return LightmapTextureManager.pack(Math.max(l, m), Math.max(n, o));
      };
   }

   public Int2IntFunction getFrom(BlockEntity blockEntity) {
      return (i) -> {
         return i;
      };
   }

   public Int2IntFunction getFallback() {
      return (i) -> {
         return i;
      };
   }

   // $FF: synthetic method
   public Object getFallback() {
      return this.getFallback();
   }
}
