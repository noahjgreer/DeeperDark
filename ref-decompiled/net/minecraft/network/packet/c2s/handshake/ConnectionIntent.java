package net.minecraft.network.packet.c2s.handshake;

public enum ConnectionIntent {
   STATUS,
   LOGIN,
   TRANSFER;

   private static final int STATUS_ID = 1;
   private static final int LOGIN_ID = 2;
   private static final int TRANSFER_ID = 3;

   public static ConnectionIntent byId(int id) {
      ConnectionIntent var10000;
      switch (id) {
         case 1:
            var10000 = STATUS;
            break;
         case 2:
            var10000 = LOGIN;
            break;
         case 3:
            var10000 = TRANSFER;
            break;
         default:
            throw new IllegalArgumentException("Unknown connection intent: " + id);
      }

      return var10000;
   }

   public int getId() {
      byte var10000;
      switch (this.ordinal()) {
         case 0:
            var10000 = 1;
            break;
         case 1:
            var10000 = 2;
            break;
         case 2:
            var10000 = 3;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   // $FF: synthetic method
   private static ConnectionIntent[] method_52286() {
      return new ConnectionIntent[]{STATUS, LOGIN, TRANSFER};
   }
}
