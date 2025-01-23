package jp.minecraftuser.ecogate;

import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.ecogate.command.*;
import jp.minecraftuser.ecogate.config.EcoGateConfig;
import jp.minecraftuser.ecogate.listener.PlayerListener;
import jp.minecraftuser.ecogate.timer.AsyncWorker;

public class EcoGate extends PluginFrame {
    
    /**
     * 起動時処理
     */
    @Override
    public void onEnable() {
        initialize();
    }

    /**
     * 終了時処理
     */
    @Override
    public void onDisable()
    {
        disable();
    }

    /**
     * 設定初期化
     */
    @Override
    public void initializeConfig() {
        EcoGateConfig conf = new EcoGateConfig(this);
        conf.registerString("server");
        conf.registerInt("gate.deactivationRadius");
        registerPluginConfig(conf);
    }

    /**
     * コマンド初期化
     */
    @Override
    public void initializeCommand() {
        // EcoGate本体コマンド
        CommandFrame cmd = new EcogateCommand(this, "ecogate");
        cmd.addCommand(new EcogateTestCommand(this, "test"));
        cmd.addCommand(new EcogateReloadCommand(this, "reload"));
        registerPluginCommand(cmd);
        
        // ゲート制御系コマンド
        cmd = new GateCommand(this, "gate");
        cmd.addCommand(new GateAddCommand(this, "add"));
        cmd.addCommand(new GateUpdateCommand(this, "update"));
        cmd.addCommand(new GateDelCommand(this, "del"));
        cmd.addCommand(new GateLinkCommand(this, "link"));
        cmd.addCommand(new GateUnlinkCommand(this, "unlink"));
        cmd.addCommand(new GateNearSearchCommand(this, "near"));
        cmd.addCommand(new GateListCommand(this, "list"));
        cmd.addCommand(new GateInfoCommand(this, "info"));
        registerPluginCommand(cmd);
        
        // ワールド制御系コマンド
        cmd = new WorldCommand(this, "world");
        CommandFrame cmd2 = new WorldAddCommand(this, "add");
        cmd2.addCommand(new WorldAddNormalCommand(this, "normal"));
        cmd2.addCommand(new WorldAddNetherCommand(this, "nether"));
        cmd2.addCommand(new WorldAddTheEndCommand(this, "end"));
        cmd2.addCommand(new WorldAddFlatCommand(this, "flat"));
        cmd2.addCommand(new WorldAddLargeCommand(this, "large"));
        cmd2.addCommand(new WorldAddAmplifiedCommand(this, "amplified"));
        cmd.addCommand(cmd2);
        cmd.addCommand(new WorldDelCommand(this, "del"));
        cmd.addCommand(new WorldListCommand(this, "list"));
        registerPluginCommand(cmd);
    }

    /**
     * イベントリスナー初期化
     */
    @Override
    public void initializeListener() {
        registerPluginListener(new PlayerListener(this, "player"));
    }

    /**
     * 定期実行タイマー初期化
     */
    @Override
    public void initializeTimer() {
        AsyncWorker timer = AsyncWorker.getInstance(this, "worker");
        registerPluginTimer(timer);
        timer.runTaskTimer(this, 0, 20);
    }
}
