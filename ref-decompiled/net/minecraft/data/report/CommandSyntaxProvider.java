package net.minecraft.data.report;

import com.mojang.brigadier.CommandDispatcher;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.server.command.CommandManager;

public class CommandSyntaxProvider implements DataProvider {
   private final DataOutput output;
   private final CompletableFuture registriesFuture;

   public CommandSyntaxProvider(DataOutput output, CompletableFuture registriesFuture) {
      this.output = output;
      this.registriesFuture = registriesFuture;
   }

   public CompletableFuture run(DataWriter writer) {
      Path path = this.output.resolvePath(DataOutput.OutputType.REPORTS).resolve("commands.json");
      return this.registriesFuture.thenCompose((registries) -> {
         CommandDispatcher commandDispatcher = (new CommandManager(CommandManager.RegistrationEnvironment.ALL, CommandManager.createRegistryAccess(registries))).getDispatcher();
         return DataProvider.writeToPath(writer, ArgumentHelper.toJson(commandDispatcher, commandDispatcher.getRoot()), path);
      });
   }

   public String getName() {
      return "Command Syntax";
   }
}
