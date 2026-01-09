package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.screen.recipebook.AbstractCraftingRecipeBookWidget;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class InventoryScreen extends RecipeBookScreen {
   private float mouseX;
   private float mouseY;
   private boolean mouseDown;
   private final StatusEffectsDisplay statusEffectsDisplay;

   public InventoryScreen(PlayerEntity player) {
      super(player.playerScreenHandler, new AbstractCraftingRecipeBookWidget(player.playerScreenHandler), player.getInventory(), Text.translatable("container.crafting"));
      this.titleX = 97;
      this.statusEffectsDisplay = new StatusEffectsDisplay(this);
   }

   public void handledScreenTick() {
      super.handledScreenTick();
      if (this.client.player.isInCreativeMode()) {
         this.client.setScreen(new CreativeInventoryScreen(this.client.player, this.client.player.networkHandler.getEnabledFeatures(), (Boolean)this.client.options.getOperatorItemsTab().getValue()));
      }

   }

   protected void init() {
      if (this.client.player.isInCreativeMode()) {
         this.client.setScreen(new CreativeInventoryScreen(this.client.player, this.client.player.networkHandler.getEnabledFeatures(), (Boolean)this.client.options.getOperatorItemsTab().getValue()));
      } else {
         super.init();
      }
   }

   protected ScreenPos getRecipeBookButtonPos() {
      return new ScreenPos(this.x + 104, this.height / 2 - 22);
   }

   protected void onRecipeBookToggled() {
      this.mouseDown = true;
   }

   protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
      context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, -12566464, false);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      this.statusEffectsDisplay.drawStatusEffects(context, mouseX, mouseY);
      super.render(context, mouseX, mouseY, deltaTicks);
      this.statusEffectsDisplay.drawStatusEffectTooltip(context, mouseX, mouseY);
      this.mouseX = (float)mouseX;
      this.mouseY = (float)mouseY;
   }

   public boolean showsStatusEffects() {
      return this.statusEffectsDisplay.shouldHideStatusEffectHud();
   }

   protected boolean shouldAddPaddingToGhostResult() {
      return false;
   }

   protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
      int i = this.x;
      int j = this.y;
      context.drawTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
      drawEntity(context, i + 26, j + 8, i + 75, j + 78, 30, 0.0625F, this.mouseX, this.mouseY, this.client.player);
   }

   public static void drawEntity(DrawContext context, int x1, int y1, int x2, int y2, int size, float scale, float mouseX, float mouseY, LivingEntity entity) {
      float f = (float)(x1 + x2) / 2.0F;
      float g = (float)(y1 + y2) / 2.0F;
      context.enableScissor(x1, y1, x2, y2);
      float h = (float)Math.atan((double)((f - mouseX) / 40.0F));
      float i = (float)Math.atan((double)((g - mouseY) / 40.0F));
      Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
      Quaternionf quaternionf2 = (new Quaternionf()).rotateX(i * 20.0F * 0.017453292F);
      quaternionf.mul(quaternionf2);
      float j = entity.bodyYaw;
      float k = entity.getYaw();
      float l = entity.getPitch();
      float m = entity.lastHeadYaw;
      float n = entity.headYaw;
      entity.bodyYaw = 180.0F + h * 20.0F;
      entity.setYaw(180.0F + h * 40.0F);
      entity.setPitch(-i * 20.0F);
      entity.headYaw = entity.getYaw();
      entity.lastHeadYaw = entity.getYaw();
      float o = entity.getScale();
      Vector3f vector3f = new Vector3f(0.0F, entity.getHeight() / 2.0F + scale * o, 0.0F);
      float p = (float)size / o;
      drawEntity(context, x1, y1, x2, y2, p, vector3f, quaternionf, quaternionf2, entity);
      entity.bodyYaw = j;
      entity.setYaw(k);
      entity.setPitch(l);
      entity.lastHeadYaw = m;
      entity.headYaw = n;
      context.disableScissor();
   }

   public static void drawEntity(DrawContext drawer, int x1, int y1, int x2, int y2, float scale, Vector3f translation, Quaternionf rotation, @Nullable Quaternionf overrideCameraAngle, LivingEntity entity) {
      EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
      EntityRenderer entityRenderer = entityRenderDispatcher.getRenderer((Entity)entity);
      EntityRenderState entityRenderState = entityRenderer.getAndUpdateRenderState(entity, 1.0F);
      entityRenderState.hitbox = null;
      drawer.addEntity(entityRenderState, scale, translation, rotation, overrideCameraAngle, x1, y1, x2, y2);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (this.mouseDown) {
         this.mouseDown = false;
         return true;
      } else {
         return super.mouseReleased(mouseX, mouseY, button);
      }
   }
}
