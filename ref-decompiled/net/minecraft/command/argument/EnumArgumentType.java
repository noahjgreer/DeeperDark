package net.minecraft.command.argument;

import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public class EnumArgumentType implements ArgumentType {
   private static final DynamicCommandExceptionType INVALID_ENUM_EXCEPTION = new DynamicCommandExceptionType((value) -> {
      return Text.stringifiedTranslatable("argument.enum.invalid", value);
   });
   private final Codec codec;
   private final Supplier valuesSupplier;

   protected EnumArgumentType(Codec codec, Supplier valuesSupplier) {
      this.codec = codec;
      this.valuesSupplier = valuesSupplier;
   }

   public Enum parse(StringReader stringReader) throws CommandSyntaxException {
      String string = stringReader.readUnquotedString();
      return (Enum)this.codec.parse(JsonOps.INSTANCE, new JsonPrimitive(string)).result().orElseThrow(() -> {
         return INVALID_ENUM_EXCEPTION.createWithContext(stringReader, string);
      });
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      return CommandSource.suggestMatching((Iterable)Arrays.stream((Enum[])this.valuesSupplier.get()).map((enum_) -> {
         return ((StringIdentifiable)enum_).asString();
      }).map(this::transformValueName).collect(Collectors.toList()), builder);
   }

   public Collection getExamples() {
      return (Collection)Arrays.stream((Enum[])this.valuesSupplier.get()).map((enum_) -> {
         return ((StringIdentifiable)enum_).asString();
      }).map(this::transformValueName).limit(2L).collect(Collectors.toList());
   }

   protected String transformValueName(String name) {
      return name;
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }
}
