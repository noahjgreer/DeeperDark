package net.noahsarch.deeperdark.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ARGB;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.gamerules.GameRules;
import net.noahsarch.deeperdark.entity.PrimedDynamite;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class GunpowderTrailBlock extends Block {

    public static final MapCodec<GunpowderTrailBlock> CODEC = simpleCodec(GunpowderTrailBlock::new);

    // Reuse vanilla redstone side properties so blockstate values match vanilla conventions
    public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.NORTH_REDSTONE;
    public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.SOUTH_REDSTONE;
    public static final EnumProperty<RedstoneSide> EAST  = BlockStateProperties.EAST_REDSTONE;
    public static final EnumProperty<RedstoneSide> WEST  = BlockStateProperties.WEST_REDSTONE;

    // True only during the single tick when this block is actively burning —
    // used to briefly emit signal strength 15 so adjacent redstone devices trigger.
    public static final BooleanProperty BURNING          = BooleanProperty.create("burning");
    // Set to true when ignition came from a redstone signal so tick() shows red
    // dust particles instead of flame, and propagates the flag to neighbours.
    public static final BooleanProperty REDSTONE_IGNITED = BooleanProperty.create("redstone_ignited");

    public static final Map<Direction, EnumProperty<RedstoneSide>> PROPERTY_BY_DIRECTION =
            ImmutableMap.copyOf(Maps.newEnumMap(Map.of(
                    Direction.NORTH, NORTH,
                    Direction.EAST,  EAST,
                    Direction.SOUTH, SOUTH,
                    Direction.WEST,  WEST
            )));

    // Flat shape matching redstone dust, includes the narrow column for the center dot
    private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 1, 13);

    @Override
    public MapCodec<GunpowderTrailBlock> codec() {
        return CODEC;
    }

    public GunpowderTrailBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(NORTH, RedstoneSide.NONE)
                .setValue(SOUTH, RedstoneSide.NONE)
                .setValue(EAST,  RedstoneSide.NONE)
                .setValue(WEST,  RedstoneSide.NONE)
                .setValue(BURNING, false)
                .setValue(REDSTONE_IGNITED, false));
    }

    // ─── Shape / Survival ─────────────────────────────────────────────────────

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return belowState.isFaceSturdy(level, below, Direction.UP) || belowState.is(Blocks.HOPPER);
    }

    // ─── Connection Logic ─────────────────────────────────────────────────────

    /**
     * Determine how (if at all) this trail connects in the given horizontal direction.
     * Mirrors vanilla RedStoneWireBlock.getConnectingSide() but only connects to other
     * gunpowder trail blocks (not to redstone signal sources).
     */
    private RedstoneSide getConnectingSide(BlockGetter level, BlockPos pos, Direction dir) {
        boolean canGoUp = !level.getBlockState(pos.above()).isRedstoneConductor(level, pos);
        BlockPos rel = pos.relative(dir);
        BlockState relState = level.getBlockState(rel);

        if (canGoUp) {
            // Solid block ahead with trail on top → connect UP (rising slope)
            boolean solidAhead = relState.isFaceSturdy(level, rel, dir.getOpposite());
            if (solidAhead && level.getBlockState(rel.above()).is(this)) {
                return RedstoneSide.UP;
            }
        }

        // Trail directly at same level
        if (relState.is(this)) return RedstoneSide.SIDE;

        // Trail one level below (descending slope)
        if (!relState.isRedstoneConductor(level, rel)
                && level.getBlockState(rel.below()).is(this)) {
            return RedstoneSide.SIDE;
        }

        return RedstoneSide.NONE;
    }

    private BlockState computeState(BlockGetter level, BlockPos pos) {
        return this.defaultBlockState()
                .setValue(NORTH, getConnectingSide(level, pos, Direction.NORTH))
                .setValue(SOUTH, getConnectingSide(level, pos, Direction.SOUTH))
                .setValue(EAST,  getConnectingSide(level, pos, Direction.EAST))
                .setValue(WEST,  getConnectingSide(level, pos, Direction.WEST));
    }

    // ─── Indirect Neighbour Shape Updates ────────────────────────────────────
    // Vanilla RedStoneWireBlock overrides this so that diagonally-adjacent wires
    // (one step ahead + one step up/down) are notified when this block changes.
    // Without it, a higher trail placed first never learns about a lower trail
    // placed one-step-below-ahead, so its SIDE connection stays NONE and the
    // burn cannot propagate downward.
    @Override
    protected void updateIndirectNeighbourShapes(BlockState state, LevelAccessor level, BlockPos pos,
                                                 int updateFlags, int updateLimit) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            RedstoneSide side = state.getValue(PROPERTY_BY_DIRECTION.get(dir));
            if (side == RedstoneSide.NONE) continue;
            if (level.getBlockState(mutablePos.setWithOffset(pos, dir)).is(this)) continue;

            // Trail one step below-and-ahead
            mutablePos.move(Direction.DOWN);
            if (level.getBlockState(mutablePos).is(this)) {
                BlockPos neighborPos = mutablePos.relative(dir.getOpposite());
                level.neighborShapeChanged(dir.getOpposite(), mutablePos, neighborPos,
                        level.getBlockState(neighborPos), updateFlags, updateLimit);
            }

            // Trail one step above-and-ahead
            mutablePos.setWithOffset(pos, dir).move(Direction.UP);
            if (level.getBlockState(mutablePos).is(this)) {
                BlockPos neighborPos = mutablePos.relative(dir.getOpposite());
                level.neighborShapeChanged(dir.getOpposite(), mutablePos, neighborPos,
                        level.getBlockState(neighborPos), updateFlags, updateLimit);
            }
        }
    }

    // ─── Placement & Shape Updates ────────────────────────────────────────────

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return computeState(ctx.getLevel(), ctx.getClickedPos());
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess,
                                     BlockPos pos, Direction dir, BlockPos neighbourPos,
                                     BlockState neighbourState, RandomSource random) {
        if (dir == Direction.DOWN && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        // Any change to adjacent blocks (above or horizontal) may affect connection geometry
        if (dir != Direction.DOWN) {
            return computeState(level, pos)
                    .setValue(BURNING, state.getValue(BURNING))
                    .setValue(REDSTONE_IGNITED, state.getValue(REDSTONE_IGNITED));
        }
        return state;
    }

    // ─── Redstone Ignition ────────────────────────────────────────────────────

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(state.getBlock()) && !level.isClientSide() && level.hasNeighborSignal(pos)) {
            level.setBlock(pos, state.setValue(REDSTONE_IGNITED, true), Block.UPDATE_CLIENTS);
            level.scheduleTick(pos, this, 2);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block,
                                   @Nullable Orientation orientation, boolean movedByPiston) {
        if (!level.isClientSide() && level.hasNeighborSignal(pos)) {
            // A burning trail emits signal=15 via BURNING=true and calls updateNeighborsAt,
            // which would reach the next trail block in the chain.  That is NOT real redstone —
            // ignore it so the propagating trail keeps its original particle type.
            if (block instanceof GunpowderTrailBlock) return;
            level.setBlock(pos, state.setValue(REDSTONE_IGNITED, true), Block.UPDATE_CLIENTS);
            level.scheduleTick(pos, this, 2);
        }
    }

    // ─── Burn Propagation ─────────────────────────────────────────────────────

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Per-block rain check: if this specific block is exposed to open sky during rain,
        // fizzle it here rather than propagate.  This lets a trail burn underground and
        // only die when it reaches an unprotected surface segment.
        if (isRainFizzle(level, pos)) {
            fizzle(level, pos);
            return;
        }

        boolean redstoneIgnited = state.getValue(REDSTONE_IGNITED);

        // Particles: red dust for redstone-ignited, flame for fire-ignited
        if (redstoneIgnited) {
            int redColor = ARGB.colorFromFloat(1.0F, 1.0F, 0.2F, 0.0F);
            level.sendParticles(new DustParticleOptions(redColor, 1.0F),
                    pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5,
                    8, 0.3, 0.05, 0.3, 0.0);
        } else {
            level.sendParticles(ParticleTypes.FLAME,
                    pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5,
                    6, 0.25, 0.05, 0.25, 0.04);
        }
        level.sendParticles(ParticleTypes.SMOKE,
                pos.getX() + 0.5, pos.getY() + 0.15, pos.getZ() + 0.5,
                3, 0.2, 0.04, 0.2, 0.02);
        level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE,
                SoundSource.BLOCKS, 0.3F, 1.6F + random.nextFloat() * 0.4F);

        // Propagate the burn along every connected arm (2-tick delay per block)
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            RedstoneSide side = state.getValue(PROPERTY_BY_DIRECTION.get(dir));
            if (side == RedstoneSide.NONE) continue;

            BlockPos target;
            if (side == RedstoneSide.UP) {
                target = pos.relative(dir).above();
            } else {
                target = pos.relative(dir);
                if (!level.getBlockState(target).is(this)) {
                    target = target.below();
                }
            }
            if (level.getBlockState(target).is(this)) {
                // Forward the redstone-ignited flag so the whole chain shows the same particle
                if (redstoneIgnited) {
                    BlockState targetState = level.getBlockState(target);
                    level.setBlock(target, targetState.setValue(REDSTONE_IGNITED, true), Block.UPDATE_CLIENTS);
                }
                level.scheduleTick(target, this, 2);
            }
        }

        // Immediately detonate adjacent TNT and Dynamite — no fuse, no primed entity.
        // Blocks are removed first so the BURNING signal below doesn't re-prime them.
        if (level.getGameRules().get(GameRules.TNT_EXPLODES)) {
            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = pos.relative(dir);
                BlockState neighborState = level.getBlockState(neighborPos);
                double cx = neighborPos.getX() + 0.5;
                double cy = neighborPos.getY() + 0.5;
                double cz = neighborPos.getZ() + 0.5;
                if (neighborState.is(Blocks.TNT)) {
                    level.setBlock(neighborPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    level.explode(null, cx, cy, cz, 4.0F, false, Level.ExplosionInteraction.TNT);
                } else if (neighborState.is(ModBlocks.DYNAMITE)) {
                    level.setBlock(neighborPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    PrimedDynamite.explodeAt(level, cx, cy, cz, PrimedDynamite.DEFAULT_EXPLOSION_POWER, null);
                }
            }
        }

        // Brief BURNING signal for all other redstone-activated devices (pistons, doors, etc.)
        level.setBlock(pos, state.setValue(BURNING, true), Block.UPDATE_CLIENTS);
        level.updateNeighborsAt(pos, this);

        // Consume this block
        level.removeBlock(pos, false);
    }

    // ─── Signal Emission (only while BURNING) ─────────────────────────────────

    @Override
    protected boolean isSignalSource(BlockState state) {
        return state.getValue(BURNING);
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
        return state.getValue(BURNING) && dir != Direction.DOWN ? 15 : 0;
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
        return state.getValue(BURNING) && dir == Direction.UP ? 15 : 0;
    }

    // ─── Rain Fizzle ──────────────────────────────────────────────────────────

    // Returns true if this block is currently rained on and exposed to the open
    // sky.  Uses the vanilla isRainingAt check, which internally verifies:
    //   (1) server weather = rain, (2) no opaque blocks above (heightmap), and
    //   (3) canSeeSky — so cave / sheltered trails are never affected.
    private static boolean isRainFizzle(Level level, BlockPos pos) {
        return level.isRainingAt(pos);
    }

    // Visual + audio feedback for a rain-fizzled trail block: brief spark then
    // sizzle-out.  Does NOT propagate to neighbours.
    private static void fizzle(ServerLevel level, BlockPos pos) {
        level.sendParticles(ParticleTypes.FLAME,
                pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5,
                3, 0.15, 0.04, 0.15, 0.02);
        level.sendParticles(ParticleTypes.SMOKE,
                pos.getX() + 0.5, pos.getY() + 0.15, pos.getZ() + 0.5,
                8, 0.2, 0.05, 0.2, 0.02);
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH,
                SoundSource.BLOCKS, 0.8F, 1.4F);
        level.removeBlock(pos, false);
    }

    // ─── Fire-Starter Ignition ────────────────────────────────────────────────

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                          BlockPos pos, Player player, InteractionHand hand,
                                          BlockHitResult hit) {
        if (stack.is(Items.FLINT_AND_STEEL) || stack.is(Items.FIRE_CHARGE)) {
            if (level instanceof ServerLevel sl) {
                if (isRainFizzle(sl, pos)) {
                    fizzle(sl, pos);
                } else {
                    sl.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE,
                            SoundSource.BLOCKS, 1.0F, 1.0F);
                    sl.scheduleTick(pos, this, 2);
                }
                Item item = stack.getItem();
                if (stack.is(Items.FLINT_AND_STEEL)) {
                    stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
                } else {
                    stack.consume(1, player);
                }
                player.awardStat(Stats.ITEM_USED.get(item));
            }
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (level instanceof ServerLevel sl) {
            BlockPos pos = hit.getBlockPos();
            if (projectile.isOnFire() && projectile.mayInteract(sl, pos)) {
                if (isRainFizzle(sl, pos)) {
                    fizzle(sl, pos);
                } else {
                    sl.scheduleTick(pos, this, 2);
                }
            }
        }
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
                                InsideBlockEffectApplier applier, boolean b) {
        if (level instanceof ServerLevel sl && entity.isOnFire()) {
            if (isRainFizzle(sl, pos)) {
                fizzle(sl, pos);
            } else {
                sl.scheduleTick(pos, this, 2);
            }
        }
    }

    // ─── Block State ──────────────────────────────────────────────────────────

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, BURNING, REDSTONE_IGNITED);
    }
}
