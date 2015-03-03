package org.flyx.dsa.heldendokument.generator;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * @author flyx
 */
public class TexBuilder implements IBuilder {
    private static final String buildRoot;
    private static final String mansonKey = "Manson";
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
                new AdditionalParameter(AdditionalParameter.Type.PATH, "Manson.ttf", "Manson Schriftart (normal)"),
                new AdditionalParameter(AdditionalParameter.Type.PATH, "MansonBold.ttf", "Manson Schriftart (fett)")
        );
    }

    @Override
    public void setPathParameter(String name, String value) {
        if ("Manson.ttf".equals(name)) {
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
        if ("Manson.ttf".equals(name)) {
            return manson != null;
        } else if ("MansonBold.ttf".equals(name)) {
            return mansonBold != null;
        } else {
            throw new RuntimeException("Unknown parameter: " + name);
        }
    }

    @Override
    public String getPathParameter(String name) {
        if ("Manson.ttf".equals(name)) {
            return manson;
        } else if ("MansonBold.ttf".equals(name)) {
            return mansonBold;
        } else {
            throw new RuntimeException("Unknown parameter: " + name);
        }
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
            final File mansonFile = new File(buildRoot + "DSA-Heldendokument/vagrant-vm/Manson.ttf");
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
}
