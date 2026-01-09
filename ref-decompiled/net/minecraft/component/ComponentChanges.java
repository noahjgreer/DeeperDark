package net.minecraft.component;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;

public final class ComponentChanges {
   public static final ComponentChanges EMPTY = new ComponentChanges(Reference2ObjectMaps.emptyMap());
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   public static final PacketCodec LENGTH_PREPENDED_PACKET_CODEC;
   private static final String REMOVE_PREFIX = "!";
   final Reference2ObjectMap changedComponents;

   private static PacketCodec createPacketCodec(final PacketCodecFunction packetCodecFunction) {
      return new PacketCodec() {
         public ComponentChanges decode(RegistryByteBuf registryByteBuf) {
            int i = registryByteBuf.readVarInt();
            int j = registryByteBuf.readVarInt();
            if (i == 0 && j == 0) {
               return ComponentChanges.EMPTY;
            } else {
               int k = i + j;
               Reference2ObjectMap reference2ObjectMap = new Reference2ObjectArrayMap(Math.min(k, 65536));

               int l;
               ComponentType componentType;
               for(l = 0; l < i; ++l) {
                  componentType = (ComponentType)ComponentType.PACKET_CODEC.decode(registryByteBuf);
                  Object object = packetCodecFunction.apply(componentType).decode(registryByteBuf);
                  reference2ObjectMap.put(componentType, Optional.of(object));
               }

               for(l = 0; l < j; ++l) {
                  componentType = (ComponentType)ComponentType.PACKET_CODEC.decode(registryByteBuf);
                  reference2ObjectMap.put(componentType, Optional.empty());
               }

               return new ComponentChanges(reference2ObjectMap);
            }
         }

         public void encode(RegistryByteBuf registryByteBuf, ComponentChanges componentChanges) {
            if (componentChanges.isEmpty()) {
               registryByteBuf.writeVarInt(0);
               registryByteBuf.writeVarInt(0);
            } else {
               int i = 0;
               int j = 0;
               ObjectIterator var5 = Reference2ObjectMaps.fastIterable(componentChanges.changedComponents).iterator();

               Reference2ObjectMap.Entry entry;
               while(var5.hasNext()) {
                  entry = (Reference2ObjectMap.Entry)var5.next();
                  if (((Optional)entry.getValue()).isPresent()) {
                     ++i;
                  } else {
                     ++j;
                  }
               }

               registryByteBuf.writeVarInt(i);
               registryByteBuf.writeVarInt(j);
               var5 = Reference2ObjectMaps.fastIterable(componentChanges.changedComponents).iterator();

               while(var5.hasNext()) {
                  entry = (Reference2ObjectMap.Entry)var5.next();
                  Optional optional = (Optional)entry.getValue();
                  if (optional.isPresent()) {
                     ComponentType componentType = (ComponentType)entry.getKey();
                     ComponentType.PACKET_CODEC.encode(registryByteBuf, componentType);
                     this.encode(registryByteBuf, componentType, optional.get());
                  }
               }

               var5 = Reference2ObjectMaps.fastIterable(componentChanges.changedComponents).iterator();

               while(var5.hasNext()) {
                  entry = (Reference2ObjectMap.Entry)var5.next();
                  if (((Optional)entry.getValue()).isEmpty()) {
                     ComponentType componentType2 = (ComponentType)entry.getKey();
                     ComponentType.PACKET_CODEC.encode(registryByteBuf, componentType2);
                  }
               }

            }
         }

         private void encode(RegistryByteBuf buf, ComponentType type, Object value) {
            packetCodecFunction.apply(type).encode(buf, value);
         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((RegistryByteBuf)object, (ComponentChanges)object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((RegistryByteBuf)object);
         }
      };
   }

   ComponentChanges(Reference2ObjectMap changedComponents) {
      this.changedComponents = changedComponents;
   }

   public static Builder builder() {
      return new Builder();
   }

   @Nullable
   public Optional get(ComponentType type) {
      return (Optional)this.changedComponents.get(type);
   }

   public Set entrySet() {
      return this.changedComponents.entrySet();
   }

   public int size() {
      return this.changedComponents.size();
   }

   public ComponentChanges withRemovedIf(Predicate removedTypePredicate) {
      if (this.isEmpty()) {
         return EMPTY;
      } else {
         Reference2ObjectMap reference2ObjectMap = new Reference2ObjectArrayMap(this.changedComponents);
         reference2ObjectMap.keySet().removeIf(removedTypePredicate);
         return reference2ObjectMap.isEmpty() ? EMPTY : new ComponentChanges(reference2ObjectMap);
      }
   }

   public boolean isEmpty() {
      return this.changedComponents.isEmpty();
   }

   public AddedRemovedPair toAddedRemovedPair() {
      if (this.isEmpty()) {
         return ComponentChanges.AddedRemovedPair.EMPTY;
      } else {
         ComponentMap.Builder builder = ComponentMap.builder();
         Set set = Sets.newIdentityHashSet();
         this.changedComponents.forEach((type, value) -> {
            if (value.isPresent()) {
               builder.put(type, value.get());
            } else {
               set.add(type);
            }

         });
         return new AddedRemovedPair(builder.build(), set);
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof ComponentChanges) {
            ComponentChanges componentChanges = (ComponentChanges)o;
            if (this.changedComponents.equals(componentChanges.changedComponents)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.changedComponents.hashCode();
   }

   public String toString() {
      return toString(this.changedComponents);
   }

   static String toString(Reference2ObjectMap changes) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append('{');
      boolean bl = true;
      ObjectIterator var3 = Reference2ObjectMaps.fastIterable(changes).iterator();

      while(var3.hasNext()) {
         Map.Entry entry = (Map.Entry)var3.next();
         if (bl) {
            bl = false;
         } else {
            stringBuilder.append(", ");
         }

         Optional optional = (Optional)entry.getValue();
         if (optional.isPresent()) {
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=>");
            stringBuilder.append(optional.get());
         } else {
            stringBuilder.append("!");
            stringBuilder.append(entry.getKey());
         }
      }

      stringBuilder.append('}');
      return stringBuilder.toString();
   }

   static {
      CODEC = Codec.dispatchedMap(ComponentChanges.Type.CODEC, Type::getValueCodec).xmap((changes) -> {
         if (changes.isEmpty()) {
            return EMPTY;
         } else {
            Reference2ObjectMap reference2ObjectMap = new Reference2ObjectArrayMap(changes.size());
            Iterator var2 = changes.entrySet().iterator();

            while(var2.hasNext()) {
               Map.Entry entry = (Map.Entry)var2.next();
               Type type = (Type)entry.getKey();
               if (type.removed()) {
                  reference2ObjectMap.put(type.type(), Optional.empty());
               } else {
                  reference2ObjectMap.put(type.type(), Optional.of(entry.getValue()));
               }
            }

            return new ComponentChanges(reference2ObjectMap);
         }
      }, (changes) -> {
         Reference2ObjectMap reference2ObjectMap = new Reference2ObjectArrayMap(changes.changedComponents.size());
         ObjectIterator var2 = Reference2ObjectMaps.fastIterable(changes.changedComponents).iterator();

         while(var2.hasNext()) {
            Map.Entry entry = (Map.Entry)var2.next();
            ComponentType componentType = (ComponentType)entry.getKey();
            if (!componentType.shouldSkipSerialization()) {
               Optional optional = (Optional)entry.getValue();
               if (optional.isPresent()) {
                  reference2ObjectMap.put(new Type(componentType, false), optional.get());
               } else {
                  reference2ObjectMap.put(new Type(componentType, true), Unit.INSTANCE);
               }
            }
         }

         return reference2ObjectMap;
      });
      PACKET_CODEC = createPacketCodec(new PacketCodecFunction() {
         public PacketCodec apply(ComponentType componentType) {
            return componentType.getPacketCodec().cast();
         }
      });
      LENGTH_PREPENDED_PACKET_CODEC = createPacketCodec(new PacketCodecFunction() {
         public PacketCodec apply(ComponentType componentType) {
            PacketCodec packetCodec = componentType.getPacketCodec().cast();
            return packetCodec.collect(PacketCodecs.lengthPrependedRegistry(Integer.MAX_VALUE));
         }
      });
   }

   @FunctionalInterface
   private interface PacketCodecFunction {
      PacketCodec apply(ComponentType type);
   }

   public static class Builder {
      private final Reference2ObjectMap changes = new Reference2ObjectArrayMap();

      Builder() {
      }

      public Builder add(ComponentType type, Object value) {
         this.changes.put(type, Optional.of(value));
         return this;
      }

      public Builder remove(ComponentType type) {
         this.changes.put(type, Optional.empty());
         return this;
      }

      public Builder add(Component component) {
         return this.add(component.type(), component.value());
      }

      public ComponentChanges build() {
         return this.changes.isEmpty() ? ComponentChanges.EMPTY : new ComponentChanges(this.changes);
      }
   }

   public static record AddedRemovedPair(ComponentMap added, Set removed) {
      public static final AddedRemovedPair EMPTY;

      public AddedRemovedPair(ComponentMap componentMap, Set set) {
         this.added = componentMap;
         this.removed = set;
      }

      public ComponentMap added() {
         return this.added;
      }

      public Set removed() {
         return this.removed;
      }

      static {
         EMPTY = new AddedRemovedPair(ComponentMap.EMPTY, Set.of());
      }
   }

   static record Type(ComponentType type, boolean removed) {
      public static final Codec CODEC;

      Type(ComponentType componentType, boolean bl) {
         this.type = componentType;
         this.removed = bl;
      }

      public Codec getValueCodec() {
         return this.removed ? Codec.EMPTY.codec() : this.type.getCodecOrThrow();
      }

      public ComponentType type() {
         return this.type;
      }

      public boolean removed() {
         return this.removed;
      }

      static {
         CODEC = Codec.STRING.flatXmap((id) -> {
            boolean bl = id.startsWith("!");
            if (bl) {
               id = id.substring("!".length());
            }

            Identifier identifier = Identifier.tryParse(id);
            ComponentType componentType = (ComponentType)Registries.DATA_COMPONENT_TYPE.get(identifier);
            if (componentType == null) {
               return DataResult.error(() -> {
                  return "No component with type: '" + String.valueOf(identifier) + "'";
               });
            } else {
               return componentType.shouldSkipSerialization() ? DataResult.error(() -> {
                  return "'" + String.valueOf(identifier) + "' is not a persistent component";
               }) : DataResult.success(new Type(componentType, bl));
            }
         }, (type) -> {
            ComponentType componentType = type.type();
            Identifier identifier = Registries.DATA_COMPONENT_TYPE.getId(componentType);
            return identifier == null ? DataResult.error(() -> {
               return "Unregistered component: " + String.valueOf(componentType);
            }) : DataResult.success(type.removed() ? "!" + String.valueOf(identifier) : identifier.toString());
         });
      }
   }
}
