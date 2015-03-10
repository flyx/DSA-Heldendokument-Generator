# DSA Heldendokument-Generator

Der **DSA Heldendokument-Generator** ist eine Java-Bibliothek,
die ein einfaches Interface bereitstellt, um ein DSA Heldendokument
zu erstellen. Im Hintergrund wird eine Virtuelle Maschine erstellt
und konfiguriert, auf der das Dokument gebaut wird. Dafür müssen auf
dem System [Vagrant][1] und [VirtualBox][2] verfügbar sein.

Es gibt eine grafische Benutzeroberfläche, um die Erstellung zu
steuern. Der Generator und die Oberfläche sind zwei getrennte Module,
damit es in Zukunft möglich ist, den Generator als Plugin in die
DSA Heldensoftware einzubinden.

## Entwicklung

Das Projekt wird mit [Maven][3] verwaltet und benötigt Java 8 oder
neuer. Die TeX-Quellen für das Heldendokument werden aus einem anderen
Repository als Submodul eingebunden. Daher muss nach dem ersten
Auschecken folgendes Kommando ausgeführt werden, um den Inhalt des
referenzierten Repositories zu holen:

    git submodule update --init --recursive

Ein Installer kann erstellt werden, indem folgendes Kommando auf dem
Project *gui* ausgeführt wird:

    mvn jfx:native

## Lizenz

[WTFPL][4]

 [1]: https://www.vagrantup.com
 [2]: https://www.virtualbox.org
 [3]: http://maven.apache.org
 [4]: http://www.wtfpl.net
