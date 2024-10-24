public class Hilo1 extends Thread{
    private int noGoles;
    private String equipo;

    public Hilo1(String equipo, int noGoles){
        this.equipo = equipo;
        this.noGoles = noGoles;
    }

    public void run(){
        for(int i=1; i <= noGoles; i++){
            System.out.println("Gol numero "+ i + " del equipo "+ equipo);
            try{
                Thread.sleep(1500);
            }catch(Exception err){
                System.out.println(err);
            }
        }
        System.out.println("Equipo Ganadaor "+ equipo + "!!!");
    }
    public static void main(String[] args) {
        Hilo1 equipo1 = new Hilo1("Barcelona", 5);
        Hilo1 equipo2 = new Hilo1("Real Madrid", 5);
        equipo1.start();
        equipo2.start();
    }
}
