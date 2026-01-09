package net.minecraft.entity.conversion;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

public record EntityConversionContext(EntityConversionType type, boolean keepEquipment, boolean preserveCanPickUpLoot, @Nullable Team team) {
   public EntityConversionContext(EntityConversionType entityConversionType, boolean bl, boolean bl2, @Nullable Team team) {
      this.type = entityConversionType;
      this.keepEquipment = bl;
      this.preserveCanPickUpLoot = bl2;
      this.team = team;
   }

   public static EntityConversionContext create(MobEntity entity, boolean keepEquipment, boolean preserveCanPickUpLoot) {
      return new EntityConversionContext(EntityConversionType.SINGLE, keepEquipment, preserveCanPickUpLoot, entity.getScoreboardTeam());
   }

   public EntityConversionType type() {
      return this.type;
   }

   public boolean keepEquipment() {
      return this.keepEquipment;
   }

   public boolean preserveCanPickUpLoot() {
      return this.preserveCanPickUpLoot;
   }

   @Nullable
   public Team team() {
      return this.team;
   }

   @FunctionalInterface
   public interface Finalizer {
      void finalizeConversion(MobEntity convertedEntity);
   }
}
