This is solution to the exercise requested by *Cognitive Scale*.

Building
====
To run the solution simply clone the repository and execute 'gradle build' from the main directory. Gradle 2.x and a 1.7+ JDK are required.

Configuration
====
Edit the config/config.properties file to match the root URI of Neo4J and adjust paths to data files as necessary.

Running
====
Execute 'gradle exec' from the main directory.

**There are no unique indices (yet), so it is necessary to drop the DB in between runs in order to avoid duplicate data.**

Some notes about the solution:
====
- It is necessary to export the two worksheets from the Excel file to CSV format. I have them stored in the data/ directory. Given more time this could be changed to use something like Apache POI and avoid the export to CSV step.
- I built nodes for the items that seemed most important, patient allergies, medications, and diagnosis, as well as physician specialties and certifications.
- This is not coded to be an end-user application. Logging is sent to the console, and is the only output from the system.

Things that need to improve:
====
- This was my first time working with Neo4j (or any graph database for that matter). I already see many things I would do differently in the schema.
- In some cases the system can adapt to additional properties being added to the data files, and in some cases it cannot.
- This works well with small datasets similar to those provided. Very large data files will likely run into resource (memory) issues.
- Similarly, every node and most properties are inserted using discrete REST calls. This would likely prove too inefficient for a large dataset.
- There are no indexes in the schema.
- Use an upsert operation (merge?) rather than create for node insertion.
- Because of the above two items, duplicate nodes are currently possible.
- Using the REST API from Java is somewhat clunky. Investigate alternative solutions.
