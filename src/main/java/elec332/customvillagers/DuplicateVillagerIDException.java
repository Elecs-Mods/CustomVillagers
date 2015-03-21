package elec332.customvillagers;

import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;

/**
 * Created by Elec332 on 24-2-2015.
 */
public class DuplicateVillagerIDException extends ReportedException {

    public DuplicateVillagerIDException(int ID){
        super(DuplicateVillagerIDException.makeReport(ID));
    }

    private static CrashReport makeReport(int ID){
        CrashReport report = new CrashReport("Creating Villager, \"Found a duplicate villager ID: " + ID+"\"", new Exception());
        report.makeCategory("Found a duplicate villager ID: " + ID);
        return report;
    }
}
