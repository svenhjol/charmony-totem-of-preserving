package svenhjol.charmony.totem_of_preserving.common.features.totem;

import net.minecraft.util.Mth;
import svenhjol.charmony.core.annotations.Configurable;
import svenhjol.charmony.core.annotations.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.enums.Side;
import svenhjol.charmony.totem_of_preserving.TotemOfPreserving;

@FeatureDefinition(side = Side.Common, description = "Preserves your items on death.")
public final class Totem extends SidedFeature {
    public final Advancements advancements;
    public final Registers registers;
    public final Handlers handlers;
    public final Providers providers;

    @Configurable(
        name = "Grave mode",
        description = """
            If true, a totem of preserving will always be created when you die.
            If false, you must be holding a totem of preserving to preserve your items on death."""
    )
    private static boolean graveMode = false;

    @Configurable(
        name = "Durability",
        description = """
            The maximum number of times a single totem can be used. Once a totem runs out of uses it is destroyed.
            A value of -1 means that the totem is never destroyed.
            You can add an echo shard on an anvil to increase the durability of the totem.
            Note: Durability has no effect if 'Grave mode' is enabled."""
    )
    private static int durability = 3;

    @Configurable(
        name = "Owner only",
        description = "If true, only the owner of the totem may pick it up.",
        requireRestart = false
    )
    private static boolean ownerOnly = false;

    @Configurable(
        name = "Show death position",
        description = "If true, the coordinates where you died will be added to the player's chat screen.",
        requireRestart = false
    )
    private static boolean showDeathPositionInChat = false;

    public Totem(Mod instance) {
        super(instance);

        advancements = new Advancements(this);
        handlers = new Handlers(this);
        registers = new Registers(this);
        providers = new Providers(this);
    }

    public static Totem feature() {
        return TotemOfPreserving.instance().sidedFeature(Totem.class);
    }

    public boolean graveMode() {
        return graveMode;
    }

    public int durability() {
        return Mth.clamp(durability, -1, 1000);
    }

    public boolean ownerOnly() {
        return ownerOnly;
    }

    public boolean showDeathPositionInChat() {
        return showDeathPositionInChat;
    }

}
