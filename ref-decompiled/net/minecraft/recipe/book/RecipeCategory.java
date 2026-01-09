package net.minecraft.recipe.book;

public enum RecipeCategory {
   BUILDING_BLOCKS("building_blocks"),
   DECORATIONS("decorations"),
   REDSTONE("redstone"),
   TRANSPORTATION("transportation"),
   TOOLS("tools"),
   COMBAT("combat"),
   FOOD("food"),
   BREWING("brewing"),
   MISC("misc");

   private final String name;

   private RecipeCategory(final String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   // $FF: synthetic method
   private static RecipeCategory[] method_46204() {
      return new RecipeCategory[]{BUILDING_BLOCKS, DECORATIONS, REDSTONE, TRANSPORTATION, TOOLS, COMBAT, FOOD, BREWING, MISC};
   }
}
