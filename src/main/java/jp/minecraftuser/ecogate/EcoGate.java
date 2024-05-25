package jp.minecraftuser.ecogate;

import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.ecogate.command.EcogateCommand;
import jp.minecraftuser.ecogate.command.EcogateReloadCommand;
import jp.minecraftuser.ecogate.command.GateAddCommand;
import jp.minecraftuser.ecogate.command.GateCommand;
import jp.minecraftuser.ecogate.command.GateNearSearchCommand;
import jp.minecraftuser.ecogate.command.GateDelCommand;
import jp.minecraftuser.ecogate.command.GateLinkCommand;
import jp.minecraftuser.ecogate.command.GateUnlinkCommand;
import jp.minecraftuser.ecogate.command.WorldAddAmplifiedCommand;
import jp.minecraftuser.ecogate.command.WorldAddCommand;
import jp.minecraftuser.ecogate.command.WorldAddFlatCommand;
import jp.minecraftuser.ecogate.command.WorldAddLargeCommand;
import jp.minecraftuser.ecogate.command.WorldAddNetherCommand;
import jp.minecraftuser.ecogate.command.WorldAddNormalCommand;
import jp.minecraftuser.ecogate.command.WorldAddTheEndCommand;
import jp.minecraftuser.ecogate.command.WorldCommand;
import jp.minecraftuser.ecogate.command.WorldDelCommand;
import jp.minecraftuser.ecogate.command.WorldListCommand;
import jp.minecraftuser.ecogate.config.EcoGateConfig;
import jp.minecraftuser.ecogate.listener.PlayerListener;

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
        registerPluginConfig(new EcoGateConfig(this));
    }

    /**
     * コマンド初期化
     */
    @Override
    public void initializeCommand() {
        // EcoGate本体コマンド
        CommandFrame cmd = new EcogateCommand(this, "ecogate");
        cmd.addCommand(new EcogateReloadCommand(this, "reload"));
        registerPluginCommand(cmd);
        
        // ゲート制御系コマンド
        cmd = new GateCommand(this, "gate");
        cmd.addCommand(new GateAddCommand(this, "add"));
        cmd.addCommand(new GateDelCommand(this, "del"));
        cmd.addCommand(new GateLinkCommand(this, "link"));
        cmd.addCommand(new GateUnlinkCommand(this, "unlink"));
        cmd.addCommand(new GateNearSearchCommand(this, "near"));
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
}
