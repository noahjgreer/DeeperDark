package net.noahsarch.deeperdark.creepiness;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Util;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * The scares a player can be dealt. Each one plays to a single player and returns false when
 * the surroundings don't suit it, so the scheduler can try another.
 */
public enum Haunt {
    /** Pre-20w12a cave ambience: a close, directional cave sound from a dark air block on the ±4 grid. */
    CAVE_SOUNDS("cave_sounds", c -> c.caveSounds, (c, v) -> c.caveSounds = v) {
        @Override
        boolean play(ServerPlayer player, HauntState state, int now) {
            ServerLevel level = player.level();
            RandomSource random = player.getRandom();
            BlockPos feet = player.blockPosition();
            List<BlockPos> grid = new ArrayList<>();
            for (int x = -4; x <= 4; x += 4)
                for (int y = -4; y <= 4; y += 4)
                    for (int z = -4; z <= 4; z += 4)
                        if (x != 0 || y != 0 || z != 0) grid.add(feet.offset(x, y, z));
            Util.shuffle(grid, random);
            for (BlockPos pos : grid) {
                if (level.getBlockState(pos).isAir()
                        && level.getBrightness(LightLayer.SKY, pos) == 0
                        && level.getBrightness(LightLayer.BLOCK, pos) <= random.nextInt(9)) {
                    HauntState.send(player, SoundEvents.AMBIENT_CAVE, SoundSource.AMBIENT, Vec3.atCenterOf(pos), 0.7f, 0.8f + random.nextFloat() * 0.2f);
                    return true;
                }
            }
            return false;
        }
    },

    /** Someone mining a block or three in the wall nearby, using that block's own sounds. */
    MINING("mining", c -> c.mining, (c, v) -> c.mining = v) {
        @Override
        boolean play(ServerPlayer player, HauntState state, int now) {
            BlockPos pos = findExposedBlock(player);
            if (pos == null) return false;
            RandomSource random = player.getRandom();
            SoundType type = player.level().getBlockState(pos).getSoundType();
            Vec3 at = Vec3.atCenterOf(pos);
            int t = now;
            for (int block = random.nextIntBetweenInclusive(1, 3); block > 0; block--) {
                for (int hit = random.nextIntBetweenInclusive(4, 10); hit > 0; hit--) {
                    t += 4 + random.nextInt(2);
                    state.queue(t, sound(type.getHitSound()), SoundSource.BLOCKS, at, (type.getVolume() + 1) / 4, type.getPitch() * 0.5f);
                }
                t += 2;
                state.queue(t, sound(type.getBreakSound()), SoundSource.BLOCKS, at, (type.getVolume() + 1) / 2, type.getPitch() * 0.8f);
                t += random.nextIntBetweenInclusive(10, 30);
            }
            return true;
        }
    },

    /** A few footsteps walking past, out of sight, on whatever ground is there. */
    FOOTSTEPS("footsteps", c -> c.footsteps, (c, v) -> c.footsteps = v) {
        @Override
        boolean play(ServerPlayer player, HauntState state, int now) {
            ServerLevel level = player.level();
            RandomSource random = player.getRandom();
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = 7 + random.nextDouble() * 4;
            Vec3 start = player.position().add(Math.cos(angle) * distance, 0, Math.sin(angle) * distance);
            // Walk tangentially so the steps pass by rather than approach
            Vec3 stride = new Vec3(-Math.sin(angle), 0, Math.cos(angle)).scale(random.nextBoolean() ? 1.67 : -1.67);
            int steps = random.nextIntBetweenInclusive(3, 4);
            int t = now;
            boolean any = false;
            for (int i = 0; i < steps; i++) {
                t += 8;
                BlockPos ground = findGround(level, BlockPos.containing(start.add(stride.scale(i))));
                if (ground == null) continue;
                SoundType type = level.getBlockState(ground).getSoundType();
                state.queue(t, sound(type.getStepSound()), SoundSource.PLAYERS, Vec3.atBottomCenterOf(ground.above()), type.getVolume() * 0.3f, type.getPitch());
                any = true;
            }
            return any;
        }
    },

    /** A nearby door or chest opening and closing by itself. Only the sound; nothing moves. */
    DOORS("doors", c -> c.doors, (c, v) -> c.doors = v) {
        @Override
        boolean play(ServerPlayer player, HauntState state, int now) {
            ServerLevel level = player.level();
            RandomSource random = player.getRandom();
            BlockPos feet = player.blockPosition();
            List<BlockPos> found = new ArrayList<>();
            for (BlockPos pos : BlockPos.betweenClosed(feet.offset(-12, -6, -12), feet.offset(12, 6, 12))) {
                if (pos.distSqr(feet) < 16) continue;
                var block = level.getBlockState(pos).getBlock();
                if (block instanceof DoorBlock || block instanceof ChestBlock || block instanceof BarrelBlock) found.add(pos.immutable());
            }
            if (found.isEmpty()) return false;

            BlockPos pos = found.get(random.nextInt(found.size()));
            var block = level.getBlockState(pos).getBlock();
            Vec3 at = Vec3.atCenterOf(pos);
            float pitch = 0.9f + random.nextFloat() * 0.1f;
            if (block instanceof DoorBlock door) {
                HauntState.send(player, sound(door.type().doorOpen()), SoundSource.BLOCKS, at, 1.0f, pitch);
                state.queue(now + random.nextIntBetweenInclusive(20, 60), sound(door.type().doorClose()), SoundSource.BLOCKS, at, 1.0f, pitch);
            } else if (block instanceof ChestBlock chest) {
                HauntState.send(player, sound(chest.getOpenChestSound()), SoundSource.BLOCKS, at, 0.5f, pitch);
                state.queue(now + random.nextIntBetweenInclusive(15, 40), sound(chest.getCloseChestSound()), SoundSource.BLOCKS, at, 0.5f, pitch);
            } else {
                HauntState.send(player, sound(SoundEvents.BARREL_OPEN), SoundSource.BLOCKS, at, 0.5f, pitch);
                state.queue(now + random.nextIntBetweenInclusive(15, 40), sound(SoundEvents.BARREL_CLOSE), SoundSource.BLOCKS, at, 0.5f, pitch);
            }
            return true;
        }
    },

