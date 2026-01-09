package net.minecraft.entity;

import com.mojang.logging.LogUtils;
import java.util.function.Predicate;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.block.Falling;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class FallingBlockEntity extends Entity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final BlockState DEFAULT_BLOCK_STATE;
   private static final int DEFAULT_TIME = 0;
   private static final float DEFAULT_FALL_HURT_AMOUNT = 0.0F;
   private static final int DEFAULT_FALL_HURT_MAX = 40;
   private static final boolean DEFAULT_DROP_ITEM = true;
   private static final boolean DEFAULT_DESTROYED_ON_LANDING = false;
   private BlockState blockState;
   public int timeFalling;
   public boolean dropItem;
   private boolean destroyedOnLanding;
   private boolean hurtEntities;
   private int fallHurtMax;
   private float fallHurtAmount;
   @Nullable
   public NbtCompound blockEntityData;
   public boolean shouldDupe;
   protected static final TrackedData BLOCK_POS;

   public FallingBlockEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.blockState = DEFAULT_BLOCK_STATE;
      this.timeFalling = 0;
      this.dropItem = true;
      this.destroyedOnLanding = false;
      this.fallHurtMax = 40;
      this.fallHurtAmount = 0.0F;
   }

   private FallingBlockEntity(World world, double x, double y, double z, BlockState blockState) {
      this(EntityType.FALLING_BLOCK, world);
      this.blockState = blockState;
      this.intersectionChecked = true;
      this.setPosition(x, y, z);
      this.setVelocity(Vec3d.ZERO);
      this.lastX = x;
      this.lastY = y;
      this.lastZ = z;
      this.setFallingBlockPos(this.getBlockPos());
   }

   public static FallingBlockEntity spawnFromBlock(World world, BlockPos pos, BlockState state) {
      FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(world, (double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, state.contains(Properties.WATERLOGGED) ? (BlockState)state.with(Properties.WATERLOGGED, false) : state);
      world.setBlockState(pos, state.getFluidState().getBlockState(), 3);
      world.spawnEntity(fallingBlockEntity);
      return fallingBlockEntity;
   }

   public boolean isAttackable() {
      return false;
   }

   public final boolean damage(ServerWorld world, DamageSource source, float amount) {
      if (!this.isAlwaysInvulnerableTo(source)) {
         this.scheduleVelocityUpdate();
      }

      return false;
   }

   public void setFallingBlockPos(BlockPos pos) {
      this.dataTracker.set(BLOCK_POS, pos);
   }

   public BlockPos getFallingBlockPos() {
      return (BlockPos)this.dataTracker.get(BLOCK_POS);
   }

   protected Entity.MoveEffect getMoveEffect() {
      return Entity.MoveEffect.NONE;
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      builder.add(BLOCK_POS, BlockPos.ORIGIN);
   }

   public boolean canHit() {
      return !this.isRemoved();
   }

   protected double getGravity() {
      return 0.04;
   }

   public void tick() {
      if (this.blockState.isAir()) {
         this.discard();
      } else {
         Block block = this.blockState.getBlock();
         ++this.timeFalling;
         this.applyGravity();
         this.move(MovementType.SELF, this.getVelocity());
         this.tickBlockCollision();
         this.tickPortalTeleportation();
         World var3 = this.getWorld();
         if (var3 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var3;
            if (this.isAlive() || this.shouldDupe) {
               BlockPos blockPos = this.getBlockPos();
               boolean bl = this.blockState.getBlock() instanceof ConcretePowderBlock;
               boolean bl2 = bl && this.getWorld().getFluidState(blockPos).isIn(FluidTags.WATER);
               double d = this.getVelocity().lengthSquared();
               if (bl && d > 1.0) {
                  BlockHitResult blockHitResult = this.getWorld().raycast(new RaycastContext(new Vec3d(this.lastX, this.lastY, this.lastZ), this.getPos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.SOURCE_ONLY, this));
                  if (blockHitResult.getType() != HitResult.Type.MISS && this.getWorld().getFluidState(blockHitResult.getBlockPos()).isIn(FluidTags.WATER)) {
                     blockPos = blockHitResult.getBlockPos();
                     bl2 = true;
                  }
               }

               if (!this.isOnGround() && !bl2) {
                  if (this.timeFalling > 100 && (blockPos.getY() <= this.getWorld().getBottomY() || blockPos.getY() > this.getWorld().getTopYInclusive()) || this.timeFalling > 600) {
                     if (this.dropItem && serverWorld.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                        this.dropItem(serverWorld, block);
                     }

                     this.discard();
                  }
               } else {
                  BlockState blockState = this.getWorld().getBlockState(blockPos);
                  this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
                  if (!blockState.isOf(Blocks.MOVING_PISTON)) {
                     if (!this.destroyedOnLanding) {
                        boolean bl3 = blockState.canReplace(new AutomaticItemPlacementContext(this.getWorld(), blockPos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                        boolean bl4 = FallingBlock.canFallThrough(this.getWorld().getBlockState(blockPos.down())) && (!bl || !bl2);
                        boolean bl5 = this.blockState.canPlaceAt(this.getWorld(), blockPos) && !bl4;
                        if (bl3 && bl5) {
                           if (this.blockState.contains(Properties.WATERLOGGED) && this.getWorld().getFluidState(blockPos).getFluid() == Fluids.WATER) {
                              this.blockState = (BlockState)this.blockState.with(Properties.WATERLOGGED, true);
                           }

                           if (this.getWorld().setBlockState(blockPos, this.blockState, 3)) {
                              ((ServerWorld)this.getWorld()).getChunkManager().chunkLoadingManager.sendToOtherNearbyPlayers(this, new BlockUpdateS2CPacket(blockPos, this.getWorld().getBlockState(blockPos)));
                              this.discard();
                              if (block instanceof Falling) {
                                 ((Falling)block).onLanding(this.getWorld(), blockPos, this.blockState, blockState, this);
                              }

                              if (this.blockEntityData != null && this.blockState.hasBlockEntity()) {
                                 BlockEntity blockEntity = this.getWorld().getBlockEntity(blockPos);
                                 if (blockEntity != null) {
                                    try {
                                       ErrorReporter.Logging logging = new ErrorReporter.Logging(blockEntity.getReporterContext(), LOGGER);

                                       try {
                                          DynamicRegistryManager dynamicRegistryManager = this.getWorld().getRegistryManager();
                                          NbtWriteView nbtWriteView = NbtWriteView.create(logging, dynamicRegistryManager);
                                          blockEntity.writeDataWithoutId(nbtWriteView);
                                          NbtCompound nbtCompound = nbtWriteView.getNbt();
                                          this.blockEntityData.forEach((string, nbtElement) -> {
                                             nbtCompound.put(string, nbtElement.copy());
                                          });
                                          blockEntity.read(NbtReadView.create(logging, dynamicRegistryManager, nbtCompound));
                                       } catch (Throwable var18) {
                                          try {
                                             logging.close();
                                          } catch (Throwable var17) {
                                             var18.addSuppressed(var17);
                                          }

                                          throw var18;
                                       }

                                       logging.close();
                                    } catch (Exception var19) {
                                       LOGGER.error("Failed to load block entity from falling block", var19);
                                    }

                                    blockEntity.markDirty();
                                 }
                              }
                           } else if (this.dropItem && serverWorld.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                              this.discard();
                              this.onDestroyedOnLanding(block, blockPos);
                              this.dropItem(serverWorld, block);
                           }
                        } else {
                           this.discard();
                           if (this.dropItem && serverWorld.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                              this.onDestroyedOnLanding(block, blockPos);
                              this.dropItem(serverWorld, block);
                           }
                        }
                     } else {
                        this.discard();
                        this.onDestroyedOnLanding(block, blockPos);
                     }
                  }
               }
            }
         }

         this.setVelocity(this.getVelocity().multiply(0.98));
      }
   }

   public void onDestroyedOnLanding(Block block, BlockPos pos) {
      if (block instanceof Falling) {
         ((Falling)block).onDestroyedOnLanding(this.getWorld(), pos, this);
      }

   }

   public boolean handleFallDamage(double fallDistance, float damagePerDistance, DamageSource damageSource) {
      if (!this.hurtEntities) {
         return false;
      } else {
         int i = MathHelper.ceil(fallDistance - 1.0);
         if (i < 0) {
            return false;
         } else {
            Predicate predicate = EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.and(EntityPredicates.VALID_LIVING_ENTITY);
            Block var9 = this.blockState.getBlock();
            DamageSource var10000;
            if (var9 instanceof Falling) {
               Falling falling = (Falling)var9;
               var10000 = falling.getDamageSource(this);
            } else {
               var10000 = this.getDamageSources().fallingBlock(this);
            }

            DamageSource damageSource2 = var10000;
            float f = (float)Math.min(MathHelper.floor((float)i * this.fallHurtAmount), this.fallHurtMax);
            this.getWorld().getOtherEntities(this, this.getBoundingBox(), predicate).forEach((entity) -> {
               entity.serverDamage(damageSource2, f);
            });
            boolean bl = this.blockState.isIn(BlockTags.ANVIL);
            if (bl && f > 0.0F && this.random.nextFloat() < 0.05F + (float)i * 0.05F) {
               BlockState blockState = AnvilBlock.getLandingState(this.blockState);
               if (blockState == null) {
                  this.destroyedOnLanding = true;
               } else {
                  this.blockState = blockState;
               }
            }

            return false;
         }
      }
   }

   protected void writeCustomData(WriteView view) {
      view.put("BlockState", BlockState.CODEC, this.blockState);
      view.putInt("Time", this.timeFalling);
      view.putBoolean("DropItem", this.dropItem);
      view.putBoolean("HurtEntities", this.hurtEntities);
      view.putFloat("FallHurtAmount", this.fallHurtAmount);
      view.putInt("FallHurtMax", this.fallHurtMax);
      if (this.blockEntityData != null) {
         view.put("TileEntityData", NbtCompound.CODEC, this.blockEntityData);
      }

      view.putBoolean("CancelDrop", this.destroyedOnLanding);
   }

   protected void readCustomData(ReadView view) {
      this.blockState = (BlockState)view.read("BlockState", BlockState.CODEC).orElse(DEFAULT_BLOCK_STATE);
      this.timeFalling = view.getInt("Time", 0);
      boolean bl = this.blockState.isIn(BlockTags.ANVIL);
      this.hurtEntities = view.getBoolean("HurtEntities", bl);
      this.fallHurtAmount = view.getFloat("FallHurtAmount", 0.0F);
      this.fallHurtMax = view.getInt("FallHurtMax", 40);
      this.dropItem = view.getBoolean("DropItem", true);
      this.blockEntityData = (NbtCompound)view.read("TileEntityData", NbtCompound.CODEC).orElse((Object)null);
      this.destroyedOnLanding = view.getBoolean("CancelDrop", false);
   }

   public void setHurtEntities(float fallHurtAmount, int fallHurtMax) {
      this.hurtEntities = true;
      this.fallHurtAmount = fallHurtAmount;
      this.fallHurtMax = fallHurtMax;
   }

   public void setDestroyedOnLanding() {
      this.destroyedOnLanding = true;
   }

   public boolean doesRenderOnFire() {
      return false;
   }

   public void populateCrashReport(CrashReportSection section) {
      super.populateCrashReport(section);
      section.add("Immitating BlockState", (Object)this.blockState.toString());
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   protected Text getDefaultName() {
      return Text.translatable("entity.minecraft.falling_block_type", this.blockState.getBlock().getName());
   }

   public Packet createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
      return new EntitySpawnS2CPacket(this, entityTrackerEntry, Block.getRawIdFromState(this.getBlockState()));
   }

   public void onSpawnPacket(EntitySpawnS2CPacket packet) {
      super.onSpawnPacket(packet);
      this.blockState = Block.getStateFromRawId(packet.getEntityData());
      this.intersectionChecked = true;
      double d = packet.getX();
      double e = packet.getY();
      double f = packet.getZ();
      this.setPosition(d, e, f);
      this.setFallingBlockPos(this.getBlockPos());
   }

   @Nullable
   public Entity teleportTo(TeleportTarget teleportTarget) {
      RegistryKey registryKey = teleportTarget.world().getRegistryKey();
      RegistryKey registryKey2 = this.getWorld().getRegistryKey();
      boolean bl = (registryKey2 == World.END || registryKey == World.END) && registryKey2 != registryKey;
      Entity entity = super.teleportTo(teleportTarget);
      this.shouldDupe = entity != null && bl;
      return entity;
   }

   static {
      DEFAULT_BLOCK_STATE = Blocks.SAND.getDefaultState();
      BLOCK_POS = DataTracker.registerData(FallingBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
   }
}
