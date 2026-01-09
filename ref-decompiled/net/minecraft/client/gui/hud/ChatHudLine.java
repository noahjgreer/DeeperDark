package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record ChatHudLine(int creationTick, Text content, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator) {
   public ChatHudLine(int creationTick, Text text, @Nullable MessageSignatureData messageSignatureData, @Nullable MessageIndicator messageIndicator) {
      this.creationTick = creationTick;
      this.content = text;
      this.signature = messageSignatureData;
      this.indicator = messageIndicator;
   }

   @Nullable
   public MessageIndicator.Icon getIcon() {
      return this.indicator != null ? this.indicator.icon() : null;
   }

   public int creationTick() {
      return this.creationTick;
   }

   public Text content() {
      return this.content;
   }

   @Nullable
   public MessageSignatureData signature() {
      return this.signature;
   }

   @Nullable
   public MessageIndicator indicator() {
      return this.indicator;
   }

   @Environment(EnvType.CLIENT)
   public static record Visible(int addedTime, OrderedText content, @Nullable MessageIndicator indicator, boolean endOfEntry) {
      public Visible(int i, OrderedText orderedText, @Nullable MessageIndicator messageIndicator, boolean bl) {
         this.addedTime = i;
         this.content = orderedText;
         this.indicator = messageIndicator;
         this.endOfEntry = bl;
      }

      public int addedTime() {
         return this.addedTime;
      }

      public OrderedText content() {
         return this.content;
      }

      @Nullable
      public MessageIndicator indicator() {
         return this.indicator;
      }

      public boolean endOfEntry() {
         return this.endOfEntry;
      }
   }
}
