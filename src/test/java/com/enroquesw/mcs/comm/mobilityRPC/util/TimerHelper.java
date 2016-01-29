package com.enroquesw.mcs.comm.mobilityRPC.util;

import java.text.NumberFormat;

/**
 * La clase utilitaria <code>TimerHelper</code> es para imprimar el tiempo entre dos intantes de tiempo
 */
public class TimerHelper {
    private static NumberFormat instanceNumber;
    long timeInitial;
    long timeFinal;
    Time_M type;
    private long time;

    public long getTime() {
        return Time_M.MILIS.equals(type) ? System.currentTimeMillis() : System.nanoTime();
    }

    public enum Time_M{ NANOS, MILIS; }
    static {
        instanceNumber = NumberFormat.getInstance();
        instanceNumber.setMinimumFractionDigits(16);
    }

    /**
     * Marca el Inicio de la cuenta
     */
    public TimerHelper setIni(Time_M type) {
        this.type = type;
        timeInitial = getTime();
        return this;
    }

    /**
     * Marca el final y imprime el tiempo transcurrido
     * @param s mensaje personal
     */
    public void getOutTime(String s/*, Logger log*/)  {
        //timeFinal = System.nanoTime();
        timeFinal = getTime();
        //System.out.println("fin ; ini  = " + timeFinal + ";" + timeInitial);
        long rest = (timeFinal - timeInitial);
        //System.out.println("fin-ini ; " + (rest));
        double pow = Math.pow(10.0, Time_M.MILIS.equals(type) ? -3 : -9);
        //System.out.println("Math.pow(10.0, "+(Time_M.MILIS.equals(type) ? -3 : -9)+") ; "+ pow);
        double v = rest * pow;
        //System.out.println("rest * pow ; "+ v);
        System.out.println("Cronos " + s + ".seg = " + instanceNumber.format(v));
    }
}
