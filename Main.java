import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("処理したいテキストファイルのファイルパスを入力してください。\n(何も入力せずEnterでこのプログラムと同じ位置に存在するtext.txtを読み込みます。)\n>");
        String filename = scanner.nextLine().trim();

        if (filename.isEmpty()) {
            filename = "./text.txt";
        }

        String paragraph = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            paragraph = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] paragraphAr = paragraph.split("\n");
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

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./result.txt"))) {
            writer.write(lastResult);
        } catch (IOException e) {
            e.printStackTrace();
        }

        scanner.close();
    }
}
