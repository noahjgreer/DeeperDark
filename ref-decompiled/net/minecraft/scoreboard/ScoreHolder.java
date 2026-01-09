package net.minecraft.scoreboard;

import com.mojang.authlib.GameProfile;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public interface ScoreHolder {
   String WILDCARD_NAME = "*";
   ScoreHolder WILDCARD = new ScoreHolder() {
      public String getNameForScoreboard() {
         return "*";
      }
   };

   String getNameForScoreboard();

   @Nullable
   default Text getDisplayName() {
      return null;
   }

   default Text getStyledDisplayName() {
      Text text = this.getDisplayName();
      return text != null ? text.copy().styled((style) -> {
         return style.withHoverEvent(new HoverEvent.ShowText(Text.literal(this.getNameForScoreboard())));
      }) : Text.literal(this.getNameForScoreboard());
   }

   static ScoreHolder fromName(final String name) {
      if (name.equals("*")) {
         return WILDCARD;
      } else {
         final Text text = Text.literal(name);
         return new ScoreHolder() {
            public String getNameForScoreboard() {
               return name;
            }

            public Text getStyledDisplayName() {
               return text;
            }
         };
      }
   }

   static ScoreHolder fromProfile(GameProfile gameProfile) {
      final String string = gameProfile.getName();
      return new ScoreHolder() {
         public String getNameForScoreboard() {
            return string;
         }
      };
   }
}
