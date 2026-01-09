package net.minecraft.client.render;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class DefaultFramebufferSet implements PostEffectProcessor.FramebufferSet {
   public static final Identifier MAIN;
   public static final Identifier TRANSLUCENT;
   public static final Identifier ITEM_ENTITY;
   public static final Identifier PARTICLES;
   public static final Identifier WEATHER;
   public static final Identifier CLOUDS;
   public static final Identifier ENTITY_OUTLINE;
   public static final Set MAIN_ONLY;
   public static final Set MAIN_AND_ENTITY_OUTLINE;
   public static final Set STAGES;
   public Handle mainFramebuffer = Handle.empty();
   @Nullable
   public Handle translucentFramebuffer;
   @Nullable
   public Handle itemEntityFramebuffer;
   @Nullable
   public Handle particlesFramebuffer;
   @Nullable
   public Handle weatherFramebuffer;
   @Nullable
   public Handle cloudsFramebuffer;
   @Nullable
   public Handle entityOutlineFramebuffer;

   public void set(Identifier id, Handle framebuffer) {
      if (id.equals(MAIN)) {
         this.mainFramebuffer = framebuffer;
      } else if (id.equals(TRANSLUCENT)) {
         this.translucentFramebuffer = framebuffer;
      } else if (id.equals(ITEM_ENTITY)) {
         this.itemEntityFramebuffer = framebuffer;
      } else if (id.equals(PARTICLES)) {
         this.particlesFramebuffer = framebuffer;
      } else if (id.equals(WEATHER)) {
         this.weatherFramebuffer = framebuffer;
      } else if (id.equals(CLOUDS)) {
         this.cloudsFramebuffer = framebuffer;
      } else {
         if (!id.equals(ENTITY_OUTLINE)) {
            throw new IllegalArgumentException("No target with id " + String.valueOf(id));
         }

         this.entityOutlineFramebuffer = framebuffer;
      }

   }

   @Nullable
   public Handle get(Identifier id) {
      if (id.equals(MAIN)) {
         return this.mainFramebuffer;
      } else if (id.equals(TRANSLUCENT)) {
         return this.translucentFramebuffer;
      } else if (id.equals(ITEM_ENTITY)) {
         return this.itemEntityFramebuffer;
      } else if (id.equals(PARTICLES)) {
         return this.particlesFramebuffer;
      } else if (id.equals(WEATHER)) {
         return this.weatherFramebuffer;
      } else if (id.equals(CLOUDS)) {
         return this.cloudsFramebuffer;
      } else {
         return id.equals(ENTITY_OUTLINE) ? this.entityOutlineFramebuffer : null;
      }
   }

   public void clear() {
      this.mainFramebuffer = Handle.empty();
      this.translucentFramebuffer = null;
      this.itemEntityFramebuffer = null;
      this.particlesFramebuffer = null;
      this.weatherFramebuffer = null;
      this.cloudsFramebuffer = null;
      this.entityOutlineFramebuffer = null;
   }

   static {
      MAIN = PostEffectProcessor.MAIN;
      TRANSLUCENT = Identifier.ofVanilla("translucent");
      ITEM_ENTITY = Identifier.ofVanilla("item_entity");
      PARTICLES = Identifier.ofVanilla("particles");
      WEATHER = Identifier.ofVanilla("weather");
      CLOUDS = Identifier.ofVanilla("clouds");
      ENTITY_OUTLINE = Identifier.ofVanilla("entity_outline");
      MAIN_ONLY = Set.of(MAIN);
      MAIN_AND_ENTITY_OUTLINE = Set.of(MAIN, ENTITY_OUTLINE);
      STAGES = Set.of(MAIN, TRANSLUCENT, ITEM_ENTITY, PARTICLES, WEATHER, CLOUDS);
   }
}
