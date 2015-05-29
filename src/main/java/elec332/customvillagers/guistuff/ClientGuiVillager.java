package elec332.customvillagers.guistuff;

import elec332.core.minetweaker.MineTweakerHelper;
import elec332.core.player.PlayerHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Elec332 on 28-5-2015.
 */
public class ClientGuiVillager extends GuiContainer {
    public ClientGuiVillager(Container container) {
        super(container);
        this.container = (ContainerVillagerGUI) container;
    }

    private ContainerVillagerGUI container;

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();
        buttonList.add(new VillagerButton(1, this.guiLeft + 33, this.guiTop + 21, "Add recipe to file"));
    }

    @Override
    protected void actionPerformed(GuiButton nope) {
        if (container.inventory.getStackInSlot(1) == null) {
            PlayerHelper.sendMessageToPlayer(container.player, "ItemStack in second 'inputslot' cannot be null, ItemStack in the first slot can!");
            return;
        }
        try {
            writeRecipe();
            MineTweakerHelper.reloadMineTweakerScripts();
            PlayerHelper.sendMessageToPlayer(container.player, "Please restart MineCraft to reload the changes.");
        } catch (IOException e){
            //Tough luck
            PlayerHelper.sendMessageToPlayer(container.player, "Error writing recipe to file...");
        }
    }

    private void writeRecipe() throws IOException{
        File file = MineTweakerHelper.getMTFile("Villager_"+container.ID, "///", "elec332.CustomVillagers.registerVillager("+container.ID+");", "///");
        List<String> stringList = FileUtils.readLines(file);
        stringList.add(MineTweakerHelper.newStringBuilder().append("elec332.CustomVillagers.addTrade(")
                        .append(container.ID).append(", ")
                        .append(itemStackString(0)).append(", ")
                        .append(itemStackString(1)).append(", ")
                        .append(itemStackString(2))
                        .append(");").toString()
        );
        FileUtils.writeLines(file, stringList);
    }

    private String itemStackString(int ID){
        return MineTweakerHelper.getItemStack(container.inventory.getStackInSlot(ID), true);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i1, int i2) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/villager.png"));
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }

    private class VillagerButton extends GuiButton{

        public VillagerButton(int p_i1020_1_, int p_i1020_2_, int p_i1020_3_, String p_i1020_4_) {
            super(p_i1020_1_, p_i1020_2_, p_i1020_3_, 106, 20,  p_i1020_4_);
        }


    }
}
