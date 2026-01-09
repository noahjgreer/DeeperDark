package net.minecraft.server;

import com.mojang.datafixers.util.Either;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.function.ValueLists;

public record ServerLinks(List entries) {
   public static final ServerLinks EMPTY = new ServerLinks(List.of());
   public static final PacketCodec TYPE_CODEC;
   public static final PacketCodec LIST_CODEC;

   public ServerLinks(List list) {
      this.entries = list;
   }

   public boolean isEmpty() {
      return this.entries.isEmpty();
   }

   public Optional getEntryFor(Known known) {
      return this.entries.stream().filter((entry) -> {
         return (Boolean)entry.type.map((type) -> {
            return type == known;
         }, (text) -> {
            return false;
         });
      }).findFirst();
   }

   public List getLinks() {
      return this.entries.stream().map((entry) -> {
         return new StringifiedEntry(entry.type, entry.link.toString());
      }).toList();
   }

   public List entries() {
      return this.entries;
   }

   static {
      TYPE_CODEC = PacketCodecs.either(ServerLinks.Known.CODEC, TextCodecs.PACKET_CODEC);
      LIST_CODEC = ServerLinks.StringifiedEntry.CODEC.collect(PacketCodecs.toList());
   }

   public static enum Known {
      BUG_REPORT(0, "report_bug"),
      COMMUNITY_GUIDELINES(1, "community_guidelines"),
      SUPPORT(2, "support"),
      STATUS(3, "status"),
      FEEDBACK(4, "feedback"),
      COMMUNITY(5, "community"),
      WEBSITE(6, "website"),
      FORUMS(7, "forums"),
      NEWS(8, "news"),
      ANNOUNCEMENTS(9, "announcements");

      private static final IntFunction FROM_ID = ValueLists.createIndexToValueFunction((known) -> {
         return known.id;
      }, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
      public static final PacketCodec CODEC = PacketCodecs.indexed(FROM_ID, (known) -> {
         return known.id;
      });
      private final int id;
      private final String name;

      private Known(final int id, final String name) {
         this.id = id;
         this.name = name;
      }

      private Text getText() {
         return Text.translatable("known_server_link." + this.name);
      }

      public Entry createEntry(URI link) {
         return ServerLinks.Entry.create(this, link);
      }

      // $FF: synthetic method
      private static Known[] method_60669() {
         return new Known[]{BUG_REPORT, COMMUNITY_GUIDELINES, SUPPORT, STATUS, FEEDBACK, COMMUNITY, WEBSITE, FORUMS, NEWS, ANNOUNCEMENTS};
      }
   }

   public static record StringifiedEntry(Either type, String link) {
      public static final PacketCodec CODEC;

      public StringifiedEntry(Either either, String string) {
         this.type = either;
         this.link = string;
      }

      public Either type() {
         return this.type;
      }

      public String link() {
         return this.link;
      }

      static {
         CODEC = PacketCodec.tuple(ServerLinks.TYPE_CODEC, StringifiedEntry::type, PacketCodecs.STRING, StringifiedEntry::link, StringifiedEntry::new);
      }
   }

   public static record Entry(Either type, URI link) {
      final Either type;
      final URI link;

      public Entry(Either either, URI uRI) {
         this.type = either;
         this.link = uRI;
      }

      public static Entry create(Known known, URI link) {
         return new Entry(Either.left(known), link);
      }

      public static Entry create(Text name, URI link) {
         return new Entry(Either.right(name), link);
      }

      public Text getText() {
         return (Text)this.type.map(Known::getText, (text) -> {
            return text;
         });
      }

      public Either type() {
         return this.type;
      }

      public URI link() {
         return this.link;
      }
   }
}
