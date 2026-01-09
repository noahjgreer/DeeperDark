package net.minecraft.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.filter.FilteredMessage;

public record RawFilteredPair(Object raw, Optional filtered) {
   public RawFilteredPair(Object object, Optional optional) {
      this.raw = object;
      this.filtered = optional;
   }

   public static Codec createCodec(Codec baseCodec) {
      Codec codec = RecordCodecBuilder.create((instance) -> {
         return instance.group(baseCodec.fieldOf("raw").forGetter(RawFilteredPair::raw), baseCodec.optionalFieldOf("filtered").forGetter(RawFilteredPair::filtered)).apply(instance, RawFilteredPair::new);
      });
      Codec codec2 = baseCodec.xmap(RawFilteredPair::of, RawFilteredPair::raw);
      return Codec.withAlternative(codec, codec2);
   }

   public static PacketCodec createPacketCodec(PacketCodec basePacketCodec) {
      return PacketCodec.tuple(basePacketCodec, RawFilteredPair::raw, basePacketCodec.collect(PacketCodecs::optional), RawFilteredPair::filtered, RawFilteredPair::new);
   }

   public static RawFilteredPair of(Object raw) {
      return new RawFilteredPair(raw, Optional.empty());
   }

   public static RawFilteredPair of(FilteredMessage message) {
      return new RawFilteredPair(message.raw(), message.isFiltered() ? Optional.of(message.getString()) : Optional.empty());
   }

   public Object get(boolean shouldFilter) {
      return shouldFilter ? this.filtered.orElse(this.raw) : this.raw;
   }

   public RawFilteredPair map(Function mapper) {
      return new RawFilteredPair(mapper.apply(this.raw), this.filtered.map(mapper));
   }

   public Optional resolve(Function resolver) {
      Optional optional = (Optional)resolver.apply(this.raw);
      if (optional.isEmpty()) {
         return Optional.empty();
      } else if (this.filtered.isPresent()) {
         Optional optional2 = (Optional)resolver.apply(this.filtered.get());
         return optional2.isEmpty() ? Optional.empty() : Optional.of(new RawFilteredPair(optional.get(), optional2));
      } else {
         return Optional.of(new RawFilteredPair(optional.get(), Optional.empty()));
      }
   }

   public Object raw() {
      return this.raw;
   }

   public Optional filtered() {
      return this.filtered;
   }
}
