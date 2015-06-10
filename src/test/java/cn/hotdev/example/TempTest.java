package cn.hotdev.example;

import me.chanjar.weixin.common.util.crypto.SHA1;

import java.security.NoSuchAlgorithmException;

/**
 * Created by andy on 6/6/15.
 */
public class TempTest {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String gen = SHA1.gen("9FnuptKrdhsRpHvRSwakkaAHVgAnpeYD", "1433576438", "396683500");
        System.out.println(gen);
        // 3dd9815df99b008c411701d875e5236d104cfa0a

        int time = (int) (System.currentTimeMillis() / 1000);

        System.out.println(time + "");
    }
}
