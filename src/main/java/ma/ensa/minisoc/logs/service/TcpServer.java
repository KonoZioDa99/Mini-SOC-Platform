package ma.ensa.minisoc.logs.service;

import org.springframework.stereotype.Component;


import ma.ensa.minisoc.logs.model.LogEntity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TcpServer {

    // Les listes statiques(failedSSH, failedFTP, nmapScan, networkErrors)
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

    // Pattern regex pour extraire timestamp (ex: [31/05/2025 15:32:45])
    private static final Pattern timestampPattern = Pattern.compile("\\[(.*?)\\]");

    // Pattern regex pour extraire IP (simple IPv4)
    private static final Pattern ipPattern = Pattern.compile("(\\b\\d{1,3}(?:\\.\\d{1,3}){3}\\b)");

    private final LogService logService;
    private ServerSocket serverSocket;
    private boolean running;

    public TcpServer(LogService logService) {
        this.logService = logService;
        this.running = false;
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("✅ TCP Server started on port " + port);
            running = true;

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("📥 Connexion entrante : " + clientSocket.getInetAddress());
                    new Thread(() -> handleClient(clientSocket)).start();
                } catch (Exception e) {
                    if (!running) {
                        System.out.println("✅ Serveur arrêté.");
                    } else {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void handleClient(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                LogEntity entity = parseLog(line);
                if (entity != null) {
                    logService.save(entity);
                    System.out.println("📝 Log parsé reçu et sauvegardé : " + line);
                } else {
                    System.out.println("⚠ Impossible de parser la ligne : " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LogEntity parseLog(String line) {
        LogEntity entity = new LogEntity();

        // Extraire le timestamp
        Matcher tsMatcher = timestampPattern.matcher(line);
        if (tsMatcher.find()) {
            String timestampStr = tsMatcher.group(1); // ex: 31/05/2025 15:32:45

            // Conversion String -> Date
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            try {
                Date date = formatter.parse(timestampStr);
                entity.setTimestamp(date);
            } catch (ParseException e) {
                e.printStackTrace();
                entity.setTimestamp(new Date()); // par défaut, la date actuelle si erreur
            }
        } else {
            // Timestamp obligatoire, si absent on abandonne
            return null;
        }

        // Extraire l’IP
        Matcher ipMatcher = ipPattern.matcher(line);
        String ip = null;
        if (ipMatcher.find()) {
            ip = ipMatcher.group(1);
            entity.setSource(ip);
        } else {
            entity.setSource("unknown");
        }

        // Extraire message = ligne sans timestamp
        String message = line.replaceFirst("\\[.?\\]\\s", "");
        entity.setMessage(message);

        // Identifier le type selon la liste dont provient le message
        String type = detectType(message);
        entity.setType(type);

        return entity;
    }

    private String detectType(String message) {
        if (failedSSH.stream().anyMatch(message::contains)) {
            return "failedSSH";
        } else if (failedFTP.stream().anyMatch(message::contains)) {
            return "failedFTP";
        } else if (nmapScan.stream().anyMatch(message::contains)) {
            return "nmapScan";
        } else if (networkErrors.stream().anyMatch(message::contains)) {
            return "networkErrors";
        }
        return "unknown";
    }
}
