package net.minecraft.structure.rule.blockentity;

import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class PassthroughRuleBlockEntityModifier implements RuleBlockEntityModifier {
   public static final PassthroughRuleBlockEntityModifier INSTANCE = new PassthroughRuleBlockEntityModifier();
   public static final MapCodec CODEC;

   @Nullable
   public NbtCompound modifyBlockEntityNbt(Random random, @Nullable NbtCompound nbt) {
      return nbt;
   }

   public RuleBlockEntityModifierType getType() {
      return RuleBlockEntityModifierType.PASSTHROUGH;
   }

   static {
      CODEC = MapCodec.unit(INSTANCE);
   }
}
