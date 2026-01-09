package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.test.TestInstanceUtil;
import net.minecraft.util.FixedBufferInputStream;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.path.PathUtil;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class StructureTemplateManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String STRUCTURE_DIRECTORY = "structure";
   private static final String STRUCTURES_DIRECTORY = "structures";
   private static final String NBT_FILE_EXTENSION = ".nbt";
   private static final String SNBT_FILE_EXTENSION = ".snbt";
   private final Map templates = Maps.newConcurrentMap();
   private final DataFixer dataFixer;
   private ResourceManager resourceManager;
   private final Path generatedPath;
   private final List providers;
   private final RegistryEntryLookup blockLookup;
   private static final ResourceFinder STRUCTURE_NBT_RESOURCE_FINDER = new ResourceFinder("structure", ".nbt");

   public StructureTemplateManager(ResourceManager resourceManager, LevelStorage.Session session, DataFixer dataFixer, RegistryEntryLookup blockLookup) {
      this.resourceManager = resourceManager;
      this.dataFixer = dataFixer;
      this.generatedPath = session.getDirectory(WorldSavePath.GENERATED).normalize();
      this.blockLookup = blockLookup;
      ImmutableList.Builder builder = ImmutableList.builder();
      builder.add(new Provider(this::loadTemplateFromFile, this::streamTemplatesFromFile));
      if (SharedConstants.isDevelopment) {
         builder.add(new Provider(this::loadTemplateFromGameTestFile, this::streamTemplatesFromGameTestFile));
      }

      builder.add(new Provider(this::loadTemplateFromResource, this::streamTemplatesFromResource));
      this.providers = builder.build();
   }

   public StructureTemplate getTemplateOrBlank(Identifier id) {
      Optional optional = this.getTemplate(id);
      if (optional.isPresent()) {
         return (StructureTemplate)optional.get();
      } else {
         StructureTemplate structureTemplate = new StructureTemplate();
         this.templates.put(id, Optional.of(structureTemplate));
         return structureTemplate;
      }
   }

   public Optional getTemplate(Identifier id) {
      return (Optional)this.templates.computeIfAbsent(id, this::loadTemplate);
   }

   public Stream streamTemplates() {
      return this.providers.stream().flatMap((provider) -> {
         return (Stream)provider.lister().get();
      }).distinct();
   }

   private Optional loadTemplate(Identifier id) {
      Iterator var2 = this.providers.iterator();

      while(var2.hasNext()) {
         Provider provider = (Provider)var2.next();

         try {
            Optional optional = (Optional)provider.loader().apply(id);
            if (optional.isPresent()) {
               return optional;
            }
         } catch (Exception var5) {
         }
      }

      return Optional.empty();
   }

   public void setResourceManager(ResourceManager resourceManager) {
      this.resourceManager = resourceManager;
      this.templates.clear();
   }

   private Optional loadTemplateFromResource(Identifier id) {
      Identifier identifier = STRUCTURE_NBT_RESOURCE_FINDER.toResourcePath(id);
      return this.loadTemplate(() -> {
         return this.resourceManager.open(identifier);
      }, (throwable) -> {
         LOGGER.error("Couldn't load structure {}", id, throwable);
      });
   }

   private Stream streamTemplatesFromResource() {
      Stream var10000 = STRUCTURE_NBT_RESOURCE_FINDER.findResources(this.resourceManager).keySet().stream();
      ResourceFinder var10001 = STRUCTURE_NBT_RESOURCE_FINDER;
      Objects.requireNonNull(var10001);
      return var10000.map(var10001::toResourceId);
   }

   private Optional loadTemplateFromGameTestFile(Identifier id) {
      return this.loadTemplateFromSnbt(id, TestInstanceUtil.testStructuresDirectoryName);
   }

   private Stream streamTemplatesFromGameTestFile() {
      if (!Files.isDirectory(TestInstanceUtil.testStructuresDirectoryName, new LinkOption[0])) {
         return Stream.empty();
      } else {
         List list = new ArrayList();
         Path var10001 = TestInstanceUtil.testStructuresDirectoryName;
         Objects.requireNonNull(list);
         this.streamTemplates(var10001, "minecraft", ".snbt", list::add);
         return list.stream();
      }
   }

   private Optional loadTemplateFromFile(Identifier id) {
      if (!Files.isDirectory(this.generatedPath, new LinkOption[0])) {
         return Optional.empty();
      } else {
         Path path = this.getTemplatePath(id, ".nbt");
         return this.loadTemplate(() -> {
            return new FileInputStream(path.toFile());
         }, (throwable) -> {
            LOGGER.error("Couldn't load structure from {}", path, throwable);
         });
      }
   }

   private Stream streamTemplatesFromFile() {
      if (!Files.isDirectory(this.generatedPath, new LinkOption[0])) {
         return Stream.empty();
      } else {
         try {
            List list = new ArrayList();
            DirectoryStream directoryStream = Files.newDirectoryStream(this.generatedPath, (pathx) -> {
               return Files.isDirectory(pathx, new LinkOption[0]);
            });

            try {
               Iterator var3 = directoryStream.iterator();

               while(var3.hasNext()) {
                  Path path = (Path)var3.next();
                  String string = path.getFileName().toString();
                  Path path2 = path.resolve("structures");
                  Objects.requireNonNull(list);
                  this.streamTemplates(path2, string, ".nbt", list::add);
               }
            } catch (Throwable var8) {
               if (directoryStream != null) {
                  try {
                     directoryStream.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (directoryStream != null) {
               directoryStream.close();
            }

            return list.stream();
         } catch (IOException var9) {
            return Stream.empty();
         }
      }
   }

   private void streamTemplates(Path directory, String namespace, String fileExtension, Consumer idConsumer) {
      int i = fileExtension.length();
      Function function = (filename) -> {
         return filename.substring(0, filename.length() - i);
      };

      try {
         Stream stream = Files.find(directory, Integer.MAX_VALUE, (path, attributes) -> {
            return attributes.isRegularFile() && path.toString().endsWith(fileExtension);
         }, new FileVisitOption[0]);

         try {
            stream.forEach((path) -> {
               try {
                  idConsumer.accept(Identifier.of(namespace, (String)function.apply(this.toRelativePath(directory, path))));
               } catch (InvalidIdentifierException var7) {
                  LOGGER.error("Invalid location while listing folder {} contents", directory, var7);
               }

            });
         } catch (Throwable var11) {
            if (stream != null) {
               try {
                  stream.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (stream != null) {
            stream.close();
         }
      } catch (IOException var12) {
         LOGGER.error("Failed to list folder {} contents", directory, var12);
      }

   }

   private String toRelativePath(Path root, Path path) {
      return root.relativize(path).toString().replace(File.separator, "/");
   }

   private Optional loadTemplateFromSnbt(Identifier id, Path path) {
      if (!Files.isDirectory(path, new LinkOption[0])) {
         return Optional.empty();
      } else {
         Path path2 = PathUtil.getResourcePath(path, id.getPath(), ".snbt");

         try {
            BufferedReader bufferedReader = Files.newBufferedReader(path2);

            Optional var6;
            try {
               String string = IOUtils.toString(bufferedReader);
               var6 = Optional.of(this.createTemplate(NbtHelper.fromNbtProviderString(string)));
            } catch (Throwable var8) {
               if (bufferedReader != null) {
                  try {
                     bufferedReader.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (bufferedReader != null) {
               bufferedReader.close();
            }

            return var6;
         } catch (NoSuchFileException var9) {
            return Optional.empty();
         } catch (CommandSyntaxException | IOException var10) {
            LOGGER.error("Couldn't load structure from {}", path2, var10);
            return Optional.empty();
         }
      }
   }

   private Optional loadTemplate(TemplateFileOpener opener, Consumer exceptionConsumer) {
      try {
         InputStream inputStream = opener.open();

         Optional var5;
         try {
            InputStream inputStream2 = new FixedBufferInputStream(inputStream);

            try {
               var5 = Optional.of(this.readTemplate(inputStream2));
            } catch (Throwable var9) {
               try {
                  inputStream2.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }

               throw var9;
            }

            inputStream2.close();
         } catch (Throwable var10) {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (Throwable var7) {
                  var10.addSuppressed(var7);
               }
            }

            throw var10;
         }

         if (inputStream != null) {
            inputStream.close();
         }

         return var5;
      } catch (FileNotFoundException var11) {
         return Optional.empty();
      } catch (Throwable var12) {
         exceptionConsumer.accept(var12);
         return Optional.empty();
      }
   }

   private StructureTemplate readTemplate(InputStream templateIInputStream) throws IOException {
      NbtCompound nbtCompound = NbtIo.readCompressed(templateIInputStream, NbtSizeTracker.ofUnlimitedBytes());
      return this.createTemplate(nbtCompound);
   }

   public StructureTemplate createTemplate(NbtCompound nbt) {
      StructureTemplate structureTemplate = new StructureTemplate();
      int i = NbtHelper.getDataVersion((NbtCompound)nbt, 500);
      structureTemplate.readNbt(this.blockLookup, DataFixTypes.STRUCTURE.update(this.dataFixer, nbt, i));
      return structureTemplate;
   }

   public boolean saveTemplate(Identifier id) {
      Optional optional = (Optional)this.templates.get(id);
      if (optional.isEmpty()) {
         return false;
      } else {
         StructureTemplate structureTemplate = (StructureTemplate)optional.get();
         Path path = this.getTemplatePath(id, ".nbt");
         Path path2 = path.getParent();
         if (path2 == null) {
            return false;
         } else {
            try {
               Files.createDirectories(Files.exists(path2, new LinkOption[0]) ? path2.toRealPath() : path2);
            } catch (IOException var13) {
               LOGGER.error("Failed to create parent directory: {}", path2);
               return false;
            }

            NbtCompound nbtCompound = structureTemplate.writeNbt(new NbtCompound());

            try {
               OutputStream outputStream = new FileOutputStream(path.toFile());

               try {
                  NbtIo.writeCompressed(nbtCompound, (OutputStream)outputStream);
               } catch (Throwable var11) {
                  try {
                     outputStream.close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }

                  throw var11;
               }

               outputStream.close();
               return true;
            } catch (Throwable var12) {
               return false;
            }
         }
      }
   }

   public Path getTemplatePath(Identifier id, String extension) {
      if (id.getPath().contains("//")) {
         throw new InvalidIdentifierException("Invalid resource path: " + String.valueOf(id));
      } else {
         try {
            Path path = this.generatedPath.resolve(id.getNamespace());
            Path path2 = path.resolve("structures");
            Path path3 = PathUtil.getResourcePath(path2, id.getPath(), extension);
            if (path3.startsWith(this.generatedPath) && PathUtil.isNormal(path3) && PathUtil.isAllowedName(path3)) {
               return path3;
            } else {
               throw new InvalidIdentifierException("Invalid resource path: " + String.valueOf(path3));
            }
         } catch (InvalidPathException var6) {
            throw new InvalidIdentifierException("Invalid resource path: " + String.valueOf(id), var6);
         }
      }
   }

   public void unloadTemplate(Identifier id) {
      this.templates.remove(id);
   }

   static record Provider(Function loader, Supplier lister) {
      Provider(Function function, Supplier supplier) {
         this.loader = function;
         this.lister = supplier;
      }

      public Function loader() {
         return this.loader;
      }

      public Supplier lister() {
         return this.lister;
      }
   }

   @FunctionalInterface
   interface TemplateFileOpener {
      InputStream open() throws IOException;
   }
}
