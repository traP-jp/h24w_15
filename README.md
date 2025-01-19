# conQest

対戦型領土取得戦略ゲームプラグイン

## 開発環境

1. `./gradlew createServer`により`./server`下にconQestがインストールされたpaperサーバーが構成されます。
2. openjdk-21で`paper.jar`を実行することによりサーバーをデバッグすることができます。

## ビルド方法

1. `docker build .`
2. `docker run -v <volume>:/usr/src/app <コンテナid>`