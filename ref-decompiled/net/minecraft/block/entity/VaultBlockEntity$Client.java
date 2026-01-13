/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import java.util.Set;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.block.VaultBlock;
import net.minecraft.block.vault.VaultClientData;
import net.minecraft.block.vault.VaultSharedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public static final class VaultBlockEntity.Client {
    private static final int field_48870 = 20;
    private static final float field_48871 = 0.5f;
    private static final float field_48872 = 0.02f;
    private static final int field_48873 = 20;
    private static final int field_48874 = 20;

    public static void tick(World world, BlockPos pos, BlockState state, VaultClientData clientData, VaultSharedData sharedData) {
        clientData.rotateDisplay();
        if (world.getTime() % 20L == 0L) {
            VaultBlockEntity.Client.spawnConnectedParticles(world, pos, state, sharedData);
        }
        VaultBlockEntity.Client.spawnAmbientParticles(world, pos, sharedData, state.get(VaultBlock.OMINOUS) != false ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME);
        VaultBlockEntity.Client.playAmbientSound(world, pos, sharedData);
    }

    public static void spawnActivateParticles(World world, BlockPos pos, BlockState state, VaultSharedData sharedData, ParticleEffect particle) {
        VaultBlockEntity.Client.spawnConnectedParticles(world, pos, state, sharedData);
        Random random = world.random;
        for (int i = 0; i < 20; ++i) {
            Vec3d vec3d = VaultBlockEntity.Client.getRegularParticlesPos(pos, random);
            world.addParticleClient(ParticleTypes.SMOKE, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 0.0, 0.0, 0.0);
            world.addParticleClient(particle, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 0.0, 0.0, 0.0);
        }
    }

    public static void spawnDeactivateParticles(World world, BlockPos pos, ParticleEffect particle) {
        Random random = world.random;
        for (int i = 0; i < 20; ++i) {
            Vec3d vec3d = VaultBlockEntity.Client.getDeactivateParticlesPos(pos, random);
            Vec3d vec3d2 = new Vec3d(random.nextGaussian() * 0.02, random.nextGaussian() * 0.02, random.nextGaussian() * 0.02);
            world.addParticleClient(particle, vec3d.getX(), vec3d.getY(), vec3d.getZ(), vec3d2.getX(), vec3d2.getY(), vec3d2.getZ());
        }
    }

    private static void spawnAmbientParticles(World world, BlockPos pos, VaultSharedData sharedData, ParticleEffect particle) {
        Random random = world.getRandom();
        if (random.nextFloat() <= 0.5f) {
            Vec3d vec3d = VaultBlockEntity.Client.getRegularParticlesPos(pos, random);
            world.addParticleClient(ParticleTypes.SMOKE, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 0.0, 0.0, 0.0);
            if (VaultBlockEntity.Client.hasDisplayItem(sharedData)) {
                world.addParticleClient(particle, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    private static void spawnConnectedParticlesFor(World world, Vec3d pos, PlayerEntity player) {
        Random random = world.random;
        Vec3d vec3d = pos.relativize(player.getEntityPos().add(0.0, player.getHeight() / 2.0f, 0.0));
        int i = MathHelper.nextInt(random, 2, 5);
        for (int j = 0; j < i; ++j) {
            Vec3d vec3d2 = vec3d.addRandom(random, 1.0f);
            world.addParticleClient(ParticleTypes.VAULT_CONNECTION, pos.getX(), pos.getY(), pos.getZ(), vec3d2.getX(), vec3d2.getY(), vec3d2.getZ());
        }
    }

    private static void spawnConnectedParticles(World world, BlockPos pos, BlockState state, VaultSharedData sharedData) {
        Set<UUID> set = sharedData.getConnectedPlayers();
        if (set.isEmpty()) {
            return;
        }
        Vec3d vec3d = VaultBlockEntity.Client.getConnectedParticlesOrigin(pos, state.get(VaultBlock.FACING));
        for (UUID uUID : set) {
            PlayerEntity playerEntity = world.getPlayerByUuid(uUID);
            if (playerEntity == null || !VaultBlockEntity.Client.isPlayerWithinConnectedParticlesRange(pos, sharedData, playerEntity)) continue;
            VaultBlockEntity.Client.spawnConnectedParticlesFor(world, vec3d, playerEntity);
        }
    }

    private static boolean isPlayerWithinConnectedParticlesRange(BlockPos pos, VaultSharedData sharedData, PlayerEntity player) {
        return player.getBlockPos().getSquaredDistance(pos) <= MathHelper.square(sharedData.getConnectedParticlesRange());
    }

    private static void playAmbientSound(World world, BlockPos pos, VaultSharedData sharedData) {
        if (!VaultBlockEntity.Client.hasDisplayItem(sharedData)) {
            return;
        }
        Random random = world.getRandom();
        if (random.nextFloat() <= 0.02f) {
            world.playSoundAtBlockCenterClient(pos, SoundEvents.BLOCK_VAULT_AMBIENT, SoundCategory.BLOCKS, random.nextFloat() * 0.25f + 0.75f, random.nextFloat() + 0.5f, false);
        }
    }

    public static boolean hasDisplayItem(VaultSharedData sharedData) {
        return sharedData.hasDisplayItem();
    }

    private static Vec3d getDeactivateParticlesPos(BlockPos pos, Random random) {
        return Vec3d.of(pos).add(MathHelper.nextDouble(random, 0.4, 0.6), MathHelper.nextDouble(random, 0.4, 0.6), MathHelper.nextDouble(random, 0.4, 0.6));
    }

    private static Vec3d getRegularParticlesPos(BlockPos pos, Random random) {
        return Vec3d.of(pos).add(MathHelper.nextDouble(random, 0.1, 0.9), MathHelper.nextDouble(random, 0.25, 0.75), MathHelper.nextDouble(random, 0.1, 0.9));
    }

    private static Vec3d getConnectedParticlesOrigin(BlockPos pos, Direction direction) {
        return Vec3d.ofBottomCenter(pos).add((double)direction.getOffsetX() * 0.5, 1.75, (double)direction.getOffsetZ() * 0.5);
    }
}
