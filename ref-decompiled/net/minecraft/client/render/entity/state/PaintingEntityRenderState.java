package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PaintingEntityRenderState extends EntityRenderState {
   public Direction facing;
   @Nullable
   public PaintingVariant variant;
   public int[] lightmapCoordinates;

   public PaintingEntityRenderState() {
      this.facing = Direction.NORTH;
      this.lightmapCoordinates = new int[0];
   }
}
