package net.minecraft.entity.vehicle;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FurnaceMinecartEntity extends AbstractMinecartEntity {
   private static final TrackedData LIT;
   private static final int FUEL_PER_ITEM = 3600;
   private static final int MAX_FUEL = 32000;
   private static final short DEFAULT_FUEL = 0;
   private static final Vec3d DEFAULT_PUSH_VEC;
   private int fuel = 0;
   public Vec3d pushVec;

   public FurnaceMinecartEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.pushVec = DEFAULT_PUSH_VEC;
   }

   public boolean isSelfPropelling() {
      return true;
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(LIT, false);
   }

   public void tick() {
      super.tick();
      if (!this.getWorld().isClient()) {
         if (this.fuel > 0) {
            --this.fuel;
         }

         if (this.fuel <= 0) {
            this.pushVec = Vec3d.ZERO;
         }

         this.setLit(this.fuel > 0);
      }

      if (this.isLit() && this.random.nextInt(4) == 0) {
         this.getWorld().addParticleClient(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.8, this.getZ(), 0.0, 0.0, 0.0);
      }

   }

   protected double getMaxSpeed(ServerWorld world) {
      return this.isTouchingWater() ? super.getMaxSpeed(world) * 0.75 : super.getMaxSpeed(world) * 0.5;
   }

   protected Item asItem() {
      return Items.FURNACE_MINECART;
   }

   public ItemStack getPickBlockStack() {
      return new ItemStack(Items.FURNACE_MINECART);
   }

   protected Vec3d applySlowdown(Vec3d velocity) {
      Vec3d vec3d;
      if (this.pushVec.lengthSquared() > 1.0E-7) {
         this.pushVec = this.method_64276(velocity);
         vec3d = velocity.multiply(0.8, 0.0, 0.8).add(this.pushVec);
         if (this.isTouchingWater()) {
            vec3d = vec3d.multiply(0.1);
         }
      } else {
         vec3d = velocity.multiply(0.98, 0.0, 0.98);
      }

      return super.applySlowdown(vec3d);
   }

   private Vec3d method_64276(Vec3d velocity) {
      double d = 1.0E-4;
      double e = 0.001;
      return this.pushVec.horizontalLengthSquared() > 1.0E-4 && velocity.horizontalLengthSquared() > 0.001 ? this.pushVec.projectOnto(velocity).normalize().multiply(this.pushVec.length()) : this.pushVec;
   }

   public ActionResult interact(PlayerEntity player, Hand hand) {
      ItemStack itemStack = player.getStackInHand(hand);
      if (itemStack.isIn(ItemTags.FURNACE_MINECART_FUEL) && this.fuel + 3600 <= 32000) {
         itemStack.decrementUnlessCreative(1, player);
         this.fuel += 3600;
      }

      if (this.fuel > 0) {
         this.pushVec = this.getPos().subtract(player.getPos()).getHorizontal();
      }

      return ActionResult.SUCCESS;
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putDouble("PushX", this.pushVec.x);
      view.putDouble("PushZ", this.pushVec.z);
      view.putShort("Fuel", (short)this.fuel);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      double d = view.getDouble("PushX", DEFAULT_PUSH_VEC.x);
      double e = view.getDouble("PushZ", DEFAULT_PUSH_VEC.z);
      this.pushVec = new Vec3d(d, 0.0, e);
      this.fuel = view.getShort("Fuel", (short)0);
   }

   protected boolean isLit() {
      return (Boolean)this.dataTracker.get(LIT);
   }

   protected void setLit(boolean lit) {
      this.dataTracker.set(LIT, lit);
   }

   public BlockState getDefaultContainedBlock() {
      return (BlockState)((BlockState)Blocks.FURNACE.getDefaultState().with(FurnaceBlock.FACING, Direction.NORTH)).with(FurnaceBlock.LIT, this.isLit());
   }

   static {
      LIT = DataTracker.registerData(FurnaceMinecartEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
      DEFAULT_PUSH_VEC = Vec3d.ZERO;
   }
}
