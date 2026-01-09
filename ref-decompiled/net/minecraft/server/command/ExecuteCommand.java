package net.minecraft.server.command;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.ArgumentGetter;
import net.minecraft.command.CommandFunctionAction;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.ExecutionFlags;
import net.minecraft.command.FallthroughCommandAction;
import net.minecraft.command.Forkable;
import net.minecraft.command.IsolatedCommandAction;
import net.minecraft.command.ReturnValueConsumer;
import net.minecraft.command.SingleCommandAction;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.HeightmapArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.command.argument.NumberRangeArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.command.argument.RegistryEntryPredicateArgumentType;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType;
import net.minecraft.command.argument.SlotRangeArgumentType;
import net.minecraft.command.argument.SwizzleArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.Targeter;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SlotRange;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtShort;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.MacroException;
import net.minecraft.server.function.Procedure;
import net.minecraft.server.world.ChunkLevelType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ExecuteCommand {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_BLOCKS = 32768;
   private static final Dynamic2CommandExceptionType BLOCKS_TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType((maxCount, count) -> {
      return Text.stringifiedTranslatable("commands.execute.blocks.toobig", maxCount, count);
   });
   private static final SimpleCommandExceptionType CONDITIONAL_FAIL_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.execute.conditional.fail"));
   private static final DynamicCommandExceptionType CONDITIONAL_FAIL_COUNT_EXCEPTION = new DynamicCommandExceptionType((count) -> {
      return Text.stringifiedTranslatable("commands.execute.conditional.fail_count", count);
   });
   @VisibleForTesting
   public static final Dynamic2CommandExceptionType INSTANTIATION_FAILURE_EXCEPTION = new Dynamic2CommandExceptionType((function, message) -> {
      return Text.stringifiedTranslatable("commands.execute.function.instantiationFailure", function, message);
   });

   public static void register(CommandDispatcher dispatcher, CommandRegistryAccess commandRegistryAccess) {
      LiteralCommandNode literalCommandNode = dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("execute").requires(CommandManager.requirePermissionLevel(2)));
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("execute").requires(CommandManager.requirePermissionLevel(2))).then(CommandManager.literal("run").redirect(dispatcher.getRoot()))).then(addConditionArguments(literalCommandNode, CommandManager.literal("if"), true, commandRegistryAccess))).then(addConditionArguments(literalCommandNode, CommandManager.literal("unless"), false, commandRegistryAccess))).then(CommandManager.literal("as").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, (context) -> {
         List list = Lists.newArrayList();
         Iterator var2 = EntityArgumentType.getOptionalEntities(context, "targets").iterator();

         while(var2.hasNext()) {
            Entity entity = (Entity)var2.next();
            list.add(((ServerCommandSource)context.getSource()).withEntity(entity));
         }

         return list;
      })))).then(CommandManager.literal("at").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, (context) -> {
         List list = Lists.newArrayList();
         Iterator var2 = EntityArgumentType.getOptionalEntities(context, "targets").iterator();

         while(var2.hasNext()) {
            Entity entity = (Entity)var2.next();
            list.add(((ServerCommandSource)context.getSource()).withWorld((ServerWorld)entity.getWorld()).withPosition(entity.getPos()).withRotation(entity.getRotationClient()));
         }

         return list;
      })))).then(((LiteralArgumentBuilder)CommandManager.literal("store").then(addStoreArguments(literalCommandNode, CommandManager.literal("result"), true))).then(addStoreArguments(literalCommandNode, CommandManager.literal("success"), false)))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("positioned").then(CommandManager.argument("pos", Vec3ArgumentType.vec3()).redirect(literalCommandNode, (context) -> {
         return ((ServerCommandSource)context.getSource()).withPosition(Vec3ArgumentType.getVec3(context, "pos")).withEntityAnchor(EntityAnchorArgumentType.EntityAnchor.FEET);
      }))).then(CommandManager.literal("as").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, (context) -> {
         List list = Lists.newArrayList();
         Iterator var2 = EntityArgumentType.getOptionalEntities(context, "targets").iterator();

         while(var2.hasNext()) {
            Entity entity = (Entity)var2.next();
            list.add(((ServerCommandSource)context.getSource()).withPosition(entity.getPos()));
         }

         return list;
      })))).then(CommandManager.literal("over").then(CommandManager.argument("heightmap", HeightmapArgumentType.heightmap()).redirect(literalCommandNode, (context) -> {
         Vec3d vec3d = ((ServerCommandSource)context.getSource()).getPosition();
         ServerWorld serverWorld = ((ServerCommandSource)context.getSource()).getWorld();
         double d = vec3d.getX();
         double e = vec3d.getZ();
         if (!serverWorld.isChunkLoaded(ChunkSectionPos.getSectionCoordFloored(d), ChunkSectionPos.getSectionCoordFloored(e))) {
            throw BlockPosArgumentType.UNLOADED_EXCEPTION.create();
         } else {
            int i = serverWorld.getTopY(HeightmapArgumentType.getHeightmap(context, "heightmap"), MathHelper.floor(d), MathHelper.floor(e));
            return ((ServerCommandSource)context.getSource()).withPosition(new Vec3d(d, (double)i, e));
         }
      }))))).then(((LiteralArgumentBuilder)CommandManager.literal("rotated").then(CommandManager.argument("rot", RotationArgumentType.rotation()).redirect(literalCommandNode, (context) -> {
         return ((ServerCommandSource)context.getSource()).withRotation(RotationArgumentType.getRotation(context, "rot").getRotation((ServerCommandSource)context.getSource()));
      }))).then(CommandManager.literal("as").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, (context) -> {
         List list = Lists.newArrayList();
         Iterator var2 = EntityArgumentType.getOptionalEntities(context, "targets").iterator();

         while(var2.hasNext()) {
            Entity entity = (Entity)var2.next();
            list.add(((ServerCommandSource)context.getSource()).withRotation(entity.getRotationClient()));
         }

         return list;
      }))))).then(((LiteralArgumentBuilder)CommandManager.literal("facing").then(CommandManager.literal("entity").then(CommandManager.argument("targets", EntityArgumentType.entities()).then(CommandManager.argument("anchor", EntityAnchorArgumentType.entityAnchor()).fork(literalCommandNode, (context) -> {
         List list = Lists.newArrayList();
         EntityAnchorArgumentType.EntityAnchor entityAnchor = EntityAnchorArgumentType.getEntityAnchor(context, "anchor");
         Iterator var3 = EntityArgumentType.getOptionalEntities(context, "targets").iterator();

         while(var3.hasNext()) {
            Entity entity = (Entity)var3.next();
            list.add(((ServerCommandSource)context.getSource()).withLookingAt(entity, entityAnchor));
         }

         return list;
      }))))).then(CommandManager.argument("pos", Vec3ArgumentType.vec3()).redirect(literalCommandNode, (context) -> {
         return ((ServerCommandSource)context.getSource()).withLookingAt(Vec3ArgumentType.getVec3(context, "pos"));
      })))).then(CommandManager.literal("align").then(CommandManager.argument("axes", SwizzleArgumentType.swizzle()).redirect(literalCommandNode, (context) -> {
         return ((ServerCommandSource)context.getSource()).withPosition(((ServerCommandSource)context.getSource()).getPosition().floorAlongAxes(SwizzleArgumentType.getSwizzle(context, "axes")));
      })))).then(CommandManager.literal("anchored").then(CommandManager.argument("anchor", EntityAnchorArgumentType.entityAnchor()).redirect(literalCommandNode, (context) -> {
         return ((ServerCommandSource)context.getSource()).withEntityAnchor(EntityAnchorArgumentType.getEntityAnchor(context, "anchor"));
      })))).then(CommandManager.literal("in").then(CommandManager.argument("dimension", DimensionArgumentType.dimension()).redirect(literalCommandNode, (context) -> {
         return ((ServerCommandSource)context.getSource()).withWorld(DimensionArgumentType.getDimensionArgument(context, "dimension"));
      })))).then(CommandManager.literal("summon").then(CommandManager.argument("entity", RegistryEntryReferenceArgumentType.registryEntry(commandRegistryAccess, RegistryKeys.ENTITY_TYPE)).suggests(SuggestionProviders.cast(SuggestionProviders.SUMMONABLE_ENTITIES)).redirect(literalCommandNode, (context) -> {
         return summon((ServerCommandSource)context.getSource(), RegistryEntryReferenceArgumentType.getSummonableEntityType(context, "entity"));
      })))).then(addOnArguments(literalCommandNode, CommandManager.literal("on"))));
   }

   private static ArgumentBuilder addStoreArguments(LiteralCommandNode node, LiteralArgumentBuilder builder, boolean requestResult) {
      builder.then(CommandManager.literal("score").then(CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(CommandManager.argument("objective", ScoreboardObjectiveArgumentType.scoreboardObjective()).redirect(node, (context) -> {
         return executeStoreScore((ServerCommandSource)context.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders(context, "targets"), ScoreboardObjectiveArgumentType.getObjective(context, "objective"), requestResult);
      }))));
      builder.then(CommandManager.literal("bossbar").then(((RequiredArgumentBuilder)CommandManager.argument("id", IdentifierArgumentType.identifier()).suggests(BossBarCommand.SUGGESTION_PROVIDER).then(CommandManager.literal("value").redirect(node, (context) -> {
         return executeStoreBossbar((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), true, requestResult);
      }))).then(CommandManager.literal("max").redirect(node, (context) -> {
         return executeStoreBossbar((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), false, requestResult);
      }))));
      Iterator var3 = DataCommand.TARGET_OBJECT_TYPES.iterator();

      while(var3.hasNext()) {
         DataCommand.ObjectType objectType = (DataCommand.ObjectType)var3.next();
         objectType.addArgumentsToBuilder(builder, (builderx) -> {
            return builderx.then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("path", NbtPathArgumentType.nbtPath()).then(CommandManager.literal("int").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, (context) -> {
               return executeStoreData((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), (result) -> {
                  return NbtInt.of((int)((double)result * DoubleArgumentType.getDouble(context, "scale")));
               }, requestResult);
            })))).then(CommandManager.literal("float").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, (context) -> {
               return executeStoreData((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), (result) -> {
                  return NbtFloat.of((float)((double)result * DoubleArgumentType.getDouble(context, "scale")));
               }, requestResult);
            })))).then(CommandManager.literal("short").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, (context) -> {
               return executeStoreData((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), (result) -> {
                  return NbtShort.of((short)((int)((double)result * DoubleArgumentType.getDouble(context, "scale"))));
               }, requestResult);
            })))).then(CommandManager.literal("long").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, (context) -> {
               return executeStoreData((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), (result) -> {
                  return NbtLong.of((long)((double)result * DoubleArgumentType.getDouble(context, "scale")));
               }, requestResult);
            })))).then(CommandManager.literal("double").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, (context) -> {
               return executeStoreData((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), (result) -> {
                  return NbtDouble.of((double)result * DoubleArgumentType.getDouble(context, "scale"));
               }, requestResult);
            })))).then(CommandManager.literal("byte").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, (context) -> {
               return executeStoreData((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), (result) -> {
                  return NbtByte.of((byte)((int)((double)result * DoubleArgumentType.getDouble(context, "scale"))));
               }, requestResult);
            }))));
         });
      }

      return builder;
   }

   private static ServerCommandSource executeStoreScore(ServerCommandSource source, Collection targets, ScoreboardObjective objective, boolean requestResult) {
      Scoreboard scoreboard = source.getServer().getScoreboard();
      return source.mergeReturnValueConsumers((successful, returnValue) -> {
         Iterator var6 = targets.iterator();

         while(var6.hasNext()) {
            ScoreHolder scoreHolder = (ScoreHolder)var6.next();
            ScoreAccess scoreAccess = scoreboard.getOrCreateScore(scoreHolder, objective);
            int i = requestResult ? returnValue : (successful ? 1 : 0);
            scoreAccess.setScore(i);
         }

      }, ReturnValueConsumer::chain);
   }

   private static ServerCommandSource executeStoreBossbar(ServerCommandSource source, CommandBossBar bossBar, boolean storeInValue, boolean requestResult) {
      return source.mergeReturnValueConsumers((successful, returnValue) -> {
         int i = requestResult ? returnValue : (successful ? 1 : 0);
         if (storeInValue) {
            bossBar.setValue(i);
         } else {
            bossBar.setMaxValue(i);
         }

      }, ReturnValueConsumer::chain);
   }

   private static ServerCommandSource executeStoreData(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path, IntFunction nbtSetter, boolean requestResult) {
      return source.mergeReturnValueConsumers((successful, returnValue) -> {
         try {
            NbtCompound nbtCompound = object.getNbt();
            int i = requestResult ? returnValue : (successful ? 1 : 0);
            path.put(nbtCompound, (NbtElement)nbtSetter.apply(i));
            object.setNbt(nbtCompound);
         } catch (CommandSyntaxException var8) {
         }

      }, ReturnValueConsumer::chain);
   }

   private static boolean isLoaded(ServerWorld world, BlockPos pos) {
      ChunkPos chunkPos = new ChunkPos(pos);
      WorldChunk worldChunk = world.getChunkManager().getWorldChunk(chunkPos.x, chunkPos.z);
      if (worldChunk == null) {
         return false;
      } else {
         return worldChunk.getLevelType() == ChunkLevelType.ENTITY_TICKING && world.isChunkLoaded(chunkPos.toLong());
      }
   }

   private static ArgumentBuilder addConditionArguments(CommandNode root, LiteralArgumentBuilder argumentBuilder, boolean positive, CommandRegistryAccess commandRegistryAccess) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)argumentBuilder.then(CommandManager.literal("block").then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).then(addConditionLogic(root, CommandManager.argument("block", BlockPredicateArgumentType.blockPredicate(commandRegistryAccess)), positive, (context) -> {
         return BlockPredicateArgumentType.getBlockPredicate(context, "block").test(new CachedBlockPosition(((ServerCommandSource)context.getSource()).getWorld(), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), true));
      }))))).then(CommandManager.literal("biome").then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).then(addConditionLogic(root, CommandManager.argument("biome", RegistryEntryPredicateArgumentType.registryEntryPredicate(commandRegistryAccess, RegistryKeys.BIOME)), positive, (context) -> {
         return RegistryEntryPredicateArgumentType.getRegistryEntryPredicate(context, "biome", RegistryKeys.BIOME).test(((ServerCommandSource)context.getSource()).getWorld().getBiome(BlockPosArgumentType.getLoadedBlockPos(context, "pos")));
      }))))).then(CommandManager.literal("loaded").then(addConditionLogic(root, CommandManager.argument("pos", BlockPosArgumentType.blockPos()), positive, (context) -> {
         return isLoaded(((ServerCommandSource)context.getSource()).getWorld(), BlockPosArgumentType.getBlockPos(context, "pos"));
      })))).then(CommandManager.literal("dimension").then(addConditionLogic(root, CommandManager.argument("dimension", DimensionArgumentType.dimension()), positive, (context) -> {
         return DimensionArgumentType.getDimensionArgument(context, "dimension") == ((ServerCommandSource)context.getSource()).getWorld();
      })))).then(CommandManager.literal("score").then(CommandManager.argument("target", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targetObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()).then(CommandManager.literal("=").then(CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(addConditionLogic(root, CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()), positive, (context) -> {
         return testScoreCondition(context, (targetScore, sourceScore) -> {
            return targetScore == sourceScore;
         });
      }))))).then(CommandManager.literal("<").then(CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(addConditionLogic(root, CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()), positive, (context) -> {
         return testScoreCondition(context, (targetScore, sourceScore) -> {
            return targetScore < sourceScore;
         });
      }))))).then(CommandManager.literal("<=").then(CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(addConditionLogic(root, CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()), positive, (context) -> {
         return testScoreCondition(context, (targetScore, sourceScore) -> {
            return targetScore <= sourceScore;
         });
      }))))).then(CommandManager.literal(">").then(CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(addConditionLogic(root, CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()), positive, (context) -> {
         return testScoreCondition(context, (targetScore, sourceScore) -> {
            return targetScore > sourceScore;
         });
      }))))).then(CommandManager.literal(">=").then(CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(addConditionLogic(root, CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()), positive, (context) -> {
         return testScoreCondition(context, (targetScore, sourceScore) -> {
            return targetScore >= sourceScore;
         });
      }))))).then(CommandManager.literal("matches").then(addConditionLogic(root, CommandManager.argument("range", NumberRangeArgumentType.intRange()), positive, (context) -> {
         return testScoreMatch(context, NumberRangeArgumentType.IntRangeArgumentType.getRangeArgument(context, "range"));
      }))))))).then(CommandManager.literal("blocks").then(CommandManager.argument("start", BlockPosArgumentType.blockPos()).then(CommandManager.argument("end", BlockPosArgumentType.blockPos()).then(((RequiredArgumentBuilder)CommandManager.argument("destination", BlockPosArgumentType.blockPos()).then(addBlocksConditionLogic(root, CommandManager.literal("all"), positive, false))).then(addBlocksConditionLogic(root, CommandManager.literal("masked"), positive, true))))))).then(CommandManager.literal("entity").then(((RequiredArgumentBuilder)CommandManager.argument("entities", EntityArgumentType.entities()).fork(root, (context) -> {
         return getSourceOrEmptyForConditionFork(context, positive, !EntityArgumentType.getOptionalEntities(context, "entities").isEmpty());
      })).executes(getExistsConditionExecute(positive, (context) -> {
         return EntityArgumentType.getOptionalEntities(context, "entities").size();
      }))))).then(CommandManager.literal("predicate").then(addConditionLogic(root, CommandManager.argument("predicate", RegistryEntryArgumentType.lootCondition(commandRegistryAccess)), positive, (context) -> {
         return testLootCondition((ServerCommandSource)context.getSource(), RegistryEntryArgumentType.getLootCondition(context, "predicate"));
      })))).then(CommandManager.literal("function").then(CommandManager.argument("name", CommandFunctionArgumentType.commandFunction()).suggests(FunctionCommand.SUGGESTION_PROVIDER).fork(root, new IfUnlessRedirector(positive))))).then(((LiteralArgumentBuilder)CommandManager.literal("items").then(CommandManager.literal("entity").then(CommandManager.argument("entities", EntityArgumentType.entities()).then(CommandManager.argument("slots", SlotRangeArgumentType.slotRange()).then(((RequiredArgumentBuilder)CommandManager.argument("item_predicate", ItemPredicateArgumentType.itemPredicate(commandRegistryAccess)).fork(root, (context) -> {
         return getSourceOrEmptyForConditionFork(context, positive, countMatchingItems(EntityArgumentType.getEntities(context, "entities"), SlotRangeArgumentType.getSlotRange(context, "slots"), ItemPredicateArgumentType.getItemStackPredicate(context, "item_predicate")) > 0);
      })).executes(getExistsConditionExecute(positive, (context) -> {
         return countMatchingItems(EntityArgumentType.getEntities(context, "entities"), SlotRangeArgumentType.getSlotRange(context, "slots"), ItemPredicateArgumentType.getItemStackPredicate(context, "item_predicate"));
      }))))))).then(CommandManager.literal("block").then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).then(CommandManager.argument("slots", SlotRangeArgumentType.slotRange()).then(((RequiredArgumentBuilder)CommandManager.argument("item_predicate", ItemPredicateArgumentType.itemPredicate(commandRegistryAccess)).fork(root, (context) -> {
         return getSourceOrEmptyForConditionFork(context, positive, countMatchingItems((ServerCommandSource)context.getSource(), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), SlotRangeArgumentType.getSlotRange(context, "slots"), ItemPredicateArgumentType.getItemStackPredicate(context, "item_predicate")) > 0);
      })).executes(getExistsConditionExecute(positive, (context) -> {
         return countMatchingItems((ServerCommandSource)context.getSource(), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), SlotRangeArgumentType.getSlotRange(context, "slots"), ItemPredicateArgumentType.getItemStackPredicate(context, "item_predicate"));
      })))))));
      Iterator var4 = DataCommand.SOURCE_OBJECT_TYPES.iterator();

      while(var4.hasNext()) {
         DataCommand.ObjectType objectType = (DataCommand.ObjectType)var4.next();
         argumentBuilder.then(objectType.addArgumentsToBuilder(CommandManager.literal("data"), (builder) -> {
            return builder.then(((RequiredArgumentBuilder)CommandManager.argument("path", NbtPathArgumentType.nbtPath()).fork(root, (context) -> {
               return getSourceOrEmptyForConditionFork(context, positive, countPathMatches(objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path")) > 0);
            })).executes(getExistsConditionExecute(positive, (context) -> {
               return countPathMatches(objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"));
            })));
         }));
      }

      return argumentBuilder;
   }

   private static int countMatchingItems(Iterable entities, SlotRange slotRange, Predicate predicate) {
      int i = 0;
      Iterator var4 = entities.iterator();

      while(var4.hasNext()) {
         Entity entity = (Entity)var4.next();
         IntList intList = slotRange.getSlotIds();

         for(int j = 0; j < intList.size(); ++j) {
            int k = intList.getInt(j);
            StackReference stackReference = entity.getStackReference(k);
            ItemStack itemStack = stackReference.get();
            if (predicate.test(itemStack)) {
               i += itemStack.getCount();
            }
         }
      }

      return i;
   }

   private static int countMatchingItems(ServerCommandSource source, BlockPos pos, SlotRange slotRange, Predicate predicate) throws CommandSyntaxException {
      int i = 0;
      Inventory inventory = ItemCommand.getInventoryAtPos(source, pos, ItemCommand.NOT_A_CONTAINER_SOURCE_EXCEPTION);
      int j = inventory.size();
      IntList intList = slotRange.getSlotIds();

      for(int k = 0; k < intList.size(); ++k) {
         int l = intList.getInt(k);
         if (l >= 0 && l < j) {
            ItemStack itemStack = inventory.getStack(l);
            if (predicate.test(itemStack)) {
               i += itemStack.getCount();
            }
         }
      }

      return i;
   }

   private static Command getExistsConditionExecute(boolean positive, ExistsCondition condition) {
      return positive ? (context) -> {
         int i = condition.test(context);
         if (i > 0) {
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> {
               return Text.translatable("commands.execute.conditional.pass_count", i);
            }, false);
            return i;
         } else {
            throw CONDITIONAL_FAIL_EXCEPTION.create();
         }
      } : (context) -> {
         int i = condition.test(context);
         if (i == 0) {
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> {
               return Text.translatable("commands.execute.conditional.pass");
            }, false);
            return 1;
         } else {
            throw CONDITIONAL_FAIL_COUNT_EXCEPTION.create(i);
         }
      };
   }

   private static int countPathMatches(DataCommandObject object, NbtPathArgumentType.NbtPath path) throws CommandSyntaxException {
      return path.count(object.getNbt());
   }

   private static boolean testScoreCondition(CommandContext context, ScoreComparisonPredicate predicate) throws CommandSyntaxException {
      ScoreHolder scoreHolder = ScoreHolderArgumentType.getScoreHolder(context, "target");
      ScoreboardObjective scoreboardObjective = ScoreboardObjectiveArgumentType.getObjective(context, "targetObjective");
      ScoreHolder scoreHolder2 = ScoreHolderArgumentType.getScoreHolder(context, "source");
      ScoreboardObjective scoreboardObjective2 = ScoreboardObjectiveArgumentType.getObjective(context, "sourceObjective");
      Scoreboard scoreboard = ((ServerCommandSource)context.getSource()).getServer().getScoreboard();
      ReadableScoreboardScore readableScoreboardScore = scoreboard.getScore(scoreHolder, scoreboardObjective);
      ReadableScoreboardScore readableScoreboardScore2 = scoreboard.getScore(scoreHolder2, scoreboardObjective2);
      return readableScoreboardScore != null && readableScoreboardScore2 != null ? predicate.test(readableScoreboardScore.getScore(), readableScoreboardScore2.getScore()) : false;
   }

   private static boolean testScoreMatch(CommandContext context, NumberRange.IntRange range) throws CommandSyntaxException {
      ScoreHolder scoreHolder = ScoreHolderArgumentType.getScoreHolder(context, "target");
      ScoreboardObjective scoreboardObjective = ScoreboardObjectiveArgumentType.getObjective(context, "targetObjective");
      Scoreboard scoreboard = ((ServerCommandSource)context.getSource()).getServer().getScoreboard();
      ReadableScoreboardScore readableScoreboardScore = scoreboard.getScore(scoreHolder, scoreboardObjective);
      return readableScoreboardScore == null ? false : range.test(readableScoreboardScore.getScore());
   }

   private static boolean testLootCondition(ServerCommandSource source, RegistryEntry lootCondition) {
      ServerWorld serverWorld = source.getWorld();
      LootWorldContext lootWorldContext = (new LootWorldContext.Builder(serverWorld)).add(LootContextParameters.ORIGIN, source.getPosition()).addOptional(LootContextParameters.THIS_ENTITY, source.getEntity()).build(LootContextTypes.COMMAND);
      LootContext lootContext = (new LootContext.Builder(lootWorldContext)).build(Optional.empty());
      lootContext.markActive(LootContext.predicate((LootCondition)lootCondition.value()));
      return ((LootCondition)lootCondition.value()).test(lootContext);
   }

   private static Collection getSourceOrEmptyForConditionFork(CommandContext context, boolean positive, boolean value) {
      return (Collection)(value == positive ? Collections.singleton((ServerCommandSource)context.getSource()) : Collections.emptyList());
   }

   private static ArgumentBuilder addConditionLogic(CommandNode root, ArgumentBuilder builder, boolean positive, Condition condition) {
      return builder.fork(root, (context) -> {
         return getSourceOrEmptyForConditionFork(context, positive, condition.test(context));
      }).executes((context) -> {
         if (positive == condition.test(context)) {
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> {
               return Text.translatable("commands.execute.conditional.pass");
            }, false);
            return 1;
         } else {
            throw CONDITIONAL_FAIL_EXCEPTION.create();
         }
      });
   }

   private static ArgumentBuilder addBlocksConditionLogic(CommandNode root, ArgumentBuilder builder, boolean positive, boolean masked) {
      return builder.fork(root, (context) -> {
         return getSourceOrEmptyForConditionFork(context, positive, testBlocksCondition(context, masked).isPresent());
      }).executes(positive ? (context) -> {
         return executePositiveBlockCondition(context, masked);
      } : (context) -> {
         return executeNegativeBlockCondition(context, masked);
      });
   }

   private static int executePositiveBlockCondition(CommandContext context, boolean masked) throws CommandSyntaxException {
      OptionalInt optionalInt = testBlocksCondition(context, masked);
      if (optionalInt.isPresent()) {
         ((ServerCommandSource)context.getSource()).sendFeedback(() -> {
            return Text.translatable("commands.execute.conditional.pass_count", optionalInt.getAsInt());
         }, false);
         return optionalInt.getAsInt();
      } else {
         throw CONDITIONAL_FAIL_EXCEPTION.create();
      }
   }

   private static int executeNegativeBlockCondition(CommandContext context, boolean masked) throws CommandSyntaxException {
      OptionalInt optionalInt = testBlocksCondition(context, masked);
      if (optionalInt.isPresent()) {
         throw CONDITIONAL_FAIL_COUNT_EXCEPTION.create(optionalInt.getAsInt());
      } else {
         ((ServerCommandSource)context.getSource()).sendFeedback(() -> {
            return Text.translatable("commands.execute.conditional.pass");
         }, false);
         return 1;
      }
   }

   private static OptionalInt testBlocksCondition(CommandContext context, boolean masked) throws CommandSyntaxException {
      return testBlocksCondition(((ServerCommandSource)context.getSource()).getWorld(), BlockPosArgumentType.getLoadedBlockPos(context, "start"), BlockPosArgumentType.getLoadedBlockPos(context, "end"), BlockPosArgumentType.getLoadedBlockPos(context, "destination"), masked);
   }

   private static OptionalInt testBlocksCondition(ServerWorld world, BlockPos start, BlockPos end, BlockPos destination, boolean masked) throws CommandSyntaxException {
      BlockBox blockBox = BlockBox.create(start, end);
      BlockBox blockBox2 = BlockBox.create(destination, destination.add(blockBox.getDimensions()));
      BlockPos blockPos = new BlockPos(blockBox2.getMinX() - blockBox.getMinX(), blockBox2.getMinY() - blockBox.getMinY(), blockBox2.getMinZ() - blockBox.getMinZ());
      int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
      if (i > 32768) {
         throw BLOCKS_TOOBIG_EXCEPTION.create(32768, i);
      } else {
         int j = 0;
         DynamicRegistryManager dynamicRegistryManager = world.getRegistryManager();
         ErrorReporter.Logging logging = new ErrorReporter.Logging(LOGGER);

         label98: {
            OptionalInt var28;
            label97: {
               label96: {
                  label95: {
                     OptionalInt var24;
                     label94: {
                        OptionalInt var27;
                        try {
                           int k = blockBox.getMinZ();

                           label90:
                           while(true) {
                              if (k > blockBox.getMaxZ()) {
                                 break label98;
                              }

                              for(int l = blockBox.getMinY(); l <= blockBox.getMaxY(); ++l) {
                                 for(int m = blockBox.getMinX(); m <= blockBox.getMaxX(); ++m) {
                                    BlockPos blockPos2 = new BlockPos(m, l, k);
                                    BlockPos blockPos3 = blockPos2.add(blockPos);
                                    BlockState blockState = world.getBlockState(blockPos2);
                                    if (!masked || !blockState.isOf(Blocks.AIR)) {
                                       if (blockState != world.getBlockState(blockPos3)) {
                                          var27 = OptionalInt.empty();
                                          break label90;
                                       }

                                       BlockEntity blockEntity = world.getBlockEntity(blockPos2);
                                       BlockEntity blockEntity2 = world.getBlockEntity(blockPos3);
                                       if (blockEntity != null) {
                                          if (blockEntity2 == null) {
                                             var28 = OptionalInt.empty();
                                             break label97;
                                          }

                                          if (blockEntity2.getType() != blockEntity.getType()) {
                                             var28 = OptionalInt.empty();
                                             break label96;
                                          }

                                          if (!blockEntity.getComponents().equals(blockEntity2.getComponents())) {
                                             var28 = OptionalInt.empty();
                                             break label95;
                                          }

                                          NbtWriteView nbtWriteView = NbtWriteView.create(logging.makeChild(blockEntity.getReporterContext()), dynamicRegistryManager);
                                          blockEntity.writeComponentlessData(nbtWriteView);
                                          NbtCompound nbtCompound = nbtWriteView.getNbt();
                                          NbtWriteView nbtWriteView2 = NbtWriteView.create(logging.makeChild(blockEntity2.getReporterContext()), dynamicRegistryManager);
                                          blockEntity2.writeComponentlessData(nbtWriteView2);
                                          NbtCompound nbtCompound2 = nbtWriteView2.getNbt();
                                          if (!nbtCompound.equals(nbtCompound2)) {
                                             var24 = OptionalInt.empty();
                                             break label94;
                                          }
                                       }

                                       ++j;
                                    }
                                 }
                              }

                              ++k;
                           }
                        } catch (Throwable var26) {
                           try {
                              logging.close();
                           } catch (Throwable var25) {
                              var26.addSuppressed(var25);
                           }

                           throw var26;
                        }

                        logging.close();
                        return var27;
                     }

                     logging.close();
                     return var24;
                  }

                  logging.close();
                  return var28;
               }

               logging.close();
               return var28;
            }

            logging.close();
            return var28;
         }

         logging.close();
         return OptionalInt.of(j);
      }
   }

   private static RedirectModifier createEntityModifier(Function function) {
      return (context) -> {
         ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
         Entity entity = serverCommandSource.getEntity();
         return (Collection)(entity == null ? List.of() : (Collection)((Optional)function.apply(entity)).filter((entityx) -> {
            return !entityx.isRemoved();
         }).map((entityx) -> {
            return List.of(serverCommandSource.withEntity(entityx));
         }).orElse(List.of()));
      };
   }

   private static RedirectModifier createMultiEntityModifier(Function function) {
      return (context) -> {
         ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
         Entity entity = serverCommandSource.getEntity();
         if (entity == null) {
            return List.of();
         } else {
            Stream var10000 = ((Stream)function.apply(entity)).filter((entityx) -> {
               return !entityx.isRemoved();
            });
            Objects.requireNonNull(serverCommandSource);
            return var10000.map(serverCommandSource::withEntity).toList();
         }
      };
   }

   private static LiteralArgumentBuilder addOnArguments(CommandNode node, LiteralArgumentBuilder builder) {
      return (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.then(CommandManager.literal("owner").fork(node, createEntityModifier((entity) -> {
         Optional var10000;
         if (entity instanceof Tameable tameable) {
            var10000 = Optional.ofNullable(tameable.getOwner());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      })))).then(CommandManager.literal("leasher").fork(node, createEntityModifier((entity) -> {
         Optional var10000;
         if (entity instanceof Leashable leashable) {
            var10000 = Optional.ofNullable(leashable.getLeashHolder());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      })))).then(CommandManager.literal("target").fork(node, createEntityModifier((entity) -> {
         Optional var10000;
         if (entity instanceof Targeter targeter) {
            var10000 = Optional.ofNullable(targeter.getTarget());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      })))).then(CommandManager.literal("attacker").fork(node, createEntityModifier((entity) -> {
         Optional var10000;
         if (entity instanceof Attackable attackable) {
            var10000 = Optional.ofNullable(attackable.getLastAttacker());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      })))).then(CommandManager.literal("vehicle").fork(node, createEntityModifier((entity) -> {
         return Optional.ofNullable(entity.getVehicle());
      })))).then(CommandManager.literal("controller").fork(node, createEntityModifier((entity) -> {
         return Optional.ofNullable(entity.getControllingPassenger());
      })))).then(CommandManager.literal("origin").fork(node, createEntityModifier((entity) -> {
         Optional var10000;
         if (entity instanceof Ownable ownable) {
            var10000 = Optional.ofNullable(ownable.getOwner());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      })))).then(CommandManager.literal("passengers").fork(node, createMultiEntityModifier((entity) -> {
         return entity.getPassengerList().stream();
      })));
   }

   private static ServerCommandSource summon(ServerCommandSource source, RegistryEntry.Reference entityType) throws CommandSyntaxException {
      Entity entity = SummonCommand.summon(source, entityType, source.getPosition(), new NbtCompound(), true);
      return source.withEntity(entity);
   }

   public static void enqueueExecutions(AbstractServerCommandSource baseSource, List sources, Function functionSourceGetter, IntPredicate predicate, ContextChain contextChain, @Nullable NbtCompound args, ExecutionControl control, ArgumentGetter functionNamesGetter, ExecutionFlags flags) {
      List list = new ArrayList(sources.size());

      Collection collection;
      try {
         collection = (Collection)functionNamesGetter.apply(contextChain.getTopContext().copyFor(baseSource));
      } catch (CommandSyntaxException var18) {
         baseSource.handleException(var18, flags.isSilent(), control.getTracer());
         return;
      }

      int i = collection.size();
      if (i != 0) {
         List list2 = new ArrayList(i);

         Iterator var13;
         try {
            var13 = collection.iterator();

            while(var13.hasNext()) {
               CommandFunction commandFunction = (CommandFunction)var13.next();

               try {
                  list2.add(commandFunction.withMacroReplaced(args, baseSource.getDispatcher()));
               } catch (MacroException var17) {
                  throw INSTANTIATION_FAILURE_EXCEPTION.create(commandFunction.id(), var17.getMessage());
               }
            }
         } catch (CommandSyntaxException var19) {
            baseSource.handleException(var19, flags.isSilent(), control.getTracer());
         }

         var13 = sources.iterator();

         while(var13.hasNext()) {
            AbstractServerCommandSource abstractServerCommandSource = (AbstractServerCommandSource)var13.next();
            AbstractServerCommandSource abstractServerCommandSource2 = (AbstractServerCommandSource)functionSourceGetter.apply(abstractServerCommandSource.withDummyReturnValueConsumer());
            ReturnValueConsumer returnValueConsumer = (successful, returnValue) -> {
               if (predicate.test(returnValue)) {
                  list.add(abstractServerCommandSource);
               }

            };
            control.enqueueAction(new IsolatedCommandAction((newControl) -> {
               Iterator var3 = list2.iterator();

               while(var3.hasNext()) {
                  Procedure procedure = (Procedure)var3.next();
                  newControl.enqueueAction((new CommandFunctionAction(procedure, newControl.getFrame().returnValueConsumer(), true)).bind(abstractServerCommandSource2));
               }

               newControl.enqueueAction(FallthroughCommandAction.getInstance());
            }, returnValueConsumer));
         }

         ContextChain contextChain2 = contextChain.nextStage();
         String string = contextChain.getTopContext().getInput();
         control.enqueueAction(new SingleCommandAction.MultiSource(string, contextChain2, flags, baseSource, list));
      }
   }

   @FunctionalInterface
   private interface Condition {
      boolean test(CommandContext context) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface ExistsCondition {
      int test(CommandContext context) throws CommandSyntaxException;
   }

   static class IfUnlessRedirector implements Forkable.RedirectModifier {
      private final IntPredicate predicate;

      IfUnlessRedirector(boolean success) {
         this.predicate = success ? (result) -> {
            return result != 0;
         } : (result) -> {
            return result == 0;
         };
      }

      public void execute(ServerCommandSource serverCommandSource, List list, ContextChain contextChain, ExecutionFlags executionFlags, ExecutionControl executionControl) {
         ExecuteCommand.enqueueExecutions(serverCommandSource, list, FunctionCommand::createFunctionCommandSource, this.predicate, contextChain, (NbtCompound)null, executionControl, (context) -> {
            return CommandFunctionArgumentType.getFunctions(context, "name");
         }, executionFlags);
      }
   }

   @FunctionalInterface
   interface ScoreComparisonPredicate {
      boolean test(int targetScore, int sourceScore);
   }
}
