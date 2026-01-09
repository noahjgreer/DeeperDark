package net.minecraft.data.tag.vanilla;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.tag.SimpleTagProvider;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;

public class VanillaDamageTypeTagProvider extends SimpleTagProvider {
   public VanillaDamageTypeTagProvider(DataOutput output, CompletableFuture registriesFuture) {
      super(output, RegistryKeys.DAMAGE_TYPE, registriesFuture);
   }

   protected void configure(RegistryWrapper.WrapperLookup registries) {
      this.builder(DamageTypeTags.DAMAGES_HELMET).add((Object[])(DamageTypes.FALLING_ANVIL, DamageTypes.FALLING_BLOCK, DamageTypes.FALLING_STALACTITE));
      this.builder(DamageTypeTags.BYPASSES_ARMOR).add((Object[])(DamageTypes.ON_FIRE, DamageTypes.IN_WALL, DamageTypes.CRAMMING, DamageTypes.DROWN, DamageTypes.FLY_INTO_WALL, DamageTypes.GENERIC, DamageTypes.WITHER, DamageTypes.DRAGON_BREATH, DamageTypes.STARVE, DamageTypes.FALL, DamageTypes.ENDER_PEARL, DamageTypes.FREEZE, DamageTypes.STALAGMITE, DamageTypes.MAGIC, DamageTypes.INDIRECT_MAGIC, DamageTypes.OUT_OF_WORLD, DamageTypes.GENERIC_KILL, DamageTypes.SONIC_BOOM, DamageTypes.OUTSIDE_BORDER));
      this.builder(DamageTypeTags.BYPASSES_SHIELD).addTag(DamageTypeTags.BYPASSES_ARMOR).add((Object[])(DamageTypes.CACTUS, DamageTypes.CAMPFIRE, DamageTypes.DRY_OUT, DamageTypes.FALLING_ANVIL, DamageTypes.FALLING_STALACTITE, DamageTypes.HOT_FLOOR, DamageTypes.IN_FIRE, DamageTypes.LAVA, DamageTypes.LIGHTNING_BOLT, DamageTypes.SWEET_BERRY_BUSH));
      this.builder(DamageTypeTags.BYPASSES_INVULNERABILITY).add((Object[])(DamageTypes.OUT_OF_WORLD, DamageTypes.GENERIC_KILL));
      this.builder(DamageTypeTags.BYPASSES_EFFECTS).add((Object)DamageTypes.STARVE);
      this.builder(DamageTypeTags.BYPASSES_RESISTANCE).add((Object[])(DamageTypes.OUT_OF_WORLD, DamageTypes.GENERIC_KILL));
      this.builder(DamageTypeTags.BYPASSES_ENCHANTMENTS).add((Object)DamageTypes.SONIC_BOOM);
      this.builder(DamageTypeTags.IS_FIRE).add((Object[])(DamageTypes.IN_FIRE, DamageTypes.CAMPFIRE, DamageTypes.ON_FIRE, DamageTypes.LAVA, DamageTypes.HOT_FLOOR, DamageTypes.UNATTRIBUTED_FIREBALL, DamageTypes.FIREBALL));
      this.builder(DamageTypeTags.IS_PROJECTILE).add((Object[])(DamageTypes.ARROW, DamageTypes.TRIDENT, DamageTypes.MOB_PROJECTILE, DamageTypes.UNATTRIBUTED_FIREBALL, DamageTypes.FIREBALL, DamageTypes.WITHER_SKULL, DamageTypes.THROWN, DamageTypes.WIND_CHARGE));
      this.builder(DamageTypeTags.WITCH_RESISTANT_TO).add((Object[])(DamageTypes.MAGIC, DamageTypes.INDIRECT_MAGIC, DamageTypes.SONIC_BOOM, DamageTypes.THORNS));
      this.builder(DamageTypeTags.IS_EXPLOSION).add((Object[])(DamageTypes.FIREWORKS, DamageTypes.EXPLOSION, DamageTypes.PLAYER_EXPLOSION, DamageTypes.BAD_RESPAWN_POINT));
      this.builder(DamageTypeTags.IS_FALL).add((Object[])(DamageTypes.FALL, DamageTypes.ENDER_PEARL, DamageTypes.STALAGMITE));
      this.builder(DamageTypeTags.IS_DROWNING).add((Object)DamageTypes.DROWN);
      this.builder(DamageTypeTags.IS_FREEZING).add((Object)DamageTypes.FREEZE);
      this.builder(DamageTypeTags.IS_LIGHTNING).add((Object)DamageTypes.LIGHTNING_BOLT);
      this.builder(DamageTypeTags.NO_ANGER).add((Object)DamageTypes.MOB_ATTACK_NO_AGGRO);
      this.builder(DamageTypeTags.NO_IMPACT).add((Object)DamageTypes.DROWN);
      this.builder(DamageTypeTags.ALWAYS_MOST_SIGNIFICANT_FALL).add((Object)DamageTypes.OUT_OF_WORLD);
      this.builder(DamageTypeTags.WITHER_IMMUNE_TO).add((Object)DamageTypes.DROWN);
      this.builder(DamageTypeTags.IGNITES_ARMOR_STANDS).add((Object[])(DamageTypes.IN_FIRE, DamageTypes.CAMPFIRE));
      this.builder(DamageTypeTags.BURNS_ARMOR_STANDS).add((Object)DamageTypes.ON_FIRE);
      this.builder(DamageTypeTags.AVOIDS_GUARDIAN_THORNS).add((Object[])(DamageTypes.MAGIC, DamageTypes.THORNS)).addTag(DamageTypeTags.IS_EXPLOSION);
      this.builder(DamageTypeTags.ALWAYS_TRIGGERS_SILVERFISH).add((Object)DamageTypes.MAGIC);
      this.builder(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS).addTag(DamageTypeTags.IS_EXPLOSION);
      this.builder(DamageTypeTags.NO_KNOCKBACK).add((Object[])(DamageTypes.EXPLOSION, DamageTypes.PLAYER_EXPLOSION, DamageTypes.BAD_RESPAWN_POINT, DamageTypes.IN_FIRE, DamageTypes.LIGHTNING_BOLT, DamageTypes.ON_FIRE, DamageTypes.LAVA, DamageTypes.HOT_FLOOR, DamageTypes.IN_WALL, DamageTypes.CRAMMING, DamageTypes.DROWN, DamageTypes.STARVE, DamageTypes.CACTUS, DamageTypes.FALL, DamageTypes.ENDER_PEARL, DamageTypes.FLY_INTO_WALL, DamageTypes.OUT_OF_WORLD, DamageTypes.GENERIC, DamageTypes.MAGIC, DamageTypes.WITHER, DamageTypes.DRAGON_BREATH, DamageTypes.DRY_OUT, DamageTypes.SWEET_BERRY_BUSH, DamageTypes.FREEZE, DamageTypes.STALAGMITE, DamageTypes.OUTSIDE_BORDER, DamageTypes.GENERIC_KILL, DamageTypes.CAMPFIRE));
      this.builder(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS).add((Object[])(DamageTypes.ARROW, DamageTypes.TRIDENT, DamageTypes.FIREBALL, DamageTypes.WITHER_SKULL, DamageTypes.WIND_CHARGE));
      this.builder(DamageTypeTags.CAN_BREAK_ARMOR_STAND).add((Object)DamageTypes.PLAYER_EXPLOSION).addTag(DamageTypeTags.IS_PLAYER_ATTACK);
      this.builder(DamageTypeTags.BYPASSES_WOLF_ARMOR).addTag(DamageTypeTags.BYPASSES_INVULNERABILITY).add((Object[])(DamageTypes.CRAMMING, DamageTypes.DROWN, DamageTypes.DRY_OUT, DamageTypes.FREEZE, DamageTypes.IN_WALL, DamageTypes.INDIRECT_MAGIC, DamageTypes.MAGIC, DamageTypes.OUTSIDE_BORDER, DamageTypes.STARVE, DamageTypes.THORNS, DamageTypes.WITHER));
      this.builder(DamageTypeTags.IS_PLAYER_ATTACK).add((Object[])(DamageTypes.PLAYER_ATTACK, DamageTypes.MACE_SMASH));
      this.builder(DamageTypeTags.BURN_FROM_STEPPING).add((Object[])(DamageTypes.CAMPFIRE, DamageTypes.HOT_FLOOR));
      this.builder(DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES).add((Object[])(DamageTypes.CACTUS, DamageTypes.FREEZE, DamageTypes.HOT_FLOOR, DamageTypes.IN_FIRE, DamageTypes.LAVA, DamageTypes.LIGHTNING_BOLT, DamageTypes.ON_FIRE));
      this.builder(DamageTypeTags.PANIC_CAUSES).addTag(DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES).add((Object[])(DamageTypes.ARROW, DamageTypes.DRAGON_BREATH, DamageTypes.EXPLOSION, DamageTypes.FIREBALL, DamageTypes.FIREWORKS, DamageTypes.INDIRECT_MAGIC, DamageTypes.MAGIC, DamageTypes.MOB_ATTACK, DamageTypes.MOB_PROJECTILE, DamageTypes.PLAYER_EXPLOSION, DamageTypes.SONIC_BOOM, DamageTypes.STING, DamageTypes.THROWN, DamageTypes.TRIDENT, DamageTypes.UNATTRIBUTED_FIREBALL, DamageTypes.WIND_CHARGE, DamageTypes.WITHER, DamageTypes.WITHER_SKULL)).addTag(DamageTypeTags.IS_PLAYER_ATTACK);
      this.builder(DamageTypeTags.MACE_SMASH).add((Object)DamageTypes.MACE_SMASH);
   }
}
