package net.minecraft.client.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class Model {
   private final Optional parent;
   private final Set requiredTextures;
   private final Optional variant;

   public Model(Optional parent, Optional variant, TextureKey... requiredTextureKeys) {
      this.parent = parent;
      this.variant = variant;
      this.requiredTextures = ImmutableSet.copyOf(requiredTextureKeys);
   }

   public Identifier getBlockSubModelId(Block block) {
      return ModelIds.getBlockSubModelId(block, (String)this.variant.orElse(""));
   }

   public Identifier upload(Block block, TextureMap textures, BiConsumer modelCollector) {
      return this.upload(ModelIds.getBlockSubModelId(block, (String)this.variant.orElse("")), textures, modelCollector);
   }

   public Identifier upload(Block block, String suffix, TextureMap textures, BiConsumer modelCollector) {
      return this.upload(ModelIds.getBlockSubModelId(block, suffix + (String)this.variant.orElse("")), textures, modelCollector);
   }

   public Identifier uploadWithoutVariant(Block block, String suffix, TextureMap textures, BiConsumer modelCollector) {
      return this.upload(ModelIds.getBlockSubModelId(block, suffix), textures, modelCollector);
   }

   public Identifier upload(Item item, TextureMap textures, BiConsumer modelCollector) {
      return this.upload(ModelIds.getItemSubModelId(item, (String)this.variant.orElse("")), textures, modelCollector);
   }

   public Identifier upload(Identifier id, TextureMap textures, BiConsumer modelCollector) {
      Map map = this.createTextureMap(textures);
      modelCollector.accept(id, () -> {
         JsonObject jsonObject = new JsonObject();
         this.parent.ifPresent((identifier) -> {
            jsonObject.addProperty("parent", identifier.toString());
         });
         if (!map.isEmpty()) {
            JsonObject jsonObject2 = new JsonObject();
            map.forEach((textureKey, identifier) -> {
               jsonObject2.addProperty(textureKey.getName(), identifier.toString());
            });
            jsonObject.add("textures", jsonObject2);
         }

         return jsonObject;
      });
      return id;
   }

   private Map createTextureMap(TextureMap textures) {
      Stream var10000 = Streams.concat(new Stream[]{this.requiredTextures.stream(), textures.getInherited()});
      Function var10001 = Function.identity();
      Objects.requireNonNull(textures);
      return (Map)var10000.collect(ImmutableMap.toImmutableMap(var10001, textures::getTexture));
   }
}
