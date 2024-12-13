package svenhjol.charmony.totem_of_preserving.client;

import net.fabricmc.api.ClientModInitializer;
import svenhjol.charmony.core.enums.Side;
import svenhjol.charmony.totem_of_preserving.TotemOfPreserving;
import svenhjol.charmony.totem_of_preserving.client.features.totem.Totem;

public class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Ensure charmony is launched first.
        svenhjol.charmony.core.client.ClientInitializer.init();

        var mod = TotemOfPreserving.instance();
        mod.addSidedFeature(Totem.class);

        mod.run(Side.Client);
    }
}
