package net.nimbus.lokiquests;

import net.nimbus.api.modules.gui.core.GUI;
import net.nimbus.api.modules.gui.core.GUIs;
import net.nimbus.api.modules.gui.core.guiobject.GUIButton;
import net.nimbus.api.modules.gui.core.guiobject.GUIObject;
import net.nimbus.api.modules.gui.core.itembuilder.ItemBuilder;
import net.nimbus.lokiquests.core.dailyquest.DailyQuest;
import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
import net.nimbus.lokiquests.core.quest.Quest;
import net.nimbus.lokiquests.core.quest.quests.DungeonQuest;
import net.nimbus.lokiquests.core.quest.quests.ItemCollectQuest;
import net.nimbus.lokiquests.core.quest.quests.LocationQuest;
import net.nimbus.lokiquests.core.quest.quests.MobKillQuest;
import net.nimbus.lokiquests.core.questplayers.QuestPlayer;
import net.nimbus.lokiquests.core.questplayers.QuestPlayers;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LQGuis {
    public static void load(){
        loadDungeonSelectingGui();
        loadSpawnerSelectingGui();
    }

    public static GUI createQuestGui(QuestPlayer player, int page) {
        if (page > ((player.getActiveQuests().size() - 1) / 14) && !player.getActiveQuests().isEmpty()) return null;
        GUI gui;
        if (page == 0) {
            gui = new VanishGUI("quests", "Your quests | Page 1", 54);
            GUIs.register(gui);

            GUIObject border = new GUIObject(gui, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                    .setName(" ")
                    .build());
            List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53).
                    forEach(i -> gui.setItem(i, border));
            for (int i = 0; i < 3; i++) {
                DailyQuest dq = player.getDailyQuests()[i];
                if(dq == null){
                    gui.setItem(12 + i, new GUIObject(gui, new ItemBuilder(Material.PAPER).setName("&a&lCOMPLETED").build()));
                    continue;
                }
                GUIObject gobj = new GUIObject(gui, dq.getDisplay());
                gui.setItem(12 + i, gobj);
            }
            for (int i = 0; i < 14 && i < player.getActiveQuests().size(); i++) {
                Quest quest = player.getActiveQuests().get(i);
                List<String> lore = new ArrayList<>();
                lore.add("  &#e0b8e6Rewards:");
                lore.addAll(quest.getRewards().stream().map(r -> "    &#e0b8e6•&#ff6993 " + r.getName()).toList());
                GUIObject gobj = new GUIObject(gui,
                        new ItemBuilder(getQuestIco(quest))
                                .setName("&#ff5e00" + quest.getName())
                                .setLore(lore)
                                .setEnchanted()
                                .hideAttributes()
                                .build()
                );
                gui.setItem(28 + i + (i / 7) * 2, gobj);
            }
        } else {
            gui = new VanishGUI("quests", "Your active quests | Page " + (page + 1), 36);
            GUIs.register(gui);

            GUIObject border = new GUIObject(gui, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                    .setName(" ")
                    .build());
            List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35).
                    forEach(i -> gui.setItem(i, border));

            for (int i = 0; i < 14 && i + page * 14 < player.getActiveQuests().size(); i++) {
                Quest quest = player.getActiveQuests().get(i + page * 14);
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add("&#e0b8e6Rewards:");
                lore.addAll(quest.getRewards().stream().map(r -> "  &#e0b8e6•&#ff6993 " + r.getName()).toList());
                lore.add("");
                GUIObject gobj = new GUIObject(gui,
                        new ItemBuilder(getQuestIco(quest))
                                .setName("&#ff5e00" + quest.getName())
                                .setLore(lore)
                                .setEnchanted()
                                .hideAttributes()
                                .build()
                );
                gui.setItem(10 + i + (i / 7) * 2, gobj);
            }
        }
        final int finalPage = page;
        if (finalPage < ((player.getActiveQuests().size() - 1) / 28)) {
            GUIButton next_page = new GUIButton(gui, new ItemBuilder(Material.PAPER)
                    .setName("&#03fcc2→" + (finalPage + 2))
                    .build()) {
                @Override
                public void onClick(ClickType clickType, Player player) {
                    player.closeInventory();
                    QuestPlayer qp = QuestPlayers.get(player);
                    GUI gui = createQuestGui(qp, finalPage + 1);
                    if (gui == null) return;
                    gui.open(player);
                }
            };
            gui.setItem(page == 0 ? 50 : 32, next_page);
        }
        if (finalPage > 0) {
            GUIButton next_page = new GUIButton(gui, new ItemBuilder(Material.PAPER)
                    .setName("&#03fcc2→" + (finalPage + 2))
                    .build()) {
                @Override
                public void onClick(ClickType clickType, Player player) {
                    player.closeInventory();
                    QuestPlayer qp = QuestPlayers.get(player);
                    GUI gui = createQuestGui(qp, finalPage - 1);
                    if (gui == null) return;
                    gui.open(player);
                }
            };
            gui.setItem(30, next_page);
        }
        return gui;
    }

    private static ItemStack getQuestIco(Quest quest) {
        if(quest instanceof DungeonQuest) {
            return new ItemStack(Material.SPAWNER);
        } else if(quest instanceof ItemCollectQuest quest1) {
            Material material;
            try {
                material = Material.valueOf(quest1.getType());
            } catch (Exception e) {
                material = Material.CHEST;
            }
            return new ItemStack(material);
        } else if(quest instanceof LocationQuest) {
            return new ItemStack(Material.FILLED_MAP);
        } else if(quest instanceof MobKillQuest quest1) {
            return new ItemStack(Material.valueOf(quest1.getType().name()+"_SPAWN_EGG"));
        } else {
            return new ItemStack(Material.BOOK);
        }
    }

    public static void updateSelectionGui(){
        for(GUI gui : GUIs.getAll()) {
            if(gui.getId().startsWith("spawner_") || gui.getId().startsWith("dungeon_")) GUIs.unregister(gui);
        }
        loadDungeonSelectingGui();
        loadSpawnerSelectingGui();
    }
    private static void loadSpawnerSelectingGui(){
        for(Dungeon dungeon : Dungeons.getAll()){
            List<Dungeon.Spawner> list = dungeon.getSpawners();
            for(int page = 0; page < ((list.size()-1)/28)+1; page++) {
                GUI gui = new GUI("spawner_"+dungeon.getId()+"_"+page, "Spawner select | Page "+(page+1), 54);
                GUIs.register(gui);

                GUIObject border = new GUIObject(gui, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                        .setName(" ")
                        .build());
                List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53).
                        forEach(i -> gui.setItem(i, border));
                for(int i = 0; i < 28 && page*28+i<list.size(); i++) {
                    Dungeon.Spawner spawner = list.get(page*28+i);
                    GUIButton button = new GUIButton(gui,
                            new ItemBuilder(Material.SPAWNER)
                                    .setName("&fLocation &b" + Utils.locToString(spawner.getLocation()))
                                    .setLore("",
                                            "&fID: &7"+spawner.getId(),
                                            "&fMob type: &a"+spawner.getType(),
                                            "&fMob source: &e"+spawner.getSpawner().id(),
                                            "&fMob amount: &d"+spawner.getAmount(),
                                            "")
                                    .setNBT("spawner_id", spawner.getId()+"")
                                    .build()) {
                        @Override
                        public void onClick(ClickType clickType, Player player) {
                            Dungeon dungeon = Dungeons.getDungeonSelection(player);
                            Dungeon.Spawner spawner = dungeon.getSpawners().get(Integer.parseInt(Utils.readTag(getView(player), "spawner_id")));
                            Dungeons.select(player.getUniqueId(), spawner);
                            player.closeInventory();
                            player.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Actions.spawn_selected")
                                    .replace("%spawner%", ""+spawner.getId())));
                        }
                    };
                    gui.setItem(10+i+(2*(i/7)), button);
                }

                final int finalPage = page;
                if(finalPage < ((list.size()-1)/28)) {
                    GUIButton next_page = new GUIButton(gui, new ItemBuilder(Material.PAPER)
                            .setName("&#03fcc2→" + (finalPage +2))
                            .build()) {
                        @Override
                        public void onClick(ClickType clickType, Player player) {
                            player.closeInventory();
                            GUI gui = GUIs.get("spawner_"+dungeon.getId()+"_" + (finalPage + 1));
                            gui.open(player);
                        }
                    };
                    gui.setItem(50, next_page);
                }
                if(finalPage > 0){
                    GUIButton prev_page = new GUIButton(gui, new ItemBuilder(Material.PAPER)
                            .setName("&#03fcc2" + finalPage + "←")
                            .build()) {
                        @Override
                        public void onClick(ClickType clickType, Player player) {
                            player.closeInventory();
                            GUI gui = GUIs.get("spawner_"+dungeon.getId()+"_" + (finalPage-1));
                            gui.open(player);
                        }
                    };
                    gui.setItem(48, prev_page);
                }
            }
        }
    }
    private static void loadDungeonSelectingGui(){
        List<Material> materials = List.of(Material.ANDESITE, Material.GRANITE, Material.BRICKS, Material.STONE_BRICKS, Material.COBBLESTONE,
                Material.BEDROCK, Material.GLASS, Material.SANDSTONE, Material.STONE, Material.END_STONE, Material.NETHER_BRICK, Material.NETHERRACK,
                Material.GRASS_BLOCK, Material.OBSIDIAN, Material.CRAFTING_TABLE);
        List<Dungeon> list = Dungeons.getAll();
        for(int page = 0; page < ((list.size()-1)/28)+1; page++) {
            GUI dungeon_administration = new GUI("dungeon_"+page, "Dungeon select | Page "+(page+1), 54);
            GUIs.register(dungeon_administration);

            GUIObject border = new GUIObject(dungeon_administration, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                    .setName(" ")
                    .build());
            List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53).
                    forEach(i -> dungeon_administration.setItem(i, border));

            for(int i = 0; i < 28 && page*28+i<list.size(); i++) {
                Dungeon dungeon = list.get(page*28+i);
                GUIButton button = new GUIButton(dungeon_administration,
                        new ItemBuilder(materials.get(LQuests.a.r.nextInt(15)))
                        .setName("&eDungeon &a" + dungeon.getName())
                        .setLore("",
                                "&fID: &7"+dungeon.getId(),
                                "&fLocation: &b"+Utils.locToString(dungeon.getLocation()),
                                "")
                        .setNBT("dungeon_id", dungeon.getId()+"")
                        .build()) {
                    @Override
                    public void onClick(ClickType clickType, Player player) {
                        Dungeon dungeon = Dungeons.get(Long.parseLong(Utils.readTag(getView(player), "dungeon_id")));
                        Dungeons.select(player.getUniqueId(), dungeon);
                        player.closeInventory();
                        player.sendMessage(Utils.toPrefix(LQuests.a.getMessage("Actions.dungeon_selected")
                                .replace("%dungeon%", ""+dungeon.getId())));
                    }
                };
                dungeon_administration.setItem(10+i+(2*(i/7)), button);
            }

            final int finalPage = page;
            if(finalPage < ((list.size()-1)/28)) {
                GUIButton next_page = new GUIButton(dungeon_administration, new ItemBuilder(Material.PAPER)
                        .setName("&#03fcc2→" + (finalPage +2))
                        .build()) {
                    @Override
                    public void onClick(ClickType clickType, Player player) {
                        player.closeInventory();
                        GUI gui = GUIs.get("dungeon_" + (finalPage + 1));
                        gui.open(player);
                    }
                };
                dungeon_administration.setItem(50, next_page);
            }
            if(finalPage > 0){
                GUIButton prev_page = new GUIButton(dungeon_administration, new ItemBuilder(Material.PAPER)
                        .setName("&#03fcc2" + finalPage + "←")
                        .build()) {
                    @Override
                    public void onClick(ClickType clickType, Player player) {
                        player.closeInventory();
                        GUI gui = GUIs.get("dungeon_" + (finalPage-1));
                        gui.open(player);
                    }
                };
                dungeon_administration.setItem(48, prev_page);
            }
        }
    }
    public static class VanishGUI extends GUI{

        public VanishGUI(String id, String name, int size) {
            super(id, name, size);
        }

        @Override
        public void onClose() {
            GUIs.unregister(this);
        }
    }
}
