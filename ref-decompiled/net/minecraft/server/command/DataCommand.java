package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.command.BlockDataObject;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.EntityDataObject;
import net.minecraft.command.StorageDataObject;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.command.argument.NbtElementArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtPrimitive;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class DataCommand {
   private static final SimpleCommandExceptionType MERGE_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.data.merge.failed"));
   private static final DynamicCommandExceptionType GET_INVALID_EXCEPTION = new DynamicCommandExceptionType((path) -> {
      return Text.stringifiedTranslatable("commands.data.get.invalid", path);
   });
   private static final DynamicCommandExceptionType GET_UNKNOWN_EXCEPTION = new DynamicCommandExceptionType((path) -> {
      return Text.stringifiedTranslatable("commands.data.get.unknown", path);
   });
   private static final SimpleCommandExceptionType GET_MULTIPLE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.data.get.multiple"));
   private static final DynamicCommandExceptionType MODIFY_EXPECTED_OBJECT_EXCEPTION = new DynamicCommandExceptionType((nbt) -> {
      return Text.stringifiedTranslatable("commands.data.modify.expected_object", nbt);
   });
   private static final DynamicCommandExceptionType MODIFY_EXPECTED_VALUE_EXCEPTION = new DynamicCommandExceptionType((nbt) -> {
      return Text.stringifiedTranslatable("commands.data.modify.expected_value", nbt);
   });
   private static final Dynamic2CommandExceptionType MODIFY_INVALID_SUBSTRING_EXCEPTION = new Dynamic2CommandExceptionType((startIndex, endIndex) -> {
      return Text.stringifiedTranslatable("commands.data.modify.invalid_substring", startIndex, endIndex);
   });
   public static final List OBJECT_TYPE_FACTORIES;
   public static final List TARGET_OBJECT_TYPES;
   public static final List SOURCE_OBJECT_TYPES;

   public static void register(CommandDispatcher dispatcher) {
      LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("data").requires(CommandManager.requirePermissionLevel(2));
      Iterator var2 = TARGET_OBJECT_TYPES.iterator();

      while(var2.hasNext()) {
         ObjectType objectType = (ObjectType)var2.next();
         ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder.then(objectType.addArgumentsToBuilder(CommandManager.literal("merge"), (builder) -> {
            return builder.then(CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound()).executes((context) -> {
               return executeMerge((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtCompoundArgumentType.getNbtCompound(context, "nbt"));
            }));
         }))).then(objectType.addArgumentsToBuilder(CommandManager.literal("get"), (builder) -> {
            return builder.executes((context) -> {
               return executeGet((ServerCommandSource)context.getSource(), objectType.getObject(context));
            }).then(((RequiredArgumentBuilder)CommandManager.argument("path", NbtPathArgumentType.nbtPath()).executes((context) -> {
               return executeGet((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"));
            })).then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes((context) -> {
               return executeGet((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), DoubleArgumentType.getDouble(context, "scale"));
            })));
         }))).then(objectType.addArgumentsToBuilder(CommandManager.literal("remove"), (builder) -> {
            return builder.then(CommandManager.argument("path", NbtPathArgumentType.nbtPath()).executes((context) -> {
               return executeRemove((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"));
            }));
         }))).then(addModifyArgument((builder, modifier) -> {
            builder.then(CommandManager.literal("insert").then(CommandManager.argument("index", IntegerArgumentType.integer()).then(modifier.create((context, sourceNbt, path, elements) -> {
               return path.insert(IntegerArgumentType.getInteger(context, "index"), sourceNbt, elements);
            })))).then(CommandManager.literal("prepend").then(modifier.create((context, sourceNbt, path, elements) -> {
               return path.insert(0, sourceNbt, elements);
            }))).then(CommandManager.literal("append").then(modifier.create((context, sourceNbt, path, elements) -> {
               return path.insert(-1, sourceNbt, elements);
            }))).then(CommandManager.literal("set").then(modifier.create((context, sourceNbt, path, elements) -> {
               return path.put(sourceNbt, (NbtElement)Iterables.getLast(elements));
            }))).then(CommandManager.literal("merge").then(modifier.create((context, element, path, elements) -> {
               NbtCompound nbtCompound = new NbtCompound();
               Iterator var5 = elements.iterator();

               while(var5.hasNext()) {
                  NbtElement nbtElement = (NbtElement)var5.next();
                  if (NbtPathArgumentType.NbtPath.isTooDeep(nbtElement, 0)) {
                     throw NbtPathArgumentType.TOO_DEEP_EXCEPTION.create();
                  }

                  if (!(nbtElement instanceof NbtCompound)) {
                     throw MODIFY_EXPECTED_OBJECT_EXCEPTION.create(nbtElement);
                  }

                  NbtCompound nbtCompound2 = (NbtCompound)nbtElement;
                  nbtCompound.copyFrom(nbtCompound2);
               }

               Collection collection = path.getOrInit(element, NbtCompound::new);
               int i = 0;

               NbtCompound nbtCompound3;
               NbtCompound nbtCompound4;
               for(Iterator var13 = collection.iterator(); var13.hasNext(); i += nbtCompound4.equals(nbtCompound3) ? 0 : 1) {
                  NbtElement nbtElement2 = (NbtElement)var13.next();
                  if (!(nbtElement2 instanceof NbtCompound)) {
                     throw MODIFY_EXPECTED_OBJECT_EXCEPTION.create(nbtElement2);
                  }

                  nbtCompound3 = (NbtCompound)nbtElement2;
                  nbtCompound4 = nbtCompound3.copy();
                  nbtCompound3.copyFrom(nbtCompound);
               }

               return i;
            })));
         }));
      }

      dispatcher.register(literalArgumentBuilder);
   }

   private static String asString(NbtElement nbt) throws CommandSyntaxException {
      Objects.requireNonNull(nbt);
      byte var2 = 0;
      String var10000;
      switch (nbt.typeSwitch<invokedynamic>(nbt, var2)) {
         case 0:
            NbtString var3 = (NbtString)nbt;
            NbtString var8 = var3;

            try {
               var10000 = var8.value();
            } catch (Throwable var6) {
               throw new MatchException(var6.toString(), var6);
            }

            String var7 = var10000;
            var10000 = var7;
            break;
         case 1:
            NbtPrimitive nbtPrimitive = (NbtPrimitive)nbt;
            var10000 = nbtPrimitive.toString();
            break;
         default:
            throw MODIFY_EXPECTED_VALUE_EXCEPTION.create(nbt);
      }

      return var10000;
   }

   private static List mapValues(List list, Processor processor) throws CommandSyntaxException {
      List list2 = new ArrayList(list.size());
      Iterator var3 = list.iterator();

      while(var3.hasNext()) {
         NbtElement nbtElement = (NbtElement)var3.next();
         String string = asString(nbtElement);
         list2.add(NbtString.of(processor.process(string)));
      }

      return list2;
   }

   private static ArgumentBuilder addModifyArgument(BiConsumer subArgumentAdder) {
      LiteralArgumentBuilder literalArgumentBuilder = CommandManager.literal("modify");
      Iterator var2 = TARGET_OBJECT_TYPES.iterator();

      while(var2.hasNext()) {
         ObjectType objectType = (ObjectType)var2.next();
         objectType.addArgumentsToBuilder(literalArgumentBuilder, (builder) -> {
            ArgumentBuilder argumentBuilder = CommandManager.argument("targetPath", NbtPathArgumentType.nbtPath());
            Iterator var4 = SOURCE_OBJECT_TYPES.iterator();

            while(var4.hasNext()) {
               ObjectType objectType2 = (ObjectType)var4.next();
               subArgumentAdder.accept(argumentBuilder, (operation) -> {
                  return objectType2.addArgumentsToBuilder(CommandManager.literal("from"), (builderx) -> {
                     return builderx.executes((context) -> {
                        return executeModify(context, objectType, operation, getValues(context, objectType2));
                     }).then(CommandManager.argument("sourcePath", NbtPathArgumentType.nbtPath()).executes((context) -> {
                        return executeModify(context, objectType, operation, getValuesByPath(context, objectType2));
                     }));
                  });
               });
               subArgumentAdder.accept(argumentBuilder, (operation) -> {
                  return objectType2.addArgumentsToBuilder(CommandManager.literal("string"), (builderx) -> {
                     return builderx.executes((context) -> {
                        return executeModify(context, objectType, operation, mapValues(getValues(context, objectType2), (value) -> {
                           return value;
                        }));
                     }).then(((RequiredArgumentBuilder)CommandManager.argument("sourcePath", NbtPathArgumentType.nbtPath()).executes((context) -> {
                        return executeModify(context, objectType, operation, mapValues(getValuesByPath(context, objectType2), (value) -> {
                           return value;
                        }));
                     })).then(((RequiredArgumentBuilder)CommandManager.argument("start", IntegerArgumentType.integer()).executes((context) -> {
                        return executeModify(context, objectType, operation, mapValues(getValuesByPath(context, objectType2), (value) -> {
                           return substring(value, IntegerArgumentType.getInteger(context, "start"));
                        }));
                     })).then(CommandManager.argument("end", IntegerArgumentType.integer()).executes((context) -> {
                        return executeModify(context, objectType, operation, mapValues(getValuesByPath(context, objectType2), (value) -> {
                           return substring(value, IntegerArgumentType.getInteger(context, "start"), IntegerArgumentType.getInteger(context, "end"));
                        }));
                     }))));
                  });
               });
            }

            subArgumentAdder.accept(argumentBuilder, (modifier) -> {
               return CommandManager.literal("value").then(CommandManager.argument("value", NbtElementArgumentType.nbtElement()).executes((context) -> {
                  List list = Collections.singletonList(NbtElementArgumentType.getNbtElement(context, "value"));
                  return executeModify(context, objectType, modifier, list);
               }));
            });
            return builder.then(argumentBuilder);
         });
      }

      return literalArgumentBuilder;
   }

   private static String substringInternal(String string, int startIndex, int endIndex) throws CommandSyntaxException {
      if (startIndex >= 0 && endIndex <= string.length() && startIndex <= endIndex) {
         return string.substring(startIndex, endIndex);
      } else {
         throw MODIFY_INVALID_SUBSTRING_EXCEPTION.create(startIndex, endIndex);
      }
   }

   private static String substring(String string, int startIndex, int endIndex) throws CommandSyntaxException {
      int i = string.length();
      int j = getSubstringIndex(startIndex, i);
      int k = getSubstringIndex(endIndex, i);
      return substringInternal(string, j, k);
   }

   private static String substring(String string, int startIndex) throws CommandSyntaxException {
      int i = string.length();
      return substringInternal(string, getSubstringIndex(startIndex, i), i);
   }

   private static int getSubstringIndex(int index, int length) {
      return index >= 0 ? index : length + index;
   }

   private static List getValues(CommandContext context, ObjectType objectType) throws CommandSyntaxException {
      DataCommandObject dataCommandObject = objectType.getObject(context);
      return Collections.singletonList(dataCommandObject.getNbt());
   }

   private static List getValuesByPath(CommandContext context, ObjectType objectType) throws CommandSyntaxException {
      DataCommandObject dataCommandObject = objectType.getObject(context);
      NbtPathArgumentType.NbtPath nbtPath = NbtPathArgumentType.getNbtPath(context, "sourcePath");
      return nbtPath.get(dataCommandObject.getNbt());
   }

   private static int executeModify(CommandContext context, ObjectType objectType, ModifyOperation modifier, List elements) throws CommandSyntaxException {
      DataCommandObject dataCommandObject = objectType.getObject(context);
      NbtPathArgumentType.NbtPath nbtPath = NbtPathArgumentType.getNbtPath(context, "targetPath");
      NbtCompound nbtCompound = dataCommandObject.getNbt();
      int i = modifier.modify(context, nbtCompound, nbtPath, elements);
      if (i == 0) {
         throw MERGE_FAILED_EXCEPTION.create();
      } else {
         dataCommandObject.setNbt(nbtCompound);
         ((ServerCommandSource)context.getSource()).sendFeedback(() -> {
            return dataCommandObject.feedbackModify();
         }, true);
         return i;
      }
   }

   private static int executeRemove(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path) throws CommandSyntaxException {
      NbtCompound nbtCompound = object.getNbt();
      int i = path.remove(nbtCompound);
      if (i == 0) {
         throw MERGE_FAILED_EXCEPTION.create();
      } else {
         object.setNbt(nbtCompound);
         source.sendFeedback(() -> {
            return object.feedbackModify();
         }, true);
         return i;
      }
   }

   public static NbtElement getNbt(NbtPathArgumentType.NbtPath path, DataCommandObject object) throws CommandSyntaxException {
      Collection collection = path.get(object.getNbt());
      Iterator iterator = collection.iterator();
      NbtElement nbtElement = (NbtElement)iterator.next();
      if (iterator.hasNext()) {
         throw GET_MULTIPLE_EXCEPTION.create();
      } else {
         return nbtElement;
      }
   }

   private static int executeGet(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path) throws CommandSyntaxException {
      NbtElement nbtElement = getNbt(path, object);
      Objects.requireNonNull(nbtElement);
      byte var6 = 0;
      int var16;
      switch (nbtElement.typeSwitch<invokedynamic>(nbtElement, var6)) {
         case 0:
            AbstractNbtNumber abstractNbtNumber = (AbstractNbtNumber)nbtElement;
            var16 = MathHelper.floor(abstractNbtNumber.doubleValue());
            break;
         case 1:
            AbstractNbtList abstractNbtList = (AbstractNbtList)nbtElement;
            var16 = abstractNbtList.size();
            break;
         case 2:
            NbtCompound nbtCompound = (NbtCompound)nbtElement;
            var16 = nbtCompound.getSize();
            break;
         case 3:
            NbtString var10 = (NbtString)nbtElement;
            NbtString var10000 = var10;

            String var15;
            try {
               var15 = var10000.value();
            } catch (Throwable var13) {
               throw new MatchException(var13.toString(), var13);
            }

            String var14 = var15;
            var16 = var14.length();
            break;
         case 4:
            NbtEnd nbtEnd = (NbtEnd)nbtElement;
            throw GET_UNKNOWN_EXCEPTION.create(path.toString());
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      int i = var16;
      source.sendFeedback(() -> {
         return object.feedbackQuery(nbtElement);
      }, false);
      return i;
   }

   private static int executeGet(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path, double scale) throws CommandSyntaxException {
      NbtElement nbtElement = getNbt(path, object);
      if (!(nbtElement instanceof AbstractNbtNumber)) {
         throw GET_INVALID_EXCEPTION.create(path.toString());
      } else {
         int i = MathHelper.floor(((AbstractNbtNumber)nbtElement).doubleValue() * scale);
         source.sendFeedback(() -> {
            return object.feedbackGet(path, scale, i);
         }, false);
         return i;
      }
   }

   private static int executeGet(ServerCommandSource source, DataCommandObject object) throws CommandSyntaxException {
      NbtCompound nbtCompound = object.getNbt();
      source.sendFeedback(() -> {
         return object.feedbackQuery(nbtCompound);
      }, false);
      return 1;
   }

   private static int executeMerge(ServerCommandSource source, DataCommandObject object, NbtCompound nbt) throws CommandSyntaxException {
      NbtCompound nbtCompound = object.getNbt();
      if (NbtPathArgumentType.NbtPath.isTooDeep(nbt, 0)) {
         throw NbtPathArgumentType.TOO_DEEP_EXCEPTION.create();
      } else {
         NbtCompound nbtCompound2 = nbtCompound.copy().copyFrom(nbt);
         if (nbtCompound.equals(nbtCompound2)) {
            throw MERGE_FAILED_EXCEPTION.create();
         } else {
            object.setNbt(nbtCompound2);
            source.sendFeedback(() -> {
               return object.feedbackModify();
            }, true);
            return 1;
         }
      }
   }

   static {
      OBJECT_TYPE_FACTORIES = ImmutableList.of(EntityDataObject.TYPE_FACTORY, BlockDataObject.TYPE_FACTORY, StorageDataObject.TYPE_FACTORY);
      TARGET_OBJECT_TYPES = (List)OBJECT_TYPE_FACTORIES.stream().map((factory) -> {
         return (ObjectType)factory.apply("target");
      }).collect(ImmutableList.toImmutableList());
      SOURCE_OBJECT_TYPES = (List)OBJECT_TYPE_FACTORIES.stream().map((factory) -> {
         return (ObjectType)factory.apply("source");
      }).collect(ImmutableList.toImmutableList());
   }

   public interface ObjectType {
      DataCommandObject getObject(CommandContext context) throws CommandSyntaxException;

      ArgumentBuilder addArgumentsToBuilder(ArgumentBuilder argument, Function argumentAdder);
   }

   @FunctionalInterface
   private interface Processor {
      String process(String string) throws CommandSyntaxException;
   }

   @FunctionalInterface
   private interface ModifyOperation {
      int modify(CommandContext context, NbtCompound sourceNbt, NbtPathArgumentType.NbtPath path, List elements) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface ModifyArgumentCreator {
      ArgumentBuilder create(ModifyOperation modifier);
   }
}
