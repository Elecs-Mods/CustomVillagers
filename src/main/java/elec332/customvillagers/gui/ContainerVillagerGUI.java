package elec332.customvillagers.gui;

import elec332.core.inventory.BaseContainer;
import elec332.core.inventory.widget.WidgetButton;
import elec332.core.inventory.widget.WidgetButtonArrow;
import elec332.core.inventory.widget.WidgetEnumChange;
import elec332.core.util.BasicInventory;
import elec332.customvillagers.CustomVillagers;
import elec332.customvillagers.json.Algorithm;
import elec332.customvillagers.json.VillagerData;
import elec332.customvillagers.network.PacketSlotStuff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.text.DecimalFormat;

/**
 * Created by Elec332 on 28-5-2015.
 */
public class ContainerVillagerGUI extends BaseContainer implements WidgetAlgorithm.IAlgorithmChangedEvent, WidgetEnumChange.IEnumChangedEvent<WidgetEnumChange<VillagerData.SpecialType>> {

    public ContainerVillagerGUI(int ID, EntityPlayer player){
        super(player, 85);
        this.ID = ID;
        this.inventory = new BasicInventory("RecipeMaker", 3){

            @Override
            public boolean isItemValidForSlot(int id, ItemStack stack) {
                return getSlot(id).isItemValid(stack);
            }

            @Override
            public int getInventoryStackLimit() {
                return 1;
            }

        };
        this.player = player;
        this.set1 = new SlotSet(this, 0, 25, 0);
        this.set2 = new SlotSet(this, 1, 25 + 18 * 2, 0);
        this.set3 = new SlotSet(this, 2, 25 + 18 * 5, 0);
        this.addWidget(left = new WidgetButtonArrow(11, 108, WidgetButtonArrow.Direction.LEFT));
        this.addWidget(right = new WidgetButtonArrow(146, 108, WidgetButtonArrow.Direction.RIGHT));
        this.addWidget(type = new WidgetAlgorithm(25, 12, 40, 9).addButtonEvent(set1).addButtonEvent(set2).addButtonEvent(set3).addButtonEvent(this));
        this.addWidget(setButton = new WidgetButton(94, 10, 0, 0, 40, 20).setDisplayString("Set"));
        this.addWidget(specialType = new WidgetEnumChange<VillagerData.SpecialType>(12, 32, 122, 20, VillagerData.SpecialType.class).addButtonEvent(this));
        this.addWidget(adjustProbability = new WidgetButton(136, 10, 0, 0, 30, 20));
        this.addWidget(probUp = new WidgetButtonArrow(142, 40, WidgetButtonArrow.Direction.UP).addButtonEvent(new WidgetButton.IButtonEvent() {
            @Override
            public void onButtonClicked(WidgetButton button) {
                prob += 0.10000000000f;
                prob = Float.parseFloat(new DecimalFormat("#.#").format(prob).replace(",", ".")); //Inaccurate floats.... -_-
                if (prob >= 1.0f) {
                    prob = 1.0f;
                    probUp.setActive(false);
                }
                if (prob > 0.0f && !probDown.isActive()){
                    probDown.setActive(true);
                }
            }
        }));
        this.addWidget(probDown = new WidgetButtonArrow(142, 65, WidgetButtonArrow.Direction.DOWN).addButtonEvent(new WidgetButton.IButtonEvent() {
            @Override
            public void onButtonClicked(WidgetButton button) {
                prob -= 0.10000000000f;
                prob = Float.parseFloat(new DecimalFormat("#.#").format(prob).replace(",", ".")); //Inaccurate floats.... -_-
                if (prob <= 0.0f) {
                    prob = 0.0f;
                    probDown.setActive(false);
                }
                if (prob < 1.0f && !probUp.isActive()){
                    probUp.setActive(true);
                }
            }
        }));
        addPlayerInventoryToContainer();
    }

    protected EntityPlayer player;
    protected WidgetButtonArrow left;
    protected WidgetButtonArrow right;
    protected WidgetAlgorithm type;
    protected WidgetButton setButton;
    protected WidgetEnumChange<VillagerData.SpecialType> specialType;
    protected WidgetButton adjustProbability;
    private WidgetButtonArrow probUp;
    private WidgetButtonArrow probDown;

    private float prob;

    protected SlotSet set1;
    protected SlotSet set2;
    protected SlotSet set3;



