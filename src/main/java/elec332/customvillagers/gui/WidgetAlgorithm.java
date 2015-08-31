package elec332.customvillagers.gui;

import com.google.common.collect.Lists;
import elec332.core.inventory.widget.WidgetButton;
import elec332.customvillagers.json.Algorithm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.List;

/**
 * Created by Elec332 on 21-8-2015.
 */
public class WidgetAlgorithm extends WidgetButton implements WidgetButton.IButtonEvent{

    public WidgetAlgorithm(int x, int y, int width, int height) {
        super(x, y, 5, 5, width, height);
        addButtonEvent(this);
        this.algorithm = Algorithm.NORMAl;
        this.list = Lists.newArrayList();
    }

    private List<IAlgorithmChangedEvent> list;

    public WidgetAlgorithm addButtonEvent(IAlgorithmChangedEvent event){
        this.list.add(event);
        return this;
    }

    public void setAlgorithm(Algorithm algorithm){
        this.algorithm = algorithm;
        for (IAlgorithmChangedEvent event : list)
            event.onAlgorithmChanged(this.algorithm);
    }

    @Override
    public void onButtonClicked(WidgetButton button) {
        if (algorithm == Algorithm.NORMAl){
            setAlgorithm(Algorithm.BLACKSMITH);
        } else {
            setAlgorithm(Algorithm.NORMAl);
        }
    }

    private Algorithm algorithm;

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public void draw(Gui gui, int guiX, int guiY, int mouseX, int mouseY) {
        //bindTexture(((IResourceLocationProvider)gui).getBackgroundImageLocation());
        //super.draw(gui, guiX, guiY, mouseX, mouseY);
        gui.drawCenteredString(Minecraft.getMinecraft().fontRenderer, algorithm.toString(), guiX + x + 26, guiY + y, 0);
    }

    public interface IAlgorithmChangedEvent{

        public void onAlgorithmChanged(Algorithm newAlgorithm);

    }
}
