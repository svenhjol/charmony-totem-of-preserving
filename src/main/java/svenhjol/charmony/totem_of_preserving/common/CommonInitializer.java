package svenhjol.charmony.totem_of_preserving.common;

import net.fabricmc.api.ModInitializer;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.totem_of_preserving.TotemOfPreservingMod;
import svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving.TotemOfPreserving;

public class CommonInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        // Ensure charmony is launched first.
        svenhjol.charmony.core.common.CommonInitializer.init();

        var mod = TotemOfPreservingMod.instance();
        mod.addSidedFeature(TotemOfPreserving.class);

        mod.run(Side.Common);
    }
}
