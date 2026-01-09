package net.minecraft.command.argument;

import com.mojang.brigadier.context.CommandContext;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtParsing;
import net.minecraft.util.packrat.Parser;

public class NbtElementArgumentType extends ParserBackedArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("0", "0b", "0l", "0.0", "\"foo\"", "{foo=bar}", "[0]");
   private static final Parser PARSER;

   private NbtElementArgumentType() {
      super(PARSER);
   }

   public static NbtElementArgumentType nbtElement() {
      return new NbtElementArgumentType();
   }

   public static NbtElement getNbtElement(CommandContext context, String name) {
      return (NbtElement)context.getArgument(name, NbtElement.class);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   static {
      PARSER = SnbtParsing.createParser(NbtOps.INSTANCE);
   }
}
