package com.blakebr0.mysticalautomation.client.screen.widget;

import com.blakebr0.cucumber.client.screen.button.IconButton;
import com.blakebr0.mysticalautomation.client.screen.InfuserScreen;
import com.blakebr0.mysticalautomation.network.payload.InfuserSelectIndexPayload;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.function.BooleanSupplier;

public class InfuserSelectedWidget extends IconButton {
    private static final Identifier TEXTURE = InfuserScreen.BACKGROUND;

    private final BlockPos pos;
    private final int index;
    private final BooleanSupplier isSelected;

    public InfuserSelectedWidget(int x, int y, BlockPos pos, int index, BooleanSupplier isSelected) {
        super(x, y, 6, 7, 218, 0, TEXTURE);
        this.pos = pos;
        this.index = index;
        this.isSelected = isSelected;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        ClientPacketDistributor.sendToServer(new InfuserSelectIndexPayload(this.pos, this.index));
    }

    @Override
    protected int getYImage() {
        return this.isHovered() ? 1 : this.isSelected.getAsBoolean() ? 2 : 0;
    }
}
