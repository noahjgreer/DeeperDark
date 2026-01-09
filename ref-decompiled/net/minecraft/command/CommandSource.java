package net.minecraft.command;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;

public interface CommandSource {
   CharMatcher SUGGESTION_MATCH_PREFIX = CharMatcher.anyOf("._/");

   Collection getPlayerNames();

   default Collection getChatSuggestions() {
      return this.getPlayerNames();
   }

   default Collection getEntitySuggestions() {
      return Collections.emptyList();
   }

   Collection getTeamNames();

   Stream getSoundIds();

   CompletableFuture getCompletions(CommandContext context);

   default Collection getBlockPositionSuggestions() {
      return Collections.singleton(CommandSource.RelativePosition.ZERO_WORLD);
   }

   default Collection getPositionSuggestions() {
      return Collections.singleton(CommandSource.RelativePosition.ZERO_WORLD);
   }

   Set getWorldKeys();

   DynamicRegistryManager getRegistryManager();

   FeatureSet getEnabledFeatures();

   default void suggestIdentifiers(RegistryWrapper registry, SuggestedIdType suggestedIdType, SuggestionsBuilder builder) {
      if (suggestedIdType.canSuggestTags()) {
         suggestIdentifiers(registry.streamTagKeys().map(TagKey::id), builder, "#");
      }

      if (suggestedIdType.canSuggestElements()) {
         suggestIdentifiers(registry.streamKeys().map(RegistryKey::getValue), builder);
      }

   }

