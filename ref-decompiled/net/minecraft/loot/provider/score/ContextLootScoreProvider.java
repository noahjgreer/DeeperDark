package net.minecraft.loot.provider.score;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.scoreboard.ScoreHolder;
import org.jetbrains.annotations.Nullable;

public record ContextLootScoreProvider(LootContext.EntityTarget target) implements LootScoreProvider {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(LootContext.EntityTarget.CODEC.fieldOf("target").forGetter(ContextLootScoreProvider::target)).apply(instance, ContextLootScoreProvider::new);
   });
   public static final Codec INLINE_CODEC;

   public ContextLootScoreProvider(LootContext.EntityTarget target) {
      this.target = target;
   }

   public static LootScoreProvider create(LootContext.EntityTarget target) {
      return new ContextLootScoreProvider(target);
   }

   public LootScoreProviderType getType() {
      return LootScoreProviderTypes.CONTEXT;
   }

   @Nullable
   public ScoreHolder getScoreHolder(LootContext context) {
      return (ScoreHolder)context.get(this.target.getParameter());
   }

   public Set getRequiredParameters() {
      return Set.of(this.target.getParameter());
   }

   public LootContext.EntityTarget target() {
      return this.target;
   }

   static {
      INLINE_CODEC = LootContext.EntityTarget.CODEC.xmap(ContextLootScoreProvider::new, ContextLootScoreProvider::target);
   }
}
