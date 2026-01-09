package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.item.equipment.trim.ArmorTrimPatterns;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class SpawnArmorTrimsCommand {
   private static final List PATTERNS;
   private static final List MATERIALS;
   private static final ToIntFunction PATTERN_INDEX_GETTER;
   private static final ToIntFunction MATERIAL_INDEX_GETTER;
   private static final DynamicCommandExceptionType INVALID_PATTERN_EXCEPTION;

   public static void register(CommandDispatcher dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("spawn_armor_trims").requires(CommandManager.requirePermissionLevel(2))).then(CommandManager.literal("*_lag_my_game").executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), ((ServerCommandSource)context.getSource()).getPlayerOrThrow());
      }))).then(CommandManager.argument("pattern", RegistryKeyArgumentType.registryKey(RegistryKeys.TRIM_PATTERN)).executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), ((ServerCommandSource)context.getSource()).getPlayerOrThrow(), (RegistryKey)RegistryKeyArgumentType.getKey(context, "pattern", RegistryKeys.TRIM_PATTERN, INVALID_PATTERN_EXCEPTION));
      })));
   }

   private static int execute(ServerCommandSource source, PlayerEntity player) {
      return execute(source, player, source.getServer().getRegistryManager().getOrThrow(RegistryKeys.TRIM_PATTERN).streamEntries());
   }

   private static int execute(ServerCommandSource source, PlayerEntity player, RegistryKey pattern) {
      return execute(source, player, Stream.of((RegistryEntry.Reference)source.getServer().getRegistryManager().getOrThrow(RegistryKeys.TRIM_PATTERN).getOptional(pattern).orElseThrow()));
   }

   private static int execute(ServerCommandSource source, PlayerEntity player, Stream patterns) {
      ServerWorld serverWorld = source.getWorld();
      List list = patterns.sorted(Comparator.comparing((pattern) -> {
         return PATTERN_INDEX_GETTER.applyAsInt(pattern.registryKey());
      })).toList();
      List list2 = serverWorld.getRegistryManager().getOrThrow(RegistryKeys.TRIM_MATERIAL).streamEntries().sorted(Comparator.comparing((material) -> {
         return MATERIAL_INDEX_GETTER.applyAsInt(material.registryKey());
      })).toList();
      List list3 = getArmorItems(serverWorld.getRegistryManager().getOrThrow(RegistryKeys.ITEM));
      BlockPos blockPos = player.getBlockPos().offset((Direction)player.getHorizontalFacing(), 5);
      double d = 3.0;

      for(int i = 0; i < list2.size(); ++i) {
         RegistryEntry.Reference reference = (RegistryEntry.Reference)list2.get(i);

         for(int j = 0; j < list.size(); ++j) {
            RegistryEntry.Reference reference2 = (RegistryEntry.Reference)list.get(j);
            ArmorTrim armorTrim = new ArmorTrim(reference, reference2);

            for(int k = 0; k < list3.size(); ++k) {
               RegistryEntry.Reference reference3 = (RegistryEntry.Reference)list3.get(k);
               double e = (double)blockPos.getX() + 0.5 - (double)k * 3.0;
               double f = (double)blockPos.getY() + 0.5 + (double)i * 3.0;
               double g = (double)blockPos.getZ() + 0.5 + (double)(j * 10);
               ArmorStandEntity armorStandEntity = new ArmorStandEntity(serverWorld, e, f, g);
               armorStandEntity.setYaw(180.0F);
               armorStandEntity.setNoGravity(true);
               ItemStack itemStack = new ItemStack(reference3);
               EquippableComponent equippableComponent = (EquippableComponent)Objects.requireNonNull((EquippableComponent)itemStack.get(DataComponentTypes.EQUIPPABLE));
               itemStack.set(DataComponentTypes.TRIM, armorTrim);
               armorStandEntity.equipStack(equippableComponent.slot(), itemStack);
               if (k == 0) {
                  armorStandEntity.setCustomName(((ArmorTrimPattern)armorTrim.pattern().value()).getDescription(armorTrim.material()).copy().append(" & ").append(((ArmorTrimMaterial)armorTrim.material().value()).description()));
                  armorStandEntity.setCustomNameVisible(true);
               } else {
                  armorStandEntity.setInvisible(true);
               }

               serverWorld.spawnEntity(armorStandEntity);
            }
         }
      }

      source.sendFeedback(() -> {
         return Text.literal("Armorstands with trimmed armor spawned around you");
      }, true);
      return 1;
   }

   private static List getArmorItems(RegistryWrapper itemRegistry) {
      List list = new ArrayList();
      itemRegistry.streamEntries().forEach((entry) -> {
         EquippableComponent equippableComponent = (EquippableComponent)((Item)entry.value()).getComponents().get(DataComponentTypes.EQUIPPABLE);
         if (equippableComponent != null && equippableComponent.slot().getType() == EquipmentSlot.Type.HUMANOID_ARMOR && equippableComponent.assetId().isPresent()) {
            list.add(entry);
         }

      });
      return list;
   }

   static {
      PATTERNS = List.of(ArmorTrimPatterns.SENTRY, ArmorTrimPatterns.DUNE, ArmorTrimPatterns.COAST, ArmorTrimPatterns.WILD, ArmorTrimPatterns.WARD, ArmorTrimPatterns.EYE, ArmorTrimPatterns.VEX, ArmorTrimPatterns.TIDE, ArmorTrimPatterns.SNOUT, ArmorTrimPatterns.RIB, ArmorTrimPatterns.SPIRE, ArmorTrimPatterns.WAYFINDER, ArmorTrimPatterns.SHAPER, ArmorTrimPatterns.SILENCE, ArmorTrimPatterns.RAISER, ArmorTrimPatterns.HOST, ArmorTrimPatterns.FLOW, ArmorTrimPatterns.BOLT);
      MATERIALS = List.of(ArmorTrimMaterials.QUARTZ, ArmorTrimMaterials.IRON, ArmorTrimMaterials.NETHERITE, ArmorTrimMaterials.REDSTONE, ArmorTrimMaterials.COPPER, ArmorTrimMaterials.GOLD, ArmorTrimMaterials.EMERALD, ArmorTrimMaterials.DIAMOND, ArmorTrimMaterials.LAPIS, ArmorTrimMaterials.AMETHYST, ArmorTrimMaterials.RESIN);
      PATTERN_INDEX_GETTER = Util.lastIndexGetter(PATTERNS);
      MATERIAL_INDEX_GETTER = Util.lastIndexGetter(MATERIALS);
      INVALID_PATTERN_EXCEPTION = new DynamicCommandExceptionType((pattern) -> {
         return Text.stringifiedTranslatable("Invalid pattern", pattern);
      });
   }
}
