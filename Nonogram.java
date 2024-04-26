
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.FloatBuffer;

class Nonogram implements Runnable
{
    public static boolean restart = false;

    // 難易度を表す列挙型
    public enum difficulty
    {
        easy,
        normal,
        hard
    }

    // スタート画面でゲーム開始ボタンが押されるまで待機する（スタートしたらtrueを返す）
    private boolean gameStart(StartmenuFrame start)
    {
        try{ Thread.sleep(100); }
        catch(InterruptedException e) { return true; }
        if(start.Started()) { return true; }
        else { return false; }
    }

    // ボタンのパネルのサイズを難易度によって決定する
    private ButtonPanel setbuttonPanel(difficulty d)
    {
        ButtonPanel b;
        switch (d)
        {
            case easy:
                b = new ButtonPanel(5, 5);    // easyなら5マスx5マス
                break;
            case normal:
                b = new ButtonPanel(10, 10);
                break;
            case hard:
                b = new ButtonPanel(15, 15);
                break;
            default:
                b = new ButtonPanel();
                break;
        }
        return b;
    }

    public void run()
    {
        /* スタート画面の表示 */
        StartmenuFrame start = new StartmenuFrame();
	    start.repaint();
        while (!gameStart(start));    // スタート画面が終わるまでループ
        difficulty d = start.getDifficulty();    // modelに伝えるための難易度を取得
        boolean battleMode = start.isBattleMode();    // modelに伝えるためのバトルモード選択の有無を取得
        start.dispose();    // スタート画面を終了
        /* ここまでスタート画面 */

        // 難易度に応じたボタンのパネルの設定
        ButtonPanel b = setbuttonPanel(d);

        // View, Model, Controllerの生成
        NonogramFrame view = new NonogramFrame(b);
        NonogramModel model = new NonogramModel(view, b, d, battleMode);
        NonogramController controller = new NonogramController(model, b);
    }

    public static void main(String argv[])
    {
        // 結局リスタート機能を実装しなかったためループは不使用
        while (true)
        {
            restart = false;
            Nonogram n = new Nonogram();
            Thread t = new Thread(n);
            t.start();

            while (!restart)    // restartフラグが立つまで無限ループ
            {
                try{ Thread.sleep(100); }
                catch(InterruptedException e) { break; }
            }
            try { t.join(); } // スレッドでの処理が終わるまで停止（多分要らない？よくわからない）
            catch (InterruptedException e) { break; }
            System.out.println("Restart");
        }
    }
}
