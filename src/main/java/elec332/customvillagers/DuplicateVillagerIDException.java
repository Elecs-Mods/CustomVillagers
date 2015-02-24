package elec332.customvillagers;

/**
 * Created by Elec332 on 24-2-2015.
 */
public class DuplicateVillagerIDException extends RuntimeException {

    private static final long serialVersionUID = 1786489398573894798L;
    public int ID;
    StackTraceElement[] STE;

    public DuplicateVillagerIDException(int ID){
        this.ID = ID;
        this.STE = new StackTraceElement[1];
        STE[0] = new StackTraceElement("Elec332", "CustomVillagers", "RegisterVillagers", this.ID);
        this.setStackTrace(STE);
    }
}
