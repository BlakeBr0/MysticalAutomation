package com.blakebr0.mysticalautomation.client.screen;

import com.blakebr0.cucumber.client.screen.BaseContainerScreen;
import com.blakebr0.cucumber.client.screen.widget.EnergyBarWidget;
import com.blakebr0.cucumber.client.screen.widget.FuelWidget;
import com.blakebr0.cucumber.client.screen.widget.ProgressArrowWidget;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.client.screen.widget.FarmerStageProgressWidget;
import com.blakebr0.mysticalautomation.container.FarmerContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FarmerScreen extends BaseContainerScreen<FarmerContainer> {
    public static final ResourceLocation BACKGROUND = MysticalAutomation.resource("textures/gui/farmer.png");

    public FarmerScreen(FarmerContainer container, Inventory inv, Component title) {
        super(container, inv, title, BACKGROUND, 176, 194);
    }

    @Override
    protected void init() {
        super.init();

        int x = this.getGuiLeft();
        int y = this.getGuiTop();

        this.addRenderableWidget(new EnergyBarWidget(x + 7, y + 17, this.menu::getEnergyStored, this.menu::getMaxEnergyStored));
        this.addRenderableWidget(new FuelWidget(x + 30, y + 39, this.menu::getFuelItemValue, this.menu::getFuelLeft));
        this.addRenderableWidget(new ProgressArrowWidget(x + 99, y + 52, this.menu::getProgress, this.menu::getOperationTime));
        this.addRenderableWidget(new FarmerStageProgressWidget(x + 93, y + 30, this.menu::getStageProgress, this.menu::getMaxStageProgress, this.menu::getStages));
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        gfx.drawString(this.font, this.title, (this.imageWidth / 2 - this.font.width(this.title) / 2), 6, 4210752, false);
        gfx.drawString(this.font, this.playerInventoryTitle, 8, (this.imageHeight - 96 + 2), 4210752, false);
    }
}
