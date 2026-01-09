package net.minecraft.entity.data;

public record TrackedData(int id, TrackedDataHandler dataType) {
   public TrackedData(int id, TrackedDataHandler dataType) {
      this.id = id;
      this.dataType = dataType;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         TrackedData trackedData = (TrackedData)o;
         return this.id == trackedData.id;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.id;
   }

   public String toString() {
      return "<entity data: " + this.id + ">";
   }

   public int id() {
      return this.id;
   }

   public TrackedDataHandler dataType() {
      return this.dataType;
   }
}
