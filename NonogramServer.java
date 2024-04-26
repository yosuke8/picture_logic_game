
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

interface NonogramMessageObserver
{
    public void updateMsg(String msg);
}

// NonogramModelで使う受信用メソッド
// 送信用のメソッド"send"をオーバーライドさせるため抽象クラス
abstract class NonogramMessageObservable
{
    // Observerの記憶にはArrayListを使用（ただし今回のプログラムではObserverは一つだけ）
    private ArrayList<NonogramMessageObserver> Observer = new ArrayList<>();

    public void add(NonogramMessageObserver ob)
    {
        Observer.add(ob);
    }

    protected void setMsg(String msg)
    {
        // addされたObserver実装クラスのupdateMsgを呼び出し
        for (int i = 0; i < Observer.size(); i++)
        {
            Observer.get(i).updateMsg(msg);
        }
    }

    abstract public void send(String msg);    // オーバーライドが必要
}

// サーバー、本科目のHP掲載のChatServerクラスを参考に作成
class NonogramServer extends NonogramMessageObservable implements Runnable
{
    private CommServer sv;
    private Thread t;

    NonogramServer()
    {
        this(10010);
    }

    NonogramServer(int portNum)
    {
        // CommServerオブジェクトをポート番号を指定して生成．
        sv = new CommServer(portNum);
        t = new Thread(this);
        t.start();
    }

    public void run()
    {
        String msg;
        // メッセージ待ちのループに入ります．
        // sv.recv()で受信．sv.send()で送信．
        // 相手のプログラムが通信を切断したら，終了．
        while((msg = sv.recv()) != null)
        {
            setMsg(msg);    // 親クラスのメソッドを呼び出し
        }
    }

    public void send(String msg)
    {
        sv.send(msg);
    }
}


// クライアント、本科目のHP掲載のChatClientクラスを参考に作成
class NonogramClient extends NonogramMessageObservable implements Runnable
{
    private CommClient cl;
    private Thread t;

    NonogramClient()
    {
        this("localhost", 10010);
    }

    NonogramClient(String hostName, int portNum)
    {
        cl = new CommClient(hostName, portNum);
        t = new Thread(this);
        t.start();
    }

    public void run()
    {
        String msg;
        while((msg = cl.recv()) != null)
        {
            setMsg(msg);
        }
    }

    public void send(String msg)
    {
        cl.send(msg);
    }
}
