package net.minecraft.data.tag.vanilla;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.tag.SimpleTagProvider;
import net.minecraft.item.Instruments;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.InstrumentTags;

public class VanillaInstrumentTagProvider extends SimpleTagProvider {
   public VanillaInstrumentTagProvider(DataOutput output, CompletableFuture registriesFuture) {
      super(output, RegistryKeys.INSTRUMENT, registriesFuture);
   }

   protected void configure(RegistryWrapper.WrapperLookup registries) {
      this.builder(InstrumentTags.REGULAR_GOAT_HORNS).add((Object)Instruments.PONDER_GOAT_HORN).add((Object)Instruments.SING_GOAT_HORN).add((Object)Instruments.SEEK_GOAT_HORN).add((Object)Instruments.FEEL_GOAT_HORN);
      this.builder(InstrumentTags.SCREAMING_GOAT_HORNS).add((Object)Instruments.ADMIRE_GOAT_HORN).add((Object)Instruments.CALL_GOAT_HORN).add((Object)Instruments.YEARN_GOAT_HORN).add((Object)Instruments.DREAM_GOAT_HORN);
      this.builder(InstrumentTags.GOAT_HORNS).addTag(InstrumentTags.REGULAR_GOAT_HORNS).addTag(InstrumentTags.SCREAMING_GOAT_HORNS);
   }
}
