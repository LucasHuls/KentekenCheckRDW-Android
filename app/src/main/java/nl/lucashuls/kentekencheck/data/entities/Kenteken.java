package nl.lucashuls.kentekencheck.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "kenteken_table")
public class Kenteken {

    @PrimaryKey
    @NonNull
    public String kenteken;
    public String merk;
    public String model;
    public String logoUrl;
    public String tag;
    public String AanschafDate;
    public boolean alertsEnabled;
    public String vervaldatum_apk_dt;

    public Kenteken(@NonNull String kenteken, String merk, String model, String logoUrl) {
        this.kenteken = kenteken;
        this.merk = merk;
        this.model = model;
        this.logoUrl = logoUrl;
        this.tag = "";
        this.AanschafDate = String.valueOf(new Date());
        this.alertsEnabled = false;
        this.vervaldatum_apk_dt = "";
    }

}
