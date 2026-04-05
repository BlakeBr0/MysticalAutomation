package com.blakebr0.mysticalautomation.client.screen.widget;

import com.blakebr0.cucumber.client.screen.button.IconButton;
import com.blakebr0.mysticalautomation.client.screen.EnchanternatorScreen;
import com.blakebr0.mysticalautomation.network.payload.EnchanternatorSelectLevelPayload;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.function.BooleanSupplier;

public class EnchanternatorLevelWidget extends IconButton {
    private static final Identifier TEXTURE = EnchanternatorScreen.BACKGROUND;

    private final BlockPos pos;
    private final int level;
    private final BooleanSupplier isSelected;

    public EnchanternatorLevelWidget(int x, int y, BlockPos pos, int level, BooleanSupplier isSelected) {
        super(x, y, 9, 9, 206 + ((level - 1) * 9), 0, TEXTURE);
        this.pos = pos;
        this.level = level;
        this.isSelected = isSelected;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        ClientPacketDistributor.sendToServer(new EnchanternatorSelectLevelPayload(this.pos, this.level));
    }

    @Override
    protected int getYImage() {
        return this.isHovered() ? 1 : this.isSelected.getAsBoolean() ? 2 : 0;
    }
}
