package net.minecraft.loot.context;

import net.minecraft.util.context.ContextParameter;

public class LootContextParameters {
   public static final ContextParameter THIS_ENTITY = ContextParameter.of("this_entity");
   public static final ContextParameter LAST_DAMAGE_PLAYER = ContextParameter.of("last_damage_player");
   public static final ContextParameter DAMAGE_SOURCE = ContextParameter.of("damage_source");
   public static final ContextParameter ATTACKING_ENTITY = ContextParameter.of("attacking_entity");
   public static final ContextParameter DIRECT_ATTACKING_ENTITY = ContextParameter.of("direct_attacking_entity");
   public static final ContextParameter ORIGIN = ContextParameter.of("origin");
   public static final ContextParameter BLOCK_STATE = ContextParameter.of("block_state");
   public static final ContextParameter BLOCK_ENTITY = ContextParameter.of("block_entity");
   public static final ContextParameter TOOL = ContextParameter.of("tool");
   public static final ContextParameter EXPLOSION_RADIUS = ContextParameter.of("explosion_radius");
   public static final ContextParameter ENCHANTMENT_LEVEL = ContextParameter.of("enchantment_level");
   public static final ContextParameter ENCHANTMENT_ACTIVE = ContextParameter.of("enchantment_active");
}
