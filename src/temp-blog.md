### はじめに 
AlibabaCloudには、GPUを使用して機械学習のタスクを効率よく実行するためのcGPUという仕組みがあります。

ChatGPTをはじめとする生成AIが大いに話題になっている中で、画像生成AIのStable dffusionをcGPU環境で実行してみることにしました。

### Stable dffusionについて
ChatGPTを開発したOpenAI社はDALL-E2という画像生成AIも出しています。それに比べて、Stable dffusionはオープンソースのサービスで、GPUがあれば誰でも無料で利用できます。

NvidiaのGPUが搭載されたPCにセットアップするのが一般的な利用シーンですが、NvidiaのGPUが搭載されていないPCを持っている場合、高額なPCを新規で購入するよりも、従量制で利用した分だけを支払う形でクラウド上で実行するという選択肢もあります。

さらに、AlibabaCloudのcGPUを使用すれば、複数の機械学習タスクを効率よく実行したり、複数の人が同時に環境を実行することが可能です。

### cGPUのご紹介
クラウドを使用する場合、CPUは仮想化が可能で、必要な分だけ割り当てることができます。しかし、GPUは完全な仮想化が難しく、VMでは1枚のGPUを丸ごと使用するしかありません。

これでは、機械学習の学習や推論のプロセスで、低スペックのGPUではスペックが足りず、一方で高スペックのGPUではリソースが余ってしまうことがあります。

以下のイラストの左側に描かれているように、1つのタスクで1枚のGPUリソースをフルに利用することができない場合、リソースの無駄が生じてしまいます。

