package net.minecraft.command.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class SuggestionProviders {
   private static final Map REGISTRY = new HashMap();
   private static final Identifier ASK_SERVER_ID = Identifier.ofVanilla("ask_server");
   public static final SuggestionProvider ASK_SERVER;
   public static final SuggestionProvider AVAILABLE_SOUNDS;
   public static final SuggestionProvider SUMMONABLE_ENTITIES;

   public static SuggestionProvider register(Identifier id, SuggestionProvider provider) {
      SuggestionProvider suggestionProvider = (SuggestionProvider)REGISTRY.putIfAbsent(id, provider);
      if (suggestionProvider != null) {
         throw new IllegalArgumentException("A command suggestion provider is already registered with the name '" + String.valueOf(id) + "'");
      } else {
         return new LocalProvider(id, provider);
      }
   }

   public static SuggestionProvider cast(SuggestionProvider suggestionProvider) {
      return suggestionProvider;
   }

   public static SuggestionProvider byId(Identifier id) {
      return cast((SuggestionProvider)REGISTRY.getOrDefault(id, ASK_SERVER));
   }

   public static Identifier computeId(SuggestionProvider provider) {
      Identifier var10000;
      if (provider instanceof LocalProvider localProvider) {
         var10000 = localProvider.id;
      } else {
         var10000 = ASK_SERVER_ID;
      }

      return var10000;
   }

   static {
      ASK_SERVER = register(ASK_SERVER_ID, (context, builder) -> {
         return ((CommandSource)context.getSource()).getCompletions(context);
      });
      AVAILABLE_SOUNDS = register(Identifier.ofVanilla("available_sounds"), (context, builder) -> {
         return CommandSource.suggestIdentifiers(((CommandSource)context.getSource()).getSoundIds(), builder);
      });
      SUMMONABLE_ENTITIES = register(Identifier.ofVanilla("summonable_entities"), (context, builder) -> {
         return CommandSource.suggestFromIdentifier(Registries.ENTITY_TYPE.stream().filter((entityType) -> {
            return entityType.isEnabled(((CommandSource)context.getSource()).getEnabledFeatures()) && entityType.isSummonable();
         }), builder, EntityType::getId, EntityType::getName);
      });
   }

   private static record LocalProvider(Identifier id, SuggestionProvider provider) implements SuggestionProvider {
      final Identifier id;

      LocalProvider(Identifier identifier, SuggestionProvider suggestionProvider) {
         this.id = identifier;
         this.provider = suggestionProvider;
      }

      public CompletableFuture getSuggestions(CommandContext context, SuggestionsBuilder builder) throws CommandSyntaxException {
         return this.provider.getSuggestions(context, builder);
      }

      public Identifier id() {
         return this.id;
      }

      public SuggestionProvider provider() {
         return this.provider;
      }
   }
}
