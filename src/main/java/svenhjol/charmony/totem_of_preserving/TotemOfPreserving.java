package svenhjol.charmony.totem_of_preserving;

import net.minecraft.resources.ResourceLocation;
import svenhjol.charmony.core.annotations.ModDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.enums.Side;

@ModDefinition(
    id = TotemOfPreserving.ID,
    name = "Totem of Preserving",
    sides = {Side.Client, Side.Common},
    description = "A totem that preserves your items on death."
)
public class TotemOfPreserving extends Mod {
    public static final String ID = "charmony-totem-of-preserving";
    private static TotemOfPreserving instance;

    private TotemOfPreserving() {}

    public static TotemOfPreserving instance() {
        if (instance == null) {
            instance = new TotemOfPreserving();
        }
        return instance;
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }
}
