package net.minecraft.resource.featuretoggle;

import it.unimi.dsi.fastutil.HashCommon;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.jetbrains.annotations.Nullable;

public final class FeatureSet {
   private static final FeatureSet EMPTY = new FeatureSet((FeatureUniverse)null, 0L);
   public static final int MAX_FEATURE_FLAGS = 64;
   @Nullable
   private final FeatureUniverse universe;
   private final long featuresMask;

   private FeatureSet(@Nullable FeatureUniverse universe, long featuresMask) {
      this.universe = universe;
      this.featuresMask = featuresMask;
   }

   static FeatureSet of(FeatureUniverse universe, Collection features) {
      if (features.isEmpty()) {
         return EMPTY;
      } else {
         long l = combineMask(universe, 0L, features);
         return new FeatureSet(universe, l);
      }
   }

   public static FeatureSet empty() {
      return EMPTY;
   }

   public static FeatureSet of(FeatureFlag feature) {
      return new FeatureSet(feature.universe, feature.mask);
   }

   public static FeatureSet of(FeatureFlag feature1, FeatureFlag... features) {
      long l = features.length == 0 ? feature1.mask : combineMask(feature1.universe, feature1.mask, Arrays.asList(features));
      return new FeatureSet(feature1.universe, l);
   }

   private static long combineMask(FeatureUniverse universe, long featuresMask, Iterable newFeatures) {
      FeatureFlag featureFlag;
      for(Iterator var4 = newFeatures.iterator(); var4.hasNext(); featuresMask |= featureFlag.mask) {
         featureFlag = (FeatureFlag)var4.next();
         if (universe != featureFlag.universe) {
            String var10002 = String.valueOf(universe);
            throw new IllegalStateException("Mismatched feature universe, expected '" + var10002 + "', but got '" + String.valueOf(featureFlag.universe) + "'");
         }
      }

      return featuresMask;
   }

   public boolean contains(FeatureFlag feature) {
      if (this.universe != feature.universe) {
         return false;
      } else {
         return (this.featuresMask & feature.mask) != 0L;
      }
   }

   public boolean isEmpty() {
      return this.equals(EMPTY);
   }

   public boolean isSubsetOf(FeatureSet features) {
      if (this.universe == null) {
         return true;
      } else if (this.universe != features.universe) {
         return false;
      } else {
         return (this.featuresMask & ~features.featuresMask) == 0L;
      }
   }

   public boolean intersects(FeatureSet features) {
      if (this.universe != null && features.universe != null && this.universe == features.universe) {
         return (this.featuresMask & features.featuresMask) != 0L;
      } else {
         return false;
      }
   }

   public FeatureSet combine(FeatureSet features) {
      if (this.universe == null) {
         return features;
      } else if (features.universe == null) {
         return this;
      } else if (this.universe != features.universe) {
         String var10002 = String.valueOf(this.universe);
         throw new IllegalArgumentException("Mismatched set elements: '" + var10002 + "' != '" + String.valueOf(features.universe) + "'");
      } else {
         return new FeatureSet(this.universe, this.featuresMask | features.featuresMask);
      }
   }

   public FeatureSet subtract(FeatureSet features) {
      if (this.universe != null && features.universe != null) {
         if (this.universe != features.universe) {
            String var10002 = String.valueOf(this.universe);
            throw new IllegalArgumentException("Mismatched set elements: '" + var10002 + "' != '" + String.valueOf(features.universe) + "'");
         } else {
            long l = this.featuresMask & ~features.featuresMask;
            return l == 0L ? EMPTY : new FeatureSet(this.universe, l);
         }
      } else {
         return this;
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof FeatureSet) {
            FeatureSet featureSet = (FeatureSet)o;
            if (this.universe == featureSet.universe && this.featuresMask == featureSet.featuresMask) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return (int)HashCommon.mix(this.featuresMask);
   }
}
