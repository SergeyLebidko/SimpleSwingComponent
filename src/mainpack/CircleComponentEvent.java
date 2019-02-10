package mainpack;

import java.util.EventObject;

public class CircleComponentEvent extends EventObject {

    private int value;

    public CircleComponentEvent(Object source, int value) {
        super(source);
        this.value=value;
    }

    public int getValue() {
        return value;
    }

}
