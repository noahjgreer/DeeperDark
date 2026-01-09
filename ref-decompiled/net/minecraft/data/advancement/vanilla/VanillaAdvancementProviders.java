package net.minecraft.data.advancement.vanilla;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.advancement.AdvancementProvider;

public class VanillaAdvancementProviders {
   public static AdvancementProvider createVanillaProvider(DataOutput output, CompletableFuture registriesFuture) {
      return new AdvancementProvider(output, registriesFuture, List.of(new VanillaEndTabAdvancementGenerator(), new VanillaHusbandryTabAdvancementGenerator(), new VanillaAdventureTabAdvancementGenerator(), new VanillaNetherTabAdvancementGenerator(), new VanillaStoryTabAdvancementGenerator()));
   }
}
