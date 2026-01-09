package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.entity.VariantSelectorProvider;
import net.minecraft.entity.spawn.SpawnConditionSelectors;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.util.ModelAndTexture;
import net.minecraft.util.StringIdentifiable;

public record CowVariant(ModelAndTexture modelAndTexture, SpawnConditionSelectors spawnConditions) implements VariantSelectorProvider {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(ModelAndTexture.createMapCodec(CowVariant.Model.CODEC, CowVariant.Model.NORMAL).forGetter(CowVariant::modelAndTexture), SpawnConditionSelectors.CODEC.fieldOf("spawn_conditions").forGetter(CowVariant::spawnConditions)).apply(instance, CowVariant::new);
   });
   public static final Codec NETWORK_CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(ModelAndTexture.createMapCodec(CowVariant.Model.CODEC, CowVariant.Model.NORMAL).forGetter(CowVariant::modelAndTexture)).apply(instance, CowVariant::new);
   });
   public static final Codec ENTRY_CODEC;
   public static final PacketCodec ENTRY_PACKET_CODEC;

   private CowVariant(ModelAndTexture modelAndTexture) {
      this(modelAndTexture, SpawnConditionSelectors.EMPTY);
   }

   public CowVariant(ModelAndTexture modelAndTexture, SpawnConditionSelectors spawnConditionSelectors) {
      this.modelAndTexture = modelAndTexture;
      this.spawnConditions = spawnConditionSelectors;
   }

   public List getSelectors() {
      return this.spawnConditions.selectors();
   }

   public ModelAndTexture modelAndTexture() {
      return this.modelAndTexture;
   }

   public SpawnConditionSelectors spawnConditions() {
      return this.spawnConditions;
   }

   static {
      ENTRY_CODEC = RegistryFixedCodec.of(RegistryKeys.COW_VARIANT);
      ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.COW_VARIANT);
   }

   public static enum Model implements StringIdentifiable {
      NORMAL("normal"),
      COLD("cold"),
      WARM("warm");

      public static final Codec CODEC = StringIdentifiable.createCodec(Model::values);
      private final String id;

      private Model(final String id) {
         this.id = id;
      }

      public String asString() {
         return this.id;
      }

      // $FF: synthetic method
      private static Model[] method_67352() {
         return new Model[]{NORMAL, COLD, WARM};
      }
   }
}
