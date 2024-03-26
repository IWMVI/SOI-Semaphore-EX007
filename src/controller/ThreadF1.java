package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class ThreadF1 extends Thread {

    private Semaphore semaforoEscuderia;
    private int escuderia;
    private int numeroCarro;
    private static List<Double> tempos;
    private static Map<Integer, Double> melhoresTempos;
    private static int carrosNaPista = 0;
    private static final int MAX_CARROS_PISTA = 5;

    public ThreadF1(Semaphore semaforoEscuderia, int escuderia, int numeroCarro) {
        this.semaforoEscuderia = semaforoEscuderia;
        this.escuderia = escuderia;
        this.numeroCarro = numeroCarro;
    }

    @Override
    public void run() {
        try {
            semaforoEscuderia.acquire();
            entrarNaPista();
            saida();
            volta();
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        } finally {
            sairDaPista();
            semaforoEscuderia.release();
            box();
        }
    }

    private void entrarNaPista() {
        synchronized (ThreadF1.class) {
            while (carrosNaPista >= MAX_CARROS_PISTA) {
                try {
                    ThreadF1.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            carrosNaPista++;
        }
    }

    private void sairDaPista() {
        synchronized (ThreadF1.class) {
            carrosNaPista--;
            ThreadF1.class.notifyAll();
        }
    }

    private void saida() {
        System.out.println("Piloto #" + numeroCarro + " da escuderia " + escuderia + " saiu dos boxes.");
    }

    private void volta() {
        double tempoMelhorVolta = Double.MAX_VALUE;
        for (int i = 0; i < 3; i++) {
            double tempo = (Math.random() * 10001) + 1000;
            try {
                sleep((long) tempo);
                System.out.println("Piloto #" + numeroCarro + " da escuderia " + escuderia + " realizou a volta em " + tempo + "s.");
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
            if (tempo < tempoMelhorVolta) {
                tempoMelhorVolta = tempo;
            }
            tempos.add(tempo);
        }
        synchronized (ThreadF1.class) {
            melhoresTempos.put(numeroCarro, tempoMelhorVolta);
        }
    }

    private void box() {
        System.out.println("Piloto #" + numeroCarro + " da escuderia " + escuderia + " voltou para os boxes.");
    }

    public static void mostrarGridLargada() {
        System.out.println("\nGrid de Largada:");

        // Criar uma cópia dos melhores tempos para evitar modificações na lista original
        Map<Integer, Double> copiaMelhoresTempos = new HashMap<>(melhoresTempos);

        // Ordenar a cópia dos melhores tempos pelos valores (tempos) de forma crescente
        List<Map.Entry<Integer, Double>> gridLargada = new ArrayList<>(copiaMelhoresTempos.entrySet());
        gridLargada.sort(Map.Entry.comparingByValue());

        // Mostrar o grid de largada
        int posicao = 1;
        for (Map.Entry<Integer, Double> entry : gridLargada) {
            System.out.println("Posição " + posicao + ": Piloto #" + entry.getKey() + " da escuderia " + entry.getValue() + " - Tempo: " + entry.getValue() + "s");
            posicao++;
        }
    }

    static {
        tempos = new ArrayList<>();
        melhoresTempos = new HashMap<>();
    }
}
