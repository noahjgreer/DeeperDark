package net.minecraft.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtParsing;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.packrat.AnyIdParsingRule;
import net.minecraft.util.packrat.PackratParser;
import net.minecraft.util.packrat.ParsingRuleEntry;
import net.minecraft.util.packrat.ParsingRules;
import net.minecraft.util.packrat.Symbol;
import net.minecraft.util.packrat.Term;
import org.jetbrains.annotations.Nullable;

public class RegistryEntryArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = List.of("foo", "foo:bar", "012", "{}", "true");
   public static final DynamicCommandExceptionType FAILED_TO_PARSE_EXCEPTION = new DynamicCommandExceptionType((argument) -> {
      return Text.stringifiedTranslatable("argument.resource_or_id.failed_to_parse", argument);
   });
   public static final Dynamic2CommandExceptionType NO_SUCH_ELEMENT_EXCEPTION = new Dynamic2CommandExceptionType((key, registryRef) -> {
      return Text.stringifiedTranslatable("argument.resource_or_id.no_such_element", key, registryRef);
   });
   public static final DynamicOps OPS;
   private final RegistryWrapper.WrapperLookup registries;
   private final Optional registry;
   private final Codec entryCodec;
   private final PackratParser parser;
   private final RegistryKey registryRef;

   protected RegistryEntryArgumentType(CommandRegistryAccess registryAccess, RegistryKey registry, Codec entryCodec) {
      this.registries = registryAccess;
      this.registry = registryAccess.getOptional(registry);
      this.registryRef = registry;
      this.entryCodec = entryCodec;
      this.parser = createParser(registry, OPS);
   }

   public static PackratParser createParser(RegistryKey key, DynamicOps ops) {
      PackratParser packratParser = SnbtParsing.createParser(ops);
      ParsingRules parsingRules = new ParsingRules();
      Symbol symbol = Symbol.of("result");
      Symbol symbol2 = Symbol.of("id");
      Symbol symbol3 = Symbol.of("value");
      parsingRules.set(symbol2, AnyIdParsingRule.INSTANCE);
      parsingRules.set(symbol3, packratParser.top().getRule());
      ParsingRuleEntry parsingRuleEntry = parsingRules.set(symbol, Term.anyOf(parsingRules.term(symbol2), parsingRules.term(symbol3)), (results) -> {
         Identifier identifier = (Identifier)results.get(symbol2);
         if (identifier != null) {
            return new ReferenceParser(RegistryKey.of(key, identifier));
         } else {
            Object object = results.getOrThrow(symbol3);
            return new DirectParser(object);
         }
      });
      return new PackratParser(parsingRules, parsingRuleEntry);
   }

   public static LootTableArgumentType lootTable(CommandRegistryAccess registryAccess) {
      return new LootTableArgumentType(registryAccess);
   }

   public static RegistryEntry getLootTable(CommandContext context, String argument) throws CommandSyntaxException {
      return getArgument(context, argument);
   }

   public static LootFunctionArgumentType lootFunction(CommandRegistryAccess registryAccess) {
      return new LootFunctionArgumentType(registryAccess);
   }

   public static RegistryEntry getLootFunction(CommandContext context, String argument) {
      return getArgument(context, argument);
   }

   public static LootConditionArgumentType lootCondition(CommandRegistryAccess registryAccess) {
      return new LootConditionArgumentType(registryAccess);
   }

   public static RegistryEntry getLootCondition(CommandContext context, String argument) {
      return getArgument(context, argument);
   }

   public static DialogArgumentType dialog(CommandRegistryAccess registryAccess) {
      return new DialogArgumentType(registryAccess);
   }

   public static RegistryEntry getDialog(CommandContext context, String argument) {
      return getArgument(context, argument);
   }

   private static RegistryEntry getArgument(CommandContext context, String argument) {
      return (RegistryEntry)context.getArgument(argument, RegistryEntry.class);
   }

   @Nullable
   public RegistryEntry parse(StringReader stringReader) throws CommandSyntaxException {
      return this.parse(stringReader, this.parser, OPS);
   }

   @Nullable
   private RegistryEntry parse(StringReader reader, PackratParser parser, DynamicOps ops) throws CommandSyntaxException {
      EntryParser entryParser = (EntryParser)parser.parse(reader);
      return this.registry.isEmpty() ? null : entryParser.parse(reader, this.registries, ops, this.entryCodec, (RegistryWrapper.Impl)this.registry.get());
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder suggestionsBuilder) {
      return CommandSource.listSuggestions(context, suggestionsBuilder, this.registryRef, CommandSource.SuggestedIdType.ELEMENTS);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   @Nullable
   public Object parse(final StringReader stringReader) throws CommandSyntaxException {
      return this.parse(stringReader);
   }

   static {
      OPS = NbtOps.INSTANCE;
   }

   public static class LootTableArgumentType extends RegistryEntryArgumentType {
      protected LootTableArgumentType(CommandRegistryAccess registryAccess) {
         super(registryAccess, RegistryKeys.LOOT_TABLE, LootTable.CODEC);
      }

      // $FF: synthetic method
      @Nullable
      public Object parse(final StringReader stringReader) throws CommandSyntaxException {
         return super.parse(stringReader);
      }
   }

   public static class LootFunctionArgumentType extends RegistryEntryArgumentType {
      protected LootFunctionArgumentType(CommandRegistryAccess registryAccess) {
         super(registryAccess, RegistryKeys.ITEM_MODIFIER, LootFunctionTypes.CODEC);
      }

      // $FF: synthetic method
      @Nullable
      public Object parse(final StringReader stringReader) throws CommandSyntaxException {
         return super.parse(stringReader);
      }
   }

   public static class LootConditionArgumentType extends RegistryEntryArgumentType {
      protected LootConditionArgumentType(CommandRegistryAccess registryAccess) {
         super(registryAccess, RegistryKeys.PREDICATE, LootCondition.CODEC);
      }

      // $FF: synthetic method
      @Nullable
      public Object parse(final StringReader stringReader) throws CommandSyntaxException {
         return super.parse(stringReader);
      }
   }

   public static class DialogArgumentType extends RegistryEntryArgumentType {
      protected DialogArgumentType(CommandRegistryAccess registryAccess) {
         super(registryAccess, RegistryKeys.DIALOG, Dialog.CODEC);
      }

      // $FF: synthetic method
      @Nullable
      public Object parse(final StringReader stringReader) throws CommandSyntaxException {
         return super.parse(stringReader);
      }
   }

   public sealed interface EntryParser permits RegistryEntryArgumentType.DirectParser, RegistryEntryArgumentType.ReferenceParser {
      RegistryEntry parse(ImmutableStringReader reader, RegistryWrapper.WrapperLookup registries, DynamicOps ops, Codec codec, RegistryWrapper.Impl registryAccess) throws CommandSyntaxException;
   }

   public static record ReferenceParser(RegistryKey key) implements EntryParser {
      public ReferenceParser(RegistryKey registryKey) {
         this.key = registryKey;
      }

      public RegistryEntry parse(ImmutableStringReader reader, RegistryWrapper.WrapperLookup registries, DynamicOps ops, Codec codec, RegistryWrapper.Impl registryAccess) throws CommandSyntaxException {
         return (RegistryEntry)registryAccess.getOptional(this.key).orElseThrow(() -> {
            return RegistryEntryArgumentType.NO_SUCH_ELEMENT_EXCEPTION.createWithContext(reader, this.key.getValue(), this.key.getRegistry());
         });
      }

      public RegistryKey key() {
         return this.key;
      }
   }

   public static record DirectParser(Object value) implements EntryParser {
      public DirectParser(Object object) {
         this.value = object;
      }

      public RegistryEntry parse(ImmutableStringReader reader, RegistryWrapper.WrapperLookup registries, DynamicOps ops, Codec codec, RegistryWrapper.Impl registryAccess) throws CommandSyntaxException {
         return RegistryEntry.of(codec.parse(registries.getOps(ops), this.value).getOrThrow((error) -> {
            return RegistryEntryArgumentType.FAILED_TO_PARSE_EXCEPTION.createWithContext(reader, error);
         }));
      }

      public Object value() {
         return this.value;
      }
   }
}
