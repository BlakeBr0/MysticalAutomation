package com.blakebr0.mysticalautomation.client.screen.widget;

import com.blakebr0.mysticalautomation.client.screen.InfuserScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

public class InfuserProgressWidget extends AbstractWidget {
    private static final ResourceLocation TEXTURE = InfuserScreen.BACKGROUND;

    private final IntSupplier progress;
    private final IntSupplier total;
    private final BooleanSupplier isSelected;

    public InfuserProgressWidget(int x, int y, IntSupplier progress, IntSupplier total, BooleanSupplier isSelected) {
        super(x, y, 14, 3, Component.literal("Infuser Progress"));
        this.progress = progress;
        this.total = total;
        this.isSelected = isSelected;
        this.active = false; // not a clickable element
    }

    @Override
    protected void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        gfx.blit(TEXTURE, this.getX(), this.getY(), 225, 0, this.width, this.height);

        if (this.isSelected.getAsBoolean()) {
            var offset = this.getProgressScaled();

            gfx.blit(TEXTURE, this.getX(), this.getY(), 225, this.height, offset, this.height);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) { }

    private int getProgressScaled() {
        int i = this.progress.getAsInt();
        int j = this.total.getAsInt();
        return j != 0 && i != 0 ? i * this.width / j : 0;
    }
}
