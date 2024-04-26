import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class NonogramController implements ActionListener
{
    private NonogramModel model;

    public NonogramController(NonogramModel m, ButtonPanel b)
    {
        model = m;
        String s1,s2,s3;
        for (int i = 0; i < b.mapLength; i++) {
            for (int j = 0; j < b.mapWidth; j++) {
                b.buttons[i][j].addActionListener(this);    
                s1 = Integer.toString(i);   //s1にiを文字列にしたものを入れる。
                s2 = Integer.toString(j);   //s2にjを文字列にしたものを入れる。
                s3 = s2 + ',' + s1;
                b.buttons[i][j].setActionCommand(s3);
            }
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        String es = e.getActionCommand();   //押されたボタンのs3取得
        int index = es.indexOf(",");        //　','の位置取得
        String xstr = es.substring(0, index);   //最初から','の位置までの文字取得（ｘ座標）
        String ystr = es.substring(index + 1);  //取得した','の位置の次から最後までの文字取得（y座標）
        int xint = Integer.parseInt(xstr);
        int yint = Integer.parseInt(ystr);
        model.setValue(xint, yint);
    }
}