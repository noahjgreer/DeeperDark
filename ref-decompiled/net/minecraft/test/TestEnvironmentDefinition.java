package net.minecraft.test;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import org.slf4j.Logger;

public interface TestEnvironmentDefinition {
   Codec CODEC = Registries.TEST_ENVIRONMENT_DEFINITION_TYPE.getCodec().dispatch(TestEnvironmentDefinition::getCodec, (codec) -> {
      return codec;
   });
   Codec ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.TEST_ENVIRONMENT, CODEC);

   static MapCodec registerAndGetDefault(Registry registry) {
      Registry.register(registry, (String)"all_of", TestEnvironmentDefinition.AllOf.CODEC);
      Registry.register(registry, (String)"game_rules", TestEnvironmentDefinition.GameRules.CODEC);
      Registry.register(registry, (String)"time_of_day", TestEnvironmentDefinition.TimeOfDay.CODEC);
      Registry.register(registry, (String)"weather", TestEnvironmentDefinition.Weather.CODEC);
      return (MapCodec)Registry.register(registry, (String)"function", TestEnvironmentDefinition.Function.CODEC);
   }

   void setup(ServerWorld world);

   default void teardown(ServerWorld world) {
   }

   MapCodec getCodec();

   public static record AllOf(List definitions) implements TestEnvironmentDefinition {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(TestEnvironmentDefinition.ENTRY_CODEC.listOf().fieldOf("definitions").forGetter(AllOf::definitions)).apply(instance, AllOf::new);
      });

      public AllOf(TestEnvironmentDefinition... definitionTypes) {
         this(Arrays.stream(definitionTypes).map(RegistryEntry::of).toList());
      }

      public AllOf(List list) {
         this.definitions = list;
      }

      public void setup(ServerWorld world) {
         this.definitions.forEach((definition) -> {
            ((TestEnvironmentDefinition)definition.value()).setup(world);
         });
      }

      public void teardown(ServerWorld world) {
         this.definitions.forEach((definition) -> {
            ((TestEnvironmentDefinition)definition.value()).teardown(world);
         });
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public List definitions() {
         return this.definitions;
      }
   }

   public static record GameRules(List boolRules, List intRules) implements TestEnvironmentDefinition {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(TestEnvironmentDefinition.GameRules.RuleValue.createCodec(net.minecraft.world.GameRules.BooleanRule.class, Codec.BOOL).listOf().fieldOf("bool_rules").forGetter(GameRules::boolRules), TestEnvironmentDefinition.GameRules.RuleValue.createCodec(net.minecraft.world.GameRules.IntRule.class, Codec.INT).listOf().fieldOf("int_rules").forGetter(GameRules::intRules)).apply(instance, GameRules::new);
      });

      public GameRules(List list, List list2) {
         this.boolRules = list;
         this.intRules = list2;
      }

      public void setup(ServerWorld world) {
         net.minecraft.world.GameRules gameRules = world.getGameRules();
         MinecraftServer minecraftServer = world.getServer();
         Iterator var4 = this.boolRules.iterator();

         RuleValue ruleValue;
         while(var4.hasNext()) {
            ruleValue = (RuleValue)var4.next();
            ((net.minecraft.world.GameRules.BooleanRule)gameRules.get(ruleValue.key())).set((Boolean)ruleValue.value(), minecraftServer);
         }

         var4 = this.intRules.iterator();

         while(var4.hasNext()) {
            ruleValue = (RuleValue)var4.next();
            ((net.minecraft.world.GameRules.IntRule)gameRules.get(ruleValue.key())).set((Integer)ruleValue.value(), minecraftServer);
         }

      }

      public void teardown(ServerWorld world) {
         net.minecraft.world.GameRules gameRules = world.getGameRules();
         MinecraftServer minecraftServer = world.getServer();
         Iterator var4 = this.boolRules.iterator();

         RuleValue ruleValue;
         while(var4.hasNext()) {
            ruleValue = (RuleValue)var4.next();
            ((net.minecraft.world.GameRules.BooleanRule)gameRules.get(ruleValue.key())).setValue((net.minecraft.world.GameRules.BooleanRule)net.minecraft.world.GameRules.getRuleType(ruleValue.key()).createRule(), minecraftServer);
         }

         var4 = this.intRules.iterator();

         while(var4.hasNext()) {
            ruleValue = (RuleValue)var4.next();
            ((net.minecraft.world.GameRules.IntRule)gameRules.get(ruleValue.key())).setValue((net.minecraft.world.GameRules.IntRule)net.minecraft.world.GameRules.getRuleType(ruleValue.key()).createRule(), minecraftServer);
         }

      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public static RuleValue ruleValue(net.minecraft.world.GameRules.Key key, Object value) {
         return new RuleValue(key, value);
      }

      public List boolRules() {
         return this.boolRules;
      }

      public List intRules() {
         return this.intRules;
      }

      public static record RuleValue(net.minecraft.world.GameRules.Key key, Object value) {
         public RuleValue(net.minecraft.world.GameRules.Key key, Object object) {
            this.key = key;
            this.value = object;
         }

         public static Codec createCodec(Class ruleClass, Codec valueCodec) {
            return RecordCodecBuilder.create((instance) -> {
               return instance.group(net.minecraft.world.GameRules.createKeyCodec(ruleClass).fieldOf("rule").forGetter(RuleValue::key), valueCodec.fieldOf("value").forGetter(RuleValue::value)).apply(instance, RuleValue::new);
            });
         }

         public net.minecraft.world.GameRules.Key key() {
            return this.key;
         }

         public Object value() {
            return this.value;
         }
      }
   }

   public static record TimeOfDay(int time) implements TestEnvironmentDefinition {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codecs.NON_NEGATIVE_INT.fieldOf("time").forGetter(TimeOfDay::time)).apply(instance, TimeOfDay::new);
      });

      public TimeOfDay(int i) {
         this.time = i;
      }

      public void setup(ServerWorld world) {
         world.setTimeOfDay((long)this.time);
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public int time() {
         return this.time;
      }
   }

   public static record Weather(State weather) implements TestEnvironmentDefinition {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(TestEnvironmentDefinition.Weather.State.CODEC.fieldOf("weather").forGetter(Weather::weather)).apply(instance, Weather::new);
      });

      public Weather(State state) {
         this.weather = state;
      }

      public void setup(ServerWorld world) {
         this.weather.apply(world);
      }

      public void teardown(ServerWorld world) {
         world.resetWeather();
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public State weather() {
         return this.weather;
      }

      public static enum State implements StringIdentifiable {
         CLEAR("clear", 100000, 0, false, false),
         RAIN("rain", 0, 100000, true, false),
         THUNDER("thunder", 0, 100000, true, true);

         public static final Codec CODEC = StringIdentifiable.createCodec(State::values);
         private final String name;
         private final int clearDuration;
         private final int rainDuration;
         private final boolean raining;
         private final boolean thundering;

         private State(final String name, final int clearDuration, final int rainDuration, final boolean raining, final boolean thundering) {
            this.name = name;
            this.clearDuration = clearDuration;
            this.rainDuration = rainDuration;
            this.raining = raining;
            this.thundering = thundering;
         }

         void apply(ServerWorld world) {
            world.setWeather(this.clearDuration, this.rainDuration, this.raining, this.thundering);
         }

         public String asString() {
            return this.name;
         }

         // $FF: synthetic method
         private static State[] method_67068() {
            return new State[]{CLEAR, RAIN, THUNDER};
         }
      }
   }

   public static record Function(Optional setupFunction, Optional teardownFunction) implements TestEnvironmentDefinition {
      private static final Logger LOGGER = LogUtils.getLogger();
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Identifier.CODEC.optionalFieldOf("setup").forGetter(Function::setupFunction), Identifier.CODEC.optionalFieldOf("teardown").forGetter(Function::teardownFunction)).apply(instance, Function::new);
      });

      public Function(Optional optional, Optional optional2) {
         this.setupFunction = optional;
         this.teardownFunction = optional2;
      }

      public void setup(ServerWorld world) {
         this.setupFunction.ifPresent((functionId) -> {
            executeFunction(world, functionId);
         });
      }

      public void teardown(ServerWorld world) {
         this.teardownFunction.ifPresent((functionId) -> {
            executeFunction(world, functionId);
         });
      }

      private static void executeFunction(ServerWorld world, Identifier functionId) {
         MinecraftServer minecraftServer = world.getServer();
         CommandFunctionManager commandFunctionManager = minecraftServer.getCommandFunctionManager();
         Optional optional = commandFunctionManager.getFunction(functionId);
         if (optional.isPresent()) {
            ServerCommandSource serverCommandSource = minecraftServer.getCommandSource().withLevel(2).withSilent().withWorld(world);
            commandFunctionManager.execute((CommandFunction)optional.get(), serverCommandSource);
         } else {
            LOGGER.error("Test Batch failed for non-existent function {}", functionId);
         }

      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public Optional setupFunction() {
         return this.setupFunction;
      }

      public Optional teardownFunction() {
         return this.teardownFunction;
      }
   }
}
