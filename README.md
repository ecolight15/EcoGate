# EcoGate

**EcoGate** は、Minecraft サーバー用のテレポートゲートプラグインです。プレイヤーが特定の場所に設置されたゲート間を瞬時に移動できるシステムを提供します。また、様々なタイプのワールドを作成・管理する機能も含まれています。

## 特徴

### ゲート機能
- **ゲート作成**: 任意の場所にテレポートゲートを設置
- **ゲート連携**: 2つのゲートを双方向または単方向でリンク
- **自動テレポート**: プレイヤーがゲートに踏み込むと自動的に連携先に移動
- **ゲート管理**: ゲートの追加、削除、更新、情報表示
- **近傍検索**: 近くにあるゲートを検索
- **爆発保護**: エンダードラゴンの爆発からゲートを保護

### ワールド管理機能
- **複数ワールドタイプ対応**:
  - 通常ワールド (Normal)
  - ネザーワールド (Nether)
  - エンドワールド (End)
  - フラットワールド (Flat)
  - 大きなバイオームワールド (Large Biome)
  - 増幅ワールド (Amplified)
- **ワールド作成**: コマンドで新しいワールドを簡単に作成
- **ワールド削除**: 不要になったワールドの削除
- **ワールド一覧**: 管理されているワールドの表示

## 必要要件

- **Minecraft サーバー**: Spigot/PaperMC 1.18以上
- **Java**: Java 8以上
- **依存プラグイン**: EcoFramework 0.19

## インストール

1. [EcoFramework](https://github.com/ecolight15/EcoFramework) をダウンロードしてプラグインフォルダに配置
2. EcoGate.jar をサーバーのプラグインフォルダに配置
3. サーバーを再起動またはプラグインをリロード

## コマンド

### EcoGate 基本コマンド
- `/ecogate` - プラグインのメインコマンド
- `/ecogate test` - テストコマンド
- `/ecogate reload` - 設定ファイルのリロード

### ゲート管理コマンド
- `/gate add <ゲート名> [メッセージ]` - 現在の位置にゲートを追加
- `/gate update <ゲート名> [メッセージ]` - ゲートの情報を更新
- `/gate del <ゲート名>` - ゲートを削除
- `/gate link <ゲート1> <ゲート2>` - 2つのゲートをリンク
- `/gate unlink <ゲート名>` - ゲートのリンクを解除
- `/gate near` - 近くのゲートを検索
- `/gate list [all]` - ゲート一覧を表示
- `/gate info [ゲート名]` - ゲートの詳細情報を表示

### ワールド管理コマンド
- `/world add normal <ワールド名>` - 通常ワールドを作成
- `/world add nether <ワールド名>` - ネザーワールドを作成
- `/world add end <ワールド名>` - エンドワールドを作成
- `/world add flat <ワールド名>` - フラットワールドを作成
- `/world add large <ワールド名>` - 大きなバイオームワールドを作成
- `/world add amplified <ワールド名>` - 増幅ワールドを作成
- `/world del <ワールド名>` - ワールドを削除
- `/world list` - ワールド一覧を表示

## 権限

### EcoGate 基本権限
- `ecogate` - EcoGateの基本コマンド使用権限
- `ecogate.test` - テストコマンド使用権限
- `ecogate.reload` - リロードコマンド使用権限

### ゲート関連権限
- `ecogate.gate` - ゲート関連コマンドの基本権限
- `ecogate.gate.add` - ゲート追加権限
- `ecogate.gate.update` - ゲート更新権限
- `ecogate.gate.del` - ゲート削除権限
- `ecogate.gate.link` - ゲートリンク権限
- `ecogate.gate.unlink` - ゲートリンク解除権限
- `ecogate.gate.near` - 近傍ゲート検索権限
- `ecogate.gate.list` - ゲート一覧表示権限
- `ecogate.gate.info` - ゲート情報表示権限

### ワールド関連権限
- `ecogate.world` - ワールド関連コマンドの基本権限
- `ecogate.world.add` - ワールド作成の基本権限
- `ecogate.world.add.normal` - 通常ワールド作成権限
- `ecogate.world.add.nether` - ネザーワールド作成権限
- `ecogate.world.add.end` - エンドワールド作成権限
- `ecogate.world.add.flat` - フラットワールド作成権限
- `ecogate.world.add.large` - 大きなバイオームワールド作成権限
- `ecogate.world.add.amplified` - 増幅ワールド作成権限
- `ecogate.world.del` - ワールド削除権限
- `ecogate.world.list` - ワールド一覧表示権限

## 設定

### config.yml
```yaml
server: 'server_name'  # サーバー名
gate:
  deactivationRadius: 2  # ゲート無効化半径
```

### gate.yml
ゲートの設定情報が自動的に保存されます。手動での編集は推奨されません。

### world.yml  
ワールドの設定情報が自動的に保存されます。手動での編集は推奨されません。

## 使用方法

### 基本的なゲートの設定手順

1. **ゲートを作成する**
   ```
   /gate add 駅 ようこそ駅へ！
   ```

2. **別の場所にもう一つのゲートを作成**
   ```
   /gate add 商店街 商店街へいらっしゃい
   ```

3. **2つのゲートをリンクする**
   ```
   /gate link 駅 商店街
   ```

4. **プレイヤーがゲートに踏み込むと自動的にテレポート**

### ワールドの作成

```
/world add normal 新しい世界
```

## ライセンス

このプラグインは [GNU Lesser General Public License v3.0](LICENSE) の下で公開されています。

## 作者

- **ecolight** - プラグイン開発者

## バージョン

現在のバージョン: 0.12

## サポート

バグ報告や機能要求は、GitHubのIssueページでお願いします。