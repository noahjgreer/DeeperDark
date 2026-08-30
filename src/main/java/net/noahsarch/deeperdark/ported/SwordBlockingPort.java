package net.noahsarch.deeperdark.ported;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.level.block.Blocks;
import net.noahsarch.deeperdark.DeeperDarkConfig;

/**
 * Sword blocking, ported from Minecraft 1.8 - the last version in which it existed.
 *
 * <p>Reference sources live in {@code ref-decompiled/Minecraft/1.8} (MCP-named). The 1.8
 * implementation was three overrides on the sword plus one branch in the player:
 *
 * <pre>
 * // net/minecraft/item/ItemSword.java
 * public EnumAction getItemUseAction(ItemStack s)  { return EnumAction.BLOCK; }
 * public int getMaxItemUseDuration(ItemStack s)    { return 72000; }
 * public ItemStack onItemRightClick(ItemStack s, World w, EntityPlayer p) {
 *     p.setItemInUse(s, this.getMaxItemUseDuration(s));
 *     return s;
 * }
 *
 * // net/minecraft/entity/player/EntityPlayer.java
 * public boolean isBlocking() {
 *     return this.isUsingItem()
 *         &amp;&amp; this.itemInUse.getItem().getItemUseAction(this.itemInUse) == EnumAction.BLOCK;
 * }
 *
 * protected void damageEntity(DamageSource src, float dmg) {
 *     if (!this.isEntityInvulnerable(src)) {
 *         if (!src.isUnblockable() &amp;&amp; this.isBlocking() &amp;&amp; dmg &gt; 0.0F) {
 *             dmg = (1.0F + dmg) * 0.5F;          // &lt;-- the mitigation
 *         }
 *         dmg = this.applyArmorCalculations(src, dmg);
 *         ...
 * </pre>
 *
 * <h2>How this maps onto 26.1.2</h2>
 *
 * Modern Minecraft expresses all of the above through the {@code minecraft:blocks_attacks} data
 * component: {@code Item#use} starts using the item, {@code Item#getUseAnimation} returns
 * {@code ItemUseAnimation.BLOCK}, {@code Item#getUseDuration} returns the very same
 * {@code 72000}, and {@code LivingEntity#applyItemBlocking} subtracts the blocked damage
 * <em>before</em> armor - matching 1.8's ordering inside {@code damageEntity}. So rather than
 * re-implementing those overrides by hand, we attach a {@link BlocksAttacks} tuned to reproduce
 * 1.8's numbers exactly. Swords then become first-class blockers everywhere the game already
 * asks {@code isBlocking()} - mob AI, the third-person pose, knockback and statistics.
 *
 * <p>The one behavioural addition over 1.8 is the shield gate: this is a fallback for players
 * without a shield, so holding a real one suppresses it (see {@link #canSwordBlock}).
 */
public final class SwordBlockingPort {

    private static final org.slf4j.Logger LOGGER =
            org.slf4j.LoggerFactory.getLogger("DeeperDark/SwordBlocking");


    /**
     * 1.8 reduced damage to {@code (1.0F + dmg) * 0.5F}, i.e. it kept {@code 0.5 * dmg + 0.5}.
     * A {@link BlocksAttacks.DamageReduction} instead describes the amount <em>blocked</em>, so
     * blocked = {@code dmg - (0.5 * dmg + 0.5)} = {@code 0.5 * dmg - 0.5}, giving base -0.5 and
     * factor 0.5. The engine clamps that to {@code [0, dmg]}, which reproduces 1.8's behaviour
     * that hits of 1.0 damage or less come through entirely unreduced.
     */
    private static final float BLOCK_REDUCTION_BASE = -0.5F;
    private static final float BLOCK_REDUCTION_FACTOR = 0.5F;

    /** 1.8 had no directional check - a raised sword protected from every angle. */
    private static final float BLOCKING_ANGLE_DEGREES = 180.0F;

    /** 1.8 blocked on the very first tick; the shield's 0.25s wind-up did not exist. */
    private static final float BLOCK_DELAY_SECONDS = 0.0F;

    /** 1.8 had no axe-disables-blocking mechanic. */
    private static final float DISABLE_COOLDOWN_SCALE = 0.0F;

    /** Speed of the cobweb rule that {@code ToolMaterial.applySwordProperties} gives every sword. */
    private static final float COBWEB_MINING_SPEED = 15.0F;

    /** 1.8 swords took no durability damage for blocking: {@code floor(0 + 0 * dmg) == 0}. */
    private static final BlocksAttacks.ItemDamageFunction NO_ITEM_DAMAGE =
            new BlocksAttacks.ItemDamageFunction(0.0F, 0.0F, 0.0F);

