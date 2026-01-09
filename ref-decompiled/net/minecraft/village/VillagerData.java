package net.minecraft.village;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

public record VillagerData(RegistryEntry type, RegistryEntry profession, int level) {
   public static final int MIN_LEVEL = 1;
   public static final int MAX_LEVEL = 5;
   private static final int[] LEVEL_BASE_EXPERIENCE = new int[]{0, 10, 70, 150, 250};
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Registries.VILLAGER_TYPE.getEntryCodec().fieldOf("type").orElseGet(() -> {
         return Registries.VILLAGER_TYPE.getOrThrow(VillagerType.PLAINS);
      }).forGetter((data) -> {
         return data.type;
      }), Registries.VILLAGER_PROFESSION.getEntryCodec().fieldOf("profession").orElseGet(() -> {
         return Registries.VILLAGER_PROFESSION.getOrThrow(VillagerProfession.NONE);
      }).forGetter((data) -> {
         return data.profession;
      }), Codec.INT.fieldOf("level").orElse(1).forGetter((data) -> {
         return data.level;
      })).apply(instance, VillagerData::new);
   });
   public static final PacketCodec PACKET_CODEC;

   public VillagerData(RegistryEntry registryEntry, RegistryEntry registryEntry2, int level) {
      level = Math.max(1, level);
      this.type = registryEntry;
      this.profession = registryEntry2;
      this.level = level;
   }

   public VillagerData withType(RegistryEntry type) {
      return new VillagerData(type, this.profession, this.level);
   }

   public VillagerData withType(RegistryEntryLookup.RegistryLookup registries, RegistryKey typeKey) {
      return this.withType(registries.getEntryOrThrow(typeKey));
   }

   public VillagerData withProfession(RegistryEntry profession) {
      return new VillagerData(this.type, profession, this.level);
   }

   public VillagerData withProfession(RegistryEntryLookup.RegistryLookup registries, RegistryKey professionKey) {
      return this.withProfession(registries.getEntryOrThrow(professionKey));
   }

   public VillagerData withLevel(int level) {
      return new VillagerData(this.type, this.profession, level);
   }

   public static int getLowerLevelExperience(int level) {
      return canLevelUp(level) ? LEVEL_BASE_EXPERIENCE[level - 1] : 0;
   }

   public static int getUpperLevelExperience(int level) {
      return canLevelUp(level) ? LEVEL_BASE_EXPERIENCE[level] : 0;
   }

   public static boolean canLevelUp(int level) {
      return level >= 1 && level < 5;
   }

   public RegistryEntry type() {
      return this.type;
   }

   public RegistryEntry profession() {
      return this.profession;
   }

   public int level() {
      return this.level;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.registryEntry(RegistryKeys.VILLAGER_TYPE), VillagerData::type, PacketCodecs.registryEntry(RegistryKeys.VILLAGER_PROFESSION), VillagerData::profession, PacketCodecs.VAR_INT, VillagerData::level, VillagerData::new);
   }
}
