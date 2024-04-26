
import javax.security.auth.login.FailedLoginException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.cert.TrustAnchor;
import java.time.chrono.JapaneseChronology;
import java.util.ArrayList;
import java.util.Random;

// お絵描きロジックの配列を扱うクラス
class NonogramMap
{
    private ButtonPanel b = new ButtonPanel();    // マップのサイズの最大値を取得するためbuttonPanelを参照
    public final int mapMaxWidth = b.mapMaxWidth;
    public final int mapMaxLength = b.mapMaxLength;

    public int correctSquareNum = 0;    // 正解のマス目の数

    // プレイヤーが開けたマスの情報（0ならまだタッチしてないマス、1ならすでにタッチして開けたマス）
    public int[][] userMap = new int[mapMaxLength][mapMaxWidth];

    // 問題となるパズルのデータ（1のところはタッチしてOK、0のところをタッチすると罰則（ライフが減るなど））
    public int[][] mapData = new int[mapMaxLength][mapMaxWidth];

    NonogramMap(String fileName)
    {
        // ユーザーマップの初期化
        for (int i = 0; i < mapMaxWidth; i++) {
            for (int j = 0; j < mapMaxLength; j++) {
                userMap[j][i] = 0;    // 初期値はすべてゼロ
            }
        }
        System.out.printf("File: %s\n", fileName);
        ReadMapData(fileName);    // 任意のファイル（MapData下に配置）
    }