    public void slot(int i, boolean b){
        switch (i){
            case 1:
                set1.slot = b;
                if (player.worldObj.isRemote)
                    sendSlotPacket(i, b);
                return;
            case 2:
                set2.slot = b;
                if (player.worldObj.isRemote)
                    sendSlotPacket(i, b);
                return;
            case 3:
                set3.slot = b;
                if (player.worldObj.isRemote)
                    sendSlotPacket(i, b);
        }
    }

    private void sendSlotPacket(int i, boolean b){
        CustomVillagers.networkHandler.getNetworkWrapper().sendToServer(new PacketSlotStuff(i, b));
    }

    @Override
    public ItemStack slotClick(int slotID, int i1, int i2, EntityPlayer player) {
        ItemStack ret;
        if (slotID >= 0 && slotID < 3){
            ret = getSlot(slotID).getStack();
            if (getSlot(slotID).isItemValid(player.inventory.getItemStack()))
                getSlot(slotID).putStack(copyItemStack(player.inventory.getItemStack()));
        } else return super.slotClick(slotID, i1, i2, player);
        return ret;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        if (slotID >= 0 && slotID < 3)
            getSlot(slotID).putStack(null);
        return null;  //No fancy stuff needed
    }

    @Override
    protected void retrySlotClick(int p_75133_1_, int p_75133_2_, boolean p_75133_3_, EntityPlayer p_75133_4_) {
        //NOPE
    }

    public void setProbability(float prob){
        if (prob >= 1.0f) {
            prob = 1.0f;
            probUp.setActive(false);
        }
        if (prob <= 0.0f) {
            prob = 0.0f;
            probDown.setActive(false);
        }
        this.prob = prob;
    }

    public float getProbability(){
        return this.prob;
    }

    private ItemStack copyItemStack(ItemStack stack){
        if (stack == null)
            return null;
        ItemStack stack1 = stack.copy();
        stack1.stackSize = stack.stackSize;
        return stack1;
    }

