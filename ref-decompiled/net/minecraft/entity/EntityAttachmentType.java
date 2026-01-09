package net.minecraft.entity;

import java.util.List;
import net.minecraft.util.math.Vec3d;

public enum EntityAttachmentType {
   PASSENGER(EntityAttachmentType.Point.AT_HEIGHT),
   VEHICLE(EntityAttachmentType.Point.ZERO),
   NAME_TAG(EntityAttachmentType.Point.AT_HEIGHT),
   WARDEN_CHEST(EntityAttachmentType.Point.WARDEN_CHEST);

   private final Point point;

   private EntityAttachmentType(final Point point) {
      this.point = point;
   }

   public List createPoint(float width, float height) {
      return this.point.create(width, height);
   }

   // $FF: synthetic method
   private static EntityAttachmentType[] method_55669() {
      return new EntityAttachmentType[]{PASSENGER, VEHICLE, NAME_TAG, WARDEN_CHEST};
   }

   public interface Point {
      List NONE = List.of(Vec3d.ZERO);
      Point ZERO = (width, height) -> {
         return NONE;
      };
      Point AT_HEIGHT = (width, height) -> {
         return List.of(new Vec3d(0.0, (double)height, 0.0));
      };
      Point WARDEN_CHEST = (width, height) -> {
         return List.of(new Vec3d(0.0, (double)height / 2.0, 0.0));
      };

      List create(float width, float height);
   }
}
