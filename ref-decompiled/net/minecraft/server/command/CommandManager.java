package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.command.CommandExecutionContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.PermissionLevelPredicate;
import net.minecraft.command.PermissionLevelSource;
import net.minecraft.command.ReturnValueConsumer;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.command.BanCommand;
import net.minecraft.server.dedicated.command.BanIpCommand;
import net.minecraft.server.dedicated.command.BanListCommand;
import net.minecraft.server.dedicated.command.DeOpCommand;
import net.minecraft.server.dedicated.command.OpCommand;
import net.minecraft.server.dedicated.command.PardonCommand;
import net.minecraft.server.dedicated.command.PardonIpCommand;
import net.minecraft.server.dedicated.command.PerfCommand;
import net.minecraft.server.dedicated.command.SaveAllCommand;
import net.minecraft.server.dedicated.command.SaveOffCommand;
import net.minecraft.server.dedicated.command.SaveOnCommand;
import net.minecraft.server.dedicated.command.SetIdleTimeoutCommand;
import net.minecraft.server.dedicated.command.StopCommand;
import net.minecraft.server.dedicated.command.TransferCommand;
import net.minecraft.server.dedicated.command.WhitelistCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class CommandManager {
   public static final String field_60859 = "/";
   private static final ThreadLocal CURRENT_CONTEXT = new ThreadLocal();
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int field_31837 = 0;
   public static final int field_31838 = 1;
   public static final int field_31839 = 2;
   public static final int field_31840 = 3;
   public static final int field_31841 = 4;
   private static final CommandTreeS2CPacket.CommandNodeInspector INSPECTOR = new CommandTreeS2CPacket.CommandNodeInspector() {
      @Nullable
      public Identifier getSuggestionProviderId(ArgumentCommandNode node) {
         SuggestionProvider suggestionProvider = node.getCustomSuggestions();
         return suggestionProvider != null ? SuggestionProviders.computeId(suggestionProvider) : null;
      }

      public boolean isExecutable(CommandNode node) {
         return node.getCommand() != null;
      }

      public boolean hasRequiredLevel(CommandNode node) {
         Predicate var3 = node.getRequirement();
         boolean var10000;
         if (var3 instanceof PermissionLevelPredicate permissionLevelPredicate) {
            if (permissionLevelPredicate.requiredLevel() > 0) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   };
   private final CommandDispatcher dispatcher = new CommandDispatcher();

   public CommandManager(RegistrationEnvironment environment, CommandRegistryAccess commandRegistryAccess) {
      AdvancementCommand.register(this.dispatcher);
      AttributeCommand.register(this.dispatcher, commandRegistryAccess);
      ExecuteCommand.register(this.dispatcher, commandRegistryAccess);
      BossBarCommand.register(this.dispatcher, commandRegistryAccess);
      ClearCommand.register(this.dispatcher, commandRegistryAccess);
      CloneCommand.register(this.dispatcher, commandRegistryAccess);
      DamageCommand.register(this.dispatcher, commandRegistryAccess);
      DataCommand.register(this.dispatcher);
      DatapackCommand.register(this.dispatcher, commandRegistryAccess);
      DebugCommand.register(this.dispatcher);
      DefaultGameModeCommand.register(this.dispatcher);
      DialogCommand.register(this.dispatcher, commandRegistryAccess);
      DifficultyCommand.register(this.dispatcher);
      EffectCommand.register(this.dispatcher, commandRegistryAccess);
      MeCommand.register(this.dispatcher);
      EnchantCommand.register(this.dispatcher, commandRegistryAccess);
      ExperienceCommand.register(this.dispatcher);
      FillCommand.register(this.dispatcher, commandRegistryAccess);
      FillBiomeCommand.register(this.dispatcher, commandRegistryAccess);
      ForceLoadCommand.register(this.dispatcher);
      FunctionCommand.register(this.dispatcher);
      GameModeCommand.register(this.dispatcher);
      GameRuleCommand.register(this.dispatcher, commandRegistryAccess);
      GiveCommand.register(this.dispatcher, commandRegistryAccess);
      HelpCommand.register(this.dispatcher);
      ItemCommand.register(this.dispatcher, commandRegistryAccess);
      KickCommand.register(this.dispatcher);
      KillCommand.register(this.dispatcher);
      ListCommand.register(this.dispatcher);
      LocateCommand.register(this.dispatcher, commandRegistryAccess);
      LootCommand.register(this.dispatcher, commandRegistryAccess);
      MessageCommand.register(this.dispatcher);
      ParticleCommand.register(this.dispatcher, commandRegistryAccess);
      PlaceCommand.register(this.dispatcher);
      PlaySoundCommand.register(this.dispatcher);
      RandomCommand.register(this.dispatcher);
      ReloadCommand.register(this.dispatcher);
      RecipeCommand.register(this.dispatcher);
      ReturnCommand.register(this.dispatcher);
      RideCommand.register(this.dispatcher);
      RotateCommand.register(this.dispatcher);
      SayCommand.register(this.dispatcher);
      ScheduleCommand.register(this.dispatcher);
      ScoreboardCommand.register(this.dispatcher, commandRegistryAccess);
      SeedCommand.register(this.dispatcher, environment != CommandManager.RegistrationEnvironment.INTEGRATED);
      VersionCommand.register(this.dispatcher, environment != CommandManager.RegistrationEnvironment.INTEGRATED);
      SetBlockCommand.register(this.dispatcher, commandRegistryAccess);
      SpawnPointCommand.register(this.dispatcher);
      SetWorldSpawnCommand.register(this.dispatcher);
      SpectateCommand.register(this.dispatcher);
      SpreadPlayersCommand.register(this.dispatcher);
      StopSoundCommand.register(this.dispatcher);
      SummonCommand.register(this.dispatcher, commandRegistryAccess);
      TagCommand.register(this.dispatcher);
      TeamCommand.register(this.dispatcher, commandRegistryAccess);
      TeamMsgCommand.register(this.dispatcher);
      TeleportCommand.register(this.dispatcher);
      TellRawCommand.register(this.dispatcher, commandRegistryAccess);
      TestCommand.register(this.dispatcher, commandRegistryAccess);
      TickCommand.register(this.dispatcher);
      TimeCommand.register(this.dispatcher);
      TitleCommand.register(this.dispatcher, commandRegistryAccess);
      TriggerCommand.register(this.dispatcher);
      WaypointCommand.register(this.dispatcher, commandRegistryAccess);
      WeatherCommand.register(this.dispatcher);
      WorldBorderCommand.register(this.dispatcher);
      if (FlightProfiler.INSTANCE.isAvailable()) {
         JfrCommand.register(this.dispatcher);
      }

      if (SharedConstants.isDevelopment) {
         RaidCommand.register(this.dispatcher, commandRegistryAccess);
         DebugPathCommand.register(this.dispatcher);
         DebugMobSpawningCommand.register(this.dispatcher);
         WardenSpawnTrackerCommand.register(this.dispatcher);
         SpawnArmorTrimsCommand.register(this.dispatcher);
         ServerPackCommand.register(this.dispatcher);
         if (environment.dedicated) {
            DebugConfigCommand.register(this.dispatcher, commandRegistryAccess);
         }
      }

      if (environment.dedicated) {
         BanIpCommand.register(this.dispatcher);
         BanListCommand.register(this.dispatcher);
         BanCommand.register(this.dispatcher);
         DeOpCommand.register(this.dispatcher);
         OpCommand.register(this.dispatcher);
         PardonCommand.register(this.dispatcher);
         PardonIpCommand.register(this.dispatcher);
         PerfCommand.register(this.dispatcher);
         SaveAllCommand.register(this.dispatcher);
         SaveOffCommand.register(this.dispatcher);
         SaveOnCommand.register(this.dispatcher);
         SetIdleTimeoutCommand.register(this.dispatcher);
         StopCommand.register(this.dispatcher);
         TransferCommand.register(this.dispatcher);
         WhitelistCommand.register(this.dispatcher);
      }

      if (environment.integrated) {
         PublishCommand.register(this.dispatcher);
      }

      this.dispatcher.setConsumer(AbstractServerCommandSource.asResultConsumer());
   }

   public static ParseResults withCommandSource(ParseResults parseResults, UnaryOperator sourceMapper) {
      CommandContextBuilder commandContextBuilder = parseResults.getContext();
      CommandContextBuilder commandContextBuilder2 = commandContextBuilder.withSource(sourceMapper.apply(commandContextBuilder.getSource()));
      return new ParseResults(commandContextBuilder2, parseResults.getReader(), parseResults.getExceptions());
   }

   public void executeWithPrefix(ServerCommandSource source, String command) {
      command = stripLeadingSlash(command);
      this.execute(this.dispatcher.parse(command, source), command);
   }

   public static String stripLeadingSlash(String command) {
      return command.startsWith("/") ? command.substring(1) : command;
   }

   public void execute(ParseResults parseResults, String command) {
      ServerCommandSource serverCommandSource = (ServerCommandSource)parseResults.getContext().getSource();
      Profilers.get().push(() -> {
         return "/" + command;
      });
      ContextChain contextChain = checkCommand(parseResults, command, serverCommandSource);

      try {
         if (contextChain != null) {
            callWithContext(serverCommandSource, (context) -> {
               CommandExecutionContext.enqueueCommand(context, command, contextChain, serverCommandSource, ReturnValueConsumer.EMPTY);
            });
         }
      } catch (Exception var12) {
         MutableText mutableText = Text.literal(var12.getMessage() == null ? var12.getClass().getName() : var12.getMessage());
         if (LOGGER.isDebugEnabled()) {
            LOGGER.error("Command exception: /{}", command, var12);
            StackTraceElement[] stackTraceElements = var12.getStackTrace();

            for(int i = 0; i < Math.min(stackTraceElements.length, 3); ++i) {
               mutableText.append("\n\n").append(stackTraceElements[i].getMethodName()).append("\n ").append(stackTraceElements[i].getFileName()).append(":").append(String.valueOf(stackTraceElements[i].getLineNumber()));
            }
         }

         serverCommandSource.sendError(Text.translatable("command.failed").styled((style) -> {
            return style.withHoverEvent(new HoverEvent.ShowText(mutableText));
         }));
         if (SharedConstants.isDevelopment) {
            serverCommandSource.sendError(Text.literal(Util.getInnermostMessage(var12)));
            LOGGER.error("'/{}' threw an exception", command, var12);
         }
      } finally {
         Profilers.get().pop();
      }

   }

   @Nullable
   private static ContextChain checkCommand(ParseResults parseResults, String command, ServerCommandSource source) {
      try {
         throwException(parseResults);
         return (ContextChain)ContextChain.tryFlatten(parseResults.getContext().build(command)).orElseThrow(() -> {
            return CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parseResults.getReader());
         });
      } catch (CommandSyntaxException var7) {
         source.sendError(Texts.toText(var7.getRawMessage()));
         if (var7.getInput() != null && var7.getCursor() >= 0) {
            int i = Math.min(var7.getInput().length(), var7.getCursor());
            MutableText mutableText = Text.empty().formatted(Formatting.GRAY).styled((style) -> {
               return style.withClickEvent(new ClickEvent.SuggestCommand("/" + command));
            });
            if (i > 10) {
               mutableText.append(ScreenTexts.ELLIPSIS);
            }

            mutableText.append(var7.getInput().substring(Math.max(0, i - 10), i));
            if (i < var7.getInput().length()) {
               Text text = Text.literal(var7.getInput().substring(i)).formatted(Formatting.RED, Formatting.UNDERLINE);
               mutableText.append((Text)text);
            }

            mutableText.append((Text)Text.translatable("command.context.here").formatted(Formatting.RED, Formatting.ITALIC));
            source.sendError(mutableText);
         }

         return null;
      }
   }

   public static void callWithContext(ServerCommandSource commandSource, Consumer callback) {
      MinecraftServer minecraftServer = commandSource.getServer();
      CommandExecutionContext commandExecutionContext = (CommandExecutionContext)CURRENT_CONTEXT.get();
      boolean bl = commandExecutionContext == null;
      if (bl) {
         int i = Math.max(1, minecraftServer.getGameRules().getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH));
         int j = minecraftServer.getGameRules().getInt(GameRules.MAX_COMMAND_FORK_COUNT);

         try {
            CommandExecutionContext commandExecutionContext2 = new CommandExecutionContext(i, j, Profilers.get());

            try {
               CURRENT_CONTEXT.set(commandExecutionContext2);
               callback.accept(commandExecutionContext2);
               commandExecutionContext2.run();
            } catch (Throwable var15) {
               try {
                  commandExecutionContext2.close();
               } catch (Throwable var14) {
                  var15.addSuppressed(var14);
               }

               throw var15;
            }

            commandExecutionContext2.close();
         } finally {
            CURRENT_CONTEXT.set((Object)null);
         }
      } else {
         callback.accept(commandExecutionContext);
      }

   }

   public void sendCommandTree(ServerPlayerEntity player) {
      Map map = new HashMap();
      RootCommandNode rootCommandNode = new RootCommandNode();
      map.put(this.dispatcher.getRoot(), rootCommandNode);
      deepCopyNodes(this.dispatcher.getRoot(), rootCommandNode, player.getCommandSource(), map);
      player.networkHandler.sendPacket(new CommandTreeS2CPacket(rootCommandNode, INSPECTOR));
   }

   private static void deepCopyNodes(CommandNode root, CommandNode newRoot, Object source, Map nodes) {
      Iterator var4 = root.getChildren().iterator();

      while(var4.hasNext()) {
         CommandNode commandNode = (CommandNode)var4.next();
         if (commandNode.canUse(source)) {
            ArgumentBuilder argumentBuilder = commandNode.createBuilder();
            if (argumentBuilder.getRedirect() != null) {
               argumentBuilder.redirect((CommandNode)nodes.get(argumentBuilder.getRedirect()));
            }

            CommandNode commandNode2 = argumentBuilder.build();
            nodes.put(commandNode, commandNode2);
            newRoot.addChild(commandNode2);
            if (!commandNode.getChildren().isEmpty()) {
               deepCopyNodes(commandNode, commandNode2, source, nodes);
            }
         }
      }

   }

   public static LiteralArgumentBuilder literal(String literal) {
      return LiteralArgumentBuilder.literal(literal);
   }

   public static RequiredArgumentBuilder argument(String name, ArgumentType type) {
      return RequiredArgumentBuilder.argument(name, type);
   }

   public static Predicate getCommandValidator(CommandParser parser) {
      return (string) -> {
         try {
            parser.parse(new StringReader(string));
            return true;
         } catch (CommandSyntaxException var3) {
            return false;
         }
      };
   }

   public CommandDispatcher getDispatcher() {
      return this.dispatcher;
   }

   public static void throwException(ParseResults parse) throws CommandSyntaxException {
      CommandSyntaxException commandSyntaxException = getException(parse);
      if (commandSyntaxException != null) {
         throw commandSyntaxException;
      }
   }

   @Nullable
   public static CommandSyntaxException getException(ParseResults parse) {
      if (!parse.getReader().canRead()) {
         return null;
      } else if (parse.getExceptions().size() == 1) {
         return (CommandSyntaxException)parse.getExceptions().values().iterator().next();
      } else {
         return parse.getContext().getRange().isEmpty() ? CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parse.getReader()) : CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parse.getReader());
      }
   }

   public static CommandRegistryAccess createRegistryAccess(final RegistryWrapper.WrapperLookup registries) {
      return new CommandRegistryAccess() {
         public FeatureSet getEnabledFeatures() {
            return FeatureFlags.FEATURE_MANAGER.getFeatureSet();
         }

         public Stream streamAllRegistryKeys() {
            return registries.streamAllRegistryKeys();
         }

         public Optional getOptional(RegistryKey registryRef) {
            return registries.getOptional(registryRef).map(this::createTagCreatingLookup);
         }

         private RegistryWrapper.Impl.Delegating createTagCreatingLookup(final RegistryWrapper.Impl original) {
            return new RegistryWrapper.Impl.Delegating(this) {
               public RegistryWrapper.Impl getBase() {
                  return original;
               }

               public Optional getOptional(TagKey tag) {
                  return Optional.of(this.getOrThrow(tag));
               }

               public RegistryEntryList.Named getOrThrow(TagKey tag) {
                  Optional optional = this.getBase().getOptional(tag);
                  return (RegistryEntryList.Named)optional.orElseGet(() -> {
                     return RegistryEntryList.of((RegistryEntryOwner)this.getBase(), (TagKey)tag);
                  });
               }
            };
         }
      };
   }

   public static void checkMissing() {
      CommandRegistryAccess commandRegistryAccess = createRegistryAccess(BuiltinRegistries.createWrapperLookup());
      CommandDispatcher commandDispatcher = (new CommandManager(CommandManager.RegistrationEnvironment.ALL, commandRegistryAccess)).getDispatcher();
      RootCommandNode rootCommandNode = commandDispatcher.getRoot();
      commandDispatcher.findAmbiguities((parent, child, sibling, inputs) -> {
         LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", new Object[]{commandDispatcher.getPath(child), commandDispatcher.getPath(sibling), inputs});
      });
      Set set = ArgumentHelper.collectUsedArgumentTypes(rootCommandNode);
      Set set2 = (Set)set.stream().filter((type) -> {
         return !ArgumentTypes.has(type.getClass());
      }).collect(Collectors.toSet());
      if (!set2.isEmpty()) {
         LOGGER.warn("Missing type registration for following arguments:\n {}", set2.stream().map((type) -> {
            return "\t" + String.valueOf(type);
         }).collect(Collectors.joining(",\n")));
         throw new IllegalStateException("Unregistered argument types");
      }
   }

   public static PermissionLevelPredicate requirePermissionLevel(int requiredLevel) {
      return new PermissionLevelSource.PermissionLevelSourcePredicate(requiredLevel);
   }

   public static enum RegistrationEnvironment {
      ALL(true, true),
      DEDICATED(false, true),
      INTEGRATED(true, false);

      public final boolean integrated;
      public final boolean dedicated;

      private RegistrationEnvironment(final boolean integrated, final boolean dedicated) {
         this.integrated = integrated;
         this.dedicated = dedicated;
      }

      // $FF: synthetic method
      private static RegistrationEnvironment[] method_36791() {
         return new RegistrationEnvironment[]{ALL, DEDICATED, INTEGRATED};
      }
   }

   @FunctionalInterface
   public interface CommandParser {
      void parse(StringReader reader) throws CommandSyntaxException;
   }
}
