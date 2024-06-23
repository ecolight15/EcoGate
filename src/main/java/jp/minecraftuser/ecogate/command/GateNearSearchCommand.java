
package jp.minecraftuser.ecogate.command;

import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.Utl;
import jp.minecraftuser.ecogate.config.EcoGateConfig;
import jp.minecraftuser.ecogate.config.LoaderGate;
import jp.minecraftuser.ecogate.struct.Gate;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * 最も近いゲートを取得するコマンド
 *
 * @author ecolight
 */
public class GateNearSearchCommand extends CommandFrame {
    private static EcoGateConfig ecgConf = null;

    /**
     * コンストラクタ
     *
     * @param plg_  プラグインインスタンス
     * @param name_ コマンド名
     */
    public GateNearSearchCommand(PluginFrame plg_, String name_) {
        super(plg_, name_);
        ecgConf = (EcoGateConfig) conf;
    }

    /**
     * コマンド権限文字列設定
     *
     * @return 権限文字列
     */
    @Override
    public String getPermissionString() {
        return "ecogate.gate.near";
    }

    /**
     * 処理実行部
     *
     * @param sender コマンド送信者
     * @param args   パラメタ接続済みゲートを取得
     * @return コマンド処理成否
     */
    @Override
    public boolean worker(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        LoaderGate gates = ecgConf.getGates();
        boolean link_check = true;
        if (args.length >= 1) {
            try {
                link_check = Boolean.parseBoolean(args[0]);
            } catch (Exception e) {
                return false;
            }
        }
        Gate nearGate = gates.nearGateSearch(player.getLocation(), link_check);
        if(nearGate == null){
            Utl.sendPluginMessage(plg, sender, "最寄りゲートが存在しません。");
            return true;
        }

        Gate gateLink = nearGate.link;
        Location playerLoc = ((Player) sender).getLocation();
        Vector vector = nearGate.loc.toVector().subtract((playerLoc.toVector()));
        playerLoc.setDirection(vector);

        player.teleport(playerLoc);

        if (gateLink != null) {
            Utl.sendPluginMessage(plg, sender, "\n§a最寄りゲート[{0}](text[{1}])§r\n" +
                            "Server[{2}] World[{3}]\n" +
                            "X[{4}] Y[{5}] Z[{6}] Yaw[{7}] Pitch[{8}]\n" +
                            "§b接続先ゲート[{9}](text:{10})§r\n" +
                            "Server[{11}] World[{12}]\n" +
                            "X[{13}] Y[{14}] Z[{15}] Yaw[{16}] Pitch[{17}]",
                    nearGate.name,
                    nearGate.text,
                    nearGate.server,
                    nearGate.worldName,
                    Integer.toString(nearGate.loc.getBlockX()),
                    Integer.toString(nearGate.loc.getBlockY()),
                    Integer.toString(nearGate.loc.getBlockZ()),
                    String.format("%.2f", nearGate.loc.getYaw()),
                    String.format("%.2f", nearGate.loc.getPitch()),
                    gateLink.name,
                    gateLink.text,
                    gateLink.server,
                    gateLink.name,
                    Integer.toString(gateLink.loc.getBlockX()),
                    Integer.toString(gateLink.loc.getBlockY()),
                    Integer.toString(gateLink.loc.getBlockZ()),
                    String.format("%.2f", gateLink.loc.getYaw()),
                    String.format("%.2f", gateLink.loc.getPitch())
            );
        }else {
            Utl.sendPluginMessage(plg, sender, "最寄りゲート[{0}](text[{1}])\n" +
                            "Server[{2}] World[{3}]\n" +
                            "X[{4}] Y[{5}] Z[{6}] Yaw[{7}] Pitch[{8}]",
                    nearGate.name,
                    nearGate.text,
                    nearGate.server,
                    nearGate.worldName,
                    Integer.toString(nearGate.loc.getBlockX()),
                    Integer.toString(nearGate.loc.getBlockY()),
                    Integer.toString(nearGate.loc.getBlockZ()),
                    String.format("%.2f", nearGate.loc.getYaw()),
                    String.format("%.2f", nearGate.loc.getPitch())
            );
        }

        return true;
    }

}
