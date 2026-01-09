package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.DataResult;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ParticleEffectArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("foo", "foo:bar", "particle{foo:bar}");
   public static final DynamicCommandExceptionType UNKNOWN_PARTICLE_EXCEPTION = new DynamicCommandExceptionType((id) -> {
      return Text.stringifiedTranslatable("particle.notFound", id);
   });
   public static final DynamicCommandExceptionType INVALID_OPTIONS_EXCEPTION = new DynamicCommandExceptionType((error) -> {
      return Text.stringifiedTranslatable("particle.invalidOptions", error);
   });
   private final RegistryWrapper.WrapperLookup registries;
   private static final StringNbtReader SNBT_READER;

   public ParticleEffectArgumentType(CommandRegistryAccess registryAccess) {
      this.registries = registryAccess;
   }

   public static ParticleEffectArgumentType particleEffect(CommandRegistryAccess registryAccess) {
      return new ParticleEffectArgumentType(registryAccess);
   }

   public static ParticleEffect getParticle(CommandContext context, String name) {
      return (ParticleEffect)context.getArgument(name, ParticleEffect.class);
   }

   public ParticleEffect parse(StringReader stringReader) throws CommandSyntaxException {
      return readParameters(stringReader, this.registries);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   public static ParticleEffect readParameters(StringReader reader, RegistryWrapper.WrapperLookup registries) throws CommandSyntaxException {
      ParticleType particleType = getType(reader, registries.getOrThrow(RegistryKeys.PARTICLE_TYPE));
      return readParameters(SNBT_READER, reader, particleType, registries);
   }

   private static ParticleType getType(StringReader reader, RegistryWrapper registryWrapper) throws CommandSyntaxException {
      Identifier identifier = Identifier.fromCommandInput(reader);
      RegistryKey registryKey = RegistryKey.of(RegistryKeys.PARTICLE_TYPE, identifier);
      return (ParticleType)((RegistryEntry.Reference)registryWrapper.getOptional(registryKey).orElseThrow(() -> {
         return UNKNOWN_PARTICLE_EXCEPTION.createWithContext(reader, identifier);
      })).value();
   }

   private static ParticleEffect readParameters(StringNbtReader snbtReader, StringReader reader, ParticleType particleType, RegistryWrapper.WrapperLookup registries) throws CommandSyntaxException {
      RegistryOps registryOps = registries.getOps(snbtReader.getOps());
      Object object;
      if (reader.canRead() && reader.peek() == '{') {
         object = snbtReader.readAsArgument(reader);
      } else {
         object = registryOps.emptyMap();
      }

      DataResult var10000 = particleType.getCodec().codec().parse(registryOps, object);
      DynamicCommandExceptionType var10001 = INVALID_OPTIONS_EXCEPTION;
      Objects.requireNonNull(var10001);
      return (ParticleEffect)var10000.getOrThrow(var10001::create);
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      RegistryWrapper.Impl impl = this.registries.getOrThrow(RegistryKeys.PARTICLE_TYPE);
      return CommandSource.suggestIdentifiers(impl.streamKeys().map(RegistryKey::getValue), builder);
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }

   static {
      SNBT_READER = StringNbtReader.fromOps(NbtOps.INSTANCE);
   }
}
