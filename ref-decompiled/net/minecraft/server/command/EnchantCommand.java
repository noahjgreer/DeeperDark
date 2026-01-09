package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

public class EnchantCommand {
   private static final DynamicCommandExceptionType FAILED_ENTITY_EXCEPTION = new DynamicCommandExceptionType((entityName) -> {
      return Text.stringifiedTranslatable("commands.enchant.failed.entity", entityName);
   });
   private static final DynamicCommandExceptionType FAILED_ITEMLESS_EXCEPTION = new DynamicCommandExceptionType((entityName) -> {
      return Text.stringifiedTranslatable("commands.enchant.failed.itemless", entityName);
   });
   private static final DynamicCommandExceptionType FAILED_INCOMPATIBLE_EXCEPTION = new DynamicCommandExceptionType((itemName) -> {
      return Text.stringifiedTranslatable("commands.enchant.failed.incompatible", itemName);
   });
   private static final Dynamic2CommandExceptionType FAILED_LEVEL_EXCEPTION = new Dynamic2CommandExceptionType((level, maxLevel) -> {
      return Text.stringifiedTranslatable("commands.enchant.failed.level", level, maxLevel);
   });
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.enchant.failed"));

   public static void register(CommandDispatcher dispatcher, CommandRegistryAccess registryAccess) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("enchant").requires(CommandManager.requirePermissionLevel(2))).then(CommandManager.argument("targets", EntityArgumentType.entities()).then(((RequiredArgumentBuilder)CommandManager.argument("enchantment", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, RegistryKeys.ENCHANTMENT)).executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"), RegistryEntryReferenceArgumentType.getEnchantment(context, "enchantment"), 1);
      })).then(CommandManager.argument("level", IntegerArgumentType.integer(0)).executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"), RegistryEntryReferenceArgumentType.getEnchantment(context, "enchantment"), IntegerArgumentType.getInteger(context, "level"));
      })))));
   }

   private static int execute(ServerCommandSource source, Collection targets, RegistryEntry enchantment, int level) throws CommandSyntaxException {
      Enchantment enchantment2 = (Enchantment)enchantment.value();
      if (level > enchantment2.getMaxLevel()) {
         throw FAILED_LEVEL_EXCEPTION.create(level, enchantment2.getMaxLevel());
      } else {
         int i = 0;
         Iterator var6 = targets.iterator();

         while(true) {
            while(true) {
               while(true) {
                  while(var6.hasNext()) {
                     Entity entity = (Entity)var6.next();
                     if (entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity)entity;
                        ItemStack itemStack = livingEntity.getMainHandStack();
                        if (!itemStack.isEmpty()) {
                           if (enchantment2.isAcceptableItem(itemStack) && EnchantmentHelper.isCompatible(EnchantmentHelper.getEnchantments(itemStack).getEnchantments(), enchantment)) {
                              itemStack.addEnchantment(enchantment, level);
                              ++i;
                           } else if (targets.size() == 1) {
                              throw FAILED_INCOMPATIBLE_EXCEPTION.create(itemStack.getName().getString());
                           }
                        } else if (targets.size() == 1) {
                           throw FAILED_ITEMLESS_EXCEPTION.create(livingEntity.getName().getString());
                        }
                     } else if (targets.size() == 1) {
                        throw FAILED_ENTITY_EXCEPTION.create(entity.getName().getString());
                     }
                  }

                  if (i == 0) {
                     throw FAILED_EXCEPTION.create();
                  }

                  if (targets.size() == 1) {
                     source.sendFeedback(() -> {
                        return Text.translatable("commands.enchant.success.single", Enchantment.getName(enchantment, level), ((Entity)targets.iterator().next()).getDisplayName());
                     }, true);
                  } else {
                     source.sendFeedback(() -> {
                        return Text.translatable("commands.enchant.success.multiple", Enchantment.getName(enchantment, level), targets.size());
                     }, true);
                  }

                  return i;
               }
            }
         }
      }
   }
}
