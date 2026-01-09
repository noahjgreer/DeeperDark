package net.minecraft.client.item;

import java.util.Objects;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ItemModelManager {
   private final Function modelGetter;
   private final Function propertiesGetter;

   public ItemModelManager(BakedModelManager bakedModelManager) {
      Objects.requireNonNull(bakedModelManager);
      this.modelGetter = bakedModelManager::getItemModel;
      Objects.requireNonNull(bakedModelManager);
      this.propertiesGetter = bakedModelManager::getItemProperties;
   }

   public void updateForLivingEntity(ItemRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, LivingEntity entity) {
      this.clearAndUpdate(renderState, stack, displayContext, entity.getWorld(), entity, entity.getId() + displayContext.ordinal());
   }

   public void updateForNonLivingEntity(ItemRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, Entity entity) {
      this.clearAndUpdate(renderState, stack, displayContext, entity.getWorld(), (LivingEntity)null, entity.getId());
   }

   public void clearAndUpdate(ItemRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, @Nullable World world, @Nullable LivingEntity entity, int seed) {
      renderState.clear();
      if (!stack.isEmpty()) {
         renderState.displayContext = displayContext;
         this.update(renderState, stack, displayContext, world, entity, seed);
      }

   }

   public void update(ItemRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, @Nullable World world, @Nullable LivingEntity entity, int seed) {
      Identifier identifier = (Identifier)stack.get(DataComponentTypes.ITEM_MODEL);
      if (identifier != null) {
         renderState.setOversizedInGui(((ItemAsset.Properties)this.propertiesGetter.apply(identifier)).oversizedInGui());
         ItemModel var10000 = (ItemModel)this.modelGetter.apply(identifier);
         ClientWorld var10005;
         if (world instanceof ClientWorld) {
            ClientWorld clientWorld = (ClientWorld)world;
            var10005 = clientWorld;
         } else {
            var10005 = null;
         }

         var10000.update(renderState, stack, this, displayContext, var10005, entity, seed);
      }
   }

   public boolean hasHandAnimationOnSwap(ItemStack stack) {
      Identifier identifier = (Identifier)stack.get(DataComponentTypes.ITEM_MODEL);
      return identifier == null ? true : ((ItemAsset.Properties)this.propertiesGetter.apply(identifier)).handAnimationOnSwap();
   }
}
