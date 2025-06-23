package com.blakebr0.mysticalautomation.client.screen;

import com.blakebr0.cucumber.client.screen.BaseContainerScreen;
import com.blakebr0.cucumber.client.screen.widget.EnergyBarWidget;
import com.blakebr0.cucumber.client.screen.widget.FuelWidget;
import com.blakebr0.cucumber.client.screen.widget.ProgressArrowWidget;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.container.AwakeningAltarnatorContainer;
import com.blakebr0.mysticalautomation.container.EnchanternatorContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AwakeningAltarnatorScreen extends BaseContainerScreen<AwakeningAltarnatorContainer> {
    public static final ResourceLocation BACKGROUND = MysticalAutomation.resource("textures/gui/awakening_altarnator.png");

    public AwakeningAltarnatorScreen(AwakeningAltarnatorContainer container, Inventory inv, Component title) {
        super(container, inv, title, BACKGROUND, 176, 217);
    }

    @Override
    protected void init() {
        super.init();

        int x = this.getGuiLeft();
        int y = this.getGuiTop();

        this.addRenderableWidget(new EnergyBarWidget(x + 7, y + 17, this.menu::getEnergyStored, this.menu::getMaxEnergyStored));
        this.addRenderableWidget(new FuelWidget(x + 30, y + 39, this.menu::getFuelItemValue, this.menu::getFuelLeft));
        this.addRenderableWidget(new ProgressArrowWidget(x + 114, y + 48, this.menu::getProgress, this.menu::getOperationTime));
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        var title = this.getTitle().getString();

        gfx.drawString(this.font, title, (this.imageWidth / 2 - this.font.width(title) / 2), 6, 4210752, false);
        gfx.drawString(this.font, this.playerInventoryTitle, 8, (this.imageHeight - 96 + 2), 4210752, false);
    }
}
