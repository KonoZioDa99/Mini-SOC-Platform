package ma.ensa.minisoc.logs.service;


import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.time.LocalDateTime;  
import java.time.format.DateTimeFormatter;

public class TrafficSimulator {

    private static final List<String> failedSSH = Arrays.asList( 

        " sshd[2371]: Failed password for root from 192.168.1.100 port 53412 ssh2",  
        " sshd[2371]: Failed password for root from 192.168.1.100 port 53412 ssh2",  
        " sshd[2372]: Failed password for invalid user admin from 192.168.1.100 port 53413 ssh2",   
        " sshd[2450]: Failed password for invalid user hacker from 192.168.1.100 port 42222 ssh2",  
        " sshd[2451]: Failed password for root from 192.168.1.100 port 35555 ssh2",  
        " sshd[2452]: Failed password for invalid user test from 192.168.1.100 port 48888 ssh2",  
        " sshd[2453]: Failed password for admin from 192.168.1.100 port 51111 ssh2",  
        " sshd[2454]: Failed password for root from 192.168.1.100 port 37777 ssh2"  
    );    
    

    private static final List<String> failedFTP = Arrays.asList(

        " ftpd: LOGIN FAILED FROM 10.0.0.15, USER anonymous",
        " ftpd: LOGIN FAILED FROM 10.0.0.15, USER admin",
        " ftpd: LOGIN FAILED FROM 10.0.0.15, USER root",
        " ftpd: LOGIN FAILED FROM 10.0.0.15, USER guest",
        " ftpd: LOGIN FAILED FROM 10.0.0.15, USER test",
        " ftpd: LOGIN FAILED FROM 10.0.0.15, USER backup",
        " ftpd: LOGIN FAILED FROM 10.0.0.15, USER administrator"

    );

    private static final List<String> nmapScan = Arrays.asList(
       
        " nmap: SYN Stealth Scan from 185.143.223.17 probing port 22 (SSH)",
        " nmap: OS Detection Scan from 91.218.114.209 probing port 80 (HTTP)",
        " nmap: NULL Scan from 45.227.253.109 probing ports 21-1000 (FTP/HTTP range)",
        " nmap: XMAS Scan from 62.210.180.42 probing port 443 (HTTPS)",
        " nmap: UDP Scan from 198.54.132.166 probing port 161 (SNMP)",
        " nmap: Version Detection Scan from 77.247.108.73 probing port 3389 (RDP)",
        " nmap: Aggressive Scan from 172.104.12.89 probing ports 22,80,443,3306 (Common services)",
        " nmap: Fragmented Scan from 5.188.206.184 (--mtu 24)",
        " nmap: Malformed Packet Scan from 93.174.95.106 with invalid TCP checksum"
    );



    private static final List<String> networkErrors = Arrays.asList(

        " kernel: [TCP] Connection reset by peer (src: 192.168.1.15:54322 dst: 10.0.0.3:443)",
        " icmp: Destination unreachable (Network unreachable) from 192.168.1.10 to 203.0.113.42",
        " firewalld: DROPPED (IN=eth0 OUT=) TCP packet from 203.0.113.1:6667 to 10.1.1.5:22",
        " kernel: [SYN Flood] Possible SYN flood attack on port 80. Rate: 1500 pps",
        " dhcpd: DHCPREQUEST for 192.168.1.100 from aa:bb:cc:dd:ee:ff via eth0: No free leases",
        " ntpd: Can't synchronise: no reachable servers (stratum 16)",
        " eth0: Link down (carrier lost)",
        " switchd: Port 7 on switch1 error: CRC errors (count: 1245)",
        " wlan0: Authentication timeout for client aa:bb:cc:11:22:33",
        " haproxy: [ALERT] Too many connections (maxconn=1000 reached)",
        " postfix: Connection timed out to gmail-smtp-in.l.google.com[142.250.101.26]:25",
        " openvpn: TLS handshake failed: TLS key negotiation failed",
        " ids: [ET SCAN] NMAP OS Detection attempt from 45.33.12.154",
        " arpwatch: Flip-flop detected: aa:bb:cc:dd:ee:ff 192.168.1.100 (old) -> 192.168.1.150 (new)"
    );


    // Méthode utilitaire pour générer le timestamp  
    private static String getTimestamp() {  
    return "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "]";  
    }

    private volatile boolean running = true;
    private static final Random random = new Random();

    public void start(String host, int port) {
        try (
            Socket socket = new Socket(host, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            while (running) {

                // 1. Choisir une catégorie aléatoire
            	int category = random.nextInt(5);

                switch (category) {
                    case 0 -> sendOneOrAll(out, failedSSH);
                    case 1 -> sendOneOrAll(out, failedFTP);
                    case 3 -> sendOneOrThree(out, nmapScan);
                    default -> sendOneOrThree(out, networkErrors);
                }

                Thread.sleep(3000); // Pause entre catégories
            }
        } catch (Exception e) {
            System.out.println("Sortie de sleep().");
        }
        System.out.println("TrafficSimulator arrêté.");
    }
    public void stop() {
        running = false;
    }

    private void sendOneOrAll(PrintWriter out, List<String> payloads) throws InterruptedException {
        boolean sendAll = random.nextBoolean();  // true ou false aléatoire
        if (sendAll) {
            for (String raw : payloads) {
                String payload = getTimestamp() + " " + raw;
                out.println(payload);
                System.out.println(" Envoyé : " + payload);
                Thread.sleep(1000);
            }
        } else {
            String raw = payloads.get(random.nextInt(payloads.size()));
            String payload = getTimestamp() + " " + raw;
            out.println(payload);
            System.out.println(" Envoyé : " + payload);
            Thread.sleep(1000);
        }
    }

    private void sendOneOrThree(PrintWriter out, List<String> payloads) throws InterruptedException {
        int count = random.nextBoolean() ? 1 : 3; // soit 1 soit 3 logs
        for (int i = 0; i < count; i++) {
            String raw = payloads.get(random.nextInt(payloads.size()));
            String payload = getTimestamp() + " " + raw;
            out.println(payload);
            System.out.println(" Envoyé : " + payload);
            Thread.sleep(1000);
        }
    }

}
