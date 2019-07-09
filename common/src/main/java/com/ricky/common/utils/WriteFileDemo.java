package com.ricky.common.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;

/**
 * User: ricky
 * Date: 2018/8/31
 * Time: 10:10
 */
public class WriteFileDemo {
    private static int a =1;

    public static void main(String[] args) throws IOException {
//        writeDataToFile("C:/Users/Administrator.WIN-TDS1FSSN1O1/Desktop/aa.txt","abc");
//        appendDataToFile("C:/Users/Administrator.WIN-TDS1FSSN1O1/Desktop/aa.txt","abdddc" +"\r\n" +"ddd");
//        appendDataToFile("C:/Users/Administrator/Desktop/a.txt","123");
        String filename = "C:/Users/Administrator/Desktop/a.txt";
        writeDataAsy(filename,"abc21561");
//        File file = new File(filename);
//        InputStream inputStream = new FileInputStream(filename);
//        byte[] msg = new byte[(int) file.length()];
//        inputStream.read(msg);
//        inputStream.close();
//
//        System.out.println(new String(msg));
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"GBK"));
//        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:/Users/Administrator/Desktop/c.txt",true)));
//        String line = null;
//        while ((line = br.readLine())!=null){
//            bw.write(line);
//            bw.write("\r\n");
//        }
//        bw.flush();
//        br.close();
//        bw.close();
//
//        Scanner sc = new Scanner(new FileInputStream(filename),"GBK");
//        PrintWriter pw = new PrintWriter(new FileOutputStream("C:/Users/Administrator/Desktop/b.txt",true));
//        while (sc.hasNext()){
//            pw.println(sc.next());
//        }
//        sc.close();
//        pw.close();

    }

    public static void writeDataToFile(String path,String content){
        try{
            FileOutputStream fout = new FileOutputStream(path);
            FileChannel fc = fout.getChannel();
            byte[] message = content.getBytes("UTF-8");
            ByteBuffer buffer = ByteBuffer.wrap(message);
            buffer.put(message) ;
            buffer.flip();
            fc.write( buffer);
            System.out.println("写入数据成功！");
//            Selector selector = Selector.open();
//            Channel channel = SocketChannel.open();
//            ((SocketChannel) channel).configureBlocking(false);
//            SelectionKey selectionKey = ((SocketChannel) channel).register(selector,SelectionKey.OP_READ);
//            selectionKey.interestOps();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void appendDataToFile(String path,String content){
        try{
            FileOutputStream fout = new FileOutputStream(path,true);
            FileChannel fc = fout.getChannel();
            byte[] message = content.getBytes("UTF-8");
            ByteBuffer buffer = ByteBuffer.wrap(message);
            buffer.put(message) ;
            buffer.flip();
            fc.write(buffer);
            System.out.println("写入数据成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void writeDataAsy(String fileName,String content) throws IOException {
        Path path = Paths.get(fileName);
        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        long position = 0;

        buffer.put(content.getBytes());
        buffer.flip();
        Future<Integer> operation = fileChannel.write(buffer, position);
        buffer.clear();

        while(!operation.isDone());

        System.out.println("Write done");

    }
}
