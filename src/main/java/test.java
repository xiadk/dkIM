import io.vertx.core.json.JsonObject;

import java.io.*;
import java.math.BigDecimal;

public class test {
    public static void main(String[] args) {
//        new test().count();
        String path = "/js/jquery.min.map";
        System.out.println(path.matches("/js/.*"));
    }

    public void count(){
        try {
            FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.home")+"\\Desktop\\1.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            BigDecimal total = new BigDecimal("0");
            int j=0;
            while (reader.ready()) {
                String str = reader.readLine();
                System.out.print(j+"值:"+str);
                String[] num= str.split(" ");
                BigDecimal one = new BigDecimal("1");
                if(num.length>1){
                    for(int i=0;i<num.length;i++){
                        BigDecimal bigDecimal = new BigDecimal(num[i]);
                        one = one.multiply(bigDecimal);
                    }
                }else{
                     one = new BigDecimal(num[0]);
                }
                one = one.setScale(3,BigDecimal.ROUND_HALF_UP);
                System.out.println("  结果:"+one);
                total = total.add(one);
                 j++;
            }
            System.out.println("总和："+total);
            System.out.println("总个:"+j);
            reader.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
