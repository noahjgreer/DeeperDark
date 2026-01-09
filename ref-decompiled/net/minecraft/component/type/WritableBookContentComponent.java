package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.RawFilteredPair;

public record WritableBookContentComponent(List pages) implements BookContent {
   public static final WritableBookContentComponent DEFAULT = new WritableBookContentComponent(List.of());
   public static final int MAX_PAGE_LENGTH = 1024;
   public static final int MAX_PAGE_COUNT = 100;
   private static final Codec PAGE_CODEC = RawFilteredPair.createCodec(Codec.string(0, 1024));
   public static final Codec PAGES_CODEC;
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;

   public WritableBookContentComponent(List pages) {
      if (pages.size() > 100) {
         throw new IllegalArgumentException("Got " + pages.size() + " pages, but maximum is 100");
      } else {
         this.pages = pages;
      }
   }

   public Stream stream(boolean shouldFilter) {
      return this.pages.stream().map((page) -> {
         return (String)page.get(shouldFilter);
      });
   }

   public WritableBookContentComponent withPages(List list) {
      return new WritableBookContentComponent(list);
   }

   public List pages() {
      return this.pages;
   }

   // $FF: synthetic method
   public Object withPages(final List pages) {
      return this.withPages(pages);
   }

   static {
      PAGES_CODEC = PAGE_CODEC.sizeLimitedListOf(100);
      CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(PAGES_CODEC.optionalFieldOf("pages", List.of()).forGetter(WritableBookContentComponent::pages)).apply(instance, WritableBookContentComponent::new);
      });
      PACKET_CODEC = RawFilteredPair.createPacketCodec(PacketCodecs.string(1024)).collect(PacketCodecs.toList(100)).xmap(WritableBookContentComponent::new, WritableBookContentComponent::pages);
   }
}
