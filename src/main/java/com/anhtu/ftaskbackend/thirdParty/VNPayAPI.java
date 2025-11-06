package com.anhtu.ftaskbackend.thirdParty;

import com.anhtu.ftaskbackend.dto.request.Wallet.AdjustWalletBalanceRequest;
import com.anhtu.ftaskbackend.dto.request.transaction.TransactionParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class VNPayAPI {

    @Value("${vnpay.code}")
    private String tmnCode;

    @Value("${vnpay.hash_secret}")
    private String hashSecret;

    @Value("${vnpay.url}")
    private String payUrl;

    @Value("${vnpay.return}")
    private String returnUrl;

    public Map<String, Object> createPayment(Double amount, String info, String type) {
        try {
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_TxnRef = getRandomNumber();
            String vnp_IpAddr = "127.0.0.1";

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", tmnCode);

            long amountInVND = Math.round(amount * 100);
            vnp_Params.put("vnp_Amount", String.valueOf(amountInVND));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", info);
            vnp_Params.put("vnp_OrderType", type);
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", returnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));
            cld.add(Calendar.MINUTE, 15);
            vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

            // Sort keys alphabetically
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext();) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString());

                    hashData.append(fieldName).append('=').append(encodedValue);
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                            .append('=').append(encodedValue);

                    if (itr.hasNext()) {
                        hashData.append('&');
                        query.append('&');
                    }
                }
            }

            String vnp_SecureHash = Config.hmacSHA512(hashSecret, hashData.toString()).toLowerCase();
            query.append("&vnp_SecureHash=").append(vnp_SecureHash);

            String paymentUrl = payUrl + "?" + query;

            System.out.println("HASH DATA = " + hashData);
            System.out.println("SECURE HASH = " + vnp_SecureHash);
            System.out.println("PAYMENT URL = " + paymentUrl);

            Map<String, Object> result = new HashMap<>();
            result.put("code", "00");
            result.put("message", "success");
            result.put("paymentUrl", paymentUrl);
            return result;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", "99");
            error.put("message", "error: " + e.getMessage());
            return error;
        }
    }

    private String getRandomNumber() {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++)
            sb.append(rnd.nextInt(10));
        return sb.toString();
    }
}
