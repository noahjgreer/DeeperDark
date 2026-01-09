package net.minecraft.test;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public abstract class TestInstance {
   public static final Codec CODEC;
   private final TestData data;

   public static MapCodec registerAndGetDefault(Registry registry) {
      register(registry, "block_based", BlockBasedTestInstance.CODEC);
      return register(registry, "function", FunctionTestInstance.CODEC);
   }

   private static MapCodec register(Registry registry, String id, MapCodec codec) {
      return (MapCodec)Registry.register(registry, (RegistryKey)RegistryKey.of(RegistryKeys.TEST_INSTANCE_TYPE, Identifier.ofVanilla(id)), codec);
   }

   protected TestInstance(TestData data) {
      this.data = data;
   }

   public abstract void start(TestContext context);

   public abstract MapCodec getCodec();

   public RegistryEntry getEnvironment() {
      return (RegistryEntry)this.data.environment();
   }

   public Identifier getStructure() {
      return this.data.structure();
   }

   public int getMaxTicks() {
      return this.data.maxTicks();
   }

   public int getSetupTicks() {
      return this.data.setupTicks();
   }

   public boolean isRequired() {
      return this.data.required();
   }

   public boolean isManualOnly() {
      return this.data.manualOnly();
   }

   public int getMaxAttempts() {
      return this.data.maxAttempts();
   }

   public int getRequiredSuccesses() {
      return this.data.requiredSuccesses();
   }

   public boolean requiresSkyAccess() {
      return this.data.skyAccess();
   }

   public BlockRotation getRotation() {
      return this.data.rotation();
   }

   protected TestData getData() {
      return this.data;
   }

   protected abstract MutableText getTypeDescription();

   public Text getDescription() {
      return this.getFormattedTypeDescription().append(this.getStructureAndBatchDescription());
   }

   protected MutableText getFormattedTypeDescription() {
      return this.getFormattedDescription("test_instance.description.type", this.getTypeDescription());
   }

   protected Text getStructureAndBatchDescription() {
      return this.getFormattedDescription("test_instance.description.structure", this.data.structure().toString()).append((Text)this.getFormattedDescription("test_instance.description.batch", ((RegistryEntry)this.data.environment()).getIdAsString()));
   }

   protected MutableText getFormattedDescription(String key, String description) {
      return this.getFormattedDescription(key, Text.literal(description));
   }

   protected MutableText getFormattedDescription(String key, MutableText description) {
      return Text.translatable(key, description.formatted(Formatting.BLUE)).append((Text)Text.literal("\n"));
   }

   static {
      CODEC = Registries.TEST_INSTANCE_TYPE.getCodec().dispatch(TestInstance::getCodec, (codec) -> {
         return codec;
      });
   }
}
