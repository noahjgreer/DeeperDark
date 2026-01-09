package net.minecraft;

import java.util.Date;
import net.minecraft.resource.ResourceType;

public interface GameVersion {
   SaveVersion dataVersion();

   String id();

   String name();

   int protocolVersion();

   int packVersion(ResourceType type);

   Date buildTime();

   boolean stable();

   public static record Impl(String id, String name, SaveVersion dataVersion, int protocolVersion, int resourcePackVersion, int datapackVersion, Date buildTime, boolean stable) implements GameVersion {
      public Impl(String string, String string2, SaveVersion saveVersion, int i, int j, int k, Date date, boolean bl) {
         this.id = string;
         this.name = string2;
         this.dataVersion = saveVersion;
         this.protocolVersion = i;
         this.resourcePackVersion = j;
         this.datapackVersion = k;
         this.buildTime = date;
         this.stable = bl;
      }

      public int packVersion(ResourceType type) {
         int var10000;
         switch (type) {
            case CLIENT_RESOURCES:
               var10000 = this.resourcePackVersion;
               break;
            case SERVER_DATA:
               var10000 = this.datapackVersion;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public String id() {
         return this.id;
      }

      public String name() {
         return this.name;
      }

      public SaveVersion dataVersion() {
         return this.dataVersion;
      }

      public int protocolVersion() {
         return this.protocolVersion;
      }

      public int resourcePackVersion() {
         return this.resourcePackVersion;
      }

      public int datapackVersion() {
         return this.datapackVersion;
      }

      public Date buildTime() {
         return this.buildTime;
      }

      public boolean stable() {
         return this.stable;
      }
   }
}
