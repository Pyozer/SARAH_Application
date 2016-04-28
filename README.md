# Application Android pour S.A.R.A.H


**/!\ VERSION BETA /!\\**

Installation:
=============

*Si vous n'avez pas le plugin Scribe passez à l'étape 2 directement*

**Etape 1: Préparation de S.A.R.A.H**

- Télécharger le .zip de ce projet github.
- Récupérez le scribe.js présent dans le dossier scribe de l'archive zip.
- Remplacer le scribe.js actuel (présent dans /plugins/scribe de S.A.R.A.H) par celui-ci.
- Si vous avez SARAH V3, remplacez la ligne "var v4 = true;" par "var v4 = false;" dans le scribe.js que vous venez de mettre.

**Etape 2: Installation de l'application**

- Dans le fichier .zip que vous avez téléchargé dans l'étape 1, récupérez le fichier .apk
- Transferez celui-ci sur votre téléphone/tablette (via un cable USB ou avec l'application Portal de Pushbullet par exemple)
- Avant toute chose vous devez avoir autorisé les Sources Inconnus via les paramètres de votre téléphone (dans paramètres -> sécurité)
- Une fois le .apk transferé, appuyez dessus pour l'ouvrir (sur votre téléphone/tablette), et lancez l'installation.
- Une fois installé, ouvrez l'application.

**Etape 3: Configuration de l'application**

- Ouvrez le menu de navigation (en glissant de gauche à droite ou en appuyant sur le bouton "hamburger")

*Le côté client*

- Allez dans "Paramètres" puis dans "Client"
- Saisissez l'adresse ip de l'ordinateur ou est installé S.A.R.A.H
*Si vous ne la connaissez pas: Ouvrez l'invite de commande de votre ordinateur, et saisissez ipconfig, et cherchez "Adresse Ipv4"*
- Saisissez aussi le port du client S.A.R.A.H (par default: 8888)

*Le côté serveur (V4 uniquement)*

- **Si vous ne souhaitez pas gérer le retour TTS sur le PC (en v4 uniquement) passez à l'étape suivante**
- Allez dans "Paramètres" puis dans "Serveur"
- Activez "Passer par le plugin Speak"
- Saisissez l'adresse ip de l'ordinateur où est installé S.A.R.A.H (le serveur)
*Si vous ne la connaissez pas: Ouvrez l'invite de commande de votre ordinateur, et saisissez ipconfig, et cherchez "Adresse Ipv4"*
- Saisissez aussi le port du serveur S.A.R.A.H (par default: 8080)
- Vous n'avez plus qu'a installer le plugin présent ici: https://github.com/Pyozer/SARAH_speak

*Le côté Scribe*

- Allez dans "Paramètres" puis dans "Scribe"
- **Si vous n'utilisez pas Scribe désactivez "Utilisez vous Scribe" et passez directement à l'étape d'après**
- Saissiez l'adresse ip de l'ordinateur où est installé le serveur de S.A.R.A.H
- Même ip que précédamment (si vous avez le serveur et client sur la même machine)
- Saissisez aussi le port du plugin Scribe (par default: 4300)

*Autres paramètres*

- Allez dans "Paramètres" puis dans "Autres"
- Choissisez si vous souhaitez que l'application ajoute automatiquement le nom de S.A.R.A.H si il n'est pas présent dans votre phrase ou bien ne l'ajoute jamais
- Saissiez le nom que vous avez donné à S.A.R.A.H, exemple: Sarah, Jarvis, Eva, Mathilde,...
- Si vous ne souhaitez pas que l'application vocalise la réponse de S.A.R.A.H, désactivez "Activer le retour TTS"
- Si vous souhaitez que S.A.R.A.H (le pc) vocalise la réponse, activez le "Retour TTS sur le PC" sinon désactivez.
- **INFO:** En v3, S.A.R.A.H vocalisera automatiquement la réponse, mais en V4 non ! Si vous voulez pouvoir activer le retour TTS sur S.A.R.A.H, vous deverez installez le plugin Speak https://github.com/Pyozer/SARAH_speak

*Informations complémentaires*

**Si vous souhaitez utilisez S.A.R.A.H à l'extérieur de chez vous, vous deverez ouvrir les ports 8888 (client) et 4300 (scribe) de votre box internet**

Si vous souhaitez aussi acceder à l'interface de S.A.R.A.H ouvrez aussi le port 8080.

(Bien sur, si vous avez changé les ports par default, ouvrez les ports correspondants)



Voilà si tout va bien, vous pouvez maintenant parler à SARAH via l'application


Pourquoi en refaire une ?
-------------------------

L'application actuelle ne peut fonctionner avec le plugin Scribe.
L'interface n'était pas à mon goût aussi ;)

Que pourra t'elle faire ?
-------------------------

Cette nouvelle version a pour but de pouvoir utiliser tous les plugins, Scribe ou non.
Si vous n'avez pas Scribe ce n'est pas un problème, il vous suffira de décocher "Scribe" dans les paramètres.

Cette application permettera aussi de faire dire une phrase à S.A.R.A.H (que vous aurez prononcé ou écrite).
**Peut-être bientôt:** Pouvoir executer des actions que vous aurez définis

Avantages:
----------
- Possibilité d'être en dehors de chez vous et de parler à S.A.R.A.H (si ports ouverts etc..)
- Fonctionne avec le plugin Scribe
- Peut faire parler S.A.R.A.H avec la phrase de votre choix, que vous aurez prononcé ou écrite
- Pouvoir gérer les retour audio (En v4 avec le Speak)
- Une interface plus actuelle (Matérial Design de Google)

Désavantages:
-------------
- Nécessite une modification du plugin Scribe (ne plus être en https notamment)
- Le besoin de se passer du ScribeSpeak sinon c'est le PC qui répond et non l'application.
- En V3, le serveur répondera forcément aussi :/ Alors qu'en v4, avec le plugin Speak il y a moyen de gérer le retour TTS

Quelles sont les modifications exactement ?
-----------------------------------------
- Ajouter un setTimeout à Scribe d'environ 2-3 secondes.
- Passer du https au http (A cause du certificat non valide)
- Ne plus utiliser ScribeSpeak mais les callbacks habituels. (Sinon c'est le PC qui parle et non l'application)

**Vous trouverez le plugin modifié sur ce github (pour sarah v3, modifiez juste "var v4 = true;" en "var v4 = false;" dans le scribe.js)**

Petit aperçu de l'interface:
----------------------------

<img src="https://raw.githubusercontent.com/Pyozer/SARAH_Application/master/Preview/PreviewInterface.png?ver=2.0" alt="Aperçu de l'interface principale" width="260" style="display: inline-block" />
<img src="https://raw.githubusercontent.com/Pyozer/SARAH_Application/master/Preview/PreviewMenu.png?ver=2.0" alt="Aperçu du menu de navigation" width="260" style="display: inline-block" />
<img src="https://github.com/Pyozer/SARAH_Application/blob/master/Preview/PreviewSpeak.png?ver=2.0" alt="Aperçu de l'interface SPEAK" width="260" style="display: inline-block" />
<img src="https://github.com/Pyozer/SARAH_Application/blob/master/Preview/PreviewSettings.png?ver=2.0" alt="Aperçu de l'interface Paramètres" width="260" style="display: inline-block" />
<img src="https://github.com/Pyozer/SARAH_Application/blob/master/Preview/PreviewHelp.png?ver=2.0" alt="Aperçu de l'interface d'aide" width="260" style="display: inline-block" />