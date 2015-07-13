package elec332.customvillagers.gui;

import elec332.customvillagers.json.VillagerData;
import elec332.customvillagers.CustomVillagers;
import elec332.customvillagers.network.PacketSyncTradeContents;
import elec332.customvillagers.registry.CustomVillagerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by Elec332 on 28-5-2015.
 */
public class ClientGuiVillager extends GuiContainer {
    public ClientGuiVillager(Container container) {
        super(container);
        this.container = (ContainerVillagerGUI) container;
        this.villagerData = CustomVillagerRegistry.instance.getData(this.container.ID);
        shouldAdd = true;
        if (villagerData != null && villagerData.trades.size() > 0){
            setTrade(villagerData.trades.get(0));
            shouldAdd = false;
        }
        index = 0;
    }

    private ContainerVillagerGUI container;
    private ResourceLocation rl = new ResourceLocation("textures/gui/container/villager.png");
    private ArrowButton button1;
    private ArrowButton button2;
    private VillagerData villagerData;
    private int index;
    private boolean shouldAdd ;

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.buttonList.add(new WriteTradeButton(0, this.guiLeft + 33, this.guiTop + 21, "Set"));
        this.buttonList.add(this.button1 = new ArrowButton(1, i + 120 + 27, j + 24 - 1, true));
        this.buttonList.add(this.button2 = new ArrowButton(2, i + 36 - 19, j + 24 - 1, false));
        this.button1.enabled = false;
        this.button2.enabled = false;
    }

    @Override
    protected void actionPerformed(GuiButton nope) {
        boolean stuffChanged = false;
        if (nope instanceof WriteTradeButton) {
            if (shouldAdd) {
                if ((getStackInSlot(0) != null || getStackInSlot(1) != null) && getStackInSlot(2) != null) {
                    villagerData.addTrade(getStackInSlot(0), getStackInSlot(1), getStackInSlot(2));
                    shouldAdd = false;
                }
            } else {
                villagerData.trades.get(index).setInput1(getStackInSlot(0));
                villagerData.trades.get(index).setInput2(getStackInSlot(1));
                villagerData.trades.get(index).setOutput(getStackInSlot(2));
            }
        } else if (nope == button1){
            index++;
            stuffChanged = true;
        } else if (nope == button2){
            index--;
            stuffChanged = true;
        }
        if (stuffChanged && villagerData != null){
            if (index < 0)
                index = 0;
            if (index > villagerData.trades.size())
                index = villagerData.trades.size();
            if (index == villagerData.trades.size()) {
                setTrade(null);
                shouldAdd = true;
            } else {
                setTrade(villagerData.trades.get(index));
                shouldAdd = false;
            }
        }
    }

    public void updateScreen() {
        super.updateScreen();
        if (villagerData != null) {
            this.button1.enabled = this.index < villagerData.trades.size();
            this.button2.enabled = this.index > 0;
        }
    }

    private void setTrade(VillagerData.Trade trade){
        if (trade != null) {
            CustomVillagers.networkHandler.getNetworkWrapper().sendToServer(new PacketSyncTradeContents(trade.getInput1(), trade.getInput2(), trade.getOutput()));
        } else {
            CustomVillagers.networkHandler.getNetworkWrapper().sendToServer(new PacketSyncTradeContents());
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        CustomVillagerRegistry.instance.rewriteFiles();
    }

    private ItemStack getStackInSlot(int i){
        return container.inventory.getStackInSlot(i);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i1, int i2) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(rl);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }

    private class WriteTradeButton extends GuiButton{
        public WriteTradeButton(int p_i1020_1_, int p_i1020_2_, int p_i1020_3_, String p_i1020_4_) {
            super(p_i1020_1_, p_i1020_2_, p_i1020_3_, 106, 20,  p_i1020_4_);
        }
    }

    private class ArrowButton extends GuiButton{

        private final boolean arrow;

        public ArrowButton(int i1, int i2, int i3, boolean arrow) {
            super(i1, i2, i3, 12, 19, "");
            this.arrow = arrow;
        }

        public void drawButton(Minecraft mc, int i1, int i2) {
            if (this.visible) {
                mc.getTextureManager().bindTexture(rl);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                boolean flag = i1 >= this.xPosition && i2 >= this.yPosition && i1 < this.xPosition + this.width && i2 < this.yPosition + this.height;
                int k = 0;
                int l = 176;
                if (!this.enabled) {
                    l += this.width * 2;
                }
                else if (flag) {
                    l += this.width;
                }
                if (!this.arrow) {
                    k += this.height;
                }
                this.drawTexturedModalRect(this.xPosition, this.yPosition, l, k, this.width, this.height);
            }
        }

    }

}