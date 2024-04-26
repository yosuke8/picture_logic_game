/* 仕様変更（予定）
 * 配列の初期化は定数でなければいけない都合上、新たにmapMaxWidth, mapMaxLengthを追加
 * 配列の初期化にはこちらを使用し、mapWidthやmapLengthはループ処理などに使用する */


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class ButtonPanel extends JPanel    // extendsとimplementsミスってました
{
    public final int mapMaxWidth = 50;    // マス目の最大の幅
    public final int mapMaxLength = 50;    // マス目の最大の縦の長さ

    public int mapWidth = 5;    // マス目の幅（デフォルトは5）
    public int mapLength = 5;    // マス目の縦の長さ（デフォルトは5）

    public JRadioButton radio1 = new JRadioButton("〇", true);
    public JRadioButton radio2 = new JRadioButton("☓", false);

    public JButton[][] buttons = new JButton[mapMaxLength][mapMaxWidth];

    ButtonPanel()
    {
        this(5, 5);
    }

    ButtonPanel(int widthSize, int lengthSize)
    {
        this.mapWidth = widthSize;
        this.mapLength = lengthSize;
        this.setLayout(new GridLayout(mapWidth, mapLength));
        for (int i = 0; i < mapLength; i++)
        {
            for (int j = 0; j < mapWidth; j++)
            {
                buttons[i][j] = new JButton();
                this.add(buttons[i][j]);
            }
        }
    }
}
