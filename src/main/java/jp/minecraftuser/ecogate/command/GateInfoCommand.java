
package jp.minecraftuser.ecogate.command;

import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.Utl;
import jp.minecraftuser.ecogate.config.EcoGateConfig;
import jp.minecraftuser.ecogate.config.LoaderGate;
import jp.minecraftuser.ecogate.struct.Gate;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * ゲート情報取得コマンドクラス
 * @author ecolight
 */
public class GateInfoCommand extends CommandFrame {
    private static EcoGateConfig ecgConf = null;

    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ コマンド名
     */
    public GateInfoCommand(PluginFrame plg_, String name_) {
        super(plg_, name_);
        ecgConf = (EcoGateConfig)conf;
        setAuthBlock(true);
        setAuthConsole(true);
    }

    /**
     * コマンド権限文字列設定
     * @return 権限文字列
     */
    @Override
    public String getPermissionString() {
        return "ecogate.gate.info";
    }

    /**
     * 処理実行部
     * @param sender コマンド送信者
     * @param args パラメタ
     * @return コマンド処理成否
     */
    @Override
    public boolean worker(CommandSender sender, String[] args) {
        // パラメータチェック:0または1つ
        if (!checkRange(sender, args, 1, 2)) return true; // 第2パラメタまで許容するが、第2パラメタは無視する
        Player player = (Player) sender;
        LoaderGate gates = ecgConf.getGates();
        // パラメタなしの場合
        ArrayList<String> list = null;

        Gate gate = gates.get(args[0]);
        if (gate == null) {
            Utl.sendPluginMessage(plg, sender, "指定されたゲートが見つかりません");
            return true;
        }

        Gate gateLink = gate.link;
        Location playerLoc = ((Player) sender).getLocation();
        Vector vector = gate.loc.toVector().subtract((playerLoc.toVector()));
        playerLoc.setDirection(vector);

        player.teleport(playerLoc);

        if (gateLink != null) {
            Utl.sendPluginMessage(plg, sender, "接続先ゲート:" + gateLink.name + "(server:" + gateLink.server + ")[" + gateLink.text + "]");
            Utl.sendPluginMessage(plg, sender, "座標" + gateLink.loc.toString());
            Utl.sendPluginMessage(plg, sender, "\n§a最寄りゲート[{0}](text[{1}])§r\n" +
                            "Server[{2}] World[{3}]\n" +
                            "X[{4}] Y[{5}] Z[{6}] Yaw[{7}] Pitch[{8}]\n" +
                            "§b接続先ゲート[{9}](text[{10}])§r\n" +
                            "Server[{11}] World[{12}]\n" +
                            "X[{13}] Y[{14}] Z[{15}] Yaw[{16}] Pitch[{17}]",
                    gate.name,
                    gate.text,
                    gate.server,
                    gate.worldName,
                    Integer.toString(gate.loc.getBlockX()),
                    Integer.toString(gate.loc.getBlockY()),
                    Integer.toString(gate.loc.getBlockZ()),
                    String.format("%.2f", gate.loc.getYaw()),
                    String.format("%.2f", gate.loc.getPitch()),
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
                    gate.name,
                    gate.text,
                    gate.server,
                    gate.worldName,
                    Integer.toString(gate.loc.getBlockX()),
                    Integer.toString(gate.loc.getBlockY()),
                    Integer.toString(gate.loc.getBlockZ()),
                    String.format("%.2f", gate.loc.getYaw()),
                    String.format("%.2f", gate.loc.getPitch())
            );
        }

        return true;
    }

    /**
     * タブ補完用リスト取得
     * @param sender コマンド送信者
     * @param cmd コマンド
     * @param string タブ補完対象文字列
     * @param strings その他パラメタ
     * @return 補完リスト
     */
    @Override
    protected List<String> onTabComplete(CommandSender sender, Command cmd, String string, String[] strings) {
        LoaderGate gates = ecgConf.getGates();
        ArrayList<String> nameList = gates.getGateNameList();
        ArrayList<String> unloadList = gates.getUnloadWorldGateNameList();
        ArrayList<String> ret = new ArrayList<>();
        for (String name : nameList) {
            if (unloadList.contains(name)) {
                ret.add(name + " (unload)");
            } else {
                ret.add(name);
            }
        }
        return ret;
    }
}
