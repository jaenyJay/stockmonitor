package com.apple.stock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
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
    //private static final String HOST_URL = "https://www.apple.com/kr/shop/retail/pickup-message?pl=true&searchNearby=true&store=R692&parts.0=MKLW3KH/A&option.0=ML873FE%2FA&mt=regular&parts.1=MKMT3KH/A&option.1=ML8D3FE%2FA&mt=regular&parts.2=MKNA3KH/A&mt=regular&_=1633708471832";
    //private static final String HOST_URL = "https://www.apple.com/kr/shop/retail/pickup-message?pl=true&searchNearby=true&store=R692&parts.0=MKLW3KH/A&option.0=ML873FE%2FA&mt=regular&parts.1=MKMT3KH/A&option.1=ML8D3FE%2FA&mt=regular&parts.2=MKNA3KH/A&mt=regular&parts.3=MKJ33KH/A&mt=regular&parts.4=MKL43KH/A&mt=regular&_=1633708471832";
    private static final String HOST_URL = "https://www.apple.com/kr/shop/retail/pickup-message?pl=true&searchNearby=true&store=R692&parts.0=MKLW3KH/A&option.0=ML2W3FE%2FA&mt=regular&parts.1=MKMT3KH/A&option.1=ML373FE%2FA&mt=regular&parts.2=MKNA3KH/A&mt=regular&parts.3=MKJ33KH/A&mt=regular&parts.4=MKL43KH/A&mt=regular&_=1633708471832";


    @Test
    void contextLoads() {

    while(true){
        try {
            run();
            Thread.sleep(10000); //1??? ?????? 300 * 1000(1???) = 300000 (5???)
        }catch (Exception e){
            e.printStackTrace();
        }
    }
        

    }

    private void mailSend(String msg){
        final String bodyEncoding = "UTF-8"; //????????? ?????????

        String subject = "????????????7 ???????????? ??????";
        String fromEmail = "taylorror@gmail.com";
        String fromUsername = "?????????";
        String toEmail = "kokikiko@knou.ac.kr,ssamta@icloud.com,liz_y115@naver.com"; // ??????(,)??? ????????? ??????
        //String toEmail = "kokikiko@knou.ac.kr"; // ??????(,)??? ????????? ??????


        // ????????? ????????? ?????????
        String html = msg;

        // ?????? ?????? ??????
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

            // ?????? ?????? ??????
            Session session = Session.getInstance(props, auth);

            // ?????? ???/?????? ?????? ??????
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail, fromUsername));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            message.setSubject(subject);
            message.setSentDate(new Date());

            // ?????? ????????? ??????
            Multipart mParts = new MimeMultipart();
            MimeBodyPart mTextPart = new MimeBodyPart();
            MimeBodyPart mFilePart = null;

            // ?????? ????????? - ??????
            mTextPart.setText(html, bodyEncoding, "html");
            mParts.addBodyPart(mTextPart);

            // ?????? ????????? ??????
            message.setContent(mParts);

            // MIME ?????? ??????
            MailcapCommandMap MailcapCmdMap = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
            MailcapCmdMap.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
            MailcapCmdMap.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
            MailcapCmdMap.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
            MailcapCmdMap.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
            MailcapCmdMap.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
            CommandMap.setDefaultCommandMap(MailcapCmdMap);

            // ?????? ??????
            Transport.send( message );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    class MyAuthentication extends Authenticator {

        PasswordAuthentication pa;

        public MyAuthentication() {

            String id = "taylorror@gmail.com";       // ?????? ID
            String pw = "ipihstyzyurvstjy";          // ?????? ????????????

            // ID??? ??????????????? ????????????.
            pa = new PasswordAuthentication(id, pw);

        }

        // ??????????????? ???????????? ????????????
        public PasswordAuthentication getPasswordAuthentication() {
            return pa;
        }
    }
    private void run() {
        HttpURLConnection conn = null;
        JSONObject responseJson = null;
        StringBuilder sb = new StringBuilder();
        StringBuilder noticeSB = new StringBuilder();
        StringBuilder noticeTelegram = new StringBuilder();
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

                //??????
                JSONObject headObject = (JSONObject) responseJson.get("head");
                JSONObject bodyObject = (JSONObject) responseJson.get("body");
                JSONArray storesObject = bodyObject.getJSONArray("stores");

                if(headObject.get("status").equals("200")){
                    //?????? 200 OK!
                    noticeSB.append("<table style=\"border-collapse:collapse;border-spacing:0\" class=\"tg\">");
                    noticeSB.append("<tbody>");

                    for (int i = 0; i < storesObject.length(); i++) {
                        JSONObject jsonObject = storesObject.getJSONObject(i);
                        //?????????
                        String storeName = (String) jsonObject.get("storeName");
                        //?????? ????????? ???????????? ?????????
                        JSONObject partsAvailability = (JSONObject) jsonObject.get("partsAvailability");
                        /**
                         * ?????? ??????
                         */
                        //?????? MKLW3KH/A 41mm   // Test-> MKQA3KH/A
                        JSONObject product1 = (JSONObject) partsAvailability.get("MKLW3KH/A");
                        //?????????
                        String product1_storePickupProductTitle = (String) product1.get("storePickupProductTitle");
                        //?????? ?????? :  ML873FE/A =  ????????? ?????????/?????? ??????
                        //          ML2W3FE/A = ?????? ?????????
                        String product1_ctoOptions = (String) product1.get("ctoOptions");
                        if(product1_ctoOptions.equals("ML873FE/A")){
                            product1_ctoOptions = "????????? ?????????/?????? ??????";
                        }else if(product1_ctoOptions.equals("ML2W3FE/A")){
                            product1_ctoOptions = "?????? ?????????";
                        }
                        //?????? ??????
                        Boolean product1_storeSelectionEnabled = (Boolean) product1.get("storeSelectionEnabled");
                        /**
                         * ?????? ??????
                         */
                        //?????? : MKMT3KH/A 45mm  // Test -> MKTG3KH/A MKMT3KH
                        JSONObject product2 = (JSONObject) partsAvailability.get("MKMT3KH/A");
                        //?????????
                        String product2_storePickupProductTitle = (String) product2.get("storePickupProductTitle");
                        //?????? ?????? : ML8D3FE/A =  ????????? ?????????/?????? ??????
                        //         ML373FE/A = ?????? ?????????
                        String product2_ctoOptions = (String) product2.get("ctoOptions");
                        if(product2_ctoOptions.equals("ML8D3FE/A")){
                            product2_ctoOptions = "????????? ?????????/?????? ??????";
                        }else if(product2_ctoOptions.equals("ML373FE/A")){
                            product2_ctoOptions = "?????? ?????????";
                        }
                        //?????? ??????
                        Boolean product2_storeSelectionEnabled = (Boolean) product2.get("storeSelectionEnabled");
                        /**
                         * ?????? ??????
                         */
                        //?????? : MKNA3KH/A 45mm
                        JSONObject product3 = (JSONObject) partsAvailability.get("MKNA3KH/A");
                        //?????????
                        String product3_storePickupProductTitle = (String) product3.get("storePickupProductTitle");
                        //?????? ??????
                        Boolean product3_storeSelectionEnabled = (Boolean) product3.get("storeSelectionEnabled");

                        /**
                         *  ??????????????? ???????????? ????????? 41 + ?????????, ????????? Nike ????????? ??????
                         */
                        //?????? : MKJ33KH/A 45mm
                        JSONObject product4 = (JSONObject) partsAvailability.get("MKJ33KH/A");
                        //?????????
                        String product4_storePickupProductTitle = (String) product4.get("storePickupProductTitle");
                        //?????? ??????
                        Boolean product4_storeSelectionEnabled = (Boolean) product4.get("storeSelectionEnabled");
                        /**
                         *  ??????????????? ???????????? ????????? 45 + ?????????, ????????? Nike ????????? ??????
                         */
                        //?????? : MKL43KH/A 45mm
                        JSONObject product5 = (JSONObject) partsAvailability.get("MKL43KH/A");
                        //?????????
                        String product5_storePickupProductTitle = (String) product5.get("storePickupProductTitle");
                        //?????? ??????
                        Boolean product5_storeSelectionEnabled = (Boolean) product5.get("storeSelectionEnabled");

                        //?????? ????????? ?????? ???????????? ??????
                        if(
                                   product1_storeSelectionEnabled
                                || product2_storeSelectionEnabled
                                || product3_storeSelectionEnabled
                                || product4_storeSelectionEnabled
                                || product5_storeSelectionEnabled
                        ) {

                            /**
                             * ????????????
                             */
                            noticeTelegram.append("[??????]  " +  storeName +System.lineSeparator());

                            noticeTelegram.append("[?????????]  " + product1_storePickupProductTitle + " " +  product1_ctoOptions +System.lineSeparator());
                            noticeTelegram.append("[?????? ??????] "+ (product1_storeSelectionEnabled == true ? "O":"X") +System.lineSeparator() +System.lineSeparator());

                            noticeTelegram.append("[?????????] " + product2_storePickupProductTitle + " " +  product2_ctoOptions +System.lineSeparator());
                            noticeTelegram.append("[?????? ??????] " + (product2_storeSelectionEnabled == true ? "O":"X") +System.lineSeparator() +System.lineSeparator());

                            noticeTelegram.append("[?????????] " + product3_storePickupProductTitle+System.lineSeparator());
                            noticeTelegram.append("[?????? ??????] " + (product3_storeSelectionEnabled == true ? "O":"X") +System.lineSeparator() +System.lineSeparator());

                            noticeTelegram.append("[?????????] " + product4_storePickupProductTitle+System.lineSeparator());
                            noticeTelegram.append("[?????? ??????] " + (product4_storeSelectionEnabled == true ? "O":"X") +System.lineSeparator() +System.lineSeparator());

                            noticeTelegram.append("[?????????] " + product5_storePickupProductTitle+System.lineSeparator());
                            noticeTelegram.append("[?????? ??????] " + (product5_storeSelectionEnabled == true ? "O":"X") +System.lineSeparator() +System.lineSeparator());
                            noticeTelegram.append(System.lineSeparator());

                            /**
                             * ?????? - product1
                             * ?????? - product2
                             * ?????? - product3
                             */
                            noticeSB.append("<tr>");
                            noticeSB.append("<th style=\"background-color:#fffc9e;border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:28px;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:center;vertical-align:top;word-break:normal\" colspan=\"3\">"+storeName+"</th>");
                            noticeSB.append("</tr>");
                            noticeSB.append("<tr>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:center;vertical-align:top;word-break:normal\">??????</td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:center;vertical-align:top;word-break:normal\">?????????</td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:center;vertical-align:top;word-break:normal\">?????? ?????? ??????</td>");
                            noticeSB.append("</tr>");
                            noticeSB.append("<tr>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:left;vertical-align:top;word-break:normal\">??????</td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:left;vertical-align:top;word-break:normal\">" +
                                    "<a href=\"https://www.apple.com/kr/shop/buy-watch/apple-watch-nike/41mm-cellular-%EC%8A%A4%ED%83%80%EB%9D%BC%EC%9D%B4%ED%8A%B8-%EC%95%8C%EB%A3%A8%EB%AF%B8%EB%8A%84-%EC%84%9C%EB%B0%8B-%ED%99%94%EC%9D%B4%ED%8A%B8-%EC%8A%A4%ED%8F%AC%EC%B8%A0-%EB%A3%A8%ED%94%84-onesize\" target='_blank'>" + product1_storePickupProductTitle + " " + product1_ctoOptions +"</a></td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:center;vertical-align:top;word-break:normal\">"+ (product1_storeSelectionEnabled == true ? "O":"X") +"</td>");
                            noticeSB.append("</tr>");
                            noticeSB.append("<tr>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:left;vertical-align:top;word-break:normal\">??????</td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:left;vertical-align:top;word-break:normal\">"+
                                    "<a href=\"https://www.apple.com/kr/shop/buy-watch/apple-watch-nike/45mm-cellular-%EC%8A%A4%ED%83%80%EB%9D%BC%EC%9D%B4%ED%8A%B8-%EC%95%8C%EB%A3%A8%EB%AF%B8%EB%8A%84-%EC%84%9C%EB%B0%8B-%ED%99%94%EC%9D%B4%ED%8A%B8-%EC%8A%A4%ED%8F%AC%EC%B8%A0-%EB%A3%A8%ED%94%84-onesize\" target='_blank'>"+ product2_storePickupProductTitle + " " + product2_ctoOptions +"</a></td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:center;vertical-align:top;word-break:normal\">"+ (product2_storeSelectionEnabled == true ? "O":"X") +"</td>");
                            noticeSB.append("</tr>");
                            noticeSB.append("<tr>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:left;vertical-align:top;word-break:normal\">??????</td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:left;vertical-align:top;word-break:normal\">" +
                                    "<a href=\"https://www.apple.com/kr/shop/buy-watch/apple-watch-nike/45mm-gps-%EC%8A%A4%ED%83%80%EB%9D%BC%EC%9D%B4%ED%8A%B8-%EC%95%8C%EB%A3%A8%EB%AF%B8%EB%8A%84-%ED%93%A8%EC%96%B4-%ED%94%8C%EB%9E%98%ED%8B%B0%EB%84%98-%EB%B8%94%EB%9E%99-%EC%8A%A4%ED%8F%AC%EC%B8%A0-%EB%B0%B4%EB%93%9C-onesize\" target='_blank'>"+ product3_storePickupProductTitle +"</a></td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:center;vertical-align:top;word-break:normal\">"+ (product3_storeSelectionEnabled == true ? "O":"X") +"</td>");
                            noticeSB.append("</tr>");

                            noticeSB.append("<tr>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:left;vertical-align:top;word-break:normal\"></td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:left;vertical-align:top;word-break:normal\">" +
                                    "<a href=\"https://www.apple.com/kr/shop/buy-watch/apple-watch-nike/41mm-cellular-%EC%8A%A4%ED%83%80%EB%9D%BC%EC%9D%B4%ED%8A%B8-%EC%95%8C%EB%A3%A8%EB%AF%B8%EB%8A%84-%ED%93%A8%EC%96%B4-%ED%94%8C%EB%9E%98%ED%8B%B0%EB%84%98-%EB%B8%94%EB%9E%99-%EC%8A%A4%ED%8F%AC%EC%B8%A0-%EB%B0%B4%EB%93%9C-onesize\" target='_blank'>"+ product4_storePickupProductTitle +"</a></td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:center;vertical-align:top;word-break:normal\">"+ (product4_storeSelectionEnabled == true ? "O":"X") +"</td>");
                            noticeSB.append("</tr>");

                            noticeSB.append("<tr>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:left;vertical-align:top;word-break:normal\"></td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:left;vertical-align:top;word-break:normal\">" +
                                    "<a href=\"https://www.apple.com/kr/shop/buy-watch/apple-watch-nike/45mm-cellular-%EC%8A%A4%ED%83%80%EB%9D%BC%EC%9D%B4%ED%8A%B8-%EC%95%8C%EB%A3%A8%EB%AF%B8%EB%8A%84-%ED%93%A8%EC%96%B4-%ED%94%8C%EB%9E%98%ED%8B%B0%EB%84%98-%EB%B8%94%EB%9E%99-%EC%8A%A4%ED%8F%AC%EC%B8%A0-%EB%B0%B4%EB%93%9C-onesize\" target='_blank'>"+ product5_storePickupProductTitle +"</a></td>");
                            noticeSB.append("<td style=\"border-color:inherit;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:100%;font-weight:bold;overflow:hidden;padding:10px 20px;text-align:center;vertical-align:top;word-break:normal\">"+ (product5_storeSelectionEnabled == true ? "O":"X") +"</td>");
                            noticeSB.append("</tr>");
                            isSend = true;
                        }

                    }

                    noticeSB.append("</tbody>");
                    noticeSB.append("</table>");
                }
                if(noticeSB.toString().length() > 0 && isSend){
                    /**
                     *???????????? 
                     */
                    //funcTelegram(noticeSB.toString());
                    //noticeTelegram.setLength(0);
                    /**
                     *??????
                     */
                    mailSend(noticeSB.toString());
                    noticeSB.setLength(0);
                    isSend = false;

                    SimpleDateFormat format2 = new SimpleDateFormat ( "yyyy??? MM???dd??? HH???mm???ss???");
                    Date time = new Date();
                    String nowTime = format2.format(time);
                    System.out.println(nowTime+" => [?????? ??????]");
                }else {
                    SimpleDateFormat format2 = new SimpleDateFormat ( "yyyy??? MM???dd??? HH???mm???ss???");
                    Date time = new Date();
                    String nowTime = format2.format(time);
                    System.out.println(nowTime+" => [?????? ??????]");
                }

                funcTelegram(noticeTelegram.toString());
                noticeTelegram.setLength(0);

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
        String text =  message;
        String url = "https://api.telegram.org/bot" + token + "/sendMessage";

        //https://api.telegram.org/bot2002093648:AAHbzGDAOULge7CeS7Ostl6r6f3E2PCItpE/getUpdates


        BufferedReader in = null;
        try {
            if(!StringUtils.isEmpty(message)){
                TelegramMessage telegramMessage = new TelegramMessage(chat_id,text);
                String param = new Gson().toJson(telegramMessage);

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
                HttpEntity<String> entity = new HttpEntity<>(param, headers);
                restTemplate.postForEntity(url, entity, String.class);
            }

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(in != null) try { in.close(); } catch(Exception e) { e.printStackTrace(); }
        }
    }
}
