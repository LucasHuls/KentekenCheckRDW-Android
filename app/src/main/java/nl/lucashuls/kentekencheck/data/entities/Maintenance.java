package nl.lucashuls.kentekencheck.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "maintenance_table",
        foreignKeys = @ForeignKey(entity = Kenteken.class,
                parentColumns = "kenteken",
                childColumns = "kenteken",
                onDelete = ForeignKey.CASCADE))
public class Maintenance {

    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public String kenteken;
    public String description;
    public String date;

    public Maintenance(@NonNull String kenteken, String description, String date) {
        this.kenteken = kenteken;
        this.description = description;
        this.date = date;
    }
}
