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
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record AllOfEnchantmentEffects.EntityEffects(List<EnchantmentEntityEffect> effects) implements EnchantmentEntityEffect
{
    public static final MapCodec<AllOfEnchantmentEffects.EntityEffects> CODEC = AllOfEnchantmentEffects.buildCodec(EnchantmentEntityEffect.CODEC, AllOfEnchantmentEffects.EntityEffects::new, AllOfEnchantmentEffects.EntityEffects::effects);

    @Override
    public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
        for (EnchantmentEntityEffect enchantmentEntityEffect : this.effects) {
            enchantmentEntityEffect.apply(world, level, context, user, pos);
        }
    }

    public MapCodec<AllOfEnchantmentEffects.EntityEffects> getCodec() {
        return CODEC;
    }
}
