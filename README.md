# Liquibase
##Introduction
Liquibase is a database migration tool. It allows us to write database migration scripts in `XML, YAML, JSON, SQL`. 
This allows for developers to write scripts:
 - which are schema and data agnostic of database.
 - which allow to apply different changes to different environment.
 - with ability to rollback from certain changesets.
 
 
## Basics
 Here we will cover some of the basic concepts of Liquibase
 ### Changelog
 A changelog is used to specify all the changes that occur to the database. We can 
 add all changes directly to this file, or specify other files/folder where those changes 
 can be read from and applied.

```$xslt
<?xml version="1.0" encoding="UTF-8"?>
    
<databaseChangeLog
  xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
         http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd">

         
</databaseChangeLog>
```

 ### Changeset
 These are the atomic changes that will be applied to the database. 
 Each change set is uniquely defined by the `id`, `author` and `package` of the file where changeset is defined.
 Each changeset is run as a single transaction by liquibase.

```$xslt
<changeSet author="ankit (generated)" id="1516474605675-1">
    <createTable tableName="user">
        <column autoIncrement="true" name="id" type="INT">
            <constraints primaryKey="true"/>
        </column>
        <column name="email" type="VARCHAR(255)"/>
        <column name="name" type="VARCHAR(255)"/>
        <column name="phone" type="VARCHAR(255)"/>
    </createTable>
</changeSet>
```

## Internals
The Liquibase creates 2 tables, `DATABASECHANGELOG` and `DATABASECHANGELOGLOCK`, for its metadata storage.

`DATABASECHANGELOG` keeps records of what all changeset have been applied on the database. 
In the table create, liquibase creates the `MD5SUM` column to store Checksum of each changeset that has been applied. 
When applying new changes, it compares the checksum of existing changesets to what has already been applied to the database. 
This step ensures that the existing changesets have not been modified. If the checksum is different, Liquibase fails. 
This is a step that guarantees that a changeset once applied is immutable and that any changes to a database schema 
should be applied as a new changeset.

`DATABASECHANGELOGLOCK`is a way mutex works. Liquibase tries to acquire a lock from this table. It makes sure that no 
other migrations are going on. If it fails to acquire the lock, the migration step fails.

## Integration with Spring-Boot

- `Maven` configurations needed

    ```$xslt
    <dependency>
        <groupId>org.liquibase</groupId>
        <artifactId>liquibase-core</artifactId>
    </dependency>

    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
 
    ```
- `Organise` changeset files: We can achieve this by having all changelog files, in a changelog folder in resources and 
including all the changelog there in the main changelog file, as shown below
    ```$xslt
    <?xml version="1.0" encoding="UTF-8"?>
    
    <databaseChangeLog
            xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
             http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd">
    
        <changeSet author="ankit (generated)" id="1516474605675-1">
            <createTable tableName="user">
                <column autoIncrement="true" name="id" type="INT">
                    <constraints primaryKey="true"/>
                </column>
                <column name="email" type="VARCHAR(255)"/>
                <column name="name" type="VARCHAR(255)"/>
                <column name="phone" type="VARCHAR(255)"/>
            </createTable>
        </changeSet>
    </databaseChangeLog>
    ```
- Setting `Properties`: Add the below details to ``application.properties``
    ```$properties
    spring.datasource.url=jdbc:mysql://localhost:3306/db_example
    spring.datasource.username=db-username
    spring.datasource.password=db-pass
    
    # This tells spring not to create database on its own, so that liquibase can takeover. 
    spring.jpa.hibernate.ddl-auto=none 
    liquibase.change-log=classpath:db/liquibase-changelog.xml
    liquibase.enabled=true
    ```