    private SwordBlockingPort() {
    }

    public static void register() {
        List<String> matched = new ArrayList<>();
        DefaultItemComponentEvents.MODIFY.register(context -> {
            context.modify(SwordBlockingPort::isSword, (builder, lookupProvider, item) -> {
                matched.add(BuiltInRegistries.ITEM.getKey(item).toString());
                builder.set(DataComponents.BLOCKS_ATTACKS, new BlocksAttacks(
                        BLOCK_DELAY_SECONDS,
                        DISABLE_COOLDOWN_SCALE,
                        List.of(new BlocksAttacks.DamageReduction(
                                BLOCKING_ANGLE_DEGREES,
                                Optional.empty(),
                                BLOCK_REDUCTION_BASE,
                                BLOCK_REDUCTION_FACTOR)),
                        NO_ITEM_DAMAGE,
                        // 1.8 gated on DamageSource.isUnblockable(); the modern
                        // equivalent is the bypasses_shield damage type tag.
                        Optional.of(lookupProvider.lookupOrThrow(Registries.DAMAGE_TYPE)
                                .getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
                        Optional.of(SoundEvents.SHIELD_BLOCK),
                        Optional.empty()));
            });
            LOGGER.info("Sword blocking (1.8 port): attached blocks_attacks to {} item(s)", matched.size());
            LOGGER.debug("Sword blocking (1.8 port) applied to: {}", matched);
        });
    }

    /**
     * Identifies swords by the component fingerprint that {@code ToolMaterial.applySwordProperties}
     * leaves behind: a {@link Tool} costing 2 durability per block that cannot destroy blocks in
     * creative, whose rules include the sword's signature "shears cobwebs at speed 15" entry,
     * paired with a {@link Weapon} costing 1 durability per attack and never disabling blocking.
     *
     * <p>{@code ItemTags.SWORDS} would be the obvious test, but item tags come from datapacks and
     * are not bound yet when default components are assembled. Matching on the properties instead
     * also picks up modded swords, which are built through the same {@code Properties.sword(...)}
     * helper.
     *
     * <p>The cobweb rule is what keeps the mace and the trident out: both are {@code Weapon(1)}
     * tools with {@code damagePerBlock == 2} and no creative block breaking, so the durability
     * fields alone match them too. Only swords carry mining rules at all.
     */
    private static boolean isSword(Item item) {
        // Never touch anything that already blocks - shields, or another mod's blocking sword.
        return !item.components().has(DataComponents.BLOCKS_ATTACKS)
                && hasSwordFingerprint(item.components().get(DataComponents.TOOL),
                        item.components().get(DataComponents.WEAPON));
    }

    /** Whether {@code stack} is a sword carrying the blocking component we attached above. */
    public static boolean isBlockingSword(ItemStack stack) {
        return stack.has(DataComponents.BLOCKS_ATTACKS)
                && hasSwordFingerprint(stack.get(DataComponents.TOOL), stack.get(DataComponents.WEAPON));
    }

    private static boolean hasSwordFingerprint(Tool tool, Weapon weapon) {
        if (tool == null || weapon == null) {
            return false;
        }
        return tool.damagePerBlock() == 2
                && !tool.canDestroyBlocksInCreative()
                && weapon.itemDamagePerAttack() == 1
                && weapon.disableBlockingForSeconds() == 0.0F
                && minesCobwebsLikeASword(tool);
    }

    private static boolean minesCobwebsLikeASword(Tool tool) {
        for (Tool.Rule rule : tool.rules()) {
            if (rule.speed().isPresent()
                    && rule.speed().get() == COBWEB_MINING_SPEED
                    && rule.blocks().contains(Blocks.COBWEB.builtInRegistryHolder())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether {@code user} may raise {@code stack} as a sword-block right now.
     *
     * <p>Sword blocking stands in for a shield, so it yields to a real one: if either hand holds
     * something that blocks attacks in its own right, the interaction is passed along instead so
     * that shield gets used normally.
     */
    public static boolean canSwordBlock(LivingEntity user, ItemStack stack) {
        if (!DeeperDarkConfig.get().swordBlockingEnabled || !isBlockingSword(stack)) {
            return false;
        }
        return !DeeperDarkConfig.get().swordBlockingRequiresNoShield || !isHoldingShield(user, stack);
    }

    private static boolean isHoldingShield(LivingEntity user, ItemStack blockingStack) {
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack held = user.getItemInHand(hand);
            if (held != blockingStack && held.has(DataComponents.BLOCKS_ATTACKS) && !isBlockingSword(held)) {
                return true;
            }
        }
        return false;
    }
}
