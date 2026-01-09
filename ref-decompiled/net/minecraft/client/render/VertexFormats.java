package net.minecraft.client.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VertexFormats {
   public static final VertexFormat BLIT_SCREEN;
   public static final VertexFormat EMPTY;
   public static final VertexFormat POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
   public static final VertexFormat POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL;
   public static final VertexFormat POSITION_TEXTURE_COLOR_LIGHT;
   public static final VertexFormat POSITION;
   public static final VertexFormat POSITION_COLOR;
   public static final VertexFormat POSITION_COLOR_NORMAL;
   public static final VertexFormat POSITION_COLOR_LIGHT;
   public static final VertexFormat POSITION_TEXTURE;
   public static final VertexFormat POSITION_TEXTURE_COLOR;
   public static final VertexFormat POSITION_COLOR_TEXTURE_LIGHT;
   public static final VertexFormat POSITION_TEXTURE_LIGHT_COLOR;
   public static final VertexFormat POSITION_TEXTURE_COLOR_NORMAL;

   static {
      BLIT_SCREEN = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).build();
      EMPTY = VertexFormat.builder().build();
      POSITION_COLOR_TEXTURE_LIGHT_NORMAL = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("Color", VertexFormatElement.COLOR).add("UV0", VertexFormatElement.UV0).add("UV2", VertexFormatElement.UV2).add("Normal", VertexFormatElement.NORMAL).padding(1).build();
      POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("Color", VertexFormatElement.COLOR).add("UV0", VertexFormatElement.UV0).add("UV1", VertexFormatElement.UV1).add("UV2", VertexFormatElement.UV2).add("Normal", VertexFormatElement.NORMAL).padding(1).build();
      POSITION_TEXTURE_COLOR_LIGHT = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("UV0", VertexFormatElement.UV0).add("Color", VertexFormatElement.COLOR).add("UV2", VertexFormatElement.UV2).build();
      POSITION = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).build();
      POSITION_COLOR = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("Color", VertexFormatElement.COLOR).build();
      POSITION_COLOR_NORMAL = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("Color", VertexFormatElement.COLOR).add("Normal", VertexFormatElement.NORMAL).padding(1).build();
      POSITION_COLOR_LIGHT = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("Color", VertexFormatElement.COLOR).add("UV2", VertexFormatElement.UV2).build();
      POSITION_TEXTURE = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("UV0", VertexFormatElement.UV0).build();
      POSITION_TEXTURE_COLOR = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("UV0", VertexFormatElement.UV0).add("Color", VertexFormatElement.COLOR).build();
      POSITION_COLOR_TEXTURE_LIGHT = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("Color", VertexFormatElement.COLOR).add("UV0", VertexFormatElement.UV0).add("UV2", VertexFormatElement.UV2).build();
      POSITION_TEXTURE_LIGHT_COLOR = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("UV0", VertexFormatElement.UV0).add("UV2", VertexFormatElement.UV2).add("Color", VertexFormatElement.COLOR).build();
      POSITION_TEXTURE_COLOR_NORMAL = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("UV0", VertexFormatElement.UV0).add("Color", VertexFormatElement.COLOR).add("Normal", VertexFormatElement.NORMAL).padding(1).build();
   }
}
