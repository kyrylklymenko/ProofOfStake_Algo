package com.example.demo.Service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

public class IpService extends Thread {
    private final String CENTRE_URL = "http://controlserver:8001";
    private final RestTemplate template = new RestTemplate();


    public static String getThisIp() {
        try {
            for (Enumeration<NetworkInterface> ipEnum = NetworkInterface.getNetworkInterfaces(); ipEnum.hasMoreElements(); ) {
                NetworkInterface interf = ipEnum.nextElement();

                for (Enumeration<InetAddress> enumIpAddr = interf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress thisAddress = enumIpAddr.nextElement();

                    if (!thisAddress.isLoopbackAddress() && thisAddress instanceof Inet4Address) {
                        return thisAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }


    private void sendIpAddress() throws InterruptedException {

        String ip = getThisIp();
        TimeUnit.SECONDS.sleep(2);
        while (true) {
            if (ip != null) {
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(CENTRE_URL + "/nodeIp").
                        queryParam("nodeIp", ip);

                try {
                    HttpEntity<String> response = template.exchange(builder.toUriString(), HttpMethod.POST, null, String.class);
                    System.out.println(response.getBody());
                } catch (Exception ignored) {

                }
            }
            TimeUnit.SECONDS.sleep(30);
        }
    }

    @Override
    public void run() {
        try {
            sendIpAddress();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
