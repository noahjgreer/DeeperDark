package net.minecraft.util.math.random;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;
import java.util.concurrent.atomic.AtomicLong;

public final class RandomSeed {
   public static final long GOLDEN_RATIO_64 = -7046029254386353131L;
   public static final long SILVER_RATIO_64 = 7640891576956012809L;
   private static final HashFunction MD5_HASH = Hashing.md5();
   private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(8682522807148012L);

   @VisibleForTesting
   public static long mixStafford13(long seed) {
      seed = (seed ^ seed >>> 30) * -4658895280553007687L;
      seed = (seed ^ seed >>> 27) * -7723592293110705685L;
      return seed ^ seed >>> 31;
   }

   public static XoroshiroSeed createUnmixedXoroshiroSeed(long seed) {
      long l = seed ^ 7640891576956012809L;
      long m = l + -7046029254386353131L;
      return new XoroshiroSeed(l, m);
   }

   public static XoroshiroSeed createXoroshiroSeed(long seed) {
      return createUnmixedXoroshiroSeed(seed).mix();
   }

   public static XoroshiroSeed createXoroshiroSeed(String seed) {
      byte[] bs = MD5_HASH.hashString(seed, Charsets.UTF_8).asBytes();
      long l = Longs.fromBytes(bs[0], bs[1], bs[2], bs[3], bs[4], bs[5], bs[6], bs[7]);
      long m = Longs.fromBytes(bs[8], bs[9], bs[10], bs[11], bs[12], bs[13], bs[14], bs[15]);
      return new XoroshiroSeed(l, m);
   }

   public static long getSeed() {
      return SEED_UNIQUIFIER.updateAndGet((seedUniquifier) -> {
         return seedUniquifier * 1181783497276652981L;
      }) ^ System.nanoTime();
   }

   public static record XoroshiroSeed(long seedLo, long seedHi) {
      public XoroshiroSeed(long l, long m) {
         this.seedLo = l;
         this.seedHi = m;
      }

      public XoroshiroSeed split(long seedLo, long seedHi) {
         return new XoroshiroSeed(this.seedLo ^ seedLo, this.seedHi ^ seedHi);
      }

      public XoroshiroSeed split(XoroshiroSeed seed) {
         return this.split(seed.seedLo, seed.seedHi);
      }

      public XoroshiroSeed mix() {
         return new XoroshiroSeed(RandomSeed.mixStafford13(this.seedLo), RandomSeed.mixStafford13(this.seedHi));
      }

      public long seedLo() {
         return this.seedLo;
      }

      public long seedHi() {
         return this.seedHi;
      }
   }
}
