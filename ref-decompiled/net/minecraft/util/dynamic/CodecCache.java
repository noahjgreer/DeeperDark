package net.minecraft.util.dynamic;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.NbtElement;

public class CodecCache {
   final LoadingCache cache;

   public CodecCache(int size) {
      this.cache = CacheBuilder.newBuilder().maximumSize((long)size).concurrencyLevel(1).softValues().build(new CacheLoader(this) {
         public DataResult load(Key key) {
            return key.encode();
         }

         // $FF: synthetic method
         public Object load(final Object key) throws Exception {
            return this.load((Key)key);
         }
      });
   }

   public Codec wrap(final Codec codec) {
      return new Codec() {
         public DataResult decode(DynamicOps ops, Object input) {
            return codec.decode(ops, input);
         }

         public DataResult encode(Object value, DynamicOps ops, Object prefix) {
            return ((DataResult)CodecCache.this.cache.getUnchecked(new Key(codec, value, ops))).map((object) -> {
               if (object instanceof NbtElement nbtElement) {
                  return nbtElement.copy();
               } else {
                  return object;
               }
            });
         }
      };
   }

   private static record Key(Codec codec, Object value, DynamicOps ops) {
      Key(Codec codec, Object object, DynamicOps dynamicOps) {
         this.codec = codec;
         this.value = object;
         this.ops = dynamicOps;
      }

      public DataResult encode() {
         return this.codec.encodeStart(this.ops, this.value);
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof Key)) {
            return false;
         } else {
            Key key = (Key)o;
            return this.codec == key.codec && this.value.equals(key.value) && this.ops.equals(key.ops);
         }
      }

      public int hashCode() {
         int i = System.identityHashCode(this.codec);
         i = 31 * i + this.value.hashCode();
         i = 31 * i + this.ops.hashCode();
         return i;
      }

      public Codec codec() {
         return this.codec;
      }

      public Object value() {
         return this.value;
      }

      public DynamicOps ops() {
         return this.ops;
      }
   }
}
