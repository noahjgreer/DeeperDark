package net.minecraft.entity;

import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class OminousItemSpawnerEntity extends Entity {
   private static final int MIN_SPAWN_ITEM_AFTER_TICKS = 60;
   private static final int MAX_SPAWN_ITEM_AFTER_TICKS = 120;
   private static final String SPAWN_ITEM_AFTER_TICKS_NBT_KEY = "spawn_item_after_ticks";
   private static final String ITEM_NBT_KEY = "item";
   private static final TrackedData ITEM;
   public static final int field_50128 = 36;
   private long spawnItemAfterTicks;

   public OminousItemSpawnerEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.noClip = true;
   }

   public static OminousItemSpawnerEntity create(World world, ItemStack stack) {
      OminousItemSpawnerEntity ominousItemSpawnerEntity = new OminousItemSpawnerEntity(EntityType.OMINOUS_ITEM_SPAWNER, world);
      ominousItemSpawnerEntity.spawnItemAfterTicks = (long)world.random.nextBetween(60, 120);
      ominousItemSpawnerEntity.setItem(stack);
      return ominousItemSpawnerEntity;
   }

   public void tick() {
      super.tick();
      World var2 = this.getWorld();
      if (var2 instanceof ServerWorld serverWorld) {
         this.tickServer(serverWorld);
      } else {
         this.tickClient();
      }

   }

   private void tickServer(ServerWorld world) {
      if ((long)this.age == this.spawnItemAfterTicks - 36L) {
         world.playSound((Entity)null, this.getBlockPos(), SoundEvents.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, SoundCategory.NEUTRAL);
      }

      if ((long)this.age >= this.spawnItemAfterTicks) {
         this.spawnItem();
         this.kill(world);
      }

   }

   private void tickClient() {
      if (this.getWorld().getTime() % 5L == 0L) {
         this.addParticles();
      }

   }

   private void spawnItem() {
      World var2 = this.getWorld();
      if (var2 instanceof ServerWorld serverWorld) {
         ItemStack itemStack = this.getItem();
         if (!itemStack.isEmpty()) {
            Item var5 = itemStack.getItem();
            Object entity;
            if (var5 instanceof ProjectileItem) {
               ProjectileItem projectileItem = (ProjectileItem)var5;
               entity = this.spawnProjectile(serverWorld, projectileItem, itemStack);
            } else {
               entity = new ItemEntity(serverWorld, this.getX(), this.getY(), this.getZ(), itemStack);
               serverWorld.spawnEntity((Entity)entity);
            }

            serverWorld.syncWorldEvent(3021, this.getBlockPos(), 1);
            serverWorld.emitGameEvent((Entity)entity, GameEvent.ENTITY_PLACE, this.getPos());
            this.setItem(ItemStack.EMPTY);
         }
      }
   }

   private Entity spawnProjectile(ServerWorld world, ProjectileItem item, ItemStack stack) {
      ProjectileItem.Settings settings = item.getProjectileSettings();
      settings.overrideDispenseEvent().ifPresent((dispenseEvent) -> {
         world.syncWorldEvent(dispenseEvent, this.getBlockPos(), 0);
      });
      Direction direction = Direction.DOWN;
      ProjectileEntity projectileEntity = ProjectileEntity.spawnWithVelocity(item.createEntity(world, this.getPos(), stack, direction), world, stack, (double)direction.getOffsetX(), (double)direction.getOffsetY(), (double)direction.getOffsetZ(), settings.power(), settings.uncertainty());
      projectileEntity.setOwner((Entity)this);
      return projectileEntity;
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      builder.add(ITEM, ItemStack.EMPTY);
   }

   protected void readCustomData(ReadView view) {
      this.setItem((ItemStack)view.read("item", ItemStack.CODEC).orElse(ItemStack.EMPTY));
      this.spawnItemAfterTicks = view.getLong("spawn_item_after_ticks", 0L);
   }

   protected void writeCustomData(WriteView view) {
      if (!this.getItem().isEmpty()) {
         view.put("item", ItemStack.CODEC, this.getItem());
      }

      view.putLong("spawn_item_after_ticks", this.spawnItemAfterTicks);
   }

   protected boolean canAddPassenger(Entity passenger) {
      return false;
   }

   protected boolean couldAcceptPassenger() {
      return false;
   }

   protected void addPassenger(Entity passenger) {
      throw new IllegalStateException("Should never addPassenger without checking couldAcceptPassenger()");
   }

   public PistonBehavior getPistonBehavior() {
      return PistonBehavior.IGNORE;
   }

   public boolean canAvoidTraps() {
      return true;
   }

   public void addParticles() {
      Vec3d vec3d = this.getPos();
      int i = this.random.nextBetween(1, 3);

      for(int j = 0; j < i; ++j) {
         double d = 0.4;
         Vec3d vec3d2 = new Vec3d(this.getX() + 0.4 * (this.random.nextGaussian() - this.random.nextGaussian()), this.getY() + 0.4 * (this.random.nextGaussian() - this.random.nextGaussian()), this.getZ() + 0.4 * (this.random.nextGaussian() - this.random.nextGaussian()));
         Vec3d vec3d3 = vec3d.relativize(vec3d2);
         this.getWorld().addParticleClient(ParticleTypes.OMINOUS_SPAWNING, vec3d.getX(), vec3d.getY(), vec3d.getZ(), vec3d3.getX(), vec3d3.getY(), vec3d3.getZ());
      }

   }

   public ItemStack getItem() {
      return (ItemStack)this.getDataTracker().get(ITEM);
   }

   private void setItem(ItemStack stack) {
      this.getDataTracker().set(ITEM, stack);
   }

   public final boolean damage(ServerWorld world, DamageSource source, float amount) {
      return false;
   }

   static {
      ITEM = DataTracker.registerData(OminousItemSpawnerEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
   }
}
