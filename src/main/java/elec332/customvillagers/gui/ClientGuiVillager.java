package elec332.customvillagers.gui;

import elec332.core.client.inventory.BaseGuiContainer;
import elec332.core.inventory.BaseContainer;
import elec332.core.inventory.widget.WidgetButton;
import elec332.customvillagers.CustomVillagers;
import elec332.customvillagers.json.Algorithm;
import elec332.customvillagers.json.RandomisationData;
import elec332.customvillagers.json.VillagerData;
import elec332.customvillagers.json.WrappedStack;
import elec332.customvillagers.network.PacketSyncTradeContents;
import elec332.customvillagers.registry.CustomVillagerRegistry;
import elec332.customvillagers.util.CorrectedTuple;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 28-5-2015.
 */
public class ClientGuiVillager extends BaseGuiContainer implements WidgetButton.IButtonEvent{
    public ClientGuiVillager(BaseContainer container) {
        super(container);
        ySize = 250;
        this.container = (ContainerVillagerGUI) container;
        ((ContainerVillagerGUI) container).right.addButtonEvent(this);
        ((ContainerVillagerGUI) container).left.addButtonEvent(this);
        ((ContainerVillagerGUI) container).setButton.addButtonEvent(this);
        ((ContainerVillagerGUI) container).adjustProbability.addButtonEvent(this);
        this.villagerData = CustomVillagerRegistry.instance.getData(this.container.ID);
        shouldAdd = true;
        if (villagerData != null && villagerData.trades.size() > 0){
            setTrade(villagerData.trades.get(0));
            shouldAdd = false;
        }
        index = 0;
        adjustProbability = false;
    }

    private ContainerVillagerGUI container;
    private ResourceLocation rl = new ResourceLocation("customvillagers", "villager.png");
    private VillagerData villagerData;
    private int index;
    private boolean shouldAdd ;

