package net.minecraft.entity.projectile.thrown;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;

public abstract class ThrownItemEntity extends ThrownEntity implements FlyingItemEntity {
   private static final TrackedData ITEM;

   public ThrownItemEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   public ThrownItemEntity(EntityType type, double x, double y, double z, World world, ItemStack stack) {
      super(type, x, y, z, world);
      this.setItem(stack);
   }

   public ThrownItemEntity(EntityType type, LivingEntity owner, World world, ItemStack stack) {
      this(type, owner.getX(), owner.getEyeY() - 0.10000000149011612, owner.getZ(), world, stack);
      this.setOwner(owner);
   }

   public void setItem(ItemStack stack) {
      this.getDataTracker().set(ITEM, stack.copyWithCount(1));
   }

   protected abstract Item getDefaultItem();

   public ItemStack getStack() {
      return (ItemStack)this.getDataTracker().get(ITEM);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      builder.add(ITEM, new ItemStack(this.getDefaultItem()));
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.put("Item", ItemStack.CODEC, this.getStack());
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setItem((ItemStack)view.read("Item", ItemStack.CODEC).orElseGet(() -> {
         return new ItemStack(this.getDefaultItem());
      }));
   }

   static {
      ITEM = DataTracker.registerData(ThrownItemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
   }
}
