package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

@Environment(EnvType.CLIENT)
public class NeighborUpdateDebugRenderer implements DebugRenderer.Renderer {
   private final MinecraftClient client;
   private final Map neighborUpdates = Maps.newTreeMap(Ordering.natural().reverse());

   NeighborUpdateDebugRenderer(MinecraftClient client) {
      this.client = client;
   }

   public void addNeighborUpdate(long time, BlockPos pos) {
      Map map = (Map)this.neighborUpdates.computeIfAbsent(time, (time2) -> {
         return Maps.newHashMap();
      });
      int i = (Integer)map.getOrDefault(pos, 0);
      map.put(pos, i + 1);
   }

   public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
      long l = this.client.world.getTime();
      int i = true;
      double d = 0.0025;
      Set set = Sets.newHashSet();
      Map map = Maps.newHashMap();
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
      Iterator iterator = this.neighborUpdates.entrySet().iterator();

      while(true) {
         Map.Entry entry;
         while(iterator.hasNext()) {
            entry = (Map.Entry)iterator.next();
            Long long_ = (Long)entry.getKey();
            Map map2 = (Map)entry.getValue();
            long m = l - long_;
            if (m > 200L) {
               iterator.remove();
            } else {
               Iterator var23 = map2.entrySet().iterator();

               while(var23.hasNext()) {
                  Map.Entry entry2 = (Map.Entry)var23.next();
                  BlockPos blockPos = (BlockPos)entry2.getKey();
                  Integer integer = (Integer)entry2.getValue();
                  if (set.add(blockPos)) {
                     Box box = (new Box(BlockPos.ORIGIN)).expand(0.002).contract(0.0025 * (double)m).offset((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()).offset(-cameraX, -cameraY, -cameraZ);
                     VertexRendering.drawBox(matrices, vertexConsumer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, 1.0F, 1.0F, 1.0F, 1.0F);
                     map.put(blockPos, integer);
                  }
               }
            }
         }

         iterator = map.entrySet().iterator();

         while(iterator.hasNext()) {
            entry = (Map.Entry)iterator.next();
            BlockPos blockPos2 = (BlockPos)entry.getKey();
            Integer integer2 = (Integer)entry.getValue();
            DebugRenderer.drawString(matrices, vertexConsumers, String.valueOf(integer2), blockPos2.getX(), blockPos2.getY(), blockPos2.getZ(), -1);
         }

         return;
      }
   }
}
