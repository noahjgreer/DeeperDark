package net.minecraft.client.gui.hud;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.DataFixUtils;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.DynamicUniforms;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.debug.PacketSizeChart;
import net.minecraft.client.gui.hud.debug.PieChart;
import net.minecraft.client.gui.hud.debug.PingChart;
import net.minecraft.client.gui.hud.debug.RenderingChart;
import net.minecraft.client.gui.hud.debug.TickChart;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.ServerTickManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import net.minecraft.util.profiler.ServerTickType;
import net.minecraft.util.profiler.log.DebugSampleType;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.tick.TickManager;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;

@Environment(EnvType.CLIENT)
public class DebugHud {
   private static final float DEBUG_CROSSHAIR_SCALE = 0.01F;
   private static final int field_57920 = 18;
   private static final int TEXT_COLOR = -2039584;
   private static final int field_32188 = 2;
   private static final int field_32189 = 2;
   private static final int field_32190 = 2;
   private static final Map HEIGHT_MAP_TYPES;
   private final MinecraftClient client;
   private final AllocationRateCalculator allocationRateCalculator;
   private final TextRenderer textRenderer;
   private final GpuBuffer debugCrosshairBuffer;
   private final RenderSystem.ShapeIndexBuffer debugCrosshairIndexBuffer;
   private HitResult blockHit;
   private HitResult fluidHit;
   @Nullable
   private ChunkPos pos;
   @Nullable
   private WorldChunk chunk;
   @Nullable
   private CompletableFuture chunkFuture;
   private boolean showDebugHud;
   private boolean renderingChartVisible;
   private boolean renderingAndTickChartsVisible;
   private boolean packetSizeAndPingChartsVisible;
   private final MultiValueDebugSampleLogImpl frameNanosLog;
   private final MultiValueDebugSampleLogImpl tickNanosLog;
   private final MultiValueDebugSampleLogImpl pingLog;
   private final MultiValueDebugSampleLogImpl packetSizeLog;
   private final Map receivedDebugSamples;
   private final RenderingChart renderingChart;
   private final TickChart tickChart;
   private final PingChart pingChart;
   private final PacketSizeChart packetSizeChart;
   private final PieChart pieChart;

   public DebugHud(MinecraftClient client) {
      this.debugCrosshairIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.LINES);
      this.frameNanosLog = new MultiValueDebugSampleLogImpl(1);
      this.tickNanosLog = new MultiValueDebugSampleLogImpl(ServerTickType.values().length);
      this.pingLog = new MultiValueDebugSampleLogImpl(1);
      this.packetSizeLog = new MultiValueDebugSampleLogImpl(1);
      this.receivedDebugSamples = Map.of(DebugSampleType.TICK_TIME, this.tickNanosLog);
      this.client = client;
      this.allocationRateCalculator = new AllocationRateCalculator();
      this.textRenderer = client.textRenderer;
      this.renderingChart = new RenderingChart(this.textRenderer, this.frameNanosLog);
      this.tickChart = new TickChart(this.textRenderer, this.tickNanosLog, () -> {
         return client.world.getTickManager().getMillisPerTick();
      });
      this.pingChart = new PingChart(this.textRenderer, this.pingLog);
      this.packetSizeChart = new PacketSizeChart(this.textRenderer, this.packetSizeLog);
      this.pieChart = new PieChart(this.textRenderer);
      BufferAllocator bufferAllocator = BufferAllocator.method_72201(VertexFormats.POSITION_COLOR_NORMAL.getVertexSize() * 12);

      try {
         BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR_NORMAL);
         bufferBuilder.vertex(0.0F, 0.0F, 0.0F).color(-65536).normal(1.0F, 0.0F, 0.0F);
         bufferBuilder.vertex(1.0F, 0.0F, 0.0F).color(-65536).normal(1.0F, 0.0F, 0.0F);
         bufferBuilder.vertex(0.0F, 0.0F, 0.0F).color(-16711936).normal(0.0F, 1.0F, 0.0F);
         bufferBuilder.vertex(0.0F, 1.0F, 0.0F).color(-16711936).normal(0.0F, 1.0F, 0.0F);
         bufferBuilder.vertex(0.0F, 0.0F, 0.0F).color(-8421377).normal(0.0F, 0.0F, 1.0F);
         bufferBuilder.vertex(0.0F, 0.0F, 1.0F).color(-8421377).normal(0.0F, 0.0F, 1.0F);
         BuiltBuffer builtBuffer = bufferBuilder.end();

