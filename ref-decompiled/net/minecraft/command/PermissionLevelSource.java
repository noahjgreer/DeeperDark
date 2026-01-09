package net.minecraft.command;

public interface PermissionLevelSource {
   boolean hasPermissionLevel(int level);

   default boolean hasElevatedPermissions() {
      return this.hasPermissionLevel(2);
   }

   public static record PermissionLevelSourcePredicate(int requiredLevel) implements PermissionLevelPredicate {
      public PermissionLevelSourcePredicate(int i) {
         this.requiredLevel = i;
      }

      public boolean test(PermissionLevelSource permissionLevelSource) {
         return permissionLevelSource.hasPermissionLevel(this.requiredLevel);
      }

      public int requiredLevel() {
         return this.requiredLevel;
      }

      // $FF: synthetic method
      public boolean test(final Object permissionLevelSource) {
         return this.test((PermissionLevelSource)permissionLevelSource);
      }
   }
}
