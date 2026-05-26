package net.noahsarch.deeperdark.entity;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.random.WeightedList;
import net.noahsarch.deeperdark.sound.ModSounds;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.noahsarch.deeperdark.block.ModBlocks;
import org.jspecify.annotations.Nullable;

public class PrimedDynamite extends Entity implements TraceableEntity {

    private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(PrimedDynamite.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE_ID = SynchedEntityData
            .defineId(PrimedDynamite.class, EntityDataSerializers.BLOCK_STATE);

    private static final short DEFAULT_FUSE_TIME = 80; // 2x vanilla TNT (80)
    private static final float DEFAULT_EXPLOSION_POWER = 24.0F; // 3x vanilla TNT (4.0F)

    // Initialized after ModBlocks is loaded (ModEntities.initialize() runs after
    // ModBlocks.initialize())
    private static BlockState defaultBlockState() {
        return ModBlocks.DYNAMITE.defaultBlockState();
    }

    private static final ExplosionDamageCalculator DYNAMITE_DAMAGE_CALCULATOR = new ExplosionDamageCalculator() {
        @Override
        public boolean shouldBlockExplode(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state,
                float power) {
            return state.is(Blocks.NETHER_PORTAL) ? false
                    : super.shouldBlockExplode(explosion, level, pos, state, power);
        }

        @Override
        public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos,
                BlockState block, FluidState fluid) {
            if (!fluid.isEmpty()) return Optional.of(0.0F);
            return block.is(Blocks.NETHER_PORTAL) ? Optional.empty()
                    : super.getBlockExplosionResistance(explosion, level, pos, block, fluid);
        }
    };

    @Nullable
    private EntityReference<LivingEntity> owner;
    private final float explosionPower = DEFAULT_EXPLOSION_POWER;

    public PrimedDynamite(EntityType<? extends PrimedDynamite> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
    }

    public PrimedDynamite(Level level, double x, double y, double z, @Nullable LivingEntity owner) {
        this(ModEntities.PRIMED_DYNAMITE, level);
        this.setPos(x, y, z);
        double rot = level.getRandom().nextDouble() * (Math.PI * 2);
        this.setDeltaMovement(-Math.sin(rot) * 0.02, 0.2F, -Math.cos(rot) * 0.02);
        this.setFuse(DEFAULT_FUSE_TIME);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.owner = EntityReference.of(owner);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        entityData.define(DATA_FUSE_ID, (int) DEFAULT_FUSE_TIME);
        entityData.define(DATA_BLOCK_STATE_ID, defaultBlockState());
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    public void tick() {
        this.handlePortal();
        this.applyGravity();
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.applyEffectsFromBlocks();
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
        }

        int fuse = this.getFuse() - 1;
        this.setFuse(fuse);
        if (fuse <= 0) {
            this.discard();
            if (!this.level().isClientSide()) {
                this.explode();
            }
        } else {
            this.updateFluidInteraction();
            if (this.level().isClientSide()) {
                this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0,
                        0.0);
            }
        }
    }

    private void explode() {
        if (!(this.level() instanceof ServerLevel level))
            return;
        if (!level.getGameRules().get(GameRules.TNT_EXPLODES))
            return;

        WeightedList<ExplosionParticleInfo> blockParticles = WeightedList.<ExplosionParticleInfo>builder()
                .add(new ExplosionParticleInfo(ParticleTypes.POOF, 0.5F, 1.0F))
                .add(new ExplosionParticleInfo(ParticleTypes.SMOKE, 1.0F, 1.0F))
                .build();
        Holder<SoundEvent> explosionSound =
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(ModSounds.DYNAMITE_EXPLODE);

        level.explode(
                this,
                Explosion.getDefaultDamageSource(level, this),
                DYNAMITE_DAMAGE_CALCULATOR,
                this.getX(),
                this.getY(0.0625),
                this.getZ(),
                this.explosionPower,
                false,
                Level.ExplosionInteraction.TNT,
                ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER,
                blockParticles,
                explosionSound);

        level.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                this.getX(), this.getY() + 0.5, this.getZ(),
                240, 5.0, 1.0, 5.0, 0.08);
        level.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                this.getX(), this.getY() + 3.0, this.getZ(),
                180, 3.5, 1.5, 3.5, 0.12);
        level.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                this.getX(), this.getY() + 6.0, this.getZ(),
                120, 2.0, 2.0, 2.0, 0.15);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putShort("fuse", (short) this.getFuse());
        output.store("block_state", BlockState.CODEC, this.getBlockState());
        EntityReference.store(this.owner, output, "owner");
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.setFuse(input.getShortOr("fuse", DEFAULT_FUSE_TIME));
        this.setBlockState((BlockState) input.read("block_state", BlockState.CODEC).orElse(defaultBlockState()));
        this.owner = EntityReference.read(input, "owner");
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return EntityReference.getLivingEntity(this.owner, this.level());
    }

    @Override
    public void restoreFrom(Entity oldEntity) {
        super.restoreFrom(oldEntity);
        if (oldEntity instanceof PrimedDynamite primedDynamite) {
            this.owner = primedDynamite.owner;
        }
    }

    public void setFuse(int time) {
        this.entityData.set(DATA_FUSE_ID, time);
    }

    public int getFuse() {
        return this.entityData.get(DATA_FUSE_ID);
    }

    public void setBlockState(BlockState blockState) {
        this.entityData.set(DATA_BLOCK_STATE_ID, blockState);
    }

    public BlockState getBlockState() {
        return this.entityData.get(DATA_BLOCK_STATE_ID);
    }

    @Override
    public final boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }
}
