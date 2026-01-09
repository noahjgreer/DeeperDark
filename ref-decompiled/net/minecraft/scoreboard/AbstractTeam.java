package net.minecraft.scoreboard;

import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTeam {
   public boolean isEqual(@Nullable AbstractTeam team) {
      if (team == null) {
         return false;
      } else {
         return this == team;
      }
   }

   public abstract String getName();

   public abstract MutableText decorateName(Text name);

   public abstract boolean shouldShowFriendlyInvisibles();

   public abstract boolean isFriendlyFireAllowed();

   public abstract VisibilityRule getNameTagVisibilityRule();

   public abstract Formatting getColor();

   public abstract Collection getPlayerList();

   public abstract VisibilityRule getDeathMessageVisibilityRule();

   public abstract CollisionRule getCollisionRule();

   public static enum CollisionRule implements StringIdentifiable {
      ALWAYS("always", 0),
      NEVER("never", 1),
      PUSH_OTHER_TEAMS("pushOtherTeams", 2),
      PUSH_OWN_TEAM("pushOwnTeam", 3);

      public static final Codec CODEC = StringIdentifiable.createCodec(CollisionRule::values);
      private static final IntFunction INDEX_MAPPER = ValueLists.createIndexToValueFunction((collisionRule) -> {
         return collisionRule.index;
      }, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
      public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, (collisionRule) -> {
         return collisionRule.index;
      });
      public final String name;
      public final int index;

      private CollisionRule(final String name, final int index) {
         this.name = name;
         this.index = index;
      }

      public Text getDisplayName() {
         return Text.translatable("team.collision." + this.name);
      }

      public String asString() {
         return this.name;
      }

      // $FF: synthetic method
      private static CollisionRule[] method_36797() {
         return new CollisionRule[]{ALWAYS, NEVER, PUSH_OTHER_TEAMS, PUSH_OWN_TEAM};
      }
   }

   public static enum VisibilityRule implements StringIdentifiable {
      ALWAYS("always", 0),
      NEVER("never", 1),
      HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
      HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

      public static final Codec CODEC = StringIdentifiable.createCodec(VisibilityRule::values);
      private static final IntFunction INDEX_MAPPER = ValueLists.createIndexToValueFunction((visibilityRule) -> {
         return visibilityRule.index;
      }, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
      public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, (visibilityRule) -> {
         return visibilityRule.index;
      });
      public final String name;
      public final int index;

      private VisibilityRule(final String name, final int index) {
         this.name = name;
         this.index = index;
      }

      public Text getDisplayName() {
         return Text.translatable("team.visibility." + this.name);
      }

      public String asString() {
         return this.name;
      }

      // $FF: synthetic method
      private static VisibilityRule[] method_36798() {
         return new VisibilityRule[]{ALWAYS, NEVER, HIDE_FOR_OTHER_TEAMS, HIDE_FOR_OWN_TEAM};
      }
   }
}
