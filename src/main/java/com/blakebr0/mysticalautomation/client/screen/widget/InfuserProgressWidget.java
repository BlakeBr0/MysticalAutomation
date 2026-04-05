package com.blakebr0.mysticalautomation.client.screen.widget;

import com.blakebr0.mysticalautomation.client.screen.InfuserScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

public class InfuserProgressWidget extends AbstractWidget {
    private static final Identifier TEXTURE = InfuserScreen.BACKGROUND;

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
    protected void extractWidgetRenderState(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float v) {
        gfx.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX(), this.getY(), 225, 0, this.width, this.height, 256, 256);

        if (this.isSelected.getAsBoolean()) {
            var offset = this.getProgressScaled();

            gfx.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX(), this.getY(), 225, this.height, offset, this.height, 256, 256);
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
