
package jp.minecraftuser.ecogate.command;

import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.Utl;
import jp.minecraftuser.ecogate.config.EcoGateConfig;
import jp.minecraftuser.ecogate.config.LoaderGate;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        setAuthBlock(true);
        setAuthConsole(true);
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
        String gateName = gates.nearGateSearch(player.getLocation(), link_check);
        if(gateName.equals("null")){
            Utl.sendPluginMessage(plg, sender, "最寄りゲートが存在しません。");
            return true;
        }
        String gateText = gates.getGateText(gateName);
        Utl.sendPluginMessage(plg, sender, "最寄りゲート:" + gateName + "[" + gateText + "]");
        Location gateLoc = gates.getGateLocation(gateName);
        Utl.sendPluginMessage(plg, sender, "座標" + gateLoc.toString());

        String linkGateName = gates.getLinkGateName(gateName);
        if (!linkGateName.equals("null")) {
            String linkGateText = gates.getGateText(linkGateName);
            Location linkGateLoc = gates.getGateLocation(linkGateName);
            Utl.sendPluginMessage(plg, sender, "接続先ゲート:" + linkGateName + "[" + linkGateText + "]");
            Utl.sendPluginMessage(plg, sender, "座標" + linkGateLoc.toString());
        }


        return true;
    }

}
