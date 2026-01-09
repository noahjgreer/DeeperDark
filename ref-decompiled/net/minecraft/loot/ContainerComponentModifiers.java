package net.minecraft.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.registry.Registries;

public interface ContainerComponentModifiers {
   ContainerComponentModifier CONTAINER = new ContainerComponentModifier() {
      public ComponentType getComponentType() {
         return DataComponentTypes.CONTAINER;
      }

      public Stream stream(ContainerComponent containerComponent) {
         return containerComponent.stream();
      }

      public ContainerComponent getDefault() {
         return ContainerComponent.DEFAULT;
      }

      public ContainerComponent apply(ContainerComponent containerComponent, Stream stream) {
         return ContainerComponent.fromStacks(stream.toList());
      }

      // $FF: synthetic method
      public Object getDefault() {
         return this.getDefault();
      }
   };
   ContainerComponentModifier BUNDLE_CONTENTS = new ContainerComponentModifier() {
      public ComponentType getComponentType() {
         return DataComponentTypes.BUNDLE_CONTENTS;
      }

      public BundleContentsComponent getDefault() {
         return BundleContentsComponent.DEFAULT;
      }

      public Stream stream(BundleContentsComponent bundleContentsComponent) {
         return bundleContentsComponent.stream();
      }

      public BundleContentsComponent apply(BundleContentsComponent bundleContentsComponent, Stream stream) {
         BundleContentsComponent.Builder builder = (new BundleContentsComponent.Builder(bundleContentsComponent)).clear();
         Objects.requireNonNull(builder);
         stream.forEach(builder::add);
         return builder.build();
      }

      // $FF: synthetic method
      public Object getDefault() {
         return this.getDefault();
      }
   };
   ContainerComponentModifier CHARGED_PROJECTILES = new ContainerComponentModifier() {
      public ComponentType getComponentType() {
         return DataComponentTypes.CHARGED_PROJECTILES;
      }

      public ChargedProjectilesComponent getDefault() {
         return ChargedProjectilesComponent.DEFAULT;
      }

      public Stream stream(ChargedProjectilesComponent chargedProjectilesComponent) {
         return chargedProjectilesComponent.getProjectiles().stream();
      }

      public ChargedProjectilesComponent apply(ChargedProjectilesComponent chargedProjectilesComponent, Stream stream) {
         return ChargedProjectilesComponent.of(stream.toList());
      }

      // $FF: synthetic method
      public Object getDefault() {
         return this.getDefault();
      }
   };
   Map TYPE_TO_MODIFIER = (Map)Stream.of(CONTAINER, BUNDLE_CONTENTS, CHARGED_PROJECTILES).collect(Collectors.toMap(ContainerComponentModifier::getComponentType, (containerComponentModifier) -> {
      return containerComponentModifier;
   }));
   Codec MODIFIER_CODEC = Registries.DATA_COMPONENT_TYPE.getCodec().comapFlatMap((componentType) -> {
      ContainerComponentModifier containerComponentModifier = (ContainerComponentModifier)TYPE_TO_MODIFIER.get(componentType);
      return containerComponentModifier != null ? DataResult.success(containerComponentModifier) : DataResult.error(() -> {
         return "No items in component";
      });
   }, ContainerComponentModifier::getComponentType);
}
