package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class SignEditScreen extends AbstractSignEditScreen {
   public static final float BACKGROUND_SCALE = 62.500004F;
   public static final float TEXT_SCALE_MULTIPLIER = 0.9765628F;
   private static final Vector3f TEXT_SCALE = new Vector3f(0.9765628F, 0.9765628F, 0.9765628F);
   @Nullable
   private Model model;

   public SignEditScreen(SignBlockEntity sign, boolean filtered, boolean bl) {
      super(sign, filtered, bl);
   }

   protected void init() {
      super.init();
      boolean bl = this.blockEntity.getCachedState().getBlock() instanceof SignBlock;
      this.model = SignBlockEntityRenderer.createSignModel(this.client.getLoadedEntityModels(), this.signType, bl);
   }

   protected float getYOffset() {
      return 90.0F;
   }

   protected void renderSignBackground(DrawContext context) {
      if (this.model != null) {
         int i = this.width / 2;
         int j = i - 48;
         int k = true;
         int l = i + 48;
         int m = true;
         context.addSign(this.model, 62.500004F, this.signType, j, 66, l, 168);
      }
   }

   protected Vector3f getTextScale() {
      return TEXT_SCALE;
   }
}
