package net.minecraft.item;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.BundleTooltipData;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.math.Fraction;

public class BundleItem extends Item {
   public static final int TOOLTIP_STACKS_COLUMNS = 4;
   public static final int TOOLTIP_STACKS_ROWS = 3;
   public static final int MAX_TOOLTIP_STACKS_SHOWN = 12;
   public static final int MAX_TOOLTIP_STACKS_SHOWN_WHEN_TOO_MANY_TYPES = 11;
   private static final int FULL_ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 1.0F, 0.33F, 0.33F);
   private static final int ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 0.44F, 0.53F, 1.0F);
   private static final int field_54109 = 10;
   private static final int field_54110 = 2;
   private static final int MAX_USE_TIME = 200;

   public BundleItem(Item.Settings settings) {
      super(settings);
   }

   public static float getAmountFilled(ItemStack stack) {
      BundleContentsComponent bundleContentsComponent = (BundleContentsComponent)stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
      return bundleContentsComponent.getOccupancy().floatValue();
   }

   public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
      BundleContentsComponent bundleContentsComponent = (BundleContentsComponent)stack.get(DataComponentTypes.BUNDLE_CONTENTS);
      if (bundleContentsComponent == null) {
         return false;
      } else {
         ItemStack itemStack = slot.getStack();
         BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent);
         if (clickType == ClickType.LEFT && !itemStack.isEmpty()) {
            if (builder.add(slot, player) > 0) {
               playInsertSound(player);
            } else {
               playInsertFailSound(player);
            }

            stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
            this.onContentChanged(player);
            return true;
         } else if (clickType == ClickType.RIGHT && itemStack.isEmpty()) {
            ItemStack itemStack2 = builder.removeSelected();
            if (itemStack2 != null) {
               ItemStack itemStack3 = slot.insertStack(itemStack2);
               if (itemStack3.getCount() > 0) {
                  builder.add(itemStack3);
               } else {
                  playRemoveOneSound(player);
               }
            }

            stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
            this.onContentChanged(player);
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
      if (clickType == ClickType.LEFT && otherStack.isEmpty()) {
         setSelectedStackIndex(stack, -1);
         return false;
      } else {
         BundleContentsComponent bundleContentsComponent = (BundleContentsComponent)stack.get(DataComponentTypes.BUNDLE_CONTENTS);
         if (bundleContentsComponent == null) {
            return false;
         } else {
            BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent);
            if (clickType == ClickType.LEFT && !otherStack.isEmpty()) {
               if (slot.canTakePartial(player) && builder.add(otherStack) > 0) {
                  playInsertSound(player);
               } else {
                  playInsertFailSound(player);
               }

               stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
               this.onContentChanged(player);
               return true;
            } else if (clickType == ClickType.RIGHT && otherStack.isEmpty()) {
               if (slot.canTakePartial(player)) {
                  ItemStack itemStack = builder.removeSelected();
                  if (itemStack != null) {
                     playRemoveOneSound(player);
                     cursorStackReference.set(itemStack);
                  }
               }

               stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
               this.onContentChanged(player);
               return true;
            } else {
               setSelectedStackIndex(stack, -1);
               return false;
            }
         }
      }
   }

   public ActionResult use(World world, PlayerEntity user, Hand hand) {
      user.setCurrentHand(hand);
      return ActionResult.SUCCESS;
   }

   private void dropContentsOnUse(World world, PlayerEntity player, ItemStack stack) {
      if (this.dropFirstBundledStack(stack, player)) {
         playDropContentsSound(world, player);
         player.incrementStat(Stats.USED.getOrCreateStat(this));
      }

   }

   public boolean isItemBarVisible(ItemStack stack) {
      BundleContentsComponent bundleContentsComponent = (BundleContentsComponent)stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
      return bundleContentsComponent.getOccupancy().compareTo(Fraction.ZERO) > 0;
   }

   public int getItemBarStep(ItemStack stack) {
      BundleContentsComponent bundleContentsComponent = (BundleContentsComponent)stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
      return Math.min(1 + MathHelper.multiplyFraction(bundleContentsComponent.getOccupancy(), 12), 13);
   }

   public int getItemBarColor(ItemStack stack) {
      BundleContentsComponent bundleContentsComponent = (BundleContentsComponent)stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
      return bundleContentsComponent.getOccupancy().compareTo(Fraction.ONE) >= 0 ? FULL_ITEM_BAR_COLOR : ITEM_BAR_COLOR;
   }

   public static void setSelectedStackIndex(ItemStack stack, int selectedStackIndex) {
      BundleContentsComponent bundleContentsComponent = (BundleContentsComponent)stack.get(DataComponentTypes.BUNDLE_CONTENTS);
      if (bundleContentsComponent != null) {
         BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent);
         builder.setSelectedStackIndex(selectedStackIndex);
         stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
      }
   }

   public static boolean hasSelectedStack(ItemStack stack) {
      BundleContentsComponent bundleContentsComponent = (BundleContentsComponent)stack.get(DataComponentTypes.BUNDLE_CONTENTS);
      return bundleContentsComponent != null && bundleContentsComponent.getSelectedStackIndex() != -1;
   }

   public static int getSelectedStackIndex(ItemStack stack) {
      BundleContentsComponent bundleContentsComponent = (BundleContentsComponent)stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
      return bundleContentsComponent.getSelectedStackIndex();
   }

   public static ItemStack getSelectedStack(ItemStack stack) {
      BundleContentsComponent bundleContentsComponent = (BundleContentsComponent)stack.get(DataComponentTypes.BUNDLE_CONTENTS);
      return bundleContentsComponent != null && bundleContentsComponent.getSelectedStackIndex() != -1 ? bundleContentsComponent.get(bundleContentsComponent.getSelectedStackIndex()) : ItemStack.EMPTY;
   }

   public static int getNumberOfStacksShown(ItemStack stack) {
      BundleContentsComponent bundleContentsComponent = (BundleContentsComponent)stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
      return bundleContentsComponent.getNumberOfStacksShown();
   }

   private boolean dropFirstBundledStack(ItemStack stack, PlayerEntity player) {
      BundleContentsComponent bundleContentsComponent = (BundleContentsComponent)stack.get(DataComponentTypes.BUNDLE_CONTENTS);
      if (bundleContentsComponent != null && !bundleContentsComponent.isEmpty()) {
         Optional optional = popFirstBundledStack(stack, player, bundleContentsComponent);
         if (optional.isPresent()) {
            player.dropItem((ItemStack)optional.get(), true);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static Optional popFirstBundledStack(ItemStack stack, PlayerEntity player, BundleContentsComponent contents) {
      BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(contents);
      ItemStack itemStack = builder.removeSelected();
      if (itemStack != null) {
         playRemoveOneSound(player);
         stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
         return Optional.of(itemStack);
      } else {
         return Optional.empty();
      }
   }

   public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
      if (user instanceof PlayerEntity playerEntity) {
         int i = this.getMaxUseTime(stack, user);
         boolean bl = remainingUseTicks == i;
         if (bl || remainingUseTicks < i - 10 && remainingUseTicks % 2 == 0) {
            this.dropContentsOnUse(world, playerEntity, stack);
         }
      }

   }

   public int getMaxUseTime(ItemStack stack, LivingEntity user) {
      return 200;
   }

   public UseAction getUseAction(ItemStack stack) {
      return UseAction.BUNDLE;
   }

   public Optional getTooltipData(ItemStack stack) {
      TooltipDisplayComponent tooltipDisplayComponent = (TooltipDisplayComponent)stack.getOrDefault(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplayComponent.DEFAULT);
      return !tooltipDisplayComponent.shouldDisplay(DataComponentTypes.BUNDLE_CONTENTS) ? Optional.empty() : Optional.ofNullable((BundleContentsComponent)stack.get(DataComponentTypes.BUNDLE_CONTENTS)).map(BundleTooltipData::new);
   }

   public void onItemEntityDestroyed(ItemEntity entity) {
      BundleContentsComponent bundleContentsComponent = (BundleContentsComponent)entity.getStack().get(DataComponentTypes.BUNDLE_CONTENTS);
      if (bundleContentsComponent != null) {
         entity.getStack().set(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
         ItemUsage.spawnItemContents(entity, bundleContentsComponent.iterateCopy());
      }
   }

   public static List getBundles() {
      return Stream.of(Items.BUNDLE, Items.WHITE_BUNDLE, Items.ORANGE_BUNDLE, Items.MAGENTA_BUNDLE, Items.LIGHT_BLUE_BUNDLE, Items.YELLOW_BUNDLE, Items.LIME_BUNDLE, Items.PINK_BUNDLE, Items.GRAY_BUNDLE, Items.LIGHT_GRAY_BUNDLE, Items.CYAN_BUNDLE, Items.BLACK_BUNDLE, Items.BROWN_BUNDLE, Items.GREEN_BUNDLE, Items.RED_BUNDLE, Items.BLUE_BUNDLE, Items.PURPLE_BUNDLE).map((item) -> {
         return (BundleItem)item;
      }).toList();
   }

   public static Item getBundle(DyeColor color) {
      Item var10000;
      switch (color) {
         case WHITE:
            var10000 = Items.WHITE_BUNDLE;
            break;
         case ORANGE:
            var10000 = Items.ORANGE_BUNDLE;
            break;
         case MAGENTA:
            var10000 = Items.MAGENTA_BUNDLE;
            break;
         case LIGHT_BLUE:
            var10000 = Items.LIGHT_BLUE_BUNDLE;
            break;
         case YELLOW:
            var10000 = Items.YELLOW_BUNDLE;
            break;
         case LIME:
            var10000 = Items.LIME_BUNDLE;
            break;
         case PINK:
            var10000 = Items.PINK_BUNDLE;
            break;
         case GRAY:
            var10000 = Items.GRAY_BUNDLE;
            break;
         case LIGHT_GRAY:
            var10000 = Items.LIGHT_GRAY_BUNDLE;
            break;
         case CYAN:
            var10000 = Items.CYAN_BUNDLE;
            break;
         case BLUE:
            var10000 = Items.BLUE_BUNDLE;
            break;
         case BROWN:
            var10000 = Items.BROWN_BUNDLE;
            break;
         case GREEN:
            var10000 = Items.GREEN_BUNDLE;
            break;
         case RED:
            var10000 = Items.RED_BUNDLE;
            break;
         case BLACK:
            var10000 = Items.BLACK_BUNDLE;
            break;
         case PURPLE:
            var10000 = Items.PURPLE_BUNDLE;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private static void playRemoveOneSound(Entity entity) {
      entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
   }

   private static void playInsertSound(Entity entity) {
      entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
   }

   private static void playInsertFailSound(Entity entity) {
      entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
   }

   private static void playDropContentsSound(World world, Entity entity) {
      world.playSound((Entity)null, entity.getBlockPos(), SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, SoundCategory.PLAYERS, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
   }

   private void onContentChanged(PlayerEntity user) {
      ScreenHandler screenHandler = user.currentScreenHandler;
      if (screenHandler != null) {
         screenHandler.onContentChanged(user.getInventory());
      }

   }
}
