package net.minecraft.network.encryption;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.security.PrivateKey;
import java.time.Instant;
import net.minecraft.util.dynamic.Codecs;

public record PlayerKeyPair(PrivateKey privateKey, PlayerPublicKey publicKey, Instant refreshedAfter) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(NetworkEncryptionUtils.RSA_PRIVATE_KEY_CODEC.fieldOf("private_key").forGetter(PlayerKeyPair::privateKey), PlayerPublicKey.CODEC.fieldOf("public_key").forGetter(PlayerKeyPair::publicKey), Codecs.INSTANT.fieldOf("refreshed_after").forGetter(PlayerKeyPair::refreshedAfter)).apply(instance, PlayerKeyPair::new);
   });

   public PlayerKeyPair(PrivateKey privateKey, PlayerPublicKey playerPublicKey, Instant instant) {
      this.privateKey = privateKey;
      this.publicKey = playerPublicKey;
      this.refreshedAfter = instant;
   }

   public boolean needsRefreshing() {
      return this.refreshedAfter.isBefore(Instant.now());
   }

   public PrivateKey privateKey() {
      return this.privateKey;
   }

   public PlayerPublicKey publicKey() {
      return this.publicKey;
   }

   public Instant refreshedAfter() {
      return this.refreshedAfter;
   }
}
