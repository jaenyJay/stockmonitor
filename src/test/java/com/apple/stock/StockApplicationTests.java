package com.apple.stock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import protostream.com.google.gson.Gson;
import watch.TelegramMessage;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@SpringBootTest
class StockApplicationTests {

    //Test
    //private static final String HOST_URL = "https://www.apple.com/kr/shop/retail/pickup-message?pl=true&searchNearby=true&store=R692&parts.0=MKQA3KH/A&option.0=ML2P3FE%2FA&mt=regular&parts.1=MKTG3KH/A&option.1=ML303FE%2FA&mt=regular&_=1633740088271";
    //Origin
    private static final String HOST_URL = "https://www.apple.com/kr/shop/retail/pickup-message?pl=true&searchNearby=true&store=R692&parts.0=MKLW3KH/A&option.0=ML873FE%2FA&mt=regular&parts.1=MKMT3KH/A&option.1=ML8D3FE%2FA&mt=regular&parts.2=MKNA3KH/A&mt=regular&_=1633708471832";

    @Test
    void contextLoads() {

    while(true){
        try {
            run();
            Thread.sleep(300000); //1초 대기 300 * 1000(1초) = 300000 (5분)
        }catch (Exception e){
            e.printStackTrace();
        }
    }
        

    }

    private void mailSend(String msg){
        final String bodyEncoding = "UTF-8"; //콘텐츠 인코딩

        String subject = "애플워치7 픽업재고 알림";
        String fromEmail = "taylorror@gmail.com";
        String fromUsername = "박재현";
        String toEmail = "kokikiko@knou.ac.kr,ssamta@icloud.com,liz_y115@naver.com"; // 콤마(,)로 여러개 나열


        // 메일에 출력할 텍스트
        String html = msg;

        // 메일 옵션 설정
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");

        props.put("mail.smtp.quitwait", "false");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        try {

            Authenticator auth = new MyAuthentication();

            // 메일 세션 생성
            Session session = Session.getInstance(props, auth);

            // 메일 송/수신 옵션 설정
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail, fromUsername));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            message.setSubject(subject);
            message.setSentDate(new Date());

            // 메일 콘텐츠 설정
            Multipart mParts = new MimeMultipart();
            MimeBodyPart mTextPart = new MimeBodyPart();
            MimeBodyPart mFilePart = null;

            // 메일 콘텐츠 - 내용
            mTextPart.setText(html, bodyEncoding, "html");
            mParts.addBodyPart(mTextPart);

            // 메일 콘텐츠 설정
            message.setContent(mParts);

            // MIME 타입 설정
            MailcapCommandMap MailcapCmdMap = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
            MailcapCmdMap.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
            MailcapCmdMap.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
            MailcapCmdMap.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
            MailcapCmdMap.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
            MailcapCmdMap.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
            CommandMap.setDefaultCommandMap(MailcapCmdMap);

