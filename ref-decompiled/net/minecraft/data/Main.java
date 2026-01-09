package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.SharedConstants;
import net.minecraft.data.advancement.vanilla.VanillaAdvancementProviders;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.data.loottable.rebalance.TradeRebalanceLootTableProviders;
import net.minecraft.data.loottable.vanilla.VanillaLootTableProviders;
import net.minecraft.data.recipe.VanillaRecipeGenerator;
import net.minecraft.data.report.BiomeParametersProvider;
import net.minecraft.data.report.BlockListProvider;
import net.minecraft.data.report.CommandSyntaxProvider;
import net.minecraft.data.report.DataPackStructureProvider;
import net.minecraft.data.report.ItemListProvider;
import net.minecraft.data.report.PacketReportProvider;
import net.minecraft.data.report.RegistryDumpProvider;
import net.minecraft.data.tag.TagProvider;
import net.minecraft.data.tag.rebalance.TradeRebalanceEnchantmentTagProvider;
import net.minecraft.data.tag.vanilla.VanillaBannerPatternTagProvider;
import net.minecraft.data.tag.vanilla.VanillaBiomeTagProvider;
import net.minecraft.data.tag.vanilla.VanillaBlockTagProvider;
import net.minecraft.data.tag.vanilla.VanillaDamageTypeTagProvider;
import net.minecraft.data.tag.vanilla.VanillaDialogTagProvider;
import net.minecraft.data.tag.vanilla.VanillaEnchantmentTagProvider;
import net.minecraft.data.tag.vanilla.VanillaEntityTypeTagProvider;
import net.minecraft.data.tag.vanilla.VanillaFlatLevelGeneratorPresetTagProvider;
import net.minecraft.data.tag.vanilla.VanillaFluidTagProvider;
import net.minecraft.data.tag.vanilla.VanillaGameEventTagProvider;
import net.minecraft.data.tag.vanilla.VanillaInstrumentTagProvider;
import net.minecraft.data.tag.vanilla.VanillaItemTagProvider;
import net.minecraft.data.tag.vanilla.VanillaPaintingVariantTagProvider;
import net.minecraft.data.tag.vanilla.VanillaPointOfInterestTypeTagProvider;
import net.minecraft.data.tag.vanilla.VanillaStructureTagProvider;
import net.minecraft.data.tag.vanilla.VanillaWorldPresetTagProvider;
import net.minecraft.data.validate.StructureValidatorProvider;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.TradeRebalanceBuiltinRegistries;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.UsesSystemOut;

public class Main {
   @UsesSystemOut(
      reason = "System.out needed before bootstrap"
   )
   @DontObfuscate
   public static void main(String[] args) throws IOException {
      SharedConstants.createGameVersion();
      OptionParser optionParser = new OptionParser();
      OptionSpec optionSpec = optionParser.accepts("help", "Show the help menu").forHelp();
      OptionSpec optionSpec2 = optionParser.accepts("server", "Include server generators");
      OptionSpec optionSpec3 = optionParser.accepts("dev", "Include development tools");
      OptionSpec optionSpec4 = optionParser.accepts("reports", "Include data reports");
      optionParser.accepts("validate", "Validate inputs");
      OptionSpec optionSpec5 = optionParser.accepts("all", "Include all generators");
      OptionSpec optionSpec6 = optionParser.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated", new String[0]);
      OptionSpec optionSpec7 = optionParser.accepts("input", "Input folder").withRequiredArg();
      OptionSet optionSet = optionParser.parse(args);
      if (!optionSet.has(optionSpec) && optionSet.hasOptions()) {
         Path path = Paths.get((String)optionSpec6.value(optionSet));
         boolean bl = optionSet.has(optionSpec5);
         boolean bl2 = bl || optionSet.has(optionSpec2);
         boolean bl3 = bl || optionSet.has(optionSpec3);
         boolean bl4 = bl || optionSet.has(optionSpec4);
         Collection collection = optionSet.valuesOf(optionSpec7).stream().map((input) -> {
            return Paths.get(input);
         }).toList();
         DataGenerator dataGenerator = new DataGenerator(path, SharedConstants.getGameVersion(), true);
         create(dataGenerator, collection, bl2, bl3, bl4);
         dataGenerator.run();
      } else {
         optionParser.printHelpOn(System.out);
      }
   }

   private static DataProvider.Factory toFactory(BiFunction baseFactory, CompletableFuture registriesFuture) {
      return (output) -> {
         return (DataProvider)baseFactory.apply(output, registriesFuture);
      };
   }

