package view;

import controller.ThreadF1;

import java.util.concurrent.Semaphore;

public class Principal {
    public static void main(String[] args) {

        Semaphore semaforo = new Semaphore(5);
        int numEscuderia = 1;
        for (int i = 1; i <= 14; i++) {
            Thread t = new ThreadF1(semaforo, numEscuderia, i);
            t.start();
            numEscuderia++;
            if (numEscuderia > 7) {
                numEscuderia = 1;
            }
        }
    }
}