            // 메일 발송
            Transport.send( message );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    class MyAuthentication extends Authenticator {

        PasswordAuthentication pa;

        public MyAuthentication() {

            String id = "taylorror@gmail.com";       // 구글 ID
            String pw = "ipihstyzyurvstjy";          // 구글 비밀번호

            // ID와 비밀번호를 입력한다.
            pa = new PasswordAuthentication(id, pw);

        }

        // 시스템에서 사용하는 인증정보
        public PasswordAuthentication getPasswordAuthentication() {
            return pa;
        }
    }
    private void run() {
        HttpURLConnection conn = null;
        JSONObject responseJson = null;
        StringBuilder sb = new StringBuilder();
        StringBuilder noticeSB = new StringBuilder();
        Boolean isSend = false;

        try {
            URL url = new URL(HOST_URL);

            conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            //conn.setDoOutput(true);

            JSONObject commands = new JSONObject();

            int responseCode = conn.getResponseCode();
            if (responseCode == 400 || responseCode == 401 || responseCode == 500 ) {
                System.out.println(responseCode + " Error!");
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                responseJson = new JSONObject(sb.toString());

                //통신
                JSONObject headObject = (JSONObject) responseJson.get("head");
                JSONObject bodyObject = (JSONObject) responseJson.get("body");
                JSONArray storesObject = bodyObject.getJSONArray("stores");

                if(headObject.get("status").equals("200")){
                    //통신 200 OK!
                    noticeSB.append("<table style=\"border-collapse:collapse;border-spacing:0\" class=\"tg\">");
                    noticeSB.append("<tbody>");

                    for (int i = 0; i < storesObject.length(); i++) {
                        JSONObject jsonObject = storesObject.getJSONObject(i);
                        //매장명
                        String storeName = (String) jsonObject.get("storeName");
                        //해당 매장에 존재하는 상품들
                        JSONObject partsAvailability = (JSONObject) jsonObject.get("partsAvailability");
                        /**
                         * 예진 상품
                         */
                        //상품 MKLW3KH/A 41mm   // Test-> MKQA3KH/A
                        JSONObject product1 = (JSONObject) partsAvailability.get("MKLW3KH/A");
                        //상품명
                        String product1_storePickupProductTitle = (String) product1.get("storePickupProductTitle");
                        //상품 옵션 :  ML873FE/A =  올리브 그레이/카고 카키
                        String product1_ctoOptions = (String) product1.get("ctoOptions");
                        //상품 재고
                        Boolean product1_storeSelectionEnabled = (Boolean) product1.get("storeSelectionEnabled");
                        /**
                         * 재현 상품
                         */
                        //상품 : MKMT3KH/A 45mm  // Test -> MKTG3KH/A
                        JSONObject product2 = (JSONObject) partsAvailability.get("MKMT3KH/A");
                        //상품명
                        String product2_storePickupProductTitle = (String) product2.get("storePickupProductTitle");
                        //상품 옵션 : ML8D3FE/A =  올리브 그레이/카고 카키
                        String product2_ctoOptions = (String) product2.get("ctoOptions");
                        //상품 재고
                        Boolean product2_storeSelectionEnabled = (Boolean) product2.get("storeSelectionEnabled");
                        /**
                         * 현수 상품
                         */
                        //상품 : MKNA3KH/A 45mm
                        JSONObject product3 = (JSONObject) partsAvailability.get("MKNA3KH/A");
                        //상품명
                        String product3_storePickupProductTitle = (String) product3.get("storePickupProductTitle");
                        //상품 재고
                        Boolean product3_storeSelectionEnabled = (Boolean) product3.get("storeSelectionEnabled");


                        //픽업 재고가 있는 경우에만 셋팅
                        if(product1_storeSelectionEnabled || product2_storeSelectionEnabled || product3_storeSelectionEnabled) {
/*

                            noticeSB.append("[매장]  " +  storeName +System.lineSeparator());
                            noticeSB.append("[상품명]  " + product1_storePickupProductTitle + " " +  (product1_ctoOptions.equals("ML873FE/A") ? "올리브 그레이/카고 카키": product1_ctoOptions) +System.lineSeparator());
                            noticeSB.append("[픽업 재고] "+ (product1_storeSelectionEnabled == true ? "O":"X") +System.lineSeparator());
                            noticeSB.append("[상품명] " + product2_storePickupProductTitle + " " + (product2_ctoOptions.equals("ML8D3FE/A") ? "올리브 그레이/카고 카키" : product2_ctoOptions) +System.lineSeparator());
                            noticeSB.append("[픽업 재고] " + (product2_storeSelectionEnabled == true ? "O":"X") +System.lineSeparator() +System.lineSeparator());
                            noticeSB.append(System.lineSeparator());
*/
/*
                            noticeSB.append("<h1> 매장  " +  storeName +"</h1>");
                            noticeSB.append("<h3>[예진]  " + product1_storePickupProductTitle + " " +  (product1_ctoOptions.equals("ML873FE/A") ? "올리브 그레이/카고 카키": product1_ctoOptions) +"<br>");
                            noticeSB.append("[픽업 가능 여부]  "+ (product1_storeSelectionEnabled == true ? "O":"X") +"</<h3>");
                            noticeSB.append("<h3>[재현] " + product2_storePickupProductTitle + " " + (product2_ctoOptions.equals("ML8D3FE/A") ? "올리브 그레이/카고 카키" : product2_ctoOptions) +"<br>");
                            noticeSB.append("[픽업 가능 여부]  " + (product2_storeSelectionEnabled == true ? "O":"X") +"</h3>");
                            noticeSB.append("<h3>[현수] " + product3_storePickupProductTitle + "<br>");
                            noticeSB.append("[픽업 가능 여부]  " + (product3_storeSelectionEnabled == true ? "O":"X") +"</h3>");
                            noticeSB.append("<br><br>");

 */
                            /**
                             * 예진 - product1
                             * 재현 - product2
                             * 현수 - product3
                             */
                            noticeSB.append("<tr>");
                            noticeSB.append("<th style=\"background-color:#fffc9e;border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:28px;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:center;vertical-align:top;word-break:normal\" colspan=\"3\">"+storeName+"</th>");
                            noticeSB.append("</tr>");
                            noticeSB.append("<tr>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:center;vertical-align:top;word-break:normal\">이름</td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:center;vertical-align:top;word-break:normal\">상품명</td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:center;vertical-align:top;word-break:normal\">픽업 재고 여부</td>");
                            noticeSB.append("</tr>");
                            noticeSB.append("<tr>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:left;vertical-align:top;word-break:normal\">예진</td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:left;vertical-align:top;word-break:normal\">" +
                                    "<a href=\"https://www.apple.com/kr/shop/buy-watch/apple-watch-nike/41mm-cellular-%EC%8A%A4%ED%83%80%EB%9D%BC%EC%9D%B4%ED%8A%B8-%EC%95%8C%EB%A3%A8%EB%AF%B8%EB%8A%84-%EC%98%AC%EB%A6%AC%EB%B8%8C-%EA%B7%B8%EB%A0%88%EC%9D%B4-%EC%B9%B4%EA%B3%A0-%EC%B9%B4%ED%82%A4-%EC%8A%A4%ED%8F%AC%EC%B8%A0-%EB%B0%B4%EB%93%9C-onesize\" target='_blank'>" + product1_storePickupProductTitle + " " +  (product1_ctoOptions.equals("ML873FE/A") ? "올리브 그레이/카고 카키": product1_ctoOptions) +"</a></td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:center;vertical-align:top;word-break:normal\">"+ (product1_storeSelectionEnabled == true ? "O":"X") +"</td>");
                            noticeSB.append("</tr>");
                            noticeSB.append("<tr>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:left;vertical-align:top;word-break:normal\">재현</td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:left;vertical-align:top;word-break:normal\">"+
                                    "<a href=\"https://www.apple.com/kr/shop/buy-watch/apple-watch-nike/45mm-cellular-%EC%8A%A4%ED%83%80%EB%9D%BC%EC%9D%B4%ED%8A%B8-%EC%95%8C%EB%A3%A8%EB%AF%B8%EB%8A%84-%EC%98%AC%EB%A6%AC%EB%B8%8C-%EA%B7%B8%EB%A0%88%EC%9D%B4-%EC%B9%B4%EA%B3%A0-%EC%B9%B4%ED%82%A4-%EC%8A%A4%ED%8F%AC%EC%B8%A0-%EB%B0%B4%EB%93%9C-onesize\" target='_blank'>"+ product2_storePickupProductTitle + " " + (product2_ctoOptions.equals("ML8D3FE/A") ? "올리브 그레이/카고 카키" : product2_ctoOptions) +"</a></td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:center;vertical-align:top;word-break:normal\">"+ (product2_storeSelectionEnabled == true ? "O":"X") +"</td>");
                            noticeSB.append("</tr>");
                            noticeSB.append("<tr>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:left;vertical-align:top;word-break:normal\">현수</td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:left;vertical-align:top;word-break:normal\">" +
                                    "<a href=\"https://www.apple.com/kr/shop/buy-watch/apple-watch-nike/45mm-gps-%EC%8A%A4%ED%83%80%EB%9D%BC%EC%9D%B4%ED%8A%B8-%EC%95%8C%EB%A3%A8%EB%AF%B8%EB%8A%84-%ED%93%A8%EC%96%B4-%ED%94%8C%EB%9E%98%ED%8B%B0%EB%84%98-%EB%B8%94%EB%9E%99-%EC%8A%A4%ED%8F%AC%EC%B8%A0-%EB%B0%B4%EB%93%9C-onesize\" target='_blank'>"+ product3_storePickupProductTitle +"</a></td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:center;vertical-align:top;word-break:normal\">"+ (product3_storeSelectionEnabled == true ? "O":"X") +"</td>");
                            noticeSB.append("</tr>");

                            isSend = true;
                        }

                    }

                    noticeSB.append("</tbody>");
                    noticeSB.append("</table>");
                }
                if(noticeSB.toString().length() > 0 && isSend){
                    /**
                     *텔레그램 
                     */
                    //funcTelegram(noticeSB.toString());
                    /**
                     *메일
                     */
                    mailSend(noticeSB.toString());
                    noticeSB.setLength(0);
                    isSend = false;
                }else {
                    SimpleDateFormat format2 = new SimpleDateFormat ( "yyyy년 MM월dd일 HH시mm분ss초");
                    Date time = new Date();
                    String nowTime = format2.format(time);
                    System.out.println(nowTime+" => [재고 없음]");
                }


            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            System.out.println("not JSON Format response");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void funcTelegram(String message){
        String token = "2002093648:AAHbzGDAOULge7CeS7Ostl6r6f3E2PCItpE";
        String chat_id = "1766085447";
        String text = message ;
        String url = "https://api.telegram.org/bot" + token + "/sendMessage";


        BufferedReader in = null;

        try {

            TelegramMessage telegramMessage = new TelegramMessage(chat_id,text);
            String param = new Gson().toJson(telegramMessage);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<String> entity = new HttpEntity<>(param, headers);
            restTemplate.postForEntity(url, entity, String.class);

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(in != null) try { in.close(); } catch(Exception e) { e.printStackTrace(); }
        }
    }
}
