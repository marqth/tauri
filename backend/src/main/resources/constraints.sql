ALTER TABLE grades
    ADD CONSTRAINT grades_check CHECK (
        (student_id IS NULL AND team_id IS NOT NULL)
            OR
        (team_id IS NULL AND student_id IS NOT NULL)
        );

/*1) Open the Database tool window (View -> Tool Windows -> Database).
2) Click the + button and select Data Source -> MySQL.
3) In the Data Sources and Drivers dialog, enter your database connection details (URL, user, password) and click OK.
4) Right-click your constraints.sql file in the Project tool window and select "Change Dialect (MySQL) to..."
5) Select MySQL*/