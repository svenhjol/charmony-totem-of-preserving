package svenhjol.charmony.totem_of_preserving.common.features.totem_of_preserving;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.List;
import java.util.function.Consumer;

public record TotemData(List<ItemStack> items, String message, boolean glint) implements TooltipProvider {
    public static final Codec<TotemData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ItemStack.OPTIONAL_CODEC.listOf().fieldOf("items")
            .forGetter(TotemData::items),
        Codec.STRING.fieldOf("message")
            .forGetter(TotemData::message),
        Codec.BOOL.fieldOf("glint")
            .forGetter(TotemData::glint)
    ).apply(instance, TotemData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TotemData> STREAM_CODEC = StreamCodec.composite(
        ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()),
            TotemData::items,
        ByteBufCodecs.STRING_UTF8,
            TotemData::message,
        ByteBufCodecs.BOOL,
            TotemData::glint,
        TotemData::new
    );

    public static final TotemData EMPTY = new TotemData(List.of(), "", false);

    public static Mutable create() {
        return new Mutable(EMPTY);
    }

    public static TotemData get(ItemStack stack) {
        return stack.getOrDefault(TotemOfPreserving.feature().registers.data.get(), EMPTY);
    }

    public static Mutable mutable(ItemStack stack) {
        return new Mutable(get(stack));
    }

    public static void set(ItemStack stack, Mutable mutable) {
        stack.set(TotemOfPreserving.feature().registers.data.get(), mutable.toImmutable());
    }

    @Override
    public void addToTooltip(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag, DataComponentGetter dataComponentGetter) {
        if (!this.message().isEmpty()) {
            consumer.accept(Component.literal(this.message()));
        }

        if (!this.items().isEmpty()) {
            var size = this.items().size();
            var str = size == 1 ? "totem_of_preserving.item" : "totem_of_preserving.items";
            consumer.accept(Component.literal(I18n.get(str, size)));
        }
    }

    public static class Mutable {
        private List<ItemStack> items;
        private String message;
        private boolean glint;

        public Mutable(TotemData data) {
            this.items = data.items();
            this.message = data.message();
            this.glint = data.glint();
        }

        public TotemData toImmutable() {
            return new TotemData(items, message, glint);
        }

        public void save(ItemStack stack) {
            TotemData.set(stack, this);
        }

        public Mutable setMessage(String message) {
            this.message = message;
            return this;
        }

        public Mutable setItems(List<ItemStack> items) {
            this.items = items;
            return this;
        }

        public Mutable setGlint(boolean flag) {
            this.glint = flag;
            return this;
        }
    }
}
