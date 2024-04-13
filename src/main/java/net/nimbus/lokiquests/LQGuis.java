package net.nimbus.lokiquests;

import net.nimbus.api.modules.gui.core.GUI;
import net.nimbus.api.modules.gui.core.GUIs;
import net.nimbus.api.modules.gui.core.guiobject.GUIButton;
import net.nimbus.api.modules.gui.core.guiobject.GUIObject;
import net.nimbus.api.modules.gui.core.itembuilder.ItemBuilder;
import net.nimbus.lokiquests.core.dungeon.Dungeon;
import net.nimbus.lokiquests.core.dungeon.Dungeons;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class LQGuis {
    public static void load(){
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
                                            "&fMob type: &a"+Utils.locToString(dungeon.getLocation()),
                                            "&fMob source: &e"+spawner.getSpawner().id(),
                                            "&fMob amount: &d"+spawner.getAmount(),
                                            "")
                                    .setNBT("spawner_id", spawner.getId()+"")
                                    .build()) {
                        @Override
                        public void onClick(ClickType clickType, Player player) {
                            Dungeon.Spawner spawner = Dungeons.getSpawner(Long.parseLong(Utils.readTag(getView(player), "spawner_id")));
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
}
