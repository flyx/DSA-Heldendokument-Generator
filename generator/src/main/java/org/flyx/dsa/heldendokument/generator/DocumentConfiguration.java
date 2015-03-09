package org.flyx.dsa.heldendokument.generator;

import javafx.util.Pair;
import org.yaml.snakeyaml.Yaml;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        public static final class Sonstiges {
            public String titel;
            public int zeilen;

            public Sonstiges(String titel, int zeilen) {
                this.titel = titel;
                this.zeilen = zeilen;
            }
        }

        public enum Gruppen {
            Sonderfertigkeiten("Sonderfertigkeiten", Integer.class),
            Kampftechniken("Kampftechniken", Integer.class),
            Koerperlich("Körperliche Talente", Integer.class),
            Gesellschaft("Gesellschaftliche Talente", Integer.class),
            Natur("Naturtalente", Integer.class),
            Wissen("Wissenstalente", Integer.class),
            Sprachen("Sprachen und Schriften", Integer.class),
            Handwerk("Handwerkliche Talente", Integer.class);

            private final String key;
            private final Type type;

            private Gruppen(String key, Type type) {
                this.key = key;
                this.type = type;
            }

            @Override
            public String toString() {
                return key;
            }

            public Type valueType() {
                return type;
            }
        };

        @YamlMapping("Layout")
        @Layout
        public final Map<Gruppen, Integer> gruppen;

        @YamlMapping("Layout")
        @Layout
        public final List<Sonstiges> sonstiges;

        @YamlMapping("M-Spalte")
        public boolean mSpalte;

        public Talentbogen() {
            gruppen = new HashMap<>();
            sonstiges = new ArrayList<>();
        }
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
                Layout[] layoutAnnotations = field.getAnnotationsByType(Layout.class);
                try {
                    if (lineAnnotations.length > 0) {
                        value = ((Map) data.get("Zeilen")).get(annotations[0].value());
                    } else if (layoutAnnotations.length > 0) {
                        final Map layout = (Map) data.get(annotations[0].value());
                        Object rawTarget = field.get(target);

                        if (rawTarget instanceof Map) {
                            @SuppressWarnings("unchecked")
                            final Map<Talentbogen.Gruppen, Integer> layoutTarget = (Map<Talentbogen.Gruppen, Integer>)rawTarget;
                            layoutTarget.clear();
                            for (String side: new String[]{"Links", "Rechts"}) {
                                final List sideMap = (List)layout.get(side);
                                for (Object rawEntry : sideMap) {
                                    final Map.Entry entry = (Map.Entry) ((Map)rawEntry).entrySet().iterator().next();
                                    final String key = (String) entry.getKey();
                                    for (Talentbogen.Gruppen group : Talentbogen.Gruppen.values()) {
                                        if (group.toString().equals(key)) {
                                            //if (Integer.class.equals(group.valueType())) {
                                            layoutTarget.put(group, (Integer) entry.getValue());
                                        /*} else if (Talentbogen.Sonstiges.class.equals(group.valueType())) {
                                            final Map source = (Map)entry.getValue();
                                            final Talentbogen.Sonstiges sonstiges = new Talentbogen.Sonstiges(
                                                    (String)source.get("Titel"), (Integer)source.get("Zeilen")
                                            );
                                            layoutTarget.put(group, sonstiges);
                                        }*/
                                            break;
                                        }
                                    }
                                }
                            }
                        } else if (rawTarget instanceof List) {
                            @SuppressWarnings("unchecked")
                            final List<Talentbogen.Sonstiges> layoutTarget = (List<Talentbogen.Sonstiges>)rawTarget;
                            for (String side: new String[]{"Links", "Rechts"}) {
                                final List sideMap = (List)layout.get(side);
                                for (Object rawEntry : sideMap) {
                                    final Map.Entry entry = (Map.Entry)((Map) rawEntry).entrySet().iterator().next();
                                    final String key = (String) entry.getKey();
                                    if ("Sonstiges".equals(key)) {
                                        final Map source = (Map) entry.getValue();
                                        layoutTarget.add(new Talentbogen.Sonstiges((String) source.get("Titel"),
                                                (Integer) source.get("Zeilen")));
                                    }
                                }
                            }
                        } else {
                            throw new RuntimeException("Unsupported layout type!");
                        }
                        continue;
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

    public void serialize(OutputStream os) {
        final Map<String, Object> root = new HashMap<>();
        for (Field pageField: getClass().getFields()) {
            YamlMapping[] pageAnnotations = pageField.getAnnotationsByType(YamlMapping.class);
            if (pageAnnotations.length > 0) {
                final Map<String, Object> pageSet = new HashMap<>();
                root.put(pageAnnotations[0].value(), pageSet);
                try {
                    final Object page = pageField.get(this);

                    Map<String, Integer> zeilen = null;
                    Map<String, Boolean> optionen = null;
                    Map<Talentbogen.Gruppen, Integer> layoutStandard = null;
                    List<Talentbogen.Sonstiges> layoutSonstiges = null;

                    for (Field valueField: page.getClass().getFields()) {
                        YamlMapping[] valueAnnotations = valueField.getAnnotationsByType(YamlMapping.class);
                        Lines[] lineAnnotations = valueField.getAnnotationsByType(Lines.class);
                        Layout[] layoutAnnotations = valueField.getAnnotationsByType(Layout.class);

                        if (lineAnnotations.length > 0) {
                            if (zeilen == null) {
                                zeilen = new HashMap<>();
                                pageSet.put("Zeilen", zeilen);
                            }
                            zeilen.put(valueAnnotations[0].value(), (Integer)valueField.get(page));
                        } else if (layoutAnnotations.length > 0) {
                            Object obj = valueField.get(page);
                            if (obj instanceof Map) {
                                layoutStandard = (Map<Talentbogen.Gruppen, Integer>) obj;
                            } else if (obj instanceof List) {
                                layoutSonstiges = (List<Talentbogen.Sonstiges>) obj;
                            }
                            if (layoutStandard != null && layoutSonstiges != null) {
                                pageSet.put("Layout", createLayout(layoutStandard, layoutSonstiges));
                            }
                        } else if (boolean.class.equals(valueField.getType())) {
                            if (optionen == null) {
                                optionen = new HashMap<>();
                                pageSet.put("Optionen", optionen);
                            }
                            optionen.put(valueAnnotations[0].value(), (Boolean)valueField.get(page));
                        } else if (int.class.equals(valueField.getType())) {
                            pageSet.put(valueAnnotations[0].value(), (Integer)valueField.get(page));
                        } else if (Hintergrund.Hintergrundbild.class.equals(valueField.getType())) {
                            final Hintergrund.Hintergrundbild bild = (Hintergrund.Hintergrundbild)valueField.get(page);
                            switch (bild.type) {
                                case ORIGINAL: pageSet.put(valueAnnotations[0].value(), true); break;
                                case NONE: pageSet.put(valueAnnotations[0].value(), false); break;
                                case ALTERNATIVE: pageSet.put(valueAnnotations[0].value(), "TODO"); break;
                                case CUSTOM: pageSet.put(valueAnnotations[0].value(), bild.customPath); break;
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Could not load value", e);
                }
            }
        }
        final Yaml yaml = new Yaml();
        yaml.dump(root, new OutputStreamWriter(os));
    }

    private int totalLines(List<Pair<String, Integer>> list) {
        int ret = 0;
        for (Pair<String, Integer> p : list) {
            ret += p.getValue() + 2;
        }
        return ret;
    }

    private Object createLayout(Map<Talentbogen.Gruppen, Integer> standard, List<Talentbogen.Sonstiges> sonstiges) {
        final List<Pair<String, Integer>> left = new ArrayList<>();
        final List<Pair<String, Integer>> right = new ArrayList<>();
        for (Talentbogen.Gruppen gruppe: new Talentbogen.Gruppen[]{Talentbogen.Gruppen.Sonderfertigkeiten,
                Talentbogen.Gruppen.Kampftechniken, Talentbogen.Gruppen.Koerperlich, Talentbogen.Gruppen.Gesellschaft}) {
            left.add(new Pair<>(gruppe.toString(), standard.get(gruppe)));
        }
        for (Talentbogen.Gruppen gruppe: new Talentbogen.Gruppen[]{Talentbogen.Gruppen.Wissen,
                Talentbogen.Gruppen.Sprachen, Talentbogen.Gruppen.Handwerk}) {
            right.add(new Pair<>(gruppe.toString(), standard.get(gruppe)));
        }

        if (totalLines(left) < totalLines(right)) {
            left.add(new Pair<>(Talentbogen.Gruppen.Natur.toString(), standard.get(Talentbogen.Gruppen.Natur)));
        } else {
            right.add(0, new Pair<>(Talentbogen.Gruppen.Natur.toString(), standard.get(Talentbogen.Gruppen.Natur)));
        }
        for (Talentbogen.Sonstiges s: sonstiges) {
            if (totalLines(left) < totalLines(right)) {
                left.add(1, new Pair<>("Sonstiges-" + s.titel, s.zeilen));
            } else {
                right.add(0, new Pair<>("Sonstiges-" + s.titel, s.zeilen));
            }
        }

        final Map<String, Object> root = new HashMap<>();
        final List<Object> yamlLeft = new ArrayList<>();
        final List<Object> yamlRight = new ArrayList<>();
        root.put("Links", yamlLeft);
        root.put("Rechts", yamlRight);

        for (Pair<List<Pair<String, Integer>>, List<Object>> p : new ArrayList<Pair<List<Pair<String, Integer>>, List<Object>>>() {{
            add(new Pair<>(left, yamlLeft));
            add(new Pair<>(right, yamlRight));
        }}) {
            for (Pair<String, Integer> i : p.getKey()) {
                if (i.getKey().startsWith("Sonstiges-")) {
                    final Map<String, Object> m = new HashMap<>();
                    m.put("Titel", i.getKey().substring(10));
                    m.put("Zeilen", i.getValue());
                    p.getValue().add(singletonMap("Sonstiges", m));
                } else {
                    p.getValue().add(singletonMap(i.getKey(), i.getValue()));
                }
            }
        }

        return root;
    }

    private <T> Map<String, T> singletonMap(String key, T value) {
        final Map<String, T> result = new HashMap<>(1);
        result.put(key, value);
        return result;
    }
}
