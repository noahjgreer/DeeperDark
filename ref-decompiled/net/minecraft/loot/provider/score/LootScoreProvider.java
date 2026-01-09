package net.minecraft.loot.provider.score;

import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.scoreboard.ScoreHolder;
import org.jetbrains.annotations.Nullable;

public interface LootScoreProvider {
   @Nullable
   ScoreHolder getScoreHolder(LootContext context);

   LootScoreProviderType getType();

   Set getRequiredParameters();
}
