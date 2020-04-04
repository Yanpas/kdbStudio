package studio.kdb;

import studio.core.Credentials;
import studio.core.DefaultAuthenticationMechanism;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.awt.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Config {
    public static String imageBase = "/de/skelton/images/";
    public static String imageBase2 = "/de/skelton/utils/";

    private static String PATH = System.getProperties().getProperty("user.home") + "/.studioforkdb/";
    private static String FILENAME = PATH + "studio.properties";
    private static String VERSION = "1.1";

    private Properties p = new Properties();
    private final static Config instance = new Config();

    private Config() {
        init();
    }

    public Font getFont() {
        String name = p.getProperty("font.name", "Monospaced");
        int  size = Integer.parseInt(p.getProperty("font.size","14"));

        Font f = new Font(name, Font.PLAIN, size);
        setFont(f);

        return f;
    }

    public String getEncoding() {
        return p.getProperty("encoding", "UTF-8");
    }

    public void setFont(Font f) {
        p.setProperty("font.name", f.getFamily());
        p.setProperty("font.size", "" + f.getSize());
        save();
    }

    public Color getColorForToken(String tokenType, Color defaultColor) {
        String s = p.getProperty("token." + tokenType);
        if (s != null) {
            return new Color(Integer.parseInt(s, 16));
        }

        setColorForToken(tokenType, defaultColor);
        return defaultColor;
    }

    public void setColorForToken(String tokenType, Color c) {
        p.setProperty("token." + tokenType, Integer.toHexString(c.getRGB()).substring(2));
        save();
    }

    public Color getDefaultBackgroundColor() {
        return getColorForToken("BACKGROUND", Color.white);
    }

    public synchronized NumberFormat getNumberFormat() {
        String key = p.getProperty("DecimalFormat", "#.#######");

        return new DecimalFormat(key);
    }

    public static Config getInstance() {
        return instance;
    }

    private void init() {
        Path file = Paths.get(FILENAME);
        Path dir = file.getParent();
        if (Files.notExists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                System.err.println("Can't create configuration folder: " + PATH);
            }
            return;
        }

        try {
            InputStream in = Files.newInputStream(file);
            p.load(in);
            in.close();
        } catch (IOException e) {
            System.err.println("Cant't read configuration from file " + FILENAME);
            e.printStackTrace(System.err);
        }
    }


    public void save() {
        try {
            OutputStream out = new FileOutputStream(FILENAME);
            p.put("version", VERSION);
            p.store(out, "Auto-generated by Studio for kdb+");
            out.close();
        } catch (IOException e) {
            System.err.println("Can't save configuration to " + FILENAME);
            e.printStackTrace(System.err);  //To change body of catch statement use Options | File Templates.
        }
    }

    // "".split(",") return {""}; we need to get zero length array
    private String[] split(String str) {
        str = str.trim();
        if (str.length() == 0) return new String[0];
        return str.split(",");
    }

    public String[] getQKeywords() {
        String key = p.getProperty("qkeywords", "");
        return split(key);
    }

    public String getLRUServer() {
        return p.getProperty("lruServer");
    }

    public void setLRUServer(Server s) {
        if (s == null) return; // May be it should be an exception ?

        p.put("lruServer", s.getName());
        save();
    }


    public void saveQKeywords(String[] keywords) {
        p.put("qkeywords", String.join(",",keywords));
        save();
    }

    public void setAcceptedLicense(Date d) {
        p.put("licenseAccepted", d.toString());
        save();
    }

    public int getOffset(Server server) {
        if (server == null) return -1; // or an exception should be raised?

        return Arrays.asList(getServers()).indexOf(server);
    }

    public String[] getMRUFiles() {
        String mru = p.getProperty("mrufiles", "");
        return split(mru);
    }


    public void saveMRUFiles(String[] mruFiles) {
        String value = Stream.of(mruFiles).limit(9).collect(Collectors.joining(","));
        p.put("mrufiles", value);
        save();
    }

    public String getLookAndFeel() {
        return p.getProperty("lookandfeel");
    }

    public void setLookAndFeel(String lf) {
        p.put("lookandfeel", lf);
        save();
    }

    public List<String> getServerNames() {
        return Arrays.asList(split(p.getProperty("Servers", "")));
    }

    private void setServerNames(List<String> names) {
        p.setProperty("Servers", String.join(",",names));
        save();
    }

    public Server[] getServers() {
        return getServerNames().stream()
                .map(name->getServer(name))
                .toArray(Server[]::new);
    }

    public Server getServer(String name) {
        String host = p.getProperty("server." + name + ".host");
        int port = Integer.parseInt(p.getProperty("server." + name + ".port", "-1"));
        String username = p.getProperty("server." + name + ".user");
        String password = p.getProperty("server." + name + ".password");
        String backgroundColor = p.getProperty("server." + name + ".backgroundColor", "FFFFFF");
        String authenticationMechanism = p.getProperty("server." + name + ".authenticationMechanism", DefaultAuthenticationMechanism.NAME);
        boolean useTLS = Boolean.parseBoolean(p.getProperty("server." + name + ".useTLS", "false"));
        Color c = new Color(Integer.parseInt(backgroundColor, 16));
        return new Server(name, host, port, username, password, c, authenticationMechanism, useTLS);

    }

    public void removeServer(Server server) {
        String name = server.getName();
        p.remove("server." + name + ".host");
        p.remove("server." + name + ".port");
        p.remove("server." + name + ".user");
        p.remove("server." + name + ".password");
        p.remove("server." + name + ".backgroundColor");
        p.remove("server." + name + ".authenticationMechanism");
        p.remove("server." + name + ".useTLS");

        List<String> list = getServerNames();
        list.remove(name);
        setServerNames(list);
    }

    private void setServerDetails(Server server) {
        String name = server.getName();
        if (name.trim().length() == 0) {
            throw new IllegalArgumentException("Server name can't be empty");
        }
        if (name.contains(",")) {
            throw new IllegalArgumentException("Server name can't contains ,");
        }
        p.setProperty("server." + name + ".host", server.getHost());
        p.setProperty("server." + name + ".port", "" + server.getPort());
        p.setProperty("server." + name + ".user", "" + server.getUsername());
        p.setProperty("server." + name + ".password", "" + server.getPassword());
        p.setProperty("server." + name + ".backgroundColor", "" + Integer.toHexString(server.getBackgroundColor().getRGB()).substring(2));
        p.setProperty("server." + name + ".authenticationMechanism", server.getAuthenticationMechanism());
        p.setProperty("server." + name + ".useTLS", "" + server.getUseTLS());
    }

    public void addServer(Server server) {
        setServerDetails(server);

        String name = server.getName();
        List<String> list = Stream.of(getServers()).map(s->s.getName()).collect(Collectors.toList());
        if (! list.contains(name)) {
            list.add(name);
        }
        Collections.sort(list);
        p.setProperty("Servers",String.join(",", list));
        save();
    }

    public void setServers(Server[] servers) {
        Stream.of(servers).forEach(server -> setServerDetails(server));
        setServerNames(Stream.of(servers).map(s->s.getName()).collect(Collectors.toList()));
    }

    public Credentials getDefaultCredentials(String authenticationMechanism) {
        String user = p.getProperty("auth." + authenticationMechanism + ".user", "");
        String password = p.getProperty("auth." + authenticationMechanism + ".password", "");
        return new Credentials(user, password);
    }

    public void setDefaultCredentials(String authenticationMechanism, Credentials credentials) {
        p.setProperty("auth." + authenticationMechanism + ".user", credentials.getUsername());
        p.setProperty("auth." + authenticationMechanism + ".password", credentials.getPassword());
        save();
    }

    public String getDefaultAuthMechanism() {
        return p.getProperty("auth", DefaultAuthenticationMechanism.NAME);
    }

    public void setDefaultAuthMechanism(String authMechanism) {
        p.setProperty("auth", authMechanism);
        save();
    }
}
