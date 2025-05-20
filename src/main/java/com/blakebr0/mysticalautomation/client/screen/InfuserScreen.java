package com.blakebr0.mysticalautomation.client.screen;

import com.blakebr0.cucumber.client.screen.BaseContainerScreen;
import com.blakebr0.cucumber.client.screen.widget.EnergyBarWidget;
import com.blakebr0.cucumber.util.Formatting;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.container.InfuserContainer;
import com.blakebr0.mysticalautomation.tilentity.InfuserTileEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class InfuserScreen extends BaseContainerScreen<InfuserContainer> {
    private static final ResourceLocation BACKGROUND = MysticalAutomation.resource("textures/gui/harvester.png");
    private InfuserTileEntity tile;

    public InfuserScreen(InfuserContainer container, Inventory inv, Component title) {
        super(container, inv, title, BACKGROUND, 176, 194);
    }

    @Override
    protected void init() {
        super.init();

        int x = this.getGuiLeft();
        int y = this.getGuiTop();

        this.tile = this.getTileEntity();

        if (this.tile != null) {
            this.addRenderableWidget(new EnergyBarWidget(x + 7, y + 17, this.tile.getEnergy()));
        }
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        var title = this.getTitle().getString();

        gfx.drawString(this.font, title, (this.imageWidth / 2 - this.font.width(title) / 2), 6, 4210752, false);
        gfx.drawString(this.font, this.playerInventoryTitle, 8, (this.imageHeight - 96 + 2), 4210752, false);

        // TODO: "temporary" workaround for dynamic energy storage
        if (this.tile != null) {
            var tier = this.tile.getMachineTier();
            var energy = this.tile.getEnergy();

            energy.resetMaxEnergyStorage();

            if (tier != null) {
                energy.setMaxEnergyStorage(this.tile.getEnergy().getMaxEnergyStored() * tier.getFuelCapacityMultiplier());
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(gfx, partialTicks, mouseX, mouseY);

        int x = this.getGuiLeft();
        int y = this.getGuiTop();

        if (this.getFuelItemValue() > 0) {
            int i = this.getBurnLeftScaled(13);
            gfx.blit(BACKGROUND, x + 31, y + 52 - i, 176, 12 - i, 14, i + 1);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics gfx, int mouseX, int mouseY) {
        int x = this.getGuiLeft();
        int y = this.getGuiTop();

        super.renderTooltip(gfx, mouseX, mouseY);

        if (mouseX > x + 30 && mouseX < x + 45 && mouseY > y + 39 && mouseY < y + 53) {
            gfx.renderTooltip(this.font, Formatting.energy(this.getFuelLeft()), mouseX, mouseY);
        }
    }

    private InfuserTileEntity getTileEntity() {
        var level = this.getMinecraft().level;

        if (level != null) {
            var tile = level.getBlockEntity(this.getMenu().getBlockPos());

            if (tile instanceof InfuserTileEntity machine) {
                return machine;
            }
        }

        return null;
    }

    public int getFuelLeft() {
        if (this.tile == null)
            return 0;

        return this.tile.getFuelLeft();
    }

    public int getFuelItemValue() {
        if (this.tile == null)
            return 0;

        return this.tile.getFuelItemValue();
    }

    public int getBurnLeftScaled(int pixels) {
        int i = this.getFuelLeft();
        int j = this.getFuelItemValue();
        return (int) (j != 0 && i != 0 ? (long) i * pixels / j : 0);
    }
}
