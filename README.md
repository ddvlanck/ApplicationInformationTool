# Application Information Tool
De application tool (ApplicationInformationTool project) wordt verondersteld opgestart te worden door de update tool. De jar verwacht één argument, een filename van de file die application data (in JSON) bevat.

In eerste instantie worden user, system en application data verzameld in JSON formaat (klassen UserData, SystemData en ApplicationData).

Vervolgens gebeurt de authenticatie op basis van user domain en MAC address (klasse Authenticator). Deze authenticatie gebeurt op basis van database gegevens (Azure SQL Database "DB_Interns") geraadpleegd via onze REST API (AIT-REST-maven project).

Via de DataCreator-klasse wordt de verzamelde data, indien de authenticatie succesvol was, gepost naar onze REST-API.

# AIT-REST-maven
De REST API is een Maven project. Dit project communiceert met de Azure Data Lake Store (datalakeinterns) en SQL Database (DB_interns) via de Azure Java SDK's. De klasse AITResource bevat authenticate methode die POST requests op de /authenticate URL verwerkt. Aan de hand van de verkregen informatie (domain en MAC) wordt via de database gecontroleerd of de huidige computer zijn data mag wegschrijven in het Data Lake (conform de "allow" kolom van tabellen Companies en Computers). 

Indien toegelaten wordt de POST op de /data URL verwerkt door de sendData methode. In deze methode wordt in het data lake een directory structuur opgezet van de vorm /domain/mac/user waarin een file met de informatie opgeslagen wordt. De naam van de file is de timestamp op het moment van wegschrijven. 

Indien niet toegelaten wordt een log-file aangevuld met een lijn informatie over deze unauthorized computer.
