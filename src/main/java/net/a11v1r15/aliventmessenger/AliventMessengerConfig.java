package net.a11v1r15.aliventmessenger;

import eu.midnightdust.lib.config.MidnightConfig;

public class AliventMessengerConfig extends MidnightConfig {
    @Entry(category = "text") //Gives aliven't messages for all mobs, instead of just ones with custom names
    public static boolean allMobMessages = false;
    
    @Entry(category = "text") //Gives aliven't messages for all mobs killed by a Player
    public static boolean playerKillMessages = false;

    @Entry(category = "text") //Gives aliven't messages for all Villagers and Zombie Villagers with trades
    public static boolean villagerMessages = true;

    @Entry(category = "text") //Gives lore info for items dropped from named mobs
    public static boolean loreDrops = true;

    @Entry(category = "text") //The names of the mobs will appear with colour
    public static boolean nameColour = false;

    @Entry(category = "text") //The aliven't message for named mobs will display their "species" between parenthesis
    public static boolean speciesName = false;

    @Entry(category = "text") //Removes the near-retundant log message of aliven't villagers and custom-named mobs
    public static boolean aliventMessageServerSpamRemover = true;
}
