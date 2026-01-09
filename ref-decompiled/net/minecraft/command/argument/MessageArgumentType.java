package net.minecraft.command.argument;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.network.message.MessageDecorator;
import net.minecraft.network.message.SignedCommandArguments;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class MessageArgumentType implements SignedArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");
   static final Dynamic2CommandExceptionType MESSAGE_TOO_LONG_EXCEPTION = new Dynamic2CommandExceptionType((length, maxLength) -> {
      return Text.stringifiedTranslatable("argument.message.too_long", length, maxLength);
   });

   public static MessageArgumentType message() {
      return new MessageArgumentType();
   }

   public static Text getMessage(CommandContext context, String name) throws CommandSyntaxException {
      MessageFormat messageFormat = (MessageFormat)context.getArgument(name, MessageFormat.class);
      return messageFormat.format((ServerCommandSource)context.getSource());
   }

   public static void getSignedMessage(CommandContext context, String name, Consumer callback) throws CommandSyntaxException {
      MessageFormat messageFormat = (MessageFormat)context.getArgument(name, MessageFormat.class);
      ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
      Text text = messageFormat.format(serverCommandSource);
      SignedCommandArguments signedCommandArguments = serverCommandSource.getSignedArguments();
      SignedMessage signedMessage = signedCommandArguments.getMessage(name);
      if (signedMessage != null) {
         chain(callback, serverCommandSource, signedMessage.withUnsignedContent(text));
      } else {
         chainUnsigned(callback, serverCommandSource, SignedMessage.ofUnsigned(messageFormat.contents).withUnsignedContent(text));
      }

   }

   private static void chain(Consumer callback, ServerCommandSource source, SignedMessage message) {
      MinecraftServer minecraftServer = source.getServer();
      CompletableFuture completableFuture = filterText(source, message);
      Text text = minecraftServer.getMessageDecorator().decorate(source.getPlayer(), message.getContent());
      source.getMessageChainTaskQueue().append(completableFuture, (filtered) -> {
         SignedMessage signedMessage2 = message.withUnsignedContent(text).withFilterMask(filtered.mask());
         callback.accept(signedMessage2);
      });
   }

   private static void chainUnsigned(Consumer callback, ServerCommandSource source, SignedMessage message) {
      MessageDecorator messageDecorator = source.getServer().getMessageDecorator();
      Text text = messageDecorator.decorate(source.getPlayer(), message.getContent());
      callback.accept(message.withUnsignedContent(text));
   }

   private static CompletableFuture filterText(ServerCommandSource source, SignedMessage message) {
      ServerPlayerEntity serverPlayerEntity = source.getPlayer();
      return serverPlayerEntity != null && message.canVerifyFrom(serverPlayerEntity.getUuid()) ? serverPlayerEntity.getTextStream().filterText(message.getSignedContent()) : CompletableFuture.completedFuture(FilteredMessage.permitted(message.getSignedContent()));
   }

   public MessageFormat parse(StringReader stringReader) throws CommandSyntaxException {
      return MessageArgumentType.MessageFormat.parse(stringReader, true);
   }

   public MessageFormat parse(StringReader stringReader, @Nullable Object object) throws CommandSyntaxException {
      return MessageArgumentType.MessageFormat.parse(stringReader, EntitySelectorReader.shouldAllowAtSelectors(object));
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader, @Nullable final Object source) throws CommandSyntaxException {
      return this.parse(reader, source);
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }

   public static record MessageFormat(String contents, MessageSelector[] selectors) {
      final String contents;

      public MessageFormat(String contents, MessageSelector[] selectors) {
         this.contents = contents;
         this.selectors = selectors;
      }

      Text format(ServerCommandSource source) throws CommandSyntaxException {
         return this.format(source, EntitySelectorReader.shouldAllowAtSelectors(source));
      }

      public Text format(ServerCommandSource source, boolean canUseSelectors) throws CommandSyntaxException {
         if (this.selectors.length != 0 && canUseSelectors) {
            MutableText mutableText = Text.literal(this.contents.substring(0, this.selectors[0].start()));
            int i = this.selectors[0].start();
            MessageSelector[] var5 = this.selectors;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               MessageSelector messageSelector = var5[var7];
               Text text = messageSelector.format(source);
               if (i < messageSelector.start()) {
                  mutableText.append(this.contents.substring(i, messageSelector.start()));
               }

               mutableText.append(text);
               i = messageSelector.end();
            }

            if (i < this.contents.length()) {
               mutableText.append(this.contents.substring(i));
            }

            return mutableText;
         } else {
            return Text.literal(this.contents);
         }
      }

      public static MessageFormat parse(StringReader reader, boolean allowAtSelectors) throws CommandSyntaxException {
         if (reader.getRemainingLength() > 256) {
            throw MessageArgumentType.MESSAGE_TOO_LONG_EXCEPTION.create(reader.getRemainingLength(), 256);
         } else {
            String string = reader.getRemaining();
            if (!allowAtSelectors) {
               reader.setCursor(reader.getTotalLength());
               return new MessageFormat(string, new MessageSelector[0]);
            } else {
               List list = Lists.newArrayList();
               int i = reader.getCursor();

               while(true) {
                  int j;
                  EntitySelector entitySelector;
                  label42:
                  while(true) {
                     while(reader.canRead()) {
                        if (reader.peek() == '@') {
                           j = reader.getCursor();

                           try {
                              EntitySelectorReader entitySelectorReader = new EntitySelectorReader(reader, true);
                              entitySelector = entitySelectorReader.read();
                              break label42;
                           } catch (CommandSyntaxException var8) {
                              if (var8.getType() != EntitySelectorReader.MISSING_EXCEPTION && var8.getType() != EntitySelectorReader.UNKNOWN_SELECTOR_EXCEPTION) {
                                 throw var8;
                              }

                              reader.setCursor(j + 1);
                           }
                        } else {
                           reader.skip();
                        }
                     }

                     return new MessageFormat(string, (MessageSelector[])list.toArray(new MessageSelector[0]));
                  }

                  list.add(new MessageSelector(j - i, reader.getCursor() - i, entitySelector));
               }
            }
         }
      }

      public String contents() {
         return this.contents;
      }

      public MessageSelector[] selectors() {
         return this.selectors;
      }
   }

   public static record MessageSelector(int start, int end, EntitySelector selector) {
      public MessageSelector(int start, int end, EntitySelector selector) {
         this.start = start;
         this.end = end;
         this.selector = selector;
      }

      public Text format(ServerCommandSource source) throws CommandSyntaxException {
         return EntitySelector.getNames(this.selector.getEntities(source));
      }

      public int start() {
         return this.start;
      }

      public int end() {
         return this.end;
      }

      public EntitySelector selector() {
         return this.selector;
      }
   }
}
