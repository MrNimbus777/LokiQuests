package net.nimbus.lokiquests.core.dialogs.action;

import java.util.HashMap;
import java.util.Map;

public class Actions {
    public static final Map<String, Action> map = new HashMap<>();
    public static Action get(String id) {
        return map.getOrDefault(id, null);
    }
}
