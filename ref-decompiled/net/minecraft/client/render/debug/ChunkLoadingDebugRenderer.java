package net.minecraft.client.render.debug;

import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ChunkLoadingDebugRenderer implements DebugRenderer.Renderer {
   final MinecraftClient client;
   private double lastUpdateTime = Double.MIN_VALUE;
   private final int LOADING_DATA_CHUNK_RANGE = 12;
   @Nullable
   private ChunkLoadingStatus loadingData;

   public ChunkLoadingDebugRenderer(MinecraftClient client) {
      this.client = client;
   }

   public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
      double d = (double)Util.getMeasuringTimeNano();
      if (d - this.lastUpdateTime > 3.0E9) {
         this.lastUpdateTime = d;
         IntegratedServer integratedServer = this.client.getServer();
         if (integratedServer != null) {
            this.loadingData = new ChunkLoadingStatus(this, integratedServer, cameraX, cameraZ);
         } else {
            this.loadingData = null;
         }
      }

      if (this.loadingData != null) {
         Map map = (Map)this.loadingData.serverStates.getNow((Object)null);
         double e = this.client.gameRenderer.getCamera().getPos().y * 0.85;
         Iterator var14 = this.loadingData.clientStates.entrySet().iterator();

         while(var14.hasNext()) {
            Map.Entry entry = (Map.Entry)var14.next();
            ChunkPos chunkPos = (ChunkPos)entry.getKey();
            String string = (String)entry.getValue();
            if (map != null) {
               string = string + (String)map.get(chunkPos);
            }

            String[] strings = string.split("\n");
            int i = 0;
            String[] var20 = strings;
            int var21 = strings.length;

            for(int var22 = 0; var22 < var21; ++var22) {
               String string2 = var20[var22];
               DebugRenderer.drawString(matrices, vertexConsumers, string2, (double)ChunkSectionPos.getOffsetPos(chunkPos.x, 8), e + (double)i, (double)ChunkSectionPos.getOffsetPos(chunkPos.z, 8), -1, 0.15F, true, 0.0F, true);
               i -= 2;
            }
         }
      }

   }

   @Environment(EnvType.CLIENT)
   private final class ChunkLoadingStatus {
      final Map clientStates;
      final CompletableFuture serverStates;

      ChunkLoadingStatus(final ChunkLoadingDebugRenderer chunkLoadingDebugRenderer, final IntegratedServer server, final double x, final double z) {
         ClientWorld clientWorld = chunkLoadingDebugRenderer.client.world;
         RegistryKey registryKey = clientWorld.getRegistryKey();
         int i = ChunkSectionPos.getSectionCoord(x);
         int j = ChunkSectionPos.getSectionCoord(z);
         ImmutableMap.Builder builder = ImmutableMap.builder();
         ClientChunkManager clientChunkManager = clientWorld.getChunkManager();

         for(int k = i - 12; k <= i + 12; ++k) {
            for(int l = j - 12; l <= j + 12; ++l) {
               ChunkPos chunkPos = new ChunkPos(k, l);
               String string = "";
               WorldChunk worldChunk = clientChunkManager.getWorldChunk(k, l, false);
               string = string + "Client: ";
               if (worldChunk == null) {
                  string = string + "0n/a\n";
               } else {
                  string = string + (worldChunk.isEmpty() ? " E" : "");
                  string = string + "\n";
               }

               builder.put(chunkPos, string);
            }
         }

         this.clientStates = builder.build();
         this.serverStates = server.submit(() -> {
            ServerWorld serverWorld = server.getWorld(registryKey);
            if (serverWorld == null) {
               return ImmutableMap.of();
            } else {
               ImmutableMap.Builder builder = ImmutableMap.builder();
               ServerChunkManager serverChunkManager = serverWorld.getChunkManager();

               for(int k = i - 12; k <= i + 12; ++k) {
                  for(int l = j - 12; l <= j + 12; ++l) {
                     ChunkPos chunkPos = new ChunkPos(k, l);
                     String var10002 = serverChunkManager.getChunkLoadingDebugInfo(chunkPos);
                     builder.put(chunkPos, "Server: " + var10002);
                  }
               }

               return builder.build();
            }
         });
      }
   }
}
