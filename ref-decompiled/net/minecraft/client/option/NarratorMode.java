package net.minecraft.client.option;

import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.function.ValueLists;

@Environment(EnvType.CLIENT)
public enum NarratorMode {
   OFF(0, "options.narrator.off"),
   ALL(1, "options.narrator.all"),
   CHAT(2, "options.narrator.chat"),
   SYSTEM(3, "options.narrator.system");

   private static final IntFunction BY_ID = ValueLists.createIndexToValueFunction(NarratorMode::getId, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.WRAP);
   private final int id;
   private final Text name;

   private NarratorMode(final int id, final String name) {
      this.id = id;
      this.name = Text.translatable(name);
   }

   public int getId() {
      return this.id;
   }

   public Text getName() {
      return this.name;
   }

   public static NarratorMode byId(int id) {
      return (NarratorMode)BY_ID.apply(id);
   }

   public boolean shouldNarrateChat() {
      return this == ALL || this == CHAT;
   }

   public boolean shouldNarrateSystem() {
      return this == ALL || this == SYSTEM;
   }

   public boolean shouldNarrate() {
      return this == ALL || this == SYSTEM || this == CHAT;
   }

   // $FF: synthetic method
   private static NarratorMode[] method_36864() {
      return new NarratorMode[]{OFF, ALL, CHAT, SYSTEM};
   }
}
