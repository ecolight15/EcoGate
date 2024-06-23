
package jp.minecraftuser.ecogate.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.minecraftuser.ecoframework.ListenerFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.Utl;
import jp.minecraftuser.ecogate.config.EcoGateConfig;
import jp.minecraftuser.ecogate.config.LoaderGate;
import jp.minecraftuser.ecogate.config.LoaderWorld;
import jp.minecraftuser.ecogate.struct.Gate;
import jp.minecraftuser.ecogate.timer.BlockSetTimer;
import org.bukkit.Bukkit;
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
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

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
        Player player = event.getPlayer();

        //乗り物に乗ってる場合は転送拒否
        if (player.isInsideVehicle()) {
            return;
        }
        // ゲート転送後にゲート座標にいる状態かを確認
        if (usedmap.containsKey(player)) {
            Location destGateLoc = usedmap.get(player);
            Location playerLocation = player.getLocation();
            if ((destGateLoc.getBlockX() == playerLocation.getBlockX()) &&
                //(l.getBlockY() == yy) &&
                (destGateLoc.getBlockZ() == playerLocation.getBlockZ())) {
                return;
            }
            usedmap.remove(player);
        }
        Gate gate = gates.search(event.getTo());
        if (gate == null) return;

        // ゲートが存在する場合
        Location destGateLoc = gate.getNormalizedLocation().clone();
        // ゲートの説明テキストを送信
        if (gate.text != null) player.sendMessage(Utl.repColor(gate.text));
        // ゲート転送処理を実行
        usedmap.put(player, destGateLoc);
        player.teleport(destGateLoc);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void VehicleMoveEvent(VehicleMoveEvent event){
        //MineCartとBoatのみ転送対象とする。
        if(event.getVehicle().getType() == EntityType.MINECART || event.getVehicle().getType() == EntityType.BOAT){
            Entity vehicleEntity = event.getVehicle();

            if(!vehicleEntity.getPassengers().isEmpty()){
                List<Player> playerList = new ArrayList<>();
                // ゲート転送後にゲート座標にいる状態かを確認
                for (Entity entity : vehicleEntity.getPassengers()) {
                    if(entity.getType() == EntityType.PLAYER){
                        Player player = (Player) entity;
                        if (usedmap.containsKey(player)) {
                            Location destGateLoc = usedmap.get(player);
                            Location vehicleLocation = vehicleEntity.getLocation();
                            //2ブロック離れるまではreturnする。
                            if (Math.abs(destGateLoc.getBlockX() - vehicleLocation.getBlockX()) < 2 && Math.abs(destGateLoc.getBlockZ() - vehicleLocation.getBlockZ()) < 2) {
                                return;
                            }
                            usedmap.remove(player);
                        }
                        playerList.add(player);
                    }
                }

                //プレイヤーの乗っていないビークルは除外
                if(playerList.isEmpty())return;
                
                Gate gate = gates.search(event.getTo());
                if(gate == null) return;
                
                // ゲートが存在する場合
                Location destGateLoc = gate.loc.clone();
                //ボートの場合そのままテレポートすると水に沈むのでY+1する
                if(vehicleEntity.getType() == EntityType.BOAT){
                    destGateLoc.add(0,1,0);
                }

                //プレイヤーのテレポート処理
                for (Player player : playerList) {
                    String text = gate.text;
                    if (text != null) player.sendMessage(Utl.repColor(text));
                    // ゲート転送処理を実行
                    usedmap.put(player, destGateLoc);
                    player.teleport(destGateLoc);
                }
                //テレポート処理
                vehicleEntity.teleport(destGateLoc);
                Vector direction = destGateLoc.getDirection();
                vehicleEntity.setVelocity(direction);

                //ビークルにプレイヤーを乗せる
                for (Player player : playerList) {
                    vehicleEntity.addPassenger(player);
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void EntityExplode(EntityExplodeEvent event) {
        if (event.getEntityType() != EntityType.ENDER_DRAGON) return;
        for (Block b : event.blockList()) {
            if (b.getType() != Material.WATER)continue;
            if (gates.contains(b.getLocation())) { new BlockSetTimer(plg, b.getLocation(), b.getType(), "").runTaskLater(plg, 1); continue; }
            if (gates.contains(b.getLocation(), 0, 1, 0)) { new BlockSetTimer(plg, b.getLocation(), b.getType(), "").runTaskLater(plg, 1); continue; }
        }
    }
    
    @EventHandler(priority=EventPriority.LOWEST)
    public void BlockFromTo(BlockFromToEvent event) {
        Block b = event.getBlock();
        if (b.getType() != Material.WATER) return;
        if (gates.contains(b.getLocation())) { event.setCancelled(true); return; }
        if (gates.contains(b.getLocation(), 0, 1, 0)) { event.setCancelled(true); return; }
        if (gates.contains(b.getLocation(), 0, 2, 0)) { event.setCancelled(true); return; }
    }

    /**
     * プレイヤーテレポートイベント処理
     * @param event イベント情報
     */
    @EventHandler(priority=EventPriority.LOWEST)
    public void PlayerTeleport(PlayerTeleportEvent event) {
        //エンドゲートウェイで構成されたゲートを通過する際の
        //エンドゲートウェイ本来のテレポートイベントをキャンセルする
        Location location_from = event.getFrom();
        Location location_to = event.getTo();

        if (event.getCause() != TeleportCause .END_GATEWAY) return;

        //最寄りのゲートを取得
        Gate near_gate = gates.nearGateSearch(location_from,true);
        if(near_gate != null){
            Location near_gate_loc = near_gate.loc;
            //ゲートの周囲のテレポートイベントはキャンセル
            if(near_gate_loc.getWorld() == location_from.getWorld() &&
                    Math.abs(near_gate_loc.getBlockX() - location_from.getBlockX()) <= 1 &&
                    Math.abs(near_gate_loc.getBlockY() - location_from.getBlockY()) <= 1 &&
                    Math.abs(near_gate_loc.getBlockZ() - location_from.getBlockZ()) <= 1){
                event.setCancelled(true);
            }
        }
    }
}
