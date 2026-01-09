package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class LootCommand {
   private static final DynamicCommandExceptionType NO_HELD_ITEMS_EXCEPTION = new DynamicCommandExceptionType((entityName) -> {
      return Text.stringifiedTranslatable("commands.drop.no_held_items", entityName);
   });
   private static final DynamicCommandExceptionType NO_LOOT_TABLE_ENTITY_EXCEPTION = new DynamicCommandExceptionType((entityName) -> {
      return Text.stringifiedTranslatable("commands.drop.no_loot_table.entity", entityName);
   });
   private static final DynamicCommandExceptionType NO_LOOT_TABLE_BLOCK_EXCEPTION = new DynamicCommandExceptionType((blockName) -> {
      return Text.stringifiedTranslatable("commands.drop.no_loot_table.block", blockName);
   });

   public static void register(CommandDispatcher dispatcher, CommandRegistryAccess commandRegistryAccess) {
      dispatcher.register((LiteralArgumentBuilder)addTargetArguments((LiteralArgumentBuilder)CommandManager.literal("loot").requires(CommandManager.requirePermissionLevel(2)), (builder, constructor) -> {
         return builder.then(CommandManager.literal("fish").then(CommandManager.argument("loot_table", RegistryEntryArgumentType.lootTable(commandRegistryAccess)).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes((context) -> {
            return executeFish(context, RegistryEntryArgumentType.getLootTable(context, "loot_table"), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), ItemStack.EMPTY, constructor);
         })).then(CommandManager.argument("tool", ItemStackArgumentType.itemStack(commandRegistryAccess)).executes((context) -> {
            return executeFish(context, RegistryEntryArgumentType.getLootTable(context, "loot_table"), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), ItemStackArgumentType.getItemStackArgument(context, "tool").createStack(1, false), constructor);
         }))).then(CommandManager.literal("mainhand").executes((context) -> {
            return executeFish(context, RegistryEntryArgumentType.getLootTable(context, "loot_table"), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), getHeldItem((ServerCommandSource)context.getSource(), EquipmentSlot.MAINHAND), constructor);
         }))).then(CommandManager.literal("offhand").executes((context) -> {
            return executeFish(context, RegistryEntryArgumentType.getLootTable(context, "loot_table"), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), getHeldItem((ServerCommandSource)context.getSource(), EquipmentSlot.OFFHAND), constructor);
         }))))).then(CommandManager.literal("loot").then(CommandManager.argument("loot_table", RegistryEntryArgumentType.lootTable(commandRegistryAccess)).executes((context) -> {
            return executeLoot(context, RegistryEntryArgumentType.getLootTable(context, "loot_table"), constructor);
         }))).then(CommandManager.literal("kill").then(CommandManager.argument("target", EntityArgumentType.entity()).executes((context) -> {
            return executeKill(context, EntityArgumentType.getEntity(context, "target"), constructor);
         }))).then(CommandManager.literal("mine").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes((context) -> {
            return executeMine(context, BlockPosArgumentType.getLoadedBlockPos(context, "pos"), ItemStack.EMPTY, constructor);
         })).then(CommandManager.argument("tool", ItemStackArgumentType.itemStack(commandRegistryAccess)).executes((context) -> {
            return executeMine(context, BlockPosArgumentType.getLoadedBlockPos(context, "pos"), ItemStackArgumentType.getItemStackArgument(context, "tool").createStack(1, false), constructor);
         }))).then(CommandManager.literal("mainhand").executes((context) -> {
            return executeMine(context, BlockPosArgumentType.getLoadedBlockPos(context, "pos"), getHeldItem((ServerCommandSource)context.getSource(), EquipmentSlot.MAINHAND), constructor);
         }))).then(CommandManager.literal("offhand").executes((context) -> {
            return executeMine(context, BlockPosArgumentType.getLoadedBlockPos(context, "pos"), getHeldItem((ServerCommandSource)context.getSource(), EquipmentSlot.OFFHAND), constructor);
         }))));
      }));
   }

   private static ArgumentBuilder addTargetArguments(ArgumentBuilder rootArgument, SourceConstructor sourceConstructor) {
      return rootArgument.then(((LiteralArgumentBuilder)CommandManager.literal("replace").then(CommandManager.literal("entity").then(CommandManager.argument("entities", EntityArgumentType.entities()).then(sourceConstructor.construct(CommandManager.argument("slot", ItemSlotArgumentType.itemSlot()), (context, stacks, messageSender) -> {
         return executeReplace(EntityArgumentType.getEntities(context, "entities"), ItemSlotArgumentType.getItemSlot(context, "slot"), stacks.size(), stacks, messageSender);
      }).then(sourceConstructor.construct(CommandManager.argument("count", IntegerArgumentType.integer(0)), (context, stacks, messageSender) -> {
         return executeReplace(EntityArgumentType.getEntities(context, "entities"), ItemSlotArgumentType.getItemSlot(context, "slot"), IntegerArgumentType.getInteger(context, "count"), stacks, messageSender);
      })))))).then(CommandManager.literal("block").then(CommandManager.argument("targetPos", BlockPosArgumentType.blockPos()).then(sourceConstructor.construct(CommandManager.argument("slot", ItemSlotArgumentType.itemSlot()), (context, stacks, messageSender) -> {
         return executeBlock((ServerCommandSource)context.getSource(), BlockPosArgumentType.getLoadedBlockPos(context, "targetPos"), ItemSlotArgumentType.getItemSlot(context, "slot"), stacks.size(), stacks, messageSender);
      }).then(sourceConstructor.construct(CommandManager.argument("count", IntegerArgumentType.integer(0)), (context, stacks, messageSender) -> {
         return executeBlock((ServerCommandSource)context.getSource(), BlockPosArgumentType.getLoadedBlockPos(context, "targetPos"), IntegerArgumentType.getInteger(context, "slot"), IntegerArgumentType.getInteger(context, "count"), stacks, messageSender);
      })))))).then(CommandManager.literal("insert").then(sourceConstructor.construct(CommandManager.argument("targetPos", BlockPosArgumentType.blockPos()), (context, stacks, messageSender) -> {
         return executeInsert((ServerCommandSource)context.getSource(), BlockPosArgumentType.getLoadedBlockPos(context, "targetPos"), stacks, messageSender);
      }))).then(CommandManager.literal("give").then(sourceConstructor.construct(CommandManager.argument("players", EntityArgumentType.players()), (context, stacks, messageSender) -> {
         return executeGive(EntityArgumentType.getPlayers(context, "players"), stacks, messageSender);
      }))).then(CommandManager.literal("spawn").then(sourceConstructor.construct(CommandManager.argument("targetPos", Vec3ArgumentType.vec3()), (context, stacks, messageSender) -> {
         return executeSpawn((ServerCommandSource)context.getSource(), Vec3ArgumentType.getVec3(context, "targetPos"), stacks, messageSender);
      })));
   }

   private static Inventory getBlockInventory(ServerCommandSource source, BlockPos pos) throws CommandSyntaxException {
      BlockEntity blockEntity = source.getWorld().getBlockEntity(pos);
      if (!(blockEntity instanceof Inventory)) {
         throw ItemCommand.NOT_A_CONTAINER_TARGET_EXCEPTION.create(pos.getX(), pos.getY(), pos.getZ());
      } else {
         return (Inventory)blockEntity;
      }
   }

   private static int executeInsert(ServerCommandSource source, BlockPos targetPos, List stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
      Inventory inventory = getBlockInventory(source, targetPos);
      List list = Lists.newArrayListWithCapacity(stacks.size());
      Iterator var6 = stacks.iterator();

      while(var6.hasNext()) {
         ItemStack itemStack = (ItemStack)var6.next();
         if (insert(inventory, itemStack.copy())) {
            inventory.markDirty();
            list.add(itemStack);
         }
      }

      messageSender.accept(list);
      return list.size();
   }

   private static boolean insert(Inventory inventory, ItemStack stack) {
      boolean bl = false;

      for(int i = 0; i < inventory.size() && !stack.isEmpty(); ++i) {
         ItemStack itemStack = inventory.getStack(i);
         if (inventory.isValid(i, stack)) {
            if (itemStack.isEmpty()) {
               inventory.setStack(i, stack);
               bl = true;
               break;
            }

            if (itemsMatch(itemStack, stack)) {
               int j = stack.getMaxCount() - itemStack.getCount();
               int k = Math.min(stack.getCount(), j);
               stack.decrement(k);
               itemStack.increment(k);
               bl = true;
            }
         }
      }

      return bl;
   }

   private static int executeBlock(ServerCommandSource source, BlockPos targetPos, int slot, int stackCount, List stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
      Inventory inventory = getBlockInventory(source, targetPos);
      int i = inventory.size();
      if (slot >= 0 && slot < i) {
         List list = Lists.newArrayListWithCapacity(stacks.size());

         for(int j = 0; j < stackCount; ++j) {
            int k = slot + j;
            ItemStack itemStack = j < stacks.size() ? (ItemStack)stacks.get(j) : ItemStack.EMPTY;
            if (inventory.isValid(k, itemStack)) {
               inventory.setStack(k, itemStack);
               list.add(itemStack);
            }
         }

         messageSender.accept(list);
         return list.size();
      } else {
         throw ItemCommand.NO_SUCH_SLOT_TARGET_EXCEPTION.create(slot);
      }
   }

   private static boolean itemsMatch(ItemStack first, ItemStack second) {
      return first.getCount() <= first.getMaxCount() && ItemStack.areItemsAndComponentsEqual(first, second);
   }

   private static int executeGive(Collection players, List stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
      List list = Lists.newArrayListWithCapacity(stacks.size());
      Iterator var4 = stacks.iterator();

      while(var4.hasNext()) {
         ItemStack itemStack = (ItemStack)var4.next();
         Iterator var6 = players.iterator();

         while(var6.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var6.next();
            if (serverPlayerEntity.getInventory().insertStack(itemStack.copy())) {
               list.add(itemStack);
            }
         }
      }

      messageSender.accept(list);
      return list.size();
   }

   private static void replace(Entity entity, List stacks, int slot, int stackCount, List addedStacks) {
      for(int i = 0; i < stackCount; ++i) {
         ItemStack itemStack = i < stacks.size() ? (ItemStack)stacks.get(i) : ItemStack.EMPTY;
         StackReference stackReference = entity.getStackReference(slot + i);
         if (stackReference != StackReference.EMPTY && stackReference.set(itemStack.copy())) {
            addedStacks.add(itemStack);
         }
      }

   }

   private static int executeReplace(Collection targets, int slot, int stackCount, List stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
      List list = Lists.newArrayListWithCapacity(stacks.size());
      Iterator var6 = targets.iterator();

      while(var6.hasNext()) {
         Entity entity = (Entity)var6.next();
         if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
            replace(entity, stacks, slot, stackCount, list);
            serverPlayerEntity.currentScreenHandler.sendContentUpdates();
         } else {
            replace(entity, stacks, slot, stackCount, list);
         }
      }

      messageSender.accept(list);
      return list.size();
   }

   private static int executeSpawn(ServerCommandSource source, Vec3d pos, List stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
      ServerWorld serverWorld = source.getWorld();
      stacks.forEach((stack) -> {
         ItemEntity itemEntity = new ItemEntity(serverWorld, pos.x, pos.y, pos.z, stack.copy());
         itemEntity.setToDefaultPickupDelay();
         serverWorld.spawnEntity(itemEntity);
      });
      messageSender.accept(stacks);
      return stacks.size();
   }

   private static void sendDroppedFeedback(ServerCommandSource source, List stacks) {
      if (stacks.size() == 1) {
         ItemStack itemStack = (ItemStack)stacks.get(0);
         source.sendFeedback(() -> {
            return Text.translatable("commands.drop.success.single", itemStack.getCount(), itemStack.toHoverableText());
         }, false);
      } else {
         source.sendFeedback(() -> {
            return Text.translatable("commands.drop.success.multiple", stacks.size());
         }, false);
      }

   }

   private static void sendDroppedFeedback(ServerCommandSource source, List stacks, RegistryKey lootTable) {
      if (stacks.size() == 1) {
         ItemStack itemStack = (ItemStack)stacks.get(0);
         source.sendFeedback(() -> {
            return Text.translatable("commands.drop.success.single_with_table", itemStack.getCount(), itemStack.toHoverableText(), Text.of(lootTable.getValue()));
         }, false);
      } else {
         source.sendFeedback(() -> {
            return Text.translatable("commands.drop.success.multiple_with_table", stacks.size(), Text.of(lootTable.getValue()));
         }, false);
      }

   }

   private static ItemStack getHeldItem(ServerCommandSource source, EquipmentSlot slot) throws CommandSyntaxException {
      Entity entity = source.getEntityOrThrow();
      if (entity instanceof LivingEntity) {
         return ((LivingEntity)entity).getEquippedStack(slot);
      } else {
         throw NO_HELD_ITEMS_EXCEPTION.create(entity.getDisplayName());
      }
   }

   private static int executeMine(CommandContext context, BlockPos pos, ItemStack stack, Target constructor) throws CommandSyntaxException {
      ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
      ServerWorld serverWorld = serverCommandSource.getWorld();
      BlockState blockState = serverWorld.getBlockState(pos);
      BlockEntity blockEntity = serverWorld.getBlockEntity(pos);
      Optional optional = blockState.getBlock().getLootTableKey();
      if (optional.isEmpty()) {
         throw NO_LOOT_TABLE_BLOCK_EXCEPTION.create(blockState.getBlock().getName());
      } else {
         LootWorldContext.Builder builder = (new LootWorldContext.Builder(serverWorld)).add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos)).add(LootContextParameters.BLOCK_STATE, blockState).addOptional(LootContextParameters.BLOCK_ENTITY, blockEntity).addOptional(LootContextParameters.THIS_ENTITY, serverCommandSource.getEntity()).add(LootContextParameters.TOOL, stack);
         List list = blockState.getDroppedStacks(builder);
         return constructor.accept(context, list, (stacks) -> {
            sendDroppedFeedback(serverCommandSource, stacks, (RegistryKey)optional.get());
         });
      }
   }

   private static int executeKill(CommandContext context, Entity entity, Target constructor) throws CommandSyntaxException {
      Optional optional = entity.getLootTableKey();
      if (optional.isEmpty()) {
         throw NO_LOOT_TABLE_ENTITY_EXCEPTION.create(entity.getDisplayName());
      } else {
         ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
         LootWorldContext.Builder builder = new LootWorldContext.Builder(serverCommandSource.getWorld());
         Entity entity2 = serverCommandSource.getEntity();
         if (entity2 instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)entity2;
            builder.add(LootContextParameters.LAST_DAMAGE_PLAYER, playerEntity);
         }

         builder.add(LootContextParameters.DAMAGE_SOURCE, entity.getDamageSources().magic());
         builder.addOptional(LootContextParameters.DIRECT_ATTACKING_ENTITY, entity2);
         builder.addOptional(LootContextParameters.ATTACKING_ENTITY, entity2);
         builder.add(LootContextParameters.THIS_ENTITY, entity);
         builder.add(LootContextParameters.ORIGIN, serverCommandSource.getPosition());
         LootWorldContext lootWorldContext = builder.build(LootContextTypes.ENTITY);
         LootTable lootTable = serverCommandSource.getServer().getReloadableRegistries().getLootTable((RegistryKey)optional.get());
         List list = lootTable.generateLoot(lootWorldContext);
         return constructor.accept(context, list, (stacks) -> {
            sendDroppedFeedback(serverCommandSource, stacks, (RegistryKey)optional.get());
         });
      }
   }

   private static int executeLoot(CommandContext context, RegistryEntry lootTable, Target constructor) throws CommandSyntaxException {
      ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
      LootWorldContext lootWorldContext = (new LootWorldContext.Builder(serverCommandSource.getWorld())).addOptional(LootContextParameters.THIS_ENTITY, serverCommandSource.getEntity()).add(LootContextParameters.ORIGIN, serverCommandSource.getPosition()).build(LootContextTypes.CHEST);
      return getFeedbackMessageSingle(context, lootTable, lootWorldContext, constructor);
   }

   private static int executeFish(CommandContext context, RegistryEntry lootTable, BlockPos pos, ItemStack stack, Target constructor) throws CommandSyntaxException {
      ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
      LootWorldContext lootWorldContext = (new LootWorldContext.Builder(serverCommandSource.getWorld())).add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos)).add(LootContextParameters.TOOL, stack).addOptional(LootContextParameters.THIS_ENTITY, serverCommandSource.getEntity()).build(LootContextTypes.FISHING);
      return getFeedbackMessageSingle(context, lootTable, lootWorldContext, constructor);
   }

   private static int getFeedbackMessageSingle(CommandContext context, RegistryEntry lootTable, LootWorldContext lootContextParameters, Target constructor) throws CommandSyntaxException {
      ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
      List list = ((LootTable)lootTable.value()).generateLoot(lootContextParameters);
      return constructor.accept(context, list, (stacks) -> {
         sendDroppedFeedback(serverCommandSource, stacks);
      });
   }

   @FunctionalInterface
   interface SourceConstructor {
      ArgumentBuilder construct(ArgumentBuilder builder, Target target);
   }

   @FunctionalInterface
   private interface Target {
      int accept(CommandContext context, List items, FeedbackMessage messageSender) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface FeedbackMessage {
      void accept(List items) throws CommandSyntaxException;
   }
}
