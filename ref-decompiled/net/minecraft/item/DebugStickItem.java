package net.minecraft.item;

import java.util.Collection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DebugStickStateComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class DebugStickItem extends Item {
   public DebugStickItem(Item.Settings settings) {
      super(settings);
   }

   public boolean canMine(ItemStack stack, BlockState state, World world, BlockPos pos, LivingEntity user) {
      if (!world.isClient && user instanceof PlayerEntity playerEntity) {
         this.use(playerEntity, state, world, pos, false, stack);
      }

      return false;
   }

   public ActionResult useOnBlock(ItemUsageContext context) {
      PlayerEntity playerEntity = context.getPlayer();
      World world = context.getWorld();
      if (!world.isClient && playerEntity != null) {
         BlockPos blockPos = context.getBlockPos();
         if (!this.use(playerEntity, world.getBlockState(blockPos), world, blockPos, true, context.getStack())) {
            return ActionResult.FAIL;
         }
      }

      return ActionResult.SUCCESS;
   }

   private boolean use(PlayerEntity player, BlockState state, WorldAccess world, BlockPos pos, boolean update, ItemStack stack) {
      if (!player.isCreativeLevelTwoOp()) {
         return false;
      } else {
         RegistryEntry registryEntry = state.getRegistryEntry();
         StateManager stateManager = ((Block)registryEntry.value()).getStateManager();
         Collection collection = stateManager.getProperties();
         if (collection.isEmpty()) {
            sendMessage(player, Text.translatable(this.translationKey + ".empty", registryEntry.getIdAsString()));
            return false;
         } else {
            DebugStickStateComponent debugStickStateComponent = (DebugStickStateComponent)stack.get(DataComponentTypes.DEBUG_STICK_STATE);
            if (debugStickStateComponent == null) {
               return false;
            } else {
               Property property = (Property)debugStickStateComponent.properties().get(registryEntry);
               if (update) {
                  if (property == null) {
                     property = (Property)collection.iterator().next();
                  }

                  BlockState blockState = cycle(state, property, player.shouldCancelInteraction());
                  world.setBlockState(pos, blockState, 18);
                  sendMessage(player, Text.translatable(this.translationKey + ".update", property.getName(), getValueString(blockState, property)));
               } else {
                  property = (Property)cycle((Iterable)collection, (Object)property, player.shouldCancelInteraction());
                  stack.set(DataComponentTypes.DEBUG_STICK_STATE, debugStickStateComponent.with(registryEntry, property));
                  sendMessage(player, Text.translatable(this.translationKey + ".select", property.getName(), getValueString(state, property)));
               }

               return true;
            }
         }
      }
   }

   private static BlockState cycle(BlockState state, Property property, boolean inverse) {
      return (BlockState)state.with(property, (Comparable)cycle((Iterable)property.getValues(), (Object)state.get(property), inverse));
   }

   private static Object cycle(Iterable elements, @Nullable Object current, boolean inverse) {
      return inverse ? Util.previous(elements, current) : Util.next(elements, current);
   }

   private static void sendMessage(PlayerEntity player, Text message) {
      ((ServerPlayerEntity)player).sendMessageToClient(message, true);
   }

   private static String getValueString(BlockState state, Property property) {
      return property.name(state.get(property));
   }
}
