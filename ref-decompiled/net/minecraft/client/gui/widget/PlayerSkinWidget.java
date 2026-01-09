package net.minecraft.client.gui.widget;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PlayerSkinWidget extends ClickableWidget {
   private static final float field_45997 = 2.125F;
   private static final float field_59833 = 0.97F;
   private static final float field_45999 = 2.5F;
   private static final float field_46000 = -5.0F;
   private static final float field_46001 = 30.0F;
   private static final float field_46002 = 50.0F;
   private final PlayerEntityModel wideModel;
   private final PlayerEntityModel slimModel;
   private final Supplier skinSupplier;
   private float xRotation = -5.0F;
   private float yRotation = 30.0F;

   public PlayerSkinWidget(int width, int height, LoadedEntityModels entityModels, Supplier skinSupplier) {
      super(0, 0, width, height, ScreenTexts.EMPTY);
      this.wideModel = new PlayerEntityModel(entityModels.getModelPart(EntityModelLayers.PLAYER), false);
      this.slimModel = new PlayerEntityModel(entityModels.getModelPart(EntityModelLayers.PLAYER_SLIM), true);
      this.skinSupplier = skinSupplier;
   }

   protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      float f = 0.97F * (float)this.getHeight() / 2.125F;
      float g = -1.0625F;
      SkinTextures skinTextures = (SkinTextures)this.skinSupplier.get();
      PlayerEntityModel playerEntityModel = skinTextures.model() == SkinTextures.Model.SLIM ? this.slimModel : this.wideModel;
      context.addPlayerSkin(playerEntityModel, skinTextures.texture(), f, this.xRotation, this.yRotation, -1.0625F, this.getX(), this.getY(), this.getRight(), this.getBottom());
   }

   protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
      this.xRotation = MathHelper.clamp(this.xRotation - (float)deltaY * 2.5F, -50.0F, 50.0F);
      this.yRotation += (float)deltaX * 2.5F;
   }

   public void playDownSound(SoundManager soundManager) {
   }

   protected void appendClickableNarrations(NarrationMessageBuilder builder) {
   }

   public boolean isNarratable() {
      return false;
   }

   @Nullable
   public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
      return null;
   }
}
