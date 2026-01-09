package net.minecraft.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.logging.LogUtils;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import org.slf4j.Logger;

public class EntitySelectorOptions {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map OPTIONS = Maps.newHashMap();
   public static final DynamicCommandExceptionType UNKNOWN_OPTION_EXCEPTION = new DynamicCommandExceptionType((option) -> {
      return Text.stringifiedTranslatable("argument.entity.options.unknown", option);
   });
   public static final DynamicCommandExceptionType INAPPLICABLE_OPTION_EXCEPTION = new DynamicCommandExceptionType((option) -> {
      return Text.stringifiedTranslatable("argument.entity.options.inapplicable", option);
   });
   public static final SimpleCommandExceptionType NEGATIVE_DISTANCE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.entity.options.distance.negative"));
   public static final SimpleCommandExceptionType NEGATIVE_LEVEL_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.entity.options.level.negative"));
   public static final SimpleCommandExceptionType TOO_SMALL_LEVEL_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.entity.options.limit.toosmall"));
   public static final DynamicCommandExceptionType IRREVERSIBLE_SORT_EXCEPTION = new DynamicCommandExceptionType((sortType) -> {
      return Text.stringifiedTranslatable("argument.entity.options.sort.irreversible", sortType);
   });
   public static final DynamicCommandExceptionType INVALID_MODE_EXCEPTION = new DynamicCommandExceptionType((gameMode) -> {
      return Text.stringifiedTranslatable("argument.entity.options.mode.invalid", gameMode);
   });
   public static final DynamicCommandExceptionType INVALID_TYPE_EXCEPTION = new DynamicCommandExceptionType((entity) -> {
      return Text.stringifiedTranslatable("argument.entity.options.type.invalid", entity);
   });

   private static void putOption(String id, SelectorHandler handler, Predicate condition, Text description) {
      OPTIONS.put(id, new SelectorOption(handler, condition, description));
   }

