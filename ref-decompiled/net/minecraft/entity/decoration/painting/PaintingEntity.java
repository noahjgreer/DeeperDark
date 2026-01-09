package net.minecraft.entity.decoration.painting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Variants;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.PaintingVariantTags;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PaintingEntity extends AbstractDecorationEntity {
   private static final TrackedData VARIANT;
   public static final float field_51595 = 0.0625F;

   public PaintingEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(VARIANT, Variants.getDefaultOrThrow(this.getRegistryManager(), RegistryKeys.PAINTING_VARIANT));
   }

   public void onTrackedDataSet(TrackedData data) {
      super.onTrackedDataSet(data);
      if (VARIANT.equals(data)) {
         this.updateAttachmentPosition();
      }

   }

   private void setVariant(RegistryEntry variant) {
      this.dataTracker.set(VARIANT, variant);
   }

   public RegistryEntry getVariant() {
      return (RegistryEntry)this.dataTracker.get(VARIANT);
   }

   @Nullable
   public Object get(ComponentType type) {
      return type == DataComponentTypes.PAINTING_VARIANT ? castComponentValue(type, this.getVariant()) : super.get(type);
   }

   protected void copyComponentsFrom(ComponentsAccess from) {
      this.copyComponentFrom(from, DataComponentTypes.PAINTING_VARIANT);
      super.copyComponentsFrom(from);
   }

   protected boolean setApplicableComponent(ComponentType type, Object value) {
      if (type == DataComponentTypes.PAINTING_VARIANT) {
         this.setVariant((RegistryEntry)castComponentValue(DataComponentTypes.PAINTING_VARIANT, value));
         return true;
      } else {
         return super.setApplicableComponent(type, value);
      }
   }

   public static Optional placePainting(World world, BlockPos pos, Direction facing) {
      PaintingEntity paintingEntity = new PaintingEntity(world, pos);
      List list = new ArrayList();
      Iterable var10000 = world.getRegistryManager().getOrThrow(RegistryKeys.PAINTING_VARIANT).iterateEntries(PaintingVariantTags.PLACEABLE);
      Objects.requireNonNull(list);
      var10000.forEach(list::add);
      if (list.isEmpty()) {
         return Optional.empty();
      } else {
         paintingEntity.setFacing(facing);
         list.removeIf((variant) -> {
            paintingEntity.setVariant(variant);
            return !paintingEntity.canStayAttached();
         });
         if (list.isEmpty()) {
            return Optional.empty();
         } else {
            int i = list.stream().mapToInt(PaintingEntity::getSize).max().orElse(0);
            list.removeIf((variant) -> {
               return getSize(variant) < i;
            });
            Optional optional = Util.getRandomOrEmpty(list, paintingEntity.random);
            if (optional.isEmpty()) {
               return Optional.empty();
            } else {
               paintingEntity.setVariant((RegistryEntry)optional.get());
               paintingEntity.setFacing(facing);
               return Optional.of(paintingEntity);
            }
         }
      }
   }

   private static int getSize(RegistryEntry variant) {
      return ((PaintingVariant)variant.value()).getArea();
   }

   private PaintingEntity(World world, BlockPos pos) {
      super(EntityType.PAINTING, world, pos);
   }

   public PaintingEntity(World world, BlockPos pos, Direction direction, RegistryEntry variant) {
      this(world, pos);
      this.setVariant(variant);
      this.setFacing(direction);
   }

   protected void writeCustomData(WriteView view) {
      view.put("facing", Direction.HORIZONTAL_QUARTER_TURNS_CODEC, this.getHorizontalFacing());
      super.writeCustomData(view);
      Variants.writeVariantToNbt(view, this.getVariant());
   }

   protected void readCustomData(ReadView view) {
      Direction direction = (Direction)view.read("facing", Direction.HORIZONTAL_QUARTER_TURNS_CODEC).orElse(Direction.SOUTH);
      super.readCustomData(view);
      this.setFacing(direction);
      Variants.readVariantFromNbt(view, RegistryKeys.PAINTING_VARIANT).ifPresent(this::setVariant);
   }

   protected Box calculateBoundingBox(BlockPos pos, Direction side) {
      float f = 0.46875F;
      Vec3d vec3d = Vec3d.ofCenter(pos).offset(side, -0.46875);
      PaintingVariant paintingVariant = (PaintingVariant)this.getVariant().value();
      double d = this.getOffset(paintingVariant.width());
      double e = this.getOffset(paintingVariant.height());
      Direction direction = side.rotateYCounterclockwise();
      Vec3d vec3d2 = vec3d.offset(direction, d).offset(Direction.UP, e);
      Direction.Axis axis = side.getAxis();
      double g = axis == Direction.Axis.X ? 0.0625 : (double)paintingVariant.width();
      double h = (double)paintingVariant.height();
      double i = axis == Direction.Axis.Z ? 0.0625 : (double)paintingVariant.width();
      return Box.of(vec3d2, g, h, i);
   }

   private double getOffset(int length) {
      return length % 2 == 0 ? 0.5 : 0.0;
   }

   public void onBreak(ServerWorld world, @Nullable Entity breaker) {
      if (world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
         this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);
         if (breaker instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)breaker;
            if (playerEntity.isInCreativeMode()) {
               return;
            }
         }

         this.dropItem(world, Items.PAINTING);
      }
   }

   public void onPlace() {
      this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
   }

   public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
      this.setPosition(x, y, z);
   }

   public Vec3d getSyncedPos() {
      return Vec3d.of(this.attachedBlockPos);
   }

   public Packet createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
      return new EntitySpawnS2CPacket(this, this.getHorizontalFacing().getIndex(), this.getAttachedBlockPos());
   }

   public void onSpawnPacket(EntitySpawnS2CPacket packet) {
      super.onSpawnPacket(packet);
      this.setFacing(Direction.byIndex(packet.getEntityData()));
   }

   public ItemStack getPickBlockStack() {
      return new ItemStack(Items.PAINTING);
   }

   static {
      VARIANT = DataTracker.registerData(PaintingEntity.class, TrackedDataHandlerRegistry.PAINTING_VARIANT);
   }
}
