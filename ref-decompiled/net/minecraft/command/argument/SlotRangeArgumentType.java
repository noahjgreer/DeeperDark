package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.inventory.SlotRange;
import net.minecraft.inventory.SlotRanges;
import net.minecraft.text.Text;

public class SlotRangeArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = List.of("container.*", "container.5", "weapon");
   private static final DynamicCommandExceptionType UNKNOWN_SLOT_EXCEPTION = new DynamicCommandExceptionType((slotRange) -> {
      return Text.stringifiedTranslatable("slot.unknown", slotRange);
   });

   public static SlotRangeArgumentType slotRange() {
      return new SlotRangeArgumentType();
   }

   public static SlotRange getSlotRange(CommandContext context, String name) {
      return (SlotRange)context.getArgument(name, SlotRange.class);
   }

   public SlotRange parse(StringReader stringReader) throws CommandSyntaxException {
      String string = ArgumentReaderUtils.readWhileMatching(stringReader, (c) -> {
         return c != ' ';
      });
      SlotRange slotRange = SlotRanges.fromName(string);
      if (slotRange == null) {
         throw UNKNOWN_SLOT_EXCEPTION.createWithContext(stringReader, string);
      } else {
         return slotRange;
      }
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder suggestionsBuilder) {
      return CommandSource.suggestMatching(SlotRanges.streamNames(), suggestionsBuilder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader stringReader) throws CommandSyntaxException {
      return this.parse(stringReader);
   }
}
