package net.minecraft.loot.provider.score;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.scoreboard.ScoreHolder;

public record FixedLootScoreProvider(String name) implements LootScoreProvider {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.STRING.fieldOf("name").forGetter(FixedLootScoreProvider::name)).apply(instance, FixedLootScoreProvider::new);
   });

   public FixedLootScoreProvider(String name) {
      this.name = name;
   }

   public static LootScoreProvider create(String name) {
      return new FixedLootScoreProvider(name);
   }

   public LootScoreProviderType getType() {
      return LootScoreProviderTypes.FIXED;
   }

   public ScoreHolder getScoreHolder(LootContext context) {
      return ScoreHolder.fromName(this.name);
   }

   public Set getRequiredParameters() {
      return Set.of();
   }

   public String name() {
      return this.name;
   }
}
