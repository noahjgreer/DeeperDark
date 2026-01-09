package net.minecraft.util;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import net.minecraft.block.MapColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public enum DyeColor implements StringIdentifiable {
   WHITE(0, "white", 16383998, MapColor.WHITE, 15790320, 16777215),
   ORANGE(1, "orange", 16351261, MapColor.ORANGE, 15435844, 16738335),
   MAGENTA(2, "magenta", 13061821, MapColor.MAGENTA, 12801229, 16711935),
   LIGHT_BLUE(3, "light_blue", 3847130, MapColor.LIGHT_BLUE, 6719955, 10141901),
   YELLOW(4, "yellow", 16701501, MapColor.YELLOW, 14602026, 16776960),
   LIME(5, "lime", 8439583, MapColor.LIME, 4312372, 12582656),
   PINK(6, "pink", 15961002, MapColor.PINK, 14188952, 16738740),
   GRAY(7, "gray", 4673362, MapColor.GRAY, 4408131, 8421504),
   LIGHT_GRAY(8, "light_gray", 10329495, MapColor.LIGHT_GRAY, 11250603, 13882323),
   CYAN(9, "cyan", 1481884, MapColor.CYAN, 2651799, 65535),
   PURPLE(10, "purple", 8991416, MapColor.PURPLE, 8073150, 10494192),
   BLUE(11, "blue", 3949738, MapColor.BLUE, 2437522, 255),
   BROWN(12, "brown", 8606770, MapColor.BROWN, 5320730, 9127187),
   GREEN(13, "green", 6192150, MapColor.GREEN, 3887386, 65280),
   RED(14, "red", 11546150, MapColor.RED, 11743532, 16711680),
   BLACK(15, "black", 1908001, MapColor.BLACK, 1973019, 0);

   private static final IntFunction INDEX_MAPPER = ValueLists.createIndexToValueFunction(DyeColor::getIndex, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
   private static final Int2ObjectOpenHashMap BY_FIREWORK_COLOR = new Int2ObjectOpenHashMap((Map)Arrays.stream(values()).collect(Collectors.toMap((color) -> {
      return color.fireworkColor;
   }, (color) -> {
      return color;
   })));
   public static final StringIdentifiable.EnumCodec CODEC = StringIdentifiable.createCodec(DyeColor::values);
   public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, DyeColor::getIndex);
   /** @deprecated */
   @Deprecated
   public static final Codec INDEX_CODEC = Codec.BYTE.xmap(DyeColor::byIndex, (color) -> {
      return (byte)color.index;
   });
   private final int index;
   private final String id;
   private final MapColor mapColor;
   private final int entityColor;
   private final int fireworkColor;
   private final int signColor;

   private DyeColor(final int index, final String id, final int entityColor, final MapColor mapColor, final int fireworkColor, final int signColor) {
      this.index = index;
      this.id = id;
      this.mapColor = mapColor;
      this.signColor = ColorHelper.fullAlpha(signColor);
      this.entityColor = ColorHelper.fullAlpha(entityColor);
      this.fireworkColor = fireworkColor;
   }

   public int getIndex() {
      return this.index;
   }

   public String getId() {
      return this.id;
   }

   public int getEntityColor() {
      return this.entityColor;
   }

   public MapColor getMapColor() {
      return this.mapColor;
   }

   public int getFireworkColor() {
      return this.fireworkColor;
   }

   public int getSignColor() {
      return this.signColor;
   }

   public static DyeColor byIndex(int index) {
      return (DyeColor)INDEX_MAPPER.apply(index);
   }

   @Nullable
   @Contract("_,!null->!null;_,null->_")
   public static DyeColor byId(String id, @Nullable DyeColor fallback) {
      DyeColor dyeColor = (DyeColor)CODEC.byId(id);
      return dyeColor != null ? dyeColor : fallback;
   }

   @Nullable
   public static DyeColor byFireworkColor(int color) {
      return (DyeColor)BY_FIREWORK_COLOR.get(color);
   }

   public String toString() {
      return this.id;
   }

   public String asString() {
      return this.id;
   }

   public static DyeColor mixColors(ServerWorld world, DyeColor first, DyeColor second) {
      CraftingRecipeInput craftingRecipeInput = createColorMixingRecipeInput(first, second);
      Optional var10000 = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingRecipeInput, world).map((recipe) -> {
         return ((CraftingRecipe)recipe.value()).craft(craftingRecipeInput, world.getRegistryManager());
      }).map(ItemStack::getItem);
      Objects.requireNonNull(DyeItem.class);
      var10000 = var10000.filter(DyeItem.class::isInstance);
      Objects.requireNonNull(DyeItem.class);
      return (DyeColor)var10000.map(DyeItem.class::cast).map(DyeItem::getColor).orElseGet(() -> {
         return world.random.nextBoolean() ? first : second;
      });
   }

   private static CraftingRecipeInput createColorMixingRecipeInput(DyeColor first, DyeColor second) {
      return CraftingRecipeInput.create(2, 1, List.of(new ItemStack(DyeItem.byColor(first)), new ItemStack(DyeItem.byColor(second))));
   }

   // $FF: synthetic method
   private static DyeColor[] method_36676() {
      return new DyeColor[]{WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK};
   }
}
