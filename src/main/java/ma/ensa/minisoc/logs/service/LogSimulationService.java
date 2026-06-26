package ma.ensa.minisoc.logs.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogSimulationService {
    @Autowired
    private TcpServer tcpServer;

    private Thread simulationThread;
    private Thread trafficThread;
    private TrafficSimulator trafficSimulator;
    @Getter
    private boolean running = false;

    public synchronized void startSimulation() {
        if (running) return;

        simulationThread = new Thread(() -> {
            try {
                tcpServer.start(5050); // Démarrage du serveur TCP
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        simulationThread.start();

        trafficSimulator = new TrafficSimulator();

        trafficThread = new Thread(() -> {
            try {
                Thread.sleep(2000);
                trafficSimulator.start("localhost", 5050);
            } catch (InterruptedException e) {
                System.out.println("Traffic thread interrupted.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        trafficThread.start();

        running = true;
    }

    public synchronized void stopSimulation() {
        if (!running) return;

        // Stopper le traffic
        if (trafficSimulator != null) {
            trafficSimulator.stop(); // <-- stoppe proprement la boucle
        }

        if (simulationThread != null && simulationThread.isAlive()) {
            simulationThread.interrupt(); // Provoque une interruption si le thread le gère
            trafficThread.interrupt();
        }

        if (trafficThread != null && trafficThread.isAlive()) {
            trafficThread.interrupt(); // interrompt les sleep() si actif
        }

        if (simulationThread != null && simulationThread.isAlive()) {
            simulationThread.interrupt();

            tcpServer.stop(); // Ajoute cette méthode dans TcpServer pour libérer le port proprement

            running = false;
        }
    }
}

