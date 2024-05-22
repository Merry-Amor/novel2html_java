import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    public static void main(String[] args) {
        // メインフレームの作成
        JFrame frame = new JFrame("novel2html");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // テキストエリアの設定
        JTextArea topTextArea = new JTextArea();
        JTextArea bottomTextArea = new JTextArea();

        // テキストエリアの高さと幅の設定
        topTextArea.setPreferredSize(new Dimension(200, 100));
        bottomTextArea.setPreferredSize(new Dimension(200, 100));

        // テキストの折り返しと単語単位の折り返しを有効化
        topTextArea.setLineWrap(true);
        topTextArea.setWrapStyleWord(true);
        bottomTextArea.setLineWrap(true);
        bottomTextArea.setWrapStyleWord(true);

        // 変換ボタンの作成
        JButton convertButton = new JButton("変換");

        // レイアウトマネージャーの設定
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding

        // 上部分のテキストエリアの位置調整と配置、及びとスクロール設定の有効化
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        frame.add(new JScrollPane(topTextArea), gbc);

        // 変換ボタンの配置
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        frame.add(convertButton, gbc);

        // 下部分のテキストエリアの位置調整と配置、及びスクロール設定の有効化
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        frame.add(new JScrollPane(bottomTextArea), gbc);

        // イベントリスナーの作成
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = topTextArea.getText();
                String result = novel2html(text);
                bottomTextArea.setText(result);
            }
        });

        // Make the frame visible
        frame.setVisible(true);
    }

    public static String novel2html(String message) {

        String[] paragraphAr = message.split("\n");
        int size = paragraphAr.length;
        int i = 1;

        List<String> result = new ArrayList<>();
        result.add("<p>");
        for (String line : paragraphAr) {
            System.out.println(i + " / " + size);
            char[] lineAr = line.toCharArray();
            List<String> lineRe = new ArrayList<>();
            boolean rubyFlag = false;
            boolean emptyLine = false;
            int gomaFlagS = 0;
            int gomaFlagE = 0;
            int bbFlagS = 0;
            int bbFlagE = 0;

            if (line.isEmpty()) {
                result.remove(result.size() - 1);
                lineRe.add("</p>\n<p>&nbsp;</p>\n<p>");
                emptyLine = true;
            } else {
                for (char s : lineAr) {
                    if (s == '｜') {
                        if (bbFlagS < 1) {
                            bbFlagS += 1;
                        } else if (bbFlagS == 1) {
                            bbFlagS += 1;
                            lineRe.add("<span class=\"bb\">");
                        } else if (bbFlagS == 2 && bbFlagE < 1) {
                            bbFlagE += 1;
                        } else if (bbFlagE == 1) {
                            lineRe.add("</span>");
                            bbFlagS = 0;
                            bbFlagE = 0;
                        }
                    } else if (s == '《') {
                        if (rubyFlag) {
                            lineRe.add("</rb><rp>（</rp><rt>");
                        } else if (gomaFlagS < 1) {
                            gomaFlagS += 1;
                        } else if (gomaFlagS == 1) {
                            gomaFlagS += 1;
                            lineRe.add("<span class=\"goma\">");
                        }
                    } else if (s == '》') {
                        if (rubyFlag) {
                            lineRe.add("</rt><rp>）</rp></ruby>");
                            rubyFlag = false;
                        } else if (gomaFlagS == 2 && gomaFlagE < 1) {
                            gomaFlagE += 1;
                        } else if (gomaFlagE == 1) {
                            lineRe.add("</span>");
                            gomaFlagS = 0;
                            gomaFlagE = 0;
                        } else {
                            lineRe.add("》");
                            gomaFlagS = 0;
                            gomaFlagE = 0;
                        }
                    } else if (s == '—') {
                        lineRe.add("―");
                    } else {
                        if (gomaFlagS == 1) {
                            lineRe.add("《");
                            gomaFlagS = 0;
                        }
                        if (bbFlagS == 1) {
                            lineRe.add("<ruby><rb>");
                            rubyFlag = true;
                            bbFlagS = 0;
                        }
                        if (bbFlagS == 2) {
                            lineRe.add("―");
                        } else {
                            lineRe.add(String.valueOf(s));
                        }
                    }
                }
            }
            if (gomaFlagE == 1) {
                lineRe.add("》");
                gomaFlagE = 0;
            } else if (gomaFlagS == 1) {
                lineRe.add("《");
                gomaFlagS = 0;
            }
            result.add(String.join("", lineRe));
            if (emptyLine) {
                emptyLine = false;
            } else {
                result.add("<br />\n");
            }
            i++;
        }
        result.remove(result.size() - 1);
        result.add("</p>");

        String lastResult = String.join("", result);

        System.out.println(lastResult);

        return lastResult;
    }
}