         try {
            this.debugCrosshairBuffer = RenderSystem.getDevice().createBuffer(() -> {
               return "Crosshair vertex buffer";
            }, 32, builtBuffer.getBuffer());
         } catch (Throwable var9) {
            if (builtBuffer != null) {
               try {
                  builtBuffer.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (builtBuffer != null) {
            builtBuffer.close();
         }
      } catch (Throwable var10) {
         if (bufferAllocator != null) {
            try {
               bufferAllocator.close();
            } catch (Throwable var7) {
               var10.addSuppressed(var7);
            }
         }

         throw var10;
      }

      if (bufferAllocator != null) {
         bufferAllocator.close();
      }

   }

   public void resetChunk() {
      this.chunkFuture = null;
      this.chunk = null;
   }

   public void render(DrawContext context) {
      Profiler profiler = Profilers.get();
      profiler.push("debug");
      Entity entity = this.client.getCameraEntity();
      this.blockHit = entity.raycast(20.0, 0.0F, false);
      this.fluidHit = entity.raycast(20.0, 0.0F, true);
      this.drawLeftText(context);
      this.drawRightText(context);
      context.createNewRootLayer();
      this.pieChart.setBottomMargin(10);
      int i;
      int j;
      int k;
      if (this.renderingAndTickChartsVisible) {
         i = context.getScaledWindowWidth();
         j = i / 2;
         this.renderingChart.render(context, 0, this.renderingChart.getWidth(j));
         if (this.tickNanosLog.getLength() > 0) {
            k = this.tickChart.getWidth(j);
            this.tickChart.render(context, i - k, k);
         }

         this.pieChart.setBottomMargin(this.tickChart.getHeight());
      }

      if (this.packetSizeAndPingChartsVisible) {
         i = context.getScaledWindowWidth();
         j = i / 2;
         if (!this.client.isInSingleplayer()) {
            this.packetSizeChart.render(context, 0, this.packetSizeChart.getWidth(j));
         }

         k = this.pingChart.getWidth(j);
         this.pingChart.render(context, i - k, k);
         this.pieChart.setBottomMargin(this.pingChart.getHeight());
      }

      ScopedProfiler scopedProfiler = profiler.scoped("profilerPie");

      try {
         this.pieChart.render(context);
      } catch (Throwable var8) {
         if (scopedProfiler != null) {
            try {
               scopedProfiler.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (scopedProfiler != null) {
         scopedProfiler.close();
      }

      profiler.pop();
   }

   protected void drawLeftText(DrawContext context) {
      List list = this.getLeftText();
      list.add("");
      boolean bl = this.client.getServer() != null;
      String var10001 = this.renderingChartVisible ? "visible" : "hidden";
      list.add("Debug charts: [F3+1] Profiler " + var10001 + "; [F3+2] " + (bl ? "FPS + TPS " : "FPS ") + (this.renderingAndTickChartsVisible ? "visible" : "hidden") + "; [F3+3] " + (!this.client.isInSingleplayer() ? "Bandwidth + Ping" : "Ping") + (this.packetSizeAndPingChartsVisible ? " visible" : " hidden"));
      list.add("For help: press F3 + Q");
      this.drawText(context, list, true);
   }

   protected void drawRightText(DrawContext context) {
      List list = this.getRightText();
      this.drawText(context, list, false);
   }

   private void drawText(DrawContext context, List text, boolean left) {
      Objects.requireNonNull(this.textRenderer);
      int i = 9;

      int j;
      String string;
      int k;
      int l;
      int m;
      for(j = 0; j < text.size(); ++j) {
         string = (String)text.get(j);
         if (!Strings.isNullOrEmpty(string)) {
            k = this.textRenderer.getWidth(string);
            l = left ? 2 : context.getScaledWindowWidth() - 2 - k;
            m = 2 + i * j;
            context.fill(l - 1, m - 1, l + k + 1, m + i - 1, -1873784752);
         }
      }

      for(j = 0; j < text.size(); ++j) {
         string = (String)text.get(j);
         if (!Strings.isNullOrEmpty(string)) {
            k = this.textRenderer.getWidth(string);
            l = left ? 2 : context.getScaledWindowWidth() - 2 - k;
            m = 2 + i * j;
            context.drawText(this.textRenderer, string, l, m, -2039584, false);
         }
      }

   }

   protected List getLeftText() {
      IntegratedServer integratedServer = this.client.getServer();
      ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
      ClientConnection clientConnection = clientPlayNetworkHandler.getConnection();
      float f = clientConnection.getAveragePacketsSent();
      float g = clientConnection.getAveragePacketsReceived();
      TickManager tickManager = this.getWorld().getTickManager();
      String string;
      if (tickManager.isStepping()) {
         string = " (frozen - stepping)";
      } else if (tickManager.isFrozen()) {
         string = " (frozen)";
      } else {
         string = "";
      }

      String string3;
      if (integratedServer != null) {
         ServerTickManager serverTickManager = integratedServer.getTickManager();
         boolean bl = serverTickManager.isSprinting();
         if (bl) {
            string = " (sprinting)";
         }

         String string2 = bl ? "-" : String.format(Locale.ROOT, "%.1f", tickManager.getMillisPerTick());
         string3 = String.format(Locale.ROOT, "Integrated server @ %.1f/%s ms%s, %.0f tx, %.0f rx", integratedServer.getAverageTickTime(), string2, string, f, g);
      } else {
         string3 = String.format(Locale.ROOT, "\"%s\" server%s, %.0f tx, %.0f rx", clientPlayNetworkHandler.getBrand(), string, f, g);
      }

      BlockPos blockPos = this.client.getCameraEntity().getBlockPos();
      String[] var10000;
      String var10003;
      if (this.client.hasReducedDebugInfo()) {
         var10000 = new String[9];
         var10003 = SharedConstants.getGameVersion().name();
         var10000[0] = "Minecraft " + var10003 + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ")";
         var10000[1] = this.client.fpsDebugString;
         var10000[2] = string3;
         var10000[3] = this.client.worldRenderer.getChunksDebugString();
         var10000[4] = this.client.worldRenderer.getEntitiesDebugString();
         var10003 = this.client.particleManager.getDebugString();
         var10000[5] = "P: " + var10003 + ". T: " + this.client.world.getRegularEntityCount();
         var10000[6] = this.client.world.asString();
         var10000[7] = "";
         var10000[8] = String.format(Locale.ROOT, "Chunk-relative: %d %d %d", blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15);
         return Lists.newArrayList(var10000);
      } else {
         Entity entity = this.client.getCameraEntity();
         Direction direction = entity.getHorizontalFacing();
         String string4;
         switch (direction) {
            case NORTH:
               string4 = "Towards negative Z";
               break;
            case SOUTH:
               string4 = "Towards positive Z";
               break;
            case WEST:
               string4 = "Towards negative X";
               break;
            case EAST:
               string4 = "Towards positive X";
               break;
            default:
               string4 = "Invalid";
         }

         ChunkPos chunkPos = new ChunkPos(blockPos);
         if (!Objects.equals(this.pos, chunkPos)) {
            this.pos = chunkPos;
            this.resetChunk();
         }

         World world = this.getWorld();
         LongSet longSet = world instanceof ServerWorld ? ((ServerWorld)world).getForcedChunks() : LongSets.EMPTY_SET;
         var10000 = new String[7];
         var10003 = SharedConstants.getGameVersion().name();
         var10000[0] = "Minecraft " + var10003 + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType()) + ")";
         var10000[1] = this.client.fpsDebugString;
         var10000[2] = string3;
         var10000[3] = this.client.worldRenderer.getChunksDebugString();
         var10000[4] = this.client.worldRenderer.getEntitiesDebugString();
         var10003 = this.client.particleManager.getDebugString();
         var10000[5] = "P: " + var10003 + ". T: " + this.client.world.getRegularEntityCount();
         var10000[6] = this.client.world.asString();
         List list = Lists.newArrayList(var10000);
         String string5 = this.getServerWorldDebugString();
         if (string5 != null) {
            list.add(string5);
         }

         String var10001 = String.valueOf(this.client.world.getRegistryKey().getValue());
         list.add(var10001 + " FC: " + ((LongSet)longSet).size());
         list.add("");
         list.add(String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", this.client.getCameraEntity().getX(), this.client.getCameraEntity().getY(), this.client.getCameraEntity().getZ()));
         list.add(String.format(Locale.ROOT, "Block: %d %d %d [%d %d %d]", blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15));
         list.add(String.format(Locale.ROOT, "Chunk: %d %d %d [%d %d in r.%d.%d.mca]", chunkPos.x, ChunkSectionPos.getSectionCoord(blockPos.getY()), chunkPos.z, chunkPos.getRegionRelativeX(), chunkPos.getRegionRelativeZ(), chunkPos.getRegionX(), chunkPos.getRegionZ()));
         list.add(String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", direction, string4, MathHelper.wrapDegrees(entity.getYaw()), MathHelper.wrapDegrees(entity.getPitch())));
         WorldChunk worldChunk = this.getClientChunk();
         if (worldChunk.isEmpty()) {
            list.add("Waiting for chunk...");
         } else {
            int i = this.client.world.getChunkManager().getLightingProvider().getLight(blockPos, 0);
            int j = this.client.world.getLightLevel(LightType.SKY, blockPos);
            int k = this.client.world.getLightLevel(LightType.BLOCK, blockPos);
            list.add("Client Light: " + i + " (" + j + " sky, " + k + " block)");
            WorldChunk worldChunk2 = this.getChunk();
            StringBuilder stringBuilder = new StringBuilder("CH");
            Heightmap.Type[] var24 = Heightmap.Type.values();
            int var25 = var24.length;

            int var26;
            Heightmap.Type type;
            for(var26 = 0; var26 < var25; ++var26) {
               type = var24[var26];
               if (type.shouldSendToClient()) {
                  stringBuilder.append(" ").append((String)HEIGHT_MAP_TYPES.get(type)).append(": ").append(worldChunk.sampleHeightmap(type, blockPos.getX(), blockPos.getZ()));
               }
            }

            list.add(stringBuilder.toString());
            stringBuilder.setLength(0);
            stringBuilder.append("SH");
            var24 = Heightmap.Type.values();
            var25 = var24.length;

            for(var26 = 0; var26 < var25; ++var26) {
               type = var24[var26];
               if (type.isStoredServerSide()) {
                  stringBuilder.append(" ").append((String)HEIGHT_MAP_TYPES.get(type)).append(": ");
                  if (worldChunk2 != null) {
                     stringBuilder.append(worldChunk2.sampleHeightmap(type, blockPos.getX(), blockPos.getZ()));
                  } else {
                     stringBuilder.append("??");
                  }
               }
            }

            list.add(stringBuilder.toString());
            if (this.client.world.isInHeightLimit(blockPos.getY())) {
               RegistryEntry var31 = this.client.world.getBiome(blockPos);
               list.add("Biome: " + getBiomeString(var31));
               if (worldChunk2 != null) {
                  float h = world.getMoonSize();
                  long l = worldChunk2.getInhabitedTime();
                  LocalDifficulty localDifficulty = new LocalDifficulty(world.getDifficulty(), world.getTimeOfDay(), l, h);
                  list.add(String.format(Locale.ROOT, "Local Difficulty: %.2f // %.2f (Day %d)", localDifficulty.getLocalDifficulty(), localDifficulty.getClampedLocalDifficulty(), this.client.world.getTimeOfDay() / 24000L));
               } else {
                  list.add("Local Difficulty: ??");
               }
            }

            if (worldChunk2 != null && worldChunk2.usesOldNoise()) {
               list.add("Blending: Old");
            }
         }

         ServerWorld serverWorld = this.getServerWorld();
         if (serverWorld != null) {
            ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
            ChunkGenerator chunkGenerator = serverChunkManager.getChunkGenerator();
            NoiseConfig noiseConfig = serverChunkManager.getNoiseConfig();
            chunkGenerator.appendDebugHudText(list, noiseConfig, blockPos);
            MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler = noiseConfig.getMultiNoiseSampler();
            BiomeSource biomeSource = chunkGenerator.getBiomeSource();
            biomeSource.addDebugInfo(list, blockPos, multiNoiseSampler);
            SpawnHelper.Info info = serverChunkManager.getSpawnInfo();
            if (info != null) {
               Object2IntMap object2IntMap = info.getGroupToCount();
               int m = info.getSpawningChunkCount();
               list.add("SC: " + m + ", " + (String)Stream.of(SpawnGroup.values()).map((group) -> {
                  char var10000 = Character.toUpperCase(group.getName().charAt(0));
                  return "" + var10000 + ": " + object2IntMap.getInt(group);
               }).collect(Collectors.joining(", ")));
            } else {
               list.add("SC: N/A");
            }
         }

         Identifier identifier = this.client.gameRenderer.getPostProcessorId();
         if (identifier != null) {
            list.add("Post: " + String.valueOf(identifier));
         }

         var10001 = this.client.getSoundManager().getDebugString();
         list.add(var10001 + String.format(Locale.ROOT, " (Mood %d%%)", Math.round(this.client.player.getMoodPercentage() * 100.0F)));
         return list;
      }
   }

   private static String getBiomeString(RegistryEntry biome) {
      return (String)biome.getKeyOrValue().map((biomeKey) -> {
         return biomeKey.getValue().toString();
      }, (biome_) -> {
         return "[unregistered " + String.valueOf(biome_) + "]";
      });
   }

   @Nullable
   private ServerWorld getServerWorld() {
      IntegratedServer integratedServer = this.client.getServer();
      return integratedServer != null ? integratedServer.getWorld(this.client.world.getRegistryKey()) : null;
   }

   @Nullable
   private String getServerWorldDebugString() {
      ServerWorld serverWorld = this.getServerWorld();
      return serverWorld != null ? serverWorld.asString() : null;
   }

   private World getWorld() {
      return (World)DataFixUtils.orElse(Optional.ofNullable(this.client.getServer()).flatMap((server) -> {
         return Optional.ofNullable(server.getWorld(this.client.world.getRegistryKey()));
      }), this.client.world);
   }

   @Nullable
   private WorldChunk getChunk() {
      if (this.chunkFuture == null) {
         ServerWorld serverWorld = this.getServerWorld();
         if (serverWorld == null) {
            return null;
         }

         this.chunkFuture = serverWorld.getChunkManager().getChunkFutureSyncOnMainThread(this.pos.x, this.pos.z, ChunkStatus.FULL, false).thenApply((chunk) -> {
            return (WorldChunk)chunk.orElse((Object)null);
         });
      }

      return (WorldChunk)this.chunkFuture.getNow((Object)null);
   }

   private WorldChunk getClientChunk() {
      if (this.chunk == null) {
         this.chunk = this.client.world.getChunk(this.pos.x, this.pos.z);
      }

      return this.chunk;
   }

   protected List getRightText() {
      long l = Runtime.getRuntime().maxMemory();
      long m = Runtime.getRuntime().totalMemory();
      long n = Runtime.getRuntime().freeMemory();
      long o = m - n;
      GpuDevice gpuDevice = RenderSystem.getDevice();
      List list = Lists.newArrayList(new String[]{String.format(Locale.ROOT, "Java: %s", System.getProperty("java.version")), String.format(Locale.ROOT, "Mem: %2d%% %03d/%03dMB", o * 100L / l, toMiB(o), toMiB(l)), String.format(Locale.ROOT, "Allocation rate: %03dMB/s", toMiB(this.allocationRateCalculator.get(o))), String.format(Locale.ROOT, "Allocated: %2d%% %03dMB", m * 100L / l, toMiB(m)), "", String.format(Locale.ROOT, "CPU: %s", GLX._getCpuInfo()), "", String.format(Locale.ROOT, "Display: %dx%d (%s)", MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight(), gpuDevice.getVendor()), gpuDevice.getRenderer(), String.format(Locale.ROOT, "%s %s", gpuDevice.getBackendName(), gpuDevice.getVersion())});
      if (this.client.hasReducedDebugInfo()) {
         return list;
      } else {
         BlockPos blockPos;
         Iterator var13;
         Map.Entry entry;
         Stream var10000;
         String var10001;
         if (this.blockHit.getType() == HitResult.Type.BLOCK) {
            blockPos = ((BlockHitResult)this.blockHit).getBlockPos();
            BlockState blockState = this.client.world.getBlockState(blockPos);
            list.add("");
            var10001 = String.valueOf(Formatting.UNDERLINE);
            list.add(var10001 + "Targeted Block: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
            list.add(String.valueOf(Registries.BLOCK.getId(blockState.getBlock())));
            var13 = blockState.getEntries().entrySet().iterator();

            while(var13.hasNext()) {
               entry = (Map.Entry)var13.next();
               list.add(this.propertyToString(entry));
            }

            var10000 = blockState.streamTags().map((tag) -> {
               return "#" + String.valueOf(tag.id());
            });
            Objects.requireNonNull(list);
            var10000.forEach(list::add);
         }

         if (this.fluidHit.getType() == HitResult.Type.BLOCK) {
            blockPos = ((BlockHitResult)this.fluidHit).getBlockPos();
            FluidState fluidState = this.client.world.getFluidState(blockPos);
            list.add("");
            var10001 = String.valueOf(Formatting.UNDERLINE);
            list.add(var10001 + "Targeted Fluid: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
            list.add(String.valueOf(Registries.FLUID.getId(fluidState.getFluid())));
            var13 = fluidState.getEntries().entrySet().iterator();

            while(var13.hasNext()) {
               entry = (Map.Entry)var13.next();
               list.add(this.propertyToString(entry));
            }

            var10000 = fluidState.streamTags().map((tag) -> {
               return "#" + String.valueOf(tag.id());
            });
            Objects.requireNonNull(list);
            var10000.forEach(list::add);
         }

         Entity entity = this.client.targetedEntity;
         if (entity != null) {
            list.add("");
            list.add(String.valueOf(Formatting.UNDERLINE) + "Targeted Entity");
            list.add(String.valueOf(Registries.ENTITY_TYPE.getId(entity.getType())));
         }

         return list;
      }
   }

   private String propertyToString(Map.Entry propEntry) {
      Property property = (Property)propEntry.getKey();
      Comparable comparable = (Comparable)propEntry.getValue();
      String string = Util.getValueAsString(property, comparable);
      String var10000;
      if (Boolean.TRUE.equals(comparable)) {
         var10000 = String.valueOf(Formatting.GREEN);
         string = var10000 + string;
      } else if (Boolean.FALSE.equals(comparable)) {
         var10000 = String.valueOf(Formatting.RED);
         string = var10000 + string;
      }

      var10000 = property.getName();
      return var10000 + ": " + string;
   }

   private static long toMiB(long bytes) {
      return bytes / 1024L / 1024L;
   }

   public boolean shouldShowDebugHud() {
      return this.showDebugHud && !this.client.options.hudHidden;
   }

   public boolean shouldShowRenderingChart() {
      return this.shouldShowDebugHud() && this.renderingChartVisible;
   }

   public boolean shouldShowPacketSizeAndPingCharts() {
      return this.shouldShowDebugHud() && this.packetSizeAndPingChartsVisible;
   }

   public boolean shouldRenderTickCharts() {
      return this.shouldShowDebugHud() && this.renderingAndTickChartsVisible;
   }

   public void toggleDebugHud() {
      this.showDebugHud = !this.showDebugHud;
   }

   public void togglePacketSizeAndPingCharts() {
      this.packetSizeAndPingChartsVisible = !this.showDebugHud || !this.packetSizeAndPingChartsVisible;
      if (this.packetSizeAndPingChartsVisible) {
         this.showDebugHud = true;
         this.renderingAndTickChartsVisible = false;
      }

   }

   public void toggleRenderingAndTickCharts() {
      this.renderingAndTickChartsVisible = !this.showDebugHud || !this.renderingAndTickChartsVisible;
      if (this.renderingAndTickChartsVisible) {
         this.showDebugHud = true;
         this.packetSizeAndPingChartsVisible = false;
      }

   }

   public void toggleRenderingChart() {
      this.renderingChartVisible = !this.showDebugHud || !this.renderingChartVisible;
      if (this.renderingChartVisible) {
         this.showDebugHud = true;
      }

   }

   public void pushToFrameLog(long value) {
      this.frameNanosLog.push(value);
   }

   public MultiValueDebugSampleLogImpl getTickNanosLog() {
      return this.tickNanosLog;
   }

   public MultiValueDebugSampleLogImpl getPingLog() {
      return this.pingLog;
   }

   public MultiValueDebugSampleLogImpl getPacketSizeLog() {
      return this.packetSizeLog;
   }

   public PieChart getPieChart() {
      return this.pieChart;
   }

   public void set(long[] values, DebugSampleType type) {
      MultiValueDebugSampleLogImpl multiValueDebugSampleLogImpl = (MultiValueDebugSampleLogImpl)this.receivedDebugSamples.get(type);
      if (multiValueDebugSampleLogImpl != null) {
         multiValueDebugSampleLogImpl.set(values);
      }

   }

   public void clear() {
      this.showDebugHud = false;
      this.tickNanosLog.clear();
      this.pingLog.clear();
      this.packetSizeLog.clear();
   }

   public void renderDebugCrosshair(Camera camera) {
      Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
      matrix4fStack.pushMatrix();
      matrix4fStack.translate(0.0F, 0.0F, -1.0F);
      matrix4fStack.rotateX(camera.getPitch() * 0.017453292F);
      matrix4fStack.rotateY(camera.getYaw() * 0.017453292F);
      float f = 0.01F * (float)this.client.getWindow().getScaleFactor();
      matrix4fStack.scale(-f, f, -f);
      RenderPipeline renderPipeline = RenderPipelines.LINES;
      Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
      GpuTextureView gpuTextureView = framebuffer.getColorAttachmentView();
      GpuTextureView gpuTextureView2 = framebuffer.getDepthAttachmentView();
      GpuBuffer gpuBuffer = this.debugCrosshairIndexBuffer.getIndexBuffer(18);
      GpuBufferSlice[] gpuBufferSlices = RenderSystem.getDynamicUniforms().writeAll(new DynamicUniforms.UniformValue(new Matrix4f(matrix4fStack), new Vector4f(0.0F, 0.0F, 0.0F, 1.0F), new Vector3f(), new Matrix4f(), 4.0F), new DynamicUniforms.UniformValue(new Matrix4f(matrix4fStack), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f(), 2.0F));
      RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
         return "3d crosshair";
      }, gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());

      try {
         renderPass.setPipeline(renderPipeline);
         RenderSystem.bindDefaultUniforms(renderPass);
         renderPass.setVertexBuffer(0, this.debugCrosshairBuffer);
         renderPass.setIndexBuffer(gpuBuffer, this.debugCrosshairIndexBuffer.getIndexType());
         renderPass.setUniform("DynamicTransforms", gpuBufferSlices[0]);
         renderPass.drawIndexed(0, 0, 18, 1);
         renderPass.setUniform("DynamicTransforms", gpuBufferSlices[1]);
         renderPass.drawIndexed(0, 0, 18, 1);
      } catch (Throwable var14) {
         if (renderPass != null) {
            try {
               renderPass.close();
            } catch (Throwable var13) {
               var14.addSuppressed(var13);
            }
         }

         throw var14;
      }

      if (renderPass != null) {
         renderPass.close();
      }

      matrix4fStack.popMatrix();
   }

   static {
      HEIGHT_MAP_TYPES = Maps.newEnumMap(Map.of(Heightmap.Type.WORLD_SURFACE_WG, "SW", Heightmap.Type.WORLD_SURFACE, "S", Heightmap.Type.OCEAN_FLOOR_WG, "OW", Heightmap.Type.OCEAN_FLOOR, "O", Heightmap.Type.MOTION_BLOCKING, "M", Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, "ML"));
   }

   @Environment(EnvType.CLIENT)
   private static class AllocationRateCalculator {
      private static final int INTERVAL = 500;
      private static final List GARBAGE_COLLECTORS = ManagementFactory.getGarbageCollectorMXBeans();
      private long lastCalculated = 0L;
      private long allocatedBytes = -1L;
      private long collectionCount = -1L;
      private long allocationRate = 0L;

      AllocationRateCalculator() {
      }

      long get(long allocatedBytes) {
         long l = System.currentTimeMillis();
         if (l - this.lastCalculated < 500L) {
            return this.allocationRate;
         } else {
            long m = getCollectionCount();
            if (this.lastCalculated != 0L && m == this.collectionCount) {
               double d = (double)TimeUnit.SECONDS.toMillis(1L) / (double)(l - this.lastCalculated);
               long n = allocatedBytes - this.allocatedBytes;
               this.allocationRate = Math.round((double)n * d);
            }

            this.lastCalculated = l;
            this.allocatedBytes = allocatedBytes;
            this.collectionCount = m;
            return this.allocationRate;
         }
      }

      private static long getCollectionCount() {
         long l = 0L;

         GarbageCollectorMXBean garbageCollectorMXBean;
         for(Iterator var2 = GARBAGE_COLLECTORS.iterator(); var2.hasNext(); l += garbageCollectorMXBean.getCollectionCount()) {
            garbageCollectorMXBean = (GarbageCollectorMXBean)var2.next();
         }

         return l;
      }
   }
}
