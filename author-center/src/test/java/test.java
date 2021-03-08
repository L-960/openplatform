//import org.apache.commons.lang.time.DateUtils;

import org.apache.http.client.utils.DateUtils;

import javax.xml.crypto.Data;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Author Lvxy
 * @Date 2021/3/7 20:50
 * @Version 1.0
 * @Desc
 */
public class test {
    public static void main(String[] args) {

        // 修正时间 +8小时  转成Date无需修正
        Instant now = Instant.now();

        // Instant now = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));


        System.out.println(now);
        System.out.println(new Date());
        System.out.println(Date.from(now));


        // 毫秒 1min * 60
        System.out.println(Date.from(now.plusMillis(60000*60)));
    }

}
