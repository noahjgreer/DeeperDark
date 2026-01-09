package net.minecraft.client.gui.screen.narration;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;

@Environment(EnvType.CLIENT)
public class Narration {
   private final Object value;
   private final BiConsumer transformer;
   public static final Narration EMPTY;

   private Narration(Object value, BiConsumer transformer) {
      this.value = value;
      this.transformer = transformer;
   }

   public static Narration string(String string) {
      return new Narration(string, Consumer::accept);
   }

   public static Narration text(Text text) {
      return new Narration(text, (consumer, textx) -> {
         consumer.accept(textx.getString());
      });
   }

   public static Narration texts(List texts) {
      return new Narration(texts, (consumer, textsx) -> {
         texts.stream().map(Text::getString).forEach(consumer);
      });
   }

   public void forEachSentence(Consumer consumer) {
      this.transformer.accept(consumer, this.value);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Narration)) {
         return false;
      } else {
         Narration narration = (Narration)o;
         return narration.transformer == this.transformer && narration.value.equals(this.value);
      }
   }

   public int hashCode() {
      int i = this.value.hashCode();
      i = 31 * i + this.transformer.hashCode();
      return i;
   }

   static {
      EMPTY = new Narration(Unit.INSTANCE, (consumer, text) -> {
      });
   }
}
