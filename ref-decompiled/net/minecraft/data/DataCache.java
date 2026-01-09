package net.minecraft.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.GameVersion;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class DataCache {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final String HEADER = "// ";
   private final Path root;
   private final Path cachePath;
   private final String versionName;
   private final Map cachedDatas;
   private final Set dataWriters = new HashSet();
   final Set paths = new HashSet();
   private final int totalSize;
   private int totalCacheMissCount;

   private Path getPath(String providerName) {
      return this.cachePath.resolve(Hashing.sha1().hashString(providerName, StandardCharsets.UTF_8).toString());
   }

   public DataCache(Path root, Collection providerNames, GameVersion gameVersion) throws IOException {
      this.versionName = gameVersion.name();
      this.root = root;
      this.cachePath = root.resolve(".cache");
      Files.createDirectories(this.cachePath);
      Map map = new HashMap();
      int i = 0;

      CachedData cachedData;
      for(Iterator var6 = providerNames.iterator(); var6.hasNext(); i += cachedData.size()) {
         String string = (String)var6.next();
         Path path = this.getPath(string);
         this.paths.add(path);
         cachedData = parseOrCreateCache(root, path);
         map.put(string, cachedData);
      }

      this.cachedDatas = map;
      this.totalSize = i;
   }

   private static CachedData parseOrCreateCache(Path root, Path dataProviderPath) {
      if (Files.isReadable(dataProviderPath)) {
         try {
            return DataCache.CachedData.parseCache(root, dataProviderPath);
         } catch (Exception var3) {
            LOGGER.warn("Failed to parse cache {}, discarding", dataProviderPath, var3);
         }
      }

      return new CachedData("unknown", ImmutableMap.of());
   }

   public boolean isVersionDifferent(String providerName) {
      CachedData cachedData = (CachedData)this.cachedDatas.get(providerName);
      return cachedData == null || !cachedData.version.equals(this.versionName);
   }

   public CompletableFuture run(String providerName, Runner runner) {
      CachedData cachedData = (CachedData)this.cachedDatas.get(providerName);
      if (cachedData == null) {
         throw new IllegalStateException("Provider not registered: " + providerName);
      } else {
         CachedDataWriter cachedDataWriter = new CachedDataWriter(providerName, this.versionName, cachedData);
         return runner.update(cachedDataWriter).thenApply((void_) -> {
            return cachedDataWriter.finish();
         });
      }
   }

   public void store(RunResult runResult) {
      this.cachedDatas.put(runResult.providerName(), runResult.cache());
      this.dataWriters.add(runResult.providerName());
      this.totalCacheMissCount += runResult.cacheMissCount();
   }

   public void write() throws IOException {
      final Set set = new HashSet();
      this.cachedDatas.forEach((providerName, cachedData) -> {
         if (this.dataWriters.contains(providerName)) {
            Path path = this.getPath(providerName);
            Path var10001 = this.root;
            String var10003 = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
            cachedData.write(var10001, path, var10003 + "\t" + providerName);
         }

         set.addAll(cachedData.data().keySet());
      });
      set.add(this.root.resolve("version.json"));
      final MutableInt mutableInt = new MutableInt();
      final MutableInt mutableInt2 = new MutableInt();
      Files.walkFileTree(this.root, new SimpleFileVisitor() {
         public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) {
            if (DataCache.this.paths.contains(path)) {
               return FileVisitResult.CONTINUE;
            } else {
               mutableInt.increment();
               if (set.contains(path)) {
                  return FileVisitResult.CONTINUE;
               } else {
                  try {
                     Files.delete(path);
                  } catch (IOException var4) {
                     DataCache.LOGGER.warn("Failed to delete file {}", path, var4);
                  }

                  mutableInt2.increment();
                  return FileVisitResult.CONTINUE;
               }
            }
         }

         // $FF: synthetic method
         public FileVisitResult visitFile(final Object path, final BasicFileAttributes attributes) throws IOException {
            return this.visitFile((Path)path, attributes);
         }
      });
      LOGGER.info("Caching: total files: {}, old count: {}, new count: {}, removed stale: {}, written: {}", new Object[]{mutableInt, this.totalSize, set.size(), mutableInt2, this.totalCacheMissCount});
   }

   private static record CachedData(String version, ImmutableMap data) {
      final String version;

      CachedData(String string, ImmutableMap immutableMap) {
         this.version = string;
         this.data = immutableMap;
      }

      @Nullable
      public HashCode get(Path path) {
         return (HashCode)this.data.get(path);
      }

      public int size() {
         return this.data.size();
      }

      public static CachedData parseCache(Path root, Path dataProviderPath) throws IOException {
         BufferedReader bufferedReader = Files.newBufferedReader(dataProviderPath, StandardCharsets.UTF_8);

         CachedData var7;
         try {
            String string = bufferedReader.readLine();
            if (!string.startsWith("// ")) {
               throw new IllegalStateException("Missing cache file header");
            }

            String[] strings = string.substring("// ".length()).split("\t", 2);
            String string2 = strings[0];
            ImmutableMap.Builder builder = ImmutableMap.builder();
            bufferedReader.lines().forEach((line) -> {
               int i = line.indexOf(32);
               builder.put(root.resolve(line.substring(i + 1)), HashCode.fromString(line.substring(0, i)));
            });
            var7 = new CachedData(string2, builder.build());
         } catch (Throwable var9) {
            if (bufferedReader != null) {
               try {
                  bufferedReader.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (bufferedReader != null) {
            bufferedReader.close();
         }

         return var7;
      }

      public void write(Path root, Path dataProviderPath, String description) {
         try {
            BufferedWriter bufferedWriter = Files.newBufferedWriter(dataProviderPath, StandardCharsets.UTF_8);

            try {
               bufferedWriter.write("// ");
               bufferedWriter.write(this.version);
               bufferedWriter.write(9);
               bufferedWriter.write(description);
               bufferedWriter.newLine();
               UnmodifiableIterator var5 = this.data.entrySet().iterator();

               while(var5.hasNext()) {
                  Map.Entry entry = (Map.Entry)var5.next();
                  bufferedWriter.write(((HashCode)entry.getValue()).toString());
                  bufferedWriter.write(32);
                  bufferedWriter.write(root.relativize((Path)entry.getKey()).toString());
                  bufferedWriter.newLine();
               }
            } catch (Throwable var8) {
               if (bufferedWriter != null) {
                  try {
                     bufferedWriter.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (bufferedWriter != null) {
               bufferedWriter.close();
            }
         } catch (IOException var9) {
            DataCache.LOGGER.warn("Unable write cachefile {}: {}", dataProviderPath, var9);
         }

      }

      public String version() {
         return this.version;
      }

      public ImmutableMap data() {
         return this.data;
      }
   }

   private static class CachedDataWriter implements DataWriter {
      private final String providerName;
      private final CachedData oldCache;
      private final IntermediaryCache newCache;
      private final AtomicInteger cacheMissCount = new AtomicInteger();
      private volatile boolean closed;

      CachedDataWriter(String providerName, String version, CachedData oldCache) {
         this.providerName = providerName;
         this.oldCache = oldCache;
         this.newCache = new IntermediaryCache(version);
      }

      private boolean isCacheInvalid(Path path, HashCode hashCode) {
         return !Objects.equals(this.oldCache.get(path), hashCode) || !Files.exists(path, new LinkOption[0]);
      }

      public void write(Path path, byte[] data, HashCode hashCode) throws IOException {
         if (this.closed) {
            throw new IllegalStateException("Cannot write to cache as it has already been closed");
         } else {
            if (this.isCacheInvalid(path, hashCode)) {
               this.cacheMissCount.incrementAndGet();
               Files.createDirectories(path.getParent());
               Files.write(path, data, new OpenOption[0]);
            }

            this.newCache.put(path, hashCode);
         }
      }

      public RunResult finish() {
         this.closed = true;
         return new RunResult(this.providerName, this.newCache.toCachedData(), this.cacheMissCount.get());
      }
   }

   @FunctionalInterface
   public interface Runner {
      CompletableFuture update(DataWriter writer);
   }

   public static record RunResult(String providerName, CachedData cache, int cacheMissCount) {
      public RunResult(String string, CachedData cachedData, int i) {
         this.providerName = string;
         this.cache = cachedData;
         this.cacheMissCount = i;
      }

      public String providerName() {
         return this.providerName;
      }

      public CachedData cache() {
         return this.cache;
      }

      public int cacheMissCount() {
         return this.cacheMissCount;
      }
   }

   private static record IntermediaryCache(String version, ConcurrentMap data) {
      IntermediaryCache(String version) {
         this(version, new ConcurrentHashMap());
      }

      private IntermediaryCache(String string, ConcurrentMap concurrentMap) {
         this.version = string;
         this.data = concurrentMap;
      }

      public void put(Path path, HashCode hashCode) {
         this.data.put(path, hashCode);
      }

      public CachedData toCachedData() {
         return new CachedData(this.version, ImmutableMap.copyOf(this.data));
      }

      public String version() {
         return this.version;
      }

      public ConcurrentMap data() {
         return this.data;
      }
   }
}
