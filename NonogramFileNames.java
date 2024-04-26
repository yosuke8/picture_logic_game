
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

// お絵描きロジックのファイル名を扱うクラス
class NonogramFileNames
{
    // 難易度別のファイル名のリストを保存するArrayList
    private ArrayList<String> easy = new ArrayList<>();
    private ArrayList<String> normal = new ArrayList<>();
    private ArrayList<String> hard = new ArrayList<>();

    NonogramFileNames()    // ここにファイル名を設定してください
    {
        // easyモードのファイル名
        easy.add("yama.txt");
        easy.add("yen.txt");
        easy.add("king.txt");

        // normalモードのファイル名
        normal.add("sound.txt");

        // difficultモードのファイル名
        hard.add("uec.txt");
    }

    // 指定された難易度のファイル群の中から、ランダムで一つファイル名を返す
    public String getRandomFileName(Nonogram.difficulty d)
    {
        ArrayList<String> fileNames;
        switch(d)
        {
            case easy:
                fileNames = this.easy;
                break;
            case normal:
                fileNames = this.normal;
                break;
            case hard:
                fileNames = this.hard;
                break;
            default:
                System.out.println("Unsupported difficulty setting");
                fileNames = null;
                break;
        }
        Random rand = new Random();
        int num = rand.nextInt(fileNames.size());    // 0からファイル数までの乱数
        return fileNames.get(num);    // リストの乱数番目のファイル名を返す
    }
}
