package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

@Environment(EnvType.CLIENT)
public abstract class RenderPhase {
   public static final double field_42230 = 8.0;
   protected final String name;
   private final Runnable beginAction;
   private final Runnable endAction;
   public static final Texture MIPMAP_BLOCK_ATLAS_TEXTURE;
   public static final Texture BLOCK_ATLAS_TEXTURE;
   public static final TextureBase NO_TEXTURE;
   public static final Texturing DEFAULT_TEXTURING;
   public static final Texturing GLINT_TEXTURING;
   public static final Texturing ENTITY_GLINT_TEXTURING;
   public static final Texturing ARMOR_ENTITY_GLINT_TEXTURING;
   public static final Lightmap ENABLE_LIGHTMAP;
   public static final Lightmap DISABLE_LIGHTMAP;
   public static final Overlay ENABLE_OVERLAY_COLOR;
   public static final Overlay DISABLE_OVERLAY_COLOR;
   public static final Layering NO_LAYERING;
   public static final Layering VIEW_OFFSET_Z_LAYERING;
   public static final Layering VIEW_OFFSET_Z_LAYERING_FORWARD;
   public static final Target MAIN_TARGET;
   public static final Target OUTLINE_TARGET;
   public static final Target TRANSLUCENT_TARGET;
   public static final Target PARTICLES_TARGET;
   public static final Target WEATHER_TARGET;
   public static final Target ITEM_ENTITY_TARGET;
   public static final LineWidth FULL_LINE_WIDTH;

   public RenderPhase(String name, Runnable beginAction, Runnable endAction) {
      this.name = name;
      this.beginAction = beginAction;
      this.endAction = endAction;
   }

   public void startDrawing() {
      this.beginAction.run();
   }

   public void endDrawing() {
      this.endAction.run();
   }

   public String toString() {
      return this.name;
   }

   public String getName() {
      return this.name;
   }

   private static void setupGlintTexturing(float scale) {
      long l = (long)((double)Util.getMeasuringTimeMs() * (Double)MinecraftClient.getInstance().options.getGlintSpeed().getValue() * 8.0);
      float f = (float)(l % 110000L) / 110000.0F;
      float g = (float)(l % 30000L) / 30000.0F;
      Matrix4f matrix4f = (new Matrix4f()).translation(-f, g, 0.0F);
      matrix4f.rotateZ(0.17453292F).scale(scale);
      RenderSystem.setTextureMatrix(matrix4f);
   }

