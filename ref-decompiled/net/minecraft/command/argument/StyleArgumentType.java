package net.minecraft.command.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.DynamicOps;
import java.util.Collection;
import java.util.List;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtParsing;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.packrat.Parser;

public class StyleArgumentType extends ParserBackedArgumentType {
   private static final Collection EXAMPLES = List.of("{bold: true}", "{color: 'red'}", "{}");
   public static final DynamicCommandExceptionType INVALID_STYLE_EXCEPTION = new DynamicCommandExceptionType((style) -> {
      return Text.stringifiedTranslatable("argument.style.invalid", style);
   });
   private static final DynamicOps OPS;
   private static final Parser PARSER;

   private StyleArgumentType(RegistryWrapper.WrapperLookup registries) {
      super(PARSER.withDecoding(registries.getOps(OPS), PARSER, Style.Codecs.CODEC, INVALID_STYLE_EXCEPTION));
   }

   public static Style getStyle(CommandContext context, String style) {
      return (Style)context.getArgument(style, Style.class);
   }

   public static StyleArgumentType style(CommandRegistryAccess registryAccess) {
      return new StyleArgumentType(registryAccess);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   static {
      OPS = NbtOps.INSTANCE;
      PARSER = SnbtParsing.createParser(OPS);
   }
}
