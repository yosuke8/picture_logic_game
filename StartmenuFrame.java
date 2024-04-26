import javax.swing.*;
import java.awt.*;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.event.*;

class StartmenuFrame extends JFrame implements ActionListener {
    JLabel background;
    JButton easy_button;
    JButton nomal_button;
    JButton hard_button;
    JButton Htp_button;
    JCheckBox battleChecbox;

    private boolean didStart = false;
    private Nonogram.difficulty difficulty;

    public StartmenuFrame() {

        this.setSize(600,600);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setTitle("Nonogram");

        ClassLoader cl = this.getClass().getClassLoader(); //jarファイル作成のため追加

        //スタート画面の画像設定
        //background = new JLabel(new ImageIcon("parts/NonogramStartmenu.png")); //jarファイル作成のため変更
        background = new JLabel(new ImageIcon(cl.getResource("parts/NonogramStartmenu.png")));
        background.setSize(600,600);
        background.setLayout(null);
        this.add(background);
        
        //各難易度のボタン設定
        easy_button=new JButton("<html><p text-align:center>Easy<br>5×5</p></html>");
        easy_button.setFont(new Font("Arial", Font.BOLD, 25));
        easy_button.setBorderPainted(false);
        easy_button.setForeground(Color.WHITE);
        easy_button.setBackground(new Color(135, 135, 135));
        easy_button.setBounds(96,348,120,61);
        easy_button.setBorderPainted(false);

        nomal_button=new JButton("<html><p text-align:center>Nomal<br>10×10</p></html>");
        nomal_button.setFont(new Font("Arial", Font.BOLD, 25));
        nomal_button.setForeground(Color.WHITE);
        nomal_button.setBackground(new Color(135, 135, 135));
        nomal_button.setBounds(233,348,120,61);
        nomal_button.setBorderPainted(false);

        hard_button=new JButton("<html><p text-align:center>Hard<br>15×15</p></html>");
        hard_button.setFont(new Font("Arial", Font.BOLD, 25));
        hard_button.setForeground(Color.WHITE);
        hard_button.setBackground(new Color(135, 135, 135));
        hard_button.setBounds(368,348,120,61);
        hard_button.setBorderPainted(false);

        //遊び方説明のHow to playのボタン設定
        Htp_button=new JButton("How to play");
        Htp_button.setFont(new Font("Arial", Font.BOLD, 13));
        Htp_button.setForeground(Color.WHITE);
        Htp_button.setBackground(new Color(135, 135, 135));
        Htp_button.setBounds(233,428,120,50);
        Htp_button.setBorderPainted(false);

        //ネットワーク対戦のためのチェックボックス設定
        battleChecbox = new JCheckBox("Battle Mode");
        battleChecbox.setFont(new Font("Arial", Font.BOLD, 13));
        battleChecbox.setForeground(Color.GREEN);
        battleChecbox.setBounds(368, 428, 120, 50);
        battleChecbox.setBackground(new Color(135, 135, 135));

        easy_button.addActionListener(this);
        nomal_button.addActionListener(this);
        hard_button.addActionListener(this);
        Htp_button.addActionListener(this);

        background.add(easy_button);
        background.add(nomal_button);
        background.add(hard_button);
        background.add(Htp_button);
        background.add(battleChecbox);
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == Htp_button){
            Desktop desktop = Desktop.getDesktop();
            try {
                //ルール説明PDFのURLをブラウザで開く
                desktop.browse(new URI("https://drive.google.com/file/d/12uqDnwCEAH269FS_dLhUDo42FyTpR6K-/view?usp=sharing"));
            } catch (IOException f) {f.printStackTrace();} //例外処理
              catch (URISyntaxException f) {f.printStackTrace();}
        }
        else if(e.getSource() == easy_button)  {didStart = true; difficulty = difficulty.easy;}
        else if(e.getSource() == nomal_button) {didStart = true; difficulty = difficulty.normal;}
        else if(e.getSource() == hard_button)  {didStart = true; difficulty = difficulty.hard;}
    }

    //始まったのかどうか
    public boolean Started() {return didStart;}

    //選択された難易度を取得
    public Nonogram.difficulty getDifficulty() {return difficulty;}

    //ネットワーク対戦のチェックボックスにチェックが入ったかどうか
    public boolean isBattleMode() {return battleChecbox.isSelected();}
}
