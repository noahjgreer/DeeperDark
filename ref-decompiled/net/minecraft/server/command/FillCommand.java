package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.ArgumentGetter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

public class FillCommand {
   private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((maxCount, count) -> {
      return Text.stringifiedTranslatable("commands.fill.toobig", maxCount, count);
   });
   static final BlockStateArgument AIR_BLOCK_ARGUMENT;
   private static final SimpleCommandExceptionType FAILED_EXCEPTION;

   public static void register(CommandDispatcher dispatcher, CommandRegistryAccess commandRegistryAccess) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("fill").requires(CommandManager.requirePermissionLevel(2))).then(CommandManager.argument("from", BlockPosArgumentType.blockPos()).then(CommandManager.argument("to", BlockPosArgumentType.blockPos()).then(buildModeTree(commandRegistryAccess, CommandManager.argument("block", BlockStateArgumentType.blockState(commandRegistryAccess)), (context) -> {
         return BlockPosArgumentType.getLoadedBlockPos(context, "from");
      }, (context) -> {
         return BlockPosArgumentType.getLoadedBlockPos(context, "to");
      }, (context) -> {
         return BlockStateArgumentType.getBlockState(context, "block");
      }, (context) -> {
         return null;
      }).then(((LiteralArgumentBuilder)CommandManager.literal("replace").executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), BlockBox.create(BlockPosArgumentType.getLoadedBlockPos(context, "from"), BlockPosArgumentType.getLoadedBlockPos(context, "to")), BlockStateArgumentType.getBlockState(context, "block"), FillCommand.Mode.REPLACE, (Predicate)null, false);
      })).then(buildModeTree(commandRegistryAccess, CommandManager.argument("filter", BlockPredicateArgumentType.blockPredicate(commandRegistryAccess)), (context) -> {
         return BlockPosArgumentType.getLoadedBlockPos(context, "from");
      }, (context) -> {
         return BlockPosArgumentType.getLoadedBlockPos(context, "to");
      }, (context) -> {
         return BlockStateArgumentType.getBlockState(context, "block");
      }, (context) -> {
         return BlockPredicateArgumentType.getBlockPredicate(context, "filter");
      }))).then(CommandManager.literal("keep").executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), BlockBox.create(BlockPosArgumentType.getLoadedBlockPos(context, "from"), BlockPosArgumentType.getLoadedBlockPos(context, "to")), BlockStateArgumentType.getBlockState(context, "block"), FillCommand.Mode.REPLACE, (pos) -> {
            return pos.getWorld().isAir(pos.getBlockPos());
         }, false);
      }))))));
   }

   private static ArgumentBuilder buildModeTree(CommandRegistryAccess registries, ArgumentBuilder argumentBuilder, ArgumentGetter from, ArgumentGetter to, ArgumentGetter state, OptionalArgumentResolver filter) {
      return argumentBuilder.executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), BlockBox.create((Vec3i)from.apply(context), (Vec3i)to.apply(context)), (BlockStateArgument)state.apply(context), FillCommand.Mode.REPLACE, (Predicate)filter.apply(context), false);
      }).then(CommandManager.literal("outline").executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), BlockBox.create((Vec3i)from.apply(context), (Vec3i)to.apply(context)), (BlockStateArgument)state.apply(context), FillCommand.Mode.OUTLINE, (Predicate)filter.apply(context), false);
      })).then(CommandManager.literal("hollow").executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), BlockBox.create((Vec3i)from.apply(context), (Vec3i)to.apply(context)), (BlockStateArgument)state.apply(context), FillCommand.Mode.HOLLOW, (Predicate)filter.apply(context), false);
      })).then(CommandManager.literal("destroy").executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), BlockBox.create((Vec3i)from.apply(context), (Vec3i)to.apply(context)), (BlockStateArgument)state.apply(context), FillCommand.Mode.DESTROY, (Predicate)filter.apply(context), false);
      })).then(CommandManager.literal("strict").executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), BlockBox.create((Vec3i)from.apply(context), (Vec3i)to.apply(context)), (BlockStateArgument)state.apply(context), FillCommand.Mode.REPLACE, (Predicate)filter.apply(context), true);
      }));
   }

   private static int execute(ServerCommandSource source, BlockBox range, BlockStateArgument block, Mode mode, @Nullable Predicate filter, boolean strict) throws CommandSyntaxException {
      int i = range.getBlockCountX() * range.getBlockCountY() * range.getBlockCountZ();
      int j = source.getWorld().getGameRules().getInt(GameRules.COMMAND_MODIFICATION_BLOCK_LIMIT);
      if (i > j) {
         throw TOO_BIG_EXCEPTION.create(j, i);
      } else {
         List list = Lists.newArrayList();
         ServerWorld serverWorld = source.getWorld();
         if (serverWorld.isDebugWorld()) {
            throw FAILED_EXCEPTION.create();
         } else {
            int k = 0;
            Iterator var11 = BlockPos.iterate(range.getMinX(), range.getMinY(), range.getMinZ(), range.getMaxX(), range.getMaxY(), range.getMaxZ()).iterator();

            while(true) {
               BlockPos blockPos;

               record Replaced(BlockPos pos, BlockState oldState) {
                  final BlockPos pos;
                  final BlockState oldState;

                  Replaced(BlockPos blockPos, BlockState blockState) {
                     this.pos = blockPos;
                     this.oldState = blockState;
                  }

                  public BlockPos pos() {
                     return this.pos;
                  }

                  public BlockState oldState() {
                     return this.oldState;
                  }
               }

               do {
                  if (!var11.hasNext()) {
                     var11 = list.iterator();

                     while(var11.hasNext()) {
                        Replaced replaced = (Replaced)var11.next();
                        serverWorld.onStateReplacedWithCommands(replaced.pos, replaced.oldState);
                     }

                     if (k == 0) {
                        throw FAILED_EXCEPTION.create();
                     }

                     source.sendFeedback(() -> {
                        return Text.translatable("commands.fill.success", k);
                     }, true);
                     return k;
                  }

                  blockPos = (BlockPos)var11.next();
               } while(filter != null && !filter.test(new CachedBlockPosition(serverWorld, blockPos, true)));

               BlockState blockState = serverWorld.getBlockState(blockPos);
               boolean bl = false;
               if (mode.postProcessor.affect(serverWorld, blockPos)) {
                  bl = true;
               }

               BlockStateArgument blockStateArgument = mode.filter.filter(range, blockPos, block, serverWorld);
               if (blockStateArgument == null) {
                  if (bl) {
                     ++k;
                  }
               } else if (!blockStateArgument.setBlockState(serverWorld, blockPos, 2 | (strict ? 816 : 256))) {
                  if (bl) {
                     ++k;
                  }
               } else {
                  if (!strict) {
                     list.add(new Replaced(blockPos.toImmutable(), blockState));
                  }

                  ++k;
               }
            }
         }
      }
   }

   static {
      AIR_BLOCK_ARGUMENT = new BlockStateArgument(Blocks.AIR.getDefaultState(), Collections.emptySet(), (NbtCompound)null);
      FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.fill.failed"));
   }

   @FunctionalInterface
   private interface OptionalArgumentResolver {
      @Nullable
      Object apply(Object object) throws CommandSyntaxException;
   }

   private static enum Mode {
      REPLACE(FillCommand.PostProcessor.EMPTY, FillCommand.Filter.IDENTITY),
      OUTLINE(FillCommand.PostProcessor.EMPTY, (range, pos, block, world) -> {
         return pos.getX() != range.getMinX() && pos.getX() != range.getMaxX() && pos.getY() != range.getMinY() && pos.getY() != range.getMaxY() && pos.getZ() != range.getMinZ() && pos.getZ() != range.getMaxZ() ? null : block;
      }),
      HOLLOW(FillCommand.PostProcessor.EMPTY, (range, pos, block, world) -> {
         return pos.getX() != range.getMinX() && pos.getX() != range.getMaxX() && pos.getY() != range.getMinY() && pos.getY() != range.getMaxY() && pos.getZ() != range.getMinZ() && pos.getZ() != range.getMaxZ() ? FillCommand.AIR_BLOCK_ARGUMENT : block;
      }),
      DESTROY((world, pos) -> {
         return world.breakBlock(pos, true);
      }, FillCommand.Filter.IDENTITY);

      public final Filter filter;
      public final PostProcessor postProcessor;

      private Mode(final PostProcessor postProcessor, final Filter filter) {
         this.postProcessor = postProcessor;
         this.filter = filter;
      }

      // $FF: synthetic method
      private static Mode[] method_36968() {
         return new Mode[]{REPLACE, OUTLINE, HOLLOW, DESTROY};
      }
   }

   @FunctionalInterface
   public interface PostProcessor {
      PostProcessor EMPTY = (world, pos) -> {
         return false;
      };

      boolean affect(ServerWorld world, BlockPos pos);
   }

   @FunctionalInterface
   public interface Filter {
      Filter IDENTITY = (box, pos, block, world) -> {
         return block;
      };

      @Nullable
      BlockStateArgument filter(BlockBox box, BlockPos pos, BlockStateArgument block, ServerWorld world);
   }
}
