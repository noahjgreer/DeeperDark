package net.minecraft.item;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.GlowItemFrameEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class DecorationItem extends Item {
   private static final Text RANDOM_TEXT;
   private final EntityType entityType;

   public DecorationItem(EntityType type, Item.Settings settings) {
      super(settings);
      this.entityType = type;
   }

   public ActionResult useOnBlock(ItemUsageContext context) {
      BlockPos blockPos = context.getBlockPos();
      Direction direction = context.getSide();
      BlockPos blockPos2 = blockPos.offset(direction);
      PlayerEntity playerEntity = context.getPlayer();
      ItemStack itemStack = context.getStack();
      if (playerEntity != null && !this.canPlaceOn(playerEntity, direction, itemStack, blockPos2)) {
         return ActionResult.FAIL;
      } else {
         World world = context.getWorld();
         Object abstractDecorationEntity;
         if (this.entityType == EntityType.PAINTING) {
            Optional optional = PaintingEntity.placePainting(world, blockPos2, direction);
            if (optional.isEmpty()) {
               return ActionResult.CONSUME;
            }

            abstractDecorationEntity = (AbstractDecorationEntity)optional.get();
         } else if (this.entityType == EntityType.ITEM_FRAME) {
            abstractDecorationEntity = new ItemFrameEntity(world, blockPos2, direction);
         } else {
            if (this.entityType != EntityType.GLOW_ITEM_FRAME) {
               return ActionResult.SUCCESS;
            }

            abstractDecorationEntity = new GlowItemFrameEntity(world, blockPos2, direction);
         }

         EntityType.copier(world, itemStack, playerEntity).accept(abstractDecorationEntity);
         if (((AbstractDecorationEntity)abstractDecorationEntity).canStayAttached()) {
            if (!world.isClient) {
               ((AbstractDecorationEntity)abstractDecorationEntity).onPlace();
               world.emitGameEvent(playerEntity, GameEvent.ENTITY_PLACE, ((AbstractDecorationEntity)abstractDecorationEntity).getPos());
               world.spawnEntity((Entity)abstractDecorationEntity);
            }

            itemStack.decrement(1);
            return ActionResult.SUCCESS;
         } else {
            return ActionResult.CONSUME;
         }
      }
   }

   protected boolean canPlaceOn(PlayerEntity player, Direction side, ItemStack stack, BlockPos pos) {
      return !side.getAxis().isVertical() && player.canPlaceOn(pos, side, stack);
   }

   public void appendTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent displayComponent, Consumer textConsumer, TooltipType type) {
      if (this.entityType == EntityType.PAINTING && displayComponent.shouldDisplay(DataComponentTypes.PAINTING_VARIANT)) {
         RegistryEntry registryEntry = (RegistryEntry)stack.get(DataComponentTypes.PAINTING_VARIANT);
         if (registryEntry != null) {
            ((PaintingVariant)registryEntry.value()).title().ifPresent(textConsumer);
            ((PaintingVariant)registryEntry.value()).author().ifPresent(textConsumer);
            textConsumer.accept(Text.translatable("painting.dimensions", ((PaintingVariant)registryEntry.value()).width(), ((PaintingVariant)registryEntry.value()).height()));
         } else if (type.isCreative()) {
            textConsumer.accept(RANDOM_TEXT);
         }
      }

   }

   static {
      RANDOM_TEXT = Text.translatable("painting.random").formatted(Formatting.GRAY);
   }
}
