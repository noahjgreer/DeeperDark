package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

public record CustomModelDataComponent(List floats, List flags, List strings, List colors) {
   public static final CustomModelDataComponent DEFAULT = new CustomModelDataComponent(List.of(), List.of(), List.of(), List.of());
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codec.FLOAT.listOf().optionalFieldOf("floats", List.of()).forGetter(CustomModelDataComponent::floats), Codec.BOOL.listOf().optionalFieldOf("flags", List.of()).forGetter(CustomModelDataComponent::flags), Codec.STRING.listOf().optionalFieldOf("strings", List.of()).forGetter(CustomModelDataComponent::strings), Codecs.RGB.listOf().optionalFieldOf("colors", List.of()).forGetter(CustomModelDataComponent::colors)).apply(instance, CustomModelDataComponent::new);
   });
   public static final PacketCodec PACKET_CODEC;

   public CustomModelDataComponent(List list, List list2, List list3, List list4) {
      this.floats = list;
      this.flags = list2;
      this.strings = list3;
      this.colors = list4;
   }

   @Nullable
   private static Object getValue(List values, int index) {
      return index >= 0 && index < values.size() ? values.get(index) : null;
   }

   @Nullable
   public Float getFloat(int index) {
      return (Float)getValue(this.floats, index);
   }

   @Nullable
   public Boolean getFlag(int index) {
      return (Boolean)getValue(this.flags, index);
   }

   @Nullable
   public String getString(int index) {
      return (String)getValue(this.strings, index);
   }

   @Nullable
   public Integer getColor(int index) {
      return (Integer)getValue(this.colors, index);
   }

   public List floats() {
      return this.floats;
   }

   public List flags() {
      return this.flags;
   }

   public List strings() {
      return this.strings;
   }

   public List colors() {
      return this.colors;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.FLOAT.collect(PacketCodecs.toList()), CustomModelDataComponent::floats, PacketCodecs.BOOLEAN.collect(PacketCodecs.toList()), CustomModelDataComponent::flags, PacketCodecs.STRING.collect(PacketCodecs.toList()), CustomModelDataComponent::strings, PacketCodecs.INTEGER.collect(PacketCodecs.toList()), CustomModelDataComponent::colors, CustomModelDataComponent::new);
   }
}
