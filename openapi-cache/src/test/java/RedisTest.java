import com.google.inject.internal.cglib.core.$CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @Author Lvxy
 * @Date 2021/2/26 15:40
 * @Version 1.0
 * @Desc
 */


public class RedisTest {
    public static void main(String[] args)
    {
        int[] ins = {314,298,508,123,486,145};
        int[] ins2 = sort(ins);
        for(int in: ins2){
            System.out.println(in);
        }
    }

    public static int[] sort(int[] ins){

        for(int i=1; i<ins.length; i++){
            for(int j=i; j>0; j--){
                if(ins[j]<ins[j-1]){
                    int temp = ins[j-1];
                    ins[j-1] = ins[j];
                    ins[j] = temp;
                }
            }
        }
        return ins;
    }

}

class r extends RedisTest{
    static void t1(){}
}

interface a{
//    private static final a();

}

interface b extends a{

}