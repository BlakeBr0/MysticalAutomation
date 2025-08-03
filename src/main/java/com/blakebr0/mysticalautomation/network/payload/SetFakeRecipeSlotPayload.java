package com.blakebr0.mysticalautomation.network.payload;

import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.util.IFakeRecipeContainer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetFakeRecipeSlotPayload(int slot, ItemStack stack) implements CustomPacketPayload {
    public static final Type<SetFakeRecipeSlotPayload> TYPE = new Type<>(MysticalAutomation.resource("set_fake_recipe_slot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetFakeRecipeSlotPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SetFakeRecipeSlotPayload::slot,
            ItemStack.STREAM_CODEC,
            SetFakeRecipeSlotPayload::stack,
            SetFakeRecipeSlotPayload::new
    );

    @Override
    public Type<SetFakeRecipeSlotPayload> type() {
        return TYPE;
    }

    public static void handleServer(SetFakeRecipeSlotPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            var menu = player.containerMenu;

            if (payload.slot < menu.slots.size() && menu instanceof IFakeRecipeContainer fakeRecipeContainer) {
                var slot = menu.slots.get(payload.slot);

                fakeRecipeContainer.setFakeRecipeSlot(slot, payload.stack);
            }
        });
    }
}
