package Nio.fileThread;

import java.io.File;

public class Client {
    public static int number = 1;
    private static String path = "D:\\内网通文件\\超级管理员\\linux\\linuxIso\\CentOS-6.8-x86_64-bin-DVD1.iso0";

    public static void main(String[] args) throws InterruptedException {
        File file = new File(path);

        Client client = new Client();
        client.sendFolder(file);
        Thread.sleep(1000);
    }

    public void sendFolder(File file) {
        System.out.println(number);
        while (number > 5) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (file.isFile()) {//鏂囦欢
            new ClientThreadOfFile(file, path).start();
            number++;
        } else {//鏂囦欢澶?
//			if(!Nio.file.getPath().equals(path)) {
            ClientThreadOfFolder clientThreadOfFolder = new ClientThreadOfFolder(file, path);
            clientThreadOfFolder.start();
            number++;

            try {
                clientThreadOfFolder.join();
                System.out.println("join瀹屾瘯");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
//			}

            File allFile[] = file.listFiles();
            if (allFile != null) {
                for (File subFile : allFile) {
                    sendFolder(subFile);
                }
            }
        }

    }

}
