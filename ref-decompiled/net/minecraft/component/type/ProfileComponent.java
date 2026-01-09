package net.minecraft.component.type;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

public record ProfileComponent(Optional name, Optional uuid, PropertyMap properties, GameProfile gameProfile) {
   private static final Codec BASE_CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codecs.PLAYER_NAME.optionalFieldOf("name").forGetter(ProfileComponent::name), Uuids.INT_STREAM_CODEC.optionalFieldOf("id").forGetter(ProfileComponent::uuid), Codecs.GAME_PROFILE_PROPERTY_MAP.optionalFieldOf("properties", new PropertyMap()).forGetter(ProfileComponent::properties)).apply(instance, ProfileComponent::new);
   });
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;

   public ProfileComponent(Optional name, Optional uuid, PropertyMap properties) {
      this(name, uuid, properties, createProfile(uuid, name, properties));
   }

   public ProfileComponent(GameProfile gameProfile) {
      this(Optional.of(gameProfile.getName()), Optional.of(gameProfile.getId()), gameProfile.getProperties(), gameProfile);
   }

   public ProfileComponent(Optional optional, Optional optional2, PropertyMap propertyMap, GameProfile gameProfile) {
      this.name = optional;
      this.uuid = optional2;
      this.properties = propertyMap;
      this.gameProfile = gameProfile;
   }

   @Nullable
   public ProfileComponent resolve() {
      if (this.isCompleted()) {
         return this;
      } else {
         Optional optional;
         if (this.uuid.isPresent()) {
            optional = (Optional)SkullBlockEntity.fetchProfileByUuid((UUID)this.uuid.get()).getNow((Object)null);
         } else {
            optional = (Optional)SkullBlockEntity.fetchProfileByName((String)this.name.orElseThrow()).getNow((Object)null);
         }

         return optional != null ? this.resolve(optional) : null;
      }
   }

   public CompletableFuture getFuture() {
      if (this.isCompleted()) {
         return CompletableFuture.completedFuture(this);
      } else {
         return this.uuid.isPresent() ? SkullBlockEntity.fetchProfileByUuid((UUID)this.uuid.get()).thenApply(this::resolve) : SkullBlockEntity.fetchProfileByName((String)this.name.orElseThrow()).thenApply(this::resolve);
      }
   }

   private ProfileComponent resolve(Optional profile) {
      return new ProfileComponent((GameProfile)profile.orElseGet(() -> {
         return createProfile(this.uuid, this.name);
      }));
   }

   private static GameProfile createProfile(Optional uuid, Optional name) {
      return new GameProfile((UUID)uuid.orElse(Util.NIL_UUID), (String)name.orElse(""));
   }

   private static GameProfile createProfile(Optional uuid, Optional name, PropertyMap properties) {
      GameProfile gameProfile = createProfile(uuid, name);
      gameProfile.getProperties().putAll(properties);
      return gameProfile;
   }

   public boolean isCompleted() {
      if (!this.properties.isEmpty()) {
         return true;
      } else {
         return this.uuid.isPresent() == this.name.isPresent();
      }
   }

   public Optional name() {
      return this.name;
   }

   public Optional uuid() {
      return this.uuid;
   }

   public PropertyMap properties() {
      return this.properties;
   }

   public GameProfile gameProfile() {
      return this.gameProfile;
   }

   static {
      CODEC = Codec.withAlternative(BASE_CODEC, Codecs.PLAYER_NAME, (name) -> {
         return new ProfileComponent(Optional.of(name), Optional.empty(), new PropertyMap());
      });
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.string(16).collect(PacketCodecs::optional), ProfileComponent::name, Uuids.PACKET_CODEC.collect(PacketCodecs::optional), ProfileComponent::uuid, PacketCodecs.PROPERTY_MAP, ProfileComponent::properties, ProfileComponent::new);
   }
}
