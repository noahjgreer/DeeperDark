package net.minecraft.server.function;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.command.CommandExecutionContext;
import net.minecraft.command.ReturnValueConsumer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.slf4j.Logger;

public class CommandFunctionManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Identifier TICK_TAG_ID = Identifier.ofVanilla("tick");
   private static final Identifier LOAD_TAG_ID = Identifier.ofVanilla("load");
   private final MinecraftServer server;
   private List tickFunctions = ImmutableList.of();
   private boolean justLoaded;
   private FunctionLoader loader;

   public CommandFunctionManager(MinecraftServer server, FunctionLoader loader) {
      this.server = server;
      this.loader = loader;
      this.load(loader);
   }

   public CommandDispatcher getDispatcher() {
      return this.server.getCommandManager().getDispatcher();
   }

   public void tick() {
      if (this.server.getTickManager().shouldTick()) {
         if (this.justLoaded) {
            this.justLoaded = false;
            Collection collection = this.loader.getTagOrEmpty(LOAD_TAG_ID);
            this.executeAll(collection, LOAD_TAG_ID);
         }

         this.executeAll(this.tickFunctions, TICK_TAG_ID);
      }
   }

   private void executeAll(Collection functions, Identifier label) {
      Profiler var10000 = Profilers.get();
      Objects.requireNonNull(label);
      var10000.push(label::toString);
      Iterator var3 = functions.iterator();

      while(var3.hasNext()) {
         CommandFunction commandFunction = (CommandFunction)var3.next();
         this.execute(commandFunction, this.getScheduledCommandSource());
      }

      Profilers.get().pop();
   }

   public void execute(CommandFunction function, ServerCommandSource source) {
      Profiler profiler = Profilers.get();
      profiler.push(() -> {
         return "function " + String.valueOf(function.id());
      });

      try {
         Procedure procedure = function.withMacroReplaced((NbtCompound)null, this.getDispatcher());
         CommandManager.callWithContext(source, (context) -> {
            CommandExecutionContext.enqueueProcedureCall(context, procedure, source, ReturnValueConsumer.EMPTY);
         });
      } catch (MacroException var9) {
      } catch (Exception var10) {
         LOGGER.warn("Failed to execute function {}", function.id(), var10);
      } finally {
         profiler.pop();
      }

   }

   public void setFunctions(FunctionLoader loader) {
      this.loader = loader;
      this.load(loader);
   }

   private void load(FunctionLoader loader) {
      this.tickFunctions = List.copyOf(loader.getTagOrEmpty(TICK_TAG_ID));
      this.justLoaded = true;
   }

   public ServerCommandSource getScheduledCommandSource() {
      return this.server.getCommandSource().withLevel(2).withSilent();
   }

   public Optional getFunction(Identifier id) {
      return this.loader.get(id);
   }

   public List getTag(Identifier id) {
      return this.loader.getTagOrEmpty(id);
   }

   public Iterable getAllFunctions() {
      return this.loader.getFunctions().keySet();
   }

   public Iterable getFunctionTags() {
      return this.loader.getTags();
   }
}
