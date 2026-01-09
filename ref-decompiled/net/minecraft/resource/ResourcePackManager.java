package net.minecraft.resource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public class ResourcePackManager {
   private final Set providers;
   private Map profiles = ImmutableMap.of();
   private List enabled = ImmutableList.of();

   public ResourcePackManager(ResourcePackProvider... providers) {
      this.providers = ImmutableSet.copyOf(providers);
   }

   public static String listPacks(Collection profiles) {
      return (String)profiles.stream().map((profile) -> {
         String var10000 = profile.getId();
         return var10000 + (profile.getCompatibility().isCompatible() ? "" : " (incompatible)");
      }).collect(Collectors.joining(", "));
   }

   public void scanPacks() {
      List list = (List)this.enabled.stream().map(ResourcePackProfile::getId).collect(ImmutableList.toImmutableList());
      this.profiles = this.providePackProfiles();
      this.enabled = this.buildEnabledProfiles(list);
   }

   private Map providePackProfiles() {
      Map map = Maps.newTreeMap();
      Iterator var2 = this.providers.iterator();

      while(var2.hasNext()) {
         ResourcePackProvider resourcePackProvider = (ResourcePackProvider)var2.next();
         resourcePackProvider.register((profile) -> {
            map.put(profile.getId(), profile);
         });
      }

      return ImmutableMap.copyOf(map);
   }

   public boolean hasOptionalProfilesEnabled() {
      List list = this.buildEnabledProfiles(List.of());
      return !this.enabled.equals(list);
   }

   public void setEnabledProfiles(Collection enabled) {
      this.enabled = this.buildEnabledProfiles(enabled);
   }

   public boolean enable(String profile) {
      ResourcePackProfile resourcePackProfile = (ResourcePackProfile)this.profiles.get(profile);
      if (resourcePackProfile != null && !this.enabled.contains(resourcePackProfile)) {
         List list = Lists.newArrayList(this.enabled);
         list.add(resourcePackProfile);
         this.enabled = list;
         return true;
      } else {
         return false;
      }
   }

   public boolean disable(String profile) {
      ResourcePackProfile resourcePackProfile = (ResourcePackProfile)this.profiles.get(profile);
      if (resourcePackProfile != null && this.enabled.contains(resourcePackProfile)) {
         List list = Lists.newArrayList(this.enabled);
         list.remove(resourcePackProfile);
         this.enabled = list;
         return true;
      } else {
         return false;
      }
   }

   private List buildEnabledProfiles(Collection enabledNames) {
      List list = (List)this.streamProfilesById(enabledNames).collect(Util.toArrayList());
      Iterator var3 = this.profiles.values().iterator();

      while(var3.hasNext()) {
         ResourcePackProfile resourcePackProfile = (ResourcePackProfile)var3.next();
         if (resourcePackProfile.isRequired() && !list.contains(resourcePackProfile)) {
            resourcePackProfile.getInitialPosition().insert(list, resourcePackProfile, ResourcePackProfile::getPosition, false);
         }
      }

      return ImmutableList.copyOf(list);
   }

   private Stream streamProfilesById(Collection ids) {
      Stream var10000 = ids.stream();
      Map var10001 = this.profiles;
      Objects.requireNonNull(var10001);
      return var10000.map(var10001::get).filter(Objects::nonNull);
   }

   public Collection getIds() {
      return this.profiles.keySet();
   }

   public Collection getProfiles() {
      return this.profiles.values();
   }

   public Collection getEnabledIds() {
      return (Collection)this.enabled.stream().map(ResourcePackProfile::getId).collect(ImmutableSet.toImmutableSet());
   }

   public FeatureSet getRequestedFeatures() {
      return (FeatureSet)this.getEnabledProfiles().stream().map(ResourcePackProfile::getRequestedFeatures).reduce(FeatureSet::combine).orElse(FeatureSet.empty());
   }

   public Collection getEnabledProfiles() {
      return this.enabled;
   }

   @Nullable
   public ResourcePackProfile getProfile(String id) {
      return (ResourcePackProfile)this.profiles.get(id);
   }

   public boolean hasProfile(String id) {
      return this.profiles.containsKey(id);
   }

   public List createResourcePacks() {
      return (List)this.enabled.stream().map(ResourcePackProfile::createResourcePack).collect(ImmutableList.toImmutableList());
   }
}
