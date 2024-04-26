import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.sound.sampled.Clip;

class NonogramFrame extends JFrame implements ActionListener {
    ButtonPanel b;
    NonogramModel m;

    private Timer timer; int seconds=0;
    private JLabel time = new JLabel("00:00");

    private JLabel close_window_c = new JLabel("ウィンドウを閉じてください");
    private JLabel close_window_o = new JLabel("ウィンドウを閉じてください");

    private JPanel p5 = new JPanel();

    
    ClassLoader cl = this.getClass().getClassLoader(); // jarファイル作成のため追加
    //private ImageIcon RedFlash = new ImageIcon("parts/RedFlash.gif"); //jarファイル作成のため変更
    private ImageIcon RedFlash = new ImageIcon(cl.getResource("parts/RedFlash.gif"));
    //private ImageIcon Heart01 = new ImageIcon("parts/Heart01.png"); //jarファイル作成のため変更
    private ImageIcon Heart01 = new ImageIcon(cl.getResource("parts/Heart01.png"));
    //private ImageIcon Heart02 = new ImageIcon("parts/Heart02.png"); //jarファイル作成のため変更
    private ImageIcon Heart02 = new ImageIcon(cl.getResource("parts/Heart02.png"));
    private JLabel HeartlifeImage0 = new JLabel(Heart02);
    private JLabel HeartlifeImage1 = new JLabel(Heart02);
    private JLabel HeartlifeImage2 = new JLabel(Heart02);
    private JLabel HeartlifeImage3 = new JLabel(Heart02);
    private JLabel HeartlifeImage4 = new JLabel(Heart02);
    private JLabel HeartdeathImage0 = new JLabel(Heart01);
    private JLabel HeartdeathImage1 = new JLabel(Heart01);
    private JLabel HeartdeathImage2 = new JLabel(Heart01);
    private JLabel HeartdeathImage3 = new JLabel(Heart01);
    private JLabel HeartdeathImage4 = new JLabel(Heart01);

    private JPanel gameover_screen = new JPanel();
    private JPanel gameclear_screen = new JPanel();
    private JLabel gameover_text;
    private JLabel gameclear_text;
    
    private JLabel[] Width_sub;
    private JLabel[] Length_sub;

    private JPanel frame = new JPanel();

    private AudioPlay AP, GO;
    //private ImageIcon SoundOn = new ImageIcon("parts//SoundOn.png"); // jarファイル作成のため変更
    private ImageIcon SoundOn = new ImageIcon(cl.getResource("parts/SoundOn.png"));
    //private ImageIcon SoundOff = new ImageIcon("parts/SoundOff.png"); // jarファイル作成のため変更
    private ImageIcon SoundOff = new ImageIcon(cl.getResource("parts/SoundOff.png"));
    private Boolean OnOff = true;
    private JButton SoundOnOff = new JButton(SoundOn);

    private boolean battleMode = false; //対戦モードのときtrue

    public enum judge
    {
        correct,
        miss,
        clear,
        win,
        lose
    }

