package net.minecraft.util;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.logging.LogWriter;
import net.minecraft.util.path.CacheFiles;
import net.minecraft.util.path.PathUtil;
import net.minecraft.util.thread.SimpleConsecutiveExecutor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class Downloader implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_RETAINED_CACHE_FILES = 20;
   private final Path directory;
   private final LogWriter logWriter;
   private final SimpleConsecutiveExecutor executor = new SimpleConsecutiveExecutor(Util.getDownloadWorkerExecutor(), "download-queue");

   public Downloader(Path directory) throws IOException {
      this.directory = directory;
      PathUtil.createDirectories(directory);
      this.logWriter = LogWriter.create(Downloader.LogEntry.CODEC, directory.resolve("log.json"));
      CacheFiles.clear(directory, 20);
   }

   private DownloadResult download(Config config, Map entries) {
      DownloadResult downloadResult = new DownloadResult();
      entries.forEach((id, entry) -> {
         Path path = this.directory.resolve(id.toString());
         Path path2 = null;

         try {
            path2 = NetworkUtils.download(path, entry.url, config.headers, config.hashFunction, entry.hash, config.maxSize, config.proxy, config.listener);
            downloadResult.downloaded.put(id, path2);
         } catch (Exception var9) {
            LOGGER.error("Failed to download {}", entry.url, var9);
            downloadResult.failed.add(id);
         }

         try {
            this.logWriter.write(new LogEntry(id, entry.url.toString(), Instant.now(), Optional.ofNullable(entry.hash).map(HashCode::toString), path2 != null ? this.getFileInfo(path2) : Either.left("download_failed")));
         } catch (Exception var8) {
            LOGGER.error("Failed to log download of {}", entry.url, var8);
         }

      });
      return downloadResult;
   }

   private Either getFileInfo(Path path) {
      try {
         long l = Files.size(path);
         Path path2 = this.directory.relativize(path);
         return Either.right(new FileInfo(path2.toString(), l));
      } catch (IOException var5) {
         LOGGER.error("Failed to get file size of {}", path, var5);
         return Either.left("no_access");
      }
   }

   public CompletableFuture downloadAsync(Config config, Map entries) {
      Supplier var10000 = () -> {
         return this.download(config, entries);
      };
      SimpleConsecutiveExecutor var10001 = this.executor;
      Objects.requireNonNull(var10001);
      return CompletableFuture.supplyAsync(var10000, var10001::send);
   }

   public void close() throws IOException {
      this.executor.close();
      this.logWriter.close();
   }

   static record LogEntry(UUID id, String url, Instant time, Optional hash, Either errorOrFileInfo) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Uuids.STRING_CODEC.fieldOf("id").forGetter(LogEntry::id), Codec.STRING.fieldOf("url").forGetter(LogEntry::url), Codecs.INSTANT.fieldOf("time").forGetter(LogEntry::time), Codec.STRING.optionalFieldOf("hash").forGetter(LogEntry::hash), Codec.mapEither(Codec.STRING.fieldOf("error"), Downloader.FileInfo.CODEC.fieldOf("file")).forGetter(LogEntry::errorOrFileInfo)).apply(instance, LogEntry::new);
      });

      LogEntry(UUID uUID, String string, Instant instant, Optional optional, Either either) {
         this.id = uUID;
         this.url = string;
         this.time = instant;
         this.hash = optional;
         this.errorOrFileInfo = either;
      }

      public UUID id() {
         return this.id;
      }

      public String url() {
         return this.url;
      }

      public Instant time() {
         return this.time;
      }

      public Optional hash() {
         return this.hash;
      }

      public Either errorOrFileInfo() {
         return this.errorOrFileInfo;
      }
   }

   public static record DownloadResult(Map downloaded, Set failed) {
      final Map downloaded;
      final Set failed;

      public DownloadResult() {
         this(new HashMap(), new HashSet());
      }

      public DownloadResult(Map map, Set set) {
         this.downloaded = map;
         this.failed = set;
      }

      public Map downloaded() {
         return this.downloaded;
      }

      public Set failed() {
         return this.failed;
      }
   }

   public static record Config(HashFunction hashFunction, int maxSize, Map headers, Proxy proxy, NetworkUtils.DownloadListener listener) {
      final HashFunction hashFunction;
      final int maxSize;
      final Map headers;
      final Proxy proxy;
      final NetworkUtils.DownloadListener listener;

      public Config(HashFunction hashFunction, int i, Map map, Proxy proxy, NetworkUtils.DownloadListener downloadListener) {
         this.hashFunction = hashFunction;
         this.maxSize = i;
         this.headers = map;
         this.proxy = proxy;
         this.listener = downloadListener;
      }

      public HashFunction hashFunction() {
         return this.hashFunction;
      }

      public int maxSize() {
         return this.maxSize;
      }

      public Map headers() {
         return this.headers;
      }

      public Proxy proxy() {
         return this.proxy;
      }

      public NetworkUtils.DownloadListener listener() {
         return this.listener;
      }
   }

   private static record FileInfo(String name, long size) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.STRING.fieldOf("name").forGetter(FileInfo::name), Codec.LONG.fieldOf("size").forGetter(FileInfo::size)).apply(instance, FileInfo::new);
      });

      FileInfo(String string, long l) {
         this.name = string;
         this.size = l;
      }

      public String name() {
         return this.name;
      }

      public long size() {
         return this.size;
      }
   }

   public static record DownloadEntry(URL url, @Nullable HashCode hash) {
      final URL url;
      @Nullable
      final HashCode hash;

      public DownloadEntry(URL uRL, @Nullable HashCode hashCode) {
         this.url = uRL;
         this.hash = hashCode;
      }

      public URL url() {
         return this.url;
      }

      @Nullable
      public HashCode hash() {
         return this.hash;
      }
   }
}
