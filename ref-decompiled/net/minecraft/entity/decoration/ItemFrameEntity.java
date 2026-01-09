package net.minecraft.entity.decoration;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

public class ItemFrameEntity extends AbstractDecorationEntity {
   private static final TrackedData ITEM_STACK;
   private static final TrackedData ROTATION;
   public static final int field_30454 = 8;
   private static final float field_51592 = 0.0625F;
   private static final float field_51593 = 0.75F;
   private static final float field_51594 = 0.75F;
   private static final byte DEFAULT_ITEM_ROTATION = 0;
   private static final float DEFAULT_ITEM_DROP_CHANCE = 1.0F;
   private static final boolean DEFAULT_INVISIBLE = false;
   private static final boolean DEFAULT_FIXED = false;
   private float itemDropChance;
   private boolean fixed;

   public ItemFrameEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.itemDropChance = 1.0F;
      this.fixed = false;
      this.setInvisible(false);
   }

   public ItemFrameEntity(World world, BlockPos pos, Direction facing) {
      this(EntityType.ITEM_FRAME, world, pos, facing);
   }

   public ItemFrameEntity(EntityType type, World world, BlockPos pos, Direction facing) {
      super(type, world, pos);
      this.itemDropChance = 1.0F;
      this.fixed = false;
      this.setFacing(facing);
      this.setInvisible(false);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(ITEM_STACK, ItemStack.EMPTY);
      builder.add(ROTATION, 0);
   }

   protected void setFacing(Direction facing) {
      Validate.notNull(facing);
      super.setFacingInternal(facing);
      if (facing.getAxis().isHorizontal()) {
         this.setPitch(0.0F);
         this.setYaw((float)(facing.getHorizontalQuarterTurns() * 90));
      } else {
         this.setPitch((float)(-90 * facing.getDirection().offset()));
         this.setYaw(0.0F);
      }

      this.lastPitch = this.getPitch();
      this.lastYaw = this.getYaw();
      this.updateAttachmentPosition();
   }

   protected final void updateAttachmentPosition() {
      super.updateAttachmentPosition();
      this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
   }

   protected Box calculateBoundingBox(BlockPos pos, Direction side) {
      float f = 0.46875F;
      Vec3d vec3d = Vec3d.ofCenter(pos).offset(side, -0.46875);
      Direction.Axis axis = side.getAxis();
      double d = axis == Direction.Axis.X ? 0.0625 : 0.75;
      double e = axis == Direction.Axis.Y ? 0.0625 : 0.75;
      double g = axis == Direction.Axis.Z ? 0.0625 : 0.75;
      return Box.of(vec3d, d, e, g);
   }

   public boolean canStayAttached() {
      if (this.fixed) {
         return true;
      } else if (!this.getWorld().isSpaceEmpty(this)) {
         return false;
      } else {
         BlockState blockState = this.getWorld().getBlockState(this.attachedBlockPos.offset(this.getHorizontalFacing().getOpposite()));
         return blockState.isSolid() || this.getHorizontalFacing().getAxis().isHorizontal() && AbstractRedstoneGateBlock.isRedstoneGate(blockState) ? this.getWorld().getOtherEntities(this, this.getBoundingBox(), PREDICATE).isEmpty() : false;
      }
   }

   public void move(MovementType type, Vec3d movement) {
      if (!this.fixed) {
         super.move(type, movement);
      }

   }

   public void addVelocity(double deltaX, double deltaY, double deltaZ) {
      if (!this.fixed) {
         super.addVelocity(deltaX, deltaY, deltaZ);
      }

   }

   public void kill(ServerWorld world) {
      this.removeFromFrame(this.getHeldItemStack());
      super.kill(world);
   }

   private boolean shouldDropHeldStackWhenDamaged(DamageSource damageSource) {
      return !damageSource.isIn(DamageTypeTags.IS_EXPLOSION) && !this.getHeldItemStack().isEmpty();
   }

   private static boolean canDamageWhenFixed(DamageSource damageSource) {
      return damageSource.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) || damageSource.isSourceCreativePlayer();
   }

   public boolean clientDamage(DamageSource source) {
      if (this.fixed && !canDamageWhenFixed(source)) {
         return false;
      } else {
         return !this.isAlwaysInvulnerableTo(source);
      }
   }

   public boolean damage(ServerWorld world, DamageSource source, float amount) {
      if (!this.fixed) {
         if (this.isAlwaysInvulnerableTo(source)) {
            return false;
         } else if (this.shouldDropHeldStackWhenDamaged(source)) {
            this.dropHeldStack(world, source.getAttacker(), false);
            this.emitGameEvent(GameEvent.BLOCK_CHANGE, source.getAttacker());
            this.playSound(this.getRemoveItemSound(), 1.0F, 1.0F);
            return true;
         } else {
            return super.damage(world, source, amount);
         }
      } else {
         return canDamageWhenFixed(source) && super.damage(world, source, amount);
      }
   }

   public SoundEvent getRemoveItemSound() {
      return SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM;
   }

   public boolean shouldRender(double distance) {
      double d = 16.0;
      d *= 64.0 * getRenderDistanceMultiplier();
      return distance < d * d;
   }

   public void onBreak(ServerWorld world, @Nullable Entity breaker) {
      this.playSound(this.getBreakSound(), 1.0F, 1.0F);
      this.dropHeldStack(world, breaker, true);
      this.emitGameEvent(GameEvent.BLOCK_CHANGE, breaker);
   }

   public SoundEvent getBreakSound() {
      return SoundEvents.ENTITY_ITEM_FRAME_BREAK;
   }

   public void onPlace() {
      this.playSound(this.getPlaceSound(), 1.0F, 1.0F);
   }

   public SoundEvent getPlaceSound() {
      return SoundEvents.ENTITY_ITEM_FRAME_PLACE;
   }

   private void dropHeldStack(ServerWorld world, @Nullable Entity entity, boolean dropSelf) {
      if (!this.fixed) {
         ItemStack itemStack = this.getHeldItemStack();
         this.setHeldItemStack(ItemStack.EMPTY);
         if (!world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            if (entity == null) {
               this.removeFromFrame(itemStack);
            }

         } else {
            if (entity instanceof PlayerEntity) {
               PlayerEntity playerEntity = (PlayerEntity)entity;
               if (playerEntity.isInCreativeMode()) {
                  this.removeFromFrame(itemStack);
                  return;
               }
            }

            if (dropSelf) {
               this.dropStack(world, this.getAsItemStack());
            }

            if (!itemStack.isEmpty()) {
               itemStack = itemStack.copy();
               this.removeFromFrame(itemStack);
               if (this.random.nextFloat() < this.itemDropChance) {
                  this.dropStack(world, itemStack);
               }
            }

         }
      }
   }

   private void removeFromFrame(ItemStack stack) {
      MapIdComponent mapIdComponent = this.getMapId(stack);
      if (mapIdComponent != null) {
         MapState mapState = FilledMapItem.getMapState(mapIdComponent, this.getWorld());
         if (mapState != null) {
            mapState.removeFrame(this.attachedBlockPos, this.getId());
         }
      }

      stack.setHolder((Entity)null);
   }

   public ItemStack getHeldItemStack() {
      return (ItemStack)this.getDataTracker().get(ITEM_STACK);
   }

   @Nullable
   public MapIdComponent getMapId(ItemStack stack) {
      return (MapIdComponent)stack.get(DataComponentTypes.MAP_ID);
   }

   public boolean containsMap() {
      return this.getHeldItemStack().contains(DataComponentTypes.MAP_ID);
   }

   public void setHeldItemStack(ItemStack stack) {
      this.setHeldItemStack(stack, true);
   }

   public void setHeldItemStack(ItemStack value, boolean update) {
      if (!value.isEmpty()) {
         value = value.copyWithCount(1);
      }

      this.setAsStackHolder(value);
      this.getDataTracker().set(ITEM_STACK, value);
      if (!value.isEmpty()) {
         this.playSound(this.getAddItemSound(), 1.0F, 1.0F);
      }

      if (update && this.attachedBlockPos != null) {
         this.getWorld().updateComparators(this.attachedBlockPos, Blocks.AIR);
      }

   }

   public SoundEvent getAddItemSound() {
      return SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM;
   }

   public StackReference getStackReference(int mappedIndex) {
      return mappedIndex == 0 ? StackReference.of(this::getHeldItemStack, this::setHeldItemStack) : super.getStackReference(mappedIndex);
   }

   public void onTrackedDataSet(TrackedData data) {
      super.onTrackedDataSet(data);
      if (data.equals(ITEM_STACK)) {
         this.setAsStackHolder(this.getHeldItemStack());
      }

   }

   private void setAsStackHolder(ItemStack stack) {
      if (!stack.isEmpty() && stack.getFrame() != this) {
         stack.setHolder(this);
      }

      this.updateAttachmentPosition();
   }

   public int getRotation() {
      return (Integer)this.getDataTracker().get(ROTATION);
   }

   public void setRotation(int value) {
      this.setRotation(value, true);
   }

   private void setRotation(int value, boolean updateComparators) {
      this.getDataTracker().set(ROTATION, value % 8);
      if (updateComparators && this.attachedBlockPos != null) {
         this.getWorld().updateComparators(this.attachedBlockPos, Blocks.AIR);
      }

   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      ItemStack itemStack = this.getHeldItemStack();
      if (!itemStack.isEmpty()) {
         view.put("Item", ItemStack.CODEC, itemStack);
      }

      view.putByte("ItemRotation", (byte)this.getRotation());
      view.putFloat("ItemDropChance", this.itemDropChance);
      view.put("Facing", Direction.INDEX_CODEC, this.getHorizontalFacing());
      view.putBoolean("Invisible", this.isInvisible());
      view.putBoolean("Fixed", this.fixed);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      ItemStack itemStack = (ItemStack)view.read("Item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
      ItemStack itemStack2 = this.getHeldItemStack();
      if (!itemStack2.isEmpty() && !ItemStack.areEqual(itemStack, itemStack2)) {
         this.removeFromFrame(itemStack2);
      }

      this.setHeldItemStack(itemStack, false);
      this.setRotation(view.getByte("ItemRotation", (byte)0), false);
      this.itemDropChance = view.getFloat("ItemDropChance", 1.0F);
      this.setFacing((Direction)view.read("Facing", Direction.INDEX_CODEC).orElse(Direction.DOWN));
      this.setInvisible(view.getBoolean("Invisible", false));
      this.fixed = view.getBoolean("Fixed", false);
   }

   public ActionResult interact(PlayerEntity player, Hand hand) {
      ItemStack itemStack = player.getStackInHand(hand);
      boolean bl = !this.getHeldItemStack().isEmpty();
      boolean bl2 = !itemStack.isEmpty();
      if (this.fixed) {
         return ActionResult.PASS;
      } else if (!player.getWorld().isClient) {
         if (!bl) {
            if (bl2 && !this.isRemoved()) {
               MapState mapState = FilledMapItem.getMapState(itemStack, this.getWorld());
               if (mapState != null && mapState.decorationCountNotLessThan(256)) {
                  return ActionResult.FAIL;
               } else {
                  this.setHeldItemStack(itemStack);
                  this.emitGameEvent(GameEvent.BLOCK_CHANGE, player);
                  itemStack.decrementUnlessCreative(1, player);
                  return ActionResult.SUCCESS;
               }
            } else {
               return ActionResult.PASS;
            }
         } else {
            this.playSound(this.getRotateItemSound(), 1.0F, 1.0F);
            this.setRotation(this.getRotation() + 1);
            this.emitGameEvent(GameEvent.BLOCK_CHANGE, player);
            return ActionResult.SUCCESS;
         }
      } else {
         return (ActionResult)(!bl && !bl2 ? ActionResult.PASS : ActionResult.SUCCESS);
      }
   }

   public SoundEvent getRotateItemSound() {
      return SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM;
   }

   public int getComparatorPower() {
      return this.getHeldItemStack().isEmpty() ? 0 : this.getRotation() % 8 + 1;
   }

   public Packet createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
      return new EntitySpawnS2CPacket(this, this.getHorizontalFacing().getIndex(), this.getAttachedBlockPos());
   }

   public void onSpawnPacket(EntitySpawnS2CPacket packet) {
      super.onSpawnPacket(packet);
      this.setFacing(Direction.byIndex(packet.getEntityData()));
   }

   public ItemStack getPickBlockStack() {
      ItemStack itemStack = this.getHeldItemStack();
      return itemStack.isEmpty() ? this.getAsItemStack() : itemStack.copy();
   }

   protected ItemStack getAsItemStack() {
      return new ItemStack(Items.ITEM_FRAME);
   }

   public float getBodyYaw() {
      Direction direction = this.getHorizontalFacing();
      int i = direction.getAxis().isVertical() ? 90 * direction.getDirection().offset() : 0;
      return (float)MathHelper.wrapDegrees(180 + direction.getHorizontalQuarterTurns() * 90 + this.getRotation() * 45 + i);
   }

   static {
      ITEM_STACK = DataTracker.registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
      ROTATION = DataTracker.registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.INTEGER);
   }
}
