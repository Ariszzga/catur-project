package tampilan;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.text.JTextComponent;

public final class Tema {
    private Tema() { }

    public static final Color GELAP = new Color(0x4A3F36);
    public static final Color GELAP_PEKAT = new Color(0x2E2621);
    public static final Color TERANG = new Color(0xC9B99A);
    public static final Color KRIM = new Color(0xF5EFE0);
    public static final Color AKSEN = new Color(0xE8892F);
    public static final Color AKSEN_HOVER = new Color(0xF4A04F);
    public static final Color TERANG_HOVER = new Color(0xDACDB2);
    public static final Color ERROR = new Color(0xFF8A80);
    public static final Color SUKSES = new Color(0x9CCC65);

    public static Font font(int gaya, int ukuran) {
        return new Font("SansSerif", gaya, ukuran);
    }

    private static Graphics2D halus(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g;
    }

    public static JLabel label(String teks) {
        JLabel l = new JLabel(teks);
        l.setForeground(TERANG);
        l.setFont(font(Font.BOLD, 12));
        return l;
    }

    public static JPanel header(String judul, String sub) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel ikon = new JLabel("♚");
        ikon.setFont(font(Font.PLAIN, 56));
        ikon.setForeground(AKSEN);
        ikon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel j = new JLabel(judul);
        j.setFont(font(Font.BOLD, 30));
        j.setForeground(KRIM);
        j.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel s = new JLabel(sub);
        s.setFont(font(Font.PLAIN, 13));
        s.setForeground(TERANG);
        s.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(ikon);
        p.add(j);
        p.add(Box.createVerticalStrut(2));
        p.add(s);
        return p;
    }

    // Latar: pola papan catur yang digelapkan
    public static class Latar extends JPanel {
        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            int u = 70;
            for (int r = 0; r * u < getHeight(); r++) {
                for (int c = 0; c * u < getWidth(); c++) {
                    g.setColor((r + c) % 2 == 0 ? TERANG : GELAP);
                    g.fillRect(c * u, r * u, u, u);
                }
            }
            g.setPaint(new GradientPaint(0, 0, new Color(46, 38, 33, 225),
                                         0, getHeight(), new Color(46, 38, 33, 245)));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.dispose();
        }
    }

    //Kartu: sudut sangat bulat dan bayangan 
    public static class Kartu extends JPanel {
        public Kartu() {
            setOpaque(false);
            setBorder(new EmptyBorder(32, 42, 40, 48));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = halus(g0);
            int w = getWidth() - 6, h = getHeight() - 8, a = 44;
            g.setColor(new Color(0, 0, 0, 90));
            g.fillRoundRect(6, 8, w, h, a, a);
            g.setColor(GELAP);
            g.fillRoundRect(0, 0, w, h, a, a);
            g.setColor(TERANG);
            g.setStroke(new BasicStroke(1.5f));
            g.drawRoundRect(0, 0, w - 1, h - 1, a, a);
            g.dispose();
        }
    }

    //Kolom isian berbentuk kapsul dan placeholder
    private static void lukisKolom(Graphics g0, JComponent c) {
        Graphics2D g = halus(g0);
        int a = c.getHeight();
        g.setColor(KRIM);
        g.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), a, a);
        if (c.hasFocus()) {
            g.setColor(AKSEN);
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(1, 1, c.getWidth() - 3, c.getHeight() - 3, a, a);
        }
        g.dispose();
    }

    private static void lukisHint(Graphics g0, JTextComponent t, String hint) {
        if (hint == null || hint.isEmpty() || !t.getText().isEmpty()) return;
        Graphics2D g = halus(g0);
        g.setColor(new Color(74, 63, 54, 120));
        g.setFont(t.getFont());
        FontMetrics fm = g.getFontMetrics();
        g.drawString(hint, t.getInsets().left,
                (t.getHeight() + fm.getAscent() - fm.getDescent()) / 2);
        g.dispose();
    }

    private static void gayaKolom(JTextField f) {
        f.setOpaque(false);
        f.setBorder(new EmptyBorder(11, 20, 11, 20));
        f.setForeground(GELAP_PEKAT);
        f.setCaretColor(AKSEN);
        f.setSelectionColor(AKSEN);
        f.setSelectedTextColor(Color.WHITE);
        f.setFont(font(Font.PLAIN, 14));
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { f.repaint(); }
            @Override public void focusLost(FocusEvent e) { f.repaint(); }
        });
    }

    public static class Teks extends JTextField {
        private final String hint;

        public Teks(int kolom) { this(kolom, ""); }

        public Teks(int kolom, String hint) {
            super(kolom);
            this.hint = hint;
            gayaKolom(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            lukisKolom(g, this);
            super.paintComponent(g);
            lukisHint(g, this, hint);
        }
    }

    public static class Sandi extends JPasswordField {
        private final String hint;

        public Sandi(int kolom) { this(kolom, ""); }

        public Sandi(int kolom, String hint) {
            super(kolom);
            this.hint = hint;
            gayaKolom(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            lukisKolom(g, this);
            super.paintComponent(g);
            lukisHint(g, this, hint);
        }
    }

    // ---------- Kolom alamat bersudut bulat ----------
    public static class Alamat extends JPanel {
        public final JTextArea ta = new JTextArea(3, 16);

        public Alamat() {
            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(12, 18, 12, 18));
            ta.setOpaque(false);
            ta.setBorder(null);
            ta.setLineWrap(true);
            ta.setWrapStyleWord(true);
            ta.setForeground(GELAP_PEKAT);
            ta.setCaretColor(AKSEN);
            ta.setSelectionColor(AKSEN);
            ta.setSelectedTextColor(Color.WHITE);
            ta.setFont(font(Font.PLAIN, 14));
            ta.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { repaint(); }
                @Override public void focusLost(FocusEvent e) { repaint(); }
            });
            add(ta);
        }

        public String getText() { return ta.getText(); }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = halus(g0);
            int a = 30;
            g.setColor(KRIM);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), a, a);
            if (ta.hasFocus()) {
                g.setColor(AKSEN);
                g.setStroke(new BasicStroke(2f));
                g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, a, a);
            }
            g.dispose();
            super.paintComponent(g0);
        }
    }

    //Dropdown berbentuk kapsul
    public static class Pilihan<T> extends JComboBox<T> {
        public Pilihan() { super(); mulai(); }
        public Pilihan(T[] isi) { super(isi); mulai(); }

        private void mulai() {
            setUI(new BasicComboBoxUI() {
                @Override
                protected JButton createArrowButton() {
                    JButton b = new JButton("▾");
                    b.setBorder(new EmptyBorder(0, 0, 0, 10));
                    b.setContentAreaFilled(false);
                    b.setFocusPainted(false);
                    b.setOpaque(false);
                    b.setForeground(GELAP);
                    return b;
                }

                @Override
                public void paintCurrentValueBackground(Graphics g, Rectangle r, boolean f) { }
            });
            setOpaque(false);
            setBorder(new EmptyBorder(4, 10, 4, 2));
            setFont(font(Font.PLAIN, 13));
            setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> l, Object v, int i,
                                                              boolean sel, boolean foc) {
                    super.getListCellRendererComponent(l, v, i, sel, false);
                    setBorder(new EmptyBorder(5, 12, 5, 12));
                    setFont(font(Font.PLAIN, 13));
                    if (i == -1) {
                        setOpaque(false);
                        setForeground(GELAP_PEKAT);
                    } else {
                        setOpaque(true);
                        setBackground(sel ? AKSEN : KRIM);
                        setForeground(sel ? Color.WHITE : GELAP_PEKAT);
                    }
                    return this;
                }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.height = Math.max(d.height, 42);
            return d;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = halus(g0);
            g.setColor(KRIM);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g.dispose();
            super.paintComponent(g0);
        }
    }

    //Tombol pilihan berbentuk pil (untuk jenis kelamin)
    public static class Pil extends JToggleButton {
        private boolean diAtas = false;

        public Pil(String teks) {
            super(teks);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setFont(font(Font.BOLD, 13));
            setBorder(new EmptyBorder(10, 20, 10, 20));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { diAtas = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { diAtas = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = halus(g0);
            int a = getHeight();
            if (isSelected()) {
                g.setColor(AKSEN);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), a, a);
                setForeground(Color.WHITE);
            } else {
                g.setColor(diAtas ? new Color(255, 255, 255, 40) : new Color(255, 255, 255, 18));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), a, a);
                g.setColor(TERANG);
                g.setStroke(new BasicStroke(1.5f));
                g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, a, a);
                setForeground(KRIM);
            }
            g.dispose();
            super.paintComponent(g0);
        }
    }

    //Tombol kapsul dengan efek hover
    public static class Tombol extends JButton {
        private final Color dasar, hover;
        private boolean diAtas = false;

        public Tombol(String teks, Color dasar, Color hover, Color warnaTeks) {
            super(teks);
            this.dasar = dasar;
            this.hover = hover;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setForeground(warnaTeks);
            setFont(font(Font.BOLD, 14));
            setBorder(new EmptyBorder(13, 20, 13, 20));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { diAtas = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { diAtas = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = halus(g0);
            g.setColor(diAtas ? hover : dasar);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g.dispose();
            super.paintComponent(g0);
        }
    }

    public static Tombol tombolUtama(String teks) {
        return new Tombol(teks, AKSEN, AKSEN_HOVER, Color.WHITE);
    }

    public static Tombol tombolSekunder(String teks) {
        return new Tombol(teks, TERANG, TERANG_HOVER, GELAP_PEKAT);
    }

    public static void tampilkanStatus(JLabel l, String teks, boolean sukses, int lebar) {
        l.setForeground(sukses ? SUKSES : ERROR);
        l.setText("<html><div style='text-align:center;width:" + lebar + "px'>" + teks + "</div></html>");
    }
}