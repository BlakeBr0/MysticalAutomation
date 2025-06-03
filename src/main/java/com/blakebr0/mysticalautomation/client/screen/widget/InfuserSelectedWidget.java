package com.blakebr0.mysticalautomation.client.screen.widget;

import com.blakebr0.cucumber.client.screen.button.IconButton;
import com.blakebr0.mysticalautomation.client.screen.InfuserScreen;
import com.blakebr0.mysticalautomation.network.payload.InfuserSelectIndexPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.BooleanSupplier;

public class InfuserSelectedWidget extends IconButton {
    private static final ResourceLocation TEXTURE = InfuserScreen.BACKGROUND;

    private final BooleanSupplier isSelected;

    public InfuserSelectedWidget(int x, int y, BlockPos pos, int index, BooleanSupplier isSelected) {
        super(x, y, 6, 7, 218, 0, TEXTURE, button -> onPress(pos, index));
        this.isSelected = isSelected;
    }

    @Override
    protected int getYImage() {
        return this.isHovered() ? 1 : this.isSelected.getAsBoolean() ? 2 : 0;
    }

    private static void onPress(BlockPos pos, int index) {
        PacketDistributor.sendToServer(new InfuserSelectIndexPayload(pos, index));
    }
}
