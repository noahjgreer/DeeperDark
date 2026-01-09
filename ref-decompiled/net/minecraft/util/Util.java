package net.minecraft.util;

import com.google.common.base.Ticker;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.types.Type;
import com.mojang.jtracy.TracyClient;
import com.mojang.jtracy.Zone;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceImmutableList;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.Schemas;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.Property;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ReportType;
import net.minecraft.util.function.CharPredicate;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.thread.NameableExecutor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class Util {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_PARALLELISM = 255;
   private static final int BACKUP_ATTEMPTS = 10;
   private static final String MAX_BG_THREADS_PROPERTY = "max.bg.threads";
   private static final NameableExecutor MAIN_WORKER_EXECUTOR = createWorker("Main");
   private static final NameableExecutor IO_WORKER_EXECUTOR = createIoWorker("IO-Worker-", false);
   private static final NameableExecutor DOWNLOAD_WORKER_EXECUTOR = createIoWorker("Download-", true);
   private static final DateTimeFormatter DATE_TIME_FORMATTER;
   public static final int field_46220 = 8;
   private static final Set SUPPORTED_URI_PROTOCOLS;
   public static final long NANOS_PER_MILLI = 1000000L;
   public static TimeSupplier.Nanoseconds nanoTimeSupplier;
   public static final Ticker TICKER;
   public static final UUID NIL_UUID;
   public static final FileSystemProvider JAR_FILE_SYSTEM_PROVIDER;
   private static Consumer missingBreakpointHandler;

   public static Collector toMap() {
      return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
   }

   public static Collector toArrayList() {
      return Collectors.toCollection(Lists::newArrayList);
   }

   public static String getValueAsString(Property property, Object value) {
      return property.name((Comparable)value);
   }

   public static String createTranslationKey(String type, @Nullable Identifier id) {
      return id == null ? type + ".unregistered_sadface" : type + "." + id.getNamespace() + "." + id.getPath().replace('/', '.');
   }

   public static long getMeasuringTimeMs() {
      return getMeasuringTimeNano() / 1000000L;
   }

   public static long getMeasuringTimeNano() {
      return nanoTimeSupplier.getAsLong();
   }

   public static long getEpochTimeMs() {
      return Instant.now().toEpochMilli();
   }

   public static String getFormattedCurrentTime() {
      return DATE_TIME_FORMATTER.format(ZonedDateTime.now());
   }

   private static NameableExecutor createWorker(String name) {
      int i = getAvailableBackgroundThreads();
      Object executorService;
      if (i <= 0) {
         executorService = MoreExecutors.newDirectExecutorService();
      } else {
         AtomicInteger atomicInteger = new AtomicInteger(1);
         executorService = new ForkJoinPool(i, (pool) -> {
            final String string2 = "Worker-" + name + "-" + atomicInteger.getAndIncrement();
            ForkJoinWorkerThread forkJoinWorkerThread = new ForkJoinWorkerThread(pool) {
               protected void onStart() {
                  TracyClient.setThreadName(string2, string.hashCode());
                  super.onStart();
               }

               protected void onTermination(Throwable throwable) {
                  if (throwable != null) {
                     Util.LOGGER.warn("{} died", this.getName(), throwable);
                  } else {
                     Util.LOGGER.debug("{} shutdown", this.getName());
                  }

                  super.onTermination(throwable);
               }
            };
            forkJoinWorkerThread.setName(string2);
            return forkJoinWorkerThread;
         }, Util::uncaughtExceptionHandler, true);
      }

      return new NameableExecutor((ExecutorService)executorService);
   }

   public static int getAvailableBackgroundThreads() {
      return MathHelper.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, getMaxBackgroundThreads());
   }

   private static int getMaxBackgroundThreads() {
      String string = System.getProperty("max.bg.threads");
      if (string != null) {
         try {
            int i = Integer.parseInt(string);
            if (i >= 1 && i <= 255) {
               return i;
            }

            LOGGER.error("Wrong {} property value '{}'. Should be an integer value between 1 and {}.", new Object[]{"max.bg.threads", string, 255});
         } catch (NumberFormatException var2) {
            LOGGER.error("Could not parse {} property value '{}'. Should be an integer value between 1 and {}.", new Object[]{"max.bg.threads", string, 255});
         }
      }

      return 255;
   }

   public static NameableExecutor getMainWorkerExecutor() {
      return MAIN_WORKER_EXECUTOR;
   }

   public static NameableExecutor getIoWorkerExecutor() {
      return IO_WORKER_EXECUTOR;
   }

   public static NameableExecutor getDownloadWorkerExecutor() {
      return DOWNLOAD_WORKER_EXECUTOR;
   }

   public static void shutdownExecutors() {
      MAIN_WORKER_EXECUTOR.shutdown(3L, TimeUnit.SECONDS);
      IO_WORKER_EXECUTOR.shutdown(3L, TimeUnit.SECONDS);
   }

   private static NameableExecutor createIoWorker(String namePrefix, boolean daemon) {
      AtomicInteger atomicInteger = new AtomicInteger(1);
      return new NameableExecutor(Executors.newCachedThreadPool((runnable) -> {
         Thread thread = new Thread(runnable);
         String string2 = namePrefix + atomicInteger.getAndIncrement();
         TracyClient.setThreadName(string2, namePrefix.hashCode());
         thread.setName(string2);
         thread.setDaemon(daemon);
         thread.setUncaughtExceptionHandler(Util::uncaughtExceptionHandler);
         return thread;
      }));
   }

   public static void throwUnchecked(Throwable t) {
      throw t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
   }

   private static void uncaughtExceptionHandler(Thread thread, Throwable t) {
      getFatalOrPause(t);
      if (t instanceof CompletionException) {
         t = t.getCause();
      }

      if (t instanceof CrashException crashException) {
         Bootstrap.println(crashException.getReport().asString(ReportType.MINECRAFT_CRASH_REPORT));
         System.exit(-1);
      }

      LOGGER.error(String.format(Locale.ROOT, "Caught exception in thread %s", thread), t);
   }

   @Nullable
   public static Type getChoiceType(DSL.TypeReference typeReference, String id) {
      return !SharedConstants.useChoiceTypeRegistrations ? null : getChoiceTypeInternal(typeReference, id);
   }

   @Nullable
   private static Type getChoiceTypeInternal(DSL.TypeReference typeReference, String id) {
      Type type = null;

      try {
         type = Schemas.getFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().dataVersion().id())).getChoiceType(typeReference, id);
      } catch (IllegalArgumentException var4) {
         LOGGER.error("No data fixer registered for {}", id);
         if (SharedConstants.isDevelopment) {
            throw var4;
         }
      }

      return type;
   }

   public static void runInNamedZone(Runnable runnable, String name) {
      if (SharedConstants.isDevelopment) {
         Thread thread = Thread.currentThread();
         String string = thread.getName();
         thread.setName(name);

         try {
            Zone zone = TracyClient.beginZone(name, SharedConstants.isDevelopment);

            try {
               runnable.run();
            } catch (Throwable var15) {
               if (zone != null) {
                  try {
                     zone.close();
                  } catch (Throwable var13) {
                     var15.addSuppressed(var13);
                  }
               }

               throw var15;
            }

            if (zone != null) {
               zone.close();
            }
         } finally {
            thread.setName(string);
         }
      } else {
         Zone zone2 = TracyClient.beginZone(name, SharedConstants.isDevelopment);

         try {
            runnable.run();
         } catch (Throwable var17) {
            if (zone2 != null) {
               try {
                  zone2.close();
               } catch (Throwable var14) {
                  var17.addSuppressed(var14);
               }
            }

            throw var17;
         }

         if (zone2 != null) {
            zone2.close();
         }
      }

   }

   public static String registryValueToString(Registry registry, Object value) {
      Identifier identifier = registry.getId(value);
      return identifier == null ? "[unregistered]" : identifier.toString();
   }

   public static Predicate and() {
      return (o) -> {
         return true;
      };
   }

   public static Predicate and(Predicate a) {
      return a;
   }

   public static Predicate and(Predicate a, Predicate b) {
      return (o) -> {
         return a.test(o) && b.test(o);
      };
   }

   public static Predicate and(Predicate a, Predicate b, Predicate c) {
      return (o) -> {
         return a.test(o) && b.test(o) && c.test(o);
      };
   }

   public static Predicate and(Predicate a, Predicate b, Predicate c, Predicate d) {
      return (o) -> {
         return a.test(o) && b.test(o) && c.test(o) && d.test(o);
      };
   }

   public static Predicate and(Predicate a, Predicate b, Predicate c, Predicate d, Predicate e) {
      return (o) -> {
         return a.test(o) && b.test(o) && c.test(o) && d.test(o) && e.test(o);
      };
   }

   @SafeVarargs
   public static Predicate and(Predicate... predicates) {
      return (o) -> {
         Predicate[] var2 = predicates;
         int var3 = predicates.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Predicate predicate = var2[var4];
            if (!predicate.test(o)) {
               return false;
            }
         }

         return true;
      };
   }

   public static Predicate allOf(List predicates) {
      Predicate var10000;
      switch (predicates.size()) {
         case 0:
            var10000 = and();
            break;
         case 1:
            var10000 = and((Predicate)predicates.get(0));
            break;
         case 2:
            var10000 = and((Predicate)predicates.get(0), (Predicate)predicates.get(1));
            break;
         case 3:
            var10000 = and((Predicate)predicates.get(0), (Predicate)predicates.get(1), (Predicate)predicates.get(2));
            break;
         case 4:
            var10000 = and((Predicate)predicates.get(0), (Predicate)predicates.get(1), (Predicate)predicates.get(2), (Predicate)predicates.get(3));
            break;
         case 5:
            var10000 = and((Predicate)predicates.get(0), (Predicate)predicates.get(1), (Predicate)predicates.get(2), (Predicate)predicates.get(3), (Predicate)predicates.get(4));
            break;
         default:
            Predicate[] predicates2 = (Predicate[])predicates.toArray((i) -> {
               return new Predicate[i];
            });
            var10000 = and(predicates2);
      }

      return var10000;
   }

   public static Predicate or() {
      return (o) -> {
         return false;
      };
   }

   public static Predicate or(Predicate a) {
      return a;
   }

   public static Predicate or(Predicate a, Predicate b) {
      return (o) -> {
         return a.test(o) || b.test(o);
      };
   }

   public static Predicate or(Predicate a, Predicate b, Predicate c) {
      return (o) -> {
         return a.test(o) || b.test(o) || c.test(o);
      };
   }

   public static Predicate or(Predicate a, Predicate b, Predicate c, Predicate d) {
      return (o) -> {
         return a.test(o) || b.test(o) || c.test(o) || d.test(o);
      };
   }

   public static Predicate or(Predicate a, Predicate b, Predicate c, Predicate d, Predicate e) {
      return (o) -> {
         return a.test(o) || b.test(o) || c.test(o) || d.test(o) || e.test(o);
      };
   }

   @SafeVarargs
   public static Predicate or(Predicate... predicates) {
      return (o) -> {
         Predicate[] var2 = predicates;
         int var3 = predicates.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Predicate predicate = var2[var4];
            if (predicate.test(o)) {
               return true;
            }
         }

         return false;
      };
   }

   public static Predicate anyOf(List predicates) {
      Predicate var10000;
      switch (predicates.size()) {
         case 0:
            var10000 = or();
            break;
         case 1:
            var10000 = or((Predicate)predicates.get(0));
            break;
         case 2:
            var10000 = or((Predicate)predicates.get(0), (Predicate)predicates.get(1));
            break;
         case 3:
            var10000 = or((Predicate)predicates.get(0), (Predicate)predicates.get(1), (Predicate)predicates.get(2));
            break;
         case 4:
            var10000 = or((Predicate)predicates.get(0), (Predicate)predicates.get(1), (Predicate)predicates.get(2), (Predicate)predicates.get(3));
            break;
         case 5:
            var10000 = or((Predicate)predicates.get(0), (Predicate)predicates.get(1), (Predicate)predicates.get(2), (Predicate)predicates.get(3), (Predicate)predicates.get(4));
            break;
         default:
            Predicate[] predicates2 = (Predicate[])predicates.toArray((i) -> {
               return new Predicate[i];
            });
            var10000 = or(predicates2);
      }

      return var10000;
   }

   public static boolean isSymmetrical(int width, int height, List list) {
      if (width == 1) {
         return true;
      } else {
         int i = width / 2;

         for(int j = 0; j < height; ++j) {
            for(int k = 0; k < i; ++k) {
               int l = width - 1 - k;
               Object object = list.get(k + j * width);
               Object object2 = list.get(l + j * width);
               if (!object.equals(object2)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public static int nextCapacity(int current, int min) {
      return (int)Math.max(Math.min((long)current + (long)(current >> 1), 2147483639L), (long)min);
   }

   public static OperatingSystem getOperatingSystem() {
      String string = System.getProperty("os.name").toLowerCase(Locale.ROOT);
      if (string.contains("win")) {
         return Util.OperatingSystem.WINDOWS;
      } else if (string.contains("mac")) {
         return Util.OperatingSystem.OSX;
      } else if (string.contains("solaris")) {
         return Util.OperatingSystem.SOLARIS;
      } else if (string.contains("sunos")) {
         return Util.OperatingSystem.SOLARIS;
      } else if (string.contains("linux")) {
         return Util.OperatingSystem.LINUX;
      } else {
         return string.contains("unix") ? Util.OperatingSystem.LINUX : Util.OperatingSystem.UNKNOWN;
      }
   }

   public static boolean isOnAarch64() {
      String string = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
      return string.equals("aarch64");
   }

   public static URI validateUri(String uri) throws URISyntaxException {
      URI uRI = new URI(uri);
      String string = uRI.getScheme();
      if (string == null) {
         throw new URISyntaxException(uri, "Missing protocol in URI: " + uri);
      } else {
         String string2 = string.toLowerCase(Locale.ROOT);
         if (!SUPPORTED_URI_PROTOCOLS.contains(string2)) {
            throw new URISyntaxException(uri, "Unsupported protocol in URI: " + uri);
         } else {
            return uRI;
         }
      }
   }

   public static Stream getJVMFlags() {
      RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
      return runtimeMXBean.getInputArguments().stream().filter((runtimeArg) -> {
         return runtimeArg.startsWith("-X");
      });
   }

   public static Object getLast(List list) {
      return list.get(list.size() - 1);
   }

   public static Object next(Iterable iterable, @Nullable Object object) {
      Iterator iterator = iterable.iterator();
      Object object2 = iterator.next();
      if (object != null) {
         Object object3 = object2;

         while(object3 != object) {
            if (iterator.hasNext()) {
               object3 = iterator.next();
            }
         }

         if (iterator.hasNext()) {
            return iterator.next();
         }
      }

      return object2;
   }

   public static Object previous(Iterable iterable, @Nullable Object object) {
      Iterator iterator = iterable.iterator();

      Object object2;
      Object object3;
      for(object2 = null; iterator.hasNext(); object2 = object3) {
         object3 = iterator.next();
         if (object3 == object) {
            if (object2 == null) {
               object2 = iterator.hasNext() ? Iterators.getLast(iterator) : object;
            }
            break;
         }
      }

      return object2;
   }

   public static Object make(Supplier factory) {
      return factory.get();
   }

   public static Object make(Object object, Consumer initializer) {
      initializer.accept(object);
      return object;
   }

   public static Map mapEnum(Class enumClass, Function mapper) {
      EnumMap enumMap = new EnumMap(enumClass);
      Enum[] var3 = (Enum[])enumClass.getEnumConstants();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Enum enum_ = var3[var5];
         enumMap.put(enum_, mapper.apply(enum_));
      }

      return enumMap;
   }

   public static Map transformMapValues(Map map, Function transformer) {
      return (Map)map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (entry) -> {
         return transformer.apply(entry.getValue());
      }));
   }

   public static Map transformMapValuesLazy(Map map, com.google.common.base.Function transformer) {
      return Maps.transformValues(map, transformer);
   }

   public static CompletableFuture combineSafe(List futures) {
      if (futures.isEmpty()) {
         return CompletableFuture.completedFuture(List.of());
      } else if (futures.size() == 1) {
         return ((CompletableFuture)futures.get(0)).thenApply(List::of);
      } else {
         CompletableFuture completableFuture = CompletableFuture.allOf((CompletableFuture[])futures.toArray(new CompletableFuture[0]));
         return completableFuture.thenApply((void_) -> {
            return futures.stream().map(CompletableFuture::join).toList();
         });
      }
   }

   public static CompletableFuture combine(List futures) {
      CompletableFuture completableFuture = new CompletableFuture();
      Objects.requireNonNull(completableFuture);
      return combine(futures, completableFuture::completeExceptionally).applyToEither(completableFuture, Function.identity());
   }

   public static CompletableFuture combineCancellable(List futures) {
      CompletableFuture completableFuture = new CompletableFuture();
      return combine(futures, (throwable) -> {
         if (completableFuture.completeExceptionally(throwable)) {
            Iterator var3 = futures.iterator();

            while(var3.hasNext()) {
               CompletableFuture completableFuture2 = (CompletableFuture)var3.next();
               completableFuture2.cancel(true);
            }
         }

      }).applyToEither(completableFuture, Function.identity());
   }

   private static CompletableFuture combine(List futures, Consumer exceptionHandler) {
      List list = Lists.newArrayListWithCapacity(futures.size());
      CompletableFuture[] completableFutures = new CompletableFuture[futures.size()];
      futures.forEach((future) -> {
         int i = list.size();
         list.add((Object)null);
         completableFutures[i] = future.whenComplete((value, throwable) -> {
            if (throwable != null) {
               exceptionHandler.accept(throwable);
            } else {
               list.set(i, value);
            }

         });
      });
      return CompletableFuture.allOf(completableFutures).thenApply((void_) -> {
         return list;
      });
   }

   public static Optional ifPresentOrElse(Optional optional, Consumer presentAction, Runnable elseAction) {
      if (optional.isPresent()) {
         presentAction.accept(optional.get());
      } else {
         elseAction.run();
      }

      return optional;
   }

   public static Supplier debugSupplier(Supplier supplier, Supplier messageSupplier) {
      return supplier;
   }

   public static Runnable debugRunnable(Runnable runnable, Supplier messageSupplier) {
      return runnable;
   }

   public static void logErrorOrPause(String message) {
      LOGGER.error(message);
      if (SharedConstants.isDevelopment) {
         pause(message);
      }

   }

   public static void logErrorOrPause(String message, Throwable throwable) {
      LOGGER.error(message, throwable);
      if (SharedConstants.isDevelopment) {
         pause(message);
      }

   }

   public static Throwable getFatalOrPause(Throwable t) {
      if (SharedConstants.isDevelopment) {
         LOGGER.error("Trying to throw a fatal exception, pausing in IDE", t);
         pause(t.getMessage());
      }

      return t;
   }

   public static void setMissingBreakpointHandler(Consumer missingBreakpointHandler) {
      Util.missingBreakpointHandler = missingBreakpointHandler;
   }

   private static void pause(String message) {
      Instant instant = Instant.now();
      LOGGER.warn("Did you remember to set a breakpoint here?");
      boolean bl = Duration.between(instant, Instant.now()).toMillis() > 500L;
      if (!bl) {
         missingBreakpointHandler.accept(message);
      }

   }

   public static String getInnermostMessage(Throwable t) {
      if (t.getCause() != null) {
         return getInnermostMessage(t.getCause());
      } else {
         return t.getMessage() != null ? t.getMessage() : t.toString();
      }
   }

   public static Object getRandom(Object[] array, Random random) {
      return array[random.nextInt(array.length)];
   }

   public static int getRandom(int[] array, Random random) {
      return array[random.nextInt(array.length)];
   }

   public static Object getRandom(List list, Random random) {
      return list.get(random.nextInt(list.size()));
   }

   public static Optional getRandomOrEmpty(List list, Random random) {
      return list.isEmpty() ? Optional.empty() : Optional.of(getRandom(list, random));
   }

   private static BooleanSupplier renameTask(final Path src, final Path dest) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            try {
               Files.move(src, dest);
               return true;
            } catch (IOException var2) {
               Util.LOGGER.error("Failed to rename", var2);
               return false;
            }
         }

         public String toString() {
            String var10000 = String.valueOf(src);
            return "rename " + var10000 + " to " + String.valueOf(dest);
         }
      };
   }

   private static BooleanSupplier deleteTask(final Path path) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            try {
               Files.deleteIfExists(path);
               return true;
            } catch (IOException var2) {
               Util.LOGGER.warn("Failed to delete", var2);
               return false;
            }
         }

         public String toString() {
            return "delete old " + String.valueOf(path);
         }
      };
   }

   private static BooleanSupplier deletionVerifyTask(final Path path) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            return !Files.exists(path, new LinkOption[0]);
         }

         public String toString() {
            return "verify that " + String.valueOf(path) + " is deleted";
         }
      };
   }

   private static BooleanSupplier existenceCheckTask(final Path path) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            return Files.isRegularFile(path, new LinkOption[0]);
         }

         public String toString() {
            return "verify that " + String.valueOf(path) + " is present";
         }
      };
   }

   private static boolean attemptTasks(BooleanSupplier... tasks) {
      BooleanSupplier[] var1 = tasks;
      int var2 = tasks.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         BooleanSupplier booleanSupplier = var1[var3];
         if (!booleanSupplier.getAsBoolean()) {
            LOGGER.warn("Failed to execute {}", booleanSupplier);
            return false;
         }
      }

      return true;
   }

   private static boolean attemptTasks(int retries, String taskName, BooleanSupplier... tasks) {
      for(int i = 0; i < retries; ++i) {
         if (attemptTasks(tasks)) {
            return true;
         }

         LOGGER.error("Failed to {}, retrying {}/{}", new Object[]{taskName, i, retries});
      }

      LOGGER.error("Failed to {}, aborting, progress might be lost", taskName);
      return false;
   }

   public static void backupAndReplace(Path current, Path newPath, Path backup) {
      backupAndReplace(current, newPath, backup, false);
   }

   public static boolean backupAndReplace(Path current, Path newPath, Path backup, boolean noRestoreOnFail) {
      if (Files.exists(current, new LinkOption[0]) && !attemptTasks(10, "create backup " + String.valueOf(backup), deleteTask(backup), renameTask(current, backup), existenceCheckTask(backup))) {
         return false;
      } else if (!attemptTasks(10, "remove old " + String.valueOf(current), deleteTask(current), deletionVerifyTask(current))) {
         return false;
      } else if (!attemptTasks(10, "replace " + String.valueOf(current) + " with " + String.valueOf(newPath), renameTask(newPath, current), existenceCheckTask(current)) && !noRestoreOnFail) {
         attemptTasks(10, "restore " + String.valueOf(current) + " from " + String.valueOf(backup), renameTask(backup, current), existenceCheckTask(current));
         return false;
      } else {
         return true;
      }
   }

   public static int moveCursor(String string, int cursor, int delta) {
      int i = string.length();
      int j;
      if (delta >= 0) {
         for(j = 0; cursor < i && j < delta; ++j) {
            if (Character.isHighSurrogate(string.charAt(cursor++)) && cursor < i && Character.isLowSurrogate(string.charAt(cursor))) {
               ++cursor;
            }
         }
      } else {
         for(j = delta; cursor > 0 && j < 0; ++j) {
            --cursor;
            if (Character.isLowSurrogate(string.charAt(cursor)) && cursor > 0 && Character.isHighSurrogate(string.charAt(cursor - 1))) {
               --cursor;
            }
         }
      }

      return cursor;
   }

   public static Consumer addPrefix(String prefix, Consumer consumer) {
      return (string) -> {
         consumer.accept(prefix + string);
      };
   }

   public static DataResult decodeFixedLengthArray(IntStream stream, int length) {
      int[] is = stream.limit((long)(length + 1)).toArray();
      if (is.length != length) {
         Supplier supplier = () -> {
            return "Input is not a list of " + length + " ints";
         };
         return is.length >= length ? DataResult.error(supplier, Arrays.copyOf(is, length)) : DataResult.error(supplier);
      } else {
         return DataResult.success(is);
      }
   }

   public static DataResult decodeFixedLengthArray(LongStream stream, int length) {
      long[] ls = stream.limit((long)(length + 1)).toArray();
      if (ls.length != length) {
         Supplier supplier = () -> {
            return "Input is not a list of " + length + " longs";
         };
         return ls.length >= length ? DataResult.error(supplier, Arrays.copyOf(ls, length)) : DataResult.error(supplier);
      } else {
         return DataResult.success(ls);
      }
   }

   public static DataResult decodeFixedLengthList(List list, int length) {
      if (list.size() != length) {
         Supplier supplier = () -> {
            return "Input is not a list of " + length + " elements";
         };
         return list.size() >= length ? DataResult.error(supplier, list.subList(0, length)) : DataResult.error(supplier);
      } else {
         return DataResult.success(list);
      }
   }

   public static void startTimerHack() {
      Thread thread = new Thread("Timer hack thread") {
         public void run() {
            while(true) {
               try {
                  Thread.sleep(2147483647L);
               } catch (InterruptedException var2) {
                  Util.LOGGER.warn("Timer hack thread interrupted, that really should not happen");
                  return;
               }
            }
         }
      };
      thread.setDaemon(true);
      thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
      thread.start();
   }

   public static void relativeCopy(Path src, Path dest, Path toCopy) throws IOException {
      Path path = src.relativize(toCopy);
      Path path2 = dest.resolve(path);
      Files.copy(toCopy, path2);
   }

   public static String replaceInvalidChars(String string, CharPredicate predicate) {
      return (String)string.toLowerCase(Locale.ROOT).chars().mapToObj((charCode) -> {
         return predicate.test((char)charCode) ? Character.toString((char)charCode) : "_";
      }).collect(Collectors.joining());
   }

   public static CachedMapper cachedMapper(Function mapper) {
      return new CachedMapper(mapper);
   }

   public static Function memoize(final Function function) {
      return new Function() {
         private final Map cache = new ConcurrentHashMap();

         public Object apply(Object object) {
            return this.cache.computeIfAbsent(object, function);
         }

         public String toString() {
            String var10000 = String.valueOf(function);
            return "memoize/1[function=" + var10000 + ", size=" + this.cache.size() + "]";
         }
      };
   }

   public static BiFunction memoize(final BiFunction biFunction) {
      return new BiFunction() {
         private final Map cache = new ConcurrentHashMap();

         public Object apply(Object a, Object b) {
            return this.cache.computeIfAbsent(com.mojang.datafixers.util.Pair.of(a, b), (pair) -> {
               return biFunction.apply(pair.getFirst(), pair.getSecond());
            });
         }

         public String toString() {
            String var10000 = String.valueOf(biFunction);
            return "memoize/2[function=" + var10000 + ", size=" + this.cache.size() + "]";
         }
      };
   }

   public static List copyShuffled(Stream stream, Random random) {
      ObjectArrayList objectArrayList = (ObjectArrayList)stream.collect(ObjectArrayList.toList());
      shuffle((List)objectArrayList, random);
      return objectArrayList;
   }

   public static IntArrayList shuffle(IntStream stream, Random random) {
      IntArrayList intArrayList = IntArrayList.wrap(stream.toArray());
      int i = intArrayList.size();

      for(int j = i; j > 1; --j) {
         int k = random.nextInt(j);
         intArrayList.set(j - 1, intArrayList.set(k, intArrayList.getInt(j - 1)));
      }

      return intArrayList;
   }

   public static List copyShuffled(Object[] array, Random random) {
      ObjectArrayList objectArrayList = new ObjectArrayList(array);
      shuffle((List)objectArrayList, random);
      return objectArrayList;
   }

   public static List copyShuffled(ObjectArrayList list, Random random) {
      ObjectArrayList objectArrayList = new ObjectArrayList(list);
      shuffle((List)objectArrayList, random);
      return objectArrayList;
   }

   public static void shuffle(List list, Random random) {
      int i = list.size();

      for(int j = i; j > 1; --j) {
         int k = random.nextInt(j);
         list.set(j - 1, list.set(k, list.get(j - 1)));
      }

   }

   public static CompletableFuture waitAndApply(Function resultFactory) {
      return (CompletableFuture)waitAndApply(resultFactory, CompletableFuture::isDone);
   }

   public static Object waitAndApply(Function resultFactory, Predicate donePredicate) {
      BlockingQueue blockingQueue = new LinkedBlockingQueue();
      Objects.requireNonNull(blockingQueue);
      Object object = resultFactory.apply(blockingQueue::add);

      while(!donePredicate.test(object)) {
         try {
            Runnable runnable = (Runnable)blockingQueue.poll(100L, TimeUnit.MILLISECONDS);
            if (runnable != null) {
               runnable.run();
            }
         } catch (InterruptedException var5) {
            LOGGER.warn("Interrupted wait");
            break;
         }
      }

      int i = blockingQueue.size();
      if (i > 0) {
         LOGGER.warn("Tasks left in queue: {}", i);
      }

      return object;
   }

   public static ToIntFunction lastIndexGetter(List values) {
      int i = values.size();
      if (i < 8) {
         Objects.requireNonNull(values);
         return values::indexOf;
      } else {
         Object2IntMap object2IntMap = new Object2IntOpenHashMap(i);
         object2IntMap.defaultReturnValue(-1);

         for(int j = 0; j < i; ++j) {
            object2IntMap.put(values.get(j), j);
         }

         return object2IntMap;
      }
   }

   public static ToIntFunction lastIdentityIndexGetter(List values) {
      int i = values.size();
      if (i < 8) {
         ReferenceList referenceList = new ReferenceImmutableList(values);
         Objects.requireNonNull(referenceList);
         return referenceList::indexOf;
      } else {
         Reference2IntMap reference2IntMap = new Reference2IntOpenHashMap(i);
         reference2IntMap.defaultReturnValue(-1);

         for(int j = 0; j < i; ++j) {
            reference2IntMap.put(values.get(j), j);
         }

         return reference2IntMap;
      }
   }

   public static Typed apply(Typed typed, Type type, UnaryOperator modifier) {
      Dynamic dynamic = (Dynamic)typed.write().getOrThrow();
      return readTyped(type, (Dynamic)modifier.apply(dynamic), true);
   }

   public static Typed readTyped(Type type, Dynamic value) {
      return readTyped(type, value, false);
   }

   public static Typed readTyped(Type type, Dynamic value, boolean allowPartial) {
      DataResult dataResult = type.readTyped(value).map(com.mojang.datafixers.util.Pair::getFirst);

      try {
         return allowPartial ? (Typed)dataResult.getPartialOrThrow(IllegalStateException::new) : (Typed)dataResult.getOrThrow(IllegalStateException::new);
      } catch (IllegalStateException var7) {
         CrashReport crashReport = CrashReport.create(var7, "Reading type");
         CrashReportSection crashReportSection = crashReport.addElement("Info");
         crashReportSection.add("Data", (Object)value);
         crashReportSection.add("Type", (Object)type);
         throw new CrashException(crashReport);
      }
   }

   public static List withAppended(List list, Object valueToAppend) {
      return ImmutableList.builderWithExpectedSize(list.size() + 1).addAll(list).add(valueToAppend).build();
   }

   public static List withPrepended(Object valueToPrepend, List list) {
      return ImmutableList.builderWithExpectedSize(list.size() + 1).add(valueToPrepend).addAll(list).build();
   }

   public static Map mapWith(Map map, Object keyToAppend, Object valueToAppend) {
      return ImmutableMap.builderWithExpectedSize(map.size() + 1).putAll(map).put(keyToAppend, valueToAppend).buildKeepingLast();
   }

   static {
      DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss", Locale.ROOT);
      SUPPORTED_URI_PROTOCOLS = Set.of("http", "https");
      nanoTimeSupplier = System::nanoTime;
      TICKER = new Ticker() {
         public long read() {
            return Util.nanoTimeSupplier.getAsLong();
         }
      };
      NIL_UUID = new UUID(0L, 0L);
      JAR_FILE_SYSTEM_PROVIDER = (FileSystemProvider)FileSystemProvider.installedProviders().stream().filter((fileSystemProvider) -> {
         return fileSystemProvider.getScheme().equalsIgnoreCase("jar");
      }).findFirst().orElseThrow(() -> {
         return new IllegalStateException("No jar file system provider found");
      });
      missingBreakpointHandler = (message) -> {
      };
   }

   public static enum OperatingSystem {
      LINUX("linux"),
      SOLARIS("solaris"),
      WINDOWS("windows") {
         protected String[] getURIOpenCommand(URI uri) {
            return new String[]{"rundll32", "url.dll,FileProtocolHandler", uri.toString()};
         }
      },
      OSX("mac") {
         protected String[] getURIOpenCommand(URI uri) {
            return new String[]{"open", uri.toString()};
         }
      },
      UNKNOWN("unknown");

      private final String name;

      OperatingSystem(final String name) {
         this.name = name;
      }

      public void open(URI uri) {
         try {
            Process process = (Process)AccessController.doPrivileged(() -> {
               return Runtime.getRuntime().exec(this.getURIOpenCommand(uri));
            });
            process.getInputStream().close();
            process.getErrorStream().close();
            process.getOutputStream().close();
         } catch (IOException | PrivilegedActionException var3) {
            Util.LOGGER.error("Couldn't open location '{}'", uri, var3);
         }

      }

      public void open(File file) {
         this.open(file.toURI());
      }

      public void open(Path path) {
         this.open(path.toUri());
      }

      protected String[] getURIOpenCommand(URI uri) {
         String string = uri.toString();
         if ("file".equals(uri.getScheme())) {
            string = string.replace("file:", "file://");
         }

         return new String[]{"xdg-open", string};
      }

      public void open(String uri) {
         try {
            this.open(new URI(uri));
         } catch (IllegalArgumentException | URISyntaxException var3) {
            Util.LOGGER.error("Couldn't open uri '{}'", uri, var3);
         }

      }

      public String getName() {
         return this.name;
      }

      // $FF: synthetic method
      private static OperatingSystem[] method_36579() {
         return new OperatingSystem[]{LINUX, SOLARIS, WINDOWS, OSX, UNKNOWN};
      }
   }
}
