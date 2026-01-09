package net.minecraft.client.render.entity.state;

import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class EntityRenderState {
   public EntityType entityType;
   public double x;
   public double y;
   public double z;
   public float age;
   public float width;
   public float height;
   public float standingEyeHeight;
   public double squaredDistanceToCamera;
   public boolean invisible;
   public boolean sneaking;
   public boolean onFire;
   @Nullable
   public Vec3d positionOffset;
   @Nullable
   public Text displayName;
   @Nullable
   public Vec3d nameLabelPos;
   @Nullable
   public List leashDatas;
   @Nullable
   public EntityHitboxAndView hitbox;
   @Nullable
   public EntityDebugInfo debugInfo;

   public void addCrashReportDetails(CrashReportSection crashReportSection) {
      crashReportSection.add("EntityRenderState", (Object)this.getClass().getCanonicalName());
      crashReportSection.add("Entity's Exact location", (Object)String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.x, this.y, this.z));
   }

   @Environment(EnvType.CLIENT)
   public static class LeashData {
      public Vec3d offset;
      public Vec3d startPos;
      public Vec3d endPos;
      public int leashedEntityBlockLight;
      public int leashHolderBlockLight;
      public int leashedEntitySkyLight;
      public int leashHolderSkyLight;
      public boolean field_60161;

      public LeashData() {
         this.offset = Vec3d.ZERO;
         this.startPos = Vec3d.ZERO;
         this.endPos = Vec3d.ZERO;
         this.leashedEntityBlockLight = 0;
         this.leashHolderBlockLight = 0;
         this.leashedEntitySkyLight = 15;
         this.leashHolderSkyLight = 15;
         this.field_60161 = true;
      }
   }
}
