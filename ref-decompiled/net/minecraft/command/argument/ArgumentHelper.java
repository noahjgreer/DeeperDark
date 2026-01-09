package net.minecraft.command.argument;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.command.PermissionLevelPredicate;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.registry.Registries;
import org.slf4j.Logger;

public class ArgumentHelper {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final byte MIN_FLAG = 1;
   private static final byte MAX_FLAG = 2;

   public static int getMinMaxFlag(boolean hasMin, boolean hasMax) {
      int i = 0;
      if (hasMin) {
         i |= 1;
      }

      if (hasMax) {
         i |= 2;
      }

      return i;
   }

   public static boolean hasMinFlag(byte flags) {
      return (flags & 1) != 0;
   }

   public static boolean hasMaxFlag(byte flags) {
      return (flags & 2) != 0;
   }

   private static void writeArgumentTypeProperties(JsonObject json, ArgumentSerializer serializer, ArgumentSerializer.ArgumentTypeProperties properties) {
      serializer.writeJson(properties, json);
   }

   private static void writeArgument(JsonObject json, ArgumentType argumentType) {
      ArgumentSerializer.ArgumentTypeProperties argumentTypeProperties = ArgumentTypes.getArgumentTypeProperties(argumentType);
      json.addProperty("type", "argument");
      json.addProperty("parser", String.valueOf(Registries.COMMAND_ARGUMENT_TYPE.getId(argumentTypeProperties.getSerializer())));
      JsonObject jsonObject = new JsonObject();
      writeArgumentTypeProperties(jsonObject, argumentTypeProperties.getSerializer(), argumentTypeProperties);
      if (!jsonObject.isEmpty()) {
         json.add("properties", jsonObject);
      }

   }

   public static JsonObject toJson(CommandDispatcher dispatcher, CommandNode node) {
      JsonObject jsonObject = new JsonObject();
      Objects.requireNonNull(node);
      byte var4 = 0;
      switch (node.typeSwitch<invokedynamic>(node, var4)) {
         case 0:
            RootCommandNode rootCommandNode = (RootCommandNode)node;
            jsonObject.addProperty("type", "root");
            break;
         case 1:
            LiteralCommandNode literalCommandNode = (LiteralCommandNode)node;
            jsonObject.addProperty("type", "literal");
            break;
         case 2:
            ArgumentCommandNode argumentCommandNode = (ArgumentCommandNode)node;
            writeArgument(jsonObject, argumentCommandNode.getType());
            break;
         default:
            LOGGER.error("Could not serialize node {} ({})!", node, node.getClass());
            jsonObject.addProperty("type", "unknown");
      }

      Collection collection = node.getChildren();
      if (!collection.isEmpty()) {
         JsonObject jsonObject2 = new JsonObject();
         Iterator var11 = collection.iterator();

         while(var11.hasNext()) {
            CommandNode commandNode = (CommandNode)var11.next();
            jsonObject2.add(commandNode.getName(), toJson(dispatcher, commandNode));
         }

         jsonObject.add("children", jsonObject2);
      }

      if (node.getCommand() != null) {
         jsonObject.addProperty("executable", true);
      }

      Predicate var12 = node.getRequirement();
      if (var12 instanceof PermissionLevelPredicate permissionLevelPredicate) {
         jsonObject.addProperty("required_level", permissionLevelPredicate.requiredLevel());
      }

      if (node.getRedirect() != null) {
         Collection collection2 = dispatcher.getPath(node.getRedirect());
         if (!collection2.isEmpty()) {
            JsonArray jsonArray = new JsonArray();
            Iterator var15 = collection2.iterator();

            while(var15.hasNext()) {
               String string = (String)var15.next();
               jsonArray.add(string);
            }

            jsonObject.add("redirect", jsonArray);
         }
      }

      return jsonObject;
   }

   public static Set collectUsedArgumentTypes(CommandNode rootNode) {
      Set set = new ReferenceOpenHashSet();
      Set set2 = new HashSet();
      collectUsedArgumentTypes(rootNode, set2, set);
      return set2;
   }

   private static void collectUsedArgumentTypes(CommandNode node, Set usedArgumentTypes, Set visitedNodes) {
      if (visitedNodes.add(node)) {
         if (node instanceof ArgumentCommandNode) {
            ArgumentCommandNode argumentCommandNode = (ArgumentCommandNode)node;
            usedArgumentTypes.add(argumentCommandNode.getType());
         }

         node.getChildren().forEach((child) -> {
            collectUsedArgumentTypes(child, usedArgumentTypes, visitedNodes);
         });
         CommandNode commandNode = node.getRedirect();
         if (commandNode != null) {
            collectUsedArgumentTypes(commandNode, usedArgumentTypes, visitedNodes);
         }

      }
   }
}
