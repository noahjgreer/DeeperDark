package net.minecraft.dialog.input;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.dynamic.Codecs;

public record SingleOptionInputControl(int width, List entries, Text label, boolean labelVisible) implements InputControl {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Dialog.WIDTH_CODEC.optionalFieldOf("width", 200).forGetter(SingleOptionInputControl::width), Codecs.nonEmptyList(SingleOptionInputControl.Entry.CODEC.listOf()).fieldOf("options").forGetter(SingleOptionInputControl::entries), TextCodecs.CODEC.fieldOf("label").forGetter(SingleOptionInputControl::label), Codec.BOOL.optionalFieldOf("label_visible", true).forGetter(SingleOptionInputControl::labelVisible)).apply(instance, SingleOptionInputControl::new);
   }).validate((inputControl) -> {
      long l = inputControl.entries.stream().filter(Entry::initial).count();
      return l > 1L ? DataResult.error(() -> {
         return "Multiple initial values";
      }) : DataResult.success(inputControl);
   });

   public SingleOptionInputControl(int i, List list, Text text, boolean bl) {
      this.width = i;
      this.entries = list;
      this.label = text;
      this.labelVisible = bl;
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public Optional getInitialEntry() {
      return this.entries.stream().filter(Entry::initial).findFirst();
   }

   public int width() {
      return this.width;
   }

   public List entries() {
      return this.entries;
   }

   public Text label() {
      return this.label;
   }

   public boolean labelVisible() {
      return this.labelVisible;
   }

   public static record Entry(String id, Optional display, boolean initial) {
      public static final Codec BASE_CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.STRING.fieldOf("id").forGetter(Entry::id), TextCodecs.CODEC.optionalFieldOf("display").forGetter(Entry::display), Codec.BOOL.optionalFieldOf("initial", false).forGetter(Entry::initial)).apply(instance, Entry::new);
      });
      public static final Codec CODEC;

      public Entry(String string, Optional optional, boolean bl) {
         this.id = string;
         this.display = optional;
         this.initial = bl;
      }

      public Text getDisplay() {
         return (Text)this.display.orElseGet(() -> {
            return Text.literal(this.id);
         });
      }

      public String id() {
         return this.id;
      }

      public Optional display() {
         return this.display;
      }

      public boolean initial() {
         return this.initial;
      }

      static {
         CODEC = Codec.withAlternative(BASE_CODEC, Codec.STRING, (id) -> {
            return new Entry(id, Optional.empty(), false);
         });
      }
   }
}
