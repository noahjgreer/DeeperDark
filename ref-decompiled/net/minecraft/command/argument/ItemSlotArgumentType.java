package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.inventory.SlotRange;
import net.minecraft.inventory.SlotRanges;
import net.minecraft.text.Text;

public class ItemSlotArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("container.5", "weapon");
   private static final DynamicCommandExceptionType UNKNOWN_SLOT_EXCEPTION = new DynamicCommandExceptionType((name) -> {
      return Text.stringifiedTranslatable("slot.unknown", name);
   });
   private static final DynamicCommandExceptionType ONLY_SINGLE_ALLOWED_EXCEPTION = new DynamicCommandExceptionType((name) -> {
      return Text.stringifiedTranslatable("slot.only_single_allowed", name);
   });

   public static ItemSlotArgumentType itemSlot() {
      return new ItemSlotArgumentType();
   }

   public static int getItemSlot(CommandContext context, String name) {
      return (Integer)context.getArgument(name, Integer.class);
   }

   public Integer parse(StringReader stringReader) throws CommandSyntaxException {
      String string = ArgumentReaderUtils.readWhileMatching(stringReader, (c) -> {
         return c != ' ';
      });
      SlotRange slotRange = SlotRanges.fromName(string);
      if (slotRange == null) {
         throw UNKNOWN_SLOT_EXCEPTION.createWithContext(stringReader, string);
      } else if (slotRange.getSlotCount() != 1) {
         throw ONLY_SINGLE_ALLOWED_EXCEPTION.createWithContext(stringReader, string);
      } else {
         return slotRange.getSlotIds().getInt(0);
      }
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      return CommandSource.suggestMatching(SlotRanges.streamSingleSlotNames(), builder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }
}
