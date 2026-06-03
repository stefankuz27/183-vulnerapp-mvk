# Diskussion und Selbstevaluation

  ### 1. Welche Sicherheitsmechanismen wurden wo implementiert und warum?

  **Session-basierte Authentifizierung (`SecurityConfiguration`, `RestfulFormService`)**
  Ich habe das Standard-Form-Login von Spring Security verwendet, aber den
  Redirect-Mechanismus durch einen `RestfulFormService` ersetzt, der bei
  Erfolg/Fehler einen HTTP-Status zurückgibt.
  Nach dem Login verwaltet Spring eine Server-Session und legt ein Session-Cookie
  ab. Das verbessert die Sicherheit, weil keine Credentials mehr bei jedem Request
  mitgeschickt werden und der Server jederzeit die Kontrolle über die Session hat
  (z.B. Invalidierung).

  ** RBAC **
  In der `SecurityFilterChain` habe ich die Endpunkte gemäss der Vorgabentabelle
  abgesichert/angepasst:
  - `GET /api/blog` → `permitAll()`
  - `/api/admin/**` → `hasRole("ADMIN")`
  - `/api/**` → `authenticated()`
  - alles andere → `permitAll()` (statische Files, FE)

  Die Rollen sind als eigene `RoleEntity` modelliert, die `GrantedAuthority`
  implementiert und das von Spring erwartete `ROLE_`-Präfix erzeugt. Dadurch wird
  verhindert, dass ein normaler User Admin-Funktionen (User anlegen/löschen)
  ausführen kann.

  **CSRF-Schutz (`SecurityConfiguration`, `script.js`)**
  CSRF ist mit `csrf.spa()` aktiviert, was den `CookieCsrfTokenRepository`
  verwendet: Das Token wird als lesbares `XSRF-TOKEN`-Cookie ausgeliefert. Das
  Frontend liest dieses Cookie aus (`getCsrfToken()`) und schickt den Wert bei
  Stateänderung Requests im Header `X-XSRF-Token` mit. Der `/login`-Endpunkt
  ist von der CSRF-Prüfung ausgenommen, da hier noch keine Session existiert.

  Warum das funktioniert: Ein Angreifer auf einer fremden Seite kann zwar einen
  Request an unsere App auslösen (das Session-Cookie wird vom Browser automatisch
  mitgesendet), aber er kann **nicht** den Inhalt des `XSRF-TOKEN`-Cookies lesen
  (Same-Origin-Policy) und ihn deshalb auch nicht in den Header schreiben. Ohne den
  passenden Header-Wert lehnt der Server den Request ab. Das Cookie allein genügt
  also nicht.

  **Hashed Passwortspeicherung (`SecurityConfiguration`, `UserEntity`)**
  Passwörter werden über den `DelegatingPasswordEncoder` mit bcrypt gehasht und gesaltet.
  Es werden also nie Klartext-Passwörter gespeichert. Zusätzlich erzwinge ich auf
  der `UserEntity` Passwortregeln über `@Size(min = 8)` und ein `@Pattern`, das
  mindestens eine Zahl, einen Gross-, einen Kleinbuchstaben und ein Sonderzeichen
  verlangt. Das Feld ist mit `@JsonIgnore` annotiert, damit der Hash nie über die
  API nach aussen gelangt.

  **Input-Validierung mit Hibernate-Validator (`UserEntity`, `BlogEntity`)**
  Beide Entities sind mit Validation-Annotations versehen/beschmückt (`@NotBlank`,
  `@Size`, `@Pattern`). Damit werden ungültige oder leere Eingaben abgewiesen.

  **Behebung der ursprünglichen Lücken (SQLi / XSS)**
  - **SQLi:** Ich verwende parametrisierte Queries statt String-Konkatenation.
     Damit ist SQL-Injection nicht mehr möglich.
  - **XSS:** Im Frontend (`script.js`) werden Blog-Titel und -Body über
    `element.textContent` statt `innerHTML` gesetzt. Dadurch wird vom Server
    geliefertes Markup nicht als HTML interpretiert, sondern als Text dargestellt –
    injizierte Skripte wie `<img src=a onerror=...>` werden nicht ausgeführt.

  ### 2. Weitere mögliche und sinnvolle Sicherheitsmechanismen

  - **Method-Based Security:** Zusätzlich zur URL-basierten
    Konfiguration könnte man die Autorisierung an den Service-Methoden
    machen, sodass wenn man die Berechtigung ausversehen falsch zugewiesen hat, man nicht gelifert ist.
  - **Brute-Force-Schutz:** Ein Zähler für fehlgeschlagene Logins pro
    Benutzer oder IP, der nach n Versuchen temporär sperrt
  - **Weniger Information ausgeben:** Man könnte vielleicht nicht detailierte Fehlermeldung ausgeben. 
    So erleichtert es dem Angreifer die Arbeit erheblich. Generischer Output z.B. 
  - **Keep-Me-Logged-In:** Ich würde ein JWT-Cookie statt Session-Cookie verwenden.

  ### 3. Reflexion über Schwierigkeiten und was anders gemacht werden kann

  - Ich hatte die grössten Schwierigkeiten mit der Hibernate-Validierung. 
    Ich habe die @Annotations (@Valid, @RequestBody, @JsonIgnore) an Stellen platziert, 
    wo ich es für plausibel empfunden habe, aber der Output dann anders war als `erhofft`.
    Ich habe auch manchmal die Angriffsmethoden wie CSRF nicht im Detail verstanden, 
    was bei der Implementation von Schutz-Mechanismen ** vielleicht ** nicht schlecht wäre.
    Ich werde in Zukunft **  vielleicht **  nicht nur 3 Sätze über eine Angriffs-Methodik lesen und dann denken dass ich es genug gut verstehe, sondern  mehr Theorie
    eingehen. 

  ### 4. Aufwand vs Ertrag von Sicherheitsmassnahmen

  In diesem Projekt war das Verhältnis unterschiedlich: Massnahmen wie
  Passwort-Hashing und parametrisierte Queries sind leicht
  umsetzbar und verhindern gleichzeitig sehr schwerwiegende Lücken. 

  Deutlich aufwändiger war RBAC, oder die Validation. Dort konnte man nicht einfach eine Methode aus einer Library im Prozess implementieren. Man musste auch ein wenig hustlen.

  Ich finde dass im Betrieb zu wenig auf Sicherheit geachtet wird. Im Gesundheitswesen wird immer gross von Datenschutz geredet, aber es weniger dafür gemacht. Man sollte bei weniger trivialen Anwendungen/Prozessen mindestens Sicherheit-Mechanismen mit kleinem Aufwand implementieren (Hashing, Validierung, HTTPS, Dependency-Updates).Bei Enterprise Anwendungen/Prozessen sollte man alle Zeit die möglich ist in Sicherheit investieren. Da ist es nie eine Zeitverschwendung. Natürlich gibt es nice-to-have-features wie `Keep me logged in`, aber im grossen und ganzen 100% notwendig.

