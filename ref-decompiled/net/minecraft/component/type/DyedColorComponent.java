package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;

public record DyedColorComponent(int rgb) implements TooltipAppender {
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   public static final int DEFAULT_COLOR = -6265536;

   public DyedColorComponent(int i) {
      this.rgb = i;
   }

   public static int getColor(ItemStack stack, int defaultColor) {
      DyedColorComponent dyedColorComponent = (DyedColorComponent)stack.get(DataComponentTypes.DYED_COLOR);
      return dyedColorComponent != null ? ColorHelper.fullAlpha(dyedColorComponent.rgb()) : defaultColor;
   }

   public static ItemStack setColor(ItemStack stack, List dyes) {
      if (!stack.isIn(ItemTags.DYEABLE)) {
         return ItemStack.EMPTY;
      } else {
         ItemStack itemStack = stack.copyWithCount(1);
         int i = 0;
         int j = 0;
         int k = 0;
         int l = 0;
         int m = 0;
         DyedColorComponent dyedColorComponent = (DyedColorComponent)itemStack.get(DataComponentTypes.DYED_COLOR);
         int n;
         int o;
         int p;
         if (dyedColorComponent != null) {
            n = ColorHelper.getRed(dyedColorComponent.rgb());
            o = ColorHelper.getGreen(dyedColorComponent.rgb());
            p = ColorHelper.getBlue(dyedColorComponent.rgb());
            l += Math.max(n, Math.max(o, p));
            i += n;
            j += o;
            k += p;
            ++m;
         }

         int s;
         for(Iterator var15 = dyes.iterator(); var15.hasNext(); ++m) {
            DyeItem dyeItem = (DyeItem)var15.next();
            p = dyeItem.getColor().getEntityColor();
            int q = ColorHelper.getRed(p);
            int r = ColorHelper.getGreen(p);
            s = ColorHelper.getBlue(p);
            l += Math.max(q, Math.max(r, s));
            i += q;
            j += r;
            k += s;
         }

         n = i / m;
         o = j / m;
         p = k / m;
         float f = (float)l / (float)m;
         float g = (float)Math.max(n, Math.max(o, p));
         n = (int)((float)n * f / g);
         o = (int)((float)o * f / g);
         p = (int)((float)p * f / g);
         s = ColorHelper.getArgb(0, n, o, p);
         itemStack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(s));
         return itemStack;
      }
   }

   public void appendTooltip(Item.TooltipContext context, Consumer textConsumer, TooltipType type, ComponentsAccess components) {
      if (type.isAdvanced()) {
         textConsumer.accept(Text.translatable("item.color", String.format(Locale.ROOT, "#%06X", this.rgb)).formatted(Formatting.GRAY));
      } else {
         textConsumer.accept(Text.translatable("item.dyed").formatted(Formatting.GRAY, Formatting.ITALIC));
      }

   }

   public int rgb() {
      return this.rgb;
   }

   static {
      CODEC = Codecs.RGB.xmap(DyedColorComponent::new, DyedColorComponent::rgb);
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, DyedColorComponent::rgb, DyedColorComponent::new);
   }
}
