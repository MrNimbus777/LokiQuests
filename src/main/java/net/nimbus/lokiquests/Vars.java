package net.nimbus.lokiquests;

public class Vars {
    public static String PREFIX;
    public static void init(){
        PREFIX = Utils.toColor(LQuests.a.getConfig().getString("Settings.prefix"));
    }
}
