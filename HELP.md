## Instalando bibliotecas

Para rodar o projeto atual, é necessário instalasr três dependências: mundo de wumpus, cst e meca.

As libs se encontram na pasta src -> lib.


Segue abaixo os comandos para instalar cada lib:

#### Mundo de Wumpus - Biblioteca

```sh
mvn install:install-file -Dfile=wumpus-world-1.0-SNAPSHOT.jar -DgroupId=br.com.rsoft -DartifactId=wumpus-world -Dversion=1.0.0 -Dpackaging=jar
```

#### CST - Biblioteca V1.4.0

```sh 
mvn install:install-file -Dfile=cst.jar -DgroupId=br.unicamp -DartifactId=cst -Dversion=1.4.0 -Dpackaging=jar
```

#### MECA - Biblioteca V0.6.0

```sh 
mvn install:install-file -Dfile=meca.jar -DgroupId=br.unicamp -DartifactId=meca -Dversion=0.6.0 -Dpackaging=jar
```

Após instalar os comandos, basta executar a classe `AgentMind`