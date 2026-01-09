package net.minecraft.server.filter;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.SharedConstants;
import net.minecraft.network.message.FilterMask;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.thread.SimpleConsecutiveExecutor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class AbstractTextFilterer implements AutoCloseable {
   protected static final Logger LOGGER = LogUtils.getLogger();
   private static final AtomicInteger WORKER_ID = new AtomicInteger(1);
   private static final ThreadFactory THREAD_FACTORY = (runnable) -> {
      Thread thread = new Thread(runnable);
      thread.setName("Chat-Filter-Worker-" + WORKER_ID.getAndIncrement());
      return thread;
   };
   private final URL url;
   private final MessageEncoder messageEncoder;
   final HashIgnorer hashIgnorer;
   final ExecutorService threadPool;

   protected static ExecutorService newThreadPool(int threadCount) {
      return Executors.newFixedThreadPool(threadCount, THREAD_FACTORY);
   }

   protected AbstractTextFilterer(URL url, MessageEncoder messageEncoder, HashIgnorer hashIgnorer, ExecutorService threadPool) {
      this.hashIgnorer = hashIgnorer;
      this.threadPool = threadPool;
      this.url = url;
      this.messageEncoder = messageEncoder;
   }

   protected static URL resolveEndpoint(URI uri, @Nullable JsonObject endpoints, String key, String defaultPath) throws MalformedURLException {
      String string = getEndpointPath(endpoints, key, defaultPath);
      return uri.resolve("/" + string).toURL();
   }

   protected static String getEndpointPath(@Nullable JsonObject endpoints, String key, String defaultPath) {
      return endpoints != null ? JsonHelper.getString(endpoints, key, defaultPath) : defaultPath;
   }

   @Nullable
   public static AbstractTextFilterer createTextFilter(ServerPropertiesHandler properties) {
      String string = properties.textFilteringConfig;
      if (StringHelper.isBlank(string)) {
         return null;
      } else {
         AbstractTextFilterer var10000;
         switch (properties.textFilteringVersion) {
            case 0:
               var10000 = V0TextFilterer.load(string);
               break;
            case 1:
               var10000 = V1TextFilterer.load(string);
               break;
            default:
               LOGGER.warn("Could not create text filter - unsupported text filtering version used");
               var10000 = null;
         }

         return var10000;
      }
   }

   protected CompletableFuture filter(GameProfile profile, String raw, HashIgnorer hashIgnorer, Executor executor) {
      return raw.isEmpty() ? CompletableFuture.completedFuture(FilteredMessage.EMPTY) : CompletableFuture.supplyAsync(() -> {
         JsonObject jsonObject = this.messageEncoder.encode(profile, raw);

         try {
            JsonObject jsonObject2 = this.request(jsonObject, this.url);
            return this.filter(raw, hashIgnorer, jsonObject2);
         } catch (Exception var6) {
            LOGGER.warn("Failed to validate message '{}'", raw, var6);
            return FilteredMessage.censored(raw);
         }
      }, executor);
   }

   protected abstract FilteredMessage filter(String raw, HashIgnorer hashIgnorer, JsonObject response);

   protected FilterMask createFilterMask(String raw, JsonArray redactedTextIndex, HashIgnorer hashIgnorer) {
      if (redactedTextIndex.isEmpty()) {
         return FilterMask.PASS_THROUGH;
      } else if (hashIgnorer.shouldIgnore(raw, redactedTextIndex.size())) {
         return FilterMask.FULLY_FILTERED;
      } else {
         FilterMask filterMask = new FilterMask(raw.length());

         for(int i = 0; i < redactedTextIndex.size(); ++i) {
            filterMask.markFiltered(redactedTextIndex.get(i).getAsInt());
         }

         return filterMask;
      }
   }

   public void close() {
      this.threadPool.shutdownNow();
   }

   protected void discardRestOfInput(InputStream stream) throws IOException {
      byte[] bs = new byte[1024];

      while(stream.read(bs) != -1) {
      }

   }

   private JsonObject request(JsonObject request, URL url) throws IOException {
      HttpURLConnection httpURLConnection = this.openConnection(request, url);
      InputStream inputStream = httpURLConnection.getInputStream();

      JsonObject var5;
      label90: {
         try {
            if (httpURLConnection.getResponseCode() == 204) {
               var5 = new JsonObject();
               break label90;
            }

            try {
               var5 = LenientJsonParser.parse((Reader)(new InputStreamReader(inputStream, StandardCharsets.UTF_8))).getAsJsonObject();
            } finally {
               this.discardRestOfInput(inputStream);
            }
         } catch (Throwable var12) {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (Throwable var10) {
                  var12.addSuppressed(var10);
               }
            }

            throw var12;
         }

         if (inputStream != null) {
            inputStream.close();
         }

         return var5;
      }

      if (inputStream != null) {
         inputStream.close();
      }

      return var5;
   }

   protected HttpURLConnection openConnection(JsonObject request, URL url) throws IOException {
      HttpURLConnection httpURLConnection = this.openConnection(url);
      this.addAuthentication(httpURLConnection);
      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream(), StandardCharsets.UTF_8);

      try {
         JsonWriter jsonWriter = new JsonWriter(outputStreamWriter);

         try {
            Streams.write(request, jsonWriter);
         } catch (Throwable var10) {
            try {
               jsonWriter.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }

            throw var10;
         }

         jsonWriter.close();
      } catch (Throwable var11) {
         try {
            outputStreamWriter.close();
         } catch (Throwable var8) {
            var11.addSuppressed(var8);
         }

         throw var11;
      }

      outputStreamWriter.close();
      int i = httpURLConnection.getResponseCode();
      if (i >= 200 && i < 300) {
         return httpURLConnection;
      } else {
         throw new FailedHttpRequestException("" + i + " " + httpURLConnection.getResponseMessage());
      }
   }

   protected abstract void addAuthentication(HttpURLConnection connection);

   protected int getReadTimeout() {
      return 2000;
   }

   protected HttpURLConnection openConnection(URL url) throws IOException {
      HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
      httpURLConnection.setConnectTimeout(15000);
      httpURLConnection.setReadTimeout(this.getReadTimeout());
      httpURLConnection.setUseCaches(false);
      httpURLConnection.setDoOutput(true);
      httpURLConnection.setDoInput(true);
      httpURLConnection.setRequestMethod("POST");
      httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      httpURLConnection.setRequestProperty("Accept", "application/json");
      httpURLConnection.setRequestProperty("User-Agent", "Minecraft server" + SharedConstants.getGameVersion().name());
      return httpURLConnection;
   }

   public TextStream createFilterer(GameProfile profile) {
      return new StreamImpl(profile);
   }

   @FunctionalInterface
   public interface HashIgnorer {
      HashIgnorer NEVER_IGNORE = (hashes, hashesSize) -> {
         return false;
      };
      HashIgnorer IGNORE_IF_MATCHES_ALL = (hashes, hashesSize) -> {
         return hashes.length() == hashesSize;
      };

      static HashIgnorer internalDropHashes(int hashesToDrop) {
         return (hashes, hashesSize) -> {
            return hashesSize >= hashesToDrop;
         };
      }

      static HashIgnorer dropHashes(int hashesToDrop) {
         HashIgnorer var10000;
         switch (hashesToDrop) {
            case -1:
               var10000 = NEVER_IGNORE;
               break;
            case 0:
               var10000 = IGNORE_IF_MATCHES_ALL;
               break;
            default:
               var10000 = internalDropHashes(hashesToDrop);
         }

         return var10000;
      }

      boolean shouldIgnore(String hashes, int hashesSize);
   }

   @FunctionalInterface
   protected interface MessageEncoder {
      JsonObject encode(GameProfile gameProfile, String message);
   }

   protected static class FailedHttpRequestException extends RuntimeException {
      protected FailedHttpRequestException(String message) {
         super(message);
      }
   }

   protected class StreamImpl implements TextStream {
      protected final GameProfile gameProfile;
      protected final Executor executor;

      protected StreamImpl(final GameProfile gameProfile) {
         this.gameProfile = gameProfile;
         SimpleConsecutiveExecutor simpleConsecutiveExecutor = new SimpleConsecutiveExecutor(AbstractTextFilterer.this.threadPool, "chat stream for " + gameProfile.getName());
         Objects.requireNonNull(simpleConsecutiveExecutor);
         this.executor = simpleConsecutiveExecutor::send;
      }

      public CompletableFuture filterTexts(List texts) {
         List list = (List)texts.stream().map((text) -> {
            return AbstractTextFilterer.this.filter(this.gameProfile, text, AbstractTextFilterer.this.hashIgnorer, this.executor);
         }).collect(ImmutableList.toImmutableList());
         return Util.combine(list).exceptionally((throwable) -> {
            return ImmutableList.of();
         });
      }

      public CompletableFuture filterText(String text) {
         return AbstractTextFilterer.this.filter(this.gameProfile, text, AbstractTextFilterer.this.hashIgnorer, this.executor);
      }
   }
}
