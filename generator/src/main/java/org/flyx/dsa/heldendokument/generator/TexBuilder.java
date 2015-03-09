package org.flyx.dsa.heldendokument.generator;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * @author flyx
 */
public class TexBuilder implements IBuilder {
    private static final String buildRoot;
    private static final String mansonKey = "MansonRegular";
    private static final String mansonBoldKey = "MansonBold";

    // configuration

    private IBuilderCallback callback;
    private String manson;
    private String mansonBold;


    private final Preferences preferences;

    static {
        final String os = System.getProperty("os.name").toUpperCase();
        String appDataFolder;
        if (os.contains("WIN")) {
            appDataFolder = System.getenv("APPDATA") + "/";
        } else if (os.contains("MAC")) {
            appDataFolder = System.getProperty("user.home") +
                    "/Library/Application Support/";
        } else if (os.contains("NUX")) {
            appDataFolder = System.getProperty("user.home") + "/.local/share/";
        } else {
            throw new RuntimeException("Betriebssystem nicht unterstützt: " + os);
        }
        buildRoot = appDataFolder + TexBuilder.class.getPackage().getName() + "/";
    }

    public TexBuilder() {
        final File buildDir = new File(buildRoot);
        if (!buildDir.exists()) {
            if (!buildDir.mkdirs()) {
                throw new RuntimeException("Konnte Verzeichnis nicht erstellen: " + buildRoot);
            }
        } else if (!buildDir.isDirectory()) {
            throw new RuntimeException("Pfad ist eine Datei, kein Verzeichnis: " + buildRoot);
        }

        preferences = Preferences.userNodeForPackage(TexBuilder.class);
        manson = preferences.get(mansonKey, null);
        mansonBold = preferences.get(mansonBoldKey, null);

        if (manson != null) {
            if (!new File(manson).isFile()) {
                manson = null;
            }
        }
        if (mansonBold != null) {
            if (!new File(mansonBold).isFile()) {
                mansonBold = null;
            }
        }
    }

    @Override
    public void setCallback(IBuilderCallback callback) {
        this.callback = callback;
    }

    @Override
    public List<AdditionalParameter> getAdditionalParameters() {
        return Arrays.asList(
                new AdditionalParameter(AdditionalParameter.Type.PATH, "MansonRegular.ttf", "Manson Schriftart (normal)"),
                new AdditionalParameter(AdditionalParameter.Type.PATH, "MansonBold.ttf", "Manson Schriftart (fett)")
        );
    }

    @Override
    public void setPathParameter(String name, String value) {
        if ("MansonRegular.ttf".equals(name)) {
            if (new File(value).isFile()) {
                manson = value;
                preferences.put(mansonKey, manson);
            } else {
                throw new RuntimeException("Kein gültiger Pfad: " + value);
            }
        } else if ("MansonBold.ttf".equals(name)) {
            if (new File(value).isFile()) {
                mansonBold = value;
                preferences.put(mansonBoldKey, manson);
            } else {
                throw new RuntimeException("Kein gültiger Pfad: " + value);
            }
        } else {
            throw new RuntimeException("Unknown parameter: " + name);
        }
    }

    @Override
    public boolean isParameterValid(String name) {
        if ("MansonRegular.ttf".equals(name)) {
            return manson != null;
        } else if ("MansonBold.ttf".equals(name)) {
            return mansonBold != null;
        } else {
            throw new RuntimeException("Unknown parameter: " + name);
        }
    }

    @Override
    public String getPathParameter(String name) {
        if ("MansonRegular.ttf".equals(name)) {
            return manson;
        } else if ("MansonBold.ttf".equals(name)) {
            return mansonBold;
        } else {
            throw new RuntimeException("Unknown parameter: " + name);
        }
    }

