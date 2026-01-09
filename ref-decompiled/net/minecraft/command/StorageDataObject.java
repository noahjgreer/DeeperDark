package net.minecraft.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class StorageDataObject implements DataCommandObject {
   static final SuggestionProvider SUGGESTION_PROVIDER = (context, builder) -> {
      return CommandSource.suggestIdentifiers(of(context).getIds(), builder);
   };
   public static final Function TYPE_FACTORY = (argumentName) -> {
      return new DataCommand.ObjectType() {
         public DataCommandObject getObject(CommandContext context) {
            return new StorageDataObject(StorageDataObject.of(context), IdentifierArgumentType.getIdentifier(context, argumentName));
         }

         public ArgumentBuilder addArgumentsToBuilder(ArgumentBuilder argument, Function argumentAdder) {
            return argument.then(CommandManager.literal("storage").then((ArgumentBuilder)argumentAdder.apply(CommandManager.argument(argumentName, IdentifierArgumentType.identifier()).suggests(StorageDataObject.SUGGESTION_PROVIDER))));
         }
      };
   };
   private final DataCommandStorage storage;
   private final Identifier id;

   static DataCommandStorage of(CommandContext context) {
      return ((ServerCommandSource)context.getSource()).getServer().getDataCommandStorage();
   }

   StorageDataObject(DataCommandStorage storage, Identifier id) {
      this.storage = storage;
      this.id = id;
   }

   public void setNbt(NbtCompound nbt) {
      this.storage.set(this.id, nbt);
   }

   public NbtCompound getNbt() {
      return this.storage.get(this.id);
   }

   public Text feedbackModify() {
      return Text.translatable("commands.data.storage.modified", Text.of(this.id));
   }

   public Text feedbackQuery(NbtElement element) {
      return Text.translatable("commands.data.storage.query", Text.of(this.id), NbtHelper.toPrettyPrintedText(element));
   }

   public Text feedbackGet(NbtPathArgumentType.NbtPath path, double scale, int result) {
      return Text.translatable("commands.data.storage.get", path.getString(), Text.of(this.id), String.format(Locale.ROOT, "%.2f", scale), result);
   }
}
