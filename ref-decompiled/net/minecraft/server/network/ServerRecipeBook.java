package net.minecraft.server.network;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.network.packet.s2c.play.RecipeBookAddS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookSettingsS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.recipe.book.RecipeBookOptions;
import net.minecraft.registry.RegistryKey;
import org.slf4j.Logger;

public class ServerRecipeBook extends RecipeBook {
   public static final String RECIPE_BOOK_KEY = "recipeBook";
   private static final Logger LOGGER = LogUtils.getLogger();
   private final DisplayCollector collector;
   @VisibleForTesting
   protected final Set unlocked = Sets.newIdentityHashSet();
   @VisibleForTesting
   protected final Set highlighted = Sets.newIdentityHashSet();

   public ServerRecipeBook(DisplayCollector collector) {
      this.collector = collector;
   }

   public void unlock(RegistryKey recipeKey) {
      this.unlocked.add(recipeKey);
   }

   public boolean isUnlocked(RegistryKey recipeKey) {
      return this.unlocked.contains(recipeKey);
   }

   public void lock(RegistryKey recipeKey) {
      this.unlocked.remove(recipeKey);
      this.highlighted.remove(recipeKey);
   }

   public void unmarkHighlighted(RegistryKey recipeKey) {
      this.highlighted.remove(recipeKey);
   }

   private void markHighlighted(RegistryKey recipeKey) {
      this.highlighted.add(recipeKey);
   }

   public int unlockRecipes(Collection recipes, ServerPlayerEntity player) {
      List list = new ArrayList();
      Iterator var4 = recipes.iterator();

      while(var4.hasNext()) {
         RecipeEntry recipeEntry = (RecipeEntry)var4.next();
         RegistryKey registryKey = recipeEntry.id();
         if (!this.unlocked.contains(registryKey) && !recipeEntry.value().isIgnoredInRecipeBook()) {
            this.unlock(registryKey);
            this.markHighlighted(registryKey);
            this.collector.displaysForRecipe(registryKey, (display) -> {
               list.add(new RecipeBookAddS2CPacket.Entry(display, recipeEntry.value().showNotification(), true));
            });
            Criteria.RECIPE_UNLOCKED.trigger(player, recipeEntry);
         }
      }

      if (!list.isEmpty()) {
         player.networkHandler.sendPacket(new RecipeBookAddS2CPacket(list, false));
      }

      return list.size();
   }

   public int lockRecipes(Collection recipes, ServerPlayerEntity player) {
      List list = Lists.newArrayList();
      Iterator var4 = recipes.iterator();

      while(var4.hasNext()) {
         RecipeEntry recipeEntry = (RecipeEntry)var4.next();
         RegistryKey registryKey = recipeEntry.id();
         if (this.unlocked.contains(registryKey)) {
            this.lock(registryKey);
            this.collector.displaysForRecipe(registryKey, (display) -> {
               list.add(display.id());
            });
         }
      }

      if (!list.isEmpty()) {
         player.networkHandler.sendPacket(new RecipeBookRemoveS2CPacket(list));
      }

      return list.size();
   }

   private void handleList(List recipes, Consumer handler, Predicate validPredicate) {
      Iterator var4 = recipes.iterator();

      while(var4.hasNext()) {
         RegistryKey registryKey = (RegistryKey)var4.next();
         if (!validPredicate.test(registryKey)) {
            LOGGER.error("Tried to load unrecognized recipe: {} removed now.", registryKey);
         } else {
            handler.accept(registryKey);
         }
      }

   }

   public void sendInitRecipesPacket(ServerPlayerEntity player) {
      player.networkHandler.sendPacket(new RecipeBookSettingsS2CPacket(this.getOptions().copy()));
      List list = new ArrayList(this.unlocked.size());
      Iterator var3 = this.unlocked.iterator();

      while(var3.hasNext()) {
         RegistryKey registryKey = (RegistryKey)var3.next();
         this.collector.displaysForRecipe(registryKey, (display) -> {
            list.add(new RecipeBookAddS2CPacket.Entry(display, false, this.highlighted.contains(registryKey)));
         });
      }

      player.networkHandler.sendPacket(new RecipeBookAddS2CPacket(list, true));
   }

   public void copyFrom(ServerRecipeBook recipeBook) {
      this.unpack(recipeBook.pack());
   }

   public Packed pack() {
      return new Packed(this.options.copy(), List.copyOf(this.unlocked), List.copyOf(this.highlighted));
   }

   private void unpack(Packed packed) {
      this.unlocked.clear();
      this.highlighted.clear();
      this.options.copyFrom(packed.settings);
      this.unlocked.addAll(packed.known);
      this.highlighted.addAll(packed.highlight);
   }

   public void unpack(Packed packed, Predicate validPredicate) {
      this.options.copyFrom(packed.settings);
      List var10001 = packed.known;
      Set var10002 = this.unlocked;
      Objects.requireNonNull(var10002);
      this.handleList(var10001, var10002::add, validPredicate);
      var10001 = packed.highlight;
      var10002 = this.highlighted;
      Objects.requireNonNull(var10002);
      this.handleList(var10001, var10002::add, validPredicate);
   }

   @FunctionalInterface
   public interface DisplayCollector {
      void displaysForRecipe(RegistryKey recipeKey, Consumer adder);
   }

   public static record Packed(RecipeBookOptions settings, List known, List highlight) {
      final RecipeBookOptions settings;
      final List known;
      final List highlight;
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(RecipeBookOptions.CODEC.forGetter(Packed::settings), Recipe.KEY_CODEC.listOf().fieldOf("recipes").forGetter(Packed::known), Recipe.KEY_CODEC.listOf().fieldOf("toBeDisplayed").forGetter(Packed::highlight)).apply(instance, Packed::new);
      });

      public Packed(RecipeBookOptions recipeBookOptions, List list, List list2) {
         this.settings = recipeBookOptions;
         this.known = list;
         this.highlight = list2;
      }

      public RecipeBookOptions settings() {
         return this.settings;
      }

      public List known() {
         return this.known;
      }

      public List highlight() {
         return this.highlight;
      }
   }
}
