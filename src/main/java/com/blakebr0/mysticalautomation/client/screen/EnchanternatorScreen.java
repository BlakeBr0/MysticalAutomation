package com.blakebr0.mysticalautomation.client.screen;

import com.blakebr0.cucumber.client.render.GhostItemRenderer;
import com.blakebr0.cucumber.client.screen.BaseContainerScreen;
import com.blakebr0.cucumber.client.screen.widget.EnergyBarWidget;
import com.blakebr0.cucumber.client.screen.widget.FuelWidget;
import com.blakebr0.cucumber.client.screen.widget.ProgressArrowWidget;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.container.EnchanternatorContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EnchanternatorScreen extends BaseContainerScreen<EnchanternatorContainer> {
    public static final ResourceLocation BACKGROUND = MysticalAutomation.resource("textures/gui/enchanternator.png");

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
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        gfx.drawString(this.font, this.title, (this.imageWidth / 2 - this.font.width(this.title) / 2), 6, 4210752, false);
        gfx.drawString(this.font, this.playerInventoryTitle, 22, (this.imageHeight - 96 + 2), 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(gfx, partialTicks, mouseX, mouseY);

        var x = this.getGuiLeft();
        var y = this.getGuiTop();

        if (this.minecraft == null)
            return;

        GhostItemRenderer.renderItemIntoGui(this.menu.slots.get(6).getItem(), x + 56, y + 67, this.minecraft.getItemRenderer());
        GhostItemRenderer.renderItemIntoGui(this.menu.slots.get(7).getItem(), x + 78, y + 67, this.minecraft.getItemRenderer());
        GhostItemRenderer.renderItemIntoGui(this.menu.slots.get(8).getItem(), x + 118, y + 67, this.minecraft.getItemRenderer());

        GhostItemRenderer.renderItemIntoGui(this.menu.getResult(), x + 178, y + 47, this.minecraft.getItemRenderer());
    }
}
