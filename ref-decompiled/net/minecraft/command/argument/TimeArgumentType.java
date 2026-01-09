package net.minecraft.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

public class TimeArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("0d", "0s", "0t", "0");
   private static final SimpleCommandExceptionType INVALID_UNIT_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.time.invalid_unit"));
   private static final Dynamic2CommandExceptionType TICK_COUNT_TOO_LOW_EXCEPTION = new Dynamic2CommandExceptionType((value, minimum) -> {
      return Text.stringifiedTranslatable("argument.time.tick_count_too_low", minimum, value);
   });
   private static final Object2IntMap UNITS = new Object2IntOpenHashMap();
   final int minimum;

   private TimeArgumentType(int minimum) {
      this.minimum = minimum;
   }

   public static TimeArgumentType time() {
      return new TimeArgumentType(0);
   }

   public static TimeArgumentType time(int minimum) {
      return new TimeArgumentType(minimum);
   }

   public Integer parse(StringReader stringReader) throws CommandSyntaxException {
      float f = stringReader.readFloat();
      String string = stringReader.readUnquotedString();
      int i = UNITS.getOrDefault(string, 0);
      if (i == 0) {
         throw INVALID_UNIT_EXCEPTION.createWithContext(stringReader);
      } else {
         int j = Math.round(f * (float)i);
         if (j < this.minimum) {
            throw TICK_COUNT_TOO_LOW_EXCEPTION.createWithContext(stringReader, j, this.minimum);
         } else {
            return j;
         }
      }
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      StringReader stringReader = new StringReader(builder.getRemaining());

      try {
         stringReader.readFloat();
      } catch (CommandSyntaxException var5) {
         return builder.buildFuture();
      }

      return CommandSource.suggestMatching((Iterable)UNITS.keySet(), builder.createOffset(builder.getStart() + stringReader.getCursor()));
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }

   static {
      UNITS.put("d", 24000);
      UNITS.put("s", 20);
      UNITS.put("t", 1);
      UNITS.put("", 1);
   }

   public static class Serializer implements ArgumentSerializer {
      public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
         packetByteBuf.writeInt(properties.minimum);
      }

      public Properties fromPacket(PacketByteBuf packetByteBuf) {
         int i = packetByteBuf.readInt();
         return new Properties(i);
      }

      public void writeJson(Properties properties, JsonObject jsonObject) {
         jsonObject.addProperty("min", properties.minimum);
      }

      public Properties getArgumentTypeProperties(TimeArgumentType timeArgumentType) {
         return new Properties(timeArgumentType.minimum);
      }

      // $FF: synthetic method
      public ArgumentSerializer.ArgumentTypeProperties fromPacket(final PacketByteBuf buf) {
         return this.fromPacket(buf);
      }

      public final class Properties implements ArgumentSerializer.ArgumentTypeProperties {
         final int minimum;

         Properties(final int minimum) {
            this.minimum = minimum;
         }

         public TimeArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return TimeArgumentType.time(this.minimum);
         }

         public ArgumentSerializer getSerializer() {
            return Serializer.this;
         }

         // $FF: synthetic method
         public ArgumentType createType(final CommandRegistryAccess commandRegistryAccess) {
            return this.createType(commandRegistryAccess);
         }
      }
   }
}
