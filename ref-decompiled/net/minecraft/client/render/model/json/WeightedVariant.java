package net.minecraft.client.render.model.json;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.SimpleBlockStateModel;
import net.minecraft.client.render.model.WeightedBlockStateModel;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;

@Environment(EnvType.CLIENT)
public record WeightedVariant(Pool variants) {
   public WeightedVariant(Pool pool) {
      if (pool.isEmpty()) {
         throw new IllegalArgumentException("Variant list must contain at least one element");
      } else {
         this.variants = pool;
      }
   }

   public WeightedVariant apply(ModelVariantOperator operator) {
      return new WeightedVariant(this.variants.transform(operator));
   }

   public BlockStateModel.Unbaked toModel() {
      List list = this.variants.getEntries();
      return (BlockStateModel.Unbaked)(list.size() == 1 ? new SimpleBlockStateModel.Unbaked((ModelVariant)((Weighted)list.getFirst()).value()) : new WeightedBlockStateModel.Unbaked(this.variants.transform(SimpleBlockStateModel.Unbaked::new)));
   }

   public Pool variants() {
      return this.variants;
   }
}
