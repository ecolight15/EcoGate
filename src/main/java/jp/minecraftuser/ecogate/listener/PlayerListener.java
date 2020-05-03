
package jp.minecraftuser.ecogate.listener;

import java.util.HashMap;
import jp.minecraftuser.ecoframework.ListenerFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.Utl;
import jp.minecraftuser.ecogate.config.EcoGateConfig;
import jp.minecraftuser.ecogate.config.LoaderGate;
import jp.minecraftuser.ecogate.config.LoaderWorld;
import jp.minecraftuser.ecogate.timer.BlockSetTimer;
import jp.minecraftuser.ecogate.timer.VehicleTimer;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 * プレイヤーイベント処理リスナークラス
 * @author ecolight
 */
public class PlayerListener extends ListenerFrame {
    private static EcoGateConfig ecgConf = null;
    private static LoaderWorld worlds = null;
    private static LoaderGate gates = null;
    private static HashMap<Player, Location> usedmap = null;
    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ 名前
     */
    public PlayerListener(PluginFrame plg_, String name_) {
        super(plg_, name_);
        ecgConf = (EcoGateConfig)conf;
        worlds = ecgConf.getWorlds();
        gates = ecgConf.getGates();
        usedmap = new HashMap<>();
    }
    
    public void reloadConf() {
        worlds = ecgConf.getWorlds();
        gates = ecgConf.getGates();
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void PlayerMove(PlayerMoveEvent event) {
        //Player p = event.getPlayer();
        //plg.getServer().broadcastMessage("loc"+event.getTo().getBlockX()+","+event.getTo().getBlockY()+","+event.getTo().getBlockZ());
        Player p = event.getPlayer();
        if (usedmap.containsKey(p)) {
            Location l = usedmap.get(p);
            Location pl = p.getLocation();
            int yy = pl.getBlockY();
            if (p.isInsideVehicle()) {
                if (p.getVehicle().getType() == EntityType.MINECART) {
                    yy -= 1;
                }
            }
            if ((l.getBlockX() == pl.getBlockX()) &&
                //(l.getBlockY() == yy) &&
                (l.getBlockZ() == pl.getBlockZ())) {
                return;
            }
            usedmap.remove(p);
        }
        Location loc;
        if (p.isInsideVehicle()) {
            Entity ent = p.getVehicle();
            if (ent.getType() == EntityType.MINECART) {
                loc = gates.search(event.getTo(), 0, -1, 0);
            } else {
                loc = gates.search(event.getTo());
            }
        } else {
            loc = gates.search(event.getTo());
        }
        if (loc == null) return;
        //plg.getServer().broadcastMessage("jump");
        String text = gates.getText(loc);
        if (text != null) p.sendMessage(Utl.repColor(text));
        if (p.isInsideVehicle()) {
            p.getVehicle().remove();
            VehicleTimer timer = new VehicleTimer(plg, p, loc, p.getVehicle().getType(), "vehicle");
            timer.runTaskLater(plg, 1);
//            p.teleport(loc);
//            p.getVehicle().teleport(loc);
        } else {
            p.teleport(loc);
        }
        usedmap.put(p, loc);
    }
    @EventHandler(priority=EventPriority.LOWEST)
    public void ChunkUnload(ChunkUnloadEvent event) {
        Chunk c = event.getChunk();
        if (gates.contains(c)) {
            //event.setCancelled(true);
        }
    }
    @EventHandler(priority=EventPriority.LOWEST)
    public void EntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() == null) return;
        if (event.getEntityType() != EntityType.ENDER_DRAGON) return;
        for (Block b : event.blockList()) {
            if (b.getType() != Material.WATER)continue;
            if (gates.contains(b.getLocation())) { new BlockSetTimer(plg, b.getLocation(), b.getType(), "").runTaskLater(plg, 1); continue; }
            if (gates.contains(b.getLocation(), 0, 1, 0)) { new BlockSetTimer(plg, b.getLocation(), b.getType(), "").runTaskLater(plg, 1); continue; }
        }
    }
    
    @EventHandler(priority=EventPriority.LOWEST)
    public void BlockFromTo(BlockFromToEvent event) {
        //plg.getServer().broadcastMessage("BlockFromTo");
        Block b = event.getBlock();
        if (b.getType() != Material.WATER)return;
        if (gates.contains(b.getLocation())) { event.setCancelled(true); return; }
        if (gates.contains(b.getLocation(), 0, 1, 0)) { event.setCancelled(true); return; }
        if (gates.contains(b.getLocation(), 0, 2, 0)) { event.setCancelled(true); return; }
    }
}
