package commands;

import java.util.List;

public abstract class Command {
    private final String name;
    private List<String> arguments;

    public Command(String name)
    {
        this.name = name;
    }

    public abstract void run();

    public void setArguments(List<String> arguments)
    {
        if(!(arguments == null)) {
            this.arguments = arguments;
        }
    }

    public String getName() {
        return this.name;
    }

    public List<String> getArguments() {
        return this.arguments;
    }
}
