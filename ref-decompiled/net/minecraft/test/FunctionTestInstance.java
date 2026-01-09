package net.minecraft.test;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class FunctionTestInstance extends TestInstance {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(RegistryKey.createCodec(RegistryKeys.TEST_FUNCTION).fieldOf("function").forGetter(FunctionTestInstance::getFunction), TestData.CODEC.forGetter(TestInstance::getData)).apply(instance, FunctionTestInstance::new);
   });
   private final RegistryKey function;

   public FunctionTestInstance(RegistryKey function, TestData data) {
      super(data);
      this.function = function;
   }

   public void start(TestContext context) {
      ((Consumer)context.getWorld().getRegistryManager().getOptionalEntry(this.function).map(RegistryEntry.Reference::value).orElseThrow(() -> {
         return new IllegalStateException("Trying to access missing test function: " + String.valueOf(this.function.getValue()));
      })).accept(context);
   }

   private RegistryKey getFunction() {
      return this.function;
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   protected MutableText getTypeDescription() {
      return Text.translatable("test_instance.type.function");
   }

   public Text getDescription() {
      return this.getFormattedTypeDescription().append((Text)this.getFormattedDescription("test_instance.description.function", this.function.getValue().toString())).append(this.getStructureAndBatchDescription());
   }
}
