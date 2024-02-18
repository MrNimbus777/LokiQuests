package net.nimbus.lokiquests.core.dialogs.action;

import java.util.HashMap;
import java.util.Map;

public class Actions {
    private static final Map<String, Action> map = new HashMap<>();
    public static Action get(String id) {
        return map.getOrDefault(id.toLowerCase(), null);
    }

    public static void register(String id, Action action){
        map.put(id.toLowerCase(), action);
    }

    public static void clearRAM(){
        map.clear();
    }
}
