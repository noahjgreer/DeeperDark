package net.minecraft.client.data;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TexturedModel {
   public static final Factory CUBE_ALL;
   public static final Factory CUBE_ALL_INNER_FACES;
   public static final Factory CUBE_MIRRORED_ALL;
   public static final Factory CUBE_COLUMN;
   public static final Factory CUBE_COLUMN_HORIZONTAL;
   public static final Factory CUBE_BOTTOM_TOP;
   public static final Factory CUBE_TOP;
   public static final Factory ORIENTABLE;
   public static final Factory ORIENTABLE_WITH_BOTTOM;
   public static final Factory CARPET;
   public static final Factory MOSSY_CARPET_SIDE;
   public static final Factory FLOWERBED_1;
   public static final Factory FLOWERBED_2;
   public static final Factory FLOWERBED_3;
   public static final Factory FLOWERBED_4;
   public static final Factory TEMPLATE_LEAF_LITTER_1;
   public static final Factory TEMPLATE_LEAF_LITTER_2;
   public static final Factory TEMPLATE_LEAF_LITTER_3;
   public static final Factory TEMPLATE_LEAF_LITTER_4;
   public static final Factory TEMPLATE_GLAZED_TERRACOTTA;
   public static final Factory CORAL_FAN;
   public static final Factory TEMPLATE_ANVIL;
   public static final Factory LEAVES;
   public static final Factory TEMPLATE_LANTERN;
   public static final Factory TEMPLATE_HANGING_LANTERN;
   public static final Factory TEMPLATE_SEAGRASS;
   public static final Factory END_FOR_TOP_CUBE_COLUMN;
   public static final Factory END_FOR_TOP_CUBE_COLUMN_HORIZONTAL;
   public static final Factory SIDE_TOP_BOTTOM_WALL;
   public static final Factory SIDE_END_WALL;
   private final TextureMap textures;
   private final Model model;

   private TexturedModel(TextureMap textures, Model model) {
      this.textures = textures;
      this.model = model;
   }

   public Model getModel() {
      return this.model;
   }

   public TextureMap getTextures() {
      return this.textures;
   }

   public TexturedModel textures(Consumer texturesConsumer) {
      texturesConsumer.accept(this.textures);
      return this;
   }

   public Identifier upload(Block block, BiConsumer writer) {
      return this.model.upload(block, this.textures, writer);
   }

   public Identifier upload(Block block, String suffix, BiConsumer writer) {
      return this.model.upload(block, suffix, this.textures, writer);
   }

   public static Factory makeFactory(Function texturesGetter, Model model) {
      return (block) -> {
         return new TexturedModel((TextureMap)texturesGetter.apply(block), model);
      };
   }

   public static TexturedModel getCubeAll(Identifier id) {
      return new TexturedModel(TextureMap.all(id), Models.CUBE_ALL);
   }

   static {
      CUBE_ALL = makeFactory(TextureMap::all, Models.CUBE_ALL);
      CUBE_ALL_INNER_FACES = makeFactory(TextureMap::all, Models.CUBE_ALL_INNER_FACES);
      CUBE_MIRRORED_ALL = makeFactory(TextureMap::all, Models.CUBE_MIRRORED_ALL);
      CUBE_COLUMN = makeFactory(TextureMap::sideEnd, Models.CUBE_COLUMN);
      CUBE_COLUMN_HORIZONTAL = makeFactory(TextureMap::sideEnd, Models.CUBE_COLUMN_HORIZONTAL);
      CUBE_BOTTOM_TOP = makeFactory(TextureMap::sideTopBottom, Models.CUBE_BOTTOM_TOP);
      CUBE_TOP = makeFactory(TextureMap::sideAndTop, Models.CUBE_TOP);
      ORIENTABLE = makeFactory(TextureMap::sideFrontTop, Models.ORIENTABLE);
      ORIENTABLE_WITH_BOTTOM = makeFactory(TextureMap::sideFrontTopBottom, Models.ORIENTABLE_WITH_BOTTOM);
      CARPET = makeFactory(TextureMap::wool, Models.CARPET);
      MOSSY_CARPET_SIDE = makeFactory(TextureMap::side, Models.MOSSY_CARPET_SIDE);
      FLOWERBED_1 = makeFactory(TextureMap::flowerbed, Models.FLOWERBED_1);
      FLOWERBED_2 = makeFactory(TextureMap::flowerbed, Models.FLOWERBED_2);
      FLOWERBED_3 = makeFactory(TextureMap::flowerbed, Models.FLOWERBED_3);
      FLOWERBED_4 = makeFactory(TextureMap::flowerbed, Models.FLOWERBED_4);
      TEMPLATE_LEAF_LITTER_1 = makeFactory(TextureMap::texture, Models.TEMPLATE_LEAF_LITTER_1);
      TEMPLATE_LEAF_LITTER_2 = makeFactory(TextureMap::texture, Models.TEMPLATE_LEAF_LITTER_2);
      TEMPLATE_LEAF_LITTER_3 = makeFactory(TextureMap::texture, Models.TEMPLATE_LEAF_LITTER_3);
      TEMPLATE_LEAF_LITTER_4 = makeFactory(TextureMap::texture, Models.TEMPLATE_LEAF_LITTER_4);
      TEMPLATE_GLAZED_TERRACOTTA = makeFactory(TextureMap::pattern, Models.TEMPLATE_GLAZED_TERRACOTTA);
      CORAL_FAN = makeFactory(TextureMap::fan, Models.CORAL_FAN);
      TEMPLATE_ANVIL = makeFactory(TextureMap::top, Models.TEMPLATE_ANVIL);
      LEAVES = makeFactory(TextureMap::all, Models.LEAVES);
      TEMPLATE_LANTERN = makeFactory(TextureMap::lantern, Models.TEMPLATE_LANTERN);
      TEMPLATE_HANGING_LANTERN = makeFactory(TextureMap::lantern, Models.TEMPLATE_HANGING_LANTERN);
      TEMPLATE_SEAGRASS = makeFactory(TextureMap::texture, Models.TEMPLATE_SEAGRASS);
      END_FOR_TOP_CUBE_COLUMN = makeFactory(TextureMap::sideAndEndForTop, Models.CUBE_COLUMN);
      END_FOR_TOP_CUBE_COLUMN_HORIZONTAL = makeFactory(TextureMap::sideAndEndForTop, Models.CUBE_COLUMN_HORIZONTAL);
      SIDE_TOP_BOTTOM_WALL = makeFactory(TextureMap::wallSideTopBottom, Models.CUBE_BOTTOM_TOP);
      SIDE_END_WALL = makeFactory(TextureMap::wallSideEnd, Models.CUBE_COLUMN);
   }

   @FunctionalInterface
   @Environment(EnvType.CLIENT)
   public interface Factory {
      TexturedModel get(Block block);

      default Identifier upload(Block block, BiConsumer writer) {
         return this.get(block).upload(block, writer);
      }

      default Identifier upload(Block block, String suffix, BiConsumer writer) {
         return this.get(block).upload(block, suffix, writer);
      }

      default Factory andThen(Consumer consumer) {
         return (block) -> {
            return this.get(block).textures(consumer);
         };
      }
   }
}