    // 外部ファイルのデータを配列に落とし込む
    // https://www.sejuku.net/blog/20924 などを参考に作成
    private void ReadMapData(String fileName)
    {
        try
        {
            InputStream in = this.getClass().getResourceAsStream("/MapData/" + fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            int j = 0;
            while ((line = br.readLine()) != null)    // 1行ずつ
            {
                for (int i = 0; i < line.length(); i++)    // 1文字ずつ
                {
                    mapData[j][i] = Character.getNumericValue(line.charAt(i));
                    if (mapData[j][i] == 1) { correctSquareNum++; }
                }
                j++;
            }
            br.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}


/**********  お絵描きロジックのModel部分 **********/
class NonogramModel implements NonogramMessageObserver
{
    private NonogramFrame v;    // マスをタッチした情報をViewに送るためのメンバ
    private NonogramMap map;    // お絵描きロジックの問題データのクラス

    private int mapWidth;
    private int mapLength;

    private int deathCount = 0;    // 間違ったマスを押した回数を記録
    private int correctSquareCount = 0;    // 残りの正解のマスの数を記録

    private String[] hintLine;    // ヒントの文字列の配列（行）
    private String[] hintColumn;    // ヒントの文字列の配列（列）

    private NonogramMessageObservable msgOb;    // 通信対戦時のメッセージ受信用
    private boolean didstart = false;
    private boolean didFinish = false;

    private final String _newLine = System.getProperty("line.separator");
    private NonogramModel nonogramModel = this;    // 内部クラスからアクセスできるように

    /********** コンストラクタ **********/
    NonogramModel(NonogramFrame v, ButtonPanel b)
    {
        this.v = v;    // Viewをメンバ変数に保持
        this.mapWidth = b.mapWidth;    // お絵描きロジックの問題のマス目の幅を設定
        this.mapLength = b.mapLength;    // お絵描きロジックの問題のマス目の長さ（高さ）を設定
    }

    // コンストラクタ（ファイル名が指定された場合）
    // 現在不使用
    NonogramModel(NonogramFrame v, ButtonPanel b, String fileName)
    {
        this(v, b);    // 上のコンストラクタを呼び出し
        map = new NonogramMap(fileName);
        hintLine = new String[map.mapMaxLength];
        hintColumn = new String[map.mapMaxWidth];
        makeHint();    // ヒントの作成
    }

    // コンストラクタ（難易度が指定された場合）
    NonogramModel(NonogramFrame v, ButtonPanel b, Nonogram.difficulty difficulty, boolean battleMode)
    {
        this(v, b);    // 上のコンストラクタを呼び出し
        // 難易度に応じたファイルをランダムで選択
        NonogramFileNames f = new NonogramFileNames();
        map = new NonogramMap(f.getRandomFileName(difficulty));
        // ヒントを生成
        hintLine = new String[map.mapMaxLength];
        hintColumn = new String[map.mapMaxWidth];
        makeHint();    // ヒントの作成
        // ネットワーク対戦モードなら
        if (battleMode)
        {
            // 対戦設定画面を準備
            BattleManager bm = new BattleManager();
            while (!didstart)    // 対戦開始まで待機
            {
                try { Thread.sleep(100); }
                catch(InterruptedException e) {}
            }
            System.out.println("Connected");
        }
        v.setVisible(true);
    }


    /********** お絵描きロジックのヒント **********/
    // ヒントを作成して配列に入れる
    private void makeHint()
    {
        for (int i = 0; i < mapLength; i++)    // 行のヒント
        {
            hintLine[i] = "";
            for (int j = 0; j < mapWidth; j++)
            {
                if (map.mapData[i][j] == 0) { continue; }
                int count;
                for (count = 0; j < mapWidth && map.mapData[i][j] != 0; j++, count++);
                hintLine[i] += Integer.toString(count) + " ";
            }
            v.setHintLine(i, hintLine[i]);
        }
        for (int i = 0; i < mapWidth; i++)    // 列のヒント
        {
            hintColumn[i] = "";
            for (int j = 0; j < mapLength; j++)
            {
                if (map.mapData[j][i] == 0) { continue; }
                int count;
                for (count = 0; j < mapLength && map.mapData[j][i] != 0; j++, count++);
                hintColumn[i] += Integer.toString(count) + " ";
            }
            v.setHintColumn(i, hintColumn[i]);
        }
    }


    /********** ゲームの中枢部分 **********/
    // コントローラーからアクセスされるメソッド
    public void setValue(int x, int y)    // 引数：ユーザーがタッチしたマスの座標
    {
        if (didFinish) { return; }    // 対戦終了している場合何もしない
        if (map.userMap[y][x] >= 1)    // すでにタッチした場所をもう一度タッチしたとき
        {
            return;    // 無視する
        }
        else if (map.mapData[y][x] == 0)    // タッチしてはいけないマスをタッチしたとき
        {
            // ここに間違えた場所をタッチしたときの処理を書く
            System.out.println("Incorrect position");
            map.userMap[y][x] = 2;    // 2番は間違った場所をタッチしたことを記録
            v.setView(x, y, NonogramFrame.judge.miss, deathCount);
            deathCount++;
            return;
        }
        // 正解のマスをタッチしたとき
        map.userMap[y][x] = 1;    // ユーザーが開けた位置を記録
        // ここにViewの更新処理を書く
        v.setView(x, y, NonogramFrame.judge.correct, deathCount);    // 塗りつぶした場所のtypeを1とする

        this.correctSquareCount++;    // 正解数を+1する
        sendCorrectCount();    // バトルモードなら正解数を相手に送信
        if (this.correctSquareCount == map.correctSquareNum)
        { 
            if (msgOb != null)    // バトルモードのとき
            {
                v.setView(0, 0, NonogramFrame.judge.win, deathCount);
            }
            else { v.setView(0, 0, NonogramFrame.judge.clear, deathCount); }
        }
    }

    // 機能実装予定だったが、結局未使用（Controllerから呼び出す文があるため消せない）
    public void setType(int type) {}

    // ゲームリスタートを行う関数、結局未使用
    public void restartGame()
    {
        v.dispose();
        Nonogram.restart = true;
    }

    
    /********** 通信対戦関連 **********/
    // 残りのマス目の数を送信
    private void sendCorrectCount()
    {
        if (msgOb != null && !didFinish)    // バトルモードかつ終了していないとき
        {
            msgOb.send(String.valueOf(map.correctSquareNum - this.correctSquareCount));    // 残りの正解数を相手に送信
        }
    }

    public void updateMsg(String msg)    // Observableから呼び出される
    {
        didstart = true;
        v.setTimeScreen(msg);    // Viewに相手の残りの正解マス数を表示させる
        int num = Integer.parseInt(msg);    // 相手の残りの正解マス数をintに
        if (num == 0)    // 相手が終了したとき
        {
            didFinish = true;    // 負けフラグを立てる
            // viewに負けたことを伝える
            v.setView(0, 0, NonogramFrame.judge.lose, deathCount);
        }
    }

    // 通信対戦の設定画面用フレーム
    class BattleManager extends JFrame implements ActionListener, Runnable
    {
        String hostname = "localhost";
        int portNum = 10010;

        private JPanel p;
        private JRadioButton radioS, radioC;
        private JTextField hostnameField, portNumField;
        private JButton b;

        BattleManager()
        {
            // JFrameのボタンや文字列の設定
            this.setSize(200,150);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setTitle("Battle setting");

            p = new JPanel();
            p.setLayout(new GridLayout(4, 2));

            radioS = new JRadioButton("Server", true); radioS.addActionListener(this); radioS.setActionCommand("radioS");
            radioC = new JRadioButton("Client", false); radioC.addActionListener(this); radioC.setActionCommand("radioC");
            hostnameField = new JTextField(hostname); hostnameField.setEnabled(false);
            portNumField  = new JTextField(String.valueOf(portNum));
            b = new JButton("Apply"); b.addActionListener(this); b.setActionCommand("b");

            p.add(radioS); p.add(radioC);
            p.add(new JLabel("Hostname")); p.add(hostnameField);
            p.add(new JLabel("Port number")); p.add(portNumField);
            p.add(new JPanel()); p.add(b);
            this.add(p);
            
            this.setVisible(true);

        }

        public void run()    // 設定終了後
        {
            this.portNum = Integer.parseInt(portNumField.getText());
            this.hostname = hostnameField.getText();
            if (radioS.isSelected())
            {
                // サーバーのとき
                System.out.println("I am server");
                msgOb = new NonogramServer(this.portNum);    // アップキャスト
            }
            else
            {
                // クライアントのとき
                System.out.println("I am client");
                msgOb = new NonogramClient(hostname, portNum);    // アップキャスト
            }
            msgOb.add(nonogramModel);    // Observableにクラスを設定
            sendCorrectCount();

            this.dispose();    // 接続が完了したらこのフレームは終了
        }

        private void waitFrame()
        {
            // 通信待機画面を表示
            this.setVisible(false);
            this.remove(p);
            this.revalidate();
            this.repaint();      
            this.add(new JLabel("Waiting for connection..."));
            this.setSize(300, 100);
            this.setVisible(true);
        }

        public void actionPerformed(ActionEvent e)
        {
            String es = e.getActionCommand();
            switch (es)
            {
                case "radioS":
                    radioC.setSelected(false);
                    hostnameField.setEnabled(false);
                    break;
                case "radioC":
                    radioS.setSelected(false);
                    hostnameField.setEnabled(true);
                    break;
                case "b":    // スタートボタンが押されたとき
                    Thread t = new Thread(this);
                    t.start();
                    waitFrame();
                    break;
            }
        }
    }
}
