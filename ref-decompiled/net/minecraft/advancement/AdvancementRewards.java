package net.minecraft.advancement;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.LazyContainer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public record AdvancementRewards(int experience, List loot, List recipes, Optional function) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codec.INT.optionalFieldOf("experience", 0).forGetter(AdvancementRewards::experience), LootTable.TABLE_KEY.listOf().optionalFieldOf("loot", List.of()).forGetter(AdvancementRewards::loot), Recipe.KEY_CODEC.listOf().optionalFieldOf("recipes", List.of()).forGetter(AdvancementRewards::recipes), LazyContainer.CODEC.optionalFieldOf("function").forGetter(AdvancementRewards::function)).apply(instance, AdvancementRewards::new);
   });
   public static final AdvancementRewards NONE = new AdvancementRewards(0, List.of(), List.of(), Optional.empty());

   public AdvancementRewards(int experience, List list, List list2, Optional optional) {
      this.experience = experience;
      this.loot = list;
      this.recipes = list2;
      this.function = optional;
   }

   public void apply(ServerPlayerEntity player) {
      player.addExperience(this.experience);
      ServerWorld serverWorld = player.getWorld();
      MinecraftServer minecraftServer = serverWorld.getServer();
      LootWorldContext lootWorldContext = (new LootWorldContext.Builder(serverWorld)).add(LootContextParameters.THIS_ENTITY, player).add(LootContextParameters.ORIGIN, player.getPos()).build(LootContextTypes.ADVANCEMENT_REWARD);
      boolean bl = false;
      Iterator var6 = this.loot.iterator();

      while(var6.hasNext()) {
         RegistryKey registryKey = (RegistryKey)var6.next();
         ObjectListIterator var8 = minecraftServer.getReloadableRegistries().getLootTable(registryKey).generateLoot(lootWorldContext).iterator();

         while(var8.hasNext()) {
            ItemStack itemStack = (ItemStack)var8.next();
            if (player.giveItemStack(itemStack)) {
               serverWorld.playSound((Entity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
               bl = true;
            } else {
               ItemEntity itemEntity = player.dropItem(itemStack, false);
               if (itemEntity != null) {
                  itemEntity.resetPickupDelay();
                  itemEntity.setOwner(player.getUuid());
               }
            }
         }
      }

      if (bl) {
         player.currentScreenHandler.sendContentUpdates();
      }

      if (!this.recipes.isEmpty()) {
         player.unlockRecipes(this.recipes);
      }

      this.function.flatMap((function) -> {
         return function.get(minecraftServer.getCommandFunctionManager());
      }).ifPresent((function) -> {
         minecraftServer.getCommandFunctionManager().execute(function, player.getCommandSource().withSilent().withLevel(2));
      });
   }

   public int experience() {
      return this.experience;
   }

   public List loot() {
      return this.loot;
   }

   public List recipes() {
      return this.recipes;
   }

   public Optional function() {
      return this.function;
   }

   public static class Builder {
      private int experience;
      private final ImmutableList.Builder loot = ImmutableList.builder();
      private final ImmutableList.Builder recipes = ImmutableList.builder();
      private Optional function = Optional.empty();

      public static Builder experience(int experience) {
         return (new Builder()).setExperience(experience);
      }

      public Builder setExperience(int experience) {
         this.experience += experience;
         return this;
      }

      public static Builder loot(RegistryKey loot) {
         return (new Builder()).addLoot(loot);
      }

      public Builder addLoot(RegistryKey loot) {
         this.loot.add(loot);
         return this;
      }

      public static Builder recipe(RegistryKey recipeKey) {
         return (new Builder()).addRecipe(recipeKey);
      }

      public Builder addRecipe(RegistryKey recipeKey) {
         this.recipes.add(recipeKey);
         return this;
      }

      public static Builder function(Identifier function) {
         return (new Builder()).setFunction(function);
      }

      public Builder setFunction(Identifier function) {
         this.function = Optional.of(function);
         return this;
      }

      public AdvancementRewards build() {
         return new AdvancementRewards(this.experience, this.loot.build(), this.recipes.build(), this.function.map(LazyContainer::new));
      }
   }
}
