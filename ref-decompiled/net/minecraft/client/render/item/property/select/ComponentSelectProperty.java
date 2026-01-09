package net.minecraft.client.render.item.property.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.SelectItemModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record ComponentSelectProperty(ComponentType componentType) implements SelectProperty {
   private static final SelectProperty.Type TYPE = createType();

   public ComponentSelectProperty(ComponentType componentType) {
      this.componentType = componentType;
   }

   private static SelectProperty.Type createType() {
      Codec codec = Registries.DATA_COMPONENT_TYPE.getCodec().validate((componentType) -> {
         return componentType.shouldSkipSerialization() ? DataResult.error(() -> {
            return "Component can't be serialized";
         }) : DataResult.success(componentType);
      });
      MapCodec mapCodec = codec.dispatchMap("component", (unbakedSwitch) -> {
         return ((ComponentSelectProperty)unbakedSwitch.property()).componentType;
      }, (componentType) -> {
         return SelectProperty.Type.createCaseListCodec(componentType.getCodecOrThrow()).xmap((cases) -> {
            return new SelectItemModel.UnbakedSwitch(new ComponentSelectProperty(componentType), cases);
         }, SelectItemModel.UnbakedSwitch::cases);
      });
      return new SelectProperty.Type(mapCodec);
   }

   public static SelectProperty.Type getTypeInstance() {
      return TYPE;
   }

   @Nullable
   public Object getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ItemDisplayContext displayContext) {
      return stack.get(this.componentType);
   }

   public SelectProperty.Type getType() {
      return getTypeInstance();
   }

   public Codec valueCodec() {
      return this.componentType.getCodecOrThrow();
   }

   public ComponentType componentType() {
      return this.componentType;
   }
}
