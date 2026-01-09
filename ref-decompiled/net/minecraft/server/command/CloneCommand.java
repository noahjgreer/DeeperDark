package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.ArgumentGetter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.component.ComponentMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class CloneCommand {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final SimpleCommandExceptionType OVERLAP_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.clone.overlap"));
   private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((maxCount, count) -> {
      return Text.stringifiedTranslatable("commands.clone.toobig", maxCount, count);
   });
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.clone.failed"));
   public static final Predicate IS_AIR_PREDICATE = (pos) -> {
      return !pos.getBlockState().isAir();
   };

   public static void register(CommandDispatcher dispatcher, CommandRegistryAccess commandRegistryAccess) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("clone").requires(CommandManager.requirePermissionLevel(2))).then(createSourceArgs(commandRegistryAccess, (context) -> {
         return ((ServerCommandSource)context.getSource()).getWorld();
      }))).then(CommandManager.literal("from").then(CommandManager.argument("sourceDimension", DimensionArgumentType.dimension()).then(createSourceArgs(commandRegistryAccess, (context) -> {
         return DimensionArgumentType.getDimensionArgument(context, "sourceDimension");
      })))));
   }

   private static ArgumentBuilder createSourceArgs(CommandRegistryAccess commandRegistryAccess, ArgumentGetter worldGetter) {
      return CommandManager.argument("begin", BlockPosArgumentType.blockPos()).then(((RequiredArgumentBuilder)CommandManager.argument("end", BlockPosArgumentType.blockPos()).then(createDestinationArgs(commandRegistryAccess, worldGetter, (context) -> {
         return ((ServerCommandSource)context.getSource()).getWorld();
      }))).then(CommandManager.literal("to").then(CommandManager.argument("targetDimension", DimensionArgumentType.dimension()).then(createDestinationArgs(commandRegistryAccess, worldGetter, (context) -> {
         return DimensionArgumentType.getDimensionArgument(context, "targetDimension");
      })))));
   }

   private static DimensionalPos createDimensionalPos(CommandContext context, ServerWorld world, String name) throws CommandSyntaxException {
      BlockPos blockPos = BlockPosArgumentType.getLoadedBlockPos(context, world, name);
      return new DimensionalPos(world, blockPos);
   }

   private static ArgumentBuilder createDestinationArgs(CommandRegistryAccess registries, ArgumentGetter currentWorldGetter, ArgumentGetter targetWorldGetter) {
      ArgumentGetter argumentGetter = (context) -> {
         return createDimensionalPos(context, (ServerWorld)currentWorldGetter.apply(context), "begin");
      };
      ArgumentGetter argumentGetter2 = (context) -> {
         return createDimensionalPos(context, (ServerWorld)currentWorldGetter.apply(context), "end");
      };
      ArgumentGetter argumentGetter3 = (context) -> {
         return createDimensionalPos(context, (ServerWorld)targetWorldGetter.apply(context), "destination");
      };
      return appendMode(registries, argumentGetter, argumentGetter2, argumentGetter3, false, CommandManager.argument("destination", BlockPosArgumentType.blockPos())).then(appendMode(registries, argumentGetter, argumentGetter2, argumentGetter3, true, CommandManager.literal("strict")));
   }

   private static ArgumentBuilder appendMode(CommandRegistryAccess registries, ArgumentGetter beginPosGetter, ArgumentGetter endPosGetter, ArgumentGetter destinationPosGetter, boolean strict, ArgumentBuilder builder) {
      return builder.executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), (DimensionalPos)beginPosGetter.apply(context), (DimensionalPos)endPosGetter.apply(context), (DimensionalPos)destinationPosGetter.apply(context), (pos) -> {
            return true;
         }, CloneCommand.Mode.NORMAL, strict);
      }).then(createModeArgs(beginPosGetter, endPosGetter, destinationPosGetter, (context) -> {
         return (pos) -> {
            return true;
         };
      }, strict, CommandManager.literal("replace"))).then(createModeArgs(beginPosGetter, endPosGetter, destinationPosGetter, (context) -> {
         return IS_AIR_PREDICATE;
      }, strict, CommandManager.literal("masked"))).then(CommandManager.literal("filtered").then(createModeArgs(beginPosGetter, endPosGetter, destinationPosGetter, (context) -> {
         return BlockPredicateArgumentType.getBlockPredicate(context, "filter");
      }, strict, CommandManager.argument("filter", BlockPredicateArgumentType.blockPredicate(registries)))));
   }

   private static ArgumentBuilder createModeArgs(ArgumentGetter beginPosGetter, ArgumentGetter endPosGetter, ArgumentGetter destinationPosGetter, ArgumentGetter filterGetter, boolean strict, ArgumentBuilder builder) {
      return builder.executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), (DimensionalPos)beginPosGetter.apply(context), (DimensionalPos)endPosGetter.apply(context), (DimensionalPos)destinationPosGetter.apply(context), (Predicate)filterGetter.apply(context), CloneCommand.Mode.NORMAL, strict);
      }).then(CommandManager.literal("force").executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), (DimensionalPos)beginPosGetter.apply(context), (DimensionalPos)endPosGetter.apply(context), (DimensionalPos)destinationPosGetter.apply(context), (Predicate)filterGetter.apply(context), CloneCommand.Mode.FORCE, strict);
      })).then(CommandManager.literal("move").executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), (DimensionalPos)beginPosGetter.apply(context), (DimensionalPos)endPosGetter.apply(context), (DimensionalPos)destinationPosGetter.apply(context), (Predicate)filterGetter.apply(context), CloneCommand.Mode.MOVE, strict);
      })).then(CommandManager.literal("normal").executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), (DimensionalPos)beginPosGetter.apply(context), (DimensionalPos)endPosGetter.apply(context), (DimensionalPos)destinationPosGetter.apply(context), (Predicate)filterGetter.apply(context), CloneCommand.Mode.NORMAL, strict);
      }));
   }

   private static int execute(ServerCommandSource source, DimensionalPos begin, DimensionalPos end, DimensionalPos destination, Predicate filter, Mode mode, boolean strict) throws CommandSyntaxException {
      BlockPos blockPos = begin.position();
      BlockPos blockPos2 = end.position();
      BlockBox blockBox = BlockBox.create(blockPos, blockPos2);
      BlockPos blockPos3 = destination.position();
      BlockPos blockPos4 = blockPos3.add(blockBox.getDimensions());
      BlockBox blockBox2 = BlockBox.create(blockPos3, blockPos4);
      ServerWorld serverWorld = begin.dimension();
      ServerWorld serverWorld2 = destination.dimension();
      if (!mode.allowsOverlap() && serverWorld == serverWorld2 && blockBox2.intersects(blockBox)) {
         throw OVERLAP_EXCEPTION.create();
      } else {
         int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
         int j = source.getWorld().getGameRules().getInt(GameRules.COMMAND_MODIFICATION_BLOCK_LIMIT);
         if (i > j) {
            throw TOO_BIG_EXCEPTION.create(j, i);
         } else if (serverWorld.isRegionLoaded(blockPos, blockPos2) && serverWorld2.isRegionLoaded(blockPos3, blockPos4)) {
            if (serverWorld2.isDebugWorld()) {
               throw FAILED_EXCEPTION.create();
            } else {
               List list = Lists.newArrayList();
               List list2 = Lists.newArrayList();
               List list3 = Lists.newArrayList();
               Deque deque = Lists.newLinkedList();
               int k = 0;
               ErrorReporter.Logging logging = new ErrorReporter.Logging(LOGGER);

               try {
                  BlockPos blockPos5 = new BlockPos(blockBox2.getMinX() - blockBox.getMinX(), blockBox2.getMinY() - blockBox.getMinY(), blockBox2.getMinZ() - blockBox.getMinZ());

                  int l;
                  int m;
                  BlockPos blockPos6;
                  for(l = blockBox.getMinZ(); l <= blockBox.getMaxZ(); ++l) {
                     for(m = blockBox.getMinY(); m <= blockBox.getMaxY(); ++m) {
                        for(int n = blockBox.getMinX(); n <= blockBox.getMaxX(); ++n) {
                           blockPos6 = new BlockPos(n, m, l);
                           BlockPos blockPos7 = blockPos6.add(blockPos5);
                           CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(serverWorld, blockPos6, false);
                           BlockState blockState = cachedBlockPosition.getBlockState();
                           if (filter.test(cachedBlockPosition)) {
                              BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos6);
                              if (blockEntity != null) {
                                 NbtWriteView nbtWriteView = NbtWriteView.create(logging.makeChild(blockEntity.getReporterContext()), source.getRegistryManager());
                                 blockEntity.writeComponentlessData(nbtWriteView);
                                 BlockEntityInfo blockEntityInfo = new BlockEntityInfo(nbtWriteView.getNbt(), blockEntity.getComponents());
                                 list2.add(new BlockInfo(blockPos7, blockState, blockEntityInfo, serverWorld2.getBlockState(blockPos7)));
                                 deque.addLast(blockPos6);
                              } else if (!blockState.isOpaqueFullCube() && !blockState.isFullCube(serverWorld, blockPos6)) {
                                 list3.add(new BlockInfo(blockPos7, blockState, (BlockEntityInfo)null, serverWorld2.getBlockState(blockPos7)));
                                 deque.addFirst(blockPos6);
                              } else {
                                 list.add(new BlockInfo(blockPos7, blockState, (BlockEntityInfo)null, serverWorld2.getBlockState(blockPos7)));
                                 deque.addLast(blockPos6);
                              }
                           }
                        }
                     }
                  }

                  l = 2 | (strict ? 816 : 0);
                  if (mode == CloneCommand.Mode.MOVE) {
                     Iterator var36 = deque.iterator();

                     while(var36.hasNext()) {
                        BlockPos blockPos8 = (BlockPos)var36.next();
                        serverWorld.setBlockState(blockPos8, Blocks.BARRIER.getDefaultState(), l | 816);
                     }

                     m = strict ? l : 3;
                     Iterator var39 = deque.iterator();

                     while(var39.hasNext()) {
                        blockPos6 = (BlockPos)var39.next();
                        serverWorld.setBlockState(blockPos6, Blocks.AIR.getDefaultState(), m);
                     }
                  }

                  List list4 = Lists.newArrayList();
                  list4.addAll(list);
                  list4.addAll(list2);
                  list4.addAll(list3);
                  List list5 = Lists.reverse(list4);
                  Iterator var41 = list5.iterator();

                  BlockInfo blockInfo;
                  while(var41.hasNext()) {
                     blockInfo = (BlockInfo)var41.next();
                     serverWorld2.setBlockState(blockInfo.pos, Blocks.BARRIER.getDefaultState(), l | 816);
                  }

                  var41 = list4.iterator();

                  while(var41.hasNext()) {
                     blockInfo = (BlockInfo)var41.next();
                     if (serverWorld2.setBlockState(blockInfo.pos, blockInfo.state, l)) {
                        ++k;
                     }
                  }

                  for(var41 = list2.iterator(); var41.hasNext(); serverWorld2.setBlockState(blockInfo.pos, blockInfo.state, l)) {
                     blockInfo = (BlockInfo)var41.next();
                     BlockEntity blockEntity2 = serverWorld2.getBlockEntity(blockInfo.pos);
                     if (blockInfo.blockEntityInfo != null && blockEntity2 != null) {
                        blockEntity2.readComponentlessData(NbtReadView.create(logging.makeChild(blockEntity2.getReporterContext()), serverWorld2.getRegistryManager(), blockInfo.blockEntityInfo.nbt));
                        blockEntity2.setComponents(blockInfo.blockEntityInfo.components);
                        blockEntity2.markDirty();
                     }
                  }

                  if (!strict) {
                     var41 = list5.iterator();

                     while(var41.hasNext()) {
                        blockInfo = (BlockInfo)var41.next();
                        serverWorld2.onStateReplacedWithCommands(blockInfo.pos, blockInfo.previousStateAtDestination);
                     }
                  }

                  serverWorld2.getBlockTickScheduler().scheduleTicks(serverWorld.getBlockTickScheduler(), blockBox, blockPos5);
               } catch (Throwable var35) {
                  try {
                     logging.close();
                  } catch (Throwable var34) {
                     var35.addSuppressed(var34);
                  }

                  throw var35;
               }

               logging.close();
               if (k == 0) {
                  throw FAILED_EXCEPTION.create();
               } else {
                  source.sendFeedback(() -> {
                     return Text.translatable("commands.clone.success", k);
                  }, true);
                  return k;
               }
            }
         } else {
            throw BlockPosArgumentType.UNLOADED_EXCEPTION.create();
         }
      }
   }

   private static record DimensionalPos(ServerWorld dimension, BlockPos position) {
      DimensionalPos(ServerWorld serverWorld, BlockPos blockPos) {
         this.dimension = serverWorld;
         this.position = blockPos;
      }

      public ServerWorld dimension() {
         return this.dimension;
      }

      public BlockPos position() {
         return this.position;
      }
   }

   static enum Mode {
      FORCE(true),
      MOVE(true),
      NORMAL(false);

      private final boolean allowsOverlap;

      private Mode(final boolean allowsOverlap) {
         this.allowsOverlap = allowsOverlap;
      }

      public boolean allowsOverlap() {
         return this.allowsOverlap;
      }

      // $FF: synthetic method
      private static Mode[] method_36966() {
         return new Mode[]{FORCE, MOVE, NORMAL};
      }
   }

   private static record BlockEntityInfo(NbtCompound nbt, ComponentMap components) {
      final NbtCompound nbt;
      final ComponentMap components;

      BlockEntityInfo(NbtCompound nbtCompound, ComponentMap componentMap) {
         this.nbt = nbtCompound;
         this.components = componentMap;
      }

      public NbtCompound nbt() {
         return this.nbt;
      }

      public ComponentMap components() {
         return this.components;
      }
   }

   static record BlockInfo(BlockPos pos, BlockState state, @Nullable BlockEntityInfo blockEntityInfo, BlockState previousStateAtDestination) {
      final BlockPos pos;
      final BlockState state;
      @Nullable
      final BlockEntityInfo blockEntityInfo;
      final BlockState previousStateAtDestination;

      BlockInfo(BlockPos pos, BlockState state, @Nullable BlockEntityInfo blockEntityInfo, BlockState blockState) {
         this.pos = pos;
         this.state = state;
         this.blockEntityInfo = blockEntityInfo;
         this.previousStateAtDestination = blockState;
      }

      public BlockPos pos() {
         return this.pos;
      }

      public BlockState state() {
         return this.state;
      }

      @Nullable
      public BlockEntityInfo blockEntityInfo() {
         return this.blockEntityInfo;
      }

      public BlockState previousStateAtDestination() {
         return this.previousStateAtDestination;
      }
   }
}
