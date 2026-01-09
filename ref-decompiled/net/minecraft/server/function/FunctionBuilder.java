package net.minecraft.server.function;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.command.MacroInvocation;
import net.minecraft.command.SourcedCommandAction;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

class FunctionBuilder {
   @Nullable
   private List actions = new ArrayList();
   @Nullable
   private List macroLines;
   private final List usedVariables = new ArrayList();

   public void addAction(SourcedCommandAction action) {
      if (this.macroLines != null) {
         this.macroLines.add(new Macro.FixedLine(action));
      } else {
         this.actions.add(action);
      }

   }

   private int indexOfVariable(String variable) {
      int i = this.usedVariables.indexOf(variable);
      if (i == -1) {
         i = this.usedVariables.size();
         this.usedVariables.add(variable);
      }

      return i;
   }

   private IntList indicesOfVariables(List variables) {
      IntArrayList intArrayList = new IntArrayList(variables.size());
      Iterator var3 = variables.iterator();

      while(var3.hasNext()) {
         String string = (String)var3.next();
         intArrayList.add(this.indexOfVariable(string));
      }

      return intArrayList;
   }

   public void addMacroCommand(String command, int lineNum, AbstractServerCommandSource source) {
      MacroInvocation macroInvocation;
      try {
         macroInvocation = MacroInvocation.parse(command);
      } catch (Exception var7) {
         throw new IllegalArgumentException("Can't parse function line " + lineNum + ": '" + command + "'", var7);
      }

      if (this.actions != null) {
         this.macroLines = new ArrayList(this.actions.size() + 1);
         Iterator var5 = this.actions.iterator();

         while(var5.hasNext()) {
            SourcedCommandAction sourcedCommandAction = (SourcedCommandAction)var5.next();
            this.macroLines.add(new Macro.FixedLine(sourcedCommandAction));
         }

         this.actions = null;
      }

      this.macroLines.add(new Macro.VariableLine(macroInvocation, this.indicesOfVariables(macroInvocation.variables()), source));
   }

   public CommandFunction toCommandFunction(Identifier id) {
      return (CommandFunction)(this.macroLines != null ? new Macro(id, this.macroLines, this.usedVariables) : new ExpandedMacro(id, this.actions));
   }
}