この問題を解決するためにAlibabaCloudのcGPUという仕組みがあります。cGPUを使用すると、以下のイラストの右側に描かれているように、1枚のGPUで複数の機械学習タスクを隔離した環境で効率よく同時に実行することが可能となります。
![image](https://github.com/raishiketsu/sample-foodadvisor--confirm/assets/37066555/31ca110b-5cb4-42a8-8901-8b093d406dc6)
※AlibabaCloudの資料を引用

セットアップの手順
###### ACKクラスター作成
まずはじめに、ACK Proエディションのクラスターを作成します。少なくとも1つのGPUタイプのVMをk8sのNodeとしてクラスターに追加します。

###### ack-ai-installerコンポーネントのインストール
ACKにはベーシックエディションとProエディションの2種類が存在します。ベーシックエディションではhelmを用いてack-ai-installerコンポーネントをインストールする必要がありますが、Proエディションでは管理コンソールのGUIからインストールが可能です。

ベーシックエディションは1枚のGPUの管理のみをサポートするのに対して、Proエディションでは複数枚のGPUのスケジューリングおよび隔離をサポートします。さらに、ベーシックエディションにはSLAがないのに対し、Proエディションは99.95%のSLAを提供します。一般的に、Proエディションを推奨します。

ここでは、Proエディションを使用した例を説明します。まずAlibabaCloud ACK管理コンソールを開き、クラスター一覧から目的のACKクラスターを選択します。そして、クラスターの詳細画面が表示されたら、左側のメニューバーから「アプリケーション」の下にある「Cloud-native AI Suite」をクリックします。cGPUの利用にはCloud-native AI Suiteの全ての機能をインストールする必要はありません。ack-ai-installerコンポーネントだけをインストールすれば十分です。

![image](https://github.com/raishiketsu/sample-foodadvisor--confirm/assets/37066555/75479c31-9d85-47d0-9285-e655e4ab4114)

###### Nodeにラベル追加
GPUタイプのVMにack.node.gpu.scheduleというラベルを追加すれば、cGPUを利用することが可能となります。ラベルを追加する方法は2つあります。

1つ目の方法は、ACKのノードプールにラベルを追加することで、ノードプール内のすべてのNodeにラベルが追加されます。ノードプール内のVMがすべてGPUタイプであれば、この方法が適しています。

2つ目の方法は、ACKのNodeに直接ラベルを追加することで、該当のVMにのみラベルが反映されます。同じクラスター内に通常のVMのNode、GPUタイプのVMのNode、バーチャルNodeが混在している場合や、単一のGPUタイプのVMのNodeにのみラベルを付ける場合は、この方法を使用します。

今回は、2つ目の方法を使用します。

###### kubectl-inspect-cgpuツールのインストール
CloudShellもしくはローカル環境で、kubeconfigを設定し、kubectlを実行できる環境で、以下のコマンドを実行し、kubectl-inspect-cgpuをインストールします。
```
Linux
sudo wget http://aliacs-k8s-cn-beijing.oss-cn-beijing.aliyuncs.com/gpushare/kubectl-inspect-cgpu-linux -O /usr/local/bin/kubectl-inspect-cgpu
Mac
sudo wget http://aliacs-k8s-cn-beijing.oss-cn-beijing.aliyuncs.com/gpushare/kubectl-inspect-cgpu-darwin -O /usr/local/bin/kubectl-inspect-cgpu
sudo chmod +x /usr/local/bin/kubectl-inspect-cgpu
kubectl inspect cgpu
```
![image](https://github.com/raishiketsu/sample-foodadvisor--confirm/assets/37066555/0fe7820a-942f-4d80-9b34-4df24b6e4762)

### Stable diffunsionをcGPU環境にインストール
Stable dffusionはオープンソースのため、GPUなどのコンピューティングリソースがあれば実行できます。一方でオープンソースのため、誰かが環境を用意してくれることがなく、自分でセットアップする必要があります。
本来Stable dffusion環境をセットアップするのが機械学習のエンジニアじゃないと難しいです。有識者がstable-diffusion-webuiという簡単に環境作成できるツールを開発してくれました。

https://github.com/AUTOMATIC1111/stable-diffusion-webui

調べてみたら、k8s環境にstable-diffusion-webuiを簡単にデプロイするためのhelmもあります。

https://github.com/amithkk/stable-diffusion-k8s

最初はhelm chartがあれば簡単にデプロイできると思いきや、実際にこちらのhelmを使おうとするとハマるところがあります。
helm ReleaseをACKのクラスターにデプロイしたら、libglib2.0-0というライブラリがないエラーが出ています。
ローカルにhelm chartに書かれているImageをプルーして、コンテナを実行し、中に入ってlibglib2.0-0を手動でインストールして、
再度commitして自分のコンテナレジストリにプッシュします。
それでlibglib2.0-0がないエラーが解消されましたが、今度は何もエラーが出ない状態でPodが無限ループで再起動しています。
ログに何も吐かれていないため、どうしようもないのでhelmでstable-diffusion-webuiをインストールするのを断念しました。
githubを見たら最新のcommitは半年前のため、頻繁にメンテナンスしていないようです。

方向性を変えて、自分でstable-diffusion-webuiをコンテナ化してデプロイするようにしました。
ubuntuのVMを使って、VM上にnvidiaのGPUドライブをインストールします。
VM上とコンテナ内両方でnvidiaのGPUを確認するコマンドが通ることを確認します。
VM上で確認　nvidia-smi
コンテナ内で確認　docker run --rm --gpus device=0 nvidia/cuda:11.3.0-base-ubuntu20.04 nvidia-smi
--gpus device=0 こちらのパラメータでもしうまく通らない場合、デバイス番号が合わないためです。デバイス番号を意識せず簡易的に--gpus all で指定するのもできます。

デプロイする前にkubectl inspect cgpuコマンドで確認すると、GPUがまだ配分されていないことを確認できます。
![image](https://github.com/raishiketsu/sample-foodadvisor--confirm/assets/37066555/0fe7820a-942f-4d80-9b34-4df24b6e4762)


Dockerfileはこちらです。
```
FROM nvidia/cuda:11.3.0-cudnn8-runtime-ubuntu20.04
ENV DEBIAN_FRONTEND noninteractive
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgl1 libglib2.0-0 git wget python3 python3-venv && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ADD . /stable-diffusion-webui
WORKDIR /stable-diffusion-webui/
RUN ./webui.sh -f can_run_as_root --exit --skip-torch-cuda-test

ENV VIRTUAL_ENV=/stable-diffusion-webui/venv
ENV PATH="$VIRTUAL_ENV/bin:$PATH"

VOLUME /stable-diffusion-webui/models
VOLUME /root/.cache

CMD ["python3", "launch.py", "--listen"]

```
GPUインスタンス上にコンテナが実行できることを確認します。
docker run -it -p 80:7860 --gpus device=0 stable-diffusion:v1 /bin/bash
問題なく実行できたら、イメージをコンテナレジストリにプッシュします。
その後、以下のYAMLファイルでデプロイします。

```
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: stable-diffusion
  name: stable-diffusion
  namespace: default
spec:
  replicas: 1
  selector:
    matchLabels:
      app: stable-diffusion
  template:
    metadata:
      labels:
        app: stable-diffusion
    spec:
      containers:
      - args:
        - --listen
        - --skip-torch-cuda-test
        - --no-half
        command:
        - python3
        - launch.py
        image: {{そちらのコンテナレジストリ}}/stable-diffusion:v1
        imagePullPolicy: IfNotPresent
        name: stable-diffusion
        resources:
          requests:
            cpu: "2"
            memory: 2Gi
          limits:
            aliyun.com/gpu-core.percentage: 25
            aliyun.com/gpu-mem: 5.5
---
apiVersion: v1
kind: Service
metadata:
  annotations:
    service.beta.kubernetes.io/alibaba-cloud-loadbalancer-address-type: internet
    service.beta.kubernetes.io/alibaba-cloud-loadbalancer-instance-charge-type: PayByCLCU
  name: stable-diffusion
  namespace: default
spec:
  externalTrafficPolicy: Local
  ports:
  - port: 80
    protocol: TCP
    targetPort: 7860
  selector:
    app: stable-diffusion
  type: LoadBalancer
```
注目するところはこちらのようにGPUのコンピューティングリソースとメモリを割り当てています。
```
          limits:
            aliyun.com/gpu-core.percentage: 25
            aliyun.com/gpu-mem: 5.5
```

コンテナを起動したあとに、Stable diffunsionのモデル約数GBをダウンロードする必要があります。
ダウンロードが終わったらロードバランサー経由でアクセスできるようになります。
実行している状態で、以下コマンドでGPU配分状況を確認します。
![image](https://github.com/raishiketsu/sample-foodadvisor--confirm/assets/37066555/6a434818-2520-4ccd-a30c-b5688316c97c)


### Stable diffunsionの実行
試しに、5.5GiBのGPUメモリをStable diffunsionに割り当てていますが、以下のエラーが出ています。
実行するPromptはこちらです。
Prompt:masterpiece, best quality, high quality,ultra detailed,office building,
NegativePrompt:EasyNegative,
```
RuntimeError: CUDA error: CUBLAS_STATUS_NOT_INITIALIZED when calling `cublasCreate(handle)`
Time taken: 0.02sTorch active/reserved: 4087/4196 MiB, Sys VRAM: 5161/5166 MiB (99.9%)
```
またYAMLファイルを編集して、より多めにGPUメモリを割り当ててあげます。
```
          limits:
            aliyun.com/gpu-core.percentage: 40
            aliyun.com/gpu-mem: 8
```
![image](https://github.com/raishiketsu/sample-foodadvisor--confirm/assets/37066555/c267b2e1-e514-4991-a21b-86f445bcd2d8)



### 最後に
今回は話題の生成系AIをcGPU環境で実行する環境を作成しました。
想定できる利用シーンは例えば、GPU付きのゲーミングPCを買うと約20万円以上かかるのが一般的です。
PCに適切なGPUがない場合、クラウド上で実行するのも一つの手です。チームで1枚のA10のGPUを3人でシェアして使えます。
例えば、1人はモデルを作るエンジニアで、2人は画像を生成するデザイナーです。