   static CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder, RegistryKey registryRef, SuggestedIdType suggestedIdType) {
      Object var5 = context.getSource();
      if (var5 instanceof CommandSource commandSource) {
         return commandSource.listIdSuggestions(registryRef, suggestedIdType, builder, context);
      } else {
         return builder.buildFuture();
      }
   }

   CompletableFuture listIdSuggestions(RegistryKey registryRef, SuggestedIdType suggestedIdType, SuggestionsBuilder builder, CommandContext context);

   static void forEachMatching(Iterable candidates, String remaining, Function identifier, Consumer action) {
      boolean bl = remaining.indexOf(58) > -1;
      Iterator var5 = candidates.iterator();

      while(true) {
         while(var5.hasNext()) {
            Object object = var5.next();
            Identifier identifier2 = (Identifier)identifier.apply(object);
            if (bl) {
               String string = identifier2.toString();
               if (shouldSuggest(remaining, string)) {
                  action.accept(object);
               }
            } else if (shouldSuggest(remaining, identifier2.getNamespace()) || identifier2.getNamespace().equals("minecraft") && shouldSuggest(remaining, identifier2.getPath())) {
               action.accept(object);
            }
         }

         return;
      }
   }

   static void forEachMatching(Iterable candidates, String remaining, String prefix, Function identifier, Consumer action) {
      if (remaining.isEmpty()) {
         candidates.forEach(action);
      } else {
         String string = Strings.commonPrefix(remaining, prefix);
         if (!string.isEmpty()) {
            String string2 = remaining.substring(string.length());
            forEachMatching(candidates, string2, identifier, action);
         }
      }

   }

   static CompletableFuture suggestIdentifiers(Iterable candidates, SuggestionsBuilder builder, String prefix) {
      String string = builder.getRemaining().toLowerCase(Locale.ROOT);
      forEachMatching(candidates, string, prefix, (id) -> {
         return id;
      }, (id) -> {
         builder.suggest(prefix + String.valueOf(id));
      });
      return builder.buildFuture();
   }

   static CompletableFuture suggestIdentifiers(Stream candidates, SuggestionsBuilder builder, String prefix) {
      Objects.requireNonNull(candidates);
      return suggestIdentifiers(candidates::iterator, builder, prefix);
   }

   static CompletableFuture suggestIdentifiers(Iterable candidates, SuggestionsBuilder builder) {
      String string = builder.getRemaining().toLowerCase(Locale.ROOT);
      forEachMatching(candidates, string, (id) -> {
         return id;
      }, (id) -> {
         builder.suggest(id.toString());
      });
      return builder.buildFuture();
   }

   static CompletableFuture suggestFromIdentifier(Iterable candidates, SuggestionsBuilder builder, Function identifier, Function tooltip) {
      String string = builder.getRemaining().toLowerCase(Locale.ROOT);
      forEachMatching(candidates, string, identifier, (object) -> {
         builder.suggest(((Identifier)identifier.apply(object)).toString(), (Message)tooltip.apply(object));
      });
      return builder.buildFuture();
   }

   static CompletableFuture suggestIdentifiers(Stream candidates, SuggestionsBuilder builder) {
      Objects.requireNonNull(candidates);
      return suggestIdentifiers(candidates::iterator, builder);
   }

   static CompletableFuture suggestFromIdentifier(Stream candidates, SuggestionsBuilder builder, Function identifier, Function tooltip) {
      Objects.requireNonNull(candidates);
      return suggestFromIdentifier(candidates::iterator, builder, identifier, tooltip);
   }

   static CompletableFuture suggestPositions(String remaining, Collection candidates, SuggestionsBuilder builder, Predicate predicate) {
      List list = Lists.newArrayList();
      if (Strings.isNullOrEmpty(remaining)) {
         Iterator var5 = candidates.iterator();

         while(var5.hasNext()) {
            RelativePosition relativePosition = (RelativePosition)var5.next();
            String string = relativePosition.x + " " + relativePosition.y + " " + relativePosition.z;
            if (predicate.test(string)) {
               list.add(relativePosition.x);
               list.add(relativePosition.x + " " + relativePosition.y);
               list.add(string);
            }
         }
      } else {
         String[] strings = remaining.split(" ");
         String string2;
         Iterator var10;
         RelativePosition relativePosition2;
         if (strings.length == 1) {
            var10 = candidates.iterator();

            while(var10.hasNext()) {
               relativePosition2 = (RelativePosition)var10.next();
               string2 = strings[0] + " " + relativePosition2.y + " " + relativePosition2.z;
               if (predicate.test(string2)) {
                  list.add(strings[0] + " " + relativePosition2.y);
                  list.add(string2);
               }
            }
         } else if (strings.length == 2) {
            var10 = candidates.iterator();

            while(var10.hasNext()) {
               relativePosition2 = (RelativePosition)var10.next();
               string2 = strings[0] + " " + strings[1] + " " + relativePosition2.z;
               if (predicate.test(string2)) {
                  list.add(string2);
               }
            }
         }
      }

      return suggestMatching((Iterable)list, builder);
   }

   static CompletableFuture suggestColumnPositions(String remaining, Collection candidates, SuggestionsBuilder builder, Predicate predicate) {
      List list = Lists.newArrayList();
      if (Strings.isNullOrEmpty(remaining)) {
         Iterator var5 = candidates.iterator();

         while(var5.hasNext()) {
            RelativePosition relativePosition = (RelativePosition)var5.next();
            String string = relativePosition.x + " " + relativePosition.z;
            if (predicate.test(string)) {
               list.add(relativePosition.x);
               list.add(string);
            }
         }
      } else {
         String[] strings = remaining.split(" ");
         if (strings.length == 1) {
            Iterator var10 = candidates.iterator();

            while(var10.hasNext()) {
               RelativePosition relativePosition2 = (RelativePosition)var10.next();
               String string2 = strings[0] + " " + relativePosition2.z;
               if (predicate.test(string2)) {
                  list.add(string2);
               }
            }
         }
      }

      return suggestMatching((Iterable)list, builder);
   }

   static CompletableFuture suggestMatching(Iterable candidates, SuggestionsBuilder builder) {
      String string = builder.getRemaining().toLowerCase(Locale.ROOT);
      Iterator var3 = candidates.iterator();

      while(var3.hasNext()) {
         String string2 = (String)var3.next();
         if (shouldSuggest(string, string2.toLowerCase(Locale.ROOT))) {
            builder.suggest(string2);
         }
      }

      return builder.buildFuture();
   }

   static CompletableFuture suggestMatching(Stream candidates, SuggestionsBuilder builder) {
      String string = builder.getRemaining().toLowerCase(Locale.ROOT);
      Stream var10000 = candidates.filter((candidate) -> {
         return shouldSuggest(string, candidate.toLowerCase(Locale.ROOT));
      });
      Objects.requireNonNull(builder);
      var10000.forEach(builder::suggest);
      return builder.buildFuture();
   }

   static CompletableFuture suggestMatching(String[] candidates, SuggestionsBuilder builder) {
      String string = builder.getRemaining().toLowerCase(Locale.ROOT);
      String[] var3 = candidates;
      int var4 = candidates.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String string2 = var3[var5];
         if (shouldSuggest(string, string2.toLowerCase(Locale.ROOT))) {
            builder.suggest(string2);
         }
      }

      return builder.buildFuture();
   }

   static CompletableFuture suggestMatching(Iterable candidates, SuggestionsBuilder builder, Function suggestionText, Function tooltip) {
      String string = builder.getRemaining().toLowerCase(Locale.ROOT);
      Iterator var5 = candidates.iterator();

      while(var5.hasNext()) {
         Object object = var5.next();
         String string2 = (String)suggestionText.apply(object);
         if (shouldSuggest(string, string2.toLowerCase(Locale.ROOT))) {
            builder.suggest(string2, (Message)tooltip.apply(object));
         }
      }

      return builder.buildFuture();
   }

   static boolean shouldSuggest(String remaining, String candidate) {
      int j;
      for(int i = 0; !candidate.startsWith(remaining, i); i = j + 1) {
         j = SUGGESTION_MATCH_PREFIX.indexIn(candidate, i);
         if (j < 0) {
            return false;
         }
      }

      return true;
   }

   public static class RelativePosition {
      public static final RelativePosition ZERO_LOCAL = new RelativePosition("^", "^", "^");
      public static final RelativePosition ZERO_WORLD = new RelativePosition("~", "~", "~");
      public final String x;
      public final String y;
      public final String z;

      public RelativePosition(String x, String y, String z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }
   }

   public static enum SuggestedIdType {
      TAGS,
      ELEMENTS,
      ALL;

      public boolean canSuggestTags() {
         return this == TAGS || this == ALL;
      }

      public boolean canSuggestElements() {
         return this == ELEMENTS || this == ALL;
      }

      // $FF: synthetic method
      private static SuggestedIdType[] method_41217() {
         return new SuggestedIdType[]{TAGS, ELEMENTS, ALL};
      }
   }
}
