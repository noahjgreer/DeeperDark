package net.minecraft.entity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class EntityAttachments {
   private final Map points;

   EntityAttachments(Map points) {
      this.points = points;
   }

   public static EntityAttachments of(float width, float height) {
      return builder().build(width, height);
   }

   public static Builder builder() {
      return new Builder();
   }

   public EntityAttachments scale(float xScale, float yScale, float zScale) {
      return new EntityAttachments(Util.mapEnum(EntityAttachmentType.class, (type) -> {
         List list = new ArrayList();
         Iterator var6 = ((List)this.points.get(type)).iterator();

         while(var6.hasNext()) {
            Vec3d vec3d = (Vec3d)var6.next();
            list.add(vec3d.multiply((double)xScale, (double)yScale, (double)zScale));
         }

         return list;
      }));
   }

   @Nullable
   public Vec3d getPointNullable(EntityAttachmentType type, int index, float yaw) {
      List list = (List)this.points.get(type);
      return index >= 0 && index < list.size() ? rotatePoint((Vec3d)list.get(index), yaw) : null;
   }

   public Vec3d getPoint(EntityAttachmentType type, int index, float yaw) {
      Vec3d vec3d = this.getPointNullable(type, index, yaw);
      if (vec3d == null) {
         String var10002 = String.valueOf(type);
         throw new IllegalStateException("Had no attachment point of type: " + var10002 + " for index: " + index);
      } else {
         return vec3d;
      }
   }

   public Vec3d getPointOrDefault(EntityAttachmentType type) {
      List list = (List)this.points.get(type);
      if (list != null && !list.isEmpty()) {
         Vec3d vec3d = Vec3d.ZERO;

         Vec3d vec3d2;
         for(Iterator var4 = list.iterator(); var4.hasNext(); vec3d = vec3d.add(vec3d2)) {
            vec3d2 = (Vec3d)var4.next();
         }

         return vec3d.multiply((double)(1.0F / (float)list.size()));
      } else {
         throw new IllegalStateException("No attachment points of type: PASSENGER");
      }
   }

   public Vec3d getPointOrDefault(EntityAttachmentType type, int index, float yaw) {
      List list = (List)this.points.get(type);
      if (list.isEmpty()) {
         throw new IllegalStateException("Had no attachment points of type: " + String.valueOf(type));
      } else {
         Vec3d vec3d = (Vec3d)list.get(MathHelper.clamp(index, 0, list.size() - 1));
         return rotatePoint(vec3d, yaw);
      }
   }

   private static Vec3d rotatePoint(Vec3d point, float yaw) {
      return point.rotateY(-yaw * 0.017453292F);
   }

   public static class Builder {
      private final Map points = new EnumMap(EntityAttachmentType.class);

      Builder() {
      }

      public Builder add(EntityAttachmentType type, float x, float y, float z) {
         return this.add(type, new Vec3d((double)x, (double)y, (double)z));
      }

      public Builder add(EntityAttachmentType type, Vec3d point) {
         ((List)this.points.computeIfAbsent(type, (list) -> {
            return new ArrayList(1);
         })).add(point);
         return this;
      }

      public EntityAttachments build(float width, float height) {
         Map map = Util.mapEnum(EntityAttachmentType.class, (type) -> {
            List list = (List)this.points.get(type);
            return list == null ? type.createPoint(width, height) : List.copyOf(list);
         });
         return new EntityAttachments(map);
      }
   }
}
