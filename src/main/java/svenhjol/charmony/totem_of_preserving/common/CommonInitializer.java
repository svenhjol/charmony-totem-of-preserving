package svenhjol.charmony.totem_of_preserving.common;

import net.fabricmc.api.ModInitializer;
import svenhjol.charmony.core.enums.Side;
import svenhjol.charmony.totem_of_preserving.TotemOfPreserving;
import svenhjol.charmony.totem_of_preserving.common.features.totem.Totem;

public class CommonInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        // Ensure charmony is launched first.
        svenhjol.charmony.core.common.CommonInitializer.init();

        var mod = TotemOfPreserving.instance();
        mod.addSidedFeature(Totem.class);

        mod.run(Side.Common);
    }
}
