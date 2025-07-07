package com.blakebr0.mysticalautomation.network.payload;

import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.tileentity.EnchanternatorTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EnchanternatorSelectLevelPayload(BlockPos pos, int level) implements CustomPacketPayload {
    public static final Type<EnchanternatorSelectLevelPayload> TYPE = new Type<>(MysticalAutomation.resource("enchanternator_select_level"));

    public static final StreamCodec<FriendlyByteBuf, EnchanternatorSelectLevelPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            EnchanternatorSelectLevelPayload::pos,
            ByteBufCodecs.VAR_INT,
            EnchanternatorSelectLevelPayload::level,
            EnchanternatorSelectLevelPayload::new
    );

    @Override
    public Type<EnchanternatorSelectLevelPayload> type() {
        return TYPE;
    }

    public static void handleServer(EnchanternatorSelectLevelPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            var level = player.level();
            var tile = level.getBlockEntity(payload.pos);

            if (tile instanceof EnchanternatorTileEntity machine) {
                var selectedLevel = machine.getSelectedLevel();
                if (selectedLevel == payload.level) {
                    machine.setSelectedLevel(0);
                } else {
                    machine.setSelectedLevel(payload.level);
                }
            }
        });
    }
}
