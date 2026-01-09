package net.minecraft.test;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public record TestData(Object environment, Identifier structure, int maxTicks, int setupTicks, boolean required, BlockRotation rotation, boolean manualOnly, int maxAttempts, int requiredSuccesses, boolean skyAccess) {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(TestEnvironmentDefinition.ENTRY_CODEC.fieldOf("environment").forGetter(TestData::environment), Identifier.CODEC.fieldOf("structure").forGetter(TestData::structure), Codecs.POSITIVE_INT.fieldOf("max_ticks").forGetter(TestData::maxTicks), Codecs.NON_NEGATIVE_INT.optionalFieldOf("setup_ticks", 0).forGetter(TestData::setupTicks), Codec.BOOL.optionalFieldOf("required", true).forGetter(TestData::required), BlockRotation.CODEC.optionalFieldOf("rotation", BlockRotation.NONE).forGetter(TestData::rotation), Codec.BOOL.optionalFieldOf("manual_only", false).forGetter(TestData::manualOnly), Codecs.POSITIVE_INT.optionalFieldOf("max_attempts", 1).forGetter(TestData::maxAttempts), Codecs.POSITIVE_INT.optionalFieldOf("required_successes", 1).forGetter(TestData::requiredSuccesses), Codec.BOOL.optionalFieldOf("sky_access", false).forGetter(TestData::skyAccess)).apply(instance, TestData::new);
   });

   public TestData(Object environment, Identifier structure, int maxTicks, int setupTicks, boolean required, BlockRotation rotation) {
      this(environment, structure, maxTicks, setupTicks, required, rotation, false, 1, 1, false);
   }

   public TestData(Object environment, Identifier structure, int maxTicks, int setupTicks, boolean required) {
      this(environment, structure, maxTicks, setupTicks, required, BlockRotation.NONE);
   }

   public TestData(Object object, Identifier identifier, int i, int j, boolean bl, BlockRotation blockRotation, boolean bl2, int k, int l, boolean bl3) {
      this.environment = object;
      this.structure = identifier;
      this.maxTicks = i;
      this.setupTicks = j;
      this.required = bl;
      this.rotation = blockRotation;
      this.manualOnly = bl2;
      this.maxAttempts = k;
      this.requiredSuccesses = l;
      this.skyAccess = bl3;
   }

   public TestData applyToEnvironment(Function environmentFunction) {
      return new TestData(environmentFunction.apply(this.environment), this.structure, this.maxTicks, this.setupTicks, this.required, this.rotation, this.manualOnly, this.maxAttempts, this.requiredSuccesses, this.skyAccess);
   }

   public Object environment() {
      return this.environment;
   }

   public Identifier structure() {
      return this.structure;
   }

   public int maxTicks() {
      return this.maxTicks;
   }

   public int setupTicks() {
      return this.setupTicks;
   }

   public boolean required() {
      return this.required;
   }

   public BlockRotation rotation() {
      return this.rotation;
   }

   public boolean manualOnly() {
      return this.manualOnly;
   }

   public int maxAttempts() {
      return this.maxAttempts;
   }

   public int requiredSuccesses() {
      return this.requiredSuccesses;
   }

   public boolean skyAccess() {
      return this.skyAccess;
   }
}
