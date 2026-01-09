package net.minecraft.server.function;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.command.MacroInvocation;
import net.minecraft.command.SourcedCommandAction;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public class Macro implements CommandFunction {
   private static final DecimalFormat DECIMAL_FORMAT = (DecimalFormat)Util.make(new DecimalFormat("#"), (format) -> {
      format.setMaximumFractionDigits(15);
      format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
   });
   private static final int CACHE_SIZE = 8;
   private final List varNames;
   private final Object2ObjectLinkedOpenHashMap cache = new Object2ObjectLinkedOpenHashMap(8, 0.25F);
   private final Identifier id;
   private final List lines;

   public Macro(Identifier id, List lines, List varNames) {
      this.id = id;
      this.lines = lines;
      this.varNames = varNames;
   }

   public Identifier id() {
      return this.id;
   }

   public Procedure withMacroReplaced(@Nullable NbtCompound arguments, CommandDispatcher dispatcher) throws MacroException {
      if (arguments == null) {
         throw new MacroException(Text.translatable("commands.function.error.missing_arguments", Text.of(this.id())));
      } else {
         List list = new ArrayList(this.varNames.size());
         Iterator var4 = this.varNames.iterator();

         while(var4.hasNext()) {
            String string = (String)var4.next();
            NbtElement nbtElement = arguments.get(string);
            if (nbtElement == null) {
               throw new MacroException(Text.translatable("commands.function.error.missing_argument", Text.of(this.id()), string));
            }

            list.add(toString(nbtElement));
         }

         Procedure procedure = (Procedure)this.cache.getAndMoveToLast(list);
         if (procedure != null) {
            return procedure;
         } else {
            if (this.cache.size() >= 8) {
               this.cache.removeFirst();
            }

            Procedure procedure2 = this.withMacroReplaced(this.varNames, list, dispatcher);
            this.cache.put(list, procedure2);
            return procedure2;
         }
      }
   }

   private static String toString(NbtElement nbt) {
      Objects.requireNonNull(nbt);
      byte var2 = 0;
      boolean var10001;
      Throwable var33;
      String var34;
      switch (nbt.typeSwitch<invokedynamic>(nbt, var2)) {
         case 0:
            NbtFloat var3 = (NbtFloat)nbt;
            NbtFloat var26 = var3;

            float var27;
            try {
               var27 = var26.value();
            } catch (Throwable var23) {
               var33 = var23;
               var10001 = false;
               break;
            }

            float var28 = var27;
            var34 = DECIMAL_FORMAT.format((double)var28);
            return var34;
         case 1:
            NbtDouble var5 = (NbtDouble)nbt;
            NbtDouble var24 = var5;

            double var25;
            try {
               var25 = var24.value();
            } catch (Throwable var22) {
               var33 = var22;
               var10001 = false;
               break;
            }

            double var29 = var25;
            var34 = DECIMAL_FORMAT.format(var29);
            return var34;
         case 2:
            NbtByte var8 = (NbtByte)nbt;
            NbtByte var39 = var8;

            byte var40;
            try {
               var40 = var39.value();
            } catch (Throwable var21) {
               var33 = var21;
               var10001 = false;
               break;
            }

            byte var30 = var40;
            var34 = String.valueOf(var30);
            return var34;
         case 3:
            NbtShort var10 = (NbtShort)nbt;
            NbtShort var37 = var10;

            short var38;
            try {
               var38 = var37.value();
            } catch (Throwable var20) {
               var33 = var20;
               var10001 = false;
               break;
            }

            short var31 = var38;
            var34 = String.valueOf(var31);
            return var34;
         case 4:
            NbtLong var12 = (NbtLong)nbt;
            NbtLong var35 = var12;

            long var36;
            try {
               var36 = var35.value();
            } catch (Throwable var19) {
               var33 = var19;
               var10001 = false;
               break;
            }

            long var32 = var36;
            var34 = String.valueOf(var32);
            return var34;
         case 5:
            NbtString var15 = (NbtString)nbt;
            NbtString var10000 = var15;

            try {
               var34 = var10000.value();
            } catch (Throwable var18) {
               var33 = var18;
               var10001 = false;
               break;
            }

            String var17 = var34;
            var34 = var17;
            return var34;
         default:
            var34 = nbt.toString();
            return var34;
      }

      Throwable var1 = var33;
      throw new MatchException(var1.toString(), var1);
   }

   private static void addArgumentsByIndices(List arguments, IntList indices, List out) {
      out.clear();
      indices.forEach((index) -> {
         out.add((String)arguments.get(index));
      });
   }

   private Procedure withMacroReplaced(List varNames, List arguments, CommandDispatcher dispatcher) throws MacroException {
      List list = new ArrayList(this.lines.size());
      List list2 = new ArrayList(arguments.size());
      Iterator var6 = this.lines.iterator();

      while(var6.hasNext()) {
         Line line = (Line)var6.next();
         addArgumentsByIndices(arguments, line.getDependentVariables(), list2);
         list.add(line.instantiate(list2, dispatcher, this.id));
      }

      return new ExpandedMacro(this.id().withPath((path) -> {
         return path + "/" + varNames.hashCode();
      }), list);
   }

   interface Line {
      IntList getDependentVariables();

      SourcedCommandAction instantiate(List args, CommandDispatcher dispatcher, Identifier id) throws MacroException;
   }

   static class VariableLine implements Line {
      private final MacroInvocation invocation;
      private final IntList variableIndices;
      private final AbstractServerCommandSource source;

      public VariableLine(MacroInvocation invocation, IntList variableIndices, AbstractServerCommandSource source) {
         this.invocation = invocation;
         this.variableIndices = variableIndices;
         this.source = source;
      }

      public IntList getDependentVariables() {
         return this.variableIndices;
      }

      public SourcedCommandAction instantiate(List args, CommandDispatcher dispatcher, Identifier id) throws MacroException {
         String string = this.invocation.apply(args);

         try {
            return CommandFunction.parse(dispatcher, this.source, new StringReader(string));
         } catch (CommandSyntaxException var6) {
            throw new MacroException(Text.translatable("commands.function.error.parse", Text.of(id), string, var6.getMessage()));
         }
      }
   }

   static class FixedLine implements Line {
      private final SourcedCommandAction action;

      public FixedLine(SourcedCommandAction action) {
         this.action = action;
      }

      public IntList getDependentVariables() {
         return IntLists.emptyList();
      }

      public SourcedCommandAction instantiate(List args, CommandDispatcher dispatcher, Identifier id) {
         return this.action;
      }
   }
}
