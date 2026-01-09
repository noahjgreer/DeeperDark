package net.minecraft.entity;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

public class TntEntity extends Entity implements Ownable {
   private static final TrackedData FUSE;
   private static final TrackedData BLOCK_STATE;
   private static final short DEFAULT_FUSE = 80;
   private static final float DEFAULT_EXPLOSION_POWER = 4.0F;
   private static final BlockState DEFAULT_BLOCK_STATE;
   private static final String BLOCK_STATE_NBT_KEY = "block_state";
   public static final String FUSE_NBT_KEY = "fuse";
   private static final String EXPLOSION_POWER_NBT_KEY = "explosion_power";
   private static final ExplosionBehavior TELEPORTED_EXPLOSION_BEHAVIOR;
   @Nullable
   private LazyEntityReference causingEntity;
   private boolean teleported;
   private float explosionPower;

   public TntEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.explosionPower = 4.0F;
      this.intersectionChecked = true;
   }

   public TntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter) {
      this(EntityType.TNT, world);
      this.setPosition(x, y, z);
      double d = world.random.nextDouble() * 6.2831854820251465;
      this.setVelocity(-Math.sin(d) * 0.02, 0.20000000298023224, -Math.cos(d) * 0.02);
      this.setFuse(80);
      this.lastX = x;
      this.lastY = y;
      this.lastZ = z;
      this.causingEntity = igniter != null ? new LazyEntityReference(igniter) : null;
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      builder.add(FUSE, 80);
      builder.add(BLOCK_STATE, DEFAULT_BLOCK_STATE);
   }

   protected Entity.MoveEffect getMoveEffect() {
      return Entity.MoveEffect.NONE;
   }

   public boolean canHit() {
      return !this.isRemoved();
   }

   protected double getGravity() {
      return 0.04;
   }

   public void tick() {
      this.tickPortalTeleportation();
      this.applyGravity();
      this.move(MovementType.SELF, this.getVelocity());
      this.tickBlockCollision();
      this.setVelocity(this.getVelocity().multiply(0.98));
      if (this.isOnGround()) {
         this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
      }

      int i = this.getFuse() - 1;
      this.setFuse(i);
      if (i <= 0) {
         this.discard();
         if (!this.getWorld().isClient) {
            this.explode();
         }
      } else {
         this.updateWaterState();
         if (this.getWorld().isClient) {
            this.getWorld().addParticleClient(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
         }
      }

   }

   private void explode() {
      World var2 = this.getWorld();
      if (var2 instanceof ServerWorld serverWorld) {
         if (serverWorld.getGameRules().getBoolean(GameRules.TNT_EXPLODES)) {
            this.getWorld().createExplosion(this, Explosion.createDamageSource(this.getWorld(), this), this.teleported ? TELEPORTED_EXPLOSION_BEHAVIOR : null, this.getX(), this.getBodyY(0.0625), this.getZ(), this.explosionPower, false, World.ExplosionSourceType.TNT);
         }
      }

   }

   protected void writeCustomData(WriteView view) {
      view.putShort("fuse", (short)this.getFuse());
      view.put("block_state", BlockState.CODEC, this.getBlockState());
      if (this.explosionPower != 4.0F) {
         view.putFloat("explosion_power", this.explosionPower);
      }

      LazyEntityReference.writeData(this.causingEntity, view, "owner");
   }

   protected void readCustomData(ReadView view) {
      this.setFuse(view.getShort("fuse", (short)80));
      this.setBlockState((BlockState)view.read("block_state", BlockState.CODEC).orElse(DEFAULT_BLOCK_STATE));
      this.explosionPower = MathHelper.clamp(view.getFloat("explosion_power", 4.0F), 0.0F, 128.0F);
      this.causingEntity = LazyEntityReference.fromData(view, "owner");
   }

   @Nullable
   public LivingEntity getOwner() {
      return (LivingEntity)LazyEntityReference.resolve(this.causingEntity, this.getWorld(), LivingEntity.class);
   }

   public void copyFrom(Entity original) {
      super.copyFrom(original);
      if (original instanceof TntEntity tntEntity) {
         this.causingEntity = tntEntity.causingEntity;
      }

   }

   public void setFuse(int fuse) {
      this.dataTracker.set(FUSE, fuse);
   }

   public int getFuse() {
      return (Integer)this.dataTracker.get(FUSE);
   }

   public void setBlockState(BlockState state) {
      this.dataTracker.set(BLOCK_STATE, state);
   }

   public BlockState getBlockState() {
      return (BlockState)this.dataTracker.get(BLOCK_STATE);
   }

   private void setTeleported(boolean teleported) {
      this.teleported = teleported;
   }

   @Nullable
   public Entity teleportTo(TeleportTarget teleportTarget) {
      Entity entity = super.teleportTo(teleportTarget);
      if (entity instanceof TntEntity tntEntity) {
         tntEntity.setTeleported(true);
      }

      return entity;
   }

   public final boolean damage(ServerWorld world, DamageSource source, float amount) {
      return false;
   }

   // $FF: synthetic method
   @Nullable
   public Entity getOwner() {
      return this.getOwner();
   }

   static {
      FUSE = DataTracker.registerData(TntEntity.class, TrackedDataHandlerRegistry.INTEGER);
      BLOCK_STATE = DataTracker.registerData(TntEntity.class, TrackedDataHandlerRegistry.BLOCK_STATE);
      DEFAULT_BLOCK_STATE = Blocks.TNT.getDefaultState();
      TELEPORTED_EXPLOSION_BEHAVIOR = new ExplosionBehavior() {
         public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
            return state.isOf(Blocks.NETHER_PORTAL) ? false : super.canDestroyBlock(explosion, world, pos, state, power);
         }

         public Optional getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
            return blockState.isOf(Blocks.NETHER_PORTAL) ? Optional.empty() : super.getBlastResistance(explosion, world, pos, blockState, fluidState);
         }
      };
   }
}