    /** A hostile mob's sound from inside solid rock beside or below the player, with no mob there. */
    PHANTOM_MOBS("phantom_mobs", c -> c.phantomMobs, (c, v) -> c.phantomMobs = v) {
        @Override
        boolean play(ServerPlayer player, HauntState state, int now) {
            ServerLevel level = player.level();
            RandomSource random = player.getRandom();
            BlockPos feet = player.blockPosition();
            for (int attempt = 0; attempt < 20; attempt++) {
                BlockPos pos = feet.offset(random.nextIntBetweenInclusive(-6, 6), random.nextIntBetweenInclusive(-7, 1), random.nextIntBetweenInclusive(-6, 6));
                if (pos.distSqr(feet) < 9 || !level.getBlockState(pos).isCollisionShapeFullBlock(level, pos)) continue;

                int roll = random.nextInt(10);
                SoundEvent event = roll < 4 ? SoundEvents.ZOMBIE_AMBIENT
                        : roll < 7 ? SoundEvents.SKELETON_AMBIENT
                        : roll < 9 ? SoundEvents.SPIDER_AMBIENT
                        : SoundEvents.CREEPER_PRIMED;
                // Pitched down and quieter, as if muffled by the stone
                float pitch = event == SoundEvents.CREEPER_PRIMED ? 0.5f : 0.75f + random.nextFloat() * 0.2f;
                HauntState.send(player, sound(event), SoundSource.HOSTILE, Vec3.atCenterOf(pos), 0.6f, pitch);
                return true;
            }
            return false;
        }
    },

    /** Footsteps that copy the player's own, half a second late and a few blocks behind. */
    FOLLOWER("follower", c -> c.follower, (c, v) -> c.follower = v) {
        @Override
        boolean play(ServerPlayer player, HauntState state, int now) {
            if (state.followerActive()) return false;
            state.startFollower(player.getRandom().nextIntBetweenInclusive(6, 14), 1200);
            return true;
        }
    },

    /** A block the player recently mined reappears on their client only, then vanishes again. */
    GHOST_BLOCKS("ghost_blocks", c -> c.ghostBlocks, (c, v) -> c.ghostBlocks = v) {
        @Override
        boolean play(ServerPlayer player, HauntState state, int now) {
            if (state.minedPos == null || player.level() != state.minedLevel || now - state.minedTick > 6000) return false;
            double distSqr = state.minedPos.distToCenterSqr(player.position());
            if (distSqr < 9 || distSqr > 24 * 24 || !state.minedLevel.getBlockState(state.minedPos).isAir()) return false;
            state.showGhost(player, now, player.getRandom().nextIntBetweenInclusive(100, 300));
            return true;
        }
    };

    public final String key;
    final Predicate<CreepinessConfig> enabled;
    final BiConsumer<CreepinessConfig, Boolean> setEnabled;

    Haunt(String key, Predicate<CreepinessConfig> enabled, BiConsumer<CreepinessConfig, Boolean> setEnabled) {
        this.key = key;
        this.enabled = enabled;
        this.setEnabled = setEnabled;
    }

    abstract boolean play(ServerPlayer player, HauntState state, int now);

    private static Holder<SoundEvent> sound(SoundEvent event) {
        return BuiltInRegistries.SOUND_EVENT.wrapAsHolder(event);
    }

    /** Air with a full block under it, searching a few blocks up and down from {@code pos}. */
    private static BlockPos findGround(ServerLevel level, BlockPos pos) {
        for (int dy = 2; dy >= -3; dy--) {
            BlockPos below = pos.offset(0, dy - 1, 0);
            if (level.getBlockState(pos.offset(0, dy, 0)).isAir() && level.getBlockState(below).isCollisionShapeFullBlock(level, below)) {
                return below;
            }
        }
        return null;
    }

    /**
     * A full block 4–10 blocks away with an air face, preferring stone and wood over dirt so a
     * forest sounds like chopping and a cave like digging.
     */
    private static BlockPos findExposedBlock(ServerPlayer player) {
        ServerLevel level = player.level();
        RandomSource random = player.getRandom();
        BlockPos feet = player.blockPosition();
        BlockPos fallback = null;
        for (int attempt = 0; attempt < 64; attempt++) {
            BlockPos pos = feet.offset(random.nextIntBetweenInclusive(-10, 10), random.nextIntBetweenInclusive(-4, 4), random.nextIntBetweenInclusive(-10, 10));
            double distSqr = pos.distSqr(feet);
            if (distSqr < 16 || distSqr > 100) continue;
            BlockState state = level.getBlockState(pos);
            if (!state.isCollisionShapeFullBlock(level, pos) || !hasAirFace(level, pos)) continue;
            if (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.LOGS)) return pos;
            if (fallback == null) fallback = pos;
        }
        return fallback;
    }

    private static boolean hasAirFace(ServerLevel level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (level.getBlockState(pos.relative(direction)).isAir()) return true;
        }
        return false;
    }
}
