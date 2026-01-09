package net.minecraft.entity;

import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;

public class MarkerEntity extends Entity {
   public MarkerEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.noClip = true;
   }

   public void tick() {
   }

   protected void initDataTracker(DataTracker.Builder builder) {
   }

   protected void readCustomData(ReadView view) {
   }

   protected void writeCustomData(WriteView view) {
   }

   public Packet createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
      throw new IllegalStateException("Markers should never be sent");
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

   public final boolean damage(ServerWorld world, DamageSource source, float amount) {
      return false;
   }
}
