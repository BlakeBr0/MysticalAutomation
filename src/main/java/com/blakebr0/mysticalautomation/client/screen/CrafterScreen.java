package com.blakebr0.mysticalautomation.client.screen;

import com.blakebr0.cucumber.client.screen.BaseContainerScreen;
import com.blakebr0.cucumber.client.screen.widget.EnergyBarWidget;
import com.blakebr0.cucumber.client.screen.widget.FuelWidget;
import com.blakebr0.cucumber.client.screen.widget.ProgressArrowWidget;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.container.CrafterContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class CrafterScreen extends BaseContainerScreen<CrafterContainer> {
    public static final Identifier BACKGROUND = MysticalAutomation.resource("textures/gui/crafter.png");

    public CrafterScreen(CrafterContainer container, Inventory inv, Component title) {
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
    protected void extractLabels(GuiGraphicsExtractor gfx, int mouseX, int mouseY) {
        gfx.text(this.font, this.title, (this.imageWidth / 2 - this.font.width(this.title) / 2), 6, 4210752, false);
        gfx.text(this.font, this.playerInventoryTitle, 8, (this.imageHeight - 96 + 2), 4210752, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float a) {
        super.extractBackground(gfx, mouseX, mouseY, a);

        var x = this.getGuiLeft();
        var y = this.getGuiTop();

        for (int i = 0; i < 9; i++) {
            var stack = this.menu.slots.get(i + 12).getItem(); // recipe slots start at index 11
            this.extractGhostItem(gfx, x + 8 + (i * 18), y + 101, stack);
        }

        this.extractGhostItem(gfx, x + 148, y + 48, this.menu.getResult());
    }
}
