package com.blakebr0.mysticalautomation.client.screen.widget;

import com.blakebr0.mysticalautomation.client.screen.FarmerScreen;
import com.blakebr0.mysticalautomation.lib.ModTooltips;
import net.minecraft.ChatFormatting;
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

        var stages = this.stages.getAsInt();

        if (stages > 0 && mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height) {
            var font = Minecraft.getInstance().font;
            var stage = stages * ((double) this.progress.getAsInt() / (double) this.total.getAsInt());
            var text = ModTooltips.STAGE.args((int) Math.floor(stage), stages).color(ChatFormatting.WHITE).build();

            gfx.renderTooltip(font, text, mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) { }

    private int getProgressScaled() {
        int i = this.progress.getAsInt();
        int j = this.total.getAsInt();
        return j != 0 && i != 0 ? i * this.height / j : 0;
    }
}
