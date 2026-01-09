package net.minecraft.entity.passive;

import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.Variants;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CowEntity extends AbstractCowEntity {
   private static final TrackedData VARIANT;

   public CowEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(VARIANT, Variants.getOrDefaultOrThrow(this.getRegistryManager(), CowVariants.TEMPERATE));
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      Variants.writeVariantToNbt(view, this.getVariant());
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      Variants.readVariantFromNbt(view, RegistryKeys.COW_VARIANT).ifPresent(this::setVariant);
   }

   @Nullable
   public CowEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
      CowEntity cowEntity = (CowEntity)EntityType.COW.create(serverWorld, SpawnReason.BREEDING);
      if (cowEntity != null && passiveEntity instanceof CowEntity cowEntity2) {
         cowEntity.setVariant(this.random.nextBoolean() ? this.getVariant() : cowEntity2.getVariant());
      }

      return cowEntity;
   }

   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      Variants.select(SpawnContext.of(world, this.getBlockPos()), RegistryKeys.COW_VARIANT).ifPresent(this::setVariant);
      return super.initialize(world, difficulty, spawnReason, entityData);
   }

   public void setVariant(RegistryEntry variant) {
      this.dataTracker.set(VARIANT, variant);
   }

   public RegistryEntry getVariant() {
      return (RegistryEntry)this.dataTracker.get(VARIANT);
   }

   @Nullable
   public Object get(ComponentType type) {
      return type == DataComponentTypes.COW_VARIANT ? castComponentValue(type, this.getVariant()) : super.get(type);
   }

   protected void copyComponentsFrom(ComponentsAccess from) {
      this.copyComponentFrom(from, DataComponentTypes.COW_VARIANT);
      super.copyComponentsFrom(from);
   }

   protected boolean setApplicableComponent(ComponentType type, Object value) {
      if (type == DataComponentTypes.COW_VARIANT) {
         this.setVariant((RegistryEntry)castComponentValue(DataComponentTypes.COW_VARIANT, value));
         return true;
      } else {
         return super.setApplicableComponent(type, value);
      }
   }

   // $FF: synthetic method
   @Nullable
   public PassiveEntity createChild(final ServerWorld world, final PassiveEntity entity) {
      return this.createChild(world, entity);
   }

   static {
      VARIANT = DataTracker.registerData(CowEntity.class, TrackedDataHandlerRegistry.COW_VARIANT);
   }
}
