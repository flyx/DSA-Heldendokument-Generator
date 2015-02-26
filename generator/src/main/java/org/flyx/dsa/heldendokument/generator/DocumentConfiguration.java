package org.flyx.dsa.heldendokument.generator;

import javafx.beans.property.BooleanProperty;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author flyx
 */
public final class DocumentConfiguration {
    public static final class Hintergrund {
        public static final class Hintergrundbild {
            public static enum Type {
                ORIGINAL, ALTERNATIVE, NONE, CUSTOM
            }
            public Type type;
            public String customPath;
        }
        @YamlMapping("Hochformat")
        public final Hintergrundbild hochformat;
        @YamlMapping("Querformat")
        public final Hintergrundbild querformat;

        public Hintergrund() {
            hochformat = new Hintergrundbild();
            querformat = new Hintergrundbild();
        }
    }
    @YamlMapping("Hintergrund")
    public final Hintergrund hintergrund;

    public static final class Frontseite {
        // leer
    }
    @YamlMapping("Frontseite")
    public Frontseite frontseite;

    public static final class Talentbogen {
        // TODO

        @YamlMapping("M-Spalte")
        public boolean mSpalte;
    }
    @YamlMapping("Talentbogen")
    public final Talentbogen talentbogen;

    public static final class Kampfbogen {
        @YamlMapping("Nahkampfwaffen")
        @Lines
        public int nahkampfwaffen;

        @YamlMapping("Nahkampf SF")
        @Lines
        public int nahkampfSF;

        @YamlMapping("Fernkampfwaffen")
        @Lines
        public int fernkampfwaffen;

        @YamlMapping("Fernkampf SF")
        @Lines
        public int fernkampfSF;

        @YamlMapping("Waffenlose SF")
        @Lines
        public int waffenloseSF;

        @YamlMapping("Schilde und Parierwaffen")
        @Lines
        public int schildeParierwaffen;

        @YamlMapping("Rüstung")
        @Lines
        public int ruestung;

        @YamlMapping("Astralenergie")
        public boolean astralenergie;

        @YamlMapping("Karmaenergie")
        public boolean karmaenergie;
    }

    @YamlMapping("Kampfbogen")
    public final Kampfbogen kampfbogen;

    public static final class Ausruestung {
        @YamlMapping("Kleidung")
        @Lines
        public int kleidung;

        @YamlMapping("Ausrüstung")
        @Lines
        public int ausruestung;

        @YamlMapping("Proviant")
        @Lines
        public int proviant;

        @YamlMapping("Vermögen")
        @Lines
        public int vermoegen;

        @YamlMapping("Sonstiger Besitz")
        @Lines
        public int sonstigerBesitz;

        @YamlMapping("Verbindungen")
        @Lines
        public int verbindungen;

        @YamlMapping("Notizen")
        @Lines
        public int notizen;

        @YamlMapping("Tiere")
        @Lines
        public int tiere;
    }

    @YamlMapping("Ausrüstung")
    public final Ausruestung ausruestung;

    public static final class Liturgien {
        @YamlMapping("Kleidung")
        @Lines
        public int kleidung;

        @YamlMapping("Liturgien")
        @Lines
        public int liturgien;

        @YamlMapping("Ausrüstung")
        @Lines
        public int ausruestung;

        @YamlMapping("Proviant")
        @Lines
        public int proviant;

        @YamlMapping("Vermögen")
        @Lines
        public int vermoegen;

        @YamlMapping("Sonstiger Besitz")
        @Lines
        public int sonstigerBesitz;

        @YamlMapping("Verbindungen")
        @Lines
        public int verbindungen;

        @YamlMapping("Notizen")
        @Lines
        public int notizen;

        @YamlMapping("Tiere")
        @Lines
        public int tiere;
    }

    @YamlMapping("Liturgien")
    public final Liturgien liturgien;

    public static final class Zauberdokument {
        @YamlMapping("Rituale")
        @Lines
        public int rituale;

        @YamlMapping("Ritualkenntnis")
        @Lines
        public int ritualkenntnis;

        @YamlMapping("Magische Vor- und Nachteile")
        @Lines
        public int magischeVorNachteile;

        @YamlMapping("Magische Sonderfertigkeiten")
        @Lines
        public int magischeSonderfertigkeiten;

        @YamlMapping("Artefakte")
        @Lines
        public int artefakte;

        @YamlMapping("Notizen")
        @Lines
        public int notizen;
    }

    @YamlMapping("Zauberdokument")
    public final Zauberdokument zauberdokument;

    public static final class Zauberliste {
        @YamlMapping("Seiten")
        public int seiten;
    }
    @YamlMapping("Zauberliste")
    public final Zauberliste zauberliste;


    public DocumentConfiguration() {
        hintergrund = new Hintergrund();
        frontseite = new Frontseite();
        talentbogen = new Talentbogen();
        kampfbogen = new Kampfbogen();
        ausruestung = new Ausruestung();
        liturgien = new Liturgien();
        zauberdokument = new Zauberdokument();
        zauberliste = new Zauberliste();
    }

    public void loadDefaults() {
        Yaml yaml = new Yaml();
        Map data = (Map)yaml.load(getClass().getResourceAsStream("/DSA-Heldendokument/data/parameter.yaml"));
        loadInto(data, this);
    }

    private void loadInto(Map data, Object target) {
        for (Field field: target.getClass().getFields()) {
            YamlMapping[] annotations = field.getAnnotationsByType(YamlMapping.class);
            if (annotations.length > 0) {
                Object value;
                Lines[] lineAnnotations = field.getAnnotationsByType(Lines.class);
                try {
                    if (lineAnnotations.length > 0) {
                        value = ((Map) data.get("Zeilen")).get(annotations[0].value());
                    } else if (boolean.class.equals(field.getType())) {
                        value = ((Map)data.get("Optionen")).get(annotations[0].value());
                    } else if (int.class.equals(field.getType())) {
                        value = data.get(annotations[0].value());
                    } else if (Hintergrund.Hintergrundbild.class.equals(field.getType())) {
                        Object rawValue = data.get(annotations[0].value());
                        Hintergrund.Hintergrundbild pic = (Hintergrund.Hintergrundbild)field.get(target);
                        if (rawValue instanceof Boolean) {
                            if ((Boolean) rawValue) {
                                pic.type = Hintergrund.Hintergrundbild.Type.ORIGINAL;
                            } else {
                                pic.type = Hintergrund.Hintergrundbild.Type.NONE;
                            }
                        } else {
                            pic.type = Hintergrund.Hintergrundbild.Type.ALTERNATIVE;
                        }
                        continue;
                    } else {
                        loadInto((Map)data.get(annotations[0].value()), field.get(target));
                        continue;
                    }
                    field.set(target, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Could not load value", e);
                }
            }
        }
    }
}
