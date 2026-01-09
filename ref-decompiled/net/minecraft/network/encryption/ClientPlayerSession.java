package net.minecraft.network.encryption;

import java.util.UUID;
import net.minecraft.network.message.MessageChain;

public record ClientPlayerSession(UUID sessionId, PlayerKeyPair keyPair) {
   public ClientPlayerSession(UUID uUID, PlayerKeyPair playerKeyPair) {
      this.sessionId = uUID;
      this.keyPair = playerKeyPair;
   }

   public static ClientPlayerSession create(PlayerKeyPair keyPair) {
      return new ClientPlayerSession(UUID.randomUUID(), keyPair);
   }

   public MessageChain.Packer createPacker(UUID sender) {
      return (new MessageChain(sender, this.sessionId)).getPacker(Signer.create(this.keyPair.privateKey(), "SHA256withRSA"));
   }

   public PublicPlayerSession toPublicSession() {
      return new PublicPlayerSession(this.sessionId, this.keyPair.publicKey());
   }

   public UUID sessionId() {
      return this.sessionId;
   }

   public PlayerKeyPair keyPair() {
      return this.keyPair;
   }
}