   public static void register() {
      if (OPTIONS.isEmpty()) {
         putOption("name", (reader) -> {
            int i = reader.getReader().getCursor();
            boolean bl = reader.readNegationCharacter();
            String string = reader.getReader().readString();
            if (reader.excludesName() && !bl) {
               reader.getReader().setCursor(i);
               throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext(reader.getReader(), "name");
            } else {
               if (bl) {
                  reader.setExcludesName(true);
               } else {
                  reader.setSelectsName(true);
               }

               reader.addPredicate((entity) -> {
                  return entity.getName().getString().equals(string) != bl;
               });
            }
         }, (reader) -> {
            return !reader.selectsName();
         }, Text.translatable("argument.entity.options.name.description"));
         putOption("distance", (reader) -> {
            int i = reader.getReader().getCursor();
            NumberRange.DoubleRange doubleRange = NumberRange.DoubleRange.parse(reader.getReader());
            if ((!doubleRange.min().isPresent() || !((Double)doubleRange.min().get() < 0.0)) && (!doubleRange.max().isPresent() || !((Double)doubleRange.max().get() < 0.0))) {
               reader.setDistance(doubleRange);
               reader.setLocalWorldOnly();
            } else {
               reader.getReader().setCursor(i);
               throw NEGATIVE_DISTANCE_EXCEPTION.createWithContext(reader.getReader());
            }
         }, (reader) -> {
            return reader.getDistance().isDummy();
         }, Text.translatable("argument.entity.options.distance.description"));
         putOption("level", (reader) -> {
            int i = reader.getReader().getCursor();
            NumberRange.IntRange intRange = NumberRange.IntRange.parse(reader.getReader());
            if ((!intRange.min().isPresent() || (Integer)intRange.min().get() >= 0) && (!intRange.max().isPresent() || (Integer)intRange.max().get() >= 0)) {
               reader.setLevelRange(intRange);
               reader.setIncludesNonPlayers(false);
            } else {
               reader.getReader().setCursor(i);
               throw NEGATIVE_LEVEL_EXCEPTION.createWithContext(reader.getReader());
            }
         }, (reader) -> {
            return reader.getLevelRange().isDummy();
         }, Text.translatable("argument.entity.options.level.description"));
         putOption("x", (reader) -> {
            reader.setLocalWorldOnly();
            reader.setX(reader.getReader().readDouble());
         }, (reader) -> {
            return reader.getX() == null;
         }, Text.translatable("argument.entity.options.x.description"));
         putOption("y", (reader) -> {
            reader.setLocalWorldOnly();
            reader.setY(reader.getReader().readDouble());
         }, (reader) -> {
            return reader.getY() == null;
         }, Text.translatable("argument.entity.options.y.description"));
         putOption("z", (reader) -> {
            reader.setLocalWorldOnly();
            reader.setZ(reader.getReader().readDouble());
         }, (reader) -> {
            return reader.getZ() == null;
         }, Text.translatable("argument.entity.options.z.description"));
         putOption("dx", (reader) -> {
            reader.setLocalWorldOnly();
            reader.setDx(reader.getReader().readDouble());
         }, (reader) -> {
            return reader.getDx() == null;
         }, Text.translatable("argument.entity.options.dx.description"));
         putOption("dy", (reader) -> {
            reader.setLocalWorldOnly();
            reader.setDy(reader.getReader().readDouble());
         }, (reader) -> {
            return reader.getDy() == null;
         }, Text.translatable("argument.entity.options.dy.description"));
         putOption("dz", (reader) -> {
            reader.setLocalWorldOnly();
            reader.setDz(reader.getReader().readDouble());
         }, (reader) -> {
            return reader.getDz() == null;
         }, Text.translatable("argument.entity.options.dz.description"));
         putOption("x_rotation", (reader) -> {
            reader.setPitchRange(FloatRangeArgument.parse(reader.getReader(), true, MathHelper::wrapDegrees));
         }, (reader) -> {
            return reader.getPitchRange() == FloatRangeArgument.ANY;
         }, Text.translatable("argument.entity.options.x_rotation.description"));
         putOption("y_rotation", (reader) -> {
            reader.setYawRange(FloatRangeArgument.parse(reader.getReader(), true, MathHelper::wrapDegrees));
         }, (reader) -> {
            return reader.getYawRange() == FloatRangeArgument.ANY;
         }, Text.translatable("argument.entity.options.y_rotation.description"));
         putOption("limit", (reader) -> {
            int i = reader.getReader().getCursor();
            int j = reader.getReader().readInt();
            if (j < 1) {
               reader.getReader().setCursor(i);
               throw TOO_SMALL_LEVEL_EXCEPTION.createWithContext(reader.getReader());
            } else {
               reader.setLimit(j);
               reader.setHasLimit(true);
            }
         }, (reader) -> {
            return !reader.isSenderOnly() && !reader.hasLimit();
         }, Text.translatable("argument.entity.options.limit.description"));
         putOption("sort", (reader) -> {
            int i = reader.getReader().getCursor();
            String string = reader.getReader().readUnquotedString();
            reader.setSuggestionProvider((builder, consumer) -> {
               return CommandSource.suggestMatching((Iterable)Arrays.asList("nearest", "furthest", "random", "arbitrary"), builder);
            });
            BiConsumer var10001;
            switch (string) {
               case "nearest":
                  var10001 = EntitySelectorReader.NEAREST;
                  break;
               case "furthest":
                  var10001 = EntitySelectorReader.FURTHEST;
                  break;
               case "random":
                  var10001 = EntitySelectorReader.RANDOM;
                  break;
               case "arbitrary":
                  var10001 = EntitySelector.ARBITRARY;
                  break;
               default:
                  reader.getReader().setCursor(i);
                  throw IRREVERSIBLE_SORT_EXCEPTION.createWithContext(reader.getReader(), string);
            }

            reader.setSorter(var10001);
            reader.setHasSorter(true);
         }, (reader) -> {
            return !reader.isSenderOnly() && !reader.hasSorter();
         }, Text.translatable("argument.entity.options.sort.description"));
         putOption("gamemode", (reader) -> {
            reader.setSuggestionProvider((builder, consumer) -> {
               String string = builder.getRemaining().toLowerCase(Locale.ROOT);
               boolean bl = !reader.excludesGameMode();
               boolean bl2 = true;
               if (!string.isEmpty()) {
                  if (string.charAt(0) == '!') {
                     bl = false;
                     string = string.substring(1);
                  } else {
                     bl2 = false;
                  }
               }

               GameMode[] var6 = GameMode.values();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  GameMode gameMode = var6[var8];
                  if (gameMode.getId().toLowerCase(Locale.ROOT).startsWith(string)) {
                     if (bl2) {
                        builder.suggest("!" + gameMode.getId());
                     }

                     if (bl) {
                        builder.suggest(gameMode.getId());
                     }
                  }
               }

               return builder.buildFuture();
            });
            int i = reader.getReader().getCursor();
            boolean bl = reader.readNegationCharacter();
            if (reader.excludesGameMode() && !bl) {
               reader.getReader().setCursor(i);
               throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext(reader.getReader(), "gamemode");
            } else {
               String string = reader.getReader().readUnquotedString();
               GameMode gameMode = GameMode.byId(string, (GameMode)null);
               if (gameMode == null) {
                  reader.getReader().setCursor(i);
                  throw INVALID_MODE_EXCEPTION.createWithContext(reader.getReader(), string);
               } else {
                  reader.setIncludesNonPlayers(false);
                  reader.addPredicate((entity) -> {
                     if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                        GameMode gameMode2 = serverPlayerEntity.getGameMode();
                        return gameMode2 == gameMode ^ bl;
                     } else {
                        return false;
                     }
                  });
                  if (bl) {
                     reader.setExcludesGameMode(true);
                  } else {
                     reader.setSelectsGameMode(true);
                  }

               }
            }
         }, (reader) -> {
            return !reader.selectsGameMode();
         }, Text.translatable("argument.entity.options.gamemode.description"));
         putOption("team", (reader) -> {
            boolean bl = reader.readNegationCharacter();
            String string = reader.getReader().readUnquotedString();
            reader.addPredicate((entity) -> {
               AbstractTeam abstractTeam = entity.getScoreboardTeam();
               String string2 = abstractTeam == null ? "" : abstractTeam.getName();
               return string2.equals(string) != bl;
            });
            if (bl) {
               reader.setExcludesTeam(true);
            } else {
               reader.setSelectsTeam(true);
            }

         }, (reader) -> {
            return !reader.selectsTeam();
         }, Text.translatable("argument.entity.options.team.description"));
         putOption("type", (reader) -> {
            reader.setSuggestionProvider((builder, consumer) -> {
               CommandSource.suggestIdentifiers((Iterable)Registries.ENTITY_TYPE.getIds(), (SuggestionsBuilder)builder, (String)String.valueOf('!'));
               CommandSource.suggestIdentifiers(Registries.ENTITY_TYPE.streamTags().map((tag) -> {
                  return tag.getTag().id();
               }), builder, "!#");
               if (!reader.excludesEntityType()) {
                  CommandSource.suggestIdentifiers((Iterable)Registries.ENTITY_TYPE.getIds(), builder);
                  CommandSource.suggestIdentifiers(Registries.ENTITY_TYPE.streamTags().map((tag) -> {
                     return tag.getTag().id();
                  }), builder, String.valueOf('#'));
               }

               return builder.buildFuture();
            });
            int i = reader.getReader().getCursor();
            boolean bl = reader.readNegationCharacter();
            if (reader.excludesEntityType() && !bl) {
               reader.getReader().setCursor(i);
               throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext(reader.getReader(), "type");
            } else {
               if (bl) {
                  reader.setExcludesEntityType();
               }

               if (reader.readTagCharacter()) {
                  TagKey tagKey = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.fromCommandInput(reader.getReader()));
                  reader.addPredicate((entity) -> {
                     return entity.getType().isIn(tagKey) != bl;
                  });
               } else {
                  Identifier identifier = Identifier.fromCommandInput(reader.getReader());
                  EntityType entityType = (EntityType)Registries.ENTITY_TYPE.getOptionalValue(identifier).orElseThrow(() -> {
                     reader.getReader().setCursor(i);
                     return INVALID_TYPE_EXCEPTION.createWithContext(reader.getReader(), identifier.toString());
                  });
                  if (Objects.equals(EntityType.PLAYER, entityType) && !bl) {
                     reader.setIncludesNonPlayers(false);
                  }

                  reader.addPredicate((entity) -> {
                     return Objects.equals(entityType, entity.getType()) != bl;
                  });
                  if (!bl) {
                     reader.setEntityType(entityType);
                  }
               }

            }
         }, (reader) -> {
            return !reader.selectsEntityType();
         }, Text.translatable("argument.entity.options.type.description"));
         putOption("tag", (reader) -> {
            boolean bl = reader.readNegationCharacter();
            String string = reader.getReader().readUnquotedString();
            reader.addPredicate((entity) -> {
               if ("".equals(string)) {
                  return entity.getCommandTags().isEmpty() != bl;
               } else {
                  return entity.getCommandTags().contains(string) != bl;
               }
            });
         }, (reader) -> {
            return true;
         }, Text.translatable("argument.entity.options.tag.description"));
         putOption("nbt", (reader) -> {
            boolean bl = reader.readNegationCharacter();
            NbtCompound nbtCompound = StringNbtReader.readCompoundAsArgument(reader.getReader());
            reader.addPredicate((entity) -> {
               ErrorReporter.Logging logging = new ErrorReporter.Logging(entity.getErrorReporterContext(), LOGGER);

               boolean var9;
               try {
                  NbtWriteView nbtWriteView = NbtWriteView.create(logging, entity.getRegistryManager());
                  entity.writeData(nbtWriteView);
                  if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                     ItemStack itemStack = serverPlayerEntity.getInventory().getSelectedStack();
                     if (!itemStack.isEmpty()) {
                        nbtWriteView.put("SelectedItem", ItemStack.CODEC, itemStack);
                     }
                  }

                  var9 = NbtHelper.matches(nbtCompound, nbtWriteView.getNbt(), true) != bl;
               } catch (Throwable var8) {
                  try {
                     logging.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }

                  throw var8;
               }

               logging.close();
               return var9;
            });
         }, (reader) -> {
            return true;
         }, Text.translatable("argument.entity.options.nbt.description"));
         putOption("scores", (reader) -> {
            StringReader stringReader = reader.getReader();
            Map map = Maps.newHashMap();
            stringReader.expect('{');
            stringReader.skipWhitespace();

            while(stringReader.canRead() && stringReader.peek() != '}') {
               stringReader.skipWhitespace();
               String string = stringReader.readUnquotedString();
               stringReader.skipWhitespace();
               stringReader.expect('=');
               stringReader.skipWhitespace();
               NumberRange.IntRange intRange = NumberRange.IntRange.parse(stringReader);
               map.put(string, intRange);
               stringReader.skipWhitespace();
               if (stringReader.canRead() && stringReader.peek() == ',') {
                  stringReader.skip();
               }
            }

            stringReader.expect('}');
            if (!map.isEmpty()) {
               reader.addPredicate((entity) -> {
                  Scoreboard scoreboard = entity.getServer().getScoreboard();
                  Iterator var3 = map.entrySet().iterator();

                  Map.Entry entry;
                  ReadableScoreboardScore readableScoreboardScore;
                  do {
                     if (!var3.hasNext()) {
                        return true;
                     }

                     entry = (Map.Entry)var3.next();
                     ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective((String)entry.getKey());
                     if (scoreboardObjective == null) {
                        return false;
                     }

                     readableScoreboardScore = scoreboard.getScore(entity, scoreboardObjective);
                     if (readableScoreboardScore == null) {
                        return false;
                     }
                  } while(((NumberRange.IntRange)entry.getValue()).test(readableScoreboardScore.getScore()));

                  return false;
               });
            }

            reader.setSelectsScores(true);
         }, (reader) -> {
            return !reader.selectsScores();
         }, Text.translatable("argument.entity.options.scores.description"));
         putOption("advancements", (reader) -> {
            StringReader stringReader = reader.getReader();
            Map map = Maps.newHashMap();
            stringReader.expect('{');
            stringReader.skipWhitespace();

            while(stringReader.canRead() && stringReader.peek() != '}') {
               stringReader.skipWhitespace();
               Identifier identifier = Identifier.fromCommandInput(stringReader);
               stringReader.skipWhitespace();
               stringReader.expect('=');
               stringReader.skipWhitespace();
               if (stringReader.canRead() && stringReader.peek() == '{') {
                  Map map2 = Maps.newHashMap();
                  stringReader.skipWhitespace();
                  stringReader.expect('{');
                  stringReader.skipWhitespace();

                  while(stringReader.canRead() && stringReader.peek() != '}') {
                     stringReader.skipWhitespace();
                     String string = stringReader.readUnquotedString();
                     stringReader.skipWhitespace();
                     stringReader.expect('=');
                     stringReader.skipWhitespace();
                     boolean bl = stringReader.readBoolean();
                     map2.put(string, (criterionProgress) -> {
                        return criterionProgress.isObtained() == bl;
                     });
                     stringReader.skipWhitespace();
                     if (stringReader.canRead() && stringReader.peek() == ',') {
                        stringReader.skip();
                     }
                  }

                  stringReader.skipWhitespace();
                  stringReader.expect('}');
                  stringReader.skipWhitespace();
                  map.put(identifier, (advancementProgress) -> {
                     Iterator var2 = map2.entrySet().iterator();

                     Map.Entry entry;
                     CriterionProgress criterionProgress;
                     do {
                        if (!var2.hasNext()) {
                           return true;
                        }

                        entry = (Map.Entry)var2.next();
                        criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
                     } while(criterionProgress != null && ((Predicate)entry.getValue()).test(criterionProgress));

                     return false;
                  });
               } else {
                  boolean bl2 = stringReader.readBoolean();
                  map.put(identifier, (advancementProgress) -> {
                     return advancementProgress.isDone() == bl2;
                  });
               }

               stringReader.skipWhitespace();
               if (stringReader.canRead() && stringReader.peek() == ',') {
                  stringReader.skip();
               }
            }

            stringReader.expect('}');
            if (!map.isEmpty()) {
               reader.addPredicate((entity) -> {
                  if (!(entity instanceof ServerPlayerEntity serverPlayerEntity)) {
                     return false;
                  } else {
                     PlayerAdvancementTracker playerAdvancementTracker = serverPlayerEntity.getAdvancementTracker();
                     ServerAdvancementLoader serverAdvancementLoader = serverPlayerEntity.getServer().getAdvancementLoader();
                     Iterator var5 = map.entrySet().iterator();

                     Map.Entry entry;
                     AdvancementEntry advancementEntry;
                     do {
                        if (!var5.hasNext()) {
                           return true;
                        }

                        entry = (Map.Entry)var5.next();
                        advancementEntry = serverAdvancementLoader.get((Identifier)entry.getKey());
                     } while(advancementEntry != null && ((Predicate)entry.getValue()).test(playerAdvancementTracker.getProgress(advancementEntry)));

                     return false;
                  }
               });
               reader.setIncludesNonPlayers(false);
            }

            reader.setSelectsAdvancements(true);
         }, (reader) -> {
            return !reader.selectsAdvancements();
         }, Text.translatable("argument.entity.options.advancements.description"));
         putOption("predicate", (reader) -> {
            boolean bl = reader.readNegationCharacter();
            RegistryKey registryKey = RegistryKey.of(RegistryKeys.PREDICATE, Identifier.fromCommandInput(reader.getReader()));
            reader.addPredicate((entity) -> {
               if (!(entity.getWorld() instanceof ServerWorld)) {
                  return false;
               } else {
                  ServerWorld serverWorld = (ServerWorld)entity.getWorld();
                  Optional optional = serverWorld.getServer().getReloadableRegistries().createRegistryLookup().getOptionalEntry(registryKey).map(RegistryEntry::value);
                  if (optional.isEmpty()) {
                     return false;
                  } else {
                     LootWorldContext lootWorldContext = (new LootWorldContext.Builder(serverWorld)).add(LootContextParameters.THIS_ENTITY, entity).add(LootContextParameters.ORIGIN, entity.getPos()).build(LootContextTypes.SELECTOR);
                     LootContext lootContext = (new LootContext.Builder(lootWorldContext)).build(Optional.empty());
                     lootContext.markActive(LootContext.predicate((LootCondition)optional.get()));
                     return bl ^ ((LootCondition)optional.get()).test(lootContext);
                  }
               }
            });
         }, (reader) -> {
            return true;
         }, Text.translatable("argument.entity.options.predicate.description"));
      }
   }

   public static SelectorHandler getHandler(EntitySelectorReader reader, String option, int restoreCursor) throws CommandSyntaxException {
      SelectorOption selectorOption = (SelectorOption)OPTIONS.get(option);
      if (selectorOption != null) {
         if (selectorOption.condition.test(reader)) {
            return selectorOption.handler;
         } else {
            throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext(reader.getReader(), option);
         }
      } else {
         reader.getReader().setCursor(restoreCursor);
         throw UNKNOWN_OPTION_EXCEPTION.createWithContext(reader.getReader(), option);
      }
   }

   public static void suggestOptions(EntitySelectorReader reader, SuggestionsBuilder suggestionBuilder) {
      String string = suggestionBuilder.getRemaining().toLowerCase(Locale.ROOT);
      Iterator var3 = OPTIONS.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry entry = (Map.Entry)var3.next();
         if (((SelectorOption)entry.getValue()).condition.test(reader) && ((String)entry.getKey()).toLowerCase(Locale.ROOT).startsWith(string)) {
            suggestionBuilder.suggest((String)entry.getKey() + "=", ((SelectorOption)entry.getValue()).description);
         }
      }

   }

   static record SelectorOption(SelectorHandler handler, Predicate condition, Text description) {
      final SelectorHandler handler;
      final Predicate condition;
      final Text description;

      SelectorOption(SelectorHandler handler, Predicate condition, Text description) {
         this.handler = handler;
         this.condition = condition;
         this.description = description;
      }

      public SelectorHandler handler() {
         return this.handler;
      }

      public Predicate condition() {
         return this.condition;
      }

      public Text description() {
         return this.description;
      }
   }

   public interface SelectorHandler {
      void handle(EntitySelectorReader reader) throws CommandSyntaxException;
   }
}
