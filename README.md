# Studyshare-backend

Willkommen bei Studyshare-backend, einer Spring Boot-Anwendung. Dieses Dokument beschreibt die Schritte, um das Projekt auf Ihrem lokalen Entwicklungsrechner zum Laufen zu bringen.

## Voraussetzungen

Bevor Sie beginnen, stellen Sie sicher, dass Sie die folgenden Anforderungen erfüllen:
- Java JDK 17+
- Maven 3.8+
- Docker und Docker-Compose


## Erste Schritte

Diese Anweisungen helfen Ihnen, eine Kopie des Projekts auf Ihrem lokalen Rechner für Entwicklungs- und Testzwecke zum Laufen zu bringen.

### Klonen des Repositorys

Klonen Sie zuerst das Repository auf Ihren lokalen Rechner:

```
git clone https://gitlab.bht-berlin.de/s88339/studyshare-backend.git
cd studyshare-backend
```

### Bauen der Anwendung

Um die Anwendung zu bauen, führen Sie aus:

```
mvn clean install
```

Um die Anwendung ohne Testausführung zu bauen, führen Sie aus:

```
mvn clean install -DskipTests
```

### Ausführen der Anwendung

Um die Anwendung zu starten, führen Sie aus:

```
mvn spring-boot:run
```