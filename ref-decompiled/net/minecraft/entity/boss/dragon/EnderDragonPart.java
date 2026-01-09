package net.minecraft.entity.boss.dragon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.jetbrains.annotations.Nullable;

public class EnderDragonPart extends Entity {
   public final EnderDragonEntity owner;
   public final String name;
   private final EntityDimensions partDimensions;

   public EnderDragonPart(EnderDragonEntity owner, String name, float width, float height) {
      super(owner.getType(), owner.getWorld());
      this.partDimensions = EntityDimensions.changing(width, height);
      this.calculateDimensions();
      this.owner = owner;
      this.name = name;
   }

   protected void initDataTracker(DataTracker.Builder builder) {
   }

   protected void readCustomData(ReadView view) {
   }

   protected void writeCustomData(WriteView view) {
   }

   public boolean canHit() {
      return true;
   }

   @Nullable
   public ItemStack getPickBlockStack() {
      return this.owner.getPickBlockStack();
   }

   public final boolean damage(ServerWorld world, DamageSource source, float amount) {
      return this.isAlwaysInvulnerableTo(source) ? false : this.owner.damagePart(world, this, source, amount);
   }

   public boolean isPartOf(Entity entity) {
      return this == entity || this.owner == entity;
   }

   public Packet createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
      throw new UnsupportedOperationException();
   }

   public EntityDimensions getDimensions(EntityPose pose) {
      return this.partDimensions;
   }

   public boolean shouldSave() {
      return false;
   }
}
