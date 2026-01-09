package net.minecraft.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.storage.NbtReadView;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;

public class BlockDataObject implements DataCommandObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   static final SimpleCommandExceptionType INVALID_BLOCK_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.data.block.invalid"));
   public static final Function TYPE_FACTORY = (argumentName) -> {
      return new DataCommand.ObjectType() {
         public DataCommandObject getObject(CommandContext context) throws CommandSyntaxException {
            BlockPos blockPos = BlockPosArgumentType.getLoadedBlockPos(context, argumentName + "Pos");
            BlockEntity blockEntity = ((ServerCommandSource)context.getSource()).getWorld().getBlockEntity(blockPos);
            if (blockEntity == null) {
               throw BlockDataObject.INVALID_BLOCK_EXCEPTION.create();
            } else {
               return new BlockDataObject(blockEntity, blockPos);
            }
         }

         public ArgumentBuilder addArgumentsToBuilder(ArgumentBuilder argument, Function argumentAdder) {
            return argument.then(CommandManager.literal("block").then((ArgumentBuilder)argumentAdder.apply(CommandManager.argument(argumentName + "Pos", BlockPosArgumentType.blockPos()))));
         }
      };
   };
   private final BlockEntity blockEntity;
   private final BlockPos pos;

   public BlockDataObject(BlockEntity blockEntity, BlockPos pos) {
      this.blockEntity = blockEntity;
      this.pos = pos;
   }

   public void setNbt(NbtCompound nbt) {
      BlockState blockState = this.blockEntity.getWorld().getBlockState(this.pos);
      ErrorReporter.Logging logging = new ErrorReporter.Logging(this.blockEntity.getReporterContext(), LOGGER);

      try {
         this.blockEntity.read(NbtReadView.create(logging, this.blockEntity.getWorld().getRegistryManager(), nbt));
         this.blockEntity.markDirty();
         this.blockEntity.getWorld().updateListeners(this.pos, blockState, blockState, 3);
      } catch (Throwable var7) {
         try {
            logging.close();
         } catch (Throwable var6) {
            var7.addSuppressed(var6);
         }

         throw var7;
      }

      logging.close();
   }

   public NbtCompound getNbt() {
      return this.blockEntity.createNbtWithIdentifyingData(this.blockEntity.getWorld().getRegistryManager());
   }

   public Text feedbackModify() {
      return Text.translatable("commands.data.block.modified", this.pos.getX(), this.pos.getY(), this.pos.getZ());
   }

   public Text feedbackQuery(NbtElement element) {
      return Text.translatable("commands.data.block.query", this.pos.getX(), this.pos.getY(), this.pos.getZ(), NbtHelper.toPrettyPrintedText(element));
   }

   public Text feedbackGet(NbtPathArgumentType.NbtPath path, double scale, int result) {
      return Text.translatable("commands.data.block.get", path.getString(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), String.format(Locale.ROOT, "%.2f", scale), result);
   }
}
