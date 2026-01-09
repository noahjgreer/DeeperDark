package net.minecraft.item;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class KnowledgeBookItem extends Item {
   private static final Logger LOGGER = LogUtils.getLogger();

   public KnowledgeBookItem(Item.Settings settings) {
      super(settings);
   }

   public ActionResult use(World world, PlayerEntity user, Hand hand) {
      ItemStack itemStack = user.getStackInHand(hand);
      List list = (List)itemStack.getOrDefault(DataComponentTypes.RECIPES, List.of());
      itemStack.decrementUnlessCreative(1, user);
      if (list.isEmpty()) {
         return ActionResult.FAIL;
      } else {
         if (!world.isClient) {
            ServerRecipeManager serverRecipeManager = world.getServer().getRecipeManager();
            List list2 = new ArrayList(list.size());
            Iterator var8 = list.iterator();

            while(var8.hasNext()) {
               RegistryKey registryKey = (RegistryKey)var8.next();
               Optional optional = serverRecipeManager.get(registryKey);
               if (!optional.isPresent()) {
                  LOGGER.error("Invalid recipe: {}", registryKey);
                  return ActionResult.FAIL;
               }

               list2.add((RecipeEntry)optional.get());
            }

            user.unlockRecipes((Collection)list2);
            user.incrementStat(Stats.USED.getOrCreateStat(this));
         }

         return ActionResult.SUCCESS;
      }
   }
}
