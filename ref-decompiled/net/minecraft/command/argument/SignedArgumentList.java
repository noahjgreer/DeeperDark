package net.minecraft.command.argument;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public record SignedArgumentList(List arguments) {
   public SignedArgumentList(List list) {
      this.arguments = list;
   }

   public static boolean isNotEmpty(ParseResults parseResults) {
      return !of(parseResults).arguments().isEmpty();
   }

   public static SignedArgumentList of(ParseResults parseResults) {
      String string = parseResults.getReader().getString();
      CommandContextBuilder commandContextBuilder = parseResults.getContext();
      CommandContextBuilder commandContextBuilder2 = commandContextBuilder;

      List list;
      CommandContextBuilder commandContextBuilder3;
      for(list = collectDecoratableArguments(string, commandContextBuilder); (commandContextBuilder3 = commandContextBuilder2.getChild()) != null && commandContextBuilder3.getRootNode() != commandContextBuilder.getRootNode(); commandContextBuilder2 = commandContextBuilder3) {
         list.addAll(collectDecoratableArguments(string, commandContextBuilder3));
      }

      return new SignedArgumentList(list);
   }

   private static List collectDecoratableArguments(String argumentName, CommandContextBuilder builder) {
      List list = new ArrayList();
      Iterator var3 = builder.getNodes().iterator();

      while(var3.hasNext()) {
         ParsedCommandNode parsedCommandNode = (ParsedCommandNode)var3.next();
         CommandNode var6 = parsedCommandNode.getNode();
         if (var6 instanceof ArgumentCommandNode argumentCommandNode) {
            if (argumentCommandNode.getType() instanceof SignedArgumentType) {
               com.mojang.brigadier.context.ParsedArgument parsedArgument = (com.mojang.brigadier.context.ParsedArgument)builder.getArguments().get(argumentCommandNode.getName());
               if (parsedArgument != null) {
                  String string = parsedArgument.getRange().get(argumentName);
                  list.add(new ParsedArgument(argumentCommandNode, string));
               }
            }
         }
      }

      return list;
   }

   @Nullable
   public ParsedArgument get(String name) {
      Iterator var2 = this.arguments.iterator();

      ParsedArgument parsedArgument;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         parsedArgument = (ParsedArgument)var2.next();
      } while(!name.equals(parsedArgument.getNodeName()));

      return parsedArgument;
   }

   public List arguments() {
      return this.arguments;
   }

   public static record ParsedArgument(ArgumentCommandNode node, String value) {
      public ParsedArgument(ArgumentCommandNode argumentCommandNode, String string) {
         this.node = argumentCommandNode;
         this.value = string;
      }

      public String getNodeName() {
         return this.node.getName();
      }

      public ArgumentCommandNode node() {
         return this.node;
      }

      public String value() {
         return this.value;
      }
   }
}
