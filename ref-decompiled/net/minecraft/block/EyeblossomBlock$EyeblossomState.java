/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.TrailParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public static final class EyeblossomBlock.EyeblossomState
extends Enum<EyeblossomBlock.EyeblossomState> {
    public static final /* enum */ EyeblossomBlock.EyeblossomState OPEN = new EyeblossomBlock.EyeblossomState(true, StatusEffects.BLINDNESS, 11.0f, SoundEvents.BLOCK_EYEBLOSSOM_OPEN_LONG, SoundEvents.BLOCK_EYEBLOSSOM_OPEN, 16545810);
    public static final /* enum */ EyeblossomBlock.EyeblossomState CLOSED = new EyeblossomBlock.EyeblossomState(false, StatusEffects.NAUSEA, 7.0f, SoundEvents.BLOCK_EYEBLOSSOM_CLOSE_LONG, SoundEvents.BLOCK_EYEBLOSSOM_CLOSE, 0x5F5F5F);
    final boolean open;
    final RegistryEntry<StatusEffect> stewEffect;
    final float effectLengthInSeconds;
    final SoundEvent longSound;
    final SoundEvent sound;
    private final int particleColor;
    private static final /* synthetic */ EyeblossomBlock.EyeblossomState[] field_55078;

    public static EyeblossomBlock.EyeblossomState[] values() {
        return (EyeblossomBlock.EyeblossomState[])field_55078.clone();
    }

    public static EyeblossomBlock.EyeblossomState valueOf(String string) {
        return Enum.valueOf(EyeblossomBlock.EyeblossomState.class, string);
    }

    private EyeblossomBlock.EyeblossomState(boolean open, RegistryEntry<StatusEffect> stewEffect, float effectLengthInSeconds, SoundEvent longSound, SoundEvent sound, int particleColor) {
        this.open = open;
        this.stewEffect = stewEffect;
        this.effectLengthInSeconds = effectLengthInSeconds;
        this.longSound = longSound;
        this.sound = sound;
        this.particleColor = particleColor;
    }

    public Block getBlock() {
        return this.open ? Blocks.OPEN_EYEBLOSSOM : Blocks.CLOSED_EYEBLOSSOM;
    }

    public BlockState getBlockState() {
        return this.getBlock().getDefaultState();
    }

    public EyeblossomBlock.EyeblossomState getOpposite() {
        return EyeblossomBlock.EyeblossomState.of(!this.open);
    }

    public boolean isOpen() {
        return this.open;
    }

    public static EyeblossomBlock.EyeblossomState of(boolean open) {
        return open ? OPEN : CLOSED;
    }

    public void spawnTrailParticle(ServerWorld world, BlockPos pos, Random random) {
        Vec3d vec3d = pos.toCenterPos();
        double d = 0.5 + random.nextDouble();
        Vec3d vec3d2 = new Vec3d(random.nextDouble() - 0.5, random.nextDouble() + 1.0, random.nextDouble() - 0.5);
        Vec3d vec3d3 = vec3d.add(vec3d2.multiply(d));
        TrailParticleEffect trailParticleEffect = new TrailParticleEffect(vec3d3, this.particleColor, (int)(20.0 * d));
        world.spawnParticles(trailParticleEffect, vec3d.x, vec3d.y, vec3d.z, 1, 0.0, 0.0, 0.0, 0.0);
    }

    public SoundEvent getLongSound() {
        return this.longSound;
    }

    private static /* synthetic */ EyeblossomBlock.EyeblossomState[] method_65159() {
        return new EyeblossomBlock.EyeblossomState[]{OPEN, CLOSED};
    }

    static {
        field_55078 = EyeblossomBlock.EyeblossomState.method_65159();
    }
}
