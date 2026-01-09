package net.minecraft.structure.rule.blockentity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class AppendStaticRuleBlockEntityModifier implements RuleBlockEntityModifier {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(NbtCompound.CODEC.fieldOf("data").forGetter((modifier) -> {
         return modifier.nbt;
      })).apply(instance, AppendStaticRuleBlockEntityModifier::new);
   });
   private final NbtCompound nbt;

   public AppendStaticRuleBlockEntityModifier(NbtCompound nbt) {
      this.nbt = nbt;
   }

   public NbtCompound modifyBlockEntityNbt(Random random, @Nullable NbtCompound nbt) {
      return nbt == null ? this.nbt.copy() : nbt.copyFrom(this.nbt);
   }

   public RuleBlockEntityModifierType getType() {
      return RuleBlockEntityModifierType.APPEND_STATIC;
   }
}
