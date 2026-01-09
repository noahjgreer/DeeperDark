package net.minecraft.client.render.model.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.state.StateManager;

@Environment(EnvType.CLIENT)
public record MultipartModelComponent(Optional selector, BlockStateModel.Unbaked model) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(MultipartModelCondition.CODEC.optionalFieldOf("when").forGetter(MultipartModelComponent::selector), BlockStateModel.Unbaked.CODEC.fieldOf("apply").forGetter(MultipartModelComponent::model)).apply(instance, MultipartModelComponent::new);
   });

   public MultipartModelComponent(Optional optional, BlockStateModel.Unbaked model) {
      this.selector = optional;
      this.model = model;
   }

   public Predicate init(StateManager value) {
      return (Predicate)this.selector.map((multipartModelCondition) -> {
         return multipartModelCondition.instantiate(value);
      }).orElse((state) -> {
         return true;
      });
   }

   public Optional selector() {
      return this.selector;
   }

   public BlockStateModel.Unbaked model() {
      return this.model;
   }
}
