package jp.minecraftuser.ecogate.struct;

import jp.minecraftuser.ecogate.config.LoaderGate;
import org.bukkit.Location;
import org.bukkit.World;

public class Gate {
    LoaderGate loader;
    public String name = "";
    public String server = "";
    public World world;
    public String worldName;
    public double x = 0.0;
    public double y = 0.0;
    public double z = 0.0;
    public float yaw = 0.0f;
    public float pitch = 0.0f;
    public Location loc = null;
    public String text = "";
    public Gate link = null;
    public boolean unload = false;

    /**
     * コンストラクタ
     * @param loader_ ローダー
     * @param server_ サーバ名
     * @param name_ ゲート名
     * @param world_ ワールド
     * @param x_ X座標
     * @param y_ Y座標
     * @param z_ Z座標
     * @param yaw_ Yaw
     * @param pitch_ Pitch
     * @param text_ テキスト
     */
    public Gate(LoaderGate loader_, String server_, String name_, World world_, double x_, double y_, double z_, float yaw_, float pitch_, String text_) {
        loader = loader_;
        server = server_;
        name = name_;
        world = world_;
        worldName = world.getName();
        x = x_;
        y = y_;
        z = z_;
        yaw = yaw_;
        pitch = pitch_;
        loc = new Location(world, x, y, z, yaw, pitch);
        text = text_;
    }

    /**
     * コンストラクタ
     * @param loader_ ローダー
     * @param server_ サーバ名
     * @param name_ ゲート名
     * @param worldName_ ワールド名
     * @param x_ X座標
     * @param y_ Y座標
     * @param z_ Z座標
     * @param yaw_ Yaw
     * @param pitch_ Pitch
     * @param text_ テキスト
     */
    public Gate(LoaderGate loader_, String server_, String name_, String worldName_, double x_, double y_, double z_, float yaw_, float pitch_, String text_) {
        loader = loader_;
        server = server_;
        name = name_;
        world = null;
        worldName = worldName_;
        x = x_;
        y = y_;
        z = z_;
        yaw = yaw_;
        pitch = pitch_;
        loc = new Location(world, x, y, z, yaw, pitch);
        text = text_;
    }

    /**
     * コンストラクタ
     * @param loader_ ローダー
     * @param name_ ゲート名
     * @param loc_ 位置
     * @param text_ テキスト
     */
    public Gate(LoaderGate loader_, String server_, String name_, Location loc_, String text_) {
        loader = loader_;
        server = server_;
        name = name_;
        world = loc_.getWorld();
        worldName = world.getName();
        x = loc_.getX();
        y = loc_.getY();
        z = loc_.getZ();
        yaw = loc_.getYaw();
        pitch = loc_.getPitch();
        loc = loc_;
        text = text_;
    }

    /**
     * 指定位置にゲートが存在するかどうかを取得する
     * @return ゲートの有無
     */
    public boolean isLocation(Location loc_) {
        // Locationがこのゲートの位置と一致するかどうか
        if (loc_.getWorld() == null) return false;
        return
            loc_.getWorld().equals(world) &&
            loc_.getBlockX() == loc.getBlockX() &&
            loc_.getBlockY() == loc.getBlockY() &&
            loc_.getBlockZ() == loc.getBlockZ();
    }

    /**
     * 指定位置にゲートが存在するかどうかを取得する(オフセット変更)
     * @return ゲートの有無
     */
    public boolean isLocation(Location loc_, int x_, int y_, int z_) {
        // Locationがこのゲートの位置と一致するかどうか
        if (loc_.getWorld() == null) return false;
        return
            loc_.getWorld().equals(world) &&
            loc_.getBlockX() == loc.getBlockX() + x_ &&
            loc_.getBlockY() == loc.getBlockY() + y_ &&
            loc_.getBlockZ() == loc.getBlockZ() + z_;
    }

    /**
     * ゲートの位置を取得する
     * @return ゲートの位置
     */
    public Location getNormalizedLocation() {
        return new Location(world, loc.getBlockX() + 0.5, y, loc.getBlockZ() + 0.5, yaw, pitch);
    }

    /**
     * ゲートの位置を取得する
     * @param x_ X座標
     * @param y_ Y座標
     * @param z_ Z座標
     * @return ゲートの位置
     */
    public Location getNormalizedLocation(int x_, int y_, int z_) {
        return new Location(world, loc.getBlockX() + 0.5 , y, loc.getBlockZ() + 0.5, yaw, pitch);
    }

    /**
     * ゲートのクローンを生成する
     * @return ゲートのクローン
     */
    public Gate clone() {
        return new Gate(loader, server, name, world, x, y, z, yaw, pitch, text);
    }
}
