package net.minecraft.util.path;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

public class AllowedSymlinkPathMatcher implements PathMatcher {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String COMMENT_LINE_PREFIX = "#";
   private final List allowedEntries;
   private final Map matcherCache = new ConcurrentHashMap();

   public AllowedSymlinkPathMatcher(List allowedEntries) {
      this.allowedEntries = allowedEntries;
   }

   public PathMatcher get(FileSystem fileSystem) {
      return (PathMatcher)this.matcherCache.computeIfAbsent(fileSystem.provider().getScheme(), (scheme) -> {
         List list;
         try {
            list = this.allowedEntries.stream().map((entry) -> {
               return entry.compile(fileSystem);
            }).toList();
         } catch (Exception var5) {
            LOGGER.error("Failed to compile file pattern list", var5);
            return (path) -> {
               return false;
            };
         }

         PathMatcher var10000;
         switch (list.size()) {
            case 0:
               var10000 = (path) -> {
                  return false;
               };
               break;
            case 1:
               var10000 = (PathMatcher)list.get(0);
               break;
            default:
               var10000 = (path) -> {
                  Iterator var2 = list.iterator();

                  PathMatcher pathMatcher;
                  do {
                     if (!var2.hasNext()) {
                        return false;
                     }

                     pathMatcher = (PathMatcher)var2.next();
                  } while(!pathMatcher.matches(path));

                  return true;
               };
         }

         return var10000;
      });
   }

   public boolean matches(Path path) {
      return this.get(path.getFileSystem()).matches(path);
   }

   public static AllowedSymlinkPathMatcher fromReader(BufferedReader reader) {
      return new AllowedSymlinkPathMatcher(reader.lines().flatMap((line) -> {
         return AllowedSymlinkPathMatcher.Entry.readLine(line).stream();
      }).toList());
   }

   public static record Entry(EntryType type, String pattern) {
      public Entry(EntryType entryType, String string) {
         this.type = entryType;
         this.pattern = string;
      }

      public PathMatcher compile(FileSystem fileSystem) {
         return this.type().compile(fileSystem, this.pattern);
      }

      static Optional readLine(String line) {
         if (!line.isBlank() && !line.startsWith("#")) {
            if (!line.startsWith("[")) {
               return Optional.of(new Entry(AllowedSymlinkPathMatcher.EntryType.PREFIX, line));
            } else {
               int i = line.indexOf(93, 1);
               if (i == -1) {
                  throw new IllegalArgumentException("Unterminated type in line '" + line + "'");
               } else {
                  String string = line.substring(1, i);
                  String string2 = line.substring(i + 1);
                  Optional var10000;
                  switch (string) {
                     case "glob":
                     case "regex":
                        var10000 = Optional.of(new Entry(AllowedSymlinkPathMatcher.EntryType.DEFAULT, string + ":" + string2));
                        break;
                     case "prefix":
                        var10000 = Optional.of(new Entry(AllowedSymlinkPathMatcher.EntryType.PREFIX, string2));
                        break;
                     default:
                        throw new IllegalArgumentException("Unsupported definition type in line '" + line + "'");
                  }

                  return var10000;
               }
            }
         } else {
            return Optional.empty();
         }
      }

      static Entry glob(String pattern) {
         return new Entry(AllowedSymlinkPathMatcher.EntryType.DEFAULT, "glob:" + pattern);
      }

      static Entry regex(String pattern) {
         return new Entry(AllowedSymlinkPathMatcher.EntryType.DEFAULT, "regex:" + pattern);
      }

      static Entry prefix(String prefix) {
         return new Entry(AllowedSymlinkPathMatcher.EntryType.PREFIX, prefix);
      }

      public EntryType type() {
         return this.type;
      }

      public String pattern() {
         return this.pattern;
      }
   }

   @FunctionalInterface
   public interface EntryType {
      EntryType DEFAULT = FileSystem::getPathMatcher;
      EntryType PREFIX = (fileSystem, prefix) -> {
         return (path) -> {
            return path.toString().startsWith(prefix);
         };
      };

      PathMatcher compile(FileSystem fileSystem, String pattern);
   }
}
