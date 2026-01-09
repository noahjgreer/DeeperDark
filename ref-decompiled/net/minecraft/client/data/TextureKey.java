package net.minecraft.client.data;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class TextureKey {
   public static final TextureKey ALL = of("all");
   public static final TextureKey TEXTURE;
   public static final TextureKey PARTICLE;
   public static final TextureKey END;
   public static final TextureKey BOTTOM;
   public static final TextureKey TOP;
   public static final TextureKey FRONT;
   public static final TextureKey BACK;
   public static final TextureKey SIDE;
   public static final TextureKey NORTH;
   public static final TextureKey SOUTH;
   public static final TextureKey EAST;
   public static final TextureKey WEST;
   public static final TextureKey UP;
   public static final TextureKey DOWN;
   public static final TextureKey CROSS;
   public static final TextureKey CROSS_EMISSIVE;
   public static final TextureKey PLANT;
   public static final TextureKey WALL;
   public static final TextureKey RAIL;
   public static final TextureKey WOOL;
   public static final TextureKey PATTERN;
   public static final TextureKey PANE;
   public static final TextureKey EDGE;
   public static final TextureKey FAN;
   public static final TextureKey STEM;
   public static final TextureKey UPPERSTEM;
   public static final TextureKey CROP;
   public static final TextureKey DIRT;
   public static final TextureKey FIRE;
   public static final TextureKey LANTERN;
   public static final TextureKey PLATFORM;
   public static final TextureKey UNSTICKY;
   public static final TextureKey TORCH;
   public static final TextureKey LAYER0;
   public static final TextureKey LAYER1;
   public static final TextureKey LAYER2;
   public static final TextureKey LIT_LOG;
   public static final TextureKey CANDLE;
   public static final TextureKey INSIDE;
   public static final TextureKey CONTENT;
   public static final TextureKey INNER_TOP;
   public static final TextureKey FLOWERBED;
   public static final TextureKey TENTACLES;
   private final String name;
   @Nullable
   private final TextureKey parent;

   public static TextureKey of(String name) {
      return new TextureKey(name, (TextureKey)null);
   }

   public static TextureKey of(String name, TextureKey parent) {
      return new TextureKey(name, parent);
   }

   private TextureKey(String name, @Nullable TextureKey parent) {
      this.name = name;
      this.parent = parent;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public TextureKey getParent() {
      return this.parent;
   }

   public String toString() {
      return "#" + this.name;
   }

   static {
      TEXTURE = of("texture", ALL);
      PARTICLE = of("particle", TEXTURE);
      END = of("end", ALL);
      BOTTOM = of("bottom", END);
      TOP = of("top", END);
      FRONT = of("front", ALL);
      BACK = of("back", ALL);
      SIDE = of("side", ALL);
      NORTH = of("north", SIDE);
      SOUTH = of("south", SIDE);
      EAST = of("east", SIDE);
      WEST = of("west", SIDE);
      UP = of("up");
      DOWN = of("down");
      CROSS = of("cross");
      CROSS_EMISSIVE = of("cross_emissive");
      PLANT = of("plant");
      WALL = of("wall", ALL);
      RAIL = of("rail");
      WOOL = of("wool");
      PATTERN = of("pattern");
      PANE = of("pane");
      EDGE = of("edge");
      FAN = of("fan");
      STEM = of("stem");
      UPPERSTEM = of("upperstem");
      CROP = of("crop");
      DIRT = of("dirt");
      FIRE = of("fire");
      LANTERN = of("lantern");
      PLATFORM = of("platform");
      UNSTICKY = of("unsticky");
      TORCH = of("torch");
      LAYER0 = of("layer0");
      LAYER1 = of("layer1");
      LAYER2 = of("layer2");
      LIT_LOG = of("lit_log");
      CANDLE = of("candle");
      INSIDE = of("inside");
      CONTENT = of("content");
      INNER_TOP = of("inner_top");
      FLOWERBED = of("flowerbed");
      TENTACLES = of("tentacles");
   }
}
