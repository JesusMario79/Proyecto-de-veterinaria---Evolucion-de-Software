package com.veterinaria.veterinariaapp.ui;

import com.veterinaria.veterinariaapp.security.SessionManager;
import com.veterinaria.veterinariaapp.model.Usuario; // Necesario para obtener rol

// --- ¡IMPORTS PARA EL ENSAMBLAJE COMPLETO! ---
// Repositorios (Interfaces e Implementaciones)
import com.veterinaria.veterinariaapp.repository.ICitaRepository;
import com.veterinaria.veterinariaapp.repository.CitaRepository;
import com.veterinaria.veterinariaapp.repository.IClienteRepository;
import com.veterinaria.veterinariaapp.repository.ClienteRepository;
import com.veterinaria.veterinariaapp.repository.IMascotaRepository;
import com.veterinaria.veterinariaapp.repository.MascotaRepository;
import com.veterinaria.veterinariaapp.repository.IUsuarioRepository;
import com.veterinaria.veterinariaapp.repository.UsuarioRepository;
// Servicios
import com.veterinaria.veterinariaapp.service.AuthService; // Necesario para logout
import com.veterinaria.veterinariaapp.service.CitaService;
import com.veterinaria.veterinariaapp.service.ClienteService;
import com.veterinaria.veterinariaapp.service.MascotaService;
import com.veterinaria.veterinariaapp.service.UserService;
// --- IMPORTS PARA OCP ---
import com.veterinaria.veterinariaapp.security.IPermisosRol;
import com.veterinaria.veterinariaapp.security.PermisosAdmin;
import com.veterinaria.veterinariaapp.security.PermisosRecepcionista;
import com.veterinaria.veterinariaapp.security.PermisosVeterinario;
// --- FIN IMPORTS ---

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainWindow extends JFrame {

    // ===== KPIs demo =====
    private int kpiCitas = 3, kpiMascotas = 15, kpiClientes = 10;
    private Map<String,Integer> citasPorDia = Map.of("26/3",1,"27/3",1,"28/3",1);
    private Map<String,Integer> especies = new LinkedHashMap<>() {{
        put("Perro",40); put("Gato",25); put("Conejo",15); put("Hamster",10); put("Pájaro",10);
    }};

    // Card central
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);

    // --- ¡INSTANCIAS DE SERVICIOS Y REPOSITORIOS CREADAS UNA VEZ! ---
    private final ICitaRepository citaRepo = new CitaRepository();
    private final IClienteRepository clienteRepo = new ClienteRepository();
    private final IMascotaRepository mascotaRepo = new MascotaRepository();
    private final IUsuarioRepository usuarioRepo = new UsuarioRepository();

    private final CitaService citaService = new CitaService(citaRepo, mascotaRepo);
    private final ClienteService clienteService = new ClienteService(clienteRepo);
    private final MascotaService mascotaService = new MascotaService(mascotaRepo, clienteRepo);
    private final UserService userService = new UserService(usuarioRepo);
    // AuthService también necesita el repo para el logout -> re-login
    private final AuthService authService = new AuthService(usuarioRepo);


    // --- Variables de instancia para Vistas/Paneles ---
    private CitasViewForm citasView;
    private ClienteViewForm clienteView;
    private MascotaViewForm mascotaView;
    private UsuariosPanel usuariosPanel;

    // --- Variables de instancia para Menús y Botones (NECESARIO PARA OCP) ---
    private JMenuItem miClientes, miMascotas, miCitas, miUsuarios;
    private JButton btnUsuarios, btnCitas, btnClientes, btnMascotas;
    // ------------------------------------------------------------------------

    public MainWindow() {
        setTitle("Veterinario — Panel Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- CONSTRUCCIÓN DE UI ---
        // Construir menús y sidebar ANTES de aplicar permisos
        setJMenuBar(buildMenuBar()); // Asigna a variables mi...
        add(buildSidebar(), BorderLayout.WEST); // Asigna a variables btn...

        // --- CONSTRUCCIÓN DE PANELES ---
        content.add(buildDashboard(), "dashboard");
        // Creamos los paneles/vistas inyectando los servicios
        usuariosPanel = new UsuariosPanel(userService);
        clienteView = new ClienteViewForm(clienteService);
        mascotaView = new MascotaViewForm(mascotaService);
        citasView = new CitasViewForm(citaService);
        // Añadimos al CardLayout (usando los wrappers donde sea necesario para JFrames embebidos)
        content.add(usuariosPanel, "usuarios"); // UsuariosPanel es un JPanel
        content.add(clienteView.getContentPane(), "clientes"); // Obtenemos el contentPane del JFrame
        content.add(mascotaView.getContentPane(), "mascotas"); // Obtenemos el contentPane del JFrame
        content.add(citasView.getContentPane(), "citas");       // Obtenemos el contentPane del JFrame
        add(content, BorderLayout.CENTER);

        // --- APLICAR PERMISOS (OCP) ---
        aplicarPermisosSegunRol(); // Llama al método que aplica OCP

        cards.show(content, "dashboard"); // Muestra el panel inicial
    }

    // ==== MENÚ SUPERIOR ====
    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu mArchivo = new JMenu("Archivo");
        JMenuItem miCerrarSesion = new JMenuItem("Cerrar Sesión");
        JMenuItem miSalir        = new JMenuItem("Salir");
        mArchivo.add(miCerrarSesion);
        mArchivo.add(miSalir);

        JMenu mModulos = new JMenu("Módulos");
        // Asigna a las variables de instancia
        miClientes = new JMenuItem("Clientes");
        miMascotas = new JMenuItem("Mascotas");
        miCitas    = new JMenuItem("Citas");
        miUsuarios = new JMenuItem("Usuarios");
        mModulos.add(miClientes);
        mModulos.add(miMascotas);
        mModulos.add(miCitas);
        mModulos.add(miUsuarios);

        bar.add(mArchivo);
        bar.add(mModulos);

        // Acciones
        miCerrarSesion.addActionListener(e -> {
            SessionManager.get().logout();
            JOptionPane.showMessageDialog(this, "Sesión cerrada.");
            // Re-ensambla LoginFrame correctamente
            new LoginFrame(authService).setVisible(true); // Usa el authService de la instancia
            dispose();
        });
        miSalir.addActionListener(e -> System.exit(0));

        // Los listeners ahora solo muestran el panel; los permisos ya se aplicaron
        miClientes.addActionListener(e -> { cards.show(content, "clientes"); if (clienteView != null) clienteView.recargarTabla(); });
        miMascotas.addActionListener(e -> { cards.show(content, "mascotas"); if (mascotaView != null) mascotaView.recargarTabla(); });
        miCitas.addActionListener(e -> { cards.show(content, "citas"); if (citasView != null) citasView.recargarTabla(); });
        miUsuarios.addActionListener(e -> { cards.show(content, "usuarios"); if (usuariosPanel != null) usuariosPanel.cargarUsuarios(); });

        // Atajos
        miCerrarSesion.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        miSalir.setAccelerator        (KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_DOWN_MASK));

        return bar;
    }

    // ==== MENÚ LATERAL ====
    private JComponent buildSidebar() {
        JPanel side = new JPanel(new BorderLayout());
        side.setPreferredSize(new Dimension(200, getHeight()));
        side.setBackground(new Color(225,239,255));

        JLabel hola = new JLabel("  Bienvenido, " +
             (SessionManager.get().isAuthenticated() ? SessionManager.get().getCurrentUser().getNombre() : "Usuario"),
             SwingConstants.LEFT);
        hola.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        hola.setFont(hola.getFont().deriveFont(Font.BOLD, 13f));
        side.add(hola, BorderLayout.NORTH);

        JPanel menu = new JPanel(new GridLayout(0,1,0,6));
        menu.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JButton btnInicio = mkNav("Inicio");
        // Asigna a las variables de instancia
        btnUsuarios = mkNav("Usuarios");
        btnCitas    = mkNav("Citas");
        btnClientes = mkNav("Clientes");
        btnMascotas = mkNav("Mascotas");
        JButton btnCerrar = mkNav("Cerrar sesión");

        menu.add(btnInicio);
        menu.add(btnUsuarios);
        menu.add(btnCitas);
        menu.add(btnClientes);
        menu.add(btnMascotas);

        side.add(menu, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        south.add(btnCerrar, BorderLayout.SOUTH);
        side.add(south, BorderLayout.SOUTH);

        // Acciones
        btnInicio.addActionListener(e -> cards.show(content, "dashboard"));
        btnUsuarios.addActionListener(e -> { cards.show(content, "usuarios"); if (usuariosPanel != null) usuariosPanel.cargarUsuarios(); });
        btnClientes.addActionListener(e -> { cards.show(content, "clientes"); if (clienteView != null) clienteView.recargarTabla(); });
        btnCitas.addActionListener(e -> { cards.show(content, "citas"); if (citasView != null) citasView.recargarTabla(); });
        btnMascotas.addActionListener(e -> { cards.show(content, "mascotas"); if (mascotaView != null) mascotaView.recargarTabla(); });
        btnCerrar.addActionListener(e -> {
            SessionManager.get().logout();
            JOptionPane.showMessageDialog(this, "Sesión cerrada.");
            // Re-ensambla LoginFrame correctamente
            new LoginFrame(authService).setVisible(true); // Usa el authService de la instancia
            dispose();
        });

        return side;
    }

    // Método mkNav no cambia
    private JButton mkNav(String text){
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(Color.WHITE);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210,225,245)),
                BorderFactory.createEmptyBorder(10,10,10,10)
        ));
        return b;
    }

    // ==== Dashboard y métodos de ayuda (kpiCard, chartPanel) no cambian ====
    private JComponent buildDashboard() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        JLabel title = new JLabel("Panel Administrativo");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setBorder(BorderFactory.createEmptyBorder(0,0,12,0));
        root.add(title, BorderLayout.NORTH);
        JPanel kpis = new JPanel(new GridLayout(1,3,12,12));
        kpis.add(kpiCard("Citas", kpiCitas));
        kpis.add(kpiCard("Mascotas", kpiMascotas));
        kpis.add(kpiCard("Clientes", kpiClientes));
        JPanel charts = new JPanel(new GridLayout(1,2,12,12));
        charts.setBorder(BorderFactory.createEmptyBorder(12,0,0,0));
        charts.add(chartPanel("Citas por día", new BarChartPanel(citasPorDia)));
        charts.add(chartPanel("Distribución de especies", new PieChartPanel(especies)));
        JPanel center = new JPanel(new BorderLayout());
        center.add(kpis, BorderLayout.NORTH);
        center.add(charts, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);
        return root;
    }
    private JPanel kpiCard(String titulo, int valor){
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                BorderFactory.createEmptyBorder(16,16,16,16)
        ));
        JLabel t = new JLabel(titulo);
        t.setFont(t.getFont().deriveFont(14f));
        JLabel v = new JLabel(String.valueOf(valor), SwingConstants.CENTER);
        v.setFont(v.getFont().deriveFont(Font.BOLD, 28f));
        p.add(t, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }
    private JPanel chartPanel(String titulo, JComponent chart){
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                BorderFactory.createEmptyBorder(12,12,12,12)
        ));
        JLabel t = new JLabel(titulo);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 14f));
        t.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));
        panel.add(t, BorderLayout.NORTH);
        panel.add(chart, BorderLayout.CENTER);
        return panel;
    }


    // ==== Clases de Gráficos no cambian ====
    static class BarChartPanel extends JPanel {
        private final Map<String,Integer> data;
        BarChartPanel(Map<String,Integer> data){ this.data=data; setPreferredSize(new Dimension(420,260)); }
        @Override protected void paintComponent(Graphics g){ /* ... (código igual) ... */
             super.paintComponent(g); Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
             int w=getWidth(), h=getHeight(), m=40, axisY=h-m, axisX=m; g2.setColor(new Color(200,200,200)); g2.drawLine(axisX, axisY, w-m, axisY); g2.drawLine(axisX, m/2, axisX, axisY);
             int max=data.values().stream().mapToInt(i->i).max().orElse(1); int n=Math.max(data.size(),1), bw=Math.max((w-m*2)/(n*2),20), i=0;
             for(var e: data.entrySet()){ int x=axisX+10+i*(bw*2); int barH=(int)((h-m*1.5)*(e.getValue()/(double)max)); int y=axisY-barH; g2.setColor(new Color(180,225,230)); g2.fillRoundRect(x,y,bw,barH,8,8); g2.setColor(Color.DARK_GRAY); g2.drawString(e.getKey(), x, axisY+15); i++; } g2.dispose();
        }
    }
    static class PieChartPanel extends JPanel {
        private final Map<String,Integer> data;
        PieChartPanel(Map<String,Integer> data){ this.data=data; setPreferredSize(new Dimension(420,260)); }
        @Override protected void paintComponent(Graphics g){ /* ... (código igual) ... */
             super.paintComponent(g); Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
             int w=getWidth(), h=getHeight(), size=Math.min(w,h)-40, x=(w-size)/2, y=(h-size)/2; int total=data.values().stream().mapToInt(i->i).sum(); if(total==0){g2.dispose(); return;}
             Color[] colors={ new Color(242,99,123), new Color(90,155,212), new Color(255,191,71), new Color(123,201,82), new Color(142,120,220), new Color(255,120,80) }; int start=0, idx=0;
             for(var e: data.entrySet()){ int angle=(int)Math.round(360.0*e.getValue()/total); g2.setColor(colors[idx++%colors.length]); g2.fillArc(x,y,size,size,start,angle); start+=angle; } g2.dispose();
        }
    }

    // --- MÉTODO PARA APLICAR PERMISOS (OCP) ---
    private void aplicarPermisosSegunRol() {
        Usuario usuarioLogueado = SessionManager.get().getCurrentUser();
        if (usuarioLogueado == null) {
            System.err.println("Error Crítico: Intentando aplicar permisos sin usuario logueado en MainWindow.");
            // Forzar cierre de sesión y volver al login
            JOptionPane.showMessageDialog(this, "Error de sesión. Por favor, inicie sesión de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
            new LoginFrame(authService).setVisible(true); // Usa el authService de la instancia
            dispose();
            return;
        }

        // Obtener la estrategia de permisos correcta usando la fábrica
        // Asegúrate de que tu modelo Usuario tenga getRolId()
        IPermisosRol permisos = obtenerEstrategiaPermisos(usuarioLogueado.getRolId());

        // Delegar la configuración a la estrategia (¡Aquí está OCP!)
        permisos.configurarPermisos(this);
    }

    // --- FÁBRICA SIMPLE PARA OBTENER LA ESTRATEGIA ---
    private IPermisosRol obtenerEstrategiaPermisos(int rolId) {
        // IDs asumidos: 1=Admin, 2=Veterinario, 3=Recepcionista
        // ¡Verifica que coincidan con tu BD!
        switch (rolId) {
            case 1:
                System.out.println("Aplicando permisos de Admin..."); // Log para depuración
                return new PermisosAdmin();
            case 2:
                System.out.println("Aplicando permisos de Veterinario..."); // Log para depuración
                return new PermisosVeterinario();
            case 3:
                 System.out.println("Aplicando permisos de Recepcionista..."); // Log para depuración
                return new PermisosRecepcionista();
            default:
                System.err.println("Advertencia: Rol de usuario desconocido (ID=" + rolId + "). Aplicando permisos de Recepcionista por defecto.");
                return new PermisosRecepcionista(); // Permisos mínimos por defecto
        }
    }

    // --- GETTERS PARA QUE LAS CLASES DE PERMISOS ACCEDAN A LOS COMPONENTES ---
    public JMenuItem getMiClientes() { return miClientes; }
    public JMenuItem getMiMascotas() { return miMascotas; }
    public JMenuItem getMiCitas() { return miCitas; }
    public JMenuItem getMiUsuarios() { return miUsuarios; }
    public JButton getBtnClientes() { return btnClientes; }
    public JButton getBtnMascotas() { return btnMascotas; }
    public JButton getBtnCitas() { return btnCitas; }
    public JButton getBtnUsuarios() { return btnUsuarios; }
    // --- FIN GETTERS ---

    // ==== Main (no cambia) ====
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) { e.printStackTrace(); }
        // ¡OJO! El main de MainWindow no debería ser el punto de entrada principal
        // si tienes un LoginFrame. El punto de entrada debería ser el main de LoginFrame.
        // Este main es útil para probar MainWindow directamente (quizás con un usuario dummy).
        // Si quieres probarlo directo, necesitarías simular un login:
        // SessionManager.get().login(new Usuario(1, "Admin Test", "admin@test.com", "hash", true, 1, "ADMIN")); // Simula login
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}