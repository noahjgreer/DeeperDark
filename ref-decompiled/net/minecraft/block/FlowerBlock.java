package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class FlowerBlock extends PlantBlock implements SuspiciousStewIngredient {
   protected static final MapCodec STEW_EFFECT_CODEC;
   public static final MapCodec CODEC;
   private static final VoxelShape SHAPE;
   private final SuspiciousStewEffectsComponent stewEffects;

   public MapCodec getCodec() {
      return CODEC;
   }

   public FlowerBlock(RegistryEntry stewEffect, float effectLengthInSeconds, AbstractBlock.Settings settings) {
      this(createStewEffectList(stewEffect, effectLengthInSeconds), settings);
   }

   public FlowerBlock(SuspiciousStewEffectsComponent stewEffects, AbstractBlock.Settings settings) {
      super(settings);
      this.stewEffects = stewEffects;
   }

   protected static SuspiciousStewEffectsComponent createStewEffectList(RegistryEntry effect, float effectLengthInSeconds) {
      return new SuspiciousStewEffectsComponent(List.of(new SuspiciousStewEffectsComponent.StewEffect(effect, MathHelper.floor(effectLengthInSeconds * 20.0F))));
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPE.offset(state.getModelOffset(pos));
   }

   public SuspiciousStewEffectsComponent getStewEffects() {
      return this.stewEffects;
   }

   @Nullable
   public StatusEffectInstance getContactEffect() {
      return null;
   }

   static {
      STEW_EFFECT_CODEC = SuspiciousStewEffectsComponent.CODEC.fieldOf("suspicious_stew_effects");
      CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(STEW_EFFECT_CODEC.forGetter(FlowerBlock::getStewEffects), createSettingsCodec()).apply(instance, FlowerBlock::new);
      });
      SHAPE = Block.createColumnShape(6.0, 0.0, 10.0);
   }
}