    public NonogramFrame(ButtonPanel b) {
      this.b = b;
      
      JPanel p1 = new JPanel(); JPanel p2 = new JPanel();
      JPanel p3 = new JPanel(); JPanel p4 = new JPanel();
      JPanel p6 = new JPanel();
      JPanel p7 = new JPanel(); JPanel p8 = new JPanel();

      JPanel p_all  = new JPanel();
      JPanel blank  = new JPanel();
      JPanel Width  = new JPanel();
      JPanel Length = new JPanel();
      Width_sub  = new JLabel[b.mapMaxLength];
      Length_sub = new JLabel[b.mapMaxWidth];
  
      p1.setBackground(Color.WHITE);p2.setBackground(Color.WHITE);
      p3.setBackground(Color.WHITE);p4.setBackground(Color.WHITE);
      p5.setBackground(Color.WHITE);p6.setBackground(Color.WHITE);
      p7.setBackground(Color.WHITE);p8.setBackground(Color.WHITE);
      blank.setBackground(Color.WHITE);

      p2.add(time);
      p5.add(HeartlifeImage0);
      p5.add(HeartlifeImage1);
      p5.add(HeartlifeImage2);
      p5.add(HeartlifeImage3);
      p5.add(HeartlifeImage4);

      //経過時間の設定
      time.setFont(new Font("Arial", Font.BOLD, 30));
      timer = new Timer(1000, this);
      timer.start();

      //ゲームオーバー時に出る画面の設定
      gameover_text = new JLabel("Game Over");
      gameover_text.setPreferredSize(new Dimension(580, 525));
      gameover_text.setFont(new Font("Arial", Font.BOLD, 80));
      gameover_text.setHorizontalAlignment(JLabel.CENTER);
      gameover_screen.add(gameover_text, BorderLayout.CENTER);
      gameover_screen.add(close_window_o, BorderLayout.SOUTH);
      gameover_screen.setBounds(0, 0, 600, 600);

      //ゲームクリア時に出る画面の設定
      gameclear_text = new JLabel("Game Clear");
      gameclear_text.setPreferredSize(new Dimension(580, 525));
      gameclear_text.setFont(new Font("Arial", Font.BOLD, 80));
      gameclear_text.setHorizontalAlignment(JLabel.CENTER);
      gameclear_screen.add(gameclear_text, BorderLayout.CENTER);
      gameclear_screen.add(close_window_c, BorderLayout.SOUTH);
      gameclear_screen.setBounds(0, 0, 600, 600);

      //BGMのオンオフのボタンの設定
      SoundOnOff.setPreferredSize(new Dimension(50, 50));
      SoundOnOff.setBackground(Color.WHITE);
      SoundOnOff.setBorderPainted(false);
      SoundOnOff.addActionListener(this);
      p3.add(SoundOnOff);

      for(int i=0; i<b.mapLength; i++) { //ボタンの背景色を白に設定
        for(int j = 0; j<b.mapWidth; j++)
          b.buttons[i][j].setBackground(Color.WHITE);
      }

      for(int i=0; i<b.mapWidth; i++) { //上のヒントの設定
        Width_sub[i] = new JLabel();
        Width_sub[i].setHorizontalAlignment(JLabel.CENTER);
        Width_sub[i].setVerticalAlignment(JLabel.BOTTOM);
        Width.add(Width_sub[i]);
        Width_sub[i].setBorder(new LineBorder(Color.BLACK,1));
      }
      for(int i=0; i<b.mapLength; i++) { //右のヒントの設定
        Length_sub[i] = new JLabel();
        Length_sub[i].setHorizontalAlignment(JLabel.RIGHT);
        Length.add(Length_sub[i]);
        Length_sub[i].setBorder(new LineBorder(Color.BLACK,1));
      }

      p_all.setLayout(null);
      blank.setBounds(0, 0, 100, 100);
      b.setBounds(100, 100, 300, 300);
      Width.setBounds(100, 0, 300, 100);
      Length.setBounds(0, 100, 100, 300);

      frame.setLayout(null);
      p_all.setBounds(100, 100, 400, 400);
      p1.setBounds(0, 0, 100, 100);    p2.setBounds(100, 0, 400, 100);
      p3.setBounds(500, 0, 100, 100);  p4.setBounds(0, 100, 100, 400);
      p5.setBounds(500, 100, 100, 400);p6.setBounds(0, 500, 100, 100);
      p7.setBounds(100, 500, 400, 100);p8.setBounds(500, 500, 100, 100);

      Width.setLayout(new GridLayout(1,b.mapWidth));
      Length.setLayout(new GridLayout(b.mapLength,1));

      p_all.add(blank);
      p_all.add(Width);
      p_all.add(Length);
      p_all.add(b);

      frame.add(p1);frame.add(p2);
      frame.add(p3);frame.add(p4);
      frame.add(p_all);
      frame.add(p5);frame.add(p6);
      frame.add(p7);frame.add(p8);

      this.add(frame);
      this.setTitle("NonogramFrame");
      this.setSize(600,600);
      this.setResizable(false);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      //BGMの再生
      AP = new AudioPlay("parts/BGM_play.wav");
      AP.clip.loop(Clip.LOOP_CONTINUOUSLY);
      AP.clip.stop();
    }

    @Override
    public void setVisible(boolean b) {
      super.setVisible(b);
      if (b) {AP.clip.loop(Clip.LOOP_CONTINUOUSLY);}
    }

