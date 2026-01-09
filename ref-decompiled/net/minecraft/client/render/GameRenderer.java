package net.minecraft.client.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlobalSettings;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.gui.render.BannerResultGuiElementRenderer;
import net.minecraft.client.gui.render.BookModelGuiElementRenderer;
import net.minecraft.client.gui.render.EntityGuiElementRenderer;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.PlayerSkinGuiElementRenderer;
import net.minecraft.client.gui.render.ProfilerChartGuiElementRenderer;
import net.minecraft.client.gui.render.SignGuiElementRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.Pool;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import net.minecraft.world.GameMode;
import net.minecraft.world.waypoint.TrackedWaypoint;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class GameRenderer implements TrackedWaypoint.PitchProvider, AutoCloseable {
   private static final Identifier BLUR_ID = Identifier.ofVanilla("blur");
   public static final int field_49904 = 10;
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final float CAMERA_DEPTH = 0.05F;
   public static final float field_60107 = 100.0F;
   private static final float field_55869 = 20.0F;
   private static final float field_55870 = 7.0F;
   private final MinecraftClient client;
   private final Random random = Random.create();
   private float viewDistanceBlocks;
   public final HeldItemRenderer firstPersonRenderer;
   private final InGameOverlayRenderer overlayRenderer;
   private final BufferBuilderStorage buffers;
   private float nauseaEffectTime;
   private float nauseaEffectSpeed;
   private float fovMultiplier;
   private float lastFovMultiplier;
   private float skyDarkness;
   private float lastSkyDarkness;
   private boolean blockOutlineEnabled = true;
   private long lastWorldIconUpdate;
   private boolean hasWorldIcon;
   private long lastWindowFocusedTime = Util.getMeasuringTimeMs();
   private final LightmapTextureManager lightmapTextureManager;
   private final OverlayTexture overlayTexture = new OverlayTexture();
   private boolean renderingPanorama;
   protected final CubeMapRenderer panoramaRenderer = new CubeMapRenderer(Identifier.ofVanilla("textures/gui/title/background/panorama"));
   protected final RotatingCubeMapRenderer rotatingPanoramaRenderer;
   private final Pool pool;
   private final FogRenderer fogRenderer;
   private final GuiRenderer guiRenderer;
   private final GuiRenderState guiState;
   @Nullable
   private Identifier postProcessorId;
   private boolean postProcessorEnabled;
   private final Camera camera;
   private final DiffuseLighting diffuseLighting;
   private final GlobalSettings globalSettings;
   private final RawProjectionMatrix worldProjectionMatrix;
   private final ProjectionMatrix3 hudProjectionMatrix;

   public GameRenderer(MinecraftClient client, HeldItemRenderer firstPersonHeldItemRenderer, BufferBuilderStorage buffers) {
      this.rotatingPanoramaRenderer = new RotatingCubeMapRenderer(this.panoramaRenderer);
      this.pool = new Pool(3);
      this.fogRenderer = new FogRenderer();
      this.camera = new Camera();
      this.diffuseLighting = new DiffuseLighting();
      this.globalSettings = new GlobalSettings();
      this.worldProjectionMatrix = new RawProjectionMatrix("level");
      this.hudProjectionMatrix = new ProjectionMatrix3("3d hud", 0.05F, 100.0F);
      this.client = client;
      this.firstPersonRenderer = firstPersonHeldItemRenderer;
      this.lightmapTextureManager = new LightmapTextureManager(this, client);
      this.buffers = buffers;
      this.guiState = new GuiRenderState();
      VertexConsumerProvider.Immediate immediate = buffers.getEntityVertexConsumers();
      this.guiRenderer = new GuiRenderer(this.guiState, immediate, List.of(new EntityGuiElementRenderer(immediate, client.getEntityRenderDispatcher()), new PlayerSkinGuiElementRenderer(immediate), new BookModelGuiElementRenderer(immediate), new BannerResultGuiElementRenderer(immediate), new SignGuiElementRenderer(immediate), new ProfilerChartGuiElementRenderer(immediate)));
      this.overlayRenderer = new InGameOverlayRenderer(client, immediate);
   }

   public void close() {
      this.globalSettings.close();
      this.lightmapTextureManager.close();
      this.overlayTexture.close();
      this.pool.close();
      this.guiRenderer.close();
      this.worldProjectionMatrix.close();
      this.hudProjectionMatrix.close();
      this.diffuseLighting.close();
      this.panoramaRenderer.close();
      this.fogRenderer.close();
   }

   public void setBlockOutlineEnabled(boolean blockOutlineEnabled) {
      this.blockOutlineEnabled = blockOutlineEnabled;
   }

   public void setRenderingPanorama(boolean renderingPanorama) {
      this.renderingPanorama = renderingPanorama;
   }

   public boolean isRenderingPanorama() {
      return this.renderingPanorama;
   }

   public void clearPostProcessor() {
      this.postProcessorId = null;
   }

   public void togglePostProcessorEnabled() {
      this.postProcessorEnabled = !this.postProcessorEnabled;
   }

   public void onCameraEntitySet(@Nullable Entity entity) {
      this.postProcessorId = null;
      if (entity instanceof CreeperEntity) {
         this.setPostProcessor(Identifier.ofVanilla("creeper"));
      } else if (entity instanceof SpiderEntity) {
         this.setPostProcessor(Identifier.ofVanilla("spider"));
      } else if (entity instanceof EndermanEntity) {
         this.setPostProcessor(Identifier.ofVanilla("invert"));
      }

   }

   private void setPostProcessor(Identifier id) {
      this.postProcessorId = id;
      this.postProcessorEnabled = true;
   }

   public void renderBlur() {
      PostEffectProcessor postEffectProcessor = this.client.getShaderLoader().loadPostEffect(BLUR_ID, DefaultFramebufferSet.MAIN_ONLY);
      if (postEffectProcessor != null) {
         postEffectProcessor.render(this.client.getFramebuffer(), this.pool);
      }

   }

   public void preloadPrograms(ResourceFactory factory) {
      GpuDevice gpuDevice = RenderSystem.getDevice();
      BiFunction biFunction = (id, type) -> {
         Identifier identifier = type.idConverter().toResourcePath(id);

         try {
            Reader reader = factory.getResourceOrThrow(identifier).getReader();

            String var5;
            try {
               var5 = IOUtils.toString(reader);
            } catch (Throwable var8) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (reader != null) {
               reader.close();
            }

            return var5;
         } catch (IOException var9) {
            LOGGER.error("Coudln't preload {} shader {}: {}", new Object[]{type, id, var9});
            return null;
         }
      };
      gpuDevice.precompilePipeline(RenderPipelines.GUI, biFunction);
      gpuDevice.precompilePipeline(RenderPipelines.GUI_TEXTURED, biFunction);
      if (TracyClient.isAvailable()) {
         gpuDevice.precompilePipeline(RenderPipelines.TRACY_BLIT, biFunction);
      }

   }

   public void tick() {
      this.updateFovMultiplier();
      this.lightmapTextureManager.tick();
      ClientPlayerEntity clientPlayerEntity = this.client.player;
      if (this.client.getCameraEntity() == null) {
         this.client.setCameraEntity(clientPlayerEntity);
      }

      this.camera.updateEyeHeight();
      this.firstPersonRenderer.updateHeldItems();
      float f = clientPlayerEntity.nauseaIntensity;
      float g = clientPlayerEntity.getEffectFadeFactor(StatusEffects.NAUSEA, 1.0F);
      if (!(f > 0.0F) && !(g > 0.0F)) {
         this.nauseaEffectSpeed = 0.0F;
      } else {
         this.nauseaEffectSpeed = (f * 20.0F + g * 7.0F) / (f + g);
         this.nauseaEffectTime += this.nauseaEffectSpeed;
      }

      if (this.client.world.getTickManager().shouldTick()) {
         this.client.worldRenderer.addWeatherParticlesAndSound(this.camera);
         this.lastSkyDarkness = this.skyDarkness;
         if (this.client.inGameHud.getBossBarHud().shouldDarkenSky()) {
            this.skyDarkness += 0.05F;
            if (this.skyDarkness > 1.0F) {
               this.skyDarkness = 1.0F;
            }
         } else if (this.skyDarkness > 0.0F) {
            this.skyDarkness -= 0.0125F;
         }

         this.overlayRenderer.tickFloatingItemTimer();
      }
   }

   @Nullable
   public Identifier getPostProcessorId() {
      return this.postProcessorId;
   }

   public void onResized(int width, int height) {
      this.pool.clear();
      this.client.worldRenderer.onResized(width, height);
   }

   public void updateCrosshairTarget(float tickProgress) {
      Entity entity = this.client.getCameraEntity();
      if (entity != null) {
         if (this.client.world != null && this.client.player != null) {
            Profilers.get().push("pick");
            double d = this.client.player.getBlockInteractionRange();
            double e = this.client.player.getEntityInteractionRange();
            HitResult hitResult = this.findCrosshairTarget(entity, d, e, tickProgress);
            this.client.crosshairTarget = hitResult;
            MinecraftClient var10000 = this.client;
            Entity var10001;
            if (hitResult instanceof EntityHitResult) {
               EntityHitResult entityHitResult = (EntityHitResult)hitResult;
               var10001 = entityHitResult.getEntity();
            } else {
               var10001 = null;
            }

            var10000.targetedEntity = var10001;
            Profilers.get().pop();
         }
      }
   }

   private HitResult findCrosshairTarget(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickProgress) {
      double d = Math.max(blockInteractionRange, entityInteractionRange);
      double e = MathHelper.square(d);
      Vec3d vec3d = camera.getCameraPosVec(tickProgress);
      HitResult hitResult = camera.raycast(d, tickProgress, false);
      double f = hitResult.getPos().squaredDistanceTo(vec3d);
      if (hitResult.getType() != HitResult.Type.MISS) {
         e = f;
         d = Math.sqrt(f);
      }

      Vec3d vec3d2 = camera.getRotationVec(tickProgress);
      Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
      float g = 1.0F;
      Box box = camera.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
      EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, vec3d, vec3d3, box, EntityPredicates.CAN_HIT, e);
      return entityHitResult != null && entityHitResult.getPos().squaredDistanceTo(vec3d) < f ? ensureTargetInRange(entityHitResult, vec3d, entityInteractionRange) : ensureTargetInRange(hitResult, vec3d, blockInteractionRange);
   }

   private static HitResult ensureTargetInRange(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
      Vec3d vec3d = hitResult.getPos();
      if (!vec3d.isInRange(cameraPos, interactionRange)) {
         Vec3d vec3d2 = hitResult.getPos();
         Direction direction = Direction.getFacing(vec3d2.x - cameraPos.x, vec3d2.y - cameraPos.y, vec3d2.z - cameraPos.z);
         return BlockHitResult.createMissed(vec3d2, direction, BlockPos.ofFloored(vec3d2));
      } else {
         return hitResult;
      }
   }

   private void updateFovMultiplier() {
      Entity var3 = this.client.getCameraEntity();
      float g;
      if (var3 instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
         GameOptions gameOptions = this.client.options;
         boolean bl = gameOptions.getPerspective().isFirstPerson();
         float f = ((Double)gameOptions.getFovEffectScale().getValue()).floatValue();
         g = abstractClientPlayerEntity.getFovMultiplier(bl, f);
      } else {
         g = 1.0F;
      }

      this.lastFovMultiplier = this.fovMultiplier;
      this.fovMultiplier += (g - this.fovMultiplier) * 0.5F;
      this.fovMultiplier = MathHelper.clamp(this.fovMultiplier, 0.1F, 1.5F);
   }

   private float getFov(Camera camera, float tickProgress, boolean changingFov) {
      if (this.renderingPanorama) {
         return 90.0F;
      } else {
         float f = 70.0F;
         if (changingFov) {
            f = (float)(Integer)this.client.options.getFov().getValue();
            f *= MathHelper.lerp(tickProgress, this.lastFovMultiplier, this.fovMultiplier);
         }

         Entity var6 = camera.getFocusedEntity();
         float g;
         if (var6 instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)var6;
            if (livingEntity.isDead()) {
               g = Math.min((float)livingEntity.deathTime + tickProgress, 20.0F);
               f /= (1.0F - 500.0F / (g + 500.0F)) * 2.0F + 1.0F;
            }
         }

         CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
         if (cameraSubmersionType == CameraSubmersionType.LAVA || cameraSubmersionType == CameraSubmersionType.WATER) {
            g = ((Double)this.client.options.getFovEffectScale().getValue()).floatValue();
            f *= MathHelper.lerp(g, 1.0F, 0.85714287F);
         }

         return f;
      }
   }

   private void tiltViewWhenHurt(MatrixStack matrices, float tickProgress) {
      Entity var4 = this.client.getCameraEntity();
      if (var4 instanceof LivingEntity livingEntity) {
         float f = (float)livingEntity.hurtTime - tickProgress;
         float g;
         if (livingEntity.isDead()) {
            g = Math.min((float)livingEntity.deathTime + tickProgress, 20.0F);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(40.0F - 8000.0F / (g + 200.0F)));
         }

         if (f < 0.0F) {
            return;
         }

         f /= (float)livingEntity.maxHurtTime;
         f = MathHelper.sin(f * f * f * f * 3.1415927F);
         g = livingEntity.getDamageTiltYaw();
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-g));
         float h = (float)((double)(-f) * 14.0 * (Double)this.client.options.getDamageTiltStrength().getValue());
         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(h));
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
      }

   }

   private void bobView(MatrixStack matrices, float tickProgress) {
      Entity var4 = this.client.getCameraEntity();
      if (var4 instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
         float f = abstractClientPlayerEntity.distanceMoved - abstractClientPlayerEntity.lastDistanceMoved;
         float g = -(abstractClientPlayerEntity.distanceMoved + f * tickProgress);
         float h = MathHelper.lerp(tickProgress, abstractClientPlayerEntity.lastStrideDistance, abstractClientPlayerEntity.strideDistance);
         matrices.translate(MathHelper.sin(g * 3.1415927F) * h * 0.5F, -Math.abs(MathHelper.cos(g * 3.1415927F) * h), 0.0F);
         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(g * 3.1415927F) * h * 3.0F));
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Math.abs(MathHelper.cos(g * 3.1415927F - 0.2F) * h) * 5.0F));
      }
   }

   private void renderHand(float tickProgress, boolean sleeping, Matrix4f positionMatrix) {
      if (!this.renderingPanorama) {
         MatrixStack matrixStack = new MatrixStack();
         matrixStack.push();
         matrixStack.multiplyPositionMatrix(positionMatrix.invert(new Matrix4f()));
         Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
         matrix4fStack.pushMatrix().mul(positionMatrix);
         this.tiltViewWhenHurt(matrixStack, tickProgress);
         if ((Boolean)this.client.options.getBobView().getValue()) {
            this.bobView(matrixStack, tickProgress);
         }

         if (this.client.options.getPerspective().isFirstPerson() && !sleeping && !this.client.options.hudHidden && this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) {
            this.lightmapTextureManager.enable();
            this.firstPersonRenderer.renderItem(tickProgress, matrixStack, this.buffers.getEntityVertexConsumers(), this.client.player, this.client.getEntityRenderDispatcher().getLight(this.client.player, tickProgress));
            this.lightmapTextureManager.disable();
         }

         matrix4fStack.popMatrix();
         matrixStack.pop();
      }
   }

   public Matrix4f getBasicProjectionMatrix(float fovDegrees) {
      Matrix4f matrix4f = new Matrix4f();
      return matrix4f.perspective(fovDegrees * 0.017453292F, (float)this.client.getWindow().getFramebufferWidth() / (float)this.client.getWindow().getFramebufferHeight(), 0.05F, this.getFarPlaneDistance());
   }

   public float getFarPlaneDistance() {
      return Math.max(this.viewDistanceBlocks * 4.0F, (float)((Integer)this.client.options.getCloudRenderDistance().getValue() * 16));
   }

   public static float getNightVisionStrength(LivingEntity entity, float tickProgress) {
      StatusEffectInstance statusEffectInstance = entity.getStatusEffect(StatusEffects.NIGHT_VISION);
      return !statusEffectInstance.isDurationBelow(200) ? 1.0F : 0.7F + MathHelper.sin(((float)statusEffectInstance.getDuration() - tickProgress) * 3.1415927F * 0.2F) * 0.3F;
   }

   public void render(RenderTickCounter tickCounter, boolean tick) {
      if (!this.client.isWindowFocused() && this.client.options.pauseOnLostFocus && (!(Boolean)this.client.options.getTouchscreen().getValue() || !this.client.mouse.wasRightButtonClicked())) {
         if (Util.getMeasuringTimeMs() - this.lastWindowFocusedTime > 500L) {
            this.client.openGameMenu(false);
         }
      } else {
         this.lastWindowFocusedTime = Util.getMeasuringTimeMs();
      }

      if (!this.client.skipGameRender) {
         this.globalSettings.set(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), (Double)this.client.options.getGlintStrength().getValue(), this.client.world == null ? 0L : this.client.world.getTime(), tickCounter, this.client.options.getMenuBackgroundBlurrinessValue());
         Profiler profiler = Profilers.get();
         boolean bl = this.client.isFinishedLoading();
         int i = (int)this.client.mouse.getScaledX(this.client.getWindow());
         int j = (int)this.client.mouse.getScaledY(this.client.getWindow());
         if (bl && tick && this.client.world != null) {
            profiler.push("world");
            this.renderWorld(tickCounter);
            this.updateWorldIcon();
            this.client.worldRenderer.drawEntityOutlinesFramebuffer();
            if (this.postProcessorId != null && this.postProcessorEnabled) {
               RenderSystem.resetTextureMatrix();
               PostEffectProcessor postEffectProcessor = this.client.getShaderLoader().loadPostEffect(this.postProcessorId, DefaultFramebufferSet.MAIN_ONLY);
               if (postEffectProcessor != null) {
                  postEffectProcessor.render(this.client.getFramebuffer(), this.pool);
               }
            }
         }

         this.fogRenderer.rotate();
         Framebuffer framebuffer = this.client.getFramebuffer();
         RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(framebuffer.getDepthAttachment(), 1.0);
         this.client.gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_3D);
         this.guiState.clear();
         DrawContext drawContext = new DrawContext(this.client, this.guiState);
         if (bl && tick && this.client.world != null) {
            profiler.swap("gui");
            this.client.inGameHud.render(drawContext, tickCounter);
            profiler.pop();
         }

         CrashReport crashReport;
         CrashReportSection crashReportSection;
         if (this.client.getOverlay() != null) {
            try {
               this.client.getOverlay().render(drawContext, i, j, tickCounter.getDynamicDeltaTicks());
            } catch (Throwable var15) {
               crashReport = CrashReport.create(var15, "Rendering overlay");
               crashReportSection = crashReport.addElement("Overlay render details");
               crashReportSection.add("Overlay name", () -> {
                  return this.client.getOverlay().getClass().getCanonicalName();
               });
               throw new CrashException(crashReport);
            }
         } else if (bl && this.client.currentScreen != null) {
            try {
               this.client.currentScreen.renderWithTooltip(drawContext, i, j, tickCounter.getDynamicDeltaTicks());
            } catch (Throwable var14) {
               crashReport = CrashReport.create(var14, "Rendering screen");
               crashReportSection = crashReport.addElement("Screen render details");
               crashReportSection.add("Screen name", () -> {
                  return this.client.currentScreen.getClass().getCanonicalName();
               });
               this.client.mouse.addCrashReportSection(crashReportSection, this.client.getWindow());
               throw new CrashException(crashReport);
            }

            try {
               if (this.client.currentScreen != null) {
                  this.client.currentScreen.updateNarrator();
               }
            } catch (Throwable var13) {
               crashReport = CrashReport.create(var13, "Narrating screen");
               crashReportSection = crashReport.addElement("Screen details");
               crashReportSection.add("Screen name", () -> {
                  return this.client.currentScreen.getClass().getCanonicalName();
               });
               throw new CrashException(crashReport);
            }
         }

         if (bl && tick && this.client.world != null) {
            this.client.inGameHud.renderAutosaveIndicator(drawContext, tickCounter);
         }

         if (bl) {
            ScopedProfiler scopedProfiler = profiler.scoped("toasts");

            try {
               this.client.getToastManager().draw(drawContext);
            } catch (Throwable var16) {
               if (scopedProfiler != null) {
                  try {
                     scopedProfiler.close();
                  } catch (Throwable var12) {
                     var16.addSuppressed(var12);
                  }
               }

               throw var16;
            }

            if (scopedProfiler != null) {
               scopedProfiler.close();
            }
         }

         this.guiRenderer.render(this.fogRenderer.getFogBuffer(FogRenderer.FogType.NONE));
         this.guiRenderer.incrementFrame();
         this.pool.decrementLifespan();
      }
   }

   private void updateWorldIcon() {
      if (!this.hasWorldIcon && this.client.isInSingleplayer()) {
         long l = Util.getMeasuringTimeMs();
         if (l - this.lastWorldIconUpdate >= 1000L) {
            this.lastWorldIconUpdate = l;
            IntegratedServer integratedServer = this.client.getServer();
            if (integratedServer != null && !integratedServer.isStopped()) {
               integratedServer.getIconFile().ifPresent((path) -> {
                  if (Files.isRegularFile(path, new LinkOption[0])) {
                     this.hasWorldIcon = true;
                  } else {
                     this.updateWorldIcon(path);
                  }

               });
            }
         }
      }
   }

   private void updateWorldIcon(Path path) {
      if (this.client.worldRenderer.getCompletedChunkCount() > 10 && this.client.worldRenderer.isTerrainRenderComplete()) {
         ScreenshotRecorder.takeScreenshot(this.client.getFramebuffer(), (screenshot) -> {
            Util.getIoWorkerExecutor().execute(() -> {
               int i = screenshot.getWidth();
               int j = screenshot.getHeight();
               int k = 0;
               int l = 0;
               if (i > j) {
                  k = (i - j) / 2;
                  i = j;
               } else {
                  l = (j - i) / 2;
                  j = i;
               }

               try {
                  NativeImage nativeImage2 = new NativeImage(64, 64, false);

                  try {
                     screenshot.resizeSubRectTo(k, l, i, j, nativeImage2);
                     nativeImage2.writeTo(path);
                  } catch (Throwable var15) {
                     try {
                        nativeImage2.close();
                     } catch (Throwable var14) {
                        var15.addSuppressed(var14);
                     }

                     throw var15;
                  }

                  nativeImage2.close();
               } catch (IOException var16) {
                  LOGGER.warn("Couldn't save auto screenshot", var16);
               } finally {
                  screenshot.close();
               }

            });
         });
      }

   }

   private boolean shouldRenderBlockOutline() {
      if (!this.blockOutlineEnabled) {
         return false;
      } else {
         Entity entity = this.client.getCameraEntity();
         boolean bl = entity instanceof PlayerEntity && !this.client.options.hudHidden;
         if (bl && !((PlayerEntity)entity).getAbilities().allowModifyWorld) {
            ItemStack itemStack = ((LivingEntity)entity).getMainHandStack();
            HitResult hitResult = this.client.crosshairTarget;
            if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
               BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
               BlockState blockState = this.client.world.getBlockState(blockPos);
               if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
                  bl = blockState.createScreenHandlerFactory(this.client.world, blockPos) != null;
               } else {
                  CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.client.world, blockPos, false);
                  Registry registry = this.client.world.getRegistryManager().getOrThrow(RegistryKeys.BLOCK);
                  bl = !itemStack.isEmpty() && (itemStack.canBreak(cachedBlockPosition) || itemStack.canPlaceOn(cachedBlockPosition));
               }
            }
         }

         return bl;
      }
   }

   public void renderWorld(RenderTickCounter renderTickCounter) {
      float f = renderTickCounter.getTickProgress(true);
      ClientPlayerEntity clientPlayerEntity = this.client.player;
      this.lightmapTextureManager.update(f);
      if (this.client.getCameraEntity() == null) {
         this.client.setCameraEntity(clientPlayerEntity);
      }

      this.updateCrosshairTarget(f);
      Profiler profiler = Profilers.get();
      profiler.push("center");
      boolean bl = this.shouldRenderBlockOutline();
      profiler.swap("camera");
      Camera camera = this.camera;
      Entity entity = this.client.getCameraEntity() == null ? clientPlayerEntity : this.client.getCameraEntity();
      float g = this.client.world.getTickManager().shouldSkipTick((Entity)entity) ? 1.0F : f;
      camera.update(this.client.world, (Entity)entity, !this.client.options.getPerspective().isFirstPerson(), this.client.options.getPerspective().isFrontView(), g);
      this.viewDistanceBlocks = (float)(this.client.options.getClampedViewDistance() * 16);
      float h = this.getFov(camera, f, true);
      Matrix4f matrix4f = this.getBasicProjectionMatrix(h);
      MatrixStack matrixStack = new MatrixStack();
      this.tiltViewWhenHurt(matrixStack, camera.getLastTickProgress());
      if ((Boolean)this.client.options.getBobView().getValue()) {
         this.bobView(matrixStack, camera.getLastTickProgress());
      }

      matrix4f.mul(matrixStack.peek().getPositionMatrix());
      float i = ((Double)this.client.options.getDistortionEffectScale().getValue()).floatValue();
      float j = MathHelper.lerp(f, clientPlayerEntity.lastNauseaIntensity, clientPlayerEntity.nauseaIntensity);
      float k = clientPlayerEntity.getEffectFadeFactor(StatusEffects.NAUSEA, f);
      float l = Math.max(j, k) * i * i;
      float m;
      if (l > 0.0F) {
         m = 5.0F / (l * l + 5.0F) - l * 0.04F;
         m *= m;
         Vector3f vector3f = new Vector3f(0.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F);
         float n = (this.nauseaEffectTime + f * this.nauseaEffectSpeed) * 0.017453292F;
         matrix4f.rotate(n, vector3f);
         matrix4f.scale(1.0F / m, 1.0F, 1.0F);
         matrix4f.rotate(-n, vector3f);
      }

      m = Math.max(h, (float)(Integer)this.client.options.getFov().getValue());
      Matrix4f matrix4f2 = this.getBasicProjectionMatrix(m);
      RenderSystem.setProjectionMatrix(this.worldProjectionMatrix.set(matrix4f), ProjectionType.PERSPECTIVE);
      Quaternionf quaternionf = camera.getRotation().conjugate(new Quaternionf());
      Matrix4f matrix4f3 = (new Matrix4f()).rotation(quaternionf);
      this.client.worldRenderer.setupFrustum(camera.getPos(), matrix4f3, matrix4f2);
      profiler.swap("fog");
      boolean bl2 = this.client.world.getDimensionEffects().useThickFog(camera.getBlockPos().getX(), camera.getBlockPos().getZ()) || this.client.inGameHud.getBossBarHud().shouldThickenFog();
      Vector4f vector4f = this.fogRenderer.applyFog(camera, this.client.options.getClampedViewDistance(), bl2, renderTickCounter, this.getSkyDarkness(f), this.client.world);
      GpuBufferSlice gpuBufferSlice = this.fogRenderer.getFogBuffer(FogRenderer.FogType.WORLD);
      profiler.swap("level");
      this.client.worldRenderer.render(this.pool, renderTickCounter, bl, camera, matrix4f3, matrix4f, gpuBufferSlice, vector4f, !bl2);
      profiler.swap("hand");
      boolean bl3 = this.client.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.client.getCameraEntity()).isSleeping();
      RenderSystem.setProjectionMatrix(this.hudProjectionMatrix.set(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), this.getFov(camera, f, false)), ProjectionType.PERSPECTIVE);
      RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(this.client.getFramebuffer().getDepthAttachment(), 1.0);
      this.renderHand(f, bl3, matrix4f3);
      profiler.swap("screen effects");
      VertexConsumerProvider.Immediate immediate = this.buffers.getEntityVertexConsumers();
      this.overlayRenderer.renderOverlays(bl3, f);
      immediate.draw();
      profiler.pop();
      RenderSystem.setShaderFog(this.fogRenderer.getFogBuffer(FogRenderer.FogType.NONE));
      if (this.client.inGameHud.shouldRenderCrosshair()) {
         this.client.getDebugHud().renderDebugCrosshair(camera);
      }

   }

   public void reset() {
      this.overlayRenderer.clearFloatingItem();
      this.client.getMapTextureManager().clear();
      this.camera.reset();
      this.hasWorldIcon = false;
   }

   public void showFloatingItem(ItemStack floatingItem) {
      this.overlayRenderer.setFloatingItem(floatingItem, this.random);
   }

   public MinecraftClient getClient() {
      return this.client;
   }

   public float getSkyDarkness(float tickProgress) {
      return MathHelper.lerp(tickProgress, this.lastSkyDarkness, this.skyDarkness);
   }

   public float getViewDistanceBlocks() {
      return this.viewDistanceBlocks;
   }

   public Camera getCamera() {
      return this.camera;
   }

   public LightmapTextureManager getLightmapTextureManager() {
      return this.lightmapTextureManager;
   }

   public OverlayTexture getOverlayTexture() {
      return this.overlayTexture;
   }

   public Vec3d project(Vec3d sourcePos) {
      Matrix4f matrix4f = this.getBasicProjectionMatrix(this.getFov(this.camera, 0.0F, true));
      Quaternionf quaternionf = this.camera.getRotation().conjugate(new Quaternionf());
      Matrix4f matrix4f2 = (new Matrix4f()).rotation(quaternionf);
      Matrix4f matrix4f3 = matrix4f.mul(matrix4f2);
      Vec3d vec3d = this.camera.getPos();
      Vec3d vec3d2 = sourcePos.subtract(vec3d);
      Vector3f vector3f = matrix4f3.transformProject(vec3d2.toVector3f());
      return new Vec3d(vector3f);
   }

   public double getPitch() {
      float f = this.camera.getPitch();
      if (f <= -90.0F) {
         return Double.NEGATIVE_INFINITY;
      } else if (f >= 90.0F) {
         return Double.POSITIVE_INFINITY;
      } else {
         float g = this.getFov(this.camera, 0.0F, true);
         return Math.tan((double)(f * 0.017453292F)) / Math.tan((double)(g / 2.0F * 0.017453292F));
      }
   }

   public GlobalSettings getGlobalSettings() {
      return this.globalSettings;
   }

   public DiffuseLighting getDiffuseLighting() {
      return this.diffuseLighting;
   }

   public void setWorld(@Nullable ClientWorld world) {
      if (world != null) {
         this.diffuseLighting.updateLevelBuffer(world.getDimensionEffects().isDarkened());
      }

   }

   public RotatingCubeMapRenderer getRotatingPanoramaRenderer() {
      return this.rotatingPanoramaRenderer;
   }
}
