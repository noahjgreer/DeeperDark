package net.noahsarch.deeperdark.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class QuicksandBlock extends Block {

    public static final MapCodec<QuicksandBlock> CODEC = simpleCodec(QuicksandBlock::new);

    private static final float HORIZONTAL_PARTICLE_MOMENTUM_FACTOR = 0.083333336F;
    private static final float IN_BLOCK_HORIZONTAL_SPEED_MULTIPLIER = 0.9F;
    private static final float IN_BLOCK_VERTICAL_SPEED_MULTIPLIER = 1.5F;
    private static final float NUM_BLOCKS_TO_FALL_INTO_BLOCK = 2.5F;
    private static final VoxelShape FALLING_COLLISION_SHAPE = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.9, 1.0);

    public QuicksandBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<QuicksandBlock> codec() {
        return CODEC;
    }

    /** True for mobs tagged as powder-snow-walkable, or any LivingEntity wearing boots. */
    public static boolean canWalkOnQuicksand(Entity entity) {
        if (entity.is(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)) return true;
        return entity instanceof LivingEntity le && !le.getItemBySlot(EquipmentSlot.FEET).isEmpty();
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState neighborState, Direction direction) {
        return neighborState.is(this) || super.skipRendering(state, neighborState, direction);
    }

    @Override
    protected void entityInside(
            BlockState state, Level level, BlockPos pos, Entity entity,
            InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        // Apply viscosity to any entity physically inside the block, including boot wearers.
        // getInBlockState() check prevents this from firing while standing on the surface.
        if (!(entity instanceof LivingEntity) || entity.getInBlockState().is(this)) {
            entity.makeStuckInBlock(state, new Vec3(
                    IN_BLOCK_HORIZONTAL_SPEED_MULTIPLIER,
                    IN_BLOCK_VERTICAL_SPEED_MULTIPLIER,
                    IN_BLOCK_HORIZONTAL_SPEED_MULTIPLIER));
            if (level.isClientSide()) {
                RandomSource random = level.getRandom();
                boolean isMoving = entity.xOld != entity.getX() || entity.zOld != entity.getZ();
                if (isMoving && random.nextBoolean()) {
                    level.addParticle(
                            new BlockParticleOption(ParticleTypes.BLOCK, state),
                            entity.getX(),
                            pos.getY() + 1,
                            entity.getZ(),
                            Mth.randomBetween(random, -1.0F, 1.0F) * HORIZONTAL_PARTICLE_MOMENTUM_FACTOR,
                            0.05F,
                            Mth.randomBetween(random, -1.0F, 1.0F) * HORIZONTAL_PARTICLE_MOMENTUM_FACTOR);
                }
            }
        }
        // Suffocation is based purely on head position — boots prevent sinking, not drowning.
        // We bypass effectApplier.apply(FREEZE) here because canFreeze() returns false for entities
        // wearing freeze_immune_wearables (e.g. leather boots), which would silently skip the freeze
        // tick increment. We want quicksand to suffocate regardless of worn items.
        BlockPos eyePos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
        if (level.getBlockState(eyePos).is(this)) {
            entity.setIsInPowderSnow(true);
            entity.setTicksFrozen(Math.min(entity.getTicksRequiredToFreeze(), entity.getTicksFrozen() + 1));
        }
        effectApplier.apply(InsideBlockEffectType.EXTINGUISH);
    }

    @Override
    protected VoxelShape getEntityInsideCollisionShape(
            BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        VoxelShape collisionShape = this.getCollisionShape(state, level, pos, CollisionContext.of(entity));
        return collisionShape.isEmpty() ? Shapes.block() : collisionShape;
    }

    @Override
    protected VoxelShape getCollisionShape(
            BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!context.isPlacement() && context instanceof EntityCollisionContext entityCtx) {
            Entity entity = entityCtx.getEntity();
            if (entity != null) {
                // Boots of any kind let the entity walk on the surface — bypass descending/fall checks.
                // Must return Shapes.block() directly; super delegates to BlockBehaviour which returns
                // Shapes.empty() because the block was registered with noCollision().
                if (canWalkOnQuicksand(entity) && context.isAbove(Shapes.block(), pos, false)) {
                    return Shapes.block();
                }
                if (entity.fallDistance > NUM_BLOCKS_TO_FALL_INTO_BLOCK) {
                    return FALLING_COLLISION_SHAPE;
                }
                if (entity instanceof FallingBlockEntity) {
                    return Shapes.block();
                }
            }
        }
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getVisualShape(
            BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return true;
    }
}
