package data;

import data.model.Club;

import java.io.File;

/**
 * A class which stores the shared state of the application.
 * I prefer to avoid of singletons and use dependency injection for anything beyond simple applications.
 */
public class Context {
    private final static Context context = new Context();
    private Club currentClub = new Club();
    private File clubFile;

    public Club getCurrentClub() {
        return currentClub;
    }

    public void setCurrentClub(Club club) {
        currentClub = club;
    }

    public static Context getContext() {
        return context;
    }

    public boolean getFileExists() {
        return clubFile != null;
    }

    public File getFile() {
        return clubFile;
    }

    public void setFile(File file) {
        clubFile = file;
    }
}
