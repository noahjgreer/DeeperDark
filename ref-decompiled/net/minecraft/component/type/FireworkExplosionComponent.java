package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public record FireworkExplosionComponent(Type shape, IntList colors, IntList fadeColors, boolean hasTrail, boolean hasTwinkle) implements TooltipAppender {
   public static final FireworkExplosionComponent DEFAULT;
   public static final Codec COLORS_CODEC;
   public static final Codec CODEC;
   private static final PacketCodec COLORS_PACKET_CODEC;
   public static final PacketCodec PACKET_CODEC;
   private static final Text CUSTOM_COLOR_TEXT;

   public FireworkExplosionComponent(Type type, IntList intList, IntList intList2, boolean bl, boolean bl2) {
      this.shape = type;
      this.colors = intList;
      this.fadeColors = intList2;
      this.hasTrail = bl;
      this.hasTwinkle = bl2;
   }

   public void appendTooltip(Item.TooltipContext context, Consumer textConsumer, TooltipType type, ComponentsAccess components) {
      textConsumer.accept(this.shape.getName().formatted(Formatting.GRAY));
      this.appendOptionalTooltip(textConsumer);
   }

   public void appendOptionalTooltip(Consumer textConsumer) {
      if (!this.colors.isEmpty()) {
         textConsumer.accept(appendColorsTooltipText(Text.empty().formatted(Formatting.GRAY), this.colors));
      }

      if (!this.fadeColors.isEmpty()) {
         textConsumer.accept(appendColorsTooltipText(Text.translatable("item.minecraft.firework_star.fade_to").append(ScreenTexts.SPACE).formatted(Formatting.GRAY), this.fadeColors));
      }

      if (this.hasTrail) {
         textConsumer.accept(Text.translatable("item.minecraft.firework_star.trail").formatted(Formatting.GRAY));
      }

      if (this.hasTwinkle) {
         textConsumer.accept(Text.translatable("item.minecraft.firework_star.flicker").formatted(Formatting.GRAY));
      }

   }

   private static Text appendColorsTooltipText(MutableText text, IntList colors) {
      for(int i = 0; i < colors.size(); ++i) {
         if (i > 0) {
            text.append(", ");
         }

         text.append(getColorText(colors.getInt(i)));
      }

      return text;
   }

   private static Text getColorText(int color) {
      DyeColor dyeColor = DyeColor.byFireworkColor(color);
      return (Text)(dyeColor == null ? CUSTOM_COLOR_TEXT : Text.translatable("item.minecraft.firework_star." + dyeColor.getId()));
   }

   public FireworkExplosionComponent withFadeColors(IntList fadeColors) {
      return new FireworkExplosionComponent(this.shape, this.colors, new IntArrayList(fadeColors), this.hasTrail, this.hasTwinkle);
   }

   public Type shape() {
      return this.shape;
   }

   public IntList colors() {
      return this.colors;
   }

   public IntList fadeColors() {
      return this.fadeColors;
   }

   public boolean hasTrail() {
      return this.hasTrail;
   }

   public boolean hasTwinkle() {
      return this.hasTwinkle;
   }

   static {
      DEFAULT = new FireworkExplosionComponent(FireworkExplosionComponent.Type.SMALL_BALL, IntList.of(), IntList.of(), false, false);
      COLORS_CODEC = Codec.INT.listOf().xmap(IntArrayList::new, ArrayList::new);
      CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(FireworkExplosionComponent.Type.CODEC.fieldOf("shape").forGetter(FireworkExplosionComponent::shape), COLORS_CODEC.optionalFieldOf("colors", IntList.of()).forGetter(FireworkExplosionComponent::colors), COLORS_CODEC.optionalFieldOf("fade_colors", IntList.of()).forGetter(FireworkExplosionComponent::fadeColors), Codec.BOOL.optionalFieldOf("has_trail", false).forGetter(FireworkExplosionComponent::hasTrail), Codec.BOOL.optionalFieldOf("has_twinkle", false).forGetter(FireworkExplosionComponent::hasTwinkle)).apply(instance, FireworkExplosionComponent::new);
      });
      COLORS_PACKET_CODEC = PacketCodecs.INTEGER.collect(PacketCodecs.toList()).xmap(IntArrayList::new, ArrayList::new);
      PACKET_CODEC = PacketCodec.tuple(FireworkExplosionComponent.Type.PACKET_CODEC, FireworkExplosionComponent::shape, COLORS_PACKET_CODEC, FireworkExplosionComponent::colors, COLORS_PACKET_CODEC, FireworkExplosionComponent::fadeColors, PacketCodecs.BOOLEAN, FireworkExplosionComponent::hasTrail, PacketCodecs.BOOLEAN, FireworkExplosionComponent::hasTwinkle, FireworkExplosionComponent::new);
      CUSTOM_COLOR_TEXT = Text.translatable("item.minecraft.firework_star.custom_color");
   }

   public static enum Type implements StringIdentifiable {
      SMALL_BALL(0, "small_ball"),
      LARGE_BALL(1, "large_ball"),
      STAR(2, "star"),
      CREEPER(3, "creeper"),
      BURST(4, "burst");

      private static final IntFunction BY_ID = ValueLists.createIndexToValueFunction(Type::getId, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
      public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(BY_ID, Type::getId);
      public static final Codec CODEC = StringIdentifiable.createBasicCodec(Type::values);
      private final int id;
      private final String name;

      private Type(final int id, final String name) {
         this.id = id;
         this.name = name;
      }

      public MutableText getName() {
         return Text.translatable("item.minecraft.firework_star.shape." + this.name);
      }

      public int getId() {
         return this.id;
      }

      public static Type byId(int id) {
         return (Type)BY_ID.apply(id);
      }

      public String asString() {
         return this.name;
      }

      // $FF: synthetic method
      private static Type[] method_36677() {
         return new Type[]{SMALL_BALL, LARGE_BALL, STAR, CREEPER, BURST};
      }
   }
}
