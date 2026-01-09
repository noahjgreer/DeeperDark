package net.minecraft.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.component.ComponentType;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.component.ComponentPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ItemPredicateArgumentType extends ParserBackedArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo:'bar'}");
   static final DynamicCommandExceptionType INVALID_ITEM_ID_EXCEPTION = new DynamicCommandExceptionType((id) -> {
      return Text.stringifiedTranslatable("argument.item.id.invalid", id);
   });
   static final DynamicCommandExceptionType UNKNOWN_ITEM_TAG_EXCEPTION = new DynamicCommandExceptionType((tag) -> {
      return Text.stringifiedTranslatable("arguments.item.tag.unknown", tag);
   });
   static final DynamicCommandExceptionType UNKNOWN_ITEM_COMPONENT_EXCEPTION = new DynamicCommandExceptionType((component) -> {
      return Text.stringifiedTranslatable("arguments.item.component.unknown", component);
   });
   static final Dynamic2CommandExceptionType MALFORMED_ITEM_COMPONENT_EXCEPTION = new Dynamic2CommandExceptionType((componentId, component) -> {
      return Text.stringifiedTranslatable("arguments.item.component.malformed", componentId, component);
   });
   static final DynamicCommandExceptionType UNKNOWN_ITEM_PREDICATE_EXCEPTION = new DynamicCommandExceptionType((predicate) -> {
      return Text.stringifiedTranslatable("arguments.item.predicate.unknown", predicate);
   });
   static final Dynamic2CommandExceptionType MALFORMED_ITEM_PREDICATE_EXCEPTION = new Dynamic2CommandExceptionType((predicateId, predicate) -> {
      return Text.stringifiedTranslatable("arguments.item.predicate.malformed", predicateId, predicate);
   });
   private static final Identifier COUNT_ID = Identifier.ofVanilla("count");
   static final Map SPECIAL_COMPONENT_CHECKS;
   static final Map SPECIAL_SUB_PREDICATE_CHECKS;

   public ItemPredicateArgumentType(CommandRegistryAccess commandRegistryAccess) {
      super(ItemPredicateParsing.createParser(new Context(commandRegistryAccess)).map((list) -> {
         Predicate var10000 = Util.allOf(list);
         Objects.requireNonNull(var10000);
         return var10000::test;
      }));
   }

   public static ItemPredicateArgumentType itemPredicate(CommandRegistryAccess commandRegistryAccess) {
      return new ItemPredicateArgumentType(commandRegistryAccess);
   }

   public static ItemStackPredicateArgument getItemStackPredicate(CommandContext context, String name) {
      return (ItemStackPredicateArgument)context.getArgument(name, ItemStackPredicateArgument.class);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   static {
      SPECIAL_COMPONENT_CHECKS = (Map)Stream.of(new ComponentCheck(COUNT_ID, (stack) -> {
         return true;
      }, NumberRange.IntRange.CODEC.map((range) -> {
         return (stack) -> {
            return range.test(stack.getCount());
         };
      }))).collect(Collectors.toUnmodifiableMap(ComponentCheck::id, (check) -> {
         return check;
      }));
      SPECIAL_SUB_PREDICATE_CHECKS = (Map)Stream.of(new SubPredicateCheck(COUNT_ID, NumberRange.IntRange.CODEC.map((range) -> {
         return (stack) -> {
            return range.test(stack.getCount());
         };
      }))).collect(Collectors.toUnmodifiableMap(SubPredicateCheck::id, (check) -> {
         return check;
      }));
   }

   static class Context implements ItemPredicateParsing.Callbacks {
      private final RegistryWrapper.WrapperLookup registries;
      private final RegistryWrapper.Impl itemRegistryWrapper;
      private final RegistryWrapper.Impl dataComponentTypeRegistryWrapper;
      private final RegistryWrapper.Impl itemSubPredicateTypeRegistryWrapper;

      Context(RegistryWrapper.WrapperLookup registries) {
         this.registries = registries;
         this.itemRegistryWrapper = registries.getOrThrow(RegistryKeys.ITEM);
         this.dataComponentTypeRegistryWrapper = registries.getOrThrow(RegistryKeys.DATA_COMPONENT_TYPE);
         this.itemSubPredicateTypeRegistryWrapper = registries.getOrThrow(RegistryKeys.DATA_COMPONENT_PREDICATE_TYPE);
      }

      public Predicate itemMatchPredicate(ImmutableStringReader immutableStringReader, Identifier identifier) throws CommandSyntaxException {
         RegistryEntry.Reference reference = (RegistryEntry.Reference)this.itemRegistryWrapper.getOptional(RegistryKey.of(RegistryKeys.ITEM, identifier)).orElseThrow(() -> {
            return ItemPredicateArgumentType.INVALID_ITEM_ID_EXCEPTION.createWithContext(immutableStringReader, identifier);
         });
         return (stack) -> {
            return stack.itemMatches((RegistryEntry)reference);
         };
      }

      public Predicate tagMatchPredicate(ImmutableStringReader immutableStringReader, Identifier identifier) throws CommandSyntaxException {
         RegistryEntryList registryEntryList = (RegistryEntryList)this.itemRegistryWrapper.getOptional(TagKey.of(RegistryKeys.ITEM, identifier)).orElseThrow(() -> {
            return ItemPredicateArgumentType.UNKNOWN_ITEM_TAG_EXCEPTION.createWithContext(immutableStringReader, identifier);
         });
         return (stack) -> {
            return stack.isIn(registryEntryList);
         };
      }

      public ComponentCheck componentCheck(ImmutableStringReader immutableStringReader, Identifier identifier) throws CommandSyntaxException {
         ComponentCheck componentCheck = (ComponentCheck)ItemPredicateArgumentType.SPECIAL_COMPONENT_CHECKS.get(identifier);
         if (componentCheck != null) {
            return componentCheck;
         } else {
            ComponentType componentType = (ComponentType)this.dataComponentTypeRegistryWrapper.getOptional(RegistryKey.of(RegistryKeys.DATA_COMPONENT_TYPE, identifier)).map(RegistryEntry::value).orElseThrow(() -> {
               return ItemPredicateArgumentType.UNKNOWN_ITEM_COMPONENT_EXCEPTION.createWithContext(immutableStringReader, identifier);
            });
            return ItemPredicateArgumentType.ComponentCheck.read(immutableStringReader, identifier, componentType);
         }
      }

      public Predicate componentMatchPredicate(ImmutableStringReader immutableStringReader, ComponentCheck componentCheck, Dynamic dynamic) throws CommandSyntaxException {
         return componentCheck.createPredicate(immutableStringReader, RegistryOps.withRegistry(dynamic, this.registries));
      }

      public Predicate componentPresencePredicate(ImmutableStringReader immutableStringReader, ComponentCheck componentCheck) {
         return componentCheck.presenceChecker;
      }

      public SubPredicateCheck subPredicateCheck(ImmutableStringReader immutableStringReader, Identifier identifier) throws CommandSyntaxException {
         SubPredicateCheck subPredicateCheck = (SubPredicateCheck)ItemPredicateArgumentType.SPECIAL_SUB_PREDICATE_CHECKS.get(identifier);
         return subPredicateCheck != null ? subPredicateCheck : (SubPredicateCheck)this.itemSubPredicateTypeRegistryWrapper.getOptional(RegistryKey.of(RegistryKeys.DATA_COMPONENT_PREDICATE_TYPE, identifier)).map(SubPredicateCheck::new).orElseThrow(() -> {
            return ItemPredicateArgumentType.UNKNOWN_ITEM_PREDICATE_EXCEPTION.createWithContext(immutableStringReader, identifier);
         });
      }

      public Predicate subPredicatePredicate(ImmutableStringReader immutableStringReader, SubPredicateCheck subPredicateCheck, Dynamic dynamic) throws CommandSyntaxException {
         return subPredicateCheck.createPredicate(immutableStringReader, RegistryOps.withRegistry(dynamic, this.registries));
      }

      public Stream streamItemIds() {
         return this.itemRegistryWrapper.streamKeys().map(RegistryKey::getValue);
      }

      public Stream streamTags() {
         return this.itemRegistryWrapper.streamTagKeys().map(TagKey::id);
      }

      public Stream streamComponentIds() {
         return Stream.concat(ItemPredicateArgumentType.SPECIAL_COMPONENT_CHECKS.keySet().stream(), this.dataComponentTypeRegistryWrapper.streamEntries().filter((entry) -> {
            return !((ComponentType)entry.value()).shouldSkipSerialization();
         }).map((entry) -> {
            return entry.registryKey().getValue();
         }));
      }

      public Stream streamSubPredicateIds() {
         return Stream.concat(ItemPredicateArgumentType.SPECIAL_SUB_PREDICATE_CHECKS.keySet().stream(), this.itemSubPredicateTypeRegistryWrapper.streamKeys().map(RegistryKey::getValue));
      }

      public Predicate negate(Predicate predicate) {
         return predicate.negate();
      }

      public Predicate anyOf(List list) {
         return Util.anyOf(list);
      }

      // $FF: synthetic method
      public Object anyOf(final List predicates) {
         return this.anyOf(predicates);
      }

      // $FF: synthetic method
      public Object subPredicatePredicate(final ImmutableStringReader reader, final Object check, final Dynamic dynamic) throws CommandSyntaxException {
         return this.subPredicatePredicate(reader, (SubPredicateCheck)check, dynamic);
      }

      // $FF: synthetic method
      public Object subPredicateCheck(final ImmutableStringReader reader, final Identifier id) throws CommandSyntaxException {
         return this.subPredicateCheck(reader, id);
      }

      // $FF: synthetic method
      public Object componentCheck(final ImmutableStringReader reader, final Identifier id) throws CommandSyntaxException {
         return this.componentCheck(reader, id);
      }

      // $FF: synthetic method
      public Object tagMatchPredicate(final ImmutableStringReader reader, final Identifier id) throws CommandSyntaxException {
         return this.tagMatchPredicate(reader, id);
      }

      // $FF: synthetic method
      public Object itemMatchPredicate(final ImmutableStringReader reader, final Identifier id) throws CommandSyntaxException {
         return this.itemMatchPredicate(reader, id);
      }
   }

   public interface ItemStackPredicateArgument extends Predicate {
   }

   private static record ComponentCheck(Identifier id, Predicate presenceChecker, Decoder valueChecker) {
      final Predicate presenceChecker;

      ComponentCheck(Identifier identifier, Predicate predicate, Decoder decoder) {
         this.id = identifier;
         this.presenceChecker = predicate;
         this.valueChecker = decoder;
      }

      public static ComponentCheck read(ImmutableStringReader reader, Identifier id, ComponentType type) throws CommandSyntaxException {
         Codec codec = type.getCodec();
         if (codec == null) {
            throw ItemPredicateArgumentType.UNKNOWN_ITEM_COMPONENT_EXCEPTION.createWithContext(reader, id);
         } else {
            return new ComponentCheck(id, (stack) -> {
               return stack.contains(type);
            }, codec.map((expected) -> {
               return (stack) -> {
                  Object object2 = stack.get(type);
                  return Objects.equals(expected, object2);
               };
            }));
         }
      }

      public Predicate createPredicate(ImmutableStringReader reader, Dynamic value) throws CommandSyntaxException {
         DataResult dataResult = this.valueChecker.parse(value);
         return (Predicate)dataResult.getOrThrow((error) -> {
            return ItemPredicateArgumentType.MALFORMED_ITEM_COMPONENT_EXCEPTION.createWithContext(reader, this.id.toString(), error);
         });
      }

      public Identifier id() {
         return this.id;
      }

      public Predicate presenceChecker() {
         return this.presenceChecker;
      }

      public Decoder valueChecker() {
         return this.valueChecker;
      }
   }

   private static record SubPredicateCheck(Identifier id, Decoder type) {
      public SubPredicateCheck(RegistryEntry.Reference type) {
         this(type.registryKey().getValue(), ((ComponentPredicate.Type)type.value()).getPredicateCodec().map((predicate) -> {
            Objects.requireNonNull(predicate);
            return predicate::test;
         }));
      }

      SubPredicateCheck(Identifier identifier, Decoder decoder) {
         this.id = identifier;
         this.type = decoder;
      }

      public Predicate createPredicate(ImmutableStringReader reader, Dynamic value) throws CommandSyntaxException {
         DataResult dataResult = this.type.parse(value);
         return (Predicate)dataResult.getOrThrow((error) -> {
            return ItemPredicateArgumentType.MALFORMED_ITEM_PREDICATE_EXCEPTION.createWithContext(reader, this.id.toString(), error);
         });
      }

      public Identifier id() {
         return this.id;
      }

      public Decoder type() {
         return this.type;
      }
   }
}
