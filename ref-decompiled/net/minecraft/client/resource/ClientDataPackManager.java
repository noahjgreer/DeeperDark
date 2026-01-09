package net.minecraft.client.resource;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaDataPackProvider;

@Environment(EnvType.CLIENT)
public class ClientDataPackManager {
   private final ResourcePackManager packManager = VanillaDataPackProvider.createClientManager();
   private final Map knownPacks;

   public ClientDataPackManager() {
      this.packManager.scanPacks();
      ImmutableMap.Builder builder = ImmutableMap.builder();
      this.packManager.getProfiles().forEach((resourcePackProfile) -> {
         ResourcePackInfo resourcePackInfo = resourcePackProfile.getInfo();
         resourcePackInfo.knownPackInfo().ifPresent((knownPackInfo) -> {
            builder.put(knownPackInfo, resourcePackInfo.id());
         });
      });
      this.knownPacks = builder.build();
   }

   public List getCommonKnownPacks(List serverKnownPacks) {
      List list = new ArrayList(serverKnownPacks.size());
      List list2 = new ArrayList(serverKnownPacks.size());
      Iterator var4 = serverKnownPacks.iterator();

      while(var4.hasNext()) {
         VersionedIdentifier versionedIdentifier = (VersionedIdentifier)var4.next();
         String string = (String)this.knownPacks.get(versionedIdentifier);
         if (string != null) {
            list2.add(string);
            list.add(versionedIdentifier);
         }
      }

      this.packManager.setEnabledProfiles(list2);
      return list;
   }

   public LifecycledResourceManager createResourceManager() {
      List list = this.packManager.createResourcePacks();
      return new LifecycledResourceManagerImpl(ResourceType.SERVER_DATA, list);
   }
}
