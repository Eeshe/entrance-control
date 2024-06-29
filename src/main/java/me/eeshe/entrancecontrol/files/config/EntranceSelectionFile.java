package me.eeshe.entrancecontrol.files.config;

import me.eeshe.entrancecontrol.EntranceControl;
import me.eeshe.entrancecontrol.models.EntranceSelection;
import me.eeshe.penpenlib.files.StorageDataFile;

import java.io.File;

public class EntranceSelectionFile extends StorageDataFile {

    public EntranceSelectionFile(EntranceSelection entranceSelection) {
        super(EntranceControl.getInstance().getDataFolder() + "/entrance_selections/" + entranceSelection.getUuid() + ".yml");
    }

    public EntranceSelectionFile(File file) {
        super(file);
    }
}
