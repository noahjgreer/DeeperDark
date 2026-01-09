package net.minecraft.util.logging;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class LogFileCompressor {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int COMPRESSION_BUFFER_SIZE = 4096;
   private static final String GZ_EXTENSION = ".gz";
   private final Path directory;
   private final String extension;

   private LogFileCompressor(Path directory, String extension) {
      this.directory = directory;
      this.extension = extension;
   }

   public static LogFileCompressor create(Path directory, String extension) throws IOException {
      Files.createDirectories(directory);
      return new LogFileCompressor(directory, extension);
   }

   public LogFileIterable getAll() throws IOException {
      Stream stream = Files.list(this.directory);

      LogFileIterable var2;
      try {
         var2 = new LogFileIterable(stream.filter((path) -> {
            return Files.isRegularFile(path, new LinkOption[0]);
         }).map(this::get).filter(Objects::nonNull).toList());
      } catch (Throwable var5) {
         if (stream != null) {
            try {
               stream.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (stream != null) {
         stream.close();
      }

      return var2;
   }

   @Nullable
   private LogFile get(Path path) {
      String string = path.getFileName().toString();
      int i = string.indexOf(46);
      if (i == -1) {
         return null;
      } else {
         LogId logId = LogFileCompressor.LogId.fromFileName(string.substring(0, i));
         if (logId != null) {
            String string2 = string.substring(i);
            if (string2.equals(this.extension)) {
               return new Uncompressed(path, logId);
            }

            if (string2.equals(this.extension + ".gz")) {
               return new Compressed(path, logId);
            }
         }

         return null;
      }
   }

   static void compress(Path from, Path to) throws IOException {
      if (Files.exists(to, new LinkOption[0])) {
         throw new IOException("Compressed target file already exists: " + String.valueOf(to));
      } else {
         FileChannel fileChannel = FileChannel.open(from, StandardOpenOption.WRITE, StandardOpenOption.READ);

         try {
            FileLock fileLock = fileChannel.tryLock();
            if (fileLock == null) {
               throw new IOException("Raw log file is already locked, cannot compress: " + String.valueOf(from));
            }

            compress((ReadableByteChannel)fileChannel, to);
            fileChannel.truncate(0L);
         } catch (Throwable var6) {
            if (fileChannel != null) {
               try {
                  fileChannel.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (fileChannel != null) {
            fileChannel.close();
         }

         Files.delete(from);
      }
   }

   private static void compress(ReadableByteChannel source, Path outputPath) throws IOException {
      OutputStream outputStream = new GZIPOutputStream(Files.newOutputStream(outputPath));

      try {
         byte[] bs = new byte[4096];
         ByteBuffer byteBuffer = ByteBuffer.wrap(bs);

         while(source.read(byteBuffer) >= 0) {
            byteBuffer.flip();
            outputStream.write(bs, 0, byteBuffer.limit());
            byteBuffer.clear();
         }
      } catch (Throwable var6) {
         try {
            outputStream.close();
         } catch (Throwable var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }

      outputStream.close();
   }

   public Uncompressed createLogFile(LocalDate date) throws IOException {
      int i = 1;
      Set set = this.getAll().toIdSet();

      LogId logId;
      do {
         logId = new LogId(date, i++);
      } while(set.contains(logId));

      Uncompressed uncompressed = new Uncompressed(this.directory.resolve(logId.getFileName(this.extension)), logId);
      Files.createFile(uncompressed.path());
      return uncompressed;
   }

   public static class LogFileIterable implements Iterable {
      private final List logs;

      LogFileIterable(List logs) {
         this.logs = new ArrayList(logs);
      }

      public LogFileIterable removeExpired(LocalDate currentDate, int retentionDays) {
         this.logs.removeIf((log) -> {
            LogId logId = log.id();
            LocalDate localDate2 = logId.date().plusDays((long)retentionDays);
            if (!currentDate.isBefore(localDate2)) {
               try {
                  Files.delete(log.path());
                  return true;
               } catch (IOException var6) {
                  LogFileCompressor.LOGGER.warn("Failed to delete expired event log file: {}", log.path(), var6);
               }
            }

            return false;
         });
         return this;
      }

      public LogFileIterable compressAll() {
         ListIterator listIterator = this.logs.listIterator();

         while(listIterator.hasNext()) {
            LogFile logFile = (LogFile)listIterator.next();

            try {
               listIterator.set(logFile.compress());
            } catch (IOException var4) {
               LogFileCompressor.LOGGER.warn("Failed to compress event log file: {}", logFile.path(), var4);
            }
         }

         return this;
      }

      public Iterator iterator() {
         return this.logs.iterator();
      }

      public Stream stream() {
         return this.logs.stream();
      }

      public Set toIdSet() {
         return (Set)this.logs.stream().map(LogFile::id).collect(Collectors.toSet());
      }
   }

   public static record LogId(LocalDate date, int index) {
      private static final DateTimeFormatter DATE_TIME_FORMATTER;

      public LogId(LocalDate localDate, int i) {
         this.date = localDate;
         this.index = i;
      }

      @Nullable
      public static LogId fromFileName(String fileName) {
         int i = fileName.indexOf("-");
         if (i == -1) {
            return null;
         } else {
            String string = fileName.substring(0, i);
            String string2 = fileName.substring(i + 1);

            try {
               return new LogId(LocalDate.parse(string, DATE_TIME_FORMATTER), Integer.parseInt(string2));
            } catch (DateTimeParseException | NumberFormatException var5) {
               return null;
            }
         }
      }

      public String toString() {
         String var10000 = DATE_TIME_FORMATTER.format(this.date);
         return var10000 + "-" + this.index;
      }

      public String getFileName(String extension) {
         String var10000 = String.valueOf(this);
         return var10000 + extension;
      }

      public LocalDate date() {
         return this.date;
      }

      public int index() {
         return this.index;
      }

      static {
         DATE_TIME_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
      }
   }

   public static record Uncompressed(Path path, LogId id) implements LogFile {
      public Uncompressed(Path path, LogId logId) {
         this.path = path;
         this.id = logId;
      }

      public FileChannel open() throws IOException {
         return FileChannel.open(this.path, StandardOpenOption.WRITE, StandardOpenOption.READ);
      }

      @Nullable
      public Reader getReader() throws IOException {
         return Files.exists(this.path, new LinkOption[0]) ? Files.newBufferedReader(this.path) : null;
      }

      public Compressed compress() throws IOException {
         Path path = this.path.resolveSibling(this.path.getFileName().toString() + ".gz");
         LogFileCompressor.compress(this.path, path);
         return new Compressed(path, this.id);
      }

      public Path path() {
         return this.path;
      }

      public LogId id() {
         return this.id;
      }
   }

   public static record Compressed(Path path, LogId id) implements LogFile {
      public Compressed(Path path, LogId logId) {
         this.path = path;
         this.id = logId;
      }

      @Nullable
      public Reader getReader() throws IOException {
         return !Files.exists(this.path, new LinkOption[0]) ? null : new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(this.path))));
      }

      public Compressed compress() {
         return this;
      }

      public Path path() {
         return this.path;
      }

      public LogId id() {
         return this.id;
      }
   }

   public interface LogFile {
      Path path();

      LogId id();

      @Nullable
      Reader getReader() throws IOException;

      Compressed compress() throws IOException;
   }
}
