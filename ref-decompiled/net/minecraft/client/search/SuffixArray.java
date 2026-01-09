package net.minecraft.client.search;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class SuffixArray {
   private static final boolean PRINT_COMPARISONS = Boolean.parseBoolean(System.getProperty("SuffixArray.printComparisons", "false"));
   private static final boolean PRINT_ARRAY = Boolean.parseBoolean(System.getProperty("SuffixArray.printArray", "false"));
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int field_33013 = -1;
   private static final int field_33014 = -2;
   protected final List objects = Lists.newArrayList();
   private final IntList characters = new IntArrayList();
   private final IntList textStarts = new IntArrayList();
   private IntList suffixIndexToObjectIndex = new IntArrayList();
   private IntList offsetInText = new IntArrayList();
   private int maxTextLength;

   public void add(Object object, String text) {
      this.maxTextLength = Math.max(this.maxTextLength, text.length());
      int i = this.objects.size();
      this.objects.add(object);
      this.textStarts.add(this.characters.size());

      for(int j = 0; j < text.length(); ++j) {
         this.suffixIndexToObjectIndex.add(i);
         this.offsetInText.add(j);
         this.characters.add(text.charAt(j));
      }

      this.suffixIndexToObjectIndex.add(i);
      this.offsetInText.add(text.length());
      this.characters.add(-1);
   }

   public void build() {
      int i = this.characters.size();
      int[] is = new int[i];
      int[] js = new int[i];
      int[] ks = new int[i];
      int[] ls = new int[i];
      IntComparator intComparator = (a, b) -> {
         return js[a] == js[b] ? Integer.compare(ks[a], ks[b]) : Integer.compare(js[a], js[b]);
      };
      Swapper swapper = (ix, jx) -> {
         if (ix != jx) {
            int k = js[ix];
            js[ix] = js[jx];
            js[jx] = k;
            k = ks[ix];
            ks[ix] = ks[jx];
            ks[jx] = k;
            k = ls[ix];
            ls[ix] = ls[jx];
            ls[jx] = k;
         }

      };

      int j;
      for(j = 0; j < i; ++j) {
         is[j] = this.characters.getInt(j);
      }

      j = 1;

      for(int k = Math.min(i, this.maxTextLength); j * 2 < k; j *= 2) {
         int l;
         for(l = 0; l < i; ls[l] = l++) {
            js[l] = is[l];
            ks[l] = l + j < i ? is[l + j] : -2;
         }

         Arrays.quickSort(0, i, intComparator, swapper);

         for(l = 0; l < i; ++l) {
            if (l > 0 && js[l] == js[l - 1] && ks[l] == ks[l - 1]) {
               is[ls[l]] = is[ls[l - 1]];
            } else {
               is[ls[l]] = l;
            }
         }
      }

      IntList intList = this.suffixIndexToObjectIndex;
      IntList intList2 = this.offsetInText;
      this.suffixIndexToObjectIndex = new IntArrayList(intList.size());
      this.offsetInText = new IntArrayList(intList2.size());

      for(int m = 0; m < i; ++m) {
         int n = ls[m];
         this.suffixIndexToObjectIndex.add(intList.getInt(n));
         this.offsetInText.add(intList2.getInt(n));
      }

      if (PRINT_ARRAY) {
         this.printArray();
      }

   }

   private void printArray() {
      for(int i = 0; i < this.suffixIndexToObjectIndex.size(); ++i) {
         LOGGER.debug("{} {}", i, this.getDebugString(i));
      }

      LOGGER.debug("");
   }

   private String getDebugString(int suffixIndex) {
      int i = this.offsetInText.getInt(suffixIndex);
      int j = this.textStarts.getInt(this.suffixIndexToObjectIndex.getInt(suffixIndex));
      StringBuilder stringBuilder = new StringBuilder();

      for(int k = 0; j + k < this.characters.size(); ++k) {
         if (k == i) {
            stringBuilder.append('^');
         }

         int l = this.characters.getInt(j + k);
         if (l == -1) {
            break;
         }

         stringBuilder.append((char)l);
      }

      return stringBuilder.toString();
   }

   private int compare(String string, int suffixIndex) {
      int i = this.textStarts.getInt(this.suffixIndexToObjectIndex.getInt(suffixIndex));
      int j = this.offsetInText.getInt(suffixIndex);

      for(int k = 0; k < string.length(); ++k) {
         int l = this.characters.getInt(i + j + k);
         if (l == -1) {
            return 1;
         }

         char c = string.charAt(k);
         char d = (char)l;
         if (c < d) {
            return -1;
         }

         if (c > d) {
            return 1;
         }
      }

      return 0;
   }

   public List findAll(String text) {
      int i = this.suffixIndexToObjectIndex.size();
      int j = 0;
      int k = i;

      int l;
      int m;
      while(j < k) {
         l = j + (k - j) / 2;
         m = this.compare(text, l);
         if (PRINT_COMPARISONS) {
            LOGGER.debug("comparing lower \"{}\" with {} \"{}\": {}", new Object[]{text, l, this.getDebugString(l), m});
         }

         if (m > 0) {
            j = l + 1;
         } else {
            k = l;
         }
      }

      if (j >= 0 && j < i) {
         l = j;
         k = i;

         while(j < k) {
            m = j + (k - j) / 2;
            int n = this.compare(text, m);
            if (PRINT_COMPARISONS) {
               LOGGER.debug("comparing upper \"{}\" with {} \"{}\": {}", new Object[]{text, m, this.getDebugString(m), n});
            }

            if (n >= 0) {
               j = m + 1;
            } else {
               k = m;
            }
         }

         m = j;
         IntSet intSet = new IntOpenHashSet();

         for(int o = l; o < m; ++o) {
            intSet.add(this.suffixIndexToObjectIndex.getInt(o));
         }

         int[] is = intSet.toIntArray();
         java.util.Arrays.sort(is);
         Set set = Sets.newLinkedHashSet();
         int[] var10 = is;
         int var11 = is.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            int p = var10[var12];
            set.add(this.objects.get(p));
         }

         return Lists.newArrayList(set);
      } else {
         return Collections.emptyList();
      }
   }
}
