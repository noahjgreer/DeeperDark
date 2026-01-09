package net.minecraft.server.command;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import net.minecraft.command.CommandFunctionAction;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ControlFlowAware;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.ExecutionFlags;
import net.minecraft.command.FallthroughCommandAction;
import net.minecraft.command.ReturnValueConsumer;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.function.MacroException;
import net.minecraft.server.function.Procedure;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class FunctionCommand {
   private static final DynamicCommandExceptionType ARGUMENT_NOT_COMPOUND_EXCEPTION = new DynamicCommandExceptionType((argument) -> {
      return Text.stringifiedTranslatable("commands.function.error.argument_not_compound", argument);
   });
   static final DynamicCommandExceptionType NO_FUNCTIONS_EXCEPTION = new DynamicCommandExceptionType((argument) -> {
      return Text.stringifiedTranslatable("commands.function.scheduled.no_functions", argument);
   });
   @VisibleForTesting
   public static final Dynamic2CommandExceptionType INSTANTIATION_FAILURE_EXCEPTION = new Dynamic2CommandExceptionType((argument, argument2) -> {
      return Text.stringifiedTranslatable("commands.function.instantiationFailure", argument, argument2);
   });
   public static final SuggestionProvider SUGGESTION_PROVIDER = (context, builder) -> {
      CommandFunctionManager commandFunctionManager = ((ServerCommandSource)context.getSource()).getServer().getCommandFunctionManager();
      CommandSource.suggestIdentifiers(commandFunctionManager.getFunctionTags(), builder, "#");
      return CommandSource.suggestIdentifiers(commandFunctionManager.getAllFunctions(), builder);
   };
   static final ResultConsumer RESULT_REPORTER = new ResultConsumer() {
      public void accept(ServerCommandSource serverCommandSource, Identifier identifier, int i) {
         serverCommandSource.sendFeedback(() -> {
            return Text.translatable("commands.function.result", Text.of(identifier), i);
         }, true);
      }
   };

   public static void register(CommandDispatcher dispatcher) {
      LiteralArgumentBuilder literalArgumentBuilder = CommandManager.literal("with");
      Iterator var2 = DataCommand.SOURCE_OBJECT_TYPES.iterator();

      while(var2.hasNext()) {
         DataCommand.ObjectType objectType = (DataCommand.ObjectType)var2.next();
         objectType.addArgumentsToBuilder(literalArgumentBuilder, (builder) -> {
            return builder.executes(new Command() {
               protected NbtCompound getArguments(CommandContext context) throws CommandSyntaxException {
                  return objectType.getObject(context).getNbt();
               }
            }).then(CommandManager.argument("path", NbtPathArgumentType.nbtPath()).executes(new Command() {
               protected NbtCompound getArguments(CommandContext context) throws CommandSyntaxException {
                  return FunctionCommand.getArgument(NbtPathArgumentType.getNbtPath(context, "path"), objectType.getObject(context));
               }
            }));
         });
      }

      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("function").requires(CommandManager.requirePermissionLevel(2))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("name", CommandFunctionArgumentType.commandFunction()).suggests(SUGGESTION_PROVIDER).executes(new Command() {
         @Nullable
         protected NbtCompound getArguments(CommandContext context) {
            return null;
         }
      })).then(CommandManager.argument("arguments", NbtCompoundArgumentType.nbtCompound()).executes(new Command() {
         protected NbtCompound getArguments(CommandContext context) {
            return NbtCompoundArgumentType.getNbtCompound(context, "arguments");
         }
      }))).then(literalArgumentBuilder)));
   }

   static NbtCompound getArgument(NbtPathArgumentType.NbtPath path, DataCommandObject object) throws CommandSyntaxException {
      NbtElement nbtElement = DataCommand.getNbt(path, object);
      if (nbtElement instanceof NbtCompound nbtCompound) {
         return nbtCompound;
      } else {
         throw ARGUMENT_NOT_COMPOUND_EXCEPTION.create(nbtElement.getNbtType().getCrashReportName());
      }
   }

   public static ServerCommandSource createFunctionCommandSource(ServerCommandSource source) {
      return source.withSilent().withMaxLevel(2);
   }

   public static void enqueueAction(Collection commandFunctions, @Nullable NbtCompound args, AbstractServerCommandSource parentSource, AbstractServerCommandSource functionSource, ExecutionControl control, ResultConsumer resultConsumer, ExecutionFlags flags) throws CommandSyntaxException {
      if (flags.isInsideReturnRun()) {
         enqueueInReturnRun(commandFunctions, args, parentSource, functionSource, control, resultConsumer);
      } else {
         enqueueOutsideReturnRun(commandFunctions, args, parentSource, functionSource, control, resultConsumer);
      }

   }

   private static void enqueueFunction(@Nullable NbtCompound args, ExecutionControl control, CommandDispatcher dispatcher, AbstractServerCommandSource source, CommandFunction function, Identifier id, ReturnValueConsumer returnValueConsumer, boolean propagateReturn) throws CommandSyntaxException {
      try {
         Procedure procedure = function.withMacroReplaced(args, dispatcher);
         control.enqueueAction((new CommandFunctionAction(procedure, returnValueConsumer, propagateReturn)).bind(source));
      } catch (MacroException var9) {
         throw INSTANTIATION_FAILURE_EXCEPTION.create(id, var9.getMessage());
      }
   }

   private static ReturnValueConsumer wrapReturnValueConsumer(AbstractServerCommandSource flags, ResultConsumer resultConsumer, Identifier id, ReturnValueConsumer wrapped) {
      return flags.isSilent() ? wrapped : (successful, returnValue) -> {
         resultConsumer.accept(flags, id, returnValue);
         wrapped.onResult(successful, returnValue);
      };
   }

   private static void enqueueInReturnRun(Collection functions, @Nullable NbtCompound args, AbstractServerCommandSource parentSource, AbstractServerCommandSource functionSource, ExecutionControl control, ResultConsumer resultConsumer) throws CommandSyntaxException {
      CommandDispatcher commandDispatcher = parentSource.getDispatcher();
      AbstractServerCommandSource abstractServerCommandSource = functionSource.withDummyReturnValueConsumer();
      ReturnValueConsumer returnValueConsumer = ReturnValueConsumer.chain(parentSource.getReturnValueConsumer(), control.getFrame().returnValueConsumer());
      Iterator var9 = functions.iterator();

      while(var9.hasNext()) {
         CommandFunction commandFunction = (CommandFunction)var9.next();
         Identifier identifier = commandFunction.id();
         ReturnValueConsumer returnValueConsumer2 = wrapReturnValueConsumer(parentSource, resultConsumer, identifier, returnValueConsumer);
         enqueueFunction(args, control, commandDispatcher, abstractServerCommandSource, commandFunction, identifier, returnValueConsumer2, true);
      }

      control.enqueueAction(FallthroughCommandAction.getInstance());
   }

   private static void enqueueOutsideReturnRun(Collection functions, @Nullable NbtCompound args, AbstractServerCommandSource parentSource, AbstractServerCommandSource functionSource, ExecutionControl control, ResultConsumer resultConsumer) throws CommandSyntaxException {
      CommandDispatcher commandDispatcher = parentSource.getDispatcher();
      AbstractServerCommandSource abstractServerCommandSource = functionSource.withDummyReturnValueConsumer();
      ReturnValueConsumer returnValueConsumer = parentSource.getReturnValueConsumer();
      if (!functions.isEmpty()) {
         if (functions.size() == 1) {
            CommandFunction commandFunction = (CommandFunction)functions.iterator().next();
            Identifier identifier = commandFunction.id();
            ReturnValueConsumer returnValueConsumer2 = wrapReturnValueConsumer(parentSource, resultConsumer, identifier, returnValueConsumer);
            enqueueFunction(args, control, commandDispatcher, abstractServerCommandSource, commandFunction, identifier, returnValueConsumer2, false);
         } else if (returnValueConsumer == ReturnValueConsumer.EMPTY) {
            Iterator var15 = functions.iterator();

            while(var15.hasNext()) {
               CommandFunction commandFunction2 = (CommandFunction)var15.next();
               Identifier identifier2 = commandFunction2.id();
               ReturnValueConsumer returnValueConsumer3 = wrapReturnValueConsumer(parentSource, resultConsumer, identifier2, returnValueConsumer);
               enqueueFunction(args, control, commandDispatcher, abstractServerCommandSource, commandFunction2, identifier2, returnValueConsumer3, false);
            }
         } else {
            class ReturnValueAdder {
               boolean successful;
               int returnValue;

               public void onSuccess(int returnValue) {
                  this.successful = true;
                  this.returnValue += returnValue;
               }
            }

            ReturnValueAdder returnValueAdder = new ReturnValueAdder();
            ReturnValueConsumer returnValueConsumer4 = (successful, returnValue) -> {
               returnValueAdder.onSuccess(returnValue);
            };
            Iterator var20 = functions.iterator();

            while(var20.hasNext()) {
               CommandFunction commandFunction3 = (CommandFunction)var20.next();
               Identifier identifier3 = commandFunction3.id();
               ReturnValueConsumer returnValueConsumer5 = wrapReturnValueConsumer(parentSource, resultConsumer, identifier3, returnValueConsumer4);
               enqueueFunction(args, control, commandDispatcher, abstractServerCommandSource, commandFunction3, identifier3, returnValueConsumer5, false);
            }

            control.enqueueAction((context, frame) -> {
               if (returnValueAdder.successful) {
                  returnValueConsumer.onSuccess(returnValueAdder.returnValue);
               }

            });
         }

      }
   }

   public interface ResultConsumer {
      void accept(Object source, Identifier id, int result);
   }

   private abstract static class Command extends ControlFlowAware.Helper implements ControlFlowAware.Command {
      Command() {
      }

      @Nullable
      protected abstract NbtCompound getArguments(CommandContext context) throws CommandSyntaxException;

      public void executeInner(ServerCommandSource serverCommandSource, ContextChain contextChain, ExecutionFlags executionFlags, ExecutionControl executionControl) throws CommandSyntaxException {
         CommandContext commandContext = contextChain.getTopContext().copyFor(serverCommandSource);
         Pair pair = CommandFunctionArgumentType.getIdentifiedFunctions(commandContext, "name");
         Collection collection = (Collection)pair.getSecond();
         if (collection.isEmpty()) {
            throw FunctionCommand.NO_FUNCTIONS_EXCEPTION.create(Text.of((Identifier)pair.getFirst()));
         } else {
            NbtCompound nbtCompound = this.getArguments(commandContext);
            ServerCommandSource serverCommandSource2 = FunctionCommand.createFunctionCommandSource(serverCommandSource);
            if (collection.size() == 1) {
               serverCommandSource.sendFeedback(() -> {
                  return Text.translatable("commands.function.scheduled.single", Text.of(((CommandFunction)collection.iterator().next()).id()));
               }, true);
            } else {
               serverCommandSource.sendFeedback(() -> {
                  return Text.translatable("commands.function.scheduled.multiple", Texts.join(collection.stream().map(CommandFunction::id).toList(), (Function)(Text::of)));
               }, true);
            }

            FunctionCommand.enqueueAction(collection, nbtCompound, serverCommandSource, serverCommandSource2, executionControl, FunctionCommand.RESULT_REPORTER, executionFlags);
         }
      }

      // $FF: synthetic method
      public void executeInner(final AbstractServerCommandSource source, final ContextChain contextChain, final ExecutionFlags flags, final ExecutionControl control) throws CommandSyntaxException {
         this.executeInner((ServerCommandSource)source, contextChain, flags, control);
      }
   }
}
