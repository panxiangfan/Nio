package NioWork.manyFileupload;


import java.io.File;


public class NioClient {

    //上传的路径
    private static String basePath = "D:\\内网通文件\\波波";

    public static int mark = 1;
    public static void main(String[] args) throws Exception {
        uploadFile(basePath);
        Thread.sleep(1000* 60 *60);
    }

    public static void uploadFile(String filePath) throws Exception {
        while (true){
            if (mark >5){
                Thread.sleep(10);
            }else {
                break;
            }
        }
        File file=new File(filePath);
         //判断是不是文件夹
         if(file.isDirectory()){
             if(!filePath.equals(basePath)){
                mark++;

              NioClientThread nioClientFileThread= new NioClientThread(filePath,basePath);

              nioClientFileThread.start();

              nioClientFileThread.join();

                 System.out.println(filePath+ "文件夹发送成功");
             }
             //递归查文件下所以的子文件夹和子文件
             //递归
             String files[] = file.list();
             for (String fPath : files) {
                 uploadFile(filePath + File.separator + fPath);
             }
         }else {
            mark++;
             new NioClientThread(filePath,basePath).start();
             System.out.println(filePath+ "文件传输成功");

         }
    }


}
