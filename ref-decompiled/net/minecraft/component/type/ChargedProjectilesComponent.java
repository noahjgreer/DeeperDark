package net.minecraft.component.type;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class ChargedProjectilesComponent implements TooltipAppender {
   public static final ChargedProjectilesComponent DEFAULT = new ChargedProjectilesComponent(List.of());
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   private final List projectiles;

   private ChargedProjectilesComponent(List projectiles) {
      this.projectiles = projectiles;
   }

   public static ChargedProjectilesComponent of(ItemStack projectile) {
      return new ChargedProjectilesComponent(List.of(projectile.copy()));
   }

   public static ChargedProjectilesComponent of(List projectiles) {
      return new ChargedProjectilesComponent(List.copyOf(Lists.transform(projectiles, ItemStack::copy)));
   }

   public boolean contains(Item item) {
      Iterator var2 = this.projectiles.iterator();

      ItemStack itemStack;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         itemStack = (ItemStack)var2.next();
      } while(!itemStack.isOf(item));

      return true;
   }

   public List getProjectiles() {
      return Lists.transform(this.projectiles, ItemStack::copy);
   }

   public boolean isEmpty() {
      return this.projectiles.isEmpty();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof ChargedProjectilesComponent) {
            ChargedProjectilesComponent chargedProjectilesComponent = (ChargedProjectilesComponent)o;
            if (ItemStack.stacksEqual(this.projectiles, chargedProjectilesComponent.projectiles)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return ItemStack.listHashCode(this.projectiles);
   }

   public String toString() {
      return "ChargedProjectiles[items=" + String.valueOf(this.projectiles) + "]";
   }

   public void appendTooltip(Item.TooltipContext context, Consumer textConsumer, TooltipType type, ComponentsAccess components) {
      ItemStack itemStack = null;
      int i = 0;
      Iterator var7 = this.projectiles.iterator();

      while(var7.hasNext()) {
         ItemStack itemStack2 = (ItemStack)var7.next();
         if (itemStack == null) {
            itemStack = itemStack2;
            i = 1;
         } else if (ItemStack.areEqual(itemStack, itemStack2)) {
            ++i;
         } else {
            appendProjectileTooltip(context, textConsumer, itemStack, i);
            itemStack = itemStack2;
            i = 1;
         }
      }

      if (itemStack != null) {
         appendProjectileTooltip(context, textConsumer, itemStack, i);
      }

   }

   private static void appendProjectileTooltip(Item.TooltipContext context, Consumer textConsumer, ItemStack projectile, int count) {
      if (count == 1) {
         textConsumer.accept(Text.translatable("item.minecraft.crossbow.projectile.single", projectile.toHoverableText()));
      } else {
         textConsumer.accept(Text.translatable("item.minecraft.crossbow.projectile.multiple", count, projectile.toHoverableText()));
      }

      TooltipDisplayComponent tooltipDisplayComponent = (TooltipDisplayComponent)projectile.getOrDefault(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplayComponent.DEFAULT);
      projectile.appendTooltip(context, tooltipDisplayComponent, (PlayerEntity)null, TooltipType.BASIC, (tooltip) -> {
         textConsumer.accept(Text.literal("  ").append(tooltip).formatted(Formatting.GRAY));
      });
   }

   static {
      CODEC = ItemStack.CODEC.listOf().xmap(ChargedProjectilesComponent::new, (chargedProjectilesComponent) -> {
         return chargedProjectilesComponent.projectiles;
      });
      PACKET_CODEC = ItemStack.PACKET_CODEC.collect(PacketCodecs.toList()).xmap(ChargedProjectilesComponent::new, (component) -> {
         return component.projectiles;
      });
   }
}
