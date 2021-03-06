# Sovellusarkkitehtuuri

## Näkymät

Sovelluksessa on tällä hetkellä kolme eri näkymää: kirjautuminen, rekisteröinti ja itse sovelluksen päänäkymä.

## Luokka- ja pakkauskaavio

![alt text](https://github.com/tikibeni/ot-harjoitustyo/blob/master/dokumentaatio/kuvat/arkkitehtuuri/kaaviot.png "Kaavio")

Sovelluksen pääasialliset olioluokat ovat Plan sekä Course. Plan, eli suunnitelma voi sisältää useamman valitun/suoritetun kurssin, kun taas sama kurssi voi kuulua useampaan suunnitelmaan.

Riippuvuudet on merkitty katkoviivalla. Näin ollen planapp.ui riippuu planapp.domain:sta ja planapp.domain riippuu planapp.dao:sta.

UI-luokka käyttää domainin luokkia PlanService, Plan ja Course.

Domainin PlanService käyttää PlanDao:ta ja CourseDao:ta.

## Tiedon käsittely ja tallennus

Sovellus tallentaa tietoa suunnitelmista ja kursseista DAO-rajapintojen (planDao, courseDao) toteuttavien luokkien (planFileDao, courseFileDao) avulla. Nämä DAO-luokat toteuttavat sovelluksen palvelun (planService).

Tämän toimiakseen sovelluksessa on määritetty konfiguroinnit tiedostoon _config.file_, joka määrittää tiedostojen nimet (plans.txt, courses.txt) ja sisältää kaikkien kurssien ennaltamääritetyt ja sovelluksessa luodut tiedot.

Näin jokaisen käynnistyksen yhteydessä sovellus kutsuu DAO-luokkien konstruktoreita, jotka joko luovat puuttuvat tiedostot, mahdollisesti kirjoittavat niihin (kuten courses.txt:n tapauksessa) jonka myötä palvelu saa käyttöönsä listoina kaikki tiedostoissa (tietokannassa) olevat tiedot.

Uutta tietoa tallennettaessa palvelu kokoaa tallennettavat tai poistettavat oliot kasaan, jonka myötä sovellus tallentaa DAO-luokkia hyödyntäen muutokset tiedostoon.

Sisältötyypit tiedostoilla ovat:

- config.file

```
    plans=plans.txt
    courses=courses.txt
    coursesInfo=TKT10001;Johdatus tietojenkäsittelytieteeseen\nPREREQUISITES:\n\n ... 
```

- courses.txt

```
    TKT10001;Johdatus tietojenkäsittelytieteeseen
    PREREQUISITES:
    
    TKT10002;Ohjelmoinnin perusteet
    PREREQUISITES:
    ...
```

- plans.txt

```
    fresh;fresher
    COURSES:
    TKT10001;Johdatus tietojenkäsittelytieteeseen
    
    tiramaisteri;gitgud
    COURSES:
    TKT10001;Johdatus tietojenkäsittelytieteeseen
    ...
```

Näin courses.txt:n alkuperäinen sisältö tulee suoraan config.file "coursesInfo" -osiosta. Tiedostojen lukeminen ja tietojen erottelu tapahtuu Scannerilla, joka käy rivi kerrallaan tietoja läpi. Oleellisina tunnisteina toimivat `PREREQUISITES:` ja `COURSES:` -osiot, jotka ilmoittavat ohjelmalle, että nyt käsitellään esitietoja tai kursseja, kun aiemmin ollaan käsitelty peruskursseja tai Plan-oliota.

## Kuvaukset sovelluksen perustoiminnallisuuksista

### Rekisteröinti

![alt text](https://github.com/tikibeni/ot-harjoitustyo/blob/master/dokumentaatio/kuvat/arkkitehtuuri/regSequence.png "Rekisteröintikaavio")

Rekisteröintinäkymään voidaan siirtyä kirjautumisnäkymästä nappia painamalla.

Rekisteröinti tapahtuu käyttäjän toimesta rekisteröintinäkymässä sopivat tiedot syötettyään ja rekisteröintinappia painamalla. 
Tällöin UI saa tiedon, että rekisteröintiä yritetään, jonka myötä se antaa syötetyt tiedot planService-luokalle, joka tarkistaa planDaon avulla tietokannasta onko tietoja vastaavaa suunnitelmaa jo olemassa. Mikäli suunnitelmaa ei entuudestaan ole olemassa, planService luo uuden Plan-olion ja syöttää sen planDaolle kantaan tallentamista varten. Ketjun onnistuttua näkymä vaihtuu takaisin kirjautumisnäkymään.

### Suunnitelman poistaminen

![alt-text](https://github.com/tikibeni/ot-harjoitustyo/blob/master/dokumentaatio/kuvat/arkkitehtuuri/deleteplanSeq.png "Poistaminen")

Suunnitelman poistaminen tapahtuu käyttäjän toimesta Delete plan -nappia painamalla suunnitelmanäkymässä. 

Tämän myötä UI pyytää palvelua poistamaan nykyisen suunnitelman. Palvelu hakee suunnitelman nimen ja siihen pohjautuen pyytää planDaota poistamaan suunnitelman ja siihen liittyvät tiedot tietokannasta. Lopulta UI vaihtaa näkymänsä kirjautumisruutuun.

### Kirjautuminen

![alt text](https://github.com/tikibeni/ot-harjoitustyo/blob/master/dokumentaatio/kuvat/arkkitehtuuri/logSequence.png "Kirjautumiskaavio")

Kirjautuminen tapahtuu käyttäjän toimesta kirjautumisnäkymässä nappia painamalla.

Kun käyttäjä on syöttänyt kirjautumiseen suunnitelman nimen ja painaa nappia, UI lähettää kirjautumistiedot planService-luokalle, joka tarkistaa planDaolta, löytyykö tietokannasta kyseisen nimistä suunnitelmaa. Mikäli suunnitelma on olemassa, planDao palauttaa suunnitelman palvelulle, joka myöntää UI:lle luvan vaihtaa näkymä kirjautumisnäkymästä päänäkymään. Ketjun onnistuttua näkymä vaihtuu kirjautumisnäkymästä suunnitelma-/päänäkymään.

### Uloskirjautuminen

![alt-text](https://github.com/tikibeni/ot-harjoitustyo/blob/master/dokumentaatio/kuvat/arkkitehtuuri/logoutSeq.png "Logout")

Uloskirjautuminen tapahtuu käyttäjän toimesta suunnitelmanäkymästä logout-nappia painamalla.

Tämän myötä UI pyytää palvelua resetoimaan nykyisen suunnitelman attribuutin tyhjäksi. Lopulta UI vaihtaa näkymänsä kirjautumisruutuun.

### Kurssin syöttäminen suunnitelmaan

![alt-text](https://github.com/tikibeni/ot-harjoitustyo/blob/master/dokumentaatio/kuvat/arkkitehtuuri/selectingSeq.png "Kurssin syöttökaavio")

Kurssin tallentaminen suunnitelmaan tapahtuu suunnitelmanäkymästä checkboxeja valitsemalla ja painamalla Save-nappia.

Tämän jälkeen UI lähettää checkboxien tiedot palvelulle, joka hakee Course-oliot courseDaolta. Tämän tehtyään UI pyytää palvelua vertailemaan nyt haettuja kursseja suunnitelman valittuihin kursseihin. Mikäli jokin juuri valituista kursseista ei löydy selectedCourses-listasta, niin palvelua pyydetään lisäämään kurssi nykyiseen suunnitelmaan. Tämän myötä palvelu lisää kurssin suunnitelmaan ja pyytää planDaota kirjoittamaan päivityksen tietokantaan.

### Kurssin poistaminen suunnitelmasta

![alt-text](https://github.com/tikibeni/ot-harjoitustyo/blob/master/dokumentaatio/kuvat/arkkitehtuuri/removalSeq.png "Removal")

Kurssin poistaminen suunnitelmasta tapahtuu suunnitelmanäkymästä checkboxien valintoja poistamalla ja painamalla Save-nappia.

Tämän myötä UI tekee lähes identtisen ketjun kuin em. Kurssien syöttämisessä, mutta lisäilyjen ja valintojen puuttumisen sijaan, se poistaa nykysuunnitelmasta kurssit joita ei ole nyt valittuna checkboxeissa. Lopulta planService (palvelu) pyytää planDaota tallentamaan muutokset tietokantaan.

### Kurssiehdotuksien toimintaperiaate

![alt-text](https://github.com/tikibeni/ot-harjoitustyo/blob/master/dokumentaatio/kuvat/arkkitehtuuri/suggestionSeq.png "Suggestions")

Kurssiehdotusten toimintaperiaate perustuu aiemmin esiteltyihin kurssivalintojen syöttämiseen ja poistamiseen ja aktivoituu siis painamalla nappia Save.

Tämän pohjalta UI alkaa palvelun kanssa tarkistamaan, mitä kursseja käyttäjälle voidaan ehdottaa. UI hakee kurssien esitietovaatimukset ja vertailee niitä aktiivisesti nykyisen suunnitelman valittuihin kursseihin. Mikäli kaikki esitiedoiksi merkityt kurssit on suoritettu, kurssia voidaan ehdottaa. Tämän myötä UI hakee kultakin sopivalta kurssilta tiedot ja näyttää ne käyttäjälle listana.

### Kurssiresetointi

Sovelluksessa on toiminnallisuus kurssien resetoinnille, koska käyttäjä pystyy poistamaan ja luomaan kursseja mielivaltaisesti. Resetoinnin yhteydessä käyttäjä heitetään kirjautumisnäkymään, annetaan resetoinnin tilaviesti ja suoritetaan resetointi poistamalla `courses.txt` ja pyytämällä sovellusta suorittamaan planServicen uudelleenmäärityksen.

Näin käyttäjän komennosta UI vaihtaa näkymänsä login-screeniin, pyytää palvelua resetoimaan kurssit, jonka myötä palvelu käskee courseDAO:ta resetoimaan. _CourseFileDao_ poistaa nykyisen `courses.txt`:n, jonka myötä UI kutsuu init()-metodiaan luomaan uuden planServicen, jonka myötä luodaan uusi `courses.txt` `config.file`:n avulla.

### Kurssin poisto järjestelmästä

Sovelluksessa on mahdollista poistaa kursseja mielivaltaisesti. Poiston yhteydessä palvelu pitää huolen planDaolla ja courseDaolla, että kaikki annetut Course-instanssit poistetaan järjestelmästä kokonaan. Tämä koskee poistamista suunnitelmien valituista kursseista, järjestelmään luoduista kursseista sekä kurssien esitiedoista. Poistojen yhteydessä DAO-luokat kirjoittavat muutokset tiedostoihin.

### Kurssin luonti järjestelmään

Sovelluksessa voi luoda omia kursseja ja valita niille esitietovaatimuksia. Käyttäjän syötettyä kurssitiedot UI ja painettuaan luontipainiketta UI tarkistaa onko tiedot sopivia ja pyytää palvelua ja sitä kautta courseDaota luomaan uuden kurssin järjestelmätiedostoon. Tämän jälkeen kurssi näkyy muiden kurssien tapaan.


## Sovellukseen jääneet heikkoudet

UI-luokan koodia voisi optimoida ja järjestellä huomattavasti. Tällä hetkellä UI sisältää huomattavasti toistuvaa koodia esim. näyttöasetusten ja erilaisten kenttien tyhjennyksen suhteen.

Sovelluksessa epäloogista on, ettei siinä vielä pysty muokkaamaan nykyisiä suunnitelmia nimitiedoilta tai olemassaolevia kursseja nimi- ja esitiedoilta. Nämä tosin ovat varsin helposti toteutettavissa jatkossa.