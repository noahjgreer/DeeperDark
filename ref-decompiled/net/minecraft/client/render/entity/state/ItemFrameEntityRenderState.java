package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ItemFrameEntityRenderState extends EntityRenderState {
   public Direction facing;
   public final ItemRenderState itemRenderState;
   public int rotation;
   public boolean glow;
   @Nullable
   public MapIdComponent mapId;
   public final MapRenderState mapRenderState;

   public ItemFrameEntityRenderState() {
      this.facing = Direction.NORTH;
      this.itemRenderState = new ItemRenderState();
      this.mapRenderState = new MapRenderState();
   }
}
