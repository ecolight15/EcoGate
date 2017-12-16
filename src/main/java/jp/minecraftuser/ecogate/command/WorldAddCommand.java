
package jp.minecraftuser.ecogate.command;

import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.Utl;
import jp.minecraftuser.ecogate.config.EcoGateConfig;
import jp.minecraftuser.ecogate.config.LoaderWorld;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

/**
 * ワールド追加コマンドクラス
 * @author ecolight
 */
public class WorldAddCommand extends CommandFrame {
    private static EcoGateConfig ecgConf = null;

    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ コマンド名
     */
    public WorldAddCommand(PluginFrame plg_, String name_) {
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
        return "ecogate.world.add";
    }

    /**
     * 処理実行部
     * @param sender コマンド送信者
     * @param args パラメタ
     * @return コマンド処理成否
     */
    @Override
    public boolean worker(CommandSender sender, String[] args) {
        // パラメータチェック:4つのみ
        if (!checkRange(sender, args, 4, 4)) return true;

        // 名前、タイプ、SEED値、環境名指定でワールドを追加する
        LoaderWorld worlds = ecgConf.getWorlds();
        World w = worlds.loadWorld(args[1], args[0], args[2], args[3]);
        if (w == null) { Utl.sendPluginMessage(plg, sender, "新規ワールド[{0}]の生成に失敗しました", args[0]); return true; }
        Utl.sendPluginMessage(plg, sender, "新規ワールド[{0}]を作成しました", w.getName());
        return true;
    }
}
