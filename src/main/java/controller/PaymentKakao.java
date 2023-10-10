package controller;

import common.Handler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PaymentKakao implements Handler {
    private static final long serialVersionUID = 1L;

    public PaymentKakao() {
        super();
    }

    @Override
    public String doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            URL url = new URL("https://kapi.kakao.com/v1/payment/ready");

            HttpURLConnection huc = (HttpURLConnection) url.openConnection();

            huc.setRequestMethod("POST");
            huc.setRequestProperty("Authorization", "KakaoAK ");
            huc.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            huc.setDoInput(true);
            huc.setDoOutput(true);

            Map<String, String> params = new HashMap<>();

            params.put("cid", "TC0ONETIME");
            params.put("partner_order_id", "2"); // TODO Order 번호를 받아와서 수정
//            params.put("partner_user_id", request.getSession().getId());
            params.put("partner_user_id", "testId");

            String[] items = request.getParameterValues("items");

            params.put("item_name",  "'" + items[0] + " 외 " + (items.length - 1) + "개'");
            params.put("quantity", String.valueOf(items.length));

            params.put("total_amount", request.getParameter("total"));
            params.put("tax_free_amount", "0");

            params.put("approval_url", "http://localhost/payment/success");
            params.put("cancel_url", "http://localhost/payment/cancel");
            params.put("fail_url", "http://localhost/payment/fail");

            String param = "";

            for (Map.Entry<String, String> entry : params.entrySet()) {
                param += entry.getKey() + "=" + entry.getValue() + "&";
            }

            huc.getOutputStream().write(param.getBytes("utf-8"));

            System.out.println(param);

            BufferedReader br = new BufferedReader(new InputStreamReader(huc.getInputStream()));

            JSONParser jsonParser = new JSONParser();
            JSONObject parsed = (JSONObject) jsonParser.parse(br);

            return "redirect/" + (String) parsed.get("next_redirect_pc_url");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "/";
    }

    @Override
    public String doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return null;
    }

    @Override
    public String getPath() {
        return "/payment/kakao";
    }
}
