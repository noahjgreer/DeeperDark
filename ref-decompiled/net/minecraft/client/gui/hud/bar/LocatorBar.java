package net.minecraft.client.gui.hud.bar;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.resource.waypoint.WaypointStyleAsset;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.minecraft.world.waypoint.Waypoint;

@Environment(EnvType.CLIENT)
public class LocatorBar implements Bar {
   private static final Identifier BACKGROUND = Identifier.ofVanilla("hud/locator_bar_background");
   private static final Identifier ARROW_UP = Identifier.ofVanilla("hud/locator_bar_arrow_up");
   private static final Identifier ARROW_DOWN = Identifier.ofVanilla("hud/locator_bar_arrow_down");
   private static final int field_59852 = 9;
   private static final int field_59854 = 60;
   private static final int field_59855 = 7;
   private static final int field_59856 = 5;
   private static final int field_60309 = 1;
   private static final int field_60453 = 1;
   private final MinecraftClient client;

   public LocatorBar(MinecraftClient client) {
      this.client = client;
   }

   public void renderBar(DrawContext context, RenderTickCounter tickCounter) {
      context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.getCenterX(this.client.getWindow()), this.getCenterY(this.client.getWindow()), 182, 5);
   }

   public void renderAddons(DrawContext context, RenderTickCounter tickCounter) {
      int i = this.getCenterY(this.client.getWindow());
      World world = this.client.cameraEntity.getWorld();
      this.client.player.networkHandler.getWaypointHandler().forEachWaypoint(this.client.cameraEntity, (waypoint) -> {
         if (!(Boolean)waypoint.getSource().left().map((uuid) -> {
            return uuid.equals(this.client.cameraEntity.getUuid());
         }).orElse(false)) {
            double d = waypoint.getRelativeYaw(world, this.client.gameRenderer.getCamera());
            if (!(d <= -61.0) && !(d > 60.0)) {
               int j = MathHelper.ceil((float)(context.getScaledWindowWidth() - 9) / 2.0F);
               Waypoint.Config config = waypoint.getConfig();
               WaypointStyleAsset waypointStyleAsset = this.client.getWaypointStyleAssetManager().get(config.style);
               float f = MathHelper.sqrt((float)waypoint.squaredDistanceTo(this.client.cameraEntity));
               Identifier identifier = waypointStyleAsset.getSpriteForDistance(f);
               int k = (Integer)config.color.orElseGet(() -> {
                  return (Integer)waypoint.getSource().map((uuid) -> {
                     return ColorHelper.withBrightness(ColorHelper.withAlpha(255, uuid.hashCode()), 0.9F);
                  }, (name) -> {
                     return ColorHelper.withBrightness(ColorHelper.withAlpha(255, name.hashCode()), 0.9F);
                  });
               });
               int l = (int)(d * 173.0 / 2.0 / 60.0);
               context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, j + l, i - 2, 9, 9, k);
               TrackedWaypoint.Pitch pitch = waypoint.getPitch(world, this.client.gameRenderer);
               if (pitch != TrackedWaypoint.Pitch.NONE) {
                  byte m;
                  Identifier identifier2;
                  if (pitch == TrackedWaypoint.Pitch.DOWN) {
                     m = 6;
                     identifier2 = ARROW_DOWN;
                  } else {
                     m = -6;
                     identifier2 = ARROW_UP;
                  }

                  context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier2, j + l + 1, i + m, 7, 5);
               }

            }
         }
      });
   }
}
