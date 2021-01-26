package com.nowcoder.community.community;

import java.io.IOException;

public class WkTests {

    public static void main(String[] args) {
        String cmd = "g:/work/wkhtmltopdf/bin/wkhtmltoimage --quality 75 https://www.baidu.com G:/work/data/wk-images/1.png";
        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
