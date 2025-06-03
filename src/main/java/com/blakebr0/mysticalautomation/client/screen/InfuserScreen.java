package com.blakebr0.mysticalautomation.client.screen;

import com.blakebr0.cucumber.client.screen.BaseContainerScreen;
import com.blakebr0.cucumber.client.screen.widget.EnergyBarWidget;
import com.blakebr0.cucumber.client.screen.widget.FuelWidget;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.client.screen.widget.InfuserProgressWidget;
import com.blakebr0.mysticalautomation.client.screen.widget.InfuserSelectedWidget;
import com.blakebr0.mysticalautomation.container.InfuserContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class InfuserScreen extends BaseContainerScreen<InfuserContainer> {
    public static final ResourceLocation BACKGROUND = MysticalAutomation.resource("textures/gui/infuser.png");

    public InfuserScreen(InfuserContainer container, Inventory inv, Component title) {
        super(container, inv, title, BACKGROUND, 217, 194);
    }

    @Override
    protected void init() {
        super.init();

        int x = this.getGuiLeft();
        int y = this.getGuiTop();

        this.addRenderableWidget(new EnergyBarWidget(x + 7, y + 17, this.menu::getEnergyStored, this.menu::getMaxEnergyStored));
        this.addRenderableWidget(new FuelWidget(x + 30, y + 39, this.menu::getFuelItemValue, this.menu::getFuelLeft));

        // the first essence slot is Inferium, so it doesn't need the widgets
        for (var i = 1; i < 6; i++) {
            var index = i;

            this.addRenderableWidget(new InfuserProgressWidget(x + 103 + i * 18, y + 52, this.menu::getProgress, this.menu::getOperationTime, () -> this.menu.getProgressingIndex() == index));
            this.addRenderableWidget(new InfuserSelectedWidget(x + 107 + i * 18, y + 57, this.menu.getBlockPos(), i, () -> this.menu.getSelectedIndex() == index));
        }
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        var title = this.getTitle().getString();

        gfx.drawString(this.font, title, (this.imageWidth / 2 - this.font.width(title) / 2), 6, 4210752, false);
        gfx.drawString(this.font, this.playerInventoryTitle, 28, (this.imageHeight - 96 + 2), 4210752, false);
    }
}
