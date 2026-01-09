package net.minecraft.entity.decoration;

import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public abstract class AbstractDecorationEntity extends BlockAttachedEntity {
   protected static final Predicate PREDICATE = (entity) -> {
      return entity instanceof AbstractDecorationEntity;
   };
   private static final TrackedData FACING;
   private static final Direction DEFAULT_FACING;

   protected AbstractDecorationEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   protected AbstractDecorationEntity(EntityType type, World world, BlockPos pos) {
      this(type, world);
      this.attachedBlockPos = pos;
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      builder.add(FACING, DEFAULT_FACING);
   }

   public void onTrackedDataSet(TrackedData data) {
      super.onTrackedDataSet(data);
      if (data.equals(FACING)) {
         this.setFacing(this.getHorizontalFacing());
      }

   }

   public Direction getHorizontalFacing() {
      return (Direction)this.dataTracker.get(FACING);
   }

   protected void setFacingInternal(Direction facing) {
      this.dataTracker.set(FACING, facing);
   }

   protected void setFacing(Direction facing) {
      Objects.requireNonNull(facing);
      Validate.isTrue(facing.getAxis().isHorizontal());
      this.setFacingInternal(facing);
      this.setYaw((float)(facing.getHorizontalQuarterTurns() * 90));
      this.lastYaw = this.getYaw();
      this.updateAttachmentPosition();
   }

   protected void updateAttachmentPosition() {
      if (this.getHorizontalFacing() != null) {
         Box box = this.calculateBoundingBox(this.attachedBlockPos, this.getHorizontalFacing());
         Vec3d vec3d = box.getCenter();
         this.setPos(vec3d.x, vec3d.y, vec3d.z);
         this.setBoundingBox(box);
      }
   }

   protected abstract Box calculateBoundingBox(BlockPos pos, Direction side);

   public boolean canStayAttached() {
      if (!this.getWorld().isSpaceEmpty(this)) {
         return false;
      } else {
         boolean bl = BlockPos.stream(this.getAttachmentBox()).allMatch((pos) -> {
            BlockState blockState = this.getWorld().getBlockState(pos);
            return blockState.isSolid() || AbstractRedstoneGateBlock.isRedstoneGate(blockState);
         });
         return !bl ? false : this.getWorld().getOtherEntities(this, this.getBoundingBox(), PREDICATE).isEmpty();
      }
   }

   protected Box getAttachmentBox() {
      return this.getBoundingBox().offset(this.getHorizontalFacing().getUnitVector().mul(-0.5F)).contract(1.0E-7);
   }

   public abstract void onPlace();

   public ItemEntity dropStack(ServerWorld world, ItemStack stack, float yOffset) {
      ItemEntity itemEntity = new ItemEntity(this.getWorld(), this.getX() + (double)((float)this.getHorizontalFacing().getOffsetX() * 0.15F), this.getY() + (double)yOffset, this.getZ() + (double)((float)this.getHorizontalFacing().getOffsetZ() * 0.15F), stack);
      itemEntity.setToDefaultPickupDelay();
      this.getWorld().spawnEntity(itemEntity);
      return itemEntity;
   }

   public float applyRotation(BlockRotation rotation) {
      Direction direction = this.getHorizontalFacing();
      if (direction.getAxis() != Direction.Axis.Y) {
         switch (rotation) {
            case CLOCKWISE_180:
               direction = direction.getOpposite();
               break;
            case COUNTERCLOCKWISE_90:
               direction = direction.rotateYCounterclockwise();
               break;
            case CLOCKWISE_90:
               direction = direction.rotateYClockwise();
         }

         this.setFacing(direction);
      }

      float f = MathHelper.wrapDegrees(this.getYaw());
      float var10000;
      switch (rotation) {
         case CLOCKWISE_180:
            var10000 = f + 180.0F;
            break;
         case COUNTERCLOCKWISE_90:
            var10000 = f + 90.0F;
            break;
         case CLOCKWISE_90:
            var10000 = f + 270.0F;
            break;
         default:
            var10000 = f;
      }

      return var10000;
   }

   public float applyMirror(BlockMirror mirror) {
      return this.applyRotation(mirror.getRotation(this.getHorizontalFacing()));
   }

   static {
      FACING = DataTracker.registerData(AbstractDecorationEntity.class, TrackedDataHandlerRegistry.FACING);
      DEFAULT_FACING = Direction.SOUTH;
   }
}
