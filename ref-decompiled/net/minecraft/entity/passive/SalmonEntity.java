package net.minecraft.entity.passive;

import java.util.function.IntFunction;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.function.ValueLists;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SalmonEntity extends SchoolingFishEntity {
   private static final String TYPE_KEY = "type";
   private static final TrackedData VARIANT;

   public SalmonEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.calculateDimensions();
   }

   public int getMaxGroupSize() {
      return 5;
   }

   public ItemStack getBucketItem() {
      return new ItemStack(Items.SALMON_BUCKET);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SALMON_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SALMON_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_SALMON_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ENTITY_SALMON_FLOP;
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(VARIANT, SalmonEntity.Variant.DEFAULT.getIndex());
   }

   public void onTrackedDataSet(TrackedData data) {
      super.onTrackedDataSet(data);
      if (VARIANT.equals(data)) {
         this.calculateDimensions();
      }

   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.put("type", SalmonEntity.Variant.CODEC, this.getVariant());
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setVariant((Variant)view.read("type", SalmonEntity.Variant.CODEC).orElse(SalmonEntity.Variant.DEFAULT));
   }

   public void copyDataToStack(ItemStack stack) {
      Bucketable.copyDataToStack(this, stack);
      stack.copy(DataComponentTypes.SALMON_SIZE, this);
   }

   private void setVariant(Variant variant) {
      this.dataTracker.set(VARIANT, variant.index);
   }

   public Variant getVariant() {
      return (Variant)SalmonEntity.Variant.FROM_INDEX.apply((Integer)this.dataTracker.get(VARIANT));
   }

   @Nullable
   public Object get(ComponentType type) {
      return type == DataComponentTypes.SALMON_SIZE ? castComponentValue(type, this.getVariant()) : super.get(type);
   }

   protected void copyComponentsFrom(ComponentsAccess from) {
      this.copyComponentFrom(from, DataComponentTypes.SALMON_SIZE);
      super.copyComponentsFrom(from);
   }

   protected boolean setApplicableComponent(ComponentType type, Object value) {
      if (type == DataComponentTypes.SALMON_SIZE) {
         this.setVariant((Variant)castComponentValue(DataComponentTypes.SALMON_SIZE, value));
         return true;
      } else {
         return super.setApplicableComponent(type, value);
      }
   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      Pool.Builder builder = Pool.builder();
      builder.add(SalmonEntity.Variant.SMALL, 30);
      builder.add(SalmonEntity.Variant.MEDIUM, 50);
      builder.add(SalmonEntity.Variant.LARGE, 15);
      builder.build().getOrEmpty(this.random).ifPresent(this::setVariant);
      return super.initialize(world, difficulty, spawnReason, entityData);
   }

   public float getVariantScale() {
      return this.getVariant().scale;
   }

   protected EntityDimensions getBaseDimensions(EntityPose pose) {
      return super.getBaseDimensions(pose).scaled(this.getVariantScale());
   }

   static {
      VARIANT = DataTracker.registerData(SalmonEntity.class, TrackedDataHandlerRegistry.INTEGER);
   }

   public static enum Variant implements StringIdentifiable {
      SMALL("small", 0, 0.5F),
      MEDIUM("medium", 1, 1.0F),
      LARGE("large", 2, 1.5F);

      public static final Variant DEFAULT = MEDIUM;
      public static final StringIdentifiable.EnumCodec CODEC = StringIdentifiable.createCodec(Variant::values);
      static final IntFunction FROM_INDEX = ValueLists.createIndexToValueFunction(Variant::getIndex, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.CLAMP);
      public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(FROM_INDEX, Variant::getIndex);
      private final String id;
      final int index;
      final float scale;

      private Variant(final String id, final int index, final float scale) {
         this.id = id;
         this.index = index;
         this.scale = scale;
      }

      public String asString() {
         return this.id;
      }

      int getIndex() {
         return this.index;
      }

      // $FF: synthetic method
      private static Variant[] method_61473() {
         return new Variant[]{SMALL, MEDIUM, LARGE};
      }
   }
}