    public BasicInventory inventory;
    protected int ID;

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return true;
    }

    protected void toZero(){
        set1.toZero();
        set2.toZero();
        set3.toZero();
        specialType.setEnum(VillagerData.SpecialType.NORMAL);
    }

    @Override
    public void onAlgorithmChanged(Algorithm newAlgorithm) {
        switch (newAlgorithm){
            case NORMAl:
                slot(2, true);
                specialType.setActive(true);
                return;
            case BLACKSMITH:
                slot(2, false);
                specialType.setActive(false);
                specialType.setEnum(VillagerData.SpecialType.NORMAL);
        }
    }

    @Override
    public void onEnumChanged(WidgetEnumChange<VillagerData.SpecialType> widget) {
        switch (widget.getEnum()){
            case NORMAL:
                slot(1, true);
                slot(2, true);
                slot(3, true);
                activateButtons(true);
                type.setActive(true);
                return;
            case RANDOM_ENCHANTED_BOOK:
                slot(1, true);
                slot(2, false);
                slot(3, false);
                activateButtons(false);
                type.setActive(false);
                return;
            case ADD_RANDOM_ENCHANTMENT:
                slot(1, true);
                slot(2, true);
                slot(3, false);
                activateButtons(true);
                type.setActive(false);
                return;
            case RANDOM_BLACKSMITH_WEAPONRY:
                slot(1, false);
                slot(2, false);
                slot(3, false);
                activateButtons(true);
                type.setActive(false);
        }
    }

    private void activateButtons(boolean b){
        set1.minUp.setActive(b);
        set1.minDown.setActive(b);
        set1.maxUp.setActive(b);
        set1.maxDown.setActive(b);
        set2.minUp.setActive(b);
        set2.minDown.setActive(b);
        set2.maxUp.setActive(b);
        set2.maxDown.setActive(b);
        set3.minUp.setActive(b);
        set3.minDown.setActive(b);
        set3.maxUp.setActive(b);
        set3.maxDown.setActive(b);
        if (b){
            set1.onAlgorithmChanged(type.getAlgorithm());
            set2.onAlgorithmChanged(type.getAlgorithm());
            set3.onAlgorithmChanged(type.getAlgorithm());
        }
    }

    public class SlotSet implements WidgetAlgorithm.IAlgorithmChangedEvent{

        public SlotSet(ContainerVillagerGUI container, final int slotID, int x, int y){
            container.addSlotToContainer(new Slot(container.inventory, slotID, x + 1, 108){

                @Override
                public boolean isItemValid(ItemStack stack) {
                    return slot;
                }

                @Override
                public int getSlotStackLimit() {
                    return 1;
                }

            });
            container.addWidget(minUp = new WidgetButtonArrow(x, 126, WidgetButtonArrow.Direction.UP).addButtonEvent(new WidgetButton.IButtonEvent() {
                @Override
                public void onButtonClicked(WidgetButton button) {
                    min++;
                    if (min > -64 || min > 0 && type.getAlgorithm() == Algorithm.NORMAl) {
                        minDown.setActive(true);
                    }
                    if (min >= 64) {
                        min = 64;
                        minUp.setActive(false);
                    }
                }
            }));
            container.addWidget(minDown = new WidgetButtonArrow(x, 151, WidgetButtonArrow.Direction.DOWN).addButtonEvent(new WidgetButton.IButtonEvent() {
                @Override
                public void onButtonClicked(WidgetButton button) {
                    min--;
                    if (min < 64) {
                        minUp.setActive(true);
                    }
                    if (min <= 0 && type.getAlgorithm() != Algorithm.BLACKSMITH) {
                        min = 0;
                        minDown.setActive(false);
                    } else if (min <= -64){
                        min = -64;
                        minDown.setActive(false);
                    }
                }
            }));
            container.addWidget(maxUp = new WidgetButtonArrow(x, 66, WidgetButtonArrow.Direction.UP).addButtonEvent(new WidgetButton.IButtonEvent() {
                @Override
                public void onButtonClicked(WidgetButton button) {
                    max++;
                    if (max > -64 || max > 0 && type.getAlgorithm() == Algorithm.NORMAl) {
                        maxDown.setActive(true);
                    }
                    if (max >= 64) {
                        max = 64;
                        maxUp.setActive(false);
                    }
                }
            }));
            container.addWidget(maxDown = new WidgetButtonArrow(x, 91, WidgetButtonArrow.Direction.DOWN).addButtonEvent(new WidgetButton.IButtonEvent() {
                @Override
                public void onButtonClicked(WidgetButton button) {
                    max--;
                    if (max < 64) {
                        maxUp.setActive(true);
                    }
                    if (max <= 0 && type.getAlgorithm() != Algorithm.BLACKSMITH) {
                        max = 0;
                        maxDown.setActive(false);
                    } else if (max <= -64){
                        max = -64;
                        maxDown.setActive(false);
                    }
                }
            }));
            this.x = x;
            this.min = 1;
            this.max = 1;
            this.slot = true;
        }

        private final int x;

        private WidgetButtonArrow minUp;
        public int min;
        private WidgetButtonArrow minDown;

        private boolean slot;

        private WidgetButtonArrow maxUp;
        public int max;
        private WidgetButtonArrow maxDown;

        public void draw(Gui gui, int guiLeft, int guiTop){
            gui.drawCenteredString(Minecraft.getMinecraft().fontRenderer, ""+min, guiLeft+x+10 , guiTop+141, 0);
            gui.drawCenteredString(Minecraft.getMinecraft().fontRenderer, ""+max, guiLeft+x+10 , guiTop+81, 0);
        }

        public void toZero(){
            minUp.setActive(true);
            min = 0;
            minDown.setActive(false);
            maxUp.setActive(true);
            max = 0;
            maxDown.setActive(false);
        }

        @Override
        public void onAlgorithmChanged(Algorithm newAlgorithm) {
            if (min >= 64){
                min = 64;
                minUp.setActive(false);
            } else {
                minUp.setActive(true);
            }
            if (max >= 64){
                max = 64;
                maxUp.setActive(false);
            } else {
                maxUp.setActive(true);
            }
            switch (newAlgorithm){
                case NORMAl:
                    if (min <= 0){
                        min = 0;
                        minDown.setActive(false);
                    } else {
                        minDown.setActive(true);
                    }

                    if (max <= 0){
                        max = 0;
                        maxDown.setActive(false);
                    } else {
                        maxDown.setActive(true);
                    }
                    return;
                case BLACKSMITH:
                    minDown.setActive(true);
                    maxDown.setActive(true);
                    if (!this.equals(set2)){
                        minUp.setActive(false);
                        minDown.setActive(false);
                        maxUp.setActive(false);
                        maxDown.setActive(false);
                    }
            }
        }
    }
}
