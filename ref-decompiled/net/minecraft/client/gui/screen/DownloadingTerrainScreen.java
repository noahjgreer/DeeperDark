package net.minecraft.client.gui.screen;

import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class DownloadingTerrainScreen extends Screen {
   private static final Text TEXT = Text.translatable("multiplayer.downloadingTerrain");
   private static final long MIN_LOAD_TIME_MS = 30000L;
   private final long loadStartTime;
   private final BooleanSupplier shouldClose;
   private final WorldEntryReason worldEntryReason;
   @Nullable
   private Sprite backgroundSprite;

   public DownloadingTerrainScreen(BooleanSupplier shouldClose, WorldEntryReason worldEntryReason) {
      super(NarratorManager.EMPTY);
      this.shouldClose = shouldClose;
      this.worldEntryReason = worldEntryReason;
      this.loadStartTime = Util.getMeasuringTimeMs();
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected boolean hasUsageText() {
      return false;
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      context.drawCenteredTextWithShadow(this.textRenderer, (Text)TEXT, this.width / 2, this.height / 2 - 50, -1);
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      switch (this.worldEntryReason.ordinal()) {
         case 0:
            context.drawSpriteStretched(RenderPipelines.GUI_OPAQUE_TEX_BG, this.getBackgroundSprite(), 0, 0, context.getScaledWindowWidth(), context.getScaledWindowHeight());
            break;
         case 1:
            TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
            TextureSetup textureSetup = TextureSetup.of(textureManager.getTexture(EndPortalBlockEntityRenderer.SKY_TEXTURE).getGlTextureView(), textureManager.getTexture(EndPortalBlockEntityRenderer.PORTAL_TEXTURE).getGlTextureView());
            context.fill(RenderPipelines.END_PORTAL, textureSetup, 0, 0, this.width, this.height);
            break;
         case 2:
            this.renderPanoramaBackground(context, deltaTicks);
            this.applyBlur(context);
            this.renderDarkening(context);
      }

   }

   private Sprite getBackgroundSprite() {
      if (this.backgroundSprite != null) {
         return this.backgroundSprite;
      } else {
         this.backgroundSprite = this.client.getBlockRenderManager().getModels().getModelParticleSprite(Blocks.NETHER_PORTAL.getDefaultState());
         return this.backgroundSprite;
      }
   }

   public void tick() {
      if (this.shouldClose.getAsBoolean() || Util.getMeasuringTimeMs() > this.loadStartTime + 30000L) {
         this.close();
      }

   }

   public void close() {
      this.client.getNarratorManager().narrateSystemImmediately((Text)Text.translatable("narrator.ready_to_play"));
      super.close();
   }

   public boolean shouldPause() {
      return false;
   }

   @Environment(EnvType.CLIENT)
   public static enum WorldEntryReason {
      NETHER_PORTAL,
      END_PORTAL,
      OTHER;

      // $FF: synthetic method
      private static WorldEntryReason[] method_59839() {
         return new WorldEntryReason[]{NETHER_PORTAL, END_PORTAL, OTHER};
      }
   }
}
