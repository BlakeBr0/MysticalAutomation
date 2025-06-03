package com.blakebr0.mysticalautomation.network.payload;

import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.tilentity.InfuserTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record InfuserSelectIndexPayload(BlockPos pos, int index) implements CustomPacketPayload {
    public static final Type<InfuserSelectIndexPayload> TYPE = new Type<>(MysticalAutomation.resource("infuser_select_index"));

    public static final StreamCodec<FriendlyByteBuf, InfuserSelectIndexPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            InfuserSelectIndexPayload::pos,
            ByteBufCodecs.VAR_INT,
            InfuserSelectIndexPayload::index,
            InfuserSelectIndexPayload::new
    );

    @Override
    public Type<InfuserSelectIndexPayload> type() {
        return TYPE;
    }

    public static void handleServer(InfuserSelectIndexPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            var level = player.level();
            var tile = level.getBlockEntity(payload.pos);

            if (tile instanceof InfuserTileEntity machine) {
                machine.setSelectedIndex(payload.index);
            }
        });
    }
}
