package terrablender.core;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import terrablender.api.RegionDefinition;

public class Registries {

    public static final ResourceKey<Registry<RegionDefinition>> REGION = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(TerraBlender.MOD_ID, "region"));


}
