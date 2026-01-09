package net.minecraft.dialog.action;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.ClickEvent;
import net.minecraft.util.Identifier;

public record DynamicCustomDialogAction(Identifier id, Optional additions) implements DialogAction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Identifier.CODEC.fieldOf("id").forGetter(DynamicCustomDialogAction::id), NbtCompound.CODEC.optionalFieldOf("additions").forGetter(DynamicCustomDialogAction::additions)).apply(instance, DynamicCustomDialogAction::new);
   });

   public DynamicCustomDialogAction(Identifier identifier, Optional optional) {
      this.id = identifier;
      this.additions = optional;
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public Optional createClickEvent(Map valueGetters) {
      NbtCompound nbtCompound = (NbtCompound)this.additions.map(NbtCompound::copy).orElseGet(NbtCompound::new);
      valueGetters.forEach((string, valueGetter) -> {
         nbtCompound.put(string, valueGetter.getAsNbt());
      });
      return Optional.of(new ClickEvent.Custom(this.id, Optional.of(nbtCompound)));
   }

   public Identifier id() {
      return this.id;
   }

   public Optional additions() {
      return this.additions;
   }
}
