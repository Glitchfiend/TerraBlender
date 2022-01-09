/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0.
 ******************************************************************************/
package terrablender.core;

import net.fabricmc.api.DedicatedServerModInitializer;

public class TerraBlenderFabricServer implements DedicatedServerModInitializer
{
    @Override
    public void onInitializeServer()
    {
        TerraBlenderFabric.postInitialize();
    }
}
