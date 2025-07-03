package com.blakebr0.mysticalautomation.client.screen;

import com.blakebr0.cucumber.client.render.GhostItemRenderer;
import com.blakebr0.cucumber.client.screen.BaseContainerScreen;
import com.blakebr0.cucumber.client.screen.widget.EnergyBarWidget;
import com.blakebr0.cucumber.client.screen.widget.FuelWidget;
import com.blakebr0.cucumber.client.screen.widget.ProgressArrowWidget;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.container.AwakeningAltarnatorContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AwakeningAltarnatorScreen extends BaseContainerScreen<AwakeningAltarnatorContainer> {
    public static final ResourceLocation BACKGROUND = MysticalAutomation.resource("textures/gui/infusion_altarnator.png");

    public AwakeningAltarnatorScreen(AwakeningAltarnatorContainer container, Inventory inv, Component title) {
        super(container, inv, title, BACKGROUND, 196, 217);
    }

    @Override
    protected void init() {
        super.init();

        int x = this.getGuiLeft();
        int y = this.getGuiTop();

        this.addRenderableWidget(new EnergyBarWidget(x + 7, y + 17, this.menu::getEnergyStored, this.menu::getMaxEnergyStored));
        this.addRenderableWidget(new FuelWidget(x + 30, y + 39, this.menu::getFuelItemValue, this.menu::getFuelLeft));
        this.addRenderableWidget(new ProgressArrowWidget(x + 133, y + 49, this.menu::getProgress, this.menu::getOperationTime));
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        gfx.drawString(this.font, this.title, (this.imageWidth / 2 - this.font.width(this.title) / 2), 6, 4210752, false);
        gfx.drawString(this.font, this.playerInventoryTitle, 17, (this.imageHeight - 96 + 2), 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(gfx, partialTicks, mouseX, mouseY);

        var x = this.getGuiLeft();
        var y = this.getGuiTop();

        if (this.minecraft == null)
            return;

        for (int i = 0; i < 9; i++) {
            var stack = this.menu.slots.get(i + 12).getItem(); // recipe slots start at index 11
            GhostItemRenderer.renderItemIntoGui(stack, x + 18 + (i * 18), y + 101, this.minecraft.getItemRenderer());
        }

        GhostItemRenderer.renderItemIntoGui(this.menu.getResult(), x + 168, y + 49, this.minecraft.getItemRenderer());
    }
}
