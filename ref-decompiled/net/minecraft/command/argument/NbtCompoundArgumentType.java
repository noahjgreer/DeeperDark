package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;

public class NbtCompoundArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("{}", "{foo=bar}");

   private NbtCompoundArgumentType() {
   }

   public static NbtCompoundArgumentType nbtCompound() {
      return new NbtCompoundArgumentType();
   }

   public static NbtCompound getNbtCompound(CommandContext context, String name) {
      return (NbtCompound)context.getArgument(name, NbtCompound.class);
   }

   public NbtCompound parse(StringReader stringReader) throws CommandSyntaxException {
      return StringNbtReader.readCompoundAsArgument(stringReader);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }
}