    @Override
    public boolean isEnvironmentValid(List<String> messages) {
        boolean foundErrors = false;
        for (Map.Entry<String, String> entry: new HashMap<String, String>() {{put("Vagrant", "vagrant"); put("VirtualBox", "VBoxManage");}}.entrySet()) {
            boolean available = false;
            try {
                Process vagrantProc = new ProcessBuilder(entry.getValue(), "-v").start();
                int ret = vagrantProc.waitFor();
                available = ret == 0;
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
            if (!available) {
                messages.add(entry.getKey() + " ist nicht auf deinem System installiert.");
                foundErrors = true;
            }
        }
        return !foundErrors;
    }

    @Override
    public void prepare() {
        final InputStream filesToCopy = getClass().getResourceAsStream("/resourceList.txt");
        final BufferedReader filesReader = new BufferedReader(new InputStreamReader(filesToCopy));

        try {
            String line;
            while ((line = filesReader.readLine()) != null) {
                final String targetPath = buildRoot + line;
                final String targetDirectory = targetPath.substring(0, targetPath.lastIndexOf('/'));
                final File targetDirectoryFile = new File(targetDirectory);
                if (!targetDirectoryFile.exists()) {
                    if (!targetDirectoryFile.mkdirs()) {
                        throw new RuntimeException("Konnte Ordner nicht anlegen: " + targetDirectory);
                    }
                } else if (targetDirectoryFile.isDirectory()) {
                    final File targetFile = new File(targetPath);
                    if (!targetFile.exists()) {
                        Files.copy(getClass().getResourceAsStream("/" + line), targetFile.toPath());
                    }
                }
            }
            final File mansonFile = new File(buildRoot + "DSA-Heldendokument/vagrant-vm/MansonRegular.ttf");
            final File mansonBoldFile = new File(buildRoot + "DSA-Heldendokument/vagrant-vm/MansonBold.ttf");

            if (!mansonFile.exists()) {
                Files.copy(new File(manson).toPath(), mansonFile.toPath());
            }
            if (!mansonBoldFile.exists()) {
                Files.copy(new File(mansonBold).toPath(), mansonBoldFile.toPath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Konnte eine Datei nicht anlegen.", e);
        }
    }

    private static enum VMState {NOT_CREATED, NOT_RUNNING, RUNNING, UNKNOWN};

    @Override
    public File build(DocumentConfiguration configuration) throws ExternalCallException {
        if (callback != null) {
            callback.nowDoing("Konfiguration wird geschrieben", "Die vorgenommenen Einstellungen werden in eine Datei geschrieben");
            callback.nowAt(10);
        }
        writeConfig(configuration);

        final VMState vmState = getVMState();
        switch (vmState) {
            case NOT_CREATED:
                if (callback != null) {
                    callback.nowDoing("Virtuelle Maschine wird erstellt", "Die Virtuelle Maschine wird erstellt. Dies kann eine Weile dauern und muss nur einmal gemacht werden.");
                    callback.nowAt(15);
                }
                vagrantup(callback != null);
                break;
            case NOT_RUNNING:
                if (callback != null) {
                    callback.nowDoing("Virtuelle Maschine wird hochgefahren", "Die Virtuelle Maschine existiert bereits und wird hochgefahren.");
                    callback.nowAt(60);
                }
                vagrantup(false);
                break;
            case RUNNING:
                break;
            case UNKNOWN:
                throw new RuntimeException("Der Status der VM ist unbekannt, breche ab.");
        }

        if (callback != null) {
            callback.nowDoing("PDF wird erstellt", "Das PDF wird nun erstellt.");
            callback.nowAt(85);
        }
        return createPDF();
    }

    @Override
    public void cleanup() {
        try {
            final VMState state = getVMState();
            if (VMState.RUNNING.equals(state)) {
                ProcessBuilder builder = new ProcessBuilder("vagrant", "halt");
                builder.directory(new File(buildRoot + "DSA-Heldendokument/vagrant-vm/"));
                builder.redirectErrorStream(true);
                Process proc = builder.start();
                if (proc.waitFor() != 0) {
                    throw new ExternalCallException("Konnte VM Status nicht ermitteln", proc.getInputStream());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not shutdown VM", e);
        }
    }

    private void writeConfig(DocumentConfiguration configuration) {
        final File targetParamsFile = new File(buildRoot + "DSA-Heldendokument/configuredParameters.yaml");
        if (targetParamsFile.exists()) {
            targetParamsFile.delete();
        }
        try {
            configuration.serialize(new FileOutputStream(targetParamsFile));
        } catch (IOException e) {
            throw new RuntimeException("Konnte Datei nicht anlegen.", e);
        }
    }

    private VMState getVMState() throws ExternalCallException {
        ProcessBuilder builder = new ProcessBuilder("vagrant", "status");
        builder.directory(new File(buildRoot + "DSA-Heldendokument/vagrant-vm/"));
        builder.redirectErrorStream(true);
        try {
            Process proc = builder.start();
            if (proc.waitFor() != 0) {
                throw new ExternalCallException("Konnte VM Status nicht ermitteln", proc.getInputStream());
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("default")) {
                    if (line.contains("not created")) {
                        return VMState.NOT_CREATED;
                    } else if (line.contains("running")) {
                        return VMState.RUNNING;
                    } else if (line.contains("poweroff")) {
                        return VMState.NOT_RUNNING;
                    } else {
                        return VMState.UNKNOWN;
                    }
                }
            }
        } catch (Exception ignored) {}
        return VMState.UNKNOWN;
    }

    private void vagrantup(boolean doProgress) throws ExternalCallException {
        ProcessBuilder builder = new ProcessBuilder("vagrant", "up");
        builder.directory(new File(buildRoot + "DSA-Heldendokument/vagrant-vm/"));
        builder.redirectErrorStream(true);
        StringBuilder output = new StringBuilder();

        int ret;
        Process proc;
        try {
            proc = builder.start();
            if (doProgress) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("Importing base box")) {
                        callback.nowAt(20);
                        callback.nowDoing("VM wird erstellt (Basis)", "Die Grundlagen der VM werden eingerichtet");
                    } else if (line.contains("Running provisioner")) {
                        callback.nowAt(25);
                        callback.nowDoing("VM wird erstellt (OS)", "Das Betriebssystem der VM wird eingerichtet");
                    } else if (line.contains("http://archive.ubuntu.com/ubuntu/ trusty/main libavahi-common-data")) {
                        callback.nowAt(30);
                        callback.nowDoing("VM wird erstellt (TeX laden)", "Die nötigen TeX-Komponenten werden geladen.");
                    } else if (line.contains("http://archive.ubuntu.com/ubuntu/ trusty/main lmodern")) {
                        callback.nowAt(35);
                    } else if (line.contains("Unpacking libavahi-common-data")) {
                        callback.nowAt(40);
                        callback.nowDoing("VM wird erstellt (TeX installieren)", "Die nötigen TeX-Komponenten werden installiert.");
                    } else if (line.contains("Unpacking tex-common")) {
                        callback.nowAt(45);
                    } else if (line.contains("Unpacking tipa")) {
                        callback.nowAt(50);
                    } else if (line.contains("Setting up texlive-base")) {
                        callback.nowAt(55);
                    } else if (line.contains("http://archive.ubuntu.com/ubuntu/ trusty/main imagemagick-common")) {
                        callback.nowAt(60);
                        callback.nowDoing("VM wird erstellt (ImageMagick laden)", "Die nötigen Grafikwerkzeuge werden geladen");
                    } else if (line.contains("Unpacking imagemagick-common")) {
                        callback.nowAt(65);
                        callback.nowDoing("VM wird erstellt (ImageMagick installieren)", "Die nötigen Grafikwerkzeuge werden installiert.");
                    } else if (line.contains("Setting up imagemagick")) {
                        callback.nowAt(70);
                    } else if (line.contains("http://archive.ubuntu.com/ubuntu/ trusty/main python-markdown")) {
                        callback.nowDoing("VM wird erstellt (Python)", "Die nötigen Python-Bibliotheken werden installiert.");
                    } else if (line.contains("Unpacking python-markdown")) {
                        callback.nowAt(75);
                    } else if (line.contains("creating: Das Schwarze Auge - Fanpaket")) {
                        callback.nowAt(80);
                        callback.nowDoing("VM wird erstellt (DSA)", "DSA-spezifische Dateien werden eingerichtet.");
                    }
                    output.append(line).append("\n");
                    // for debugging:
                    //System.out.println(line);
                }
            }
            ret = proc.waitFor();
        } catch (Exception e) {
            throw new RuntimeException("VM konnte nicht erstellt oder hochgefahren werden.", e);
        }
        if (ret != 0) {
            throw new ExternalCallException("VM konnte nicht erstellt oder hochgefahren werden.", output.toString());
        }
    }

    private File createPDF() throws ExternalCallException {
        try {
            // first, let's figure out on which port the VM is reachable
            ProcessBuilder builder = new ProcessBuilder("vagrant", "ssh-config");
            builder.directory(new File(buildRoot + "DSA-Heldendokument/vagrant-vm/"));
            Process proc = builder.start();
            if (proc.waitFor() != 0) {
                throw new RuntimeException("Konnte VM Konfiguration nicht lesen.");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            int port = 0;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("  Port ")) {
                    port = Integer.parseInt(line.substring(7));
                }
            }
            if (port == 0) {
                throw new RuntimeException("Konnte SSH-Port der VM nicht ermitteln.");
            }
            reader.close();

            SSHClient client = new SSHClient();
            client.addHostKeyVerifier(new PromiscuousVerifier());
            client.connect("localhost", port);
            client.authPassword("vagrant", "vagrant");
            Session session = client.startSession();
            session.exec("cd /dsa; export DATA_FILE=configuredParameters.yaml; make clean; make heldendokument.pdf");
            session.join();
            session.close();
            client.disconnect();

            File result = new File(buildRoot + "DSA-Heldendokument/heldendokument.pdf");
            if (!result.exists()) {
                throw new ExternalCallException("PDF wurde nicht erstellt!", /*shell.getInputStream()*/ "blubb");
            }
            return result;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            } else if (e instanceof ExternalCallException) {
                throw (ExternalCallException)e;
            }
            throw new RuntimeException("Konnte PDF nicht erstellen, irgendwas™ ging schief.", e);
        }
    }

}
