package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.NavigationAxis;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookType;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.RecipeCategoryOptionsC2SPacket;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public abstract class RecipeBookWidget implements Drawable, Element, Selectable {
   public static final ButtonTextures BUTTON_TEXTURES = new ButtonTextures(Identifier.ofVanilla("recipe_book/button"), Identifier.ofVanilla("recipe_book/button_highlighted"));
   protected static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/recipe_book.png");
   private static final int field_52839 = 256;
   private static final int field_52840 = 256;
   private static final Text SEARCH_HINT_TEXT;
   public static final int field_32408 = 147;
   public static final int field_32409 = 166;
   private static final int field_32410 = 86;
   private static final int field_54389 = 8;
   private static final Text TOGGLE_ALL_RECIPES_TEXT;
   private static final int field_52841 = 30;
   private int leftOffset;
   private int parentWidth;
   private int parentHeight;
   private float displayTime;
   @Nullable
   private NetworkRecipeId selectedRecipeId;
   private final GhostRecipe ghostRecipe;
   private final List tabButtons = Lists.newArrayList();
   @Nullable
   private RecipeGroupButtonWidget currentTab;
   protected ToggleButtonWidget toggleCraftableButton;
   protected final AbstractRecipeScreenHandler craftingScreenHandler;
   protected MinecraftClient client;
   @Nullable
   private TextFieldWidget searchField;
   private String searchText = "";
   private final List tabs;
   private ClientRecipeBook recipeBook;
   private final RecipeBookResults recipesArea;
   @Nullable
   private NetworkRecipeId selectedRecipe;
   @Nullable
   private RecipeResultCollection selectedRecipeResults;
   private final RecipeFinder recipeFinder = new RecipeFinder();
   private int cachedInvChangeCount;
   private boolean searching;
   private boolean open;
   private boolean narrow;
   @Nullable
   private ScreenRect searchFieldRect;

   public RecipeBookWidget(AbstractRecipeScreenHandler craftingScreenHandler, List tabs) {
      this.craftingScreenHandler = craftingScreenHandler;
      this.tabs = tabs;
      CurrentIndexProvider currentIndexProvider = () -> {
         return MathHelper.floor(this.displayTime / 30.0F);
      };
      this.ghostRecipe = new GhostRecipe(currentIndexProvider);
      this.recipesArea = new RecipeBookResults(this, currentIndexProvider, craftingScreenHandler instanceof AbstractFurnaceScreenHandler);
   }

   public void initialize(int parentWidth, int parentHeight, MinecraftClient client, boolean narrow) {
      this.client = client;
      this.parentWidth = parentWidth;
      this.parentHeight = parentHeight;
      this.narrow = narrow;
      this.recipeBook = client.player.getRecipeBook();
      this.cachedInvChangeCount = client.player.getInventory().getChangeCount();
      this.open = this.isGuiOpen();
      if (this.open) {
         this.reset();
      }

   }

   private void reset() {
      boolean bl = this.isFilteringCraftable();
      this.leftOffset = this.narrow ? 0 : 86;
      int i = this.getLeft();
      int j = this.getTop();
      this.recipeFinder.clear();
      this.client.player.getInventory().populateRecipeFinder(this.recipeFinder);
      this.craftingScreenHandler.populateRecipeFinder(this.recipeFinder);
      String string = this.searchField != null ? this.searchField.getText() : "";
      TextRenderer var10003 = this.client.textRenderer;
      int var10004 = i + 25;
      int var10005 = j + 13;
      Objects.requireNonNull(this.client.textRenderer);
      this.searchField = new TextFieldWidget(var10003, var10004, var10005, 81, 9 + 5, Text.translatable("itemGroup.search"));
      this.searchField.setMaxLength(50);
      this.searchField.setVisible(true);
      this.searchField.setEditableColor(-1);
      this.searchField.setText(string);
      this.searchField.setPlaceholder(SEARCH_HINT_TEXT);
      this.searchFieldRect = ScreenRect.of(NavigationAxis.HORIZONTAL, i + 8, this.searchField.getY(), this.searchField.getX() - this.getLeft(), this.searchField.getHeight());
      this.recipesArea.initialize(this.client, i, j);
      this.toggleCraftableButton = new ToggleButtonWidget(i + 110, j + 12, 26, 16, bl);
      this.updateTooltip();
      this.setBookButtonTexture();
      this.tabButtons.clear();
      Iterator var5 = this.tabs.iterator();

      while(var5.hasNext()) {
         Tab tab = (Tab)var5.next();
         this.tabButtons.add(new RecipeGroupButtonWidget(tab));
      }

      if (this.currentTab != null) {
         this.currentTab = (RecipeGroupButtonWidget)this.tabButtons.stream().filter((button) -> {
            return button.getCategory().equals(this.currentTab.getCategory());
         }).findFirst().orElse((Object)null);
      }

      if (this.currentTab == null) {
         this.currentTab = (RecipeGroupButtonWidget)this.tabButtons.get(0);
      }

      this.currentTab.setToggled(true);
      this.populateAllRecipes();
      this.refreshTabButtons(bl);
      this.refreshResults(false, bl);
   }

   private int getTop() {
      return (this.parentHeight - 166) / 2;
   }

   private int getLeft() {
      return (this.parentWidth - 147) / 2 - this.leftOffset;
   }

   private void updateTooltip() {
      this.toggleCraftableButton.setTooltip(this.toggleCraftableButton.isToggled() ? Tooltip.of(this.getToggleCraftableButtonText()) : Tooltip.of(TOGGLE_ALL_RECIPES_TEXT));
   }

   protected abstract void setBookButtonTexture();

   public int findLeftEdge(int width, int backgroundWidth) {
      int i;
      if (this.isOpen() && !this.narrow) {
         i = 177 + (width - backgroundWidth - 200) / 2;
      } else {
         i = (width - backgroundWidth) / 2;
      }

      return i;
   }

   public void toggleOpen() {
      this.setOpen(!this.isOpen());
   }

   public boolean isOpen() {
      return this.open;
   }

   private boolean isGuiOpen() {
      return this.recipeBook.isGuiOpen(this.craftingScreenHandler.getCategory());
   }

   protected void setOpen(boolean opened) {
      if (opened) {
         this.reset();
      }

      this.open = opened;
      this.recipeBook.setGuiOpen(this.craftingScreenHandler.getCategory(), opened);
      if (!opened) {
         this.recipesArea.hideAlternates();
      }

      this.sendBookDataPacket();
   }

   protected abstract boolean isValid(Slot slot);

   public void onMouseClick(@Nullable Slot slot) {
      if (slot != null && this.isValid(slot)) {
         this.selectedRecipeId = null;
         this.ghostRecipe.clear();
         if (this.isOpen()) {
            this.refreshInputs();
         }
      }

   }

   private void populateAllRecipes() {
      Iterator var1 = this.tabs.iterator();

      while(var1.hasNext()) {
         Tab tab = (Tab)var1.next();
         Iterator var3 = this.recipeBook.getResultsForCategory(tab.category()).iterator();

         while(var3.hasNext()) {
            RecipeResultCollection recipeResultCollection = (RecipeResultCollection)var3.next();
            this.populateRecipes(recipeResultCollection, this.recipeFinder);
         }
      }

   }

   protected abstract void populateRecipes(RecipeResultCollection recipeResultCollection, RecipeFinder recipeFinder);

   private void refreshResults(boolean resetCurrentPage, boolean filteringCraftable) {
      List list = this.recipeBook.getResultsForCategory(this.currentTab.getCategory());
      List list2 = Lists.newArrayList(list);
      list2.removeIf((resultCollection) -> {
         return !resultCollection.hasDisplayableRecipes();
      });
      String string = this.searchField.getText();
      if (!string.isEmpty()) {
         ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
         if (clientPlayNetworkHandler != null) {
            ObjectSet objectSet = new ObjectLinkedOpenHashSet(clientPlayNetworkHandler.getSearchManager().getRecipeOutputReloadFuture().findAll(string.toLowerCase(Locale.ROOT)));
            list2.removeIf((resultCollection) -> {
               return !objectSet.contains(resultCollection);
            });
         }
      }

      if (filteringCraftable) {
         list2.removeIf((resultCollection) -> {
            return !resultCollection.hasCraftableRecipes();
         });
      }

      this.recipesArea.setResults(list2, resetCurrentPage, filteringCraftable);
   }

   private void refreshTabButtons(boolean filteringCraftable) {
      int i = (this.parentWidth - 147) / 2 - this.leftOffset - 30;
      int j = (this.parentHeight - 166) / 2 + 3;
      int k = true;
      int l = 0;
      Iterator var6 = this.tabButtons.iterator();

      while(var6.hasNext()) {
         RecipeGroupButtonWidget recipeGroupButtonWidget = (RecipeGroupButtonWidget)var6.next();
         RecipeBookGroup recipeBookGroup = recipeGroupButtonWidget.getCategory();
         if (recipeBookGroup instanceof RecipeBookType) {
            recipeGroupButtonWidget.visible = true;
            recipeGroupButtonWidget.setPosition(i, j + 27 * l++);
         } else if (recipeGroupButtonWidget.hasKnownRecipes(this.recipeBook)) {
            recipeGroupButtonWidget.setPosition(i, j + 27 * l++);
            recipeGroupButtonWidget.checkForNewRecipes(this.recipeBook, filteringCraftable);
         }
      }

   }

   public void update() {
      boolean bl = this.isGuiOpen();
      if (this.isOpen() != bl) {
         this.setOpen(bl);
      }

      if (this.isOpen()) {
         if (this.cachedInvChangeCount != this.client.player.getInventory().getChangeCount()) {
            this.refreshInputs();
            this.cachedInvChangeCount = this.client.player.getInventory().getChangeCount();
         }

      }
   }

   private void refreshInputs() {
      this.recipeFinder.clear();
      this.client.player.getInventory().populateRecipeFinder(this.recipeFinder);
      this.craftingScreenHandler.populateRecipeFinder(this.recipeFinder);
      this.populateAllRecipes();
      this.refreshResults(false, this.isFilteringCraftable());
   }

   private boolean isFilteringCraftable() {
      return this.recipeBook.isFilteringCraftable(this.craftingScreenHandler.getCategory());
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      if (this.isOpen()) {
         if (!Screen.hasControlDown()) {
            this.displayTime += deltaTicks;
         }

         int i = this.getLeft();
         int j = this.getTop();
         context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 1.0F, 1.0F, 147, 166, 256, 256);
         this.searchField.render(context, mouseX, mouseY, deltaTicks);
         Iterator var7 = this.tabButtons.iterator();

         while(var7.hasNext()) {
            RecipeGroupButtonWidget recipeGroupButtonWidget = (RecipeGroupButtonWidget)var7.next();
            recipeGroupButtonWidget.render(context, mouseX, mouseY, deltaTicks);
         }

         this.toggleCraftableButton.render(context, mouseX, mouseY, deltaTicks);
         this.recipesArea.draw(context, i, j, mouseX, mouseY, deltaTicks);
      }
   }

   public void drawTooltip(DrawContext context, int x, int y, @Nullable Slot slot) {
      if (this.isOpen()) {
         this.recipesArea.drawTooltip(context, x, y);
         this.ghostRecipe.drawTooltip(context, this.client, x, y, slot);
      }
   }

   protected abstract Text getToggleCraftableButtonText();

   public void drawGhostSlots(DrawContext context, boolean resultHasPadding) {
      this.ghostRecipe.draw(context, this.client, resultHasPadding);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.isOpen() && !this.client.player.isSpectator()) {
         if (this.recipesArea.mouseClicked(mouseX, mouseY, button, this.getLeft(), this.getTop(), 147, 166)) {
            NetworkRecipeId networkRecipeId = this.recipesArea.getLastClickedRecipe();
            RecipeResultCollection recipeResultCollection = this.recipesArea.getLastClickedResults();
            if (networkRecipeId != null && recipeResultCollection != null) {
               if (!this.select(recipeResultCollection, networkRecipeId)) {
                  return false;
               }

               this.selectedRecipeResults = recipeResultCollection;
               this.selectedRecipe = networkRecipeId;
               if (!this.isWide()) {
                  this.setOpen(false);
               }
            }

            return true;
         } else {
            boolean bl;
            if (this.searchField != null) {
               bl = this.searchFieldRect != null && this.searchFieldRect.contains(MathHelper.floor(mouseX), MathHelper.floor(mouseY));
               if (bl || this.searchField.mouseClicked(mouseX, mouseY, button)) {
                  this.searchField.setFocused(true);
                  return true;
               }

               this.searchField.setFocused(false);
            }

            if (this.toggleCraftableButton.mouseClicked(mouseX, mouseY, button)) {
               bl = this.toggleFilteringCraftable();
               this.toggleCraftableButton.setToggled(bl);
               this.updateTooltip();
               this.sendBookDataPacket();
               this.refreshResults(false, bl);
               return true;
            } else {
               Iterator var8 = this.tabButtons.iterator();

               RecipeGroupButtonWidget recipeGroupButtonWidget;
               do {
                  if (!var8.hasNext()) {
                     return false;
                  }

                  recipeGroupButtonWidget = (RecipeGroupButtonWidget)var8.next();
               } while(!recipeGroupButtonWidget.mouseClicked(mouseX, mouseY, button));

               if (this.currentTab != recipeGroupButtonWidget) {
                  if (this.currentTab != null) {
                     this.currentTab.setToggled(false);
                  }

                  this.currentTab = recipeGroupButtonWidget;
                  this.currentTab.setToggled(true);
                  this.refreshResults(true, this.isFilteringCraftable());
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   private boolean select(RecipeResultCollection results, NetworkRecipeId recipeId) {
      if (!results.isCraftable(recipeId) && recipeId.equals(this.selectedRecipeId)) {
         return false;
      } else {
         this.selectedRecipeId = recipeId;
         this.ghostRecipe.clear();
         this.client.interactionManager.clickRecipe(this.client.player.currentScreenHandler.syncId, recipeId, Screen.hasShiftDown());
         return true;
      }
   }

   private boolean toggleFilteringCraftable() {
      net.minecraft.recipe.book.RecipeBookType recipeBookType = this.craftingScreenHandler.getCategory();
      boolean bl = !this.recipeBook.isFilteringCraftable(recipeBookType);
      this.recipeBook.setFilteringCraftable(recipeBookType, bl);
      return bl;
   }

   public boolean isClickOutsideBounds(double mouseX, double mouseY, int x, int y, int backgroundWidth, int backgroundHeight, int button) {
      if (!this.isOpen()) {
         return true;
      } else {
         boolean bl = mouseX < (double)x || mouseY < (double)y || mouseX >= (double)(x + backgroundWidth) || mouseY >= (double)(y + backgroundHeight);
         boolean bl2 = (double)(x - 147) < mouseX && mouseX < (double)x && (double)y < mouseY && mouseY < (double)(y + backgroundHeight);
         return bl && !bl2 && !this.currentTab.isSelected();
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      this.searching = false;
      if (this.isOpen() && !this.client.player.isSpectator()) {
         if (keyCode == 256 && !this.isWide()) {
            this.setOpen(false);
            return true;
         } else if (this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
            this.refreshSearchResults();
            return true;
         } else if (this.searchField.isFocused() && this.searchField.isVisible() && keyCode != 256) {
            return true;
         } else if (this.client.options.chatKey.matchesKey(keyCode, scanCode) && !this.searchField.isFocused()) {
            this.searching = true;
            this.searchField.setFocused(true);
            return true;
         } else if (KeyCodes.isToggle(keyCode) && this.selectedRecipeResults != null && this.selectedRecipe != null) {
            ClickableWidget.playClickSound(MinecraftClient.getInstance().getSoundManager());
            return this.select(this.selectedRecipeResults, this.selectedRecipe);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      this.searching = false;
      return Element.super.keyReleased(keyCode, scanCode, modifiers);
   }

   public boolean charTyped(char chr, int modifiers) {
      if (this.searching) {
         return false;
      } else if (this.isOpen() && !this.client.player.isSpectator()) {
         if (this.searchField.charTyped(chr, modifiers)) {
            this.refreshSearchResults();
            return true;
         } else {
            return Element.super.charTyped(chr, modifiers);
         }
      } else {
         return false;
      }
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      return false;
   }

   public void setFocused(boolean focused) {
   }

   public boolean isFocused() {
      return false;
   }

   private void refreshSearchResults() {
      String string = this.searchField.getText().toLowerCase(Locale.ROOT);
      this.triggerPirateSpeakEasterEgg(string);
      if (!string.equals(this.searchText)) {
         this.refreshResults(false, this.isFilteringCraftable());
         this.searchText = string;
      }

   }

   private void triggerPirateSpeakEasterEgg(String search) {
      if ("excitedze".equals(search)) {
         LanguageManager languageManager = this.client.getLanguageManager();
         String string = "en_pt";
         LanguageDefinition languageDefinition = languageManager.getLanguage("en_pt");
         if (languageDefinition == null || languageManager.getLanguage().equals("en_pt")) {
            return;
         }

         languageManager.setLanguage("en_pt");
         this.client.options.language = "en_pt";
         this.client.reloadResources();
         this.client.options.write();
      }

   }

   private boolean isWide() {
      return this.leftOffset == 86;
   }

   public void refresh() {
      this.populateAllRecipes();
      this.refreshTabButtons(this.isFilteringCraftable());
      if (this.isOpen()) {
         this.refreshResults(false, this.isFilteringCraftable());
      }

   }

   public void onRecipeDisplayed(NetworkRecipeId recipeId) {
      this.client.player.onRecipeDisplayed(recipeId);
   }

   public void onCraftFailed(RecipeDisplay display) {
      this.ghostRecipe.clear();
      ContextParameterMap contextParameterMap = SlotDisplayContexts.createParameters((World)Objects.requireNonNull(this.client.world));
      this.showGhostRecipe(this.ghostRecipe, display, contextParameterMap);
   }

   protected abstract void showGhostRecipe(GhostRecipe ghostRecipe, RecipeDisplay display, ContextParameterMap context);

   protected void sendBookDataPacket() {
      if (this.client.getNetworkHandler() != null) {
         net.minecraft.recipe.book.RecipeBookType recipeBookType = this.craftingScreenHandler.getCategory();
         boolean bl = this.recipeBook.getOptions().isGuiOpen(recipeBookType);
         boolean bl2 = this.recipeBook.getOptions().isFilteringCraftable(recipeBookType);
         this.client.getNetworkHandler().sendPacket(new RecipeCategoryOptionsC2SPacket(recipeBookType, bl, bl2));
      }

   }

   public Selectable.SelectionType getType() {
      return this.open ? Selectable.SelectionType.HOVERED : Selectable.SelectionType.NONE;
   }

   public void appendNarrations(NarrationMessageBuilder builder) {
      List list = Lists.newArrayList();
      this.recipesArea.forEachButton((button) -> {
         if (button.isNarratable()) {
            list.add(button);
         }

      });
      list.add(this.searchField);
      list.add(this.toggleCraftableButton);
      list.addAll(this.tabButtons);
      Screen.SelectedElementNarrationData selectedElementNarrationData = Screen.findSelectedElementData(list, (Selectable)null);
      if (selectedElementNarrationData != null) {
         selectedElementNarrationData.selectable.appendNarrations(builder.nextMessage());
      }

   }

   static {
      SEARCH_HINT_TEXT = Text.translatable("gui.recipebook.search_hint").formatted(Formatting.ITALIC).formatted(Formatting.GRAY);
      TOGGLE_ALL_RECIPES_TEXT = Text.translatable("gui.recipebook.toggleRecipes.all");
   }

   @Environment(EnvType.CLIENT)
   public static record Tab(ItemStack primaryIcon, Optional secondaryIcon, RecipeBookGroup category) {
      public Tab(RecipeBookType type) {
         this((ItemStack)(new ItemStack(Items.COMPASS)), (Optional)Optional.empty(), (RecipeBookGroup)type);
      }

      public Tab(Item primaryIcon, RecipeBookCategory category) {
         this((ItemStack)(new ItemStack(primaryIcon)), (Optional)Optional.empty(), (RecipeBookGroup)category);
      }

      public Tab(Item primaryIcon, Item secondaryIcon, RecipeBookCategory category) {
         this((ItemStack)(new ItemStack(primaryIcon)), (Optional)Optional.of(new ItemStack(secondaryIcon)), (RecipeBookGroup)category);
      }

      public Tab(ItemStack itemStack, Optional optional, RecipeBookGroup recipeBookGroup) {
         this.primaryIcon = itemStack;
         this.secondaryIcon = optional;
         this.category = recipeBookGroup;
      }

      public ItemStack primaryIcon() {
         return this.primaryIcon;
      }

      public Optional secondaryIcon() {
         return this.secondaryIcon;
      }

      public RecipeBookGroup category() {
         return this.category;
      }
   }
}
