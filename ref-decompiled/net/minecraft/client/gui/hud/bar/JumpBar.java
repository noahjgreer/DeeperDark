package net.minecraft.client.gui.hud.bar;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.JumpingMount;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class JumpBar implements Bar {
   private static final Identifier BACKGROUND = Identifier.ofVanilla("hud/jump_bar_background");
   private static final Identifier COOLDOWN = Identifier.ofVanilla("hud/jump_bar_cooldown");
   private static final Identifier PROGRESS = Identifier.ofVanilla("hud/jump_bar_progress");
   private final MinecraftClient client;
   private final JumpingMount jumpingMount;

   public JumpBar(MinecraftClient client) {
      this.client = client;
      this.jumpingMount = ((ClientPlayerEntity)Objects.requireNonNull(client.player)).getJumpingMount();
   }

   public void renderBar(DrawContext context, RenderTickCounter tickCounter) {
      int i = this.getCenterX(this.client.getWindow());
      int j = this.getCenterY(this.client.getWindow());
      context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND, i, j, 182, 5);
      if (this.jumpingMount.getJumpCooldown() > 0) {
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, COOLDOWN, i, j, 182, 5);
      } else {
         int k = (int)(this.client.player.getMountJumpStrength() * 183.0F);
         if (k > 0) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PROGRESS, 182, 5, 0, 0, i, j, k, 5);
         }

      }
   }

   public void renderAddons(DrawContext context, RenderTickCounter tickCounter) {
   }
}
