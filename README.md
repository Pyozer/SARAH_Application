# Application Android pour S.A.R.A.H


**/!\ VERSION BETA /!\\**

Pourquoi en refaire une ?
-------------------------

L'application actuelle ne peut fonctionner avec le plugin Scribe.

Que pourra t'elle faire ?
-------------------------

Cette nouvelle version a pour but de pouvoir utiliser tous les plugins, Scribe ou non.
Si vous n'avez pas Scribe ce n'est pas un problème, il vous suffira de décocher "Scribe" dans les paramètres.

Cette application permettera aussi de faire dire une phrase à S.A.R.A.H (que vous aurez prononcé ou écrite).

Avantages:
----------
- Possibilité d'être en dehors de chez vous et de parler à S.A.R.A.H (si ports ouverts etc..)
- Fonctionne avec le plugin Scribe
- Peut faire parler S.A.R.A.H avec la phrase de votre choix, que vous aurez prononcé ou écrite
- Une interface plus actuelle (Matérial Design de Google)

Désavantages:
-------------
- Nécessite une modification du plugin Scribe (ne plus être en https notamment)
- Le besoin de se passer du ScribeSpeak sinon c'est le PC qui répond et non l'application.

Quelles sont les modifications exactement ?
-----------------------------------------
- Ajouter un setTimeout à Scribe d'environ 2-3 secondes.
- Passer du https au http (A cause du certificat non valide)
- Ne plus utiliser ScribeSpeak mais les callbacks habituels. (Sinon c'est le PC qui parle et non l'application)

**Vous trouverez le plugin modifié sur ce github (pour sarah v3, modifiez juste "var v4 = true;" en "var v4 = false;" dans le scribe.js)**

Petit aperçu de l'interface:
----------------------------

<img src="https://raw.githubusercontent.com/Pyozer/SARAH_Application/master/Preview/PreviewInterface.png" alt="Aperçu de l'interface" width="260" style="display: inline-block" />
<img src="https://raw.githubusercontent.com/Pyozer/SARAH_Application/master/Preview/PreviewMenu.png" alt="Aperçu du menu" width="260" style="display: inline-block" />
<img src="https://github.com/Pyozer/SARAH_Application/blob/master/Preview/PreviewSpeak.png" alt="Aperçu de l'interface speak" width="260" style="display: inline-block" />
