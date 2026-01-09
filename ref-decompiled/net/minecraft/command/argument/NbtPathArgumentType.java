package net.minecraft.command.argument;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NbtPathArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}");
   public static final SimpleCommandExceptionType INVALID_PATH_NODE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("arguments.nbtpath.node.invalid"));
   public static final SimpleCommandExceptionType TOO_DEEP_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("arguments.nbtpath.too_deep"));
   public static final DynamicCommandExceptionType NOTHING_FOUND_EXCEPTION = new DynamicCommandExceptionType((path) -> {
      return Text.stringifiedTranslatable("arguments.nbtpath.nothing_found", path);
   });
   static final DynamicCommandExceptionType EXPECTED_LIST_EXCEPTION = new DynamicCommandExceptionType((nbt) -> {
      return Text.stringifiedTranslatable("commands.data.modify.expected_list", nbt);
   });
   static final DynamicCommandExceptionType INVALID_INDEX_EXCEPTION = new DynamicCommandExceptionType((index) -> {
      return Text.stringifiedTranslatable("commands.data.modify.invalid_index", index);
   });
   private static final char LEFT_SQUARE_BRACKET = '[';
   private static final char RIGHT_SQUARE_BRACKET = ']';
   private static final char LEFT_CURLY_BRACKET = '{';
   private static final char RIGHT_CURLY_BRACKET = '}';
   private static final char DOUBLE_QUOTE = '"';
   private static final char SINGLE_QUOTE = '\'';

   public static NbtPathArgumentType nbtPath() {
      return new NbtPathArgumentType();
   }

   public static NbtPath getNbtPath(CommandContext context, String name) {
      return (NbtPath)context.getArgument(name, NbtPath.class);
   }

   public NbtPath parse(StringReader stringReader) throws CommandSyntaxException {
      List list = Lists.newArrayList();
      int i = stringReader.getCursor();
      Object2IntMap object2IntMap = new Object2IntOpenHashMap();
      boolean bl = true;

      while(stringReader.canRead() && stringReader.peek() != ' ') {
         PathNode pathNode = parseNode(stringReader, bl);
         list.add(pathNode);
         object2IntMap.put(pathNode, stringReader.getCursor() - i);
         bl = false;
         if (stringReader.canRead()) {
            char c = stringReader.peek();
            if (c != ' ' && c != '[' && c != '{') {
               stringReader.expect('.');
            }
         }
      }

      return new NbtPath(stringReader.getString().substring(i, stringReader.getCursor()), (PathNode[])list.toArray(new PathNode[0]), object2IntMap);
   }

   private static PathNode parseNode(StringReader reader, boolean root) throws CommandSyntaxException {
      Object var10000;
      switch (reader.peek()) {
         case '"':
         case '\'':
            var10000 = readCompoundChildNode(reader, reader.readString());
            break;
         case '[':
            reader.skip();
            int i = reader.peek();
            if (i == '{') {
               NbtCompound nbtCompound2 = StringNbtReader.readCompoundAsArgument(reader);
               reader.expect(']');
               var10000 = new FilteredListElementNode(nbtCompound2);
            } else if (i == ']') {
               reader.skip();
               var10000 = NbtPathArgumentType.AllListElementNode.INSTANCE;
            } else {
               int j = reader.readInt();
               reader.expect(']');
               var10000 = new IndexedListElementNode(j);
            }
            break;
         case '{':
            if (!root) {
               throw INVALID_PATH_NODE_EXCEPTION.createWithContext(reader);
            }

            NbtCompound nbtCompound = StringNbtReader.readCompoundAsArgument(reader);
            var10000 = new FilteredRootNode(nbtCompound);
            break;
         default:
            var10000 = readCompoundChildNode(reader, readName(reader));
      }

      return (PathNode)var10000;
   }

   private static PathNode readCompoundChildNode(StringReader reader, String name) throws CommandSyntaxException {
      if (name.isEmpty()) {
         throw INVALID_PATH_NODE_EXCEPTION.createWithContext(reader);
      } else if (reader.canRead() && reader.peek() == '{') {
         NbtCompound nbtCompound = StringNbtReader.readCompoundAsArgument(reader);
         return new FilteredNamedNode(name, nbtCompound);
      } else {
         return new NamedNode(name);
      }
   }

   private static String readName(StringReader reader) throws CommandSyntaxException {
      int i = reader.getCursor();

      while(reader.canRead() && isNameCharacter(reader.peek())) {
         reader.skip();
      }

      if (reader.getCursor() == i) {
         throw INVALID_PATH_NODE_EXCEPTION.createWithContext(reader);
      } else {
         return reader.getString().substring(i, reader.getCursor());
      }
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   private static boolean isNameCharacter(char c) {
      return c != ' ' && c != '"' && c != '\'' && c != '[' && c != ']' && c != '.' && c != '{' && c != '}';
   }

   static Predicate getPredicate(NbtCompound filter) {
      return (nbt) -> {
         return NbtHelper.matches(filter, nbt, true);
      };
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }

   public static class NbtPath {
      private final String string;
      private final Object2IntMap nodeEndIndices;
      private final PathNode[] nodes;
      public static final Codec CODEC;

      public static NbtPath parse(String path) throws CommandSyntaxException {
         return (new NbtPathArgumentType()).parse(new StringReader(path));
      }

      public NbtPath(String string, PathNode[] nodes, Object2IntMap nodeEndIndices) {
         this.string = string;
         this.nodes = nodes;
         this.nodeEndIndices = nodeEndIndices;
      }

      public List get(NbtElement element) throws CommandSyntaxException {
         List list = Collections.singletonList(element);
         PathNode[] var3 = this.nodes;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            PathNode pathNode = var3[var5];
            list = pathNode.get(list);
            if (list.isEmpty()) {
               throw this.createNothingFoundException(pathNode);
            }
         }

         return list;
      }

      public int count(NbtElement element) {
         List list = Collections.singletonList(element);
         PathNode[] var3 = this.nodes;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            PathNode pathNode = var3[var5];
            list = pathNode.get(list);
            if (list.isEmpty()) {
               return 0;
            }
         }

         return list.size();
      }

      private List getTerminals(NbtElement start) throws CommandSyntaxException {
         List list = Collections.singletonList(start);

         for(int i = 0; i < this.nodes.length - 1; ++i) {
            PathNode pathNode = this.nodes[i];
            int j = i + 1;
            PathNode var10002 = this.nodes[j];
            Objects.requireNonNull(var10002);
            list = pathNode.getOrInit(list, var10002::init);
            if (list.isEmpty()) {
               throw this.createNothingFoundException(pathNode);
            }
         }

         return list;
      }

      public List getOrInit(NbtElement element, Supplier source) throws CommandSyntaxException {
         List list = this.getTerminals(element);
         PathNode pathNode = this.nodes[this.nodes.length - 1];
         return pathNode.getOrInit(list, source);
      }

      private static int forEach(List elements, Function operation) {
         return (Integer)elements.stream().map(operation).reduce(0, (a, b) -> {
            return a + b;
         });
      }

      public static boolean isTooDeep(NbtElement element, int depth) {
         if (depth >= 512) {
            return true;
         } else {
            Iterator var4;
            NbtElement nbtElement;
            if (element instanceof NbtCompound) {
               NbtCompound nbtCompound = (NbtCompound)element;
               var4 = nbtCompound.values().iterator();

               while(var4.hasNext()) {
                  nbtElement = (NbtElement)var4.next();
                  if (isTooDeep(nbtElement, depth + 1)) {
                     return true;
                  }
               }
            } else if (element instanceof NbtList) {
               NbtList nbtList = (NbtList)element;
               var4 = nbtList.iterator();

               while(var4.hasNext()) {
                  nbtElement = (NbtElement)var4.next();
                  if (isTooDeep(nbtElement, depth + 1)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }

      public int put(NbtElement element, NbtElement source) throws CommandSyntaxException {
         if (isTooDeep(source, this.getDepth())) {
            throw NbtPathArgumentType.TOO_DEEP_EXCEPTION.create();
         } else {
            NbtElement nbtElement = source.copy();
            List list = this.getTerminals(element);
            if (list.isEmpty()) {
               return 0;
            } else {
               PathNode pathNode = this.nodes[this.nodes.length - 1];
               MutableBoolean mutableBoolean = new MutableBoolean(false);
               return forEach(list, (nbt) -> {
                  return pathNode.set(nbt, () -> {
                     if (mutableBoolean.isFalse()) {
                        mutableBoolean.setTrue();
                        return nbtElement;
                     } else {
                        return nbtElement.copy();
                     }
                  });
               });
            }
         }
      }

      private int getDepth() {
         return this.nodes.length;
      }

      public int insert(int index, NbtCompound compound, List elements) throws CommandSyntaxException {
         List list = new ArrayList(elements.size());
         Iterator var5 = elements.iterator();

         while(var5.hasNext()) {
            NbtElement nbtElement = (NbtElement)var5.next();
            NbtElement nbtElement2 = nbtElement.copy();
            list.add(nbtElement2);
            if (isTooDeep(nbtElement2, this.getDepth())) {
               throw NbtPathArgumentType.TOO_DEEP_EXCEPTION.create();
            }
         }

         Collection collection = this.getOrInit(compound, NbtList::new);
         int i = 0;
         boolean bl = false;

         boolean bl2;
         for(Iterator var8 = collection.iterator(); var8.hasNext(); i += bl2 ? 1 : 0) {
            NbtElement nbtElement3 = (NbtElement)var8.next();
            if (!(nbtElement3 instanceof AbstractNbtList)) {
               throw NbtPathArgumentType.EXPECTED_LIST_EXCEPTION.create(nbtElement3);
            }

            AbstractNbtList abstractNbtList = (AbstractNbtList)nbtElement3;
            bl2 = false;
            int j = index < 0 ? abstractNbtList.size() + index + 1 : index;
            Iterator var13 = list.iterator();

            while(var13.hasNext()) {
               NbtElement nbtElement4 = (NbtElement)var13.next();

               try {
                  if (abstractNbtList.addElement(j, bl ? nbtElement4.copy() : nbtElement4)) {
                     ++j;
                     bl2 = true;
                  }
               } catch (IndexOutOfBoundsException var16) {
                  throw NbtPathArgumentType.INVALID_INDEX_EXCEPTION.create(j);
               }
            }

            bl = true;
         }

         return i;
      }

      public int remove(NbtElement element) {
         List list = Collections.singletonList(element);

         for(int i = 0; i < this.nodes.length - 1; ++i) {
            list = this.nodes[i].get(list);
         }

         PathNode pathNode = this.nodes[this.nodes.length - 1];
         Objects.requireNonNull(pathNode);
         return forEach(list, pathNode::clear);
      }

      private CommandSyntaxException createNothingFoundException(PathNode node) {
         int i = this.nodeEndIndices.getInt(node);
         return NbtPathArgumentType.NOTHING_FOUND_EXCEPTION.create(this.string.substring(0, i));
      }

      public String toString() {
         return this.string;
      }

      public String getString() {
         return this.string;
      }

      static {
         CODEC = Codec.STRING.comapFlatMap((path) -> {
            try {
               NbtPath nbtPath = (new NbtPathArgumentType()).parse(new StringReader(path));
               return DataResult.success(nbtPath);
            } catch (CommandSyntaxException var2) {
               return DataResult.error(() -> {
                  return "Failed to parse path " + path + ": " + var2.getMessage();
               });
            }
         }, NbtPath::getString);
      }
   }

   private interface PathNode {
      void get(NbtElement current, List results);

      void getOrInit(NbtElement current, Supplier source, List results);

      NbtElement init();

      int set(NbtElement current, Supplier source);

      int clear(NbtElement current);

      default List get(List elements) {
         return this.process(elements, this::get);
      }

      default List getOrInit(List elements, Supplier supplier) {
         return this.process(elements, (current, results) -> {
            this.getOrInit(current, supplier, results);
         });
      }

      default List process(List elements, BiConsumer action) {
         List list = Lists.newArrayList();
         Iterator var4 = elements.iterator();

         while(var4.hasNext()) {
            NbtElement nbtElement = (NbtElement)var4.next();
            action.accept(nbtElement, list);
         }

         return list;
      }
   }

   static class FilteredRootNode implements PathNode {
      private final Predicate matcher;

      public FilteredRootNode(NbtCompound filter) {
         this.matcher = NbtPathArgumentType.getPredicate(filter);
      }

      public void get(NbtElement current, List results) {
         if (current instanceof NbtCompound && this.matcher.test(current)) {
            results.add(current);
         }

      }

      public void getOrInit(NbtElement current, Supplier source, List results) {
         this.get(current, results);
      }

      public NbtElement init() {
         return new NbtCompound();
      }

      public int set(NbtElement current, Supplier source) {
         return 0;
      }

      public int clear(NbtElement current) {
         return 0;
      }
   }

   static class FilteredListElementNode implements PathNode {
      private final NbtCompound filter;
      private final Predicate predicate;

      public FilteredListElementNode(NbtCompound filter) {
         this.filter = filter;
         this.predicate = NbtPathArgumentType.getPredicate(filter);
      }

      public void get(NbtElement current, List results) {
         if (current instanceof NbtList nbtList) {
            Stream var10000 = nbtList.stream().filter(this.predicate);
            Objects.requireNonNull(results);
            var10000.forEach(results::add);
         }

      }

      public void getOrInit(NbtElement current, Supplier source, List results) {
         MutableBoolean mutableBoolean = new MutableBoolean();
         if (current instanceof NbtList nbtList) {
            nbtList.stream().filter(this.predicate).forEach((nbt) -> {
               results.add(nbt);
               mutableBoolean.setTrue();
            });
            if (mutableBoolean.isFalse()) {
               NbtCompound nbtCompound = this.filter.copy();
               nbtList.add(nbtCompound);
               results.add(nbtCompound);
            }
         }

      }

      public NbtElement init() {
         return new NbtList();
      }

      public int set(NbtElement current, Supplier source) {
         int i = 0;
         if (current instanceof NbtList nbtList) {
            int j = nbtList.size();
            if (j == 0) {
               nbtList.add((NbtElement)source.get());
               ++i;
            } else {
               for(int k = 0; k < j; ++k) {
                  NbtElement nbtElement = nbtList.method_10534(k);
                  if (this.predicate.test(nbtElement)) {
                     NbtElement nbtElement2 = (NbtElement)source.get();
                     if (!nbtElement2.equals(nbtElement) && nbtList.setElement(k, nbtElement2)) {
                        ++i;
                     }
                  }
               }
            }
         }

         return i;
      }

      public int clear(NbtElement current) {
         int i = 0;
         if (current instanceof NbtList nbtList) {
            for(int j = nbtList.size() - 1; j >= 0; --j) {
               if (this.predicate.test(nbtList.method_10534(j))) {
                  nbtList.method_10536(j);
                  ++i;
               }
            }
         }

         return i;
      }
   }

   private static class AllListElementNode implements PathNode {
      public static final AllListElementNode INSTANCE = new AllListElementNode();

      public void get(NbtElement current, List results) {
         if (current instanceof AbstractNbtList abstractNbtList) {
            Iterables.addAll(results, abstractNbtList);
         }

      }

      public void getOrInit(NbtElement current, Supplier source, List results) {
         if (current instanceof AbstractNbtList abstractNbtList) {
            if (abstractNbtList.isEmpty()) {
               NbtElement nbtElement = (NbtElement)source.get();
               if (abstractNbtList.addElement(0, nbtElement)) {
                  results.add(nbtElement);
               }
            } else {
               Iterables.addAll(results, abstractNbtList);
            }
         }

      }

      public NbtElement init() {
         return new NbtList();
      }

      public int set(NbtElement current, Supplier source) {
         if (!(current instanceof AbstractNbtList abstractNbtList)) {
            return 0;
         } else {
            int i = abstractNbtList.size();
            if (i == 0) {
               abstractNbtList.addElement(0, (NbtElement)source.get());
               return 1;
            } else {
               NbtElement nbtElement = (NbtElement)source.get();
               Stream var10001 = abstractNbtList.stream();
               Objects.requireNonNull(nbtElement);
               int j = i - (int)var10001.filter(nbtElement::equals).count();
               if (j == 0) {
                  return 0;
               } else {
                  abstractNbtList.clear();
                  if (!abstractNbtList.addElement(0, nbtElement)) {
                     return 0;
                  } else {
                     for(int k = 1; k < i; ++k) {
                        abstractNbtList.addElement(k, (NbtElement)source.get());
                     }

                     return j;
                  }
               }
            }
         }
      }

      public int clear(NbtElement current) {
         if (current instanceof AbstractNbtList abstractNbtList) {
            int i = abstractNbtList.size();
            if (i > 0) {
               abstractNbtList.clear();
               return i;
            }
         }

         return 0;
      }
   }

   private static class IndexedListElementNode implements PathNode {
      private final int index;

      public IndexedListElementNode(int index) {
         this.index = index;
      }

      public void get(NbtElement current, List results) {
         if (current instanceof AbstractNbtList abstractNbtList) {
            int i = abstractNbtList.size();
            int j = this.index < 0 ? i + this.index : this.index;
            if (0 <= j && j < i) {
               results.add(abstractNbtList.method_10534(j));
            }
         }

      }

      public void getOrInit(NbtElement current, Supplier source, List results) {
         this.get(current, results);
      }

      public NbtElement init() {
         return new NbtList();
      }

      public int set(NbtElement current, Supplier source) {
         if (current instanceof AbstractNbtList abstractNbtList) {
            int i = abstractNbtList.size();
            int j = this.index < 0 ? i + this.index : this.index;
            if (0 <= j && j < i) {
               NbtElement nbtElement = abstractNbtList.method_10534(j);
               NbtElement nbtElement2 = (NbtElement)source.get();
               if (!nbtElement2.equals(nbtElement) && abstractNbtList.setElement(j, nbtElement2)) {
                  return 1;
               }
            }
         }

         return 0;
      }

      public int clear(NbtElement current) {
         if (current instanceof AbstractNbtList abstractNbtList) {
            int i = abstractNbtList.size();
            int j = this.index < 0 ? i + this.index : this.index;
            if (0 <= j && j < i) {
               abstractNbtList.method_10536(j);
               return 1;
            }
         }

         return 0;
      }
   }

   static class FilteredNamedNode implements PathNode {
      private final String name;
      private final NbtCompound filter;
      private final Predicate predicate;

      public FilteredNamedNode(String name, NbtCompound filter) {
         this.name = name;
         this.filter = filter;
         this.predicate = NbtPathArgumentType.getPredicate(filter);
      }

      public void get(NbtElement current, List results) {
         if (current instanceof NbtCompound) {
            NbtElement nbtElement = ((NbtCompound)current).get(this.name);
            if (this.predicate.test(nbtElement)) {
               results.add(nbtElement);
            }
         }

      }

      public void getOrInit(NbtElement current, Supplier source, List results) {
         if (current instanceof NbtCompound nbtCompound) {
            NbtElement nbtElement = nbtCompound.get(this.name);
            if (nbtElement == null) {
               NbtElement nbtElement = this.filter.copy();
               nbtCompound.put(this.name, nbtElement);
               results.add(nbtElement);
            } else if (this.predicate.test(nbtElement)) {
               results.add(nbtElement);
            }
         }

      }

      public NbtElement init() {
         return new NbtCompound();
      }

      public int set(NbtElement current, Supplier source) {
         if (current instanceof NbtCompound nbtCompound) {
            NbtElement nbtElement = nbtCompound.get(this.name);
            if (this.predicate.test(nbtElement)) {
               NbtElement nbtElement2 = (NbtElement)source.get();
               if (!nbtElement2.equals(nbtElement)) {
                  nbtCompound.put(this.name, nbtElement2);
                  return 1;
               }
            }
         }

         return 0;
      }

      public int clear(NbtElement current) {
         if (current instanceof NbtCompound nbtCompound) {
            NbtElement nbtElement = nbtCompound.get(this.name);
            if (this.predicate.test(nbtElement)) {
               nbtCompound.remove(this.name);
               return 1;
            }
         }

         return 0;
      }
   }

   private static class NamedNode implements PathNode {
      private final String name;

      public NamedNode(String name) {
         this.name = name;
      }

      public void get(NbtElement current, List results) {
         if (current instanceof NbtCompound) {
            NbtElement nbtElement = ((NbtCompound)current).get(this.name);
            if (nbtElement != null) {
               results.add(nbtElement);
            }
         }

      }

      public void getOrInit(NbtElement current, Supplier source, List results) {
         if (current instanceof NbtCompound nbtCompound) {
            NbtElement nbtElement;
            if (nbtCompound.contains(this.name)) {
               nbtElement = nbtCompound.get(this.name);
            } else {
               nbtElement = (NbtElement)source.get();
               nbtCompound.put(this.name, nbtElement);
            }

            results.add(nbtElement);
         }

      }

      public NbtElement init() {
         return new NbtCompound();
      }

      public int set(NbtElement current, Supplier source) {
         if (current instanceof NbtCompound nbtCompound) {
            NbtElement nbtElement = (NbtElement)source.get();
            NbtElement nbtElement2 = nbtCompound.put(this.name, nbtElement);
            if (!nbtElement.equals(nbtElement2)) {
               return 1;
            }
         }

         return 0;
      }

      public int clear(NbtElement current) {
         if (current instanceof NbtCompound nbtCompound) {
            if (nbtCompound.contains(this.name)) {
               nbtCompound.remove(this.name);
               return 1;
            }
         }

         return 0;
      }
   }
}
