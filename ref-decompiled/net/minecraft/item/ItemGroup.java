package net.minecraft.item;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ItemGroup {
   static final Identifier ITEMS = getTabTextureId("items");
   private final Text displayName;
   Identifier texture;
   boolean scrollbar;
   boolean renderName;
   boolean special;
   private final Row row;
   private final int column;
   private final Type type;
   @Nullable
   private ItemStack icon;
   private Collection displayStacks;
   private Set searchTabStacks;
   private final Supplier iconSupplier;
   private final EntryCollector entryCollector;

   ItemGroup(Row row, int column, Type type, Text displayName, Supplier iconSupplier, EntryCollector entryCollector) {
      this.texture = ITEMS;
      this.scrollbar = true;
      this.renderName = true;
      this.special = false;
      this.displayStacks = ItemStackSet.create();
      this.searchTabStacks = ItemStackSet.create();
      this.row = row;
      this.column = column;
      this.displayName = displayName;
      this.iconSupplier = iconSupplier;
      this.entryCollector = entryCollector;
      this.type = type;
   }

   public static Identifier getTabTextureId(String name) {
      return Identifier.ofVanilla("textures/gui/container/creative_inventory/tab_" + name + ".png");
   }

   public static Builder create(Row location, int column) {
      return new Builder(location, column);
   }

   public Text getDisplayName() {
      return this.displayName;
   }

   public ItemStack getIcon() {
      if (this.icon == null) {
         this.icon = (ItemStack)this.iconSupplier.get();
      }

      return this.icon;
   }

   public Identifier getTexture() {
      return this.texture;
   }

   public boolean shouldRenderName() {
      return this.renderName;
   }

   public boolean hasScrollbar() {
      return this.scrollbar;
   }

   public int getColumn() {
      return this.column;
   }

   public Row getRow() {
      return this.row;
   }

   public boolean hasStacks() {
      return !this.displayStacks.isEmpty();
   }

   public boolean shouldDisplay() {
      return this.type != ItemGroup.Type.CATEGORY || this.hasStacks();
   }

   public boolean isSpecial() {
      return this.special;
   }

   public Type getType() {
      return this.type;
   }

   public void updateEntries(DisplayContext displayContext) {
      EntriesImpl entriesImpl = new EntriesImpl(this, displayContext.enabledFeatures);
      RegistryKey var10000 = (RegistryKey)Registries.ITEM_GROUP.getKey(this).orElseThrow(() -> {
         return new IllegalStateException("Unregistered creative tab: " + String.valueOf(this));
      });
      this.entryCollector.accept(displayContext, entriesImpl);
      this.displayStacks = entriesImpl.parentTabStacks;
      this.searchTabStacks = entriesImpl.searchTabStacks;
   }

   public Collection getDisplayStacks() {
      return this.displayStacks;
   }

   public Collection getSearchTabStacks() {
      return this.searchTabStacks;
   }

   public boolean contains(ItemStack stack) {
      return this.searchTabStacks.contains(stack);
   }

   public static enum Row {
      TOP,
      BOTTOM;

      // $FF: synthetic method
      private static Row[] method_47326() {
         return new Row[]{TOP, BOTTOM};
      }
   }

   @FunctionalInterface
   public interface EntryCollector {
      void accept(DisplayContext displayContext, Entries entries);
   }

   public static enum Type {
      CATEGORY,
      INVENTORY,
      HOTBAR,
      SEARCH;

      // $FF: synthetic method
      private static Type[] method_47327() {
         return new Type[]{CATEGORY, INVENTORY, HOTBAR, SEARCH};
      }
   }

   public static class Builder {
      private static final EntryCollector EMPTY_ENTRIES = (displayContext, entries) -> {
      };
      private final Row row;
      private final int column;
      private Text displayName = Text.empty();
      private Supplier iconSupplier = () -> {
         return ItemStack.EMPTY;
      };
      private EntryCollector entryCollector;
      private boolean scrollbar;
      private boolean renderName;
      private boolean special;
      private Type type;
      private Identifier texture;

      public Builder(Row row, int column) {
         this.entryCollector = EMPTY_ENTRIES;
         this.scrollbar = true;
         this.renderName = true;
         this.special = false;
         this.type = ItemGroup.Type.CATEGORY;
         this.texture = ItemGroup.ITEMS;
         this.row = row;
         this.column = column;
      }

      public Builder displayName(Text displayName) {
         this.displayName = displayName;
         return this;
      }

      public Builder icon(Supplier iconSupplier) {
         this.iconSupplier = iconSupplier;
         return this;
      }

      public Builder entries(EntryCollector entryCollector) {
         this.entryCollector = entryCollector;
         return this;
      }

      public Builder special() {
         this.special = true;
         return this;
      }

      public Builder noRenderedName() {
         this.renderName = false;
         return this;
      }

      public Builder noScrollbar() {
         this.scrollbar = false;
         return this;
      }

      protected Builder type(Type type) {
         this.type = type;
         return this;
      }

      public Builder texture(Identifier texture) {
         this.texture = texture;
         return this;
      }

      public ItemGroup build() {
         if ((this.type == ItemGroup.Type.HOTBAR || this.type == ItemGroup.Type.INVENTORY) && this.entryCollector != EMPTY_ENTRIES) {
            throw new IllegalStateException("Special tabs can't have display items");
         } else {
            ItemGroup itemGroup = new ItemGroup(this.row, this.column, this.type, this.displayName, this.iconSupplier, this.entryCollector);
            itemGroup.special = this.special;
            itemGroup.renderName = this.renderName;
            itemGroup.scrollbar = this.scrollbar;
            itemGroup.texture = this.texture;
            return itemGroup;
         }
      }
   }

   static class EntriesImpl implements Entries {
      public final Collection parentTabStacks = ItemStackSet.create();
      public final Set searchTabStacks = ItemStackSet.create();
      private final ItemGroup group;
      private final FeatureSet enabledFeatures;

      public EntriesImpl(ItemGroup group, FeatureSet enabledFeatures) {
         this.group = group;
         this.enabledFeatures = enabledFeatures;
      }

      public void add(ItemStack stack, StackVisibility visibility) {
         if (stack.getCount() != 1) {
            throw new IllegalArgumentException("Stack size must be exactly 1");
         } else {
            boolean bl = this.parentTabStacks.contains(stack) && visibility != ItemGroup.StackVisibility.SEARCH_TAB_ONLY;
            if (bl) {
               String var10002 = stack.toHoverableText().getString();
               throw new IllegalStateException("Accidentally adding the same item stack twice " + var10002 + " to a Creative Mode Tab: " + this.group.getDisplayName().getString());
            } else {
               if (stack.getItem().isEnabled(this.enabledFeatures)) {
                  switch (visibility.ordinal()) {
                     case 0:
                        this.parentTabStacks.add(stack);
                        this.searchTabStacks.add(stack);
                        break;
                     case 1:
                        this.parentTabStacks.add(stack);
                        break;
                     case 2:
                        this.searchTabStacks.add(stack);
                  }
               }

            }
         }
      }
   }

   public static record DisplayContext(FeatureSet enabledFeatures, boolean hasPermissions, RegistryWrapper.WrapperLookup lookup) {
      final FeatureSet enabledFeatures;

      public DisplayContext(FeatureSet featureSet, boolean bl, RegistryWrapper.WrapperLookup wrapperLookup) {
         this.enabledFeatures = featureSet;
         this.hasPermissions = bl;
         this.lookup = wrapperLookup;
      }

      public boolean doesNotMatch(FeatureSet enabledFeatures, boolean hasPermissions, RegistryWrapper.WrapperLookup registries) {
         return !this.enabledFeatures.equals(enabledFeatures) || this.hasPermissions != hasPermissions || this.lookup != registries;
      }

      public FeatureSet enabledFeatures() {
         return this.enabledFeatures;
      }

      public boolean hasPermissions() {
         return this.hasPermissions;
      }

      public RegistryWrapper.WrapperLookup lookup() {
         return this.lookup;
      }
   }

   public interface Entries {
      void add(ItemStack stack, StackVisibility visibility);

      default void add(ItemStack stack) {
         this.add(stack, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
      }

      default void add(ItemConvertible item, StackVisibility visibility) {
         this.add(new ItemStack(item), visibility);
      }

      default void add(ItemConvertible item) {
         this.add(new ItemStack(item), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
      }

      default void addAll(Collection stacks, StackVisibility visibility) {
         stacks.forEach((stack) -> {
            this.add(stack, visibility);
         });
      }

      default void addAll(Collection stacks) {
         this.addAll(stacks, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
      }
   }

   public static enum StackVisibility {
      PARENT_AND_SEARCH_TABS,
      PARENT_TAB_ONLY,
      SEARCH_TAB_ONLY;

      // $FF: synthetic method
      private static StackVisibility[] method_45425() {
         return new StackVisibility[]{PARENT_AND_SEARCH_TABS, PARENT_TAB_ONLY, SEARCH_TAB_ONLY};
      }
   }
}
