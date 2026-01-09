package net.minecraft.util.logging;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.Nullable;

public class LogWriter implements Closeable {
   private static final Gson GSON = new Gson();
   private final Codec codec;
   final FileChannel channel;
   private final AtomicInteger refCount = new AtomicInteger(1);

   public LogWriter(Codec codec, FileChannel channel) {
      this.codec = codec;
      this.channel = channel;
   }

   public static LogWriter create(Codec codec, Path path) throws IOException {
      FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);
      return new LogWriter(codec, fileChannel);
   }

   public void write(Object object) throws IOException {
      JsonElement jsonElement = (JsonElement)this.codec.encodeStart(JsonOps.INSTANCE, object).getOrThrow(IOException::new);
      this.channel.position(this.channel.size());
      Writer writer = Channels.newWriter(this.channel, StandardCharsets.UTF_8);
      GSON.toJson(jsonElement, GSON.newJsonWriter(writer));
      writer.write(10);
      writer.flush();
   }

   public LogReader getReader() throws IOException {
      if (this.refCount.get() <= 0) {
         throw new IOException("Event log has already been closed");
      } else {
         this.refCount.incrementAndGet();
         final LogReader logReader = LogReader.create(this.codec, Channels.newReader(this.channel, StandardCharsets.UTF_8));
         return new LogReader() {
            private volatile long pos;

            @Nullable
            public Object read() throws IOException {
               Object var1;
               try {
                  LogWriter.this.channel.position(this.pos);
                  var1 = logReader.read();
               } finally {
                  this.pos = LogWriter.this.channel.position();
               }

               return var1;
            }

            public void close() throws IOException {
               LogWriter.this.closeIfNotReferenced();
            }
         };
      }
   }

   public void close() throws IOException {
      this.closeIfNotReferenced();
   }

   void closeIfNotReferenced() throws IOException {
      if (this.refCount.decrementAndGet() <= 0) {
         this.channel.close();
      }

   }
}
