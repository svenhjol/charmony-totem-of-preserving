package svenhjol.charmony.totem_of_preserving.client;

import net.fabricmc.api.ClientModInitializer;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.totem_of_preserving.TotemOfPreservingMod;
import svenhjol.charmony.totem_of_preserving.client.features.totem_of_preserving.TotemOfPreserving;

public class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Ensure charmony is launched first.
        svenhjol.charmony.core.client.ClientInitializer.init();

        var mod = TotemOfPreservingMod.instance();
        mod.addSidedFeature(TotemOfPreserving.class);

        mod.run(Side.Client);
    }
}
