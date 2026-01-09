package net.minecraft.datafixer;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;

public enum DataFixTypes {
   LEVEL(TypeReferences.LEVEL),
   LEVEL_SUMMARY(TypeReferences.LIGHTWEIGHT_LEVEL),
   PLAYER(TypeReferences.PLAYER),
   CHUNK(TypeReferences.CHUNK),
   HOTBAR(TypeReferences.HOTBAR),
   OPTIONS(TypeReferences.OPTIONS),
   STRUCTURE(TypeReferences.STRUCTURE),
   STATS(TypeReferences.STATS),
   SAVED_DATA_COMMAND_STORAGE(TypeReferences.SAVED_DATA_COMMAND_STORAGE),
   SAVED_DATA_FORCED_CHUNKS(TypeReferences.TICKETS_SAVED_DATA),
   SAVED_DATA_MAP_DATA(TypeReferences.SAVED_DATA_MAP_DATA),
   SAVED_DATA_MAP_INDEX(TypeReferences.SAVED_DATA_IDCOUNTS),
   SAVED_DATA_RAIDS(TypeReferences.SAVED_DATA_RAIDS),
   SAVED_DATA_RANDOM_SEQUENCES(TypeReferences.SAVED_DATA_RANDOM_SEQUENCES),
   SAVED_DATA_SCOREBOARD(TypeReferences.SAVED_DATA_SCOREBOARD),
   SAVED_DATA_STRUCTURE_FEATURE_INDICES(TypeReferences.SAVED_DATA_STRUCTURE_FEATURE_INDICES),
   ADVANCEMENTS(TypeReferences.ADVANCEMENTS),
   POI_CHUNK(TypeReferences.POI_CHUNK),
   WORLD_GEN_SETTINGS(TypeReferences.WORLD_GEN_SETTINGS),
   ENTITY_CHUNK(TypeReferences.ENTITY_CHUNK);

   public static final Set REQUIRED_TYPES = Set.of(LEVEL_SUMMARY.typeReference);
   private final DSL.TypeReference typeReference;

   private DataFixTypes(final DSL.TypeReference typeReference) {
      this.typeReference = typeReference;
   }

   static int getSaveVersionId() {
      return SharedConstants.getGameVersion().dataVersion().id();
   }

   public Codec createDataFixingCodec(final Codec baseCodec, final DataFixer dataFixer, final int currentDataVersion) {
      return new Codec() {
         public DataResult encode(Object input, DynamicOps ops, Object prefix) {
            return baseCodec.encode(input, ops, prefix).flatMap((encoded) -> {
               return ops.mergeToMap(encoded, ops.createString("DataVersion"), ops.createInt(DataFixTypes.getSaveVersionId()));
            });
         }

         public DataResult decode(DynamicOps ops, Object input) {
            DataResult var10000 = ops.get(input, "DataVersion");
            Objects.requireNonNull(ops);
            int i = (Integer)var10000.flatMap(ops::getNumberValue).map(Number::intValue).result().orElse(currentDataVersion);
            Dynamic dynamic = new Dynamic(ops, ops.remove(input, "DataVersion"));
            Dynamic dynamic2 = DataFixTypes.this.update(dataFixer, dynamic, i);
            return baseCodec.decode(dynamic2);
         }
      };
   }

   public Dynamic update(DataFixer dataFixer, Dynamic dynamic, int oldVersion, int newVersion) {
      return dataFixer.update(this.typeReference, dynamic, oldVersion, newVersion);
   }

   public Dynamic update(DataFixer dataFixer, Dynamic dynamic, int oldVersion) {
      return this.update(dataFixer, dynamic, oldVersion, getSaveVersionId());
   }

   public NbtCompound update(DataFixer dataFixer, NbtCompound nbt, int oldVersion, int newVersion) {
      return (NbtCompound)this.update(dataFixer, new Dynamic(NbtOps.INSTANCE, nbt), oldVersion, newVersion).getValue();
   }

   public NbtCompound update(DataFixer dataFixer, NbtCompound nbt, int oldVersion) {
      return this.update(dataFixer, nbt, oldVersion, getSaveVersionId());
   }

   // $FF: synthetic method
   private static DataFixTypes[] method_36589() {
      return new DataFixTypes[]{LEVEL, LEVEL_SUMMARY, PLAYER, CHUNK, HOTBAR, OPTIONS, STRUCTURE, STATS, SAVED_DATA_COMMAND_STORAGE, SAVED_DATA_FORCED_CHUNKS, SAVED_DATA_MAP_DATA, SAVED_DATA_MAP_INDEX, SAVED_DATA_RAIDS, SAVED_DATA_RANDOM_SEQUENCES, SAVED_DATA_SCOREBOARD, SAVED_DATA_STRUCTURE_FEATURE_INDICES, ADVANCEMENTS, POI_CHUNK, WORLD_GEN_SETTINGS, ENTITY_CHUNK};
   }
}
