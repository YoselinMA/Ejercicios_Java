import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

// Clase Corredor que implementa Runnable para ser ejecutada en un hilo
class Corredor implements Runnable {
    private String nombre;
    private int distanciaMeta;
    private JPanel panelCarrera;
    private static AtomicBoolean hayGanador = new AtomicBoolean(false);//se utiliza para controlar si ya hay un ganador en la carrera y al ser estática, se comparte entre todos los corredores 
    private JLabel etiquetaGanador;
    private int x, y;
    private Image imagen;
    private Random random;
    private AtomicBoolean pausado;
    private final Lock lock;

    // Constructor para inicializar los atributos del corredor
    public Corredor(String nombre, int distanciaMeta, JPanel panelCarrera, JLabel etiquetaGanador, int y, Image imagen) {
        this.nombre = nombre;
        this.distanciaMeta = distanciaMeta;
        this.panelCarrera = panelCarrera;
        this.etiquetaGanador = etiquetaGanador;
        this.x = 0;
        this.y = y;
        this.imagen = imagen;
        this.random = new Random();
        this.pausado = new AtomicBoolean(false);
        this.lock = new ReentrantLock();
    }

    @Override
    public void run() {
        // Bucle para simular la carrera
        while (x < distanciaMeta && !hayGanador.get()) {
            try {
                // Pausa la ejecución del hilo si está pausado
                synchronized (pausado) {
                    while (pausado.get()) {
                        pausado.wait();
                    }
                }

                // Genera un avance aleatorio y actualiza la posición del corredor
                int avance = random.nextInt(10) + 1;
                x += avance;
                panelCarrera.repaint();

                // Pausa la ejecución del hilo por 100 milisegundos
                Thread.sleep(100);

                // Si el corredor llega a la meta y aún no hay ganador, se establece como ganador
                if (x >= distanciaMeta) {
                    if (hayGanador.compareAndSet(false, true)) {
                        // Usar JOptionPane para mostrar un mensaje emergente con el ganador
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(panelCarrera, nombre + " ha llegado a la meta y es el ganador!"));
                    }
                }
            } catch (InterruptedException e) {
                System.out.println(nombre + " ha sido interrumpido.");
            }
        }
    }

    // Método para pausar el corredor
    public void pausar() {
        pausado.set(true);
    }

    // Método para reanudar el corredor
    public void reanudar() {
        synchronized (pausado) {
            pausado.set(false);
            pausado.notify();
        }
    }

    // Método para dibujar la imagen del corredor en el panel
    public void dibujar(Graphics g) {
        g.drawImage(imagen, x, y, null);
    }
}

// Clase principal CarreraGUI que extiende JFrame para la interfaz gráfica
public class CarreraGUI extends JFrame {
    private JPanel panelCarrera;
    private JLabel etiquetaGanador;
    private Image fondoCarrera;
    private JButton botonIniciar;
    private JButton botonPausar;
    private JButton botonReanudar;
    private Corredor[] corredores;
    private Thread[] hilosCorredores;

    public CarreraGUI() {
        setTitle("Navidad en Julio :)"); // Título de la ventana
        setSize(800, 400); // Tamaño de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Operación al cerrar la ventana
        setLayout(new BorderLayout()); // Layout de la ventana

        // Cargar la imagen de fondo
        fondoCarrera = new ImageIcon("fondo1.png").getImage();

        // Panel personalizado para la carrera
        panelCarrera = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Dibujar la imagen de fondo
                g.drawImage(fondoCarrera, 0, 0, getWidth(), getHeight(), this);
                // Dibujar la línea de meta
                g.setColor(Color.black);
                g.drawLine(750, 0, 750, getHeight());
                // Dibujar a los corredores
                for (Corredor corredor : corredores) {
                    corredor.dibujar(g);
                }
            }
        };
        panelCarrera.setBackground(Color.white);

        // Etiqueta para mostrar el estado de la carrera
        etiquetaGanador = new JLabel("Que gane el mejor...", SwingConstants.CENTER);
        etiquetaGanador.setFont(new Font("Serif", Font.BOLD, 20));

        // Crear los botones y añadir sus listeners
        botonIniciar = new JButton("Iniciar");
        botonIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarCarrera();
            }
        });

        botonPausar = new JButton("Pausar");
        botonPausar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pausarCarrera();
            }
        });

        botonReanudar = new JButton("Reanudar");
        botonReanudar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reanudarCarrera();
            }
        });

        // Panel para los botones
        JPanel panelBotones = new JPanel();
        panelBotones.add(botonIniciar);
        panelBotones.add(botonPausar);
        panelBotones.add(botonReanudar);

        // Añadir el panel de carrera, la etiqueta de ganador y el panel de botones al JFrame
        add(panelCarrera, BorderLayout.CENTER);
        add(etiquetaGanador, BorderLayout.SOUTH);
        add(panelBotones, BorderLayout.NORTH);

        setVisible(true);
    }

    // Método para inicializar y empezar la carrera
    private void iniciarCarrera() {
        int distanciaMeta = 750; // Distancia en píxeles
        corredores = new Corredor[3];

        // imágenes de los corredores
        Image corredor1 = new ImageIcon("muneco1.png").getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH);
        Image corredor2 = new ImageIcon("muneco2.png").getImage().getScaledInstance(70, 50, Image.SCALE_SMOOTH);
        Image corredor3 = new ImageIcon("muneco3.png").getImage().getScaledInstance(80, 50, Image.SCALE_SMOOTH);

        // Crear instancias de Corredor con sus respectivas imágenes y posiciones iniciales
        corredores[0] = new Corredor("Olaf", distanciaMeta, panelCarrera, etiquetaGanador, 50, corredor1);
        corredores[1] = new Corredor("Stolas", distanciaMeta, panelCarrera, etiquetaGanador, 150, corredor2);
        corredores[2] = new Corredor("Mia", distanciaMeta, panelCarrera, etiquetaGanador, 250, corredor3);

        //  hilos de los corredores
        hilosCorredores = new Thread[corredores.length];
        for (int i = 0; i < corredores.length; i++) {
            hilosCorredores[i] = new Thread(corredores[i]);
            hilosCorredores[i].start();
        }
    }

    // Método para pausar la carrera
    private void pausarCarrera() {
        for (Corredor corredor : corredores) {
            corredor.pausar();//se llama la accion que ace el metodo pausar
        }
    }

    // Método para reanudar la carrera
    private void reanudarCarrera() {
        for (Corredor corredor : corredores) {
            corredor.reanudar();
        }
    }

    // Método principal para ejecutar la aplicación
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CarreraGUI::new);
    }
}
