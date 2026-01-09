package net.minecraft.network.message;

import java.util.function.IntFunction;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

public enum ChatVisibility implements TranslatableOption {
   FULL(0, "options.chat.visibility.full"),
   SYSTEM(1, "options.chat.visibility.system"),
   HIDDEN(2, "options.chat.visibility.hidden");

   private static final IntFunction BY_ID = ValueLists.createIndexToValueFunction(ChatVisibility::getId, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.WRAP);
   private final int id;
   private final String translationKey;

   private ChatVisibility(final int id, final String translationKey) {
      this.id = id;
      this.translationKey = translationKey;
   }

   public int getId() {
      return this.id;
   }

   public String getTranslationKey() {
      return this.translationKey;
   }

   public static ChatVisibility byId(int id) {
      return (ChatVisibility)BY_ID.apply(id);
   }

   // $FF: synthetic method
   private static ChatVisibility[] method_36660() {
      return new ChatVisibility[]{FULL, SYSTEM, HIDDEN};
   }
}
