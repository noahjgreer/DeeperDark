package net.minecraft.network.message;

public record AcknowledgedMessage(MessageSignatureData signature, boolean pending) {
   public AcknowledgedMessage(MessageSignatureData messageSignatureData, boolean bl) {
      this.signature = messageSignatureData;
      this.pending = bl;
   }

   public AcknowledgedMessage unmarkAsPending() {
      return this.pending ? new AcknowledgedMessage(this.signature, false) : this;
   }

   public MessageSignatureData signature() {
      return this.signature;
   }

   public boolean pending() {
      return this.pending;
   }
}
