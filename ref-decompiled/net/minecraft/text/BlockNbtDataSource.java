package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public record BlockNbtDataSource(String rawPos, @Nullable PosArgument pos) implements NbtDataSource {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.STRING.fieldOf("block").forGetter(BlockNbtDataSource::rawPos)).apply(instance, BlockNbtDataSource::new);
   });
   public static final NbtDataSource.Type TYPE;

   public BlockNbtDataSource(String rawPath) {
      this(rawPath, parsePos(rawPath));
   }

   public BlockNbtDataSource(String rawPath, @Nullable PosArgument posArgument) {
      this.rawPos = rawPath;
      this.pos = posArgument;
   }

   @Nullable
   private static PosArgument parsePos(String string) {
      try {
         return BlockPosArgumentType.blockPos().parse(new StringReader(string));
      } catch (CommandSyntaxException var2) {
         return null;
      }
   }

   public Stream get(ServerCommandSource source) {
      if (this.pos != null) {
         ServerWorld serverWorld = source.getWorld();
         BlockPos blockPos = this.pos.toAbsoluteBlockPos(source);
         if (serverWorld.isPosLoaded(blockPos)) {
            BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
            if (blockEntity != null) {
               return Stream.of(blockEntity.createNbtWithIdentifyingData(source.getRegistryManager()));
            }
         }
      }

      return Stream.empty();
   }

   public NbtDataSource.Type getType() {
      return TYPE;
   }

   public String toString() {
      return "block=" + this.rawPos;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof BlockNbtDataSource) {
            BlockNbtDataSource blockNbtDataSource = (BlockNbtDataSource)o;
            if (this.rawPos.equals(blockNbtDataSource.rawPos)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.rawPos.hashCode();
   }

   public String rawPos() {
      return this.rawPos;
   }

   @Nullable
   public PosArgument pos() {
      return this.pos;
   }

   static {
      TYPE = new NbtDataSource.Type(CODEC, "block");
   }
}
