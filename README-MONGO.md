# Ulohy 1-4 (Student repozitar, MongoDB)

Vsetok novy kod je v `src/main/java/nosql/mongo/**` a `src/test/java/nosql/mongo/**`.

## 0) Co potrebujeme mat nainstalovane
- Docker
- Java 21 + Maven

## 1) Databaza

V `sql/init/` mame:
- `00-create-database.sql` - vytvori databazu `ais-like`
- `01-create-user.sql` - vytvori usera `ais-like-user` / heslo `iceIceBaby`
- `02-aislike.sql` - samotne data

Tieto skripty sa spustia AUTOMATICKY pri prvom starte MySQL kontajnera (v poradi podla
nazvu suboru).

## 2) Spusti Docker (MySQL + MongoDB)
V koreni projektu (`nosql-datasource/`) je `docker-compose.yml`:

```
docker compose up -d
```

Spusti to:
- MySQL na `localhost:3307` (DB `ais-like`, user `ais-like-user`/`iceIceBaby`) - port 3307
- MongoDB na `localhost:27017` (bez hesla pre lokalny vyvoj)
- Mongo Express (web UI) na http://localhost:8081 - na vizualnu kontrolu dat


## 3) Co bolo zmenene v povodnom kode (MySQL cast)
`DaoFactory.java` som prepla, aby `getStudentDao()` pouzival lokalny docker MySQL
(`getJDBCTemplate2()`) namiesto skolskeho servera `nosql.gursky.sk`.
Ak by som bola na skolskej sieti/VPN, staci v
`getStudentDao()`/`getDownloadDao()` zamenit `getJDBCTemplate2()` za `getJDBCTemplate()`.

Overim si funkcnost povodneho DAO:
```
mvn test -Dtest=MysqlStudentDaoTest
```

## 4) Naplnenie MongoDB z MySQL dat
Spusti si MysqlToMongoMigration


Precita vsetkych studentov cez existujuci `StudentDao` (MySQL) a ulozi ich do Mongo
kolekcie `students` (v databaze `nosql-students`), vratane vnorenych studii a
studijnych programov. Vypise pocet migrovanych studentov.

Skratky studijnych programov, ktore mam v datach, zistim napr. cez Mongo Express
(http://localhost:8081) alebo priamo v MySQL: `SELECT DISTINCT skratka FROM studijnyprogram;`

## 5) Uloha 1 - repozitar (CrudRepository) + projekcia
- `nosql/mongo/entity/StudentDocument.java` - student (+ vnorene `Studium`, `StudijnyProgram`)
- `nosql/mongo/repository/StudentMongoRepository.java` - `extends CrudRepository<StudentDocument, String>`
  - zakladne CRUD (save/findById/findAll/delete...) 
  - `findBySkratkaakadtitul(...)` vracia `List<StudentNameProjection>` - projekcia LEN mena a priezviska
- Test zakladnej funkcionality: `src/test/java/nosql/mongo/StudentMongoRepositoryTest.java`
  ```
  mvn test -Dtest=StudentMongoRepositoryTest
  ```

## 6) Uloha 2 - studenti daneho programu v danom roku
Metoda `StudentMongoRepository.findByProgramAndYear(programSkratka, rok)`.
Upravujem si argumenty podla realnej skratky programu v mojich datach, ktore si viem pozriet napr. v mongo express:
```
nosql.mongo.demo.ProgramYearQueryDemo INF 2023
```

## 7) Uloha 3 - index na studijne programy + porovnanie casu
```
nosql.mongo.demo.IndexBenchmark INF 2023
```
Skript:
1. zrusi index (ak existuje) a zmeria dopyt z ulohy 2 BEZ indexu (`explain()` + priemerny cas z 20 opakovani)
2. vytvori index na `studium.studijnyProgram.skratka`
3. zmeria ten isty dopyt znovu, s indexom

V explain() vystupe sledujem hlavne `totalDocsExamined` (kolko dokumentov musel Mongo
prejst) a `executionTimeMillis` - po vytvoreni indexu by to malo klesnut 
`totalDocsExamined` (pri malom mnozstve testovacich dat nemusi byt rozdiel v case
citelny, ale v `explain()` uvidime rozdiel v pouzitom pláne/indexe vzdy).

## 8) Uloha 4 - tabulka poctov studentov podla roku a programu
```
nosql.mongo.demo.CountsByYearAndProgramReport
```
Pouzivame agregacny pipeline (`$unwind` -> `$project` rok+program -> `$group` count -> `$sort`)
a vypise tabulku do konzoly.

