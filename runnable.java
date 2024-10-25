public class runnable implements Runnable{
    private String corredor;
    private int nokm;
    public runnable(String corredor, int nokm){
        this.corredor = corredor;
        this.nokm = nokm;
    }
    public void run(){
        for(int i=1; i<=nokm; i++){
            System.out.println(corredor+" alcanzó "+i+" Km recorridos");
            try{
                Thread.sleep(1500);
            }catch(Exception err){
                System.out.println(err);
            }

        }
    }

    public static void main(String args []){
        Thread corredor1 =  new Thread(new runnable("Yos", 10));
        Thread corredor2 =  new Thread(new runnable("Pedrito", 10));
        Thread corredor3 =  new Thread(new runnable("Fabi", 10));
        corredor1.start();
        corredor2.start();
        corredor3.start();
    }
    
}