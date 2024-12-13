package svenhjol.charmony.totem_of_preserving;

import net.minecraft.resources.ResourceLocation;
import svenhjol.charmony.core.annotations.ModDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.enums.Side;

@ModDefinition(
    id = TotemOfPreservingMod.ID,
    name = "Totem of Preserving",
    sides = {Side.Client, Side.Common},
    description = "A totem that preserves your items on death."
)
public class TotemOfPreservingMod extends Mod {
    public static final String ID = "charmony-totem-of-preserving";
    private static TotemOfPreservingMod instance;

    private TotemOfPreservingMod() {}

    public static TotemOfPreservingMod instance() {
        if (instance == null) {
            instance = new TotemOfPreservingMod();
        }
        return instance;
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }
}