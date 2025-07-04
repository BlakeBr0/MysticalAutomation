package com.blakebr0.mysticalautomation.client.screen.widget;

import com.blakebr0.cucumber.client.screen.button.IconButton;
import com.blakebr0.mysticalautomation.client.screen.EnchanternatorScreen;
import com.blakebr0.mysticalautomation.network.payload.EnchanternatorSelectLevelPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.BooleanSupplier;

public class EnchanternatorLevelWidget extends IconButton {
    private static final ResourceLocation TEXTURE = EnchanternatorScreen.BACKGROUND;

    private final BooleanSupplier isSelected;

    public EnchanternatorLevelWidget(int x, int y, BlockPos pos, int level, BooleanSupplier isSelected) {
        super(x, y, 9, 9, 206 + ((level - 1) * 9), 0, TEXTURE, button -> onPress(pos, level));
        this.isSelected = isSelected;
    }

    @Override
    protected int getYImage() {
        return this.isHovered() ? 1 : this.isSelected.getAsBoolean() ? 2 : 0;
    }

    private static void onPress(BlockPos pos, int index) {
        PacketDistributor.sendToServer(new EnchanternatorSelectLevelPayload(pos, index));
    }
}
