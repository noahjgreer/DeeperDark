package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ModelTextures {
   public static final ModelTextures EMPTY = new ModelTextures(Map.of());
   private static final char TEXTURE_REFERENCE_PREFIX = '#';
   private final Map textures;

   ModelTextures(Map textures) {
      this.textures = textures;
   }

   @Nullable
   public SpriteIdentifier get(String textureId) {
      if (isTextureReference(textureId)) {
         textureId = textureId.substring(1);
      }

      return (SpriteIdentifier)this.textures.get(textureId);
   }

   private static boolean isTextureReference(String textureId) {
      return textureId.charAt(0) == '#';
   }

   public static Textures fromJson(JsonObject json, Identifier atlasTexture) {
      Textures.Builder builder = new Textures.Builder();
      Iterator var3 = json.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry entry = (Map.Entry)var3.next();
         add(atlasTexture, (String)entry.getKey(), ((JsonElement)entry.getValue()).getAsString(), builder);
      }

      return builder.build();
   }

   private static void add(Identifier atlasTexture, String textureId, String value, Textures.Builder builder) {
      if (isTextureReference(value)) {
         builder.addTextureReference(textureId, value.substring(1));
      } else {
         Identifier identifier = Identifier.tryParse(value);
         if (identifier == null) {
            throw new JsonParseException(value + " is not valid resource location");
         }

         builder.addSprite(textureId, new SpriteIdentifier(atlasTexture, identifier));
      }

   }

   @Environment(EnvType.CLIENT)
   public static record Textures(Map values) {
      final Map values;
      public static final Textures EMPTY = new Textures(Map.of());

      public Textures(Map map) {
         this.values = map;
      }

      public Map values() {
         return this.values;
      }

      @Environment(EnvType.CLIENT)
      public static class Builder {
         private final Map entries = new HashMap();

         public Builder addTextureReference(String textureId, String target) {
            this.entries.put(textureId, new TextureReferenceEntry(target));
            return this;
         }

         public Builder addSprite(String textureId, SpriteIdentifier spriteId) {
            this.entries.put(textureId, new SpriteEntry(spriteId));
            return this;
         }

         public Textures build() {
            return this.entries.isEmpty() ? ModelTextures.Textures.EMPTY : new Textures(Map.copyOf(this.entries));
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Builder {
      private static final Logger LOGGER = LogUtils.getLogger();
      private final List textures = new ArrayList();

      public Builder addLast(Textures textures) {
         this.textures.addLast(textures);
         return this;
      }

      public Builder addFirst(Textures textures) {
         this.textures.addFirst(textures);
         return this;
      }

      public ModelTextures build(SimpleModel modelNameSupplier) {
         if (this.textures.isEmpty()) {
            return ModelTextures.EMPTY;
         } else {
            Object2ObjectMap object2ObjectMap = new Object2ObjectArrayMap();
            Object2ObjectMap object2ObjectMap2 = new Object2ObjectArrayMap();
            Iterator var4 = Lists.reverse(this.textures).iterator();

            while(var4.hasNext()) {
               Textures textures = (Textures)var4.next();
               textures.values.forEach((textureId, entryx) -> {
                  Objects.requireNonNull(entryx);
                  int i = 0;
                  switch (entryx.typeSwitch<invokedynamic>(entryx, i)) {
                     case 0:
                        SpriteEntry spriteEntry = (SpriteEntry)entryx;
                        object2ObjectMap2.remove(textureId);
                        object2ObjectMap.put(textureId, spriteEntry.material());
                        break;
                     case 1:
                        TextureReferenceEntry textureReferenceEntry = (TextureReferenceEntry)entryx;
                        object2ObjectMap.remove(textureId);
                        object2ObjectMap2.put(textureId, textureReferenceEntry);
                        break;
                     default:
                        throw new MatchException((String)null, (Throwable)null);
                  }

               });
            }

            if (object2ObjectMap2.isEmpty()) {
               return new ModelTextures(object2ObjectMap);
            } else {
               boolean bl = true;

               while(bl) {
                  bl = false;
                  ObjectIterator objectIterator = Object2ObjectMaps.fastIterator(object2ObjectMap2);

                  while(objectIterator.hasNext()) {
                     Object2ObjectMap.Entry entry = (Object2ObjectMap.Entry)objectIterator.next();
                     SpriteIdentifier spriteIdentifier = (SpriteIdentifier)object2ObjectMap.get(((TextureReferenceEntry)entry.getValue()).target);
                     if (spriteIdentifier != null) {
                        object2ObjectMap.put((String)entry.getKey(), spriteIdentifier);
                        objectIterator.remove();
                        bl = true;
                     }
                  }
               }

               if (!object2ObjectMap2.isEmpty()) {
                  LOGGER.warn("Unresolved texture references in {}:\n{}", modelNameSupplier.name(), object2ObjectMap2.entrySet().stream().map((entryx) -> {
                     String var10000 = (String)entryx.getKey();
                     return "\t#" + var10000 + "-> #" + ((TextureReferenceEntry)entryx.getValue()).target + "\n";
                  }).collect(Collectors.joining()));
               }

               return new ModelTextures(object2ObjectMap);
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   private static record TextureReferenceEntry(String target) implements Entry {
      final String target;

      TextureReferenceEntry(String string) {
         this.target = string;
      }

      public String target() {
         return this.target;
      }
   }

   @Environment(EnvType.CLIENT)
   private static record SpriteEntry(SpriteIdentifier material) implements Entry {
      SpriteEntry(SpriteIdentifier spriteIdentifier) {
         this.material = spriteIdentifier;
      }

      public SpriteIdentifier material() {
         return this.material;
      }
   }

   @Environment(EnvType.CLIENT)
   public sealed interface Entry permits ModelTextures.SpriteEntry, ModelTextures.TextureReferenceEntry {
   }
}
