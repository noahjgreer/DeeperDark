/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.enchantment.effect;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.AllOfEnchantmentEffects;
import net.minecraft.enchantment.effect.EnchantmentLocationBasedEffect;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record AllOfEnchantmentEffects.LocationBasedEffects(List<EnchantmentLocationBasedEffect> effects) implements EnchantmentLocationBasedEffect
{
    public static final MapCodec<AllOfEnchantmentEffects.LocationBasedEffects> CODEC = AllOfEnchantmentEffects.buildCodec(EnchantmentLocationBasedEffect.CODEC, AllOfEnchantmentEffects.LocationBasedEffects::new, AllOfEnchantmentEffects.LocationBasedEffects::effects);

    @Override
    public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos, boolean newlyApplied) {
        for (EnchantmentLocationBasedEffect enchantmentLocationBasedEffect : this.effects) {
            enchantmentLocationBasedEffect.apply(world, level, context, user, pos, newlyApplied);
        }
    }

    @Override
    public void remove(EnchantmentEffectContext context, Entity user, Vec3d pos, int level) {
        for (EnchantmentLocationBasedEffect enchantmentLocationBasedEffect : this.effects) {
            enchantmentLocationBasedEffect.remove(context, user, pos, level);
        }
    }

    public MapCodec<AllOfEnchantmentEffects.LocationBasedEffects> getCodec() {
        return CODEC;
    }
}
