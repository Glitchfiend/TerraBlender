package terrablender.example.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import terrablender.example.TestSurfaceRuleData;
import terrablender.example.TestBiomes;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class DataGen implements DataGeneratorEntrypoint {
    @Override
    public void buildRegistry(RegistrySetBuilder builder) {
        builder.add(Registries.BIOME, TestBiomes::bootstrap);
        builder.add(Registries.MATERIAL_RULE, TestSurfaceRuleData::bootstrap);
    }
	@Override
	public void onInitializeDataGenerator(@NonNull FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ExampleModDynamicRegistryProvider::new);
	}

	private static class ExampleModDynamicRegistryProvider extends FabricDynamicRegistryProvider {
		private ExampleModDynamicRegistryProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		protected void configure(HolderLookup.@NonNull Provider registries, @NonNull Entries entries) {
			entries.addAll(registries.lookupOrThrow(Registries.BIOME));
			entries.addAll(registries.lookupOrThrow(Registries.MATERIAL_RULE));
		}

		@Override
		public @NonNull String getName() {
			return "World Generation";
		}
	}
}
