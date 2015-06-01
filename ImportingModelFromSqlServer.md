# Introduction #

In this tutorial we will demonstrate how to import database metadata from an existing database. We will use SQLServer 2008 and the AdventureWorks sample database.


# Details #

First we need a model that we can import into. As with any resource maintained in eclipse, we also need a project that the model can be stored into. If you dont already have one, you can create the project the usual eclipse way. It does not matter what kind of project this is. Then you can create a new model using the "New" wizard, as shown in the following video:

<a href='http://www.youtube.com/watch?feature=player_embedded&v=-P9a_uY2JaE' target='_blank'><img src='http://img.youtube.com/vi/-P9a_uY2JaE/0.jpg' width='798' height=568 /></a>

The next video shows how we create a connection to the database, and import the database metadata into RMBench. Note that import is always done first into an intermediary "import model", from which furter import into the model proper can proceed. After that, we create a new diagram and pull the contents of one database catalog into the new diagram for graphical display:

<a href='http://www.youtube.com/watch?feature=player_embedded&v=W5i9rQt-cd8' target='_blank'><img src='http://img.youtube.com/vi/W5i9rQt-cd8/0.jpg' width='798' height=568 /></a>