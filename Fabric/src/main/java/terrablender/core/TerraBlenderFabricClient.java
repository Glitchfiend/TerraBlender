/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.core;

import net.fabricmc.api.ClientModInitializer;

public class TerraBlenderFabricClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        TerraBlenderFabric.postInitialize();
    }
}
