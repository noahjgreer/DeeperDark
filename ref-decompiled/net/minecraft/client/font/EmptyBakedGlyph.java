package net.minecraft.client.font;

import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class EmptyBakedGlyph extends BakedGlyph {
   public static final EmptyBakedGlyph INSTANCE = new EmptyBakedGlyph();

   public EmptyBakedGlyph() {
      super(TextRenderLayerSet.of(Identifier.ofVanilla("")), (GpuTextureView)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   public void draw(BakedGlyph.DrawnGlyph glyph, Matrix4f matrix, VertexConsumer vertexConsumer, int light, boolean fixedZ) {
   }
}
