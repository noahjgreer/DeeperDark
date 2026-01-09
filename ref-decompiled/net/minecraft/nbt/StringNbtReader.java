package net.minecraft.nbt;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Objects;
import net.minecraft.text.Text;
import net.minecraft.util.packrat.PackratParser;

public class StringNbtReader {
   public static final SimpleCommandExceptionType TRAILING = new SimpleCommandExceptionType(Text.translatable("argument.nbt.trailing"));
   public static final SimpleCommandExceptionType EXPECTED_COMPOUND = new SimpleCommandExceptionType(Text.translatable("argument.nbt.expected.compound"));
   public static final char COMMA = ',';
   public static final char COLON = ':';
   private static final StringNbtReader DEFAULT_READER;
   public static final Codec STRINGIFIED_CODEC;
   public static final Codec NBT_COMPOUND_CODEC;
   private final DynamicOps ops;
   private final PackratParser parser;

   private StringNbtReader(DynamicOps ops, PackratParser parser) {
      this.ops = ops;
      this.parser = parser;
   }

   public DynamicOps getOps() {
      return this.ops;
   }

   public static StringNbtReader fromOps(DynamicOps ops) {
      return new StringNbtReader(ops, SnbtParsing.createParser(ops));
   }

   private static NbtCompound expectCompound(StringReader reader, NbtElement nbtElement) throws CommandSyntaxException {
      if (nbtElement instanceof NbtCompound nbtCompound) {
         return nbtCompound;
      } else {
         throw EXPECTED_COMPOUND.createWithContext(reader);
      }
   }

   public static NbtCompound readCompound(String snbt) throws CommandSyntaxException {
      StringReader stringReader = new StringReader(snbt);
      return expectCompound(stringReader, (NbtElement)DEFAULT_READER.read(stringReader));
   }

   public Object read(String snbt) throws CommandSyntaxException {
      return this.read(new StringReader(snbt));
   }

   public Object read(StringReader reader) throws CommandSyntaxException {
      Object object = this.parser.parse(reader);
      reader.skipWhitespace();
      if (reader.canRead()) {
         throw TRAILING.createWithContext(reader);
      } else {
         return object;
      }
   }

   public Object readAsArgument(StringReader reader) throws CommandSyntaxException {
      return this.parser.parse(reader);
   }

   public static NbtCompound readCompoundAsArgument(StringReader reader) throws CommandSyntaxException {
      NbtElement nbtElement = (NbtElement)DEFAULT_READER.readAsArgument(reader);
      return expectCompound(reader, nbtElement);
   }

   static {
      DEFAULT_READER = fromOps(NbtOps.INSTANCE);
      STRINGIFIED_CODEC = Codec.STRING.comapFlatMap((snbt) -> {
         try {
            NbtElement nbtElement = (NbtElement)DEFAULT_READER.read(snbt);
            if (nbtElement instanceof NbtCompound nbtCompound) {
               return DataResult.success(nbtCompound, Lifecycle.stable());
            } else {
               return DataResult.error(() -> {
                  return "Expected compound tag, got " + String.valueOf(nbtElement);
               });
            }
         } catch (CommandSyntaxException var3) {
            Objects.requireNonNull(var3);
            return DataResult.error(var3::getMessage);
         }
      }, NbtCompound::toString);
      NBT_COMPOUND_CODEC = Codec.withAlternative(STRINGIFIED_CODEC, NbtCompound.CODEC);
   }
}
