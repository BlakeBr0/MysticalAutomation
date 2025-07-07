package com.blakebr0.mysticalautomation.client.screen.widget;

import com.blakebr0.cucumber.util.Formatting;
import com.blakebr0.mysticalautomation.client.screen.FarmerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.IntSupplier;

public class FarmerStageProgressWidget extends AbstractWidget {
    private static final ResourceLocation TEXTURE = FarmerScreen.BACKGROUND;

    private final IntSupplier progress;
    private final IntSupplier total;
    private final IntSupplier stages;

    public FarmerStageProgressWidget(int x, int y, IntSupplier progress, IntSupplier total, IntSupplier stages) {
        super(x, y, 2, 16, Component.literal("Farmer Stage Progress"));
        this.progress = progress;
        this.total = total;
        this.stages = stages;
        this.active = false; // not a clickable element
    }

    @Override
    protected void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        var offset = this.getProgressScaled();

        gfx.blit(TEXTURE, this.getX(), this.getY(), 177, 0, this.width, this.height);
        gfx.blit(TEXTURE, this.getX(), this.getY() + this.height - offset, 177 + this.width, this.height - offset, this.width, offset);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) { }

    private int getProgressScaled() {
        int i = this.progress.getAsInt();
        int j = this.total.getAsInt();
        return j != 0 && i != 0 ? i * this.height / j : 0;
    }
}
