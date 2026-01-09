package net.minecraft.data.tag.vanilla;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.tag.SimpleTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DialogTags;

public class VanillaDialogTagProvider extends SimpleTagProvider {
   public VanillaDialogTagProvider(DataOutput output, CompletableFuture registriesFuture) {
      super(output, RegistryKeys.DIALOG, registriesFuture);
   }

   protected void configure(RegistryWrapper.WrapperLookup registries) {
      this.builder(DialogTags.PAUSE_SCREEN_ADDITIONS);
      this.builder(DialogTags.QUICK_ACTIONS);
   }
}