   static {
      MIPMAP_BLOCK_ATLAS_TEXTURE = new Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, true);
      BLOCK_ATLAS_TEXTURE = new Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false);
      NO_TEXTURE = new TextureBase();
      DEFAULT_TEXTURING = new Texturing("default_texturing", () -> {
      }, () -> {
      });
      GLINT_TEXTURING = new Texturing("glint_texturing", () -> {
         setupGlintTexturing(8.0F);
      }, RenderSystem::resetTextureMatrix);
      ENTITY_GLINT_TEXTURING = new Texturing("entity_glint_texturing", () -> {
         setupGlintTexturing(0.5F);
      }, RenderSystem::resetTextureMatrix);
      ARMOR_ENTITY_GLINT_TEXTURING = new Texturing("armor_entity_glint_texturing", () -> {
         setupGlintTexturing(0.16F);
      }, RenderSystem::resetTextureMatrix);
      ENABLE_LIGHTMAP = new Lightmap(true);
      DISABLE_LIGHTMAP = new Lightmap(false);
      ENABLE_OVERLAY_COLOR = new Overlay(true);
      DISABLE_OVERLAY_COLOR = new Overlay(false);
      NO_LAYERING = new Layering("no_layering", () -> {
      }, () -> {
      });
      VIEW_OFFSET_Z_LAYERING = new Layering("view_offset_z_layering", () -> {
         Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
         matrix4fStack.pushMatrix();
         RenderSystem.getProjectionType().apply(matrix4fStack, 1.0F);
      }, () -> {
         Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
         matrix4fStack.popMatrix();
      });
      VIEW_OFFSET_Z_LAYERING_FORWARD = new Layering("view_offset_z_layering_forward", () -> {
         Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
         matrix4fStack.pushMatrix();
         RenderSystem.getProjectionType().apply(matrix4fStack, -1.0F);
      }, () -> {
         Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
         matrix4fStack.popMatrix();
      });
      MAIN_TARGET = new Target("main_target", () -> {
         return MinecraftClient.getInstance().getFramebuffer();
      });
      OUTLINE_TARGET = new Target("outline_target", () -> {
         Framebuffer framebuffer = MinecraftClient.getInstance().worldRenderer.getEntityOutlinesFramebuffer();
         return framebuffer != null ? framebuffer : MinecraftClient.getInstance().getFramebuffer();
      });
      TRANSLUCENT_TARGET = new Target("translucent_target", () -> {
         Framebuffer framebuffer = MinecraftClient.getInstance().worldRenderer.getTranslucentFramebuffer();
         return framebuffer != null ? framebuffer : MinecraftClient.getInstance().getFramebuffer();
      });
      PARTICLES_TARGET = new Target("particles_target", () -> {
         Framebuffer framebuffer = MinecraftClient.getInstance().worldRenderer.getParticlesFramebuffer();
         return framebuffer != null ? framebuffer : MinecraftClient.getInstance().getFramebuffer();
      });
      WEATHER_TARGET = new Target("weather_target", () -> {
         Framebuffer framebuffer = MinecraftClient.getInstance().worldRenderer.getWeatherFramebuffer();
         return framebuffer != null ? framebuffer : MinecraftClient.getInstance().getFramebuffer();
      });
      ITEM_ENTITY_TARGET = new Target("item_entity_target", () -> {
         Framebuffer framebuffer = MinecraftClient.getInstance().worldRenderer.getEntityFramebuffer();
         return framebuffer != null ? framebuffer : MinecraftClient.getInstance().getFramebuffer();
      });
      FULL_LINE_WIDTH = new LineWidth(OptionalDouble.of(1.0));
   }

   @Environment(EnvType.CLIENT)
   public static class Texture extends TextureBase {
      private final Optional id;
      private final boolean mipmap;

      public Texture(Identifier id, boolean mipmap) {
         super(() -> {
            TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
            AbstractTexture abstractTexture = textureManager.getTexture(id);
            abstractTexture.setUseMipmaps(mipmap);
            RenderSystem.setShaderTexture(0, abstractTexture.getGlTextureView());
         }, () -> {
         });
         this.id = Optional.of(id);
         this.mipmap = mipmap;
      }

      public String toString() {
         String var10000 = this.name;
         return var10000 + "[" + String.valueOf(this.id) + "(mipmap=" + this.mipmap + ")]";
      }

      protected Optional getId() {
         return this.id;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class TextureBase extends RenderPhase {
      public TextureBase(Runnable apply, Runnable unapply) {
         super("texture", apply, unapply);
      }

      TextureBase() {
         super("texture", () -> {
         }, () -> {
         });
      }

      protected Optional getId() {
         return Optional.empty();
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Texturing extends RenderPhase {
      public Texturing(String string, Runnable runnable, Runnable runnable2) {
         super(string, runnable, runnable2);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Lightmap extends Toggleable {
      public Lightmap(boolean lightmap) {
         super("lightmap", () -> {
            if (lightmap) {
               MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().enable();
            }

         }, () -> {
            if (lightmap) {
               MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().disable();
            }

         }, lightmap);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Overlay extends Toggleable {
      public Overlay(boolean overlayColor) {
         super("overlay", () -> {
            if (overlayColor) {
               MinecraftClient.getInstance().gameRenderer.getOverlayTexture().setupOverlayColor();
            }

         }, () -> {
            if (overlayColor) {
               MinecraftClient.getInstance().gameRenderer.getOverlayTexture().teardownOverlayColor();
            }

         }, overlayColor);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Layering extends RenderPhase {
      public Layering(String string, Runnable runnable, Runnable runnable2) {
         super(string, runnable, runnable2);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Target extends RenderPhase {
      private final Supplier framebuffer;

      public Target(String name, Supplier framebuffer) {
         super(name, () -> {
         }, () -> {
         });
         this.framebuffer = framebuffer;
      }

      public Framebuffer get() {
         return (Framebuffer)this.framebuffer.get();
      }
   }

   @Environment(EnvType.CLIENT)
   public static class LineWidth extends RenderPhase {
      private final OptionalDouble width;

      public LineWidth(OptionalDouble width) {
         super("line_width", () -> {
            if (!Objects.equals(width, OptionalDouble.of(1.0))) {
               if (width.isPresent()) {
                  RenderSystem.lineWidth((float)width.getAsDouble());
               } else {
                  RenderSystem.lineWidth(Math.max(2.5F, (float)MinecraftClient.getInstance().getWindow().getFramebufferWidth() / 1920.0F * 2.5F));
               }
            }

         }, () -> {
            if (!Objects.equals(width, OptionalDouble.of(1.0))) {
               RenderSystem.lineWidth(1.0F);
            }

         });
         this.width = width;
      }

      public String toString() {
         String var10000 = this.name;
         return var10000 + "[" + String.valueOf(this.width.isPresent() ? this.width.getAsDouble() : "window_scale") + "]";
      }
   }

   @Environment(EnvType.CLIENT)
   private static class Toggleable extends RenderPhase {
      private final boolean enabled;

      public Toggleable(String name, Runnable apply, Runnable unapply, boolean enabled) {
         super(name, apply, unapply);
         this.enabled = enabled;
      }

      public String toString() {
         return this.name + "[" + this.enabled + "]";
      }
   }

   @Environment(EnvType.CLIENT)
   public static final class OffsetTexturing extends Texturing {
      public OffsetTexturing(float x, float y) {
         super("offset_texturing", () -> {
            RenderSystem.setTextureMatrix((new Matrix4f()).translation(x, y, 0.0F));
         }, () -> {
            RenderSystem.resetTextureMatrix();
         });
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Textures extends TextureBase {
      private final Optional id;

      Textures(List textures) {
         super(() -> {
            for(int i = 0; i < textures.size(); ++i) {
               TextureEntry textureEntry = (TextureEntry)textures.get(i);
               TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
               AbstractTexture abstractTexture = textureManager.getTexture(textureEntry.id);
               abstractTexture.setUseMipmaps(textureEntry.mipmap);
               RenderSystem.setShaderTexture(i, abstractTexture.getGlTextureView());
            }

         }, () -> {
         });
         this.id = textures.isEmpty() ? Optional.empty() : Optional.of(((TextureEntry)textures.getFirst()).id);
      }

      protected Optional getId() {
         return this.id;
      }

      public static Builder create() {
         return new Builder();
      }

      @Environment(EnvType.CLIENT)
      static record TextureEntry(Identifier id, boolean mipmap) {
         final Identifier id;
         final boolean mipmap;

         TextureEntry(Identifier identifier, boolean bl) {
            this.id = identifier;
            this.mipmap = bl;
         }

         public Identifier id() {
            return this.id;
         }

         public boolean mipmap() {
            return this.mipmap;
         }
      }

      @Environment(EnvType.CLIENT)
      public static final class Builder {
         private final ImmutableList.Builder textures = new ImmutableList.Builder();

         public Builder add(Identifier id, boolean blur) {
            this.textures.add(new TextureEntry(id, blur));
            return this;
         }

         public Textures build() {
            return new Textures(this.textures.build());
         }
      }
   }
}
