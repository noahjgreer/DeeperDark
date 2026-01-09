package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;

@Environment(EnvType.CLIENT)
public class GameEventDebugRenderer implements DebugRenderer.Renderer {
   private final MinecraftClient client;
   private static final int field_32899 = 32;
   private static final float field_32900 = 1.0F;
   private final List entries = Lists.newArrayList();
   private final List listeners = Lists.newArrayList();

   public GameEventDebugRenderer(MinecraftClient client) {
      this.client = client;
   }

   public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
      World world = this.client.world;
      if (world == null) {
         this.entries.clear();
         this.listeners.clear();
      } else {
         Vec3d vec3d = new Vec3d(cameraX, 0.0, cameraZ);
         this.entries.removeIf(Entry::hasExpired);
         this.listeners.removeIf((listenerx) -> {
            return listenerx.isTooFar(world, vec3d);
         });
         VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
         Iterator var12 = this.listeners.iterator();

         while(var12.hasNext()) {
            Listener listener = (Listener)var12.next();
            listener.getPos(world).ifPresent((pos) -> {
               double g = pos.getX() - (double)listener.getRange();
               double h = pos.getY() - (double)listener.getRange();
               double i = pos.getZ() - (double)listener.getRange();
               double j = pos.getX() + (double)listener.getRange();
               double k = pos.getY() + (double)listener.getRange();
               double l = pos.getZ() + (double)listener.getRange();
               DebugRenderer.drawVoxelShapeOutlines(matrices, vertexConsumer, VoxelShapes.cuboid(new Box(g, h, i, j, k, l)), -cameraX, -cameraY, -cameraZ, 1.0F, 1.0F, 0.0F, 0.35F, true);
            });
         }

         VertexConsumer vertexConsumer2 = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());
         Iterator var31 = this.listeners.iterator();

         Listener listener2;
         while(var31.hasNext()) {
            listener2 = (Listener)var31.next();
            listener2.getPos(world).ifPresent((pos) -> {
               VertexRendering.drawFilledBox(matrices, vertexConsumer2, pos.getX() - 0.25 - cameraX, pos.getY() - cameraY, pos.getZ() - 0.25 - cameraZ, pos.getX() + 0.25 - cameraX, pos.getY() - cameraY + 1.0, pos.getZ() + 0.25 - cameraZ, 1.0F, 1.0F, 0.0F, 0.35F);
            });
         }

         var31 = this.listeners.iterator();

         while(var31.hasNext()) {
            listener2 = (Listener)var31.next();
            listener2.getPos(world).ifPresent((pos) -> {
               DebugRenderer.drawString(matrices, vertexConsumers, "Listener Origin", pos.getX(), pos.getY() + 1.7999999523162842, pos.getZ(), -1, 0.025F);
               DebugRenderer.drawString(matrices, vertexConsumers, BlockPos.ofFloored(pos).toString(), pos.getX(), pos.getY() + 1.5, pos.getZ(), -6959665, 0.025F);
            });
         }

         var31 = this.entries.iterator();

         while(var31.hasNext()) {
            Entry entry = (Entry)var31.next();
            Vec3d vec3d2 = entry.pos;
            double d = 0.20000000298023224;
            double e = vec3d2.x - 0.20000000298023224;
            double f = vec3d2.y - 0.20000000298023224;
            double g = vec3d2.z - 0.20000000298023224;
            double h = vec3d2.x + 0.20000000298023224;
            double i = vec3d2.y + 0.20000000298023224 + 0.5;
            double j = vec3d2.z + 0.20000000298023224;
            drawBoxIfCameraReady(matrices, vertexConsumers, new Box(e, f, g, h, i, j), 1.0F, 1.0F, 1.0F, 0.2F);
            DebugRenderer.drawString(matrices, vertexConsumers, entry.event.getValue().toString(), vec3d2.x, vec3d2.y + 0.8500000238418579, vec3d2.z, -7564911, 0.0075F);
         }

      }
   }

   private static void drawBoxIfCameraReady(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Box box, float red, float green, float blue, float alpha) {
      Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
      if (camera.isReady()) {
         Vec3d vec3d = camera.getPos().negate();
         DebugRenderer.drawBox(matrices, vertexConsumers, box.offset(vec3d), red, green, blue, alpha);
      }
   }

   public void addEvent(RegistryKey eventKey, Vec3d pos) {
      this.entries.add(new Entry(Util.getMeasuringTimeMs(), eventKey, pos));
   }

   public void addListener(PositionSource positionSource, int range) {
      this.listeners.add(new Listener(positionSource, range));
   }

   @Environment(EnvType.CLIENT)
   static class Listener implements GameEventListener {
      public final PositionSource positionSource;
      public final int range;

      public Listener(PositionSource positionSource, int range) {
         this.positionSource = positionSource;
         this.range = range;
      }

      public boolean isTooFar(World world, Vec3d pos) {
         return this.positionSource.getPos(world).filter((pos2) -> {
            return pos2.squaredDistanceTo(pos) <= 1024.0;
         }).isPresent();
      }

      public Optional getPos(World world) {
         return this.positionSource.getPos(world);
      }

      public PositionSource getPositionSource() {
         return this.positionSource;
      }

      public int getRange() {
         return this.range;
      }

      public boolean listen(ServerWorld world, RegistryEntry event, GameEvent.Emitter emitter, Vec3d emitterPos) {
         return false;
      }
   }

   @Environment(EnvType.CLIENT)
   private static record Entry(long startingMs, RegistryKey event, Vec3d pos) {
      final RegistryKey event;
      final Vec3d pos;

      Entry(long startingMs, RegistryKey registryKey, Vec3d pos) {
         this.startingMs = startingMs;
         this.event = registryKey;
         this.pos = pos;
      }

      public boolean hasExpired() {
         return Util.getMeasuringTimeMs() - this.startingMs > 3000L;
      }

      public long startingMs() {
         return this.startingMs;
      }

      public RegistryKey event() {
         return this.event;
      }

      public Vec3d pos() {
         return this.pos;
      }
   }
}
