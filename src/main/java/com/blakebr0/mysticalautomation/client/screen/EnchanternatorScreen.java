package com.blakebr0.mysticalautomation.client.screen;

import com.blakebr0.cucumber.client.screen.BaseContainerScreen;
import com.blakebr0.cucumber.client.screen.widget.EnergyBarWidget;
import com.blakebr0.cucumber.client.screen.widget.FuelWidget;
import com.blakebr0.cucumber.client.screen.widget.ProgressArrowWidget;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.client.screen.widget.EnchanternatorLevelWidget;
import com.blakebr0.mysticalautomation.container.EnchanternatorContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class EnchanternatorScreen extends BaseContainerScreen<EnchanternatorContainer> {
    public static final Identifier BACKGROUND = MysticalAutomation.resource("textures/gui/enchanternator.png");

    public EnchanternatorScreen(EnchanternatorContainer container, Inventory inv, Component title) {
        super(container, inv, title, BACKGROUND, 206, 194);
    }

    @Override
    protected void init() {
        super.init();

        int x = this.getGuiLeft();
        int y = this.getGuiTop();

        this.addRenderableWidget(new EnergyBarWidget(x + 7, y + 17, this.menu::getEnergyStored, this.menu::getMaxEnergyStored));
        this.addRenderableWidget(new FuelWidget(x + 30, y + 39, this.menu::getFuelItemValue, this.menu::getFuelLeft));
        this.addRenderableWidget(new ProgressArrowWidget(x + 143, y + 47, this.menu::getProgress, this.menu::getOperationTime));

        for (int i = 0; i < 5; i++) {
            var level = i + 1;

            this.addRenderableWidget(new EnchanternatorLevelWidget(x + 150 + (i * 10), y + 75, this.menu.getBlockPos(), level, () -> this.menu.getSelectedLevel() == level));
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor gfx, int mouseX, int mouseY) {
        gfx.text(this.font, this.title, (this.imageWidth / 2 - this.font.width(this.title) / 2), 6, 4210752, false);
        gfx.text(this.font, this.playerInventoryTitle, 22, (this.imageHeight - 96 + 2), 4210752, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float a) {
        super.extractBackground(gfx, mouseX, mouseY, a);

        var x = this.getGuiLeft();
        var y = this.getGuiTop();

        this.extractGhostItem(gfx, x + 56, y + 67, this.menu.slots.get(6).getItem());
        this.extractGhostItem(gfx, x + 78, y + 67, this.menu.slots.get(7).getItem());
        this.extractGhostItem(gfx, x + 118, y + 67, this.menu.slots.get(8).getItem());

        this.extractGhostItem(gfx, x + 178, y + 47, this.menu.getResult());
    }
}
