package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.custom.DebugStructuresCustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class StructureDebugRenderer implements DebugRenderer.Renderer {
   private final MinecraftClient client;
   private final Map structureBoundingBoxes = Maps.newIdentityHashMap();
   private final Map structurePiecesBoundingBoxes = Maps.newIdentityHashMap();
   private static final int RANGE = 500;

   public StructureDebugRenderer(MinecraftClient client) {
      this.client = client;
   }

   public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
      Camera camera = this.client.gameRenderer.getCamera();
      RegistryKey registryKey = this.client.world.getRegistryKey();
      BlockPos blockPos = BlockPos.ofFloored(camera.getPos().x, 0.0, camera.getPos().z);
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
      if (this.structureBoundingBoxes.containsKey(registryKey)) {
         Iterator var13 = ((Map)this.structureBoundingBoxes.get(registryKey)).values().iterator();

         while(var13.hasNext()) {
            BlockBox blockBox = (BlockBox)var13.next();
            if (blockPos.isWithinDistance(blockBox.getCenter(), 500.0)) {
               VertexRendering.drawBox(matrices, vertexConsumer, (double)blockBox.getMinX() - cameraX, (double)blockBox.getMinY() - cameraY, (double)blockBox.getMinZ() - cameraZ, (double)(blockBox.getMaxX() + 1) - cameraX, (double)(blockBox.getMaxY() + 1) - cameraY, (double)(blockBox.getMaxZ() + 1) - cameraZ, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      }

      Map map = (Map)this.structurePiecesBoundingBoxes.get(registryKey);
      if (map != null) {
         Iterator var18 = map.values().iterator();

         while(var18.hasNext()) {
            DebugStructuresCustomPayload.Piece piece = (DebugStructuresCustomPayload.Piece)var18.next();
            BlockBox blockBox2 = piece.boundingBox();
            if (blockPos.isWithinDistance(blockBox2.getCenter(), 500.0)) {
               if (piece.isStart()) {
                  VertexRendering.drawBox(matrices, vertexConsumer, (double)blockBox2.getMinX() - cameraX, (double)blockBox2.getMinY() - cameraY, (double)blockBox2.getMinZ() - cameraZ, (double)(blockBox2.getMaxX() + 1) - cameraX, (double)(blockBox2.getMaxY() + 1) - cameraY, (double)(blockBox2.getMaxZ() + 1) - cameraZ, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F);
               } else {
                  VertexRendering.drawBox(matrices, vertexConsumer, (double)blockBox2.getMinX() - cameraX, (double)blockBox2.getMinY() - cameraY, (double)blockBox2.getMinZ() - cameraZ, (double)(blockBox2.getMaxX() + 1) - cameraX, (double)(blockBox2.getMaxY() + 1) - cameraY, (double)(blockBox2.getMaxZ() + 1) - cameraZ, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F);
               }
            }
         }
      }

   }

   public void addStructure(BlockBox boundingBox, List pieces, RegistryKey dimensionKey) {
      ((Map)this.structureBoundingBoxes.computeIfAbsent(dimensionKey, (dimension) -> {
         return new HashMap();
      })).put(boundingBox.toString(), boundingBox);
      Map map = (Map)this.structurePiecesBoundingBoxes.computeIfAbsent(dimensionKey, (dimension) -> {
         return new HashMap();
      });
      Iterator var5 = pieces.iterator();

      while(var5.hasNext()) {
         DebugStructuresCustomPayload.Piece piece = (DebugStructuresCustomPayload.Piece)var5.next();
         map.put(piece.boundingBox().toString(), piece);
      }

   }

   public void clear() {
      this.structureBoundingBoxes.clear();
      this.structurePiecesBoundingBoxes.clear();
   }
}
