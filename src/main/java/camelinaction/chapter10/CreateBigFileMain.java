package camelinaction.chapter10;

import java.io.File;
import java.io.FileOutputStream;

public class CreateBigFileMain {

    public static void main(String[] args) throws Exception {
        new File("data/inventory").mkdirs();
        File file = new File("data/inventory/bigfile.csv");
        if (file.exists()) {
            file.delete();
        }

        int lines = 100;
        if (args.length == 1) {
            lines = Integer.parseInt(args[0]);
        }

        System.out.println("Creating data/inventory/bigfile.csv with " + lines + " ");
        createBigFile(file, lines);
        System.out.println("Creating data/inventory/bigfile.csv done");
    }

    private static void createBigFile(File file, int lines) throws Exception {
        FileOutputStream fos = new FileOutputStream(file);
        for (int i = 0; i < lines; i++) {
            String line = "123," + i + ",bumper,4\n";
            fos.write(line.getBytes());
        }
        fos.close();
    }
}
