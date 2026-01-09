package net.minecraft.command.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.DynamicOps;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtParsing;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.Texts;
import net.minecraft.util.packrat.Parser;
import org.jetbrains.annotations.Nullable;

public class TextArgumentType extends ParserBackedArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("\"hello world\"", "'hello world'", "\"\"", "{text:\"hello world\"}", "[\"\"]");
   public static final DynamicCommandExceptionType INVALID_COMPONENT_EXCEPTION = new DynamicCommandExceptionType((text) -> {
      return Text.stringifiedTranslatable("argument.component.invalid", text);
   });
   private static final DynamicOps OPS;
   private static final Parser PARSER;

   private TextArgumentType(RegistryWrapper.WrapperLookup registries) {
      super(PARSER.withDecoding(registries.getOps(OPS), PARSER, TextCodecs.CODEC, INVALID_COMPONENT_EXCEPTION));
   }

   public static Text getTextArgument(CommandContext context, String name) {
      return (Text)context.getArgument(name, Text.class);
   }

   public static Text parseTextArgument(CommandContext context, String name, @Nullable Entity sender) throws CommandSyntaxException {
      return Texts.parse((ServerCommandSource)context.getSource(), (Text)getTextArgument(context, name), sender, 0);
   }

   public static Text parseTextArgument(CommandContext context, String name) throws CommandSyntaxException {
      return parseTextArgument(context, name, ((ServerCommandSource)context.getSource()).getEntity());
   }

   public static TextArgumentType text(CommandRegistryAccess registryAccess) {
      return new TextArgumentType(registryAccess);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   static {
      OPS = NbtOps.INSTANCE;
      PARSER = SnbtParsing.createParser(OPS);
   }
}
