package net.minecraft.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class NamespaceResourceManager implements ResourceManager {
   static final Logger LOGGER = LogUtils.getLogger();
   protected final List packList = Lists.newArrayList();
   private final ResourceType type;
   private final String namespace;

   public NamespaceResourceManager(ResourceType type, String namespace) {
      this.type = type;
      this.namespace = namespace;
   }

   public void addPack(ResourcePack pack) {
      this.addPack(pack.getId(), pack, (Predicate)null);
   }

   public void addPack(ResourcePack pack, Predicate filter) {
      this.addPack(pack.getId(), pack, filter);
   }

   public void addPack(String id, Predicate filter) {
      this.addPack(id, (ResourcePack)null, filter);
   }

   private void addPack(String id, @Nullable ResourcePack underlyingPack, @Nullable Predicate filter) {
      this.packList.add(new FilterablePack(id, underlyingPack, filter));
   }

   public Set getAllNamespaces() {
      return ImmutableSet.of(this.namespace);
   }

   public Optional getResource(Identifier identifier) {
      for(int i = this.packList.size() - 1; i >= 0; --i) {
         FilterablePack filterablePack = (FilterablePack)this.packList.get(i);
         ResourcePack resourcePack = filterablePack.underlying;
         if (resourcePack != null) {
            InputSupplier inputSupplier = resourcePack.open(this.type, identifier);
            if (inputSupplier != null) {
               InputSupplier inputSupplier2 = this.createMetadataSupplier(identifier, i);
               return Optional.of(createResource(resourcePack, identifier, inputSupplier, inputSupplier2));
            }
         }

         if (filterablePack.isFiltered(identifier)) {
            LOGGER.warn("Resource {} not found, but was filtered by pack {}", identifier, filterablePack.name);
            return Optional.empty();
         }
      }

      return Optional.empty();
   }

   private static Resource createResource(ResourcePack pack, Identifier id, InputSupplier supplier, InputSupplier metadataSupplier) {
      return new Resource(pack, wrapForDebug(id, pack, supplier), metadataSupplier);
   }

   private static InputSupplier wrapForDebug(Identifier id, ResourcePack pack, InputSupplier supplier) {
      return LOGGER.isDebugEnabled() ? () -> {
         return new DebugInputStream((InputStream)supplier.get(), id, pack.getId());
      } : supplier;
   }

   public List getAllResources(Identifier id) {
      Identifier identifier = getMetadataPath(id);
      List list = new ArrayList();
      boolean bl = false;
      String string = null;

      for(int i = this.packList.size() - 1; i >= 0; --i) {
         FilterablePack filterablePack = (FilterablePack)this.packList.get(i);
         ResourcePack resourcePack = filterablePack.underlying;
         if (resourcePack != null) {
            InputSupplier inputSupplier = resourcePack.open(this.type, id);
            if (inputSupplier != null) {
               InputSupplier inputSupplier2;
               if (bl) {
                  inputSupplier2 = ResourceMetadata.NONE_SUPPLIER;
               } else {
                  inputSupplier2 = () -> {
                     InputSupplier inputSupplier = resourcePack.open(this.type, identifier);
                     return inputSupplier != null ? loadMetadata(inputSupplier) : ResourceMetadata.NONE;
                  };
               }

               list.add(new Resource(resourcePack, inputSupplier, inputSupplier2));
            }
         }

         if (filterablePack.isFiltered(id)) {
            string = filterablePack.name;
            break;
         }

         if (filterablePack.isFiltered(identifier)) {
            bl = true;
         }
      }

      if (list.isEmpty() && string != null) {
         LOGGER.warn("Resource {} not found, but was filtered by pack {}", id, string);
      }

      return Lists.reverse(list);
   }

   private static boolean isMcmeta(Identifier id) {
      return id.getPath().endsWith(".mcmeta");
   }

   private static Identifier getMetadataFileName(Identifier id) {
      String string = id.getPath().substring(0, id.getPath().length() - ".mcmeta".length());
      return id.withPath(string);
   }

   static Identifier getMetadataPath(Identifier id) {
      return id.withPath(id.getPath() + ".mcmeta");
   }

   public Map findResources(String startingPath, Predicate allowedPathPredicate) {
      Map map = new HashMap();
      Map map2 = new HashMap();
      int i = this.packList.size();

      for(int j = 0; j < i; ++j) {
         FilterablePack filterablePack = (FilterablePack)this.packList.get(j);
         filterablePack.removeFiltered(map.keySet());
         filterablePack.removeFiltered(map2.keySet());
         ResourcePack resourcePack = filterablePack.underlying;
         if (resourcePack != null) {
            resourcePack.findResources(this.type, this.namespace, startingPath, (id, supplier) -> {
               record Result(ResourcePack pack, InputSupplier supplier, int packIndex) {
                  final ResourcePack pack;
                  final InputSupplier supplier;
                  final int packIndex;

                  Result(ResourcePack resourcePack, InputSupplier inputSupplier, int i) {
                     this.pack = resourcePack;
                     this.supplier = inputSupplier;
                     this.packIndex = i;
                  }

                  public ResourcePack pack() {
                     return this.pack;
                  }

                  public InputSupplier supplier() {
                     return this.supplier;
                  }

                  public int packIndex() {
                     return this.packIndex;
                  }
               }

               if (isMcmeta(id)) {
                  if (allowedPathPredicate.test(getMetadataFileName(id))) {
                     map2.put(id, new Result(resourcePack, supplier, j));
                  }
               } else if (allowedPathPredicate.test(id)) {
                  map.put(id, new Result(resourcePack, supplier, j));
               }

            });
         }
      }

      Map map3 = Maps.newTreeMap();
      map.forEach((id, result) -> {
         Identifier identifier = getMetadataPath(id);
         Result result2 = (Result)map2.get(identifier);
         InputSupplier inputSupplier;
         if (result2 != null && result2.packIndex >= result.packIndex) {
            inputSupplier = getMetadataSupplier(result2.supplier);
         } else {
            inputSupplier = ResourceMetadata.NONE_SUPPLIER;
         }

         map3.put(id, createResource(result.pack, id, result.supplier, inputSupplier));
      });
      return map3;
   }

   private InputSupplier createMetadataSupplier(Identifier id, int index) {
      return () -> {
         Identifier identifier2 = getMetadataPath(id);

         for(int j = this.packList.size() - 1; j >= index; --j) {
            FilterablePack filterablePack = (FilterablePack)this.packList.get(j);
            ResourcePack resourcePack = filterablePack.underlying;
            if (resourcePack != null) {
               InputSupplier inputSupplier = resourcePack.open(this.type, identifier2);
               if (inputSupplier != null) {
                  return loadMetadata(inputSupplier);
               }
            }

            if (filterablePack.isFiltered(identifier2)) {
               break;
            }
         }

         return ResourceMetadata.NONE;
      };
   }

   private static InputSupplier getMetadataSupplier(InputSupplier supplier) {
      return () -> {
         return loadMetadata(supplier);
      };
   }

   private static ResourceMetadata loadMetadata(InputSupplier supplier) throws IOException {
      InputStream inputStream = (InputStream)supplier.get();

      ResourceMetadata var2;
      try {
         var2 = ResourceMetadata.create(inputStream);
      } catch (Throwable var5) {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (inputStream != null) {
         inputStream.close();
      }

      return var2;
   }

   private static void applyFilter(FilterablePack pack, Map idToEntryList) {
      Iterator var2 = idToEntryList.values().iterator();

      while(var2.hasNext()) {
         EntryList entryList = (EntryList)var2.next();
         if (pack.isFiltered(entryList.id)) {
            entryList.fileSources.clear();
         } else if (pack.isFiltered(entryList.metadataId())) {
            entryList.metaSources.clear();
         }
      }

   }

   private void findAndAdd(FilterablePack pack, String startingPath, Predicate allowedPathPredicate, Map idToEntryList) {
      ResourcePack resourcePack = pack.underlying;
      if (resourcePack != null) {
         resourcePack.findResources(this.type, this.namespace, startingPath, (id, supplier) -> {
            if (isMcmeta(id)) {
               Identifier identifier = getMetadataFileName(id);
               if (!allowedPathPredicate.test(identifier)) {
                  return;
               }

               ((EntryList)idToEntryList.computeIfAbsent(identifier, EntryList::new)).metaSources.put(resourcePack, supplier);
            } else {
               if (!allowedPathPredicate.test(id)) {
                  return;
               }

               ((EntryList)idToEntryList.computeIfAbsent(id, EntryList::new)).fileSources.add(new FileSource(resourcePack, supplier));
            }

         });
      }
   }

   public Map findAllResources(String startingPath, Predicate allowedPathPredicate) {
      Map map = Maps.newHashMap();
      Iterator var4 = this.packList.iterator();

      while(var4.hasNext()) {
         FilterablePack filterablePack = (FilterablePack)var4.next();
         applyFilter(filterablePack, map);
         this.findAndAdd(filterablePack, startingPath, allowedPathPredicate, map);
      }

      TreeMap treeMap = Maps.newTreeMap();
      Iterator var14 = map.values().iterator();

      while(true) {
         EntryList entryList;
         do {
            if (!var14.hasNext()) {
               return treeMap;
            }

            entryList = (EntryList)var14.next();
         } while(entryList.fileSources.isEmpty());

         List list = new ArrayList();
         Iterator var8 = entryList.fileSources.iterator();

         while(var8.hasNext()) {
            FileSource fileSource = (FileSource)var8.next();
            ResourcePack resourcePack = fileSource.sourcePack;
            InputSupplier inputSupplier = (InputSupplier)entryList.metaSources.get(resourcePack);
            InputSupplier inputSupplier2 = inputSupplier != null ? getMetadataSupplier(inputSupplier) : ResourceMetadata.NONE_SUPPLIER;
            list.add(createResource(resourcePack, entryList.id, fileSource.supplier, inputSupplier2));
         }

         treeMap.put(entryList.id, list);
      }
   }

   public Stream streamResourcePacks() {
      return this.packList.stream().map((pack) -> {
         return pack.underlying;
      }).filter(Objects::nonNull);
   }

   static record FilterablePack(String name, @Nullable ResourcePack underlying, @Nullable Predicate filter) {
      final String name;
      @Nullable
      final ResourcePack underlying;

      FilterablePack(String string, @Nullable ResourcePack resourcePack, @Nullable Predicate predicate) {
         this.name = string;
         this.underlying = resourcePack;
         this.filter = predicate;
      }

      public void removeFiltered(Collection ids) {
         if (this.filter != null) {
            ids.removeIf(this.filter);
         }

      }

      public boolean isFiltered(Identifier id) {
         return this.filter != null && this.filter.test(id);
      }

      public String name() {
         return this.name;
      }

      @Nullable
      public ResourcePack underlying() {
         return this.underlying;
      }

      @Nullable
      public Predicate filter() {
         return this.filter;
      }
   }

   static record EntryList(Identifier id, Identifier metadataId, List fileSources, Map metaSources) {
      final Identifier id;
      final List fileSources;
      final Map metaSources;

      EntryList(Identifier id) {
         this(id, NamespaceResourceManager.getMetadataPath(id), new ArrayList(), new Object2ObjectArrayMap());
      }

      private EntryList(Identifier identifier, Identifier identifier2, List list, Map map) {
         this.id = identifier;
         this.metadataId = identifier2;
         this.fileSources = list;
         this.metaSources = map;
      }

      public Identifier id() {
         return this.id;
      }

      public Identifier metadataId() {
         return this.metadataId;
      }

      public List fileSources() {
         return this.fileSources;
      }

      public Map metaSources() {
         return this.metaSources;
      }
   }

   static record FileSource(ResourcePack sourcePack, InputSupplier supplier) {
      final ResourcePack sourcePack;
      final InputSupplier supplier;

      FileSource(ResourcePack resourcePack, InputSupplier inputSupplier) {
         this.sourcePack = resourcePack;
         this.supplier = inputSupplier;
      }

      public ResourcePack sourcePack() {
         return this.sourcePack;
      }

      public InputSupplier supplier() {
         return this.supplier;
      }
   }

   static class DebugInputStream extends FilterInputStream {
      private final Supplier leakMessage;
      private boolean closed;

      public DebugInputStream(InputStream parent, Identifier id, String packId) {
         super(parent);
         Exception exception = new Exception("Stacktrace");
         this.leakMessage = () -> {
            StringWriter stringWriter = new StringWriter();
            exception.printStackTrace(new PrintWriter(stringWriter));
            return "Leaked resource: '" + String.valueOf(id) + "' loaded from pack: '" + packId + "'\n" + String.valueOf(stringWriter);
         };
      }

      public void close() throws IOException {
         super.close();
         this.closed = true;
      }

      protected void finalize() throws Throwable {
         if (!this.closed) {
            NamespaceResourceManager.LOGGER.warn("{}", this.leakMessage.get());
         }

         super.finalize();
      }
   }
}