    public void actionPerformed(ActionEvent e) {
      if(e.getSource() == timer) { //経過時間のカウント
        seconds++;
        if (!battleMode)
          time.setText(String.format("%02d:%02d", seconds/60, seconds%60));
        return;
      }

      if(e.getSource() == SoundOnOff) {
        if(OnOff==true) { //オフならアイコン替えてBGMを停止
          SoundOnOff.setIcon(SoundOff);
          AP.clip.stop();
          OnOff = false;
        } else { //オンならアイコン替えてBGMを再生
          SoundOnOff.setIcon(SoundOn);
          AP.clip.loop(Clip.LOOP_CONTINUOUSLY);
          OnOff = true;
        }
        return;
      }
    }

    public void setView(int x, int y, judge type, int deathCount) {
      if(type == judge.correct) { //正解ならそのマスを黒く塗る
        b.buttons[y][x].setBackground(Color.BLACK);
        return;
      } else if (type == judge.miss) { //不正解ならそのマスに☓のGIFを設定
        Image image = RedFlash.getImage().getScaledInstance((int)(RedFlash.getIconWidth()*0.6), (int)(RedFlash.getIconHeight()*0.6), Image.SCALE_DEFAULT);
        ImageIcon RedFlash1 = new ImageIcon(image);
        b.buttons[y][x].setIcon(RedFlash1);

        if(deathCount==0) { //残りライフ4
          p5.remove(HeartlifeImage4); p5.add(HeartdeathImage4);
        } else if(deathCount==1) { //残りライフ3
          p5.remove(HeartlifeImage3); p5.add(HeartdeathImage3);
        } else if(deathCount==2) { //残りライフ2
          p5.remove(HeartlifeImage2); p5.add(HeartdeathImage2);
        } else if(deathCount==3) { //残りライフ1
          p5.remove(HeartlifeImage1); p5.add(HeartdeathImage1);
        } else if(deathCount==4) { //残りライフ0
          p5.remove(HeartlifeImage0); p5.add(HeartdeathImage0);
          gameover(); //ゲームオーバー画面を表示
        }
      }
      else if (type == judge.clear) {gameclear();} //ゲームクリアならゲームクリア画面表示
      else if (type == judge.win)   {gameWin();}   //ネットワーク対戦で勝ったならWin画面表示
      else if (type == judge.lose)  {gameLose();}  //ネットワーク対戦で負けたならLose画面表示
    }

    public void gameover() {
      System.out.println("Game Over");
      timer.stop(); //タイマーとBGMを停止
      AP.clip.close();

      GO = new AudioPlay("parts/BGM_gameover.wav");
      GO.clip.start(); //専用BGMの再生

      frame.removeAll();
      frame.revalidate();
      frame.repaint();
      frame.add(gameover_screen);
    }

    public void gameclear() {
      System.out.println("Game Clear");
      timer.stop();
      AP.clip.close();

      GO = new AudioPlay("parts/BGM_gameclear.wav");
      GO.clip.start();
      
      frame.removeAll();
      frame.revalidate();
      frame.repaint();
      frame.add(gameclear_screen);
    }

    public void gameLose() {
      System.out.println("Lose");
      gameover_text.setText("You Lose");
      timer.stop();
      AP.clip.close();

      GO = new AudioPlay("parts/BGM_gameover.wav");
      GO.clip.start();

      frame.removeAll();
      frame.revalidate();
      frame.repaint();
      frame.add(gameover_screen);
    }

    public void gameWin() {
      System.out.println("Win");
      gameclear_text.setText("You Win");
      timer.stop();
      AP.clip.close();

      GO = new AudioPlay("parts/BGM_gameclear.wav");
      GO.clip.start();
      
      frame.removeAll();
      frame.revalidate();
      frame.repaint();
      frame.add(gameclear_screen);
    }

    public void setHintLine(int lineNumber, String hint) {
      Length_sub[lineNumber].setText(hint); //横書きなのでそのまま
    }

    public void setHintColumn(int columnNumber, String hint) { 
      String[] str = hint.split(" ");
      String html = "<html>";
      for (int i=0; i<str.length; i++)
          html += str[i]+"<br>";
      html += "</html>"; //縦書きへ変更
      Width_sub[columnNumber].setText(html);
    }

    //ネットワーク対戦時に経過時間のところに相手の残り正解マス数
    public void setTimeScreen(String str) { 
      this.battleMode = true;
      time.setText(str);
    }
}

//BGM：魔王魂
//効果音素材：ポケットサウンド – https://pocket-se.info/