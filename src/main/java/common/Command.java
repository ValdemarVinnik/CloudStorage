package common;

public enum Command {
    START("#start"),
    END("#end"),
    SEND("#file"),
    DOWNLOAD_REQUEST("#download"),
    DAWN("#dawn"),
    UP("#up"),
    DIR_CONTENT("#dirContent"),
    LOCATION("#location");

    private final String command;

    Command(String command) {
        this.command = command;
    }

    public String getCommand(){
        return command;
    }
}
