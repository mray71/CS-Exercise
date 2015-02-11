This is solution to the exercise requested by Cognitive Scale. 

Some notes about the solution:
- It is necessary to export the two worksheets from the Excel file to CSV format. I have them stored in the data/ directory. Given more time this could be changed to use something like Apache POI and avoid the export to CSV step.
- The src/main/resources/config.properties file contains the paths to the data files as well as the root URI for the Neo4J instance.
- I built nodes for the items that seemed most important, patient allergies, medications, and diagnosis, as well as physician specialties and certifications.
- This is not coded to be an end-user application. Logging is sent to the console, and is the only output from the system. 

Things that need to improve:
- In some cases the system can adapt to additional properties, and in some cases it cannot.
- This works well with small datasets similar to those provided. Very large data files will likely run into resource (memory) issues.
- Similarly, every node and most properties are inserted using discrete REST calls. This would likely prove too inefficient for a large dataset.
- There are no indexes in the schema.
