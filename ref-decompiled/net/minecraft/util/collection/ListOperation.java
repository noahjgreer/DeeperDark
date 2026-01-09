package net.minecraft.util.collection;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import org.slf4j.Logger;

public interface ListOperation {
   MapCodec UNLIMITED_SIZE_CODEC = createCodec(Integer.MAX_VALUE);

   static MapCodec createCodec(int maxSize) {
      return ListOperation.Mode.CODEC.dispatchMap("mode", ListOperation::getMode, (mode) -> {
         return mode.codec;
      }).validate((operation) -> {
         if (operation instanceof ReplaceSection replaceSection) {
            if (replaceSection.size().isPresent()) {
               int j = (Integer)replaceSection.size().get();
               if (j > maxSize) {
                  return DataResult.error(() -> {
                     return "Size value too large: " + j + ", max size is " + maxSize;
                  });
               }
            }
         }

         return DataResult.success(operation);
      });
   }

   Mode getMode();

   default List apply(List current, List values) {
      return this.apply(current, values, Integer.MAX_VALUE);
   }

   List apply(List current, List values, int maxSize);

   public static enum Mode implements StringIdentifiable {
      REPLACE_ALL("replace_all", ListOperation.ReplaceAll.CODEC),
      REPLACE_SECTION("replace_section", ListOperation.ReplaceSection.CODEC),
      INSERT("insert", ListOperation.Insert.CODEC),
      APPEND("append", ListOperation.Append.CODEC);

      public static final Codec CODEC = StringIdentifiable.createCodec(Mode::values);
      private final String id;
      final MapCodec codec;

      private Mode(final String id, final MapCodec codec) {
         this.id = id;
         this.codec = codec;
      }

      public MapCodec getCodec() {
         return this.codec;
      }

      public String asString() {
         return this.id;
      }

      // $FF: synthetic method
      private static Mode[] method_58199() {
         return new Mode[]{REPLACE_ALL, REPLACE_SECTION, INSERT, APPEND};
      }
   }

   public static record ReplaceSection(int offset, Optional size) implements ListOperation {
      private static final Logger LOGGER = LogUtils.getLogger();
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codecs.NON_NEGATIVE_INT.optionalFieldOf("offset", 0).forGetter(ReplaceSection::offset), Codecs.NON_NEGATIVE_INT.optionalFieldOf("size").forGetter(ReplaceSection::size)).apply(instance, ReplaceSection::new);
      });

      public ReplaceSection(int offset) {
         this(offset, Optional.empty());
      }

      public ReplaceSection(int i, Optional optional) {
         this.offset = i;
         this.size = optional;
      }

      public Mode getMode() {
         return ListOperation.Mode.REPLACE_SECTION;
      }

      public List apply(List current, List values, int maxSize) {
         int i = current.size();
         if (this.offset > i) {
            LOGGER.error("Cannot replace when offset is out of bounds");
            return current;
         } else {
            ImmutableList.Builder builder = ImmutableList.builder();
            builder.addAll(current.subList(0, this.offset));
            builder.addAll(values);
            int j = this.offset + (Integer)this.size.orElse(values.size());
            if (j < i) {
               builder.addAll(current.subList(j, i));
            }

            List list = builder.build();
            if (list.size() > maxSize) {
               LOGGER.error("Contents overflow in section replacement");
               return current;
            } else {
               return list;
            }
         }
      }

      public int offset() {
         return this.offset;
      }

      public Optional size() {
         return this.size;
      }
   }

   public static record Values(List value, ListOperation operation) {
      public Values(List list, ListOperation listOperation) {
         this.value = list;
         this.operation = listOperation;
      }

      public static Codec createCodec(Codec codec, int maxSize) {
         return RecordCodecBuilder.create((instance) -> {
            return instance.group(codec.sizeLimitedListOf(maxSize).fieldOf("values").forGetter((values) -> {
               return values.value;
            }), ListOperation.createCodec(maxSize).forGetter((values) -> {
               return values.operation;
            })).apply(instance, Values::new);
         });
      }

      public List apply(List current) {
         return this.operation.apply(current, this.value);
      }

      public List value() {
         return this.value;
      }

      public ListOperation operation() {
         return this.operation;
      }
   }

   public static class Append implements ListOperation {
      private static final Logger LOGGER = LogUtils.getLogger();
      public static final Append INSTANCE = new Append();
      public static final MapCodec CODEC = MapCodec.unit(() -> {
         return INSTANCE;
      });

      private Append() {
      }

      public Mode getMode() {
         return ListOperation.Mode.APPEND;
      }

      public List apply(List current, List values, int maxSize) {
         if (current.size() + values.size() > maxSize) {
            LOGGER.error("Contents overflow in section append");
            return current;
         } else {
            return Stream.concat(current.stream(), values.stream()).toList();
         }
      }
   }

   public static record Insert(int offset) implements ListOperation {
      private static final Logger LOGGER = LogUtils.getLogger();
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codecs.NON_NEGATIVE_INT.optionalFieldOf("offset", 0).forGetter(Insert::offset)).apply(instance, Insert::new);
      });

      public Insert(int i) {
         this.offset = i;
      }

      public Mode getMode() {
         return ListOperation.Mode.INSERT;
      }

      public List apply(List current, List values, int maxSize) {
         int i = current.size();
         if (this.offset > i) {
            LOGGER.error("Cannot insert when offset is out of bounds");
            return current;
         } else if (i + values.size() > maxSize) {
            LOGGER.error("Contents overflow in section insertion");
            return current;
         } else {
            ImmutableList.Builder builder = ImmutableList.builder();
            builder.addAll(current.subList(0, this.offset));
            builder.addAll(values);
            builder.addAll(current.subList(this.offset, i));
            return builder.build();
         }
      }

      public int offset() {
         return this.offset;
      }
   }

   public static class ReplaceAll implements ListOperation {
      public static final ReplaceAll INSTANCE = new ReplaceAll();
      public static final MapCodec CODEC = MapCodec.unit(() -> {
         return INSTANCE;
      });

      private ReplaceAll() {
      }

      public Mode getMode() {
         return ListOperation.Mode.REPLACE_ALL;
      }

      public List apply(List current, List values, int maxSize) {
         return values;
      }
   }
}