   public static void create(DataGenerator dataGenerator, Collection inputs, boolean includeClient, boolean includeServer, boolean includeDev) {
      DataGenerator.Pack pack = dataGenerator.createVanillaPack(includeClient);
      pack.addProvider((output) -> {
         return (new SnbtProvider(output, inputs)).addWriter(new StructureValidatorProvider());
      });
      CompletableFuture completableFuture = CompletableFuture.supplyAsync(BuiltinRegistries::createWrapperLookup, Util.getMainWorkerExecutor());
      DataGenerator.Pack pack2 = dataGenerator.createVanillaPack(includeClient);
      pack2.addProvider(toFactory(DynamicRegistriesProvider::new, completableFuture));
      pack2.addProvider(toFactory(VanillaAdvancementProviders::createVanillaProvider, completableFuture));
      pack2.addProvider(toFactory(VanillaLootTableProviders::createVanillaProvider, completableFuture));
      pack2.addProvider(toFactory(VanillaRecipeGenerator.Provider::new, completableFuture));
      TagProvider tagProvider = (TagProvider)pack2.addProvider(toFactory(VanillaBlockTagProvider::new, completableFuture));
      TagProvider tagProvider2 = (TagProvider)pack2.addProvider(toFactory(VanillaItemTagProvider::new, completableFuture));
      TagProvider tagProvider3 = (TagProvider)pack2.addProvider(toFactory(VanillaBiomeTagProvider::new, completableFuture));
      TagProvider tagProvider4 = (TagProvider)pack2.addProvider(toFactory(VanillaBannerPatternTagProvider::new, completableFuture));
      TagProvider tagProvider5 = (TagProvider)pack2.addProvider(toFactory(VanillaStructureTagProvider::new, completableFuture));
      pack2.addProvider(toFactory(VanillaDamageTypeTagProvider::new, completableFuture));
      pack2.addProvider(toFactory(VanillaDialogTagProvider::new, completableFuture));
      pack2.addProvider(toFactory(VanillaEntityTypeTagProvider::new, completableFuture));
      pack2.addProvider(toFactory(VanillaFlatLevelGeneratorPresetTagProvider::new, completableFuture));
      pack2.addProvider(toFactory(VanillaFluidTagProvider::new, completableFuture));
      pack2.addProvider(toFactory(VanillaGameEventTagProvider::new, completableFuture));
      pack2.addProvider(toFactory(VanillaInstrumentTagProvider::new, completableFuture));
      pack2.addProvider(toFactory(VanillaPaintingVariantTagProvider::new, completableFuture));
      pack2.addProvider(toFactory(VanillaPointOfInterestTypeTagProvider::new, completableFuture));
      pack2.addProvider(toFactory(VanillaWorldPresetTagProvider::new, completableFuture));
      pack2.addProvider(toFactory(VanillaEnchantmentTagProvider::new, completableFuture));
      pack2 = dataGenerator.createVanillaPack(includeServer);
      pack2.addProvider((output) -> {
         return new NbtProvider(output, inputs);
      });
      pack2 = dataGenerator.createVanillaPack(includeDev);
      pack2.addProvider(toFactory(BiomeParametersProvider::new, completableFuture));
      pack2.addProvider(toFactory(ItemListProvider::new, completableFuture));
      pack2.addProvider(toFactory(BlockListProvider::new, completableFuture));
      pack2.addProvider(toFactory(CommandSyntaxProvider::new, completableFuture));
      pack2.addProvider(RegistryDumpProvider::new);
      pack2.addProvider(PacketReportProvider::new);
      pack2.addProvider(DataPackStructureProvider::new);
      CompletableFuture completableFuture2 = TradeRebalanceBuiltinRegistries.validate(completableFuture);
      CompletableFuture completableFuture3 = completableFuture2.thenApply(RegistryBuilder.FullPatchesRegistriesPair::patches);
      DataGenerator.Pack pack3 = dataGenerator.createVanillaSubPack(includeClient, "trade_rebalance");
      pack3.addProvider(toFactory(DynamicRegistriesProvider::new, completableFuture3));
      pack3.addProvider((output) -> {
         return MetadataProvider.create(output, Text.translatable("dataPack.trade_rebalance.description"), FeatureSet.of(FeatureFlags.TRADE_REBALANCE));
      });
      pack3.addProvider(toFactory(TradeRebalanceLootTableProviders::createTradeRebalanceProvider, completableFuture));
      pack3.addProvider(toFactory(TradeRebalanceEnchantmentTagProvider::new, completableFuture));
      pack2 = dataGenerator.createVanillaSubPack(includeClient, "redstone_experiments");
      pack2.addProvider((output) -> {
         return MetadataProvider.create(output, Text.translatable("dataPack.redstone_experiments.description"), FeatureSet.of(FeatureFlags.REDSTONE_EXPERIMENTS));
      });
      pack2 = dataGenerator.createVanillaSubPack(includeClient, "minecart_improvements");
      pack2.addProvider((output) -> {
         return MetadataProvider.create(output, Text.translatable("dataPack.minecart_improvements.description"), FeatureSet.of(FeatureFlags.MINECART_IMPROVEMENTS));
      });
   }
}
