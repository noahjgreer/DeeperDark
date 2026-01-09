package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;

public class DebugMobSpawningCommand {
   public static void register(CommandDispatcher dispatcher) {
      LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("debugmobspawning").requires(CommandManager.requirePermissionLevel(2));
      SpawnGroup[] var2 = SpawnGroup.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SpawnGroup spawnGroup = var2[var4];
         literalArgumentBuilder.then(CommandManager.literal(spawnGroup.getName()).then(CommandManager.argument("at", BlockPosArgumentType.blockPos()).executes((context) -> {
            return execute((ServerCommandSource)context.getSource(), spawnGroup, BlockPosArgumentType.getLoadedBlockPos(context, "at"));
         })));
      }

      dispatcher.register(literalArgumentBuilder);
   }

   private static int execute(ServerCommandSource source, SpawnGroup group, BlockPos pos) {
      SpawnHelper.spawnEntitiesInChunk(group, source.getWorld(), pos);
      return 1;
   }
}
