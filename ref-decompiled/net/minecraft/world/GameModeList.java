package net.minecraft.world;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.List;

public record GameModeList(List gameModes) {
   public static final GameModeList ALL = of(GameMode.values());
   public static final GameModeList SURVIVAL_LIKE;
   public static final Codec CODEC;

   public GameModeList(List list) {
      this.gameModes = list;
   }

   public static GameModeList of(GameMode... gameModes) {
      return new GameModeList(Arrays.stream(gameModes).toList());
   }

   public boolean contains(GameMode gameMode) {
      return this.gameModes.contains(gameMode);
   }

   public List gameModes() {
      return this.gameModes;
   }

   static {
      SURVIVAL_LIKE = of(GameMode.SURVIVAL, GameMode.ADVENTURE);
      CODEC = GameMode.CODEC.listOf().xmap(GameModeList::new, GameModeList::gameModes);
   }
}
