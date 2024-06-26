package nl.lucashuls.kentekencheck.data.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class KentekenWithMaintenance {

    @Embedded
    public Kenteken kenteken;

    @Relation(
            parentColumn = "kenteken",
            entityColumn = "kenteken"
    )
    public List<Maintenance> maintenanceList;
}