    private boolean adjustProbability;

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        container.set1.draw(this, guiLeft, guiTop);
        container.set2.draw(this, guiLeft, guiTop);
        container.set3.draw(this, guiLeft, guiTop);
        drawCenteredString(Minecraft.getMinecraft().fontRenderer, ""+container.getProbability(), guiLeft+148 , guiTop+55, 0);
    }

    public void updateScreen() {
        super.updateScreen();
        if (villagerData != null) {
            container.left.setActive(this.index > 0);
            container.right.setActive(this.index < villagerData.trades.size());
        }
    }

    private void setTrade(VillagerData.Trade trade){
        if (trade != null) {
            container.toZero();
            CustomVillagers.networkHandler.getNetworkWrapper().sendToServer(new PacketSyncTradeContents(trade));
            Algorithm algorithm = null;
            if (trade.input1 != null && trade.input1.amount != null){
                algorithm = trade.input1.amount.algorithm;
            } else if (trade.input2 != null && trade.input2.amount != null){
                algorithm = trade.input2.amount.algorithm;
            } else if (trade.output != null && trade.output.amount != null){
                algorithm = trade.output.amount.algorithm;
            }
            if (trade.input1 != null) {
                if (trade.input1.amount != null && algorithm != Algorithm.BLACKSMITH) {
                    container.set1.max = trade.input1.amount.max;
                    container.set1.min = trade.input1.amount.min;
                } else {
                    container.set1.max = 1;
                    container.set1.min = 1;
                }
            }
            if (trade.input2 != null) {
                if (trade.input2.amount != null && algorithm != Algorithm.BLACKSMITH) {
                    container.set2.max = trade.input2.amount.max;
                    container.set2.min = trade.input2.amount.min;
                } else {
                    container.set2.max = 1;
                    container.set2.min = 1;
                }
            }
            if (trade.output != null) {
                if (trade.output.amount != null && algorithm != Algorithm.BLACKSMITH) {
                    container.set3.max = trade.output.amount.max;
                    container.set3.min = trade.output.amount.min;
                } else {
                    container.set3.max = 1;
                    container.set3.min = 1;
                }
            }
            if (algorithm == Algorithm.BLACKSMITH ){
                if (trade.output != null && trade.output.amount != null) {
                    container.set2.max = trade.output.amount.max;
                    container.set2.min = trade.output.amount.min;
                } else {
                    throw new RuntimeException();
                }
            }
            if (algorithm != null) {
                container.type.setAlgorithm(algorithm);
            } else {
                container.type.setAlgorithm(Algorithm.NORMAl);
            }
            container.specialType.setEnum(trade.specialType);
            adjustProbability = trade.adjustProbability;
            container.adjustProbability.setDisplayString(""+adjustProbability);
            container.setProbability(trade.probability);
        } else {
            CustomVillagers.networkHandler.getNetworkWrapper().sendToServer(new PacketSyncTradeContents());
            container.toZero();
            adjustProbability = false;
            container.setProbability(1.0f);
            container.adjustProbability.setDisplayString("false");
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
    public ResourceLocation getBackgroundImageLocation() {
        return rl;
    }

    @Override
    public void onButtonClicked(WidgetButton button) {
        if (button == container.setButton){
            if (shouldAdd) {
                if (specialTypeValid(container.specialType.getEnum(), getStackInSlot(0), getStackInSlot(1), getStackInSlot(2))){
                    WrappedStack s1 = null;
                    WrappedStack s2 = null;
                    WrappedStack o;
                    if (getStackInSlot(0) != null){
                        s1 = new WrappedStack(getStackInSlot(0)).setAmount(new RandomisationData(CorrectedTuple.newTuple(container.set1.min, container.set1.max), container.type.getAlgorithm()));
                    }
                    if (getStackInSlot(1) != null){
                        s2 = new WrappedStack(getStackInSlot(1)).setAmount(new RandomisationData(CorrectedTuple.newTuple(container.set2.min, container.set2.max), container.type.getAlgorithm()));
                    }
                    o = new WrappedStack(getStackInSlot(2)).setAmount(new RandomisationData(CorrectedTuple.newTuple(container.set3.min, container.set3.max), container.type.getAlgorithm()));
                    VillagerData.Trade trade = villagerData.newTrade(s1, s2, o);
                    trade.specialType = container.specialType.getEnum();
                    trade.adjustProbability = adjustProbability;
                    trade.probability = container.getProbability();
                    villagerData.addTrade(trade);
                    shouldAdd = false;
                }
            } else {
                if ((getStackInSlot(2) != null && (getStackInSlot(0) != null || getStackInSlot(1) != null) && container.type.getAlgorithm() == Algorithm.NORMAl) || (getStackInSlot(0) != null && getStackInSlot(1) == null && getStackInSlot(2) != null && container.type.getAlgorithm() == Algorithm.BLACKSMITH)){
                    if (getStackInSlot(0) != null){
                        villagerData.trades.get(index).setInput1(new WrappedStack(getStackInSlot(0)).setAmount(getData(container.set1)));
                    }
                    if (getStackInSlot(1) != null){
                        villagerData.trades.get(index).setInput2(new WrappedStack(getStackInSlot(1)).setAmount(getData(container.set2)));
                    }
                    villagerData.trades.get(index).setOutput(new WrappedStack(getStackInSlot(2)).setAmount(getData(container.set3)));
                    villagerData.trades.get(index).specialType = container.specialType.getEnum();
                    villagerData.trades.get(index).adjustProbability = adjustProbability;
                    villagerData.trades.get(index).probability = container.getProbability();
                }
            }
        } if (button == container.adjustProbability){
            adjustProbability = !adjustProbability;
            container.adjustProbability.setDisplayString(""+adjustProbability);
        } else {
            boolean stuffChanged = false;
            if (button == container.right) {
                index++;
                stuffChanged = true;
            } else if (button == container.left) {
                index--;
                stuffChanged = true;
            }
            if (stuffChanged && villagerData != null) {
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
    }

    private boolean specialTypeValid(VillagerData.SpecialType type, ItemStack s1, ItemStack s2, ItemStack o){
        switch (type){
            case NORMAL:
                return (o != null && (s1 != null || s2 != null) && container.type.getAlgorithm() == Algorithm.NORMAl) || (s1 != null && s2 == null && o != null && container.type.getAlgorithm() == Algorithm.BLACKSMITH);
            case RANDOM_ENCHANTED_BOOK:
                return s1 != null && s2 == null && o == null;
            case ADD_RANDOM_ENCHANTMENT:
                return s1 != null && s2 != null && o == null;
            /*case RANDOM_BLACKSMITH_WEAPONRY:
                return false;*/
            default:
                return false;
        }
    }

    private RandomisationData getData(ContainerVillagerGUI.SlotSet set){
        if (set.min == 1 && set.max == 1)
            return null;
        return new RandomisationData(CorrectedTuple.newTuple(set.min, set.max), container.type.getAlgorithm());
    }

}