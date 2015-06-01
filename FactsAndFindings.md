# Introduction #

here we collect miscellaneous items collected during development and testing, which should not get lost while no real documentation is in place. Individual items may be added directly as text in this page, or via linked sub-pages.


# Items #
## Importing tables with user-defined data types (UDTs) ##
in MS SQLServer, when importing table metadata from tables with columns that reference UDTs, these columns are _not_ returned unless the current user has the required privileges to see the data types. In SQLServer, data types are located in the dbo schema. Thus, it may happen that a table appears to only have 2 columns, while in fact it has more. This can lead to model save errors if there are, e.g., indexes that reference these columns.

In my case, adding the db\_owner role to the user that performs the import fixed the problem